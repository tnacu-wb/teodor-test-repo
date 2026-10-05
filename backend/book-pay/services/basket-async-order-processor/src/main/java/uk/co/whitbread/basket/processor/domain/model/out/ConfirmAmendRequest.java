package uk.co.whitbread.basket.processor.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ConfirmAmendRequest {

  private String originalBookingRef;
  private String tempBookingRef;
  private BookingChannel bookingChannel;
  private String token;
  private String paymentOptionSelected;
  private String emailAddress;
  private String ccAgentId;
}
