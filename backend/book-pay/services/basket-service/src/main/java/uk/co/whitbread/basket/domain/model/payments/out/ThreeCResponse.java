package uk.co.whitbread.basket.domain.model.payments.out;

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
  private String providerReason;
  private String providerResult;
  private String providerUrl;
  private String fraudCheckResultReason;
  private String cardSchemeId;
  private String fraudCheckDecision;
  private String fraudCheckResult;
  private String token;
  private String expiry;
  private String last4Digits;
  private String threeDSIndicator;
}
