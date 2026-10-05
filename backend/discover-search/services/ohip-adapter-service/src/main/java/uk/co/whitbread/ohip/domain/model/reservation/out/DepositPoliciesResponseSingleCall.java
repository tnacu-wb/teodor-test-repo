package uk.co.whitbread.ohip.domain.model.reservation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositPoliciesResponseSingleCall {
  private CurrencyAmountTypeSingleCall amountPaid;
  private CurrencyAmountTypeSingleCall amountDue;
  private String policyCode;
}