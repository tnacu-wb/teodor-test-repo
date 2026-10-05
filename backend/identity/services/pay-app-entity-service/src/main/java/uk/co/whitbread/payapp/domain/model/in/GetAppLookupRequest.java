package uk.co.whitbread.payapp.domain.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payapp.domain.model.validation.DomainValidator;

@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GetAppLookupRequest extends DomainValidator<GetAppLookupRequest> {

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "lookupNames", required = true)
  List<LookupName> lookupNames;

  @Parameter(in = ParameterIn.QUERY, name = "scheme")
  Scheme scheme;

  public GetAppLookupRequest(List<LookupName> lookupNames, Scheme scheme) {
    this.lookupNames = lookupNames;
    this.scheme = scheme;
    this.validateSelf();
  }
}
