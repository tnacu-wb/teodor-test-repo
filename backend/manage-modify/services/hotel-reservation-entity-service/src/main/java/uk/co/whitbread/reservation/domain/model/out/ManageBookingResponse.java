package uk.co.whitbread.reservation.domain.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManageBookingResponse {

  private Boolean isCancellable;
  private Boolean isAmendable;
  private Boolean isRuleCompliant;
  private String aemLabelKey;
  private boolean isCheckInOnlineAvailable;
  private boolean isCheckOutOnlineAvailable;
  private boolean isDigitalKey;
  private String ciolErrorLabelKey;
}
