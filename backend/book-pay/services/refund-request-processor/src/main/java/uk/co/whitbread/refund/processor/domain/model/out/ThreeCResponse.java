package uk.co.whitbread.refund.processor.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreeCResponse {

  private String iPageHtml;
  private String sessionId;
  private String template;
  private String providerResult;
  private String fraudCheckResultReason;
  private String cardSchemeId;
  private String fraudCheckDecision;
  private String fraudCheckResult;
  private String token;
  private String expiry;
  private String last4Digits;
}
