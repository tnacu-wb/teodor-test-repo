package uk.co.whitbread.reservation.domain.model.in;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationsRequest {
  private BookingChannel bookingChannel;
  private List<UpdateReservationRequest> reservations;
  private ReservationByBasketRefResponse tempReservations;
  private Map<String, String> linkAmendReservations;
  private List<Reservation> newRatesReservation;
  private String companyId;
  private String distributionIATANumber;

  private String token;
}
