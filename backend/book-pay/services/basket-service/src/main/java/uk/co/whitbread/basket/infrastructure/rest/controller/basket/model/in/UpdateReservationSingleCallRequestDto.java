package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.PaymentRequestDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationSingleCallRequestDto implements
    SelfValidation<UpdateReservationSingleCallRequestDto> {
  private String basketReference;
  private String hotelId;
  private String requestId;
  @JsonProperty("reservationGuest")
  @Valid
  private ReservationGuestRequestDto reservationGuest;
  @JsonProperty("ancillaries")
  @Valid
  private ReservationPackagesRequestDto ancillaries;
  @JsonProperty("createPayment")
  private PaymentRequestDto createPayment;
  @JsonProperty("priceBreakdownNeeded")
  private String priceBreakdownNeeded;
}
