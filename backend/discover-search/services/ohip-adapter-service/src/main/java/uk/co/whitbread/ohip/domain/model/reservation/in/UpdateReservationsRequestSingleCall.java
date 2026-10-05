package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByBasketRefResponseSingleCall;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReservationsRequestSingleCall {

  private BookingChannelSingleCall bookingChannel;

  private List<UpdateReservationRequestSingleCall> reservations;

  private ReservationByBasketRefResponseSingleCall tempReservations;

  private Map<String, String> linkAmendReservations;

  private String companyId;

  private String distributionIATANumber;

  private String token;

}