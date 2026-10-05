package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrepaidDepositDto {
  private String reservationId;
  private Integer paymentNo;
  private List<ChargeDto> charges;
}
