package uk.co.whitbread.booking.domain.model.information.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancellationInfoResponse {

  private Boolean cancelable;
  private Boolean amendable;
  private Boolean ruleCompliant;
  private String aemLabelKey;
}
