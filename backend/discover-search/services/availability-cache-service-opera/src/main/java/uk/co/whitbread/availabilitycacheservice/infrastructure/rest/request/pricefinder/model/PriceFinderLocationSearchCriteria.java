package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SortingOption;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.ArrivalDate;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.PriceFinderSortDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@PriceFinderSortDate
public class PriceFinderLocationSearchCriteria {
  @NotNull
  @Parameter(in = ParameterIn.QUERY, name = "locationId", example = "ChIJdd4hrwug2EcRmSrV3Vo6llI", required = true,
          schema = @Schema(type = "string"))
  private String locationId;

  @NotBlank
  @ArrivalDate
  @Parameter(in = ParameterIn.QUERY, name = "arrival", example = "2025-07-02", required = true, schema =
      @Schema(type = "string"))
  private String arrival;

  @Min(1)
  @Max(364)
  @Parameter(in = ParameterIn.QUERY, name = "daysRange", example = "7", required = true, schema = @Schema(type =
          "integer"))
  private Integer daysRange;

  @Parameter(in = ParameterIn.QUERY, name = "showMinimumNights", example = "true", schema = @Schema(type = "boolean"))
  private boolean showMinimumNights;

  @Min(1)
  @NotNull
  @Parameter(in = ParameterIn.QUERY, name = "page", example = "2", required = true, schema = @Schema(type = "integer"))
  private Integer page;

  @Min(1)
  @NotNull
  @Parameter(in = ParameterIn.QUERY, name = "initialPageSize", example = "15", required = true, schema =
      @Schema(type = "integer"))
  private Integer initialPageSize;

  @Min(1)
  @NotNull
  @Parameter(in = ParameterIn.QUERY, name = "lazyLoadPageSize", example = "10", required = true, schema =
      @Schema(type = "integer"))
  private Integer lazyLoadPageSize;

  @Parameter(in = ParameterIn.QUERY, name = "sortBy", example = "DISTANCE", schema = @Schema(type = "enum",
          allowableValues = {"DISTANCE", "PRICE"}))
  private SortingOption sortBy;

  @Parameter(in = ParameterIn.QUERY, name = "arrival", example = "2025-07-02", schema = @Schema(type = "string"))
  private String sortDate;

  @Parameter(in = ParameterIn.QUERY, name = "filterByRoomType", example = "SB,DB,FAM", description = "Comma-separated"
          + " list of room types in order of precedence (SB,DB,FAM,TWIN,DIS)", schema = @Schema(type = "string"))
  private String filterByRoomType;
}
