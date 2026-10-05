package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancellationInfoResponseDto {

  private Boolean amendable;
  private Boolean cancelable;
  private Boolean ruleCompliant;
  private String aemLabelKey;
}
