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
public class MaxRoomOccupancyRequestDto {

  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "channelId", example = "CCUI",
      required = true, schema = @Schema(type = "string"))
  private String channelId;

  @Parameter(in = ParameterIn.QUERY, name = "brand", example = "HUB", required = false,
      schema = @Schema(type = "string"))
  private String brand;
}
