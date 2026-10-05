package uk.co.whitbread.infrastructure.rest.controller.availability.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.infrastructure.rest.controller.validation.ArrivalDepartureDateConstraint;
import uk.co.whitbread.infrastructure.rest.controller.validation.DateFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ArrivalDepartureDateConstraint()
public class HotelAvailabilitiesByIdsRequestDto {

  @NotNull(message = "at least one hotelId must be specified")
  @ArraySchema(arraySchema = @Schema(required = true, name = "hotelIds", example = "LONEUS,LONKIN"))
  private List<String> hotelIds;

  @DateFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "arrivalDate",
      example = "Arrival date in format yyyy-mm-dd",
      required = true, schema = @Schema(type = "string"))
  private String arrivalDate;

  @DateFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "departureDate",
      example = "Departure date in format yyyy-mm-dd",
      required = true, schema = @Schema(type = "string"))
  private String departureDate;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "roomTypes", example = "DB,TWIN,FAM", required = true,
      schema = @Schema(type = "array"))
  private List<String> roomTypes;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "adultsNumber", example = "1,2,2", required = true,
      schema = @Schema(type = "array"))
  private List<Integer> adultsNumber;

  @Parameter(in = ParameterIn.QUERY, name = "childrenNumber", example = "0,0,2",
      schema = @Schema(type = "array"))
  private List<Integer> childrenNumber;

  @Parameter(in = ParameterIn.QUERY, name = "cotsRequired", example = "true,false,true",
      schema = @Schema(type = "array"))
  private List<Boolean> cotsRequired;

  @Parameter(in = ParameterIn.QUERY, name = "ratePlanCodes", example = "FLEXRATE",
      schema = @Schema(type = "string"))
  private List<String> ratePlanCodes;

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

  @Parameter(in = ParameterIn.QUERY, name = "country", example = "GB",
      required = false, schema = @Schema(type = "string"))
  private String country;

  @Parameter(in = ParameterIn.QUERY, name = "globalCompanyId",
      example = "1370", schema = @Schema(type = "string"))
  private String globalCompanyId;

  @Parameter(in = ParameterIn.QUERY, name = "negotiatedRateDisplaySets", example = "BMD",
      required = true, schema = @Schema(type = "string"))
  private String[] negotiatedRateDisplaySets;

  @Parameter(in = ParameterIn.QUERY, name = "vatNotRequired", example = "true",
      schema = @Schema(type = "boolean"))
  private boolean vatNotRequired;

  @Parameter(in = ParameterIn.QUERY, name = "roomTypes", example = "DOUBLE, PPLDBL",
      schema = @Schema(type = "array"))
  private List<String> pmsRoomTypes;

  @Parameter(in = ParameterIn.QUERY, name = "isOTA", example = "true",
      schema = @Schema(type = "boolean"))
  private Boolean isOTA;
}
