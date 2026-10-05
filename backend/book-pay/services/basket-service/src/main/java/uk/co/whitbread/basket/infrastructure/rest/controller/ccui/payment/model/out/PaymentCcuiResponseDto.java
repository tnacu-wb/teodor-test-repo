package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCcuiResponseDto {

  private String reference;
  private BasketStatus status;
}
