package uk.co.whitbread.infrastructure.rest.controller.availability.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
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
public class HotelAvailabilityRequestDto {

  @NotEmpty
  @Parameter(in = ParameterIn.PATH, name = "hotelId", example = "TKINPT", required = true,
      schema = @Schema(type = "string"))
  private String hotelId;

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

  @Parameter(in = ParameterIn.QUERY, name = "roomTypes", example = "DB,TWIN,FAM",
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

  @Parameter(in = ParameterIn.QUERY, name = "companyId", example = "2569623",
      schema = @Schema(type = "string"))
  private String companyId;

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
      schema = @Schema(type = "string"))
  private String country;

  @Parameter(in = ParameterIn.QUERY, name = "language", example = "EMP01",
      required = true, schema = @Schema(type = "string"))
  private List<String> ratePlanCodes;

  @Parameter(in = ParameterIn.QUERY, name = "promotionCode", example = "PROMO",
      schema = @Schema(type = "string"))
  private String promotionCode;

  @Parameter(in = ParameterIn.QUERY, name = "originalBasketReference",
      example = "GBH-214d4b84-5b3c-4670-ba3e-dfe249c52c16", schema = @Schema(type = "string"))
  private String originalBasketReference;

  @Parameter(in = ParameterIn.QUERY, name = "softBundle", example = "rate",
      schema = @Schema(type = "string"))
  private String softBundle;

  @Parameter(in = ParameterIn.QUERY, name = "promoKind", example = "LANDING_PAGE",
      schema = @Schema(type = "string"))
  private PromoKind promoKind;
}
