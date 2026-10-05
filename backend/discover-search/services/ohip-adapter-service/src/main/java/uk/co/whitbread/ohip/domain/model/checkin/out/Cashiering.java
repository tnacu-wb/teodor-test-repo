package uk.co.whitbread.ohip.domain.model.checkin.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cashiering {

  private RevenuesAndBalances revenuesAndBalances;
  private BillingPrivileges billingPrivileges;
  private CheckInTaxType taxType;
  private CompAccounting compAccounting;
  private boolean reverseCheckInAllowed;
  private boolean reverseAdvanceCheckInAllowed;
  private boolean transactionsPosted;

}
