package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByBasketRefResponse;

@Data
@Builder
@AllArgsConstructor
public class UpdateReservationsRequest {

  private BookingChannel bookingChannel;

  private String companyId;

  private List<UpdateReservationRequest> updateReservationsRequest;

  private ReservationByBasketRefResponse tempReservations;

  private Map<String, String> linkAmendReservations;

  private List<Reservation> newRatesReservation;

  private String distributionIATANumber;

  private Boolean clearCcAgentIdUdf;
}
