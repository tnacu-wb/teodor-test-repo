package uk.co.whitbread.spending.domain.ports.secondary;

import uk.co.whitbread.spending.domain.model.out.pibaaccountservice.CustomerAccountsResponse;
import uk.co.whitbread.spending.domain.model.out.pibaaccountservice.TetheredUserDetailsResponse;

public interface PibaAccountServiceOutPort {

  TetheredUserDetailsResponse getTetheredUserDetails(String authorization,
       String tetheredUserGuid, String scheme);

  CustomerAccountsResponse getAccounts(String authorization);

}
