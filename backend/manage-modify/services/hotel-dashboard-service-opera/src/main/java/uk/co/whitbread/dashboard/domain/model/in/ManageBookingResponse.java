package uk.co.whitbread.dashboard.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManageBookingResponse {

  private Boolean isCancellable;
  private Boolean isAmendable;
  private Boolean isRuleCompliant;
  private String aemLabelKey;
  private Boolean isCheckInOnlineAvailable;

}