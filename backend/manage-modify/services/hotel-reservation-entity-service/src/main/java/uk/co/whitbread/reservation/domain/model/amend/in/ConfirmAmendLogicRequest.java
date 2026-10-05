package uk.co.whitbread.reservation.domain.model.amend.in;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.payment.in.ccui.CcuiExtraItems;
import uk.co.whitbread.reservation.domain.model.payment.in.ccui.PaymentCcuiRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmAmendLogicRequest {

  @NotNull
  private String originalBookingRef;

  @NotNull
  private String tempBookingRef;

  @NotNull
  private BookingChannel bookingChannel;

  @NotNull
  private String token;
  private PaymentOption paymentOptionSelected;

  @NotNull
  private String environment;

  private String emailAddress;
  private CcuiExtraItems ccuiExtraItems;
  private PaymentCcuiRequest paymentRequest;
  private String paymentOption;
  private String subPaymentType;
  private List<String> preCheckIn;
}
