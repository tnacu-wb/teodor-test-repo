package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class BillingCcuiDto implements SelfValidation<BillingCcuiDto> {

  private String title;
  private String firstName;
  private String lastName;
  private String email;
  private String telephone;
  @Valid
  private AddressCcuiDto address;
  private boolean differentBillingAddress;
  private boolean bookerIsNotGuest;

  public BillingCcuiDto(String title, String firstName, String lastName, String email,
      String telephone, AddressCcuiDto address, boolean differentBillingAddress, boolean bookerIsNotGuest) {
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
