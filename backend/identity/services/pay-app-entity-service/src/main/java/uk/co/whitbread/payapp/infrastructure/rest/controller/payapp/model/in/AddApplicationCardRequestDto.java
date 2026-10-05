package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payapp.domain.model.in.Scheme;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.validation.ModelValidator;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
public class AddApplicationCardRequestDto extends ModelValidator<AddApplicationCardRequestDto> {

  @NotBlank
  private String applicationGuid;
  @NotBlank
  private String applicationId;
  private Scheme scheme;
  private Integer employeeId;

  private AddApplicationCardDetailsDto cardDetails;

  public AddApplicationCardRequestDto(String applicationGuid, String applicationId, Scheme scheme,
      Integer employeeId, AddApplicationCardDetailsDto cardDetails) {
    this.applicationGuid = applicationGuid;
    this.applicationId = applicationId;
    this.scheme = scheme;
    this.employeeId = employeeId;
    this.cardDetails = cardDetails;
    this.validate();
  }
}
