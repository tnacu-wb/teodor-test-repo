package uk.co.whitbread.account.domain.ports.primary;

import uk.co.whitbread.account.domain.model.in.AccountRegistrationRequest;
import uk.co.whitbread.account.domain.model.out.AccountRegistrationResponse;

public interface AccountRegistrationPort {
  AccountRegistrationResponse registerAccount(final AccountRegistrationRequest accountRegistrationRequest,
                              String country, String language);
}
