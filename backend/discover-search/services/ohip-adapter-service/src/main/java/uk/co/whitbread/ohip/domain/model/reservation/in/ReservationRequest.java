package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@EqualsAndHashCode(callSuper = false)
public class ReservationRequest implements SelfValidation<ReservationRequest> {

  @NotEmpty
  private List<Reservation> reservations;

  @NotNull
  private BookingChannel bookingChannel;

  private String reasonForStay;

  private boolean getReservationsByIds;

  public ReservationRequest(
      List<Reservation> reservations, BookingChannel bookingChannel, Boolean getReservationsByIds) {
    this.reservations = reservations;
    this.bookingChannel = bookingChannel;
    this.getReservationsByIds = getReservationsByIds;
    this.validateSelf();
  }
}
