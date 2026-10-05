package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;
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
public class AppCompanyDetailsRequestDto extends ModelValidator<AppCompanyDetailsRequestDto> {

  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "companyRegistrationNumber",
      example = "OC342786",
      required = true, schema = @Schema(type = "string"))
  private String companyRegistrationNumber;

  @Parameter(in = ParameterIn.QUERY, name = "scheme")
  private Scheme scheme;

  public AppCompanyDetailsRequestDto(String companyRegistrationNumber, Scheme scheme) {
    this.companyRegistrationNumber = companyRegistrationNumber;
    this.scheme = Optional.ofNullable(scheme).isPresent() ? scheme : Scheme.GB;
    this.validate();
  }

}
