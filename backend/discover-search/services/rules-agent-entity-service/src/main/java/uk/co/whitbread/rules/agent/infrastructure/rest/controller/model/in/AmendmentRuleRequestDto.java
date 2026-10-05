package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.validation.AmendmentRuleDtoRequestFormat;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.validation.DateFormat;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.validation.DateTimeFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AmendmentRuleDtoRequestFormat
public class AmendmentRuleRequestDto {

  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "rateType", example = "Flex",
      required = true, schema = @Schema(type = "string"))
  private String rateType;

  @DateFormat
  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "arrivalDate", example = "YYYYMMDD",
      required = true, schema = @Schema(type = "string"))
  private String arrivalDate;

  @DateTimeFormat
  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "hotelLocalDateTime", example = "YYYYMMDDTHHmmss",
      required = true, schema = @Schema(type = "string"))
  private String hotelLocalDateTime;

  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "hotelCountryCode", example = "GB",
      required = true, schema = @Schema(type = "string"))
  private String hotelCountryCode;
}
