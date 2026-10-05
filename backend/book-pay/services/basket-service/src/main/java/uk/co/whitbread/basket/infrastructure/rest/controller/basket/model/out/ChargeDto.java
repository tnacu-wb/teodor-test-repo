package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargeDto {

  private String transactionCode;
  private Integer postingQuantity;
  private String postingReference;
  private ChargeAmountDto chargeAmount;
}
