package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.validation.ModelValidator;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
public class ContactInfoDto extends ModelValidator<ContactInfoDto> {

  @NotBlank
  private String title;

  @NotBlank
  private String foreName;

  @NotBlank
  private String lastName;

  private String position;

  private String telephone;

  private String mobile;

  @NotBlank
  private String email;

  public ContactInfoDto(String title, String foreName, String lastName, String position,
      String telephone, String mobile, String email) {
    this.title = title;
    this.foreName = foreName;
    this.lastName = lastName;
    this.position = position;
    this.telephone = telephone;
    this.mobile = mobile;
    this.email = email;
    this.validate();
  }

}
