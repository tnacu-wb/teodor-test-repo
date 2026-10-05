package uk.co.whitbread.reservation.domain.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PromoKind;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class RoomRate implements SelfValidation<RoomRate> {

  @NotNull
  @Schema(example = "2015-10-20", required = true)
  private String startDate;
  @NotNull
  @Schema(example = "2015-10-21", required = true)
  private String endDate;
  @NotEmpty
  @Schema(example = "SDB", required = true)
  private String pmsRoomType;
  @Schema(example = "DOUBLE", required = false)
  private String operaRoomType;
  @NotEmpty
  @Schema(example = "DAILY", required = true)
  private String ratePlanCode;
  @Schema(example = "NEG")
  private String rateDisplaySet;
  @Schema(example = "SNGL")
  private List<String> specialRequests;
  private String cellCode;
  private List<RatePrice> ratePrices;
  private Boolean fixedRate;
  private String promotionCode;
  private PromoKind promoKind;
}
