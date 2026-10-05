package uk.co.whitbread.infrastructure.rest.controller.packages.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.infrastructure.rest.controller.validation.DateFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagesRequestDto {

  @NotEmpty
  @Parameter(in = ParameterIn.PATH, name = "hotelId", example = "TKINPT", required = true,
      schema = @Schema(type = "string"))
  private String hotelId;

  @DateFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "startDate",
      example = "Start date in format yyyy-mm-dd",
      required = true, schema = @Schema(type = "string"))
  private String startDate;

  @DateFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "endDate",
      example = "End date in format yyyy-mm-dd",
      required = true, schema = @Schema(type = "string"))
  private String endDate;

  @Positive(message = "must be positive")
  @Parameter(in = ParameterIn.QUERY, name = "adultsNumber", example = "1", required = true,
      schema = @Schema(type = "integer"))
  private Integer adultsNumber;

  @NotNull
  @PositiveOrZero(message = "must be positive or zero")
  @Parameter(in = ParameterIn.QUERY, name = "childrenNumber", example = "0",
      schema = @Schema(type = "integer"))
  private Integer childrenNumber;

  @NotNull
  @Positive(message = "must be positive")
  @Parameter(in = ParameterIn.QUERY, name = "nightsNumber", example = "2",
      schema = @Schema(type = "integer"))
  private Integer nightsNumber;

  @Parameter(in = ParameterIn.QUERY, name = "ratePlanCode", example = "FLEXRATE",
      schema = @Schema(type = "string"))
  private String ratePlanCode;

  @Parameter(in = ParameterIn.QUERY, name = "language", example = "en", schema = @Schema(type = "string"))
  private String language = "en";

  @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb", schema = @Schema(type = "string"))
  private String country = "gb";

  @Nullable
  @Parameter(in = ParameterIn.QUERY, name = "channel", example = "PI", schema = @Schema(type = "string"))
  private String channel;

  @Nullable
  @Parameter(in = ParameterIn.QUERY, name = "isManageBookingPage", example = "TRUE", schema = @Schema(type = "boolean"))
  private Boolean isManageBookingPage;

  @Nullable
  @Parameter(in = ParameterIn.QUERY, name = "mealInclusiveRate", example = "TRUE", schema = @Schema(type = "boolean"))
  private Boolean mealInclusiveRate;

  @Nullable
  @Parameter(in = ParameterIn.QUERY, name = "packageSelections", example = "OBFPRO,FIFR24,HSCKIF",
          schema = @Schema(type = "string"))
  private String packageSelections;

  @Nullable
  @Parameter(in = ParameterIn.QUERY, name = "isCiol", example = "TRUE", schema = @Schema(type = "boolean"))
  private Boolean isCiol;

  @Nullable
  @Parameter(in = ParameterIn.QUERY, name = "basketReference", example = "abc", schema = @Schema(type = "string"))
  private String basketReference;
}

