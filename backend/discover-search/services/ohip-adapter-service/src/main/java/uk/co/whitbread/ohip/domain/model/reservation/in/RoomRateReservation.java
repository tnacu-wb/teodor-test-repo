package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RoomRateReservation implements SelfValidation<RoomRateReservation> {

  @NotEmpty
  private String start;
  @NotEmpty
  private String end;
  @NotEmpty
  private String roomType;
  @NotEmpty
  private String ratePlanCode;
  private List<String> specialRequests;
  private String cellCode;
  private List<RatePrice> ratePrices;
  private String promotionCode;

  public RoomRateReservation(String start, String end, String roomType, String ratePlanCode) {
    this.start = start;
    this.end = end;
    this.roomType = roomType;
    this.ratePlanCode = ratePlanCode;
    this.validateSelf();
  }

  public RoomRateReservation(String start, String end, String roomType, String ratePlanCode,
      List<String> specialRequests, String cellCode, List<RatePrice> ratePrices, String promotionCode) {
    this.start = start;
    this.end = end;
    this.roomType = roomType;
    this.ratePlanCode = ratePlanCode;
    this.specialRequests = specialRequests;
    this.cellCode = cellCode;
    this.ratePrices = ratePrices;
    this.promotionCode = promotionCode;
    this.validateSelf();
  }
}
