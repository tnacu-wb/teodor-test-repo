package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EckohPaymentRequestDto {
  @NotEmpty
  private String requestId;
  @NotNull
  private EckohPaymentDto payment;
  @NotNull
  private EckohBookingDto booking;
}
