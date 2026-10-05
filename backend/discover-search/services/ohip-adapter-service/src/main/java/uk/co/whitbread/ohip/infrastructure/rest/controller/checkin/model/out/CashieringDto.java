package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CashieringDto {

  private RevenuesAndBalancesDto revenuesAndBalances;
  private BillingPrivilegesDto billingPrivileges;
  private CheckInTaxTypeDto taxType;
  private CompAccountingDto compAccounting;
  private boolean reverseCheckInAllowed;
  private boolean reverseAdvanceCheckInAllowed;
  private boolean transactionsPosted;

}
