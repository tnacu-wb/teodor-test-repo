package uk.co.whitbread.reservation.domain.model.in;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfirmAmendOnReservationsRequest {
  private String hotelId;
  private BookingChannel bookingChannel;
  private List<String> tempReservations;
  private List<String> originalReservations;
  private Map<String, String> linkAmendReservations;
  private Boolean sendEmailConfirmation;
  private Boolean sendEmailInvoice;
  private Boolean markAsPayOnArrival;
  private Boolean clearCcAgentIdUdf;
}
