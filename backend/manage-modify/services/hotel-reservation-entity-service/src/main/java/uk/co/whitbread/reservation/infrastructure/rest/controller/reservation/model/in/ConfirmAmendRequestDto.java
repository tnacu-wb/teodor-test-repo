package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfirmAmendRequestDto {

  @NotNull
  private String originalBookingRef;

  @NotNull
  private String tempBookingRef;

  @NotNull
  private BookingChannelDto bookingChannel;

  private String token;

  private String paymentOptionSelected;

  private String emailAddress;

  private String ccAgentId;
}
