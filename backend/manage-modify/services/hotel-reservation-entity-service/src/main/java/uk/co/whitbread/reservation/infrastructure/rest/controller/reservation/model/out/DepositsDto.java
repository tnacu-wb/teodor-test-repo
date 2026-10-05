package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepositsDto {
  private String paymentReference;
  private CurrencyAmountTypeDto postedAmount;
}