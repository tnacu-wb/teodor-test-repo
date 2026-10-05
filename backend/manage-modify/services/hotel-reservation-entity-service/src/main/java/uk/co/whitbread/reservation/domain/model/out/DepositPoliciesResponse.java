package uk.co.whitbread.reservation.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositPoliciesResponse {

  private CurrencyAmountType amountPaid;
  private CurrencyAmountType amountDue;
  private String policyCode;
}