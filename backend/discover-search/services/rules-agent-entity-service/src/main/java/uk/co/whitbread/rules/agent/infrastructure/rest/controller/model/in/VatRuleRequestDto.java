package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class VatRuleRequestDto {

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "vatRegion", example = "UK",
      required = true, schema = @Schema(type = "string"))
  private String vatRegion;
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "pkgCodeArr", example = "[MD2DIN,FI24HR]",
      required = true, schema = @Schema(type = "object"))
  private List<String> pkgCodeArr;

}
