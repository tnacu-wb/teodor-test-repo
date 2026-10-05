package uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositPoliciesDto {
  private CurrencyAmountTypeDto amountPaid;
  private CurrencyAmountTypeDto amountDue;
  private String policyCode;
}
