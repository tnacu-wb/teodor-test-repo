package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payapp.domain.model.in.LookupName;
import uk.co.whitbread.payapp.domain.model.in.Scheme;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.validation.ModelValidator;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
public class GetAppLookupRequestDto extends
    ModelValidator<GetAppLookupRequestDto> {

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "lookupNames", required = true)
  private List<LookupName> lookupNames;

  @Parameter(in = ParameterIn.QUERY, name = "scheme")
  private Scheme scheme;

  public GetAppLookupRequestDto(List<LookupName> lookupNames, Scheme scheme) {
    this.lookupNames = lookupNames;
    this.scheme = Optional.ofNullable(scheme).isPresent() ? scheme : Scheme.GB;
    this.validate();
  }
}
