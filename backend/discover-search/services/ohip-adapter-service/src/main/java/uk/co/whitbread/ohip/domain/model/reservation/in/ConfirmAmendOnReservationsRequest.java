package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmAmendOnReservationsRequest {

  @NotNull
  private String hotelId;

  @NotNull
  private List<String> tempReservations;

  @NotNull
  private List<String> originalReservations;

  @NotNull
  private BookingChannel bookingChannel;
  private Map<String, String> linkAmendReservations;
  private Boolean sendEmailConfirmation;
  private Boolean sendEmailInvoice;
  private Boolean markAsPayOnArrival;
  private Boolean clearCcAgentIdUdf;
}
