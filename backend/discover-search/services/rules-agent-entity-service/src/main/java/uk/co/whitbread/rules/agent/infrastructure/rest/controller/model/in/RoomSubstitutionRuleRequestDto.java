package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSubstitutionRuleRequestDto {

  @NotNull
  @Min(1)
  @Max(2)
  @Parameter(in = ParameterIn.QUERY, name = "adults", example = "1",
      required = true, schema = @Schema(type = "integer"))
  private Integer adults;

  @NotNull
  @Min(0)
  @Max(3)
  @Parameter(in = ParameterIn.QUERY, name = "children", example = "0",
      required = true, schema = @Schema(type = "integer"))
  private Integer children;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "roomType", example = "Double",
      required = true, schema = @Schema(type = "string"))
  private String roomType;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "pms", example = "OP",
      required = true, schema = @Schema(type = "string"))
  private String pms;

  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "channel", example = "PI",
      required = true, schema = @Schema(type = "string"))
  private String channel;
}
