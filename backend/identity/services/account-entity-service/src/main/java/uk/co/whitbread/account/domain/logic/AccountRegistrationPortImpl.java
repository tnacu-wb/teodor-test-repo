package uk.co.whitbread.account.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.account.domain.model.in.AccountRegistrationRequest;
import uk.co.whitbread.account.domain.model.out.AccountRegistrationResponse;
import uk.co.whitbread.account.domain.ports.primary.AccountRegistrationPort;
import uk.co.whitbread.account.domain.ports.secondary.CustomerRegistrationPort;

@Slf4j
@RequiredArgsConstructor
public class AccountRegistrationPortImpl implements AccountRegistrationPort {
  private final CustomerRegistrationPort customerRegistrationPort;

  @Override
  public AccountRegistrationResponse registerAccount(
      AccountRegistrationRequest accountRegistrationRequest, String country, String language) {
    return customerRegistrationPort.registerCustomer(accountRegistrationRequest, country, language);
  }
}
