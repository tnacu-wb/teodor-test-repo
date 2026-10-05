package uk.co.whitbread.ohip.domain.model.availability.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class RateCodeCriteria implements SelfValidation<RateCodeCriteria> {

  @NotEmpty
  private String hotelId;
  @NotEmpty
  private String arrivalDate;
  @NotEmpty
  private String departureDate;
  @NotEmpty
  private String ratePlanCode;

  @Singular("roomInfoCriteria")
  @NotNull
  private List<RateCodeRoomInfoCriteria> roomInfoCriteriaList;

  public RateCodeCriteria(String hotelId, String arrivalDate, String departureDate,
      String ratePlanCode, List<RateCodeRoomInfoCriteria> roomInfoCriteriaList) {
    this.hotelId = hotelId;
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.ratePlanCode = ratePlanCode;
    this.roomInfoCriteriaList = roomInfoCriteriaList;
    this.validateSelf();
  }
}
