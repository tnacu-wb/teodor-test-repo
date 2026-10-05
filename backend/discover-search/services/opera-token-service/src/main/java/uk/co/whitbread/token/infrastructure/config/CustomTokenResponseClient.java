package uk.co.whitbread.token.infrastructure.config;

import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.DefaultMapOAuth2AccessTokenResponseConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestClient;

public class CustomTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> {

  public static final String HEADER_ENTERPRISE_ID = "enterpriseid";
  public static final String HEADER_X_APP_KEY = "x-app-key";
  public static final String ACCESS_TOKEN = "access_token";
  public static final String EXPIRES_IN = "expires_in";
  public static final String TOKEN_TYPE_BEARER = "bearer";
  public static final String TOKEN_TYPE = "token_type";
  private final RestClientClientCredentialsTokenResponseClient delegate
      = new RestClientClientCredentialsTokenResponseClient();

  public CustomTokenResponseClient(String enterpriseId, String appKey) {
    delegate.addHeadersConverter(request -> {
      HttpHeaders headers = new HttpHeaders();
      headers.add(HEADER_ENTERPRISE_ID, enterpriseId);
      headers.add(HEADER_X_APP_KEY, appKey);
      return headers;
    });
    delegate.setRestClient(buildRestClient());
  }

  @Override
  public OAuth2AccessTokenResponse getTokenResponse(OAuth2ClientCredentialsGrantRequest request) {
    return delegate.getTokenResponse(request);
  }

  private RestClient buildRestClient() {
    OAuth2AccessTokenResponseHttpMessageConverter tokenResponseMessageConverter
        = new OAuth2AccessTokenResponseHttpMessageConverter();
    DefaultMapOAuth2AccessTokenResponseConverter tokenResponseConverter
        = new DefaultMapOAuth2AccessTokenResponseConverter();
    tokenResponseMessageConverter.setAccessTokenResponseConverter(body -> {
      validateResponse(body);
      return tokenResponseConverter.convert(body);
    });

    return RestClient.builder()
        .messageConverters(converters -> {
          converters.clear();
          converters.add(new FormHttpMessageConverter());
          converters.add(tokenResponseMessageConverter);
        })
        .defaultStatusHandler(new OAuth2ErrorResponseErrorHandler())
        .build();
  }

  private void validateResponse(Map<String, Object> body) {
    if (body.get(ACCESS_TOKEN) == null) {
      throw new IllegalStateException("Opera response missing access_token: " + body);
    }

    Object tokenType = body.get(TOKEN_TYPE);
    if (tokenType == null || !TOKEN_TYPE_BEARER.equalsIgnoreCase(tokenType.toString())) {
      throw new IllegalStateException("Unexpected token_type in Opera response: " + tokenType);
    }

    if (body.get(EXPIRES_IN) == null) {
      throw new IllegalStateException("Opera response missing expires_in: " + body);
    }
  }


}




