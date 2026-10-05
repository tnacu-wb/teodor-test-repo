package uk.co.whitbread.account.domain.ports.secondary;

import uk.co.whitbread.account.domain.model.in.AccountRegistrationRequest;
import uk.co.whitbread.account.domain.model.out.AccountRegistrationResponse;

public interface CustomerRegistrationPort {
  AccountRegistrationResponse registerCustomer(final AccountRegistrationRequest accountRegistrationRequest,
                              String country, String language);
}
