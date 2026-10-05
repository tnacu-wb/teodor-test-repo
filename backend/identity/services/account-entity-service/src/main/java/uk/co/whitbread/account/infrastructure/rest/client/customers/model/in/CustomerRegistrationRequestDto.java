package uk.co.whitbread.account.infrastructure.rest.client.customers.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.account.domain.model.validation.SelfValidation;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.ContactDetailDto;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class CustomerRegistrationRequestDto extends SelfValidation<CustomerRegistrationRequestDto> {
  String captcha;
  @NotEmpty
  String password;
  @NotNull
  ContactDetailDto contactDetail;

  String basketReference;

  public CustomerRegistrationRequestDto(String captcha, String password, ContactDetailDto contactDetail,
      String basketReference) {
    this.captcha = captcha;
    this.password = password;
    this.contactDetail = contactDetail;
    this.basketReference = basketReference;
    this.validateSelf();
  }
}
