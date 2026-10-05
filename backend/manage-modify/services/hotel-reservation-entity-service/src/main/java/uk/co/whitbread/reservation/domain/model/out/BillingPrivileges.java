package uk.co.whitbread.reservation.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillingPrivileges {

  private boolean postingRestriction;
  private boolean directBillAuthorized;
  private boolean videoCheckout;

}