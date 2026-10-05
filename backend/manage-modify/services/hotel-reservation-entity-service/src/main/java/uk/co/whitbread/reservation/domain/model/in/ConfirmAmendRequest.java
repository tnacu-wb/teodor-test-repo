package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmAmendRequest implements SelfValidation<ConfirmAmendRequest> {

  @NotNull
  private String originalBookingRef;

  @NotNull
  private String tempBookingRef;

  @NotNull
  private BookingChannel bookingChannel;

  private String token;

  private Boolean sendEmailConfirmation;

  private Boolean sendEmailInvoice;

  private String paymentOptionSelected;

  private String emailAddress;

  private String ccAgentId;

  public ConfirmAmendRequest(String originalBookingRef, String tempBookingRef, String token,
                             BookingChannel bookingChannel, Boolean sendEmailConfirmation, Boolean sendEmailInvoice,
                             String paymentOptionSelected, String emailAddress, String ccAgentId) {
    this.originalBookingRef = originalBookingRef;
    this.tempBookingRef = tempBookingRef;
    this.bookingChannel = bookingChannel;
    this.token = token;
    this.sendEmailConfirmation = sendEmailConfirmation;
    this.sendEmailInvoice = sendEmailInvoice;
    this.paymentOptionSelected = paymentOptionSelected;
    this.emailAddress = emailAddress;
    this.ccAgentId = ccAgentId;
    this.validateSelf();
  }
}
