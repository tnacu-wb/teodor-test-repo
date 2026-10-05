package uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManageBookingResponseDto {

  private Boolean isCancellable;
  private Boolean isAmendable;
  private Boolean isRuleCompliant;
  private String aemLabelKey;
  private Boolean isCheckInOnlineAvailable;

}