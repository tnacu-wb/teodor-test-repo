package uk.co.whitbread.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.infrastructure.config.Dynamics365Properties;
import uk.co.whitbread.infrastructure.config.OAuthProperties;
import uk.co.whitbread.infrastructure.rest.client.microsoftoauth.OAuthProvider;
import uk.co.whitbread.infrastructure.rest.client.microsoftoauth.model.MicrosoftOauthResponseDto;
import uk.co.whitbread.infrastructure.rest.client.utils.CustomTestResponseSpec;


@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class OAuthProviderTest {

  @Mock
  private WebClient webClient;
  @Mock
  private OAuthProperties oAuthProperties;
  @Mock
  private Dynamics365Properties dynamics365Properties;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private CustomTestResponseSpec responseSpec;
  private OAuthProvider oAuthProvider;

  @BeforeEach
  public void setOAuthProperties() {
    MockitoAnnotations.openMocks(this);

    oAuthProvider = new OAuthProvider(oAuthProperties, dynamics365Properties);
    try {
      Field webClientField = OAuthProvider.class.getDeclaredField("microsoftWebClient");
      webClientField.setAccessible(true);
      webClientField.set(oAuthProvider, webClient);
    } catch (Exception e) {
      throw new RuntimeException("Failed to invoke @PostConstruct method", e);
    }

    when(oAuthProperties.getTokenUrl()).thenReturn("https://tokenUrl");
    when(oAuthProperties.getClientId()).thenReturn("mockclientid");
    when((oAuthProperties.getClientSecret())).thenReturn("mockclientsecret");
    when(oAuthProperties.getGrantType()).thenReturn("mockgranttype");
    when((oAuthProperties.getScope())).thenReturn("mockscope");
    when(dynamics365Properties.getHost()).thenReturn("mock-resource");
  }

  @Test
  void testGetBearerToken() {
    //Arrange
    MicrosoftOauthResponseDto mockResponse = new MicrosoftOauthResponseDto();
    mockResponse.setAccessToken("mock-access-token");

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(oAuthProperties.getTokenUrl())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(
        requestBodySpec);
    when(requestBodySpec.body(any(BodyInserters.FormInserter.class))).thenReturn(
        requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

    when(responseSpec.bodyToMono(MicrosoftOauthResponseDto.class)).thenReturn(
        Mono.just(mockResponse));

    //Act
    String accessToken = oAuthProvider.getBearerTokenNonCached();

    //Assert
    assertEquals("mock-access-token", accessToken);
  }

  @Test
  void testGetBearerToken_nullToken__ShouldReturnNull() {
    //Arrange
    MicrosoftOauthResponseDto mockResponse = new MicrosoftOauthResponseDto();
    mockResponse.setAccessToken(null);

    Mono<MicrosoftOauthResponseDto> bodyToMonoResponse = mock(Mono.class);

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(oAuthProperties.getTokenUrl())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(
        requestBodySpec);
    when(requestBodySpec.body(any(BodyInserters.FormInserter.class))).thenReturn(
        requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(MicrosoftOauthResponseDto.class)).thenReturn(
        bodyToMonoResponse);
    when(bodyToMonoResponse.block()).thenReturn(null);

    //Act
    String accessToken = oAuthProvider.getBearerTokenNonCached();

    //Assert
    assertNull(accessToken);
  }


}

