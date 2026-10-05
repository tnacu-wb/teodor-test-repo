package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
public class ApplicationDetailsRequestDto extends ModelValidator<ApplicationDetailsRequestDto> {

  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "applicationId", required = true)
  private String applicationId;

  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "applicationGuid", required = true)
  private String applicationGuid;

  @Parameter(in = ParameterIn.QUERY, name = "scheme")
  private Scheme scheme;

  public ApplicationDetailsRequestDto(String applicationId, String applicationGuid, Scheme scheme) {
    this.applicationId = applicationId;
    this.applicationGuid = applicationGuid;
    this.scheme = scheme;
    this.validate();
  }

}
