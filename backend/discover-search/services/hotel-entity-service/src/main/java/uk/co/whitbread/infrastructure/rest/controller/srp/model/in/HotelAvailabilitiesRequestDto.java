package uk.co.whitbread.infrastructure.rest.controller.srp.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import uk.co.whitbread.infrastructure.rest.controller.validation.CountryFormat;
import uk.co.whitbread.infrastructure.rest.controller.validation.DateFormat;
import uk.co.whitbread.infrastructure.rest.controller.validation.LanguageFormat;
import uk.co.whitbread.infrastructure.rest.controller.validation.RequestDtoFormat;
import uk.co.whitbread.infrastructure.rest.controller.validation.RoomOccupanciesConstraint;

@RoomOccupanciesConstraint
@Data
@AllArgsConstructor
@RequestDtoFormat
public class HotelAvailabilitiesRequestDto {

  @NotEmpty
  @Parameter(in = ParameterIn.PATH, name = "location", example = "ChIJdd4hrwug2EcRmSrV3Vo6llI",
      required = true, schema = @Schema(type = "string"))
  private String location;

  @NotNull
  @Parameter(in = ParameterIn.PATH, name = "locationFormat", example = "PLACEID", required = true,
      schema = @Schema(implementation = LocationFormatEnumDto.class))
  private LocationFormatEnumDto locationFormat;

  @NotNull
  @PositiveOrZero
  @Parameter(in = ParameterIn.PATH, name = "radius", example = "50", required = true,
      schema = @Schema(type = "string"))
  private Integer radius;

  @NotNull
  @Parameter(in = ParameterIn.PATH, name = "radiusUnit", example = "MILES", required = true,
      schema = @Schema(implementation = RadiusUnitEnumDto.class))
  private RadiusUnitEnumDto radiusUnit;

  @DateFormat
  @Parameter(in = ParameterIn.QUERY, name = "arrivalDate",
      example = "Arrival date in format yyyy-mm-dd", schema = @Schema(type = "string"))
  private String arrivalDate;

  @DateFormat
  @Parameter(in = ParameterIn.QUERY, name = "departureDate",
      example = "Departure date in format yyyy-mm-dd", schema = @Schema(type = "string"))
  private String departureDate;

  @LanguageFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
      required = true, schema = @Schema(type = "string"))
  private String language;

  @CountryFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
      required = true, schema = @Schema(type = "string"))
  private String country;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "adultsNumber", example = "1,2,2", required = true,
      schema = @Schema(type = "array"))
  private List<Integer> adultsNumber;

  @Parameter(in = ParameterIn.QUERY, name = "childrenNumber", example = "0,0,2",
      schema = @Schema(type = "array"))
  private List<Integer> childrenNumber;

  @Parameter(in = ParameterIn.QUERY, name = "roomTypes", example = "DB,TWIN,FAM",
      schema = @Schema(type = "array"))
  private List<String> roomTypes;

  @NotNull
  @Parameter(in = ParameterIn.QUERY, name = "oldWorldChannel", example = "MOBILE,WEB,WEB_DE,CBT",
      required = true, schema = @Schema(implementation = OldWorldChannelEnumDto.class))
  private OldWorldChannelEnumDto oldWorldChannel;

  @NotNull
  @Parameter(in = ParameterIn.QUERY, name = "channel", example = "PI,CCUI,BB",
      required = true, schema = @Schema(type = "string"))
  private String channel;

  @NotNull
  @Parameter(in = ParameterIn.QUERY, name = "subChannel", example = "WEB",
      required = true, schema = @Schema(type = "string"))
  private String subChannel;

  @Parameter(in = ParameterIn.QUERY, name = "companyId", example = "2569623",
      schema = @Schema(type = "string"))
  private String companyId;

  @Min(1)
  @NotNull
  @Parameter(in = ParameterIn.QUERY, name = "page", example = "10", required = true,
      schema = @Schema(type = "integer"))
  private Integer page;

  @Min(1)
  @NotNull
  @Parameter(in = ParameterIn.QUERY, name = "initialPageSize", example = "10",
      schema = @Schema(type = "integer"))
  private Integer initialPageSize;

  @Min(1)
  @NotNull
  @Parameter(in = ParameterIn.QUERY, name = "lazyLoadPageSize", example = "10",
      schema = @Schema(type = "integer"))
  private Integer lazyLoadPageSize;

  @Parameter(in = ParameterIn.QUERY, name = "sort", example = "DISTANCE or PRICE",
      schema = @Schema(type = "string"))
  private String sort;

  @Min(0)
  @Max(1)
  @Parameter(in = ParameterIn.QUERY, name = "rcPriceModifier", example = "0.3",
      schema = @Schema(type = "number"))
  private Float rcPriceModifier;

  @Min(0)
  @Max(1)
  @Parameter(in = ParameterIn.QUERY, name = "rcDistanceModifier", example = "0.5",
      schema = @Schema(type = "number"))
  private Float rcDistanceModifier;

  @Parameter(in = ParameterIn.QUERY, name = "filters", example = "EAT,LFT",
      schema = @Schema(type = "array"))
  private List<String> filters;

  @Parameter(in = ParameterIn.QUERY, name = "language", example = "EMP01", schema = @Schema(type = "string"))
  private List<String> ratePlanCodes;

  @Min(0)
  @Max(1)
  @Parameter(in = ParameterIn.QUERY, name = "rcHubModifier", example = "0.85",
          schema = @Schema(type = "number"))
  private Float rcHubModifier;
}