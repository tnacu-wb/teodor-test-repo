package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RbacRuleRoleIdsRequestDto {

  @Parameter(in = ParameterIn.QUERY, name = "roleIdList", example = "AGENT_ROLE",
      required = true, schema = @Schema(type = "string"))
  private List<String> roleIdList;


}
