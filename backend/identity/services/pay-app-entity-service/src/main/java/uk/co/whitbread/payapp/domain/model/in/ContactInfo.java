package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.payapp.domain.model.validation.DomainValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class ContactInfo extends DomainValidator<ContactInfo> {

  @NotBlank
  String title;

  @NotBlank
  String foreName;

  @NotBlank
  String lastName;

  String position;

  String telephone;

  String mobile;

  @NotBlank
  String email;

  public ContactInfo(String title, String foreName, String lastName, String position,
      String telephone, String mobile, String email) {
    this.title = title;
    this.foreName = foreName;
    this.lastName = lastName;
    this.position = position;
    this.telephone = telephone;
    this.mobile = mobile;
    this.email = email;
    this.validateSelf();
  }

}
