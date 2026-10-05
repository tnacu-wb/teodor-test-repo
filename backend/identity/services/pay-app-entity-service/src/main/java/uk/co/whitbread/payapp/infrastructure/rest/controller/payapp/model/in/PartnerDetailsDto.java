package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.validation.ModelValidator;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
public class PartnerDetailsDto extends ModelValidator<PartnerDetailsDto> {

  @NotBlank
  private String title;

  @NotBlank
  private String foreName;

  @NotBlank
  private String lastName;

  @NotBlank
  private String dateOfBirth;

  @NotNull
  private Integer numberOfPartners;

  public PartnerDetailsDto(String title, String foreName, String lastName, String dateOfBirth,
      Integer numberOfPartners) {
    this.title = title;
    this.foreName = foreName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.numberOfPartners = numberOfPartners;
    this.validate();
  }

}
