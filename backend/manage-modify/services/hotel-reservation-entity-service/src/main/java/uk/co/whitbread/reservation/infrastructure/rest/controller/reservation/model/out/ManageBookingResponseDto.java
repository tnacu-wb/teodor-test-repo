package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManageBookingResponseDto {

  private Boolean isCancellable;
  private Boolean isAmendable;
  private Boolean isRuleCompliant;
  private String aemLabelKey;
  @JsonProperty("isCheckInOnlineAvailable")
  private boolean isCheckInOnlineAvailable;
  @JsonProperty("isCheckOutOnlineAvailable")
  private boolean isCheckOutOnlineAvailable;
  @JsonProperty("isDigitalKey")
  private boolean isDigitalKey;
  private String ciolErrorLabelKey;
}