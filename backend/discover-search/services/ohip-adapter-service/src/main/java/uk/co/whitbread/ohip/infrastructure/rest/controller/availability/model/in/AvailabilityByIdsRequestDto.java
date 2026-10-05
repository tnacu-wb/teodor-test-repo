package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.infrastructure.rest.controller.validation.DateFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityByIdsRequestDto {

  @NotNull(message = "at least one hotelId must be specified")
  @ArraySchema(arraySchema = @Schema(required = true, name = "hotelIds", example = "LONEUS,LONKIN"))
  private String[] hotelIds;

  @DateFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "arrivalDate", example = "2022-03-01",
      required = true, schema = @Schema(type = "string"))
  private String arrivalDate;

  @DateFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "departureDate", example = "2022-03-05",
      required = true, schema = @Schema(type = "string"))
  private String departureDate;

  @NotNull(message = "at least one roomTypes value must be specified")
  @ArraySchema(arraySchema = @Schema(required = true,
      name = "roomTypes",
      example = "[\"DB\", \"TWIN\"]"))
  private String[] roomTypes;

  @NotNull(message = "at least one adults value must be specified")
  @ArraySchema(arraySchema = @Schema(required = true,
      name = "adults",
      example = "[\"2\", \"0\"]"))
  private Integer[] adults;

  @ArraySchema(arraySchema = @Schema(
      name = "children",
      example = "[\"2\", \"0\"]"))
  private Integer[] children;

  @ArraySchema(arraySchema = @Schema(
      name = "cotsRequired",
      example = "[\"true\", \"false\"]"))
  private Boolean[] cotsRequired;

  @Parameter(in = ParameterIn.QUERY, name = "ratePlanCodes", example = "FLEXRATE",
      required = true, schema = @Schema(type = "string"))
  private String[] ratePlanCodes;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "channel", example = "PI",
      required = true, schema = @Schema(type = "string"))
  private String channel;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "subchannel", example = "WEB",
      required = true, schema = @Schema(type = "string"))
  private String subchannel;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
      required = true, schema = @Schema(type = "string"))
  private String language;

  @Parameter(in = ParameterIn.QUERY, name = "globalCompanyId",
      example = "1370", schema = @Schema(type = "string"))
  private String globalCompanyId;

  @Parameter(in = ParameterIn.QUERY, name = "negotiatedRateDisplaySets", example = "BMD",
      required = true, schema = @Schema(type = "string"))
  private String[] negotiatedRateDisplaySets;

  @ArraySchema(arraySchema = @Schema(
      name = "pmsRoomTypes",
      example = "[\"PPLDBL\", \"FMTRPL\"]"))
  private String[] pmsRoomTypes;
}
