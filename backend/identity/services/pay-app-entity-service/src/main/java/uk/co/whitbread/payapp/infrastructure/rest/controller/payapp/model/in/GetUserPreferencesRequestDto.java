package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.validation.ModelValidator;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserPreferencesRequestDto extends ModelValidator<GetUserPreferencesRequestDto> {

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, required = true)
  private List<String> tetheredUserGuids;
}
