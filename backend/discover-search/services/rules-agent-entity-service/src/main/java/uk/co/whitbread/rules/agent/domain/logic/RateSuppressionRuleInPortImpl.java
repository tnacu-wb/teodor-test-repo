package uk.co.whitbread.rules.agent.domain.logic;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import uk.co.whitbread.rules.agent.domain.model.out.RateSuppressionRule;
import uk.co.whitbread.rules.agent.domain.model.out.RateSuppressionRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.RateSuppressionRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RateSuppressionRuleRepositoryOutPort;

@RequiredArgsConstructor
public class RateSuppressionRuleInPortImpl implements RateSuppressionRuleInPort {

  private final RateSuppressionRuleRepositoryOutPort rateSuppressionRuleRepositoryOutPort;

  @Override
  public RateSuppressionRuleResponse getRateSuppressionRule() {
    return RateSuppressionRuleResponse.builder()
        .generatedAt(LocalDateTime.now())
        .rateSuppressionList(buildRateSuppressionList())
        .expiryDate(LocalDateTime.now().plusDays(1))
        .build();
  }

  private List<String> buildRateSuppressionList() {
    return rateSuppressionRuleRepositoryOutPort.findRateSuppressionRule()
        .stream()
        .sorted(Comparator.comparing(RateSuppressionRule::getPriority))
        .map(RateSuppressionRule::getRateType)
        .toList();
  }
}
