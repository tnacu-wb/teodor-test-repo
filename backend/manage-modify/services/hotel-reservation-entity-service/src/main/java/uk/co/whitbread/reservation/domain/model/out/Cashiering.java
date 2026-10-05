package uk.co.whitbread.reservation.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cashiering {

  private BillingPrivileges billingPrivileges;
  private CheckInTaxType taxType;
  private CompAccounting compAccounting;
  private boolean reverseCheckInAllowed;
  private boolean reverseAdvanceCheckInAllowed;
  private boolean transactionsPosted;

}