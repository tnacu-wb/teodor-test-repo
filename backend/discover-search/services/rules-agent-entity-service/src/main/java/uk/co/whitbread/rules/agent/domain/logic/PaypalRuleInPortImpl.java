package uk.co.whitbread.rules.agent.domain.logic;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.agent.domain.model.out.PaypalRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.PaypalRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.PaypalRuleRepositoryOutPort;

@Slf4j
@RequiredArgsConstructor
public class PaypalRuleInPortImpl implements PaypalRuleInPort {

  private final PaypalRuleRepositoryOutPort paypalRuleRepositoryOutPort;

  @Override
  public PaypalRuleResponse getPaypalHasAccess(String channelId, String country, String hotelId) {
    var hasAccess = paypalRuleRepositoryOutPort.getPaypalHasAccess(channelId, country, hotelId);
    return PaypalRuleResponse.builder()
        .isPayPalPaymentEnabled(hasAccess)
        .generatedAt(LocalDateTime.now())
        .build();
  }


}
