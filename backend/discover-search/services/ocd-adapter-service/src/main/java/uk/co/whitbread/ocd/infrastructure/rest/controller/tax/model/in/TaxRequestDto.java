package uk.co.whitbread.ocd.infrastructure.rest.controller.tax.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ocd.infrastructure.rest.controller.validation.ArrivalDepartureDateConstraint;
import uk.co.whitbread.ocd.infrastructure.rest.controller.validation.DateFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ArrivalDepartureDateConstraint
public class TaxRequestDto {

  @DateFormat
  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "arrivalDate", example = "2025-08-30",
      required = true, schema = @Schema(type = "string"))
  private String arrivalDate;

  @DateFormat
  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "departureDate", example = "2025-08-30",
      required = true, schema = @Schema(type = "string"))
  private String departureDate;

  @NotNull
  @Parameter(in = ParameterIn.QUERY, name = "adults", example = "2",
      required = true, schema = @Schema(type = "integer"))
  private Integer adults;

  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "ratePlanCode", example = "FLEXRATE",
      required = true, schema = @Schema(type = "string"))
  private String ratePlanCode;

  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "roomType", example = "DOUBLE",
      required = true, schema = @Schema(type = "string"))
  private String roomType;

}
