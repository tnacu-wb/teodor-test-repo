package uk.co.whitbread.basket.domain.model.ccuieckoh.in;

import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class BillingCcui implements SelfValidation<BillingCcui> {

  private String title;
  private String firstName;
  private String lastName;
  private String email;
  private String telephone;
  private AddressCcui address;
  private boolean differentBillingAddress;
  private boolean bookerIsNotGuest;

  public BillingCcui(String title, String firstName, String lastName, String email, String telephone,
      AddressCcui address, boolean differentBillingAddress, boolean bookerIsNotGuest) {
    this.title = title;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.telephone = telephone;
    this.address = address;
    this.differentBillingAddress = differentBillingAddress;
    this.bookerIsNotGuest = bookerIsNotGuest;
    this.validateSelf();
  }
}
