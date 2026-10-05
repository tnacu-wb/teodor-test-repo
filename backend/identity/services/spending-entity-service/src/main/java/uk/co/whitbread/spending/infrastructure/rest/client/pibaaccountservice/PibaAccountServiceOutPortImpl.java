package uk.co.whitbread.spending.infrastructure.rest.client.pibaaccountservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.spending.domain.model.out.pibaaccountservice.CustomerAccountsResponse;
import uk.co.whitbread.spending.domain.model.out.pibaaccountservice.TetheredUserDetailsResponse;
import uk.co.whitbread.spending.domain.ports.secondary.PibaAccountServiceOutPort;

@Slf4j
@RequiredArgsConstructor
public class PibaAccountServiceOutPortImpl implements PibaAccountServiceOutPort {

  private final PibaAccountServiceClient pibaAccountServiceClient;

  @Override
  public TetheredUserDetailsResponse getTetheredUserDetails(String authorization,
        String tetheredUserGuid, String scheme) {
    return pibaAccountServiceClient.getTetheredUserDetails(authorization, tetheredUserGuid, scheme);
  }

  @Override
  public CustomerAccountsResponse getAccounts(String authorization) {
    return pibaAccountServiceClient.getAccounts(authorization);
  }

}
