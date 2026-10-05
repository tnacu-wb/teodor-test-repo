package uk.co.whitbread.ohip.domain.model.reservation.in;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class SpecialRequests implements SelfValidation<SpecialRequests> {

  @NotEmpty
  private List<String> reservationIds;
  @NotEmpty
  private String hotelId;
  @NotNull
  private List<String> specialRequests;

  private List<String> bookingNotes;

  public SpecialRequests(List<String> reservationIds, String hotelId,
      List<String> specialRequests, List<String> bookingNotes) {
    this.reservationIds = reservationIds;
    this.hotelId = hotelId;
    this.specialRequests = specialRequests;
    this.bookingNotes = bookingNotes;
    this.validateSelf();
  }
}
