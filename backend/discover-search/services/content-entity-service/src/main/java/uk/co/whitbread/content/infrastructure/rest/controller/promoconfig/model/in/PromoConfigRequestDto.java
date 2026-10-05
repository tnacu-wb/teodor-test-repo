package uk.co.whitbread.content.infrastructure.rest.controller.promoconfig.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoCodeStatus;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoKind;

@Data
@Builder
public class PromoConfigRequestDto {

  @Parameter(in = ParameterIn.QUERY, name = "promotionCode", example = "ST10R",
      schema = @Schema(type = "string"))
  private String promotionCode;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "BookingDate",
      example = "Arrival date in format yyyy-mm-dd",
      required = true, schema = @Schema(type = "string"))
  private String bookingDate;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "stayStartDate",
      example = "Arrival date in format yyyy-mm-dd",
      required = true, schema = @Schema(type = "string"))
  private String stayStartDate;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "stayEndDate",
      example = "Arrival date in format yyyy-mm-dd",
      required = true, schema = @Schema(type = "string"))
  private String stayEndDate;

  @Parameter(in = ParameterIn.QUERY, name = "promoKind",
      example = "SITE_WIDE", schema = @Schema(type = "string"))
  private PromoKind promoKind;

  @Parameter(in = ParameterIn.QUERY, name = "isAmendRequest",
      example = "true", schema = @Schema(type = "boolean"))
  private Boolean isAmendRequest;

  @Parameter(in = ParameterIn.QUERY, name = "isPromoBox",
      example = "true", schema = @Schema(type = "boolean"))
  private Boolean isPromoBox;

  @Parameter(in = ParameterIn.QUERY, name = "operaPromoCode", example = "ST10R",
      schema = @Schema(type = "string"))
  private String operaPromoCode;

  @Parameter(in = ParameterIn.QUERY, name = "uniquePromoCodeStatus",
      example = "ISSUED", schema = @Schema(type = "string"))
  private PromoCodeStatus uniquePromoCodeStatus;

  @Parameter(in = ParameterIn.QUERY, name = "rateName",
          example = "FLEXRATE", schema = @Schema(type = "string"))
  private String rateName;

  @Parameter(in = ParameterIn.QUERY, name = "roomClass",
          example = "ST", schema = @Schema(type = "string"))
  private String roomClass;

  @Parameter(in = ParameterIn.QUERY, name = "maxRooms",
          example = "3", schema = @Schema(type = "integer"))
  private Integer noOfRooms;
}
