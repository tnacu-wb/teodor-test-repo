package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RbacRuleRequestDto {

  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "resourceId", example = "CCUI_RES1",
      required = true, schema = @Schema(type = "string"))
  private String resourceId;
  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "roleId", example = "AGENT",
      required = true, schema = @Schema(type = "string"))
  private String roleId;
}
