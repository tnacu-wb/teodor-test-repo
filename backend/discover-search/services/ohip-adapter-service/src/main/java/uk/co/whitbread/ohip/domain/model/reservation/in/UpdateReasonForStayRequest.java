package uk.co.whitbread.ohip.domain.model.reservation.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class UpdateReasonForStayRequest implements SelfValidation<UpdateReasonForStayRequest> {

  @NotNull
  private List<String> reservationIds;
  @NotNull
  private String hotelId;
  @NotEmpty
  @Schema(example = "LEI", required = true)
  private String reasonForStay;

  public UpdateReasonForStayRequest(List<String> reservationIds, String hotelId,
      String reasonForStay) {
    this.reservationIds = reservationIds;
    this.hotelId = hotelId;
    this.reasonForStay = reasonForStay;
    this.validateSelf();
  }
}
