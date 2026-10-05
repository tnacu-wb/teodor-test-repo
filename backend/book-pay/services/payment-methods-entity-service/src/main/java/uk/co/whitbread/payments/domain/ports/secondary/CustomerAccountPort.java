package uk.co.whitbread.payments.domain.ports.secondary;

import java.util.List;
import java.util.Optional;
import uk.co.whitbread.payments.domain.model.in.UserType;
import uk.co.whitbread.payments.domain.model.out.Card;
import uk.co.whitbread.payments.domain.model.out.CompanyDetailsResponse;
import uk.co.whitbread.payments.domain.model.out.CustomerAccount;

public interface CustomerAccountPort {

  List<Card> findCustomerSavedCards(String userId, UserType userType, String channel,
                                    CustomerAccount account, String authToken);

  CustomerAccount findCustomerAccount(String userId, UserType userType, String channel, String authToken);

  Optional<CompanyDetailsResponse> findCompany(CustomerAccount account, String authToken);

}
