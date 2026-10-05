package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DepositPoliciesDto {

  private CurrencyAmountTypeDto amountPaid;
  private CurrencyAmountTypeDto amountDue;
  private String policyCode;

}
