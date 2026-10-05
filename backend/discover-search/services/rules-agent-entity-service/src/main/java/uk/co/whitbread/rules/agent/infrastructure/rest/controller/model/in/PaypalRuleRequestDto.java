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
public class PaypalRuleRequestDto {

  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "channelId", example = "PI",
      required = true, schema = @Schema(type = "string"))
  private String channelId;

  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "country", example = "DE",
          required = true, schema = @Schema(type = "string"))
  private String country;

  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "hotelId", example = "BMIB",
          required = true, schema = @Schema(type = "string"))
  private String hotelId;
}
