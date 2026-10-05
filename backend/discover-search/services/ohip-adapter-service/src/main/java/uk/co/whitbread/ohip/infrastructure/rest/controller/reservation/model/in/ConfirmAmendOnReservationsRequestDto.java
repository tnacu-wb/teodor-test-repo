package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class ConfirmAmendOnReservationsRequestDto {

  @NotNull
  private String hotelId;
  @NotNull
  private BookingChannelDto bookingChannel;
  @NotNull
  private List<String> tempReservations;
  @NotNull
  private List<String> originalReservations;
  private Map<String, String> linkAmendReservations;
  private Boolean sendEmailConfirmation;
  private Boolean sendEmailInvoice;
  private Boolean markAsPayOnArrival;
  private Boolean clearCcAgentIdUdf;
}
