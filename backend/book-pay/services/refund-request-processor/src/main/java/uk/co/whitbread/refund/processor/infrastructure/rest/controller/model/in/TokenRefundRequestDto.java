package uk.co.whitbread.refund.processor.infrastructure.rest.controller.model.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.refund.processor.domain.model.in.Booking;
import uk.co.whitbread.refund.processor.domain.model.in.Refund;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenRefundRequestDto {

  @NotBlank
  private String requestId;

  @NotBlank
  private String hotelCode;

  @NotNull
  private Refund refund;

  @NotNull
  private Booking booking;

}
