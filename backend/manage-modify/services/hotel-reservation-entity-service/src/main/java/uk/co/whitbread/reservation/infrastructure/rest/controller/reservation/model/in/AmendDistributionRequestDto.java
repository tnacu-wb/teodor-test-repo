package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AmendDistributionRequestDto {

  private List<AmendDistributionReservationDto> reservations;
  @NotEmpty
  private String token;
  @NotNull
  private BookingChannelDto bookingChannel;
  private Boolean sendEmailConfirmation;
  private Boolean sendEmailInvoice;
  private String distributionIATANumber;

  @Valid
  private BookerDetailsCnpDto bookerDetails;
  private List<String> bookingNotes;

  private Boolean isOta;
}