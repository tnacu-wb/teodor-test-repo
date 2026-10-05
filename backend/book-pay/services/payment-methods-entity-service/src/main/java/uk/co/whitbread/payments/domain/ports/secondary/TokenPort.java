package uk.co.whitbread.payments.domain.ports.secondary;

import uk.co.whitbread.payments.domain.model.out.PaypalToken;

public interface TokenPort {
  public PaypalToken getPaypalToken(String countryCode);
}
