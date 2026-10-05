package uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.infrastructure.rest.controller.validation.DateFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagesRequestDto {

  @NotBlank
  @Parameter(in = ParameterIn.PATH, name = "hotelId", example = "LONEUS",
      required = true, schema = @Schema(type = "string"))
  private String hotelId;

  @DateFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "startDate", example = "2022-03-01",
      required = true, schema = @Schema(type = "string"))
  private String startDate;

  @DateFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "endDate", example = "2022-03-05",
      required = true, schema = @Schema(type = "string"))
  private String endDate;

  @NotNull(message = "must be specified")
  @Positive(message = "must be positive")
  @Parameter(in = ParameterIn.QUERY, name = "adults", example = "3",
      required = true, schema = @Schema(type = "integer"))
  private Integer adults;

  @NotNull(message = "must be specified")
  @PositiveOrZero(message = "must be positive or zero")
  @Parameter(in = ParameterIn.QUERY, name = "children", example = "0",
      schema = @Schema(type = "integer"))
  private Integer children;

  @NotNull(message = "must be specified")
  @Positive(message = "must be positive")
  @Parameter(in = ParameterIn.QUERY, name = "nrNights", example = "3",
      schema = @Schema(type = "integer"))
  private Integer nrNights;

  @Parameter(in = ParameterIn.QUERY, name = "ratePlanCode", example = "FLEXRATE",
      schema = @Schema(type = "string"))
  private String ratePlanCode;

  @Parameter(in = ParameterIn.QUERY, name = "mealInclusiveRate", example = "true",
      schema = @Schema(type = "boolean"))
  private Boolean mealInclusiveRate;

}