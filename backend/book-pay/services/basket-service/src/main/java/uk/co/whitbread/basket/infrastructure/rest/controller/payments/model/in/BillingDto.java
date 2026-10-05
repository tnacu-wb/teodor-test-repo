package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class BillingDto implements SelfValidation<BillingDto> {

  private String title;
  private String firstName;
  private String lastName;
  private String email;
  private String telephone;
  private boolean differentBillingAddress;
  private boolean bookerIsNotGuest;
  @Valid
  private AddressDto address;
  @Valid
  private AddressDto cardBillingAddress;

  public BillingDto(String title, String firstName, String lastName, String email, String telephone,
      boolean differentBillingAddress, boolean bookerIsNotGuest, AddressDto address, AddressDto cardBillingAddress) {
    this.title = title;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.telephone = telephone;
    this.differentBillingAddress = differentBillingAddress;
    this.bookerIsNotGuest = bookerIsNotGuest;
    this.address = address;
    this.cardBillingAddress = cardBillingAddress;
    this.validateSelf();
  }
}
