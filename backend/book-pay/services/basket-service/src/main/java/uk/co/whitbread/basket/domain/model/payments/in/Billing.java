package uk.co.whitbread.basket.domain.model.payments.in;

import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class Billing implements SelfValidation<Billing> {

  private String title;
  private String firstName;
  private String lastName;
  private String email;
  private String telephone;
  private boolean differentBillingAddress;
  private boolean bookerIsNotGuest;
  private Address address;
  private Address cardBillingAddress;


  public Billing(String title, String firstName, String lastName, String email, String telephone,
       boolean differentBillingAddress, boolean bookerIsNotGuest, Address address, Address cardBillingAddress) {
    this.title = title;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.telephone = telephone;
    this.address = address;
    this.cardBillingAddress = cardBillingAddress;
    this.differentBillingAddress = differentBillingAddress;
    this.bookerIsNotGuest = bookerIsNotGuest;
    this.validateSelf();
  }
}
