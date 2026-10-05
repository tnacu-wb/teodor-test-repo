package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;


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
  private Long paymentNo;
  private List<ChargeDto> charges;

}
