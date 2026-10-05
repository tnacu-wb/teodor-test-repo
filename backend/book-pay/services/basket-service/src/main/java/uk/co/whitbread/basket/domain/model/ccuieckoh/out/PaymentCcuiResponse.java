package uk.co.whitbread.basket.domain.model.ccuieckoh.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCcuiResponse {

  private String reference;
  private String bookingReference;
  private BasketStatus status;
}
