package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.ccui.CcuiExtraItemsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.ccui.PaymentCcuiRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.BookingChannelDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmAmendLogicRequestDto {

  @NotNull
  private String originalBookingRef;
  @NotNull
  private String tempBookingRef;
  @NotNull
  private BookingChannelDto bookingChannel;
  @NotNull
  private String token;
  private PaymentOptionDto paymentOptionSelected;
  @NotNull
  private String environment;
  private String emailAddress;
  private CcuiExtraItemsDto ccuiExtraItems;
  private PaymentCcuiRequestDto paymentRequest;
  private String paymentOption;
  private String subPaymentType;
  private List<String> preCheckIn;
}
