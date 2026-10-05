package uk.co.whitbread.payments.infrastructure.rest.client.token;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payments.domain.exception.ErrorCode;
import uk.co.whitbread.payments.infrastructure.rest.client.token.model.out.TokenResponseDto;
import uk.co.whitbread.payments.infrastructure.rest.client.token.service.TokenClient;

@ExtendWith(MockitoExtension.class)
class TokenPortImplTest {

  public static final String COUNTRY = "GB";

  @Mock
  private TokenClient tokenClient;
  @InjectMocks
  private TokenPortImpl tokenPort;

  @Test
  void getPaypalToken_success() {
    // Arrange
    TokenResponseDto dto = new TokenResponseDto();
    given(tokenClient.getPaypalToken(COUNTRY)).willReturn(dto);

    // Act
    var result = tokenPort.getPaypalToken(COUNTRY);

    // Assert
    assertThat(result, notNullValue());
    verify(tokenClient).getPaypalToken(COUNTRY);
  }

  @Test
  void getPaypalRule_failure() {
    // Arrange
    when(tokenClient.getPaypalToken(COUNTRY)).thenThrow(
        new TokenException(ErrorCode.GET_PAYPAL_TOKEN_EXCEPTION, "Token Exception"));

    // Act
    Exception exception = assertThrows(TokenException.class, () ->
        tokenPort.getPaypalToken(COUNTRY), "Token Exception"
    );

    // Assert
    assertTrue(exception.getMessage().contains("Token Exception"));
    verify(tokenClient).getPaypalToken(COUNTRY);
  }
}