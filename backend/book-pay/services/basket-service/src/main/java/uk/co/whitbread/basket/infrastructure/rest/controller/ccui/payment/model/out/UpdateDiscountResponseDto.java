package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDiscountResponseDto {

  private List<String> reservationIds = null;
  private BigDecimal discount;
}
