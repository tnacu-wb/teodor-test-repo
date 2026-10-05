package uk.co.whitbread.payments.infrastructure.rest.client.token.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.infrastructure.rest.client.service.CustomTestResponseSpec;
import uk.co.whitbread.payments.infrastructure.rest.client.token.TokenException;
import uk.co.whitbread.payments.infrastructure.rest.client.token.model.out.TokenResponseDto;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class TokenClientTest {

  private static final String COUNTRY = "GB";

  @Mock
  private WebClient tokenWebClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;
  @InjectMocks
  private TokenClient tokenClient;


  @Test
  void getPaypalToken_success() {
    when(tokenWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class)))
          .thenReturn(responseSpec);
    when(responseSpec.bodyToMono(TokenResponseDto.class)).thenReturn(mockRuleResponse());

    //Act
    var response = this.tokenClient.getPaypalToken(COUNTRY);

    //Assert
    assertThat(response, notNullValue());
  }

  @Test
  void getPaypalToken_throwException() {

    String errorMessage = "Error while trying to get paypal token!";
    // Arrange
    when(tokenWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class)))
        .thenCallRealMethod();
    //Act
    Exception exception = assertThrows(TokenException.class,
        () -> tokenClient.getPaypalToken("countryCode"));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(errorMessage));
  }

  private Mono<TokenResponseDto> mockRuleResponse() {
    TokenResponseDto dto = new TokenResponseDto();
    dto.setClientId("testId");
    dto.setClientToken("testToken");
    return Mono.just(dto);
  }
}