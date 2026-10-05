package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelRuleRequestDto {

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "pms", example = "OP",
      required = true, schema = @Schema(type = "string"))
  private String pms;
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "channel", example = "PI",
      required = true, schema = @Schema(type = "string"))
  private String channel;
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "subchannel", example = "WEB",
      required = true, schema = @Schema(type = "string"))
  private String subchannel;
  @Parameter(in = ParameterIn.QUERY, name = "language", example = "EN",
      required = true, schema = @Schema(type = "string"))
  private String language;

}
