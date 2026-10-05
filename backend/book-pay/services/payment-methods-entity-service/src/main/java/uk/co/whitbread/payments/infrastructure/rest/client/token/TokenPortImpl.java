package uk.co.whitbread.payments.infrastructure.rest.client.token;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.PaypalToken;
import uk.co.whitbread.payments.domain.ports.secondary.TokenPort;
import uk.co.whitbread.payments.infrastructure.rest.client.token.service.TokenClient;


@Slf4j
public class TokenPortImpl implements TokenPort {

  private final TokenClient tokenClient;

  public TokenPortImpl(final TokenClient tokenClient) {
    this.tokenClient = tokenClient;
  }

  @Override
  public PaypalToken getPaypalToken(String countryCode) {
    log.debug("Entered getPaypalToken for countryCode={}", countryCode);
    final var paypalTokenResponse = tokenClient.getPaypalToken(countryCode);
    return PaypalToken.builder()
        .clientToken(paypalTokenResponse.getClientToken())
        .clientId(paypalTokenResponse.getClientId())
        .build();
  }
}
