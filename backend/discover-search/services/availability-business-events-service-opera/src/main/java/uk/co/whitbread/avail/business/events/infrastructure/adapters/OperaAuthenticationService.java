package uk.co.whitbread.avail.business.events.infrastructure.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.co.whitbread.avail.business.events.domain.model.feature.FeatureFlag;
import uk.co.whitbread.avail.business.events.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.avail.business.events.domain.ports.secondary.OperaAuthenticationPort;
import uk.co.whitbread.avail.business.events.infrastructure.client.OperaRestClient;
import uk.co.whitbread.avail.business.events.infrastructure.config.OperaProperties;
import uk.co.whitbread.avail.business.events.infrastructure.model.OauthTokenResponse;
import uk.co.whitbread.avail.business.events.infrastructure.utils.HashingUtils;

@Slf4j
@RequiredArgsConstructor
public class OperaAuthenticationService implements OperaAuthenticationPort {

  private static final String APP_KEY_HEADER = "x-app-key";
  private static final String USERNAME = "username";
  private static final String PWD_HEADER_NAME = "password";
  private static final String GRANT_TYPE = "grant_type";
  private static final String SCOPE = "scope";
  private static final String ENTERPRISE_ID = "enterpriseId";
  private String oauthTokenInCache;
  private LocalDateTime oauthExpiryTimeInCache;
  private final OperaProperties operaProperties;
  private final ObjectMapper objectMapper;
  private final OperaRestClient operaRestClient;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final FeatureFlag featureFlag;

  public String fetchOauthToken(final boolean isInvalidToken) {
    log.trace("Fetch the Authentication token to call Opera API's");
    final boolean renewTokenFrmOpera = renewTokenFrmOpera(isInvalidToken);
    if (renewTokenFrmOpera) {
      final String oAuthTokenFrmOpera = getAuthTokenFromOpera();
      log.info("Using renewed oAuthToken from Opera, "
              + "oAuthToken : {}, oAuthExpiryTime : {}",
          HashingUtils.maskStringExceptLast4(oAuthTokenFrmOpera), oauthExpiryTimeInCache);
      return oAuthTokenFrmOpera;
    } else {
      log.info("Using cached oAuthToken : {}, oAuthExpiryTimeInCache : {}",
          HashingUtils.maskStringExceptLast4(oauthTokenInCache), oauthExpiryTimeInCache);
      return oauthTokenInCache;
    }
  }

  private boolean renewTokenFrmOpera(final boolean isInvalidToken) {
    boolean renewTokenFrmOpera = false;
    if (isInvalidToken || isTokenInCacheNull()) {
      renewTokenFrmOpera = true;
    } else {
      final LocalDateTime currentTime = LocalDateTime.now();
      final long tokenRefreshSkewInMinutes = getTokenRefreshSkewInMinutes();
      
      final LocalDateTime calculatedExpiryTimeInCache =
          oauthExpiryTimeInCache.minusMinutes(tokenRefreshSkewInMinutes);
      if (currentTime.isEqual(calculatedExpiryTimeInCache)
          || currentTime.isAfter(calculatedExpiryTimeInCache)) {
        log.debug("Cached oAuthToken expired, oAuthExpiryTimeInCache : {} "
            + ", currentTime : {}, configured early expiration in mins : {}",
             oauthExpiryTimeInCache, currentTime,
             tokenRefreshSkewInMinutes);
        renewTokenFrmOpera = true;
      }
    }
    return renewTokenFrmOpera;
  }

  private long getTokenRefreshSkewInMinutes() {
    var flag = featureFlag.getUseTokenRefreshSkew();
    if (flag != null && unleashWrapper.isEnabled(flag)) {
      Long clockSkew = operaProperties.getTokenRefreshClockSkew();
      if (clockSkew != null) {
        log.debug("Using feature-flagged tokenRefreshClockSkew: {} minutes", clockSkew);
        return clockSkew;
      }
      log.warn("Feature flag enabled but tokenRefreshClockSkew is null, "
          + "falling back to tokenExpiryTimeout");
    }
    long defaultTimeout = operaProperties.getTokenExpiryTimeout();
    log.debug("Using default tokenExpiryTimeout: {} minutes", defaultTimeout);
    return defaultTimeout;
  }

  private boolean isTokenInCacheNull() {
    return oauthTokenInCache == null
        || oauthTokenInCache.isEmpty()
        || oauthExpiryTimeInCache == null;
  }

  private String getAuthTokenFromOpera() {
    log.debug("Fetch Auth Token from Opera");
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    
    if (operaProperties.getIsClientCredentialsEnabled()) {
      map.add(GRANT_TYPE, operaProperties.getOcimGrantType());
      map.add(SCOPE, operaProperties.getScope());
    } else {
      map.add(USERNAME, operaProperties.getUsername());
      map.add(PWD_HEADER_NAME, operaProperties.getPassword());
      map.add(GRANT_TYPE, operaProperties.getGrantType());
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.add(APP_KEY_HEADER, operaProperties.getAppkey());
    if (operaProperties.getIsClientCredentialsEnabled()) {
      headers.add(ENTERPRISE_ID, operaProperties.getEnterpriseId());
    }
    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

    log.debug("request url - {} request body - {} and  are {}",
        operaProperties.getServiceUrl().getOauth(), entity.getBody(), entity.getHeaders());

    ResponseEntity<String> response =
        operaRestClient.getOauthToken(operaProperties.getServiceUrl().getOauth(), entity);

    log.debug("response - {} and status code - {} and response body - {}",
        response, response.getStatusCode(), response.getBody());

    if (response.getStatusCode().is2xxSuccessful()) {
      try {
        OauthTokenResponse oauth2TokenResponse = objectMapper
            .readValue(response.getBody(), OauthTokenResponse.class);
        saveOauthTokenAndCacheExpiryInCache(oauth2TokenResponse.getAccessToken());
        return oauth2TokenResponse.getAccessToken();
      } catch (IOException e) {
        log.error("Could not deserialize token");
      }
    } else {
      log.error("Count not get auth token\n"
              + "Message: {}\n"
              + "Status code: {}",
          response.getBody(), response.getStatusCode());
    }
    log.error(
        "unable to fetch Authorization oAuthToken data from the Opera, "
            + "so returning null as the accessToken");
    return null;
  }

  private void saveOauthTokenAndCacheExpiryInCache(final String oauthToken) {
    log.trace("save AuthToken and CacheExpiry into cache for token - {}", oauthToken);

    oauthTokenInCache = oauthToken;
    Base64.Decoder decoder = Base64.getUrlDecoder();
    final String[] parts = oauthToken
        .split("\\."); // split out the "parts" (header, payload and signature)

    final String headerJson = new String(decoder.decode(parts[0]));
    final String payloadJson = new String(decoder.decode(parts[1]));
    final String signatureJson = new String(decoder.decode(parts[2]));
    log.debug("Oauth Token values - headerJson - {} payLoadJson - {} and signatureJson - {}",
        headerJson, payloadJson, signatureJson);

    //Fetch ExpiryTime from PayLoadJson
    log.debug("fetch expiryTime from the payLoadJson - {}", payloadJson);
    JsonObject jsonObject = JsonParser.parseString(payloadJson).getAsJsonObject();

    final long expiryInEpochSeconds = jsonObject.get("exp").getAsLong();
    final LocalDateTime expiryTime =
        LocalDateTime
            .ofInstant(Instant.ofEpochSecond(expiryInEpochSeconds), ZoneId.systemDefault());
    log.debug("expiryInEpochSeconds - {} and date - {}", expiryInEpochSeconds, expiryTime);
    oauthExpiryTimeInCache = expiryTime;

    log.trace(
        "Processing completed to save the oAuthTokenInCache and "
            + "oAuthExpiryTimeInCache with its respective values as - {} & {}",
        oauthTokenInCache, oauthExpiryTimeInCache);
  }

}

