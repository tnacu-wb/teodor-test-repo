package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EckohPaymentResponseDto {

  private String paymentId;
}
