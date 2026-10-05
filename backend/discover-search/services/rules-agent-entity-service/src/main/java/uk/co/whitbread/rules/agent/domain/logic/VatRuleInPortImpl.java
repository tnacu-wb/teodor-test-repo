package uk.co.whitbread.rules.agent.domain.logic;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import uk.co.whitbread.rules.agent.domain.model.in.VatRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.TransactionCode;
import uk.co.whitbread.rules.agent.domain.model.out.VatRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.VatRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.VatRuleRepositoryOutPort;

@RequiredArgsConstructor
public class VatRuleInPortImpl implements VatRuleInPort {

  private final VatRuleRepositoryOutPort vatRuleRepositoryOutPort;
  private final List<String> specialPackages = List.of("CITYTAX");

  @Override
  public VatRuleResponse getVatRule(VatRuleRequest vatRuleRequest) {
    return VatRuleResponse.builder()
        .vatRegion(vatRuleRequest.getVatRegion())
        .tranCodes(obtainListOfTransactionCodes(vatRuleRequest))
        .generatedAt(LocalDateTime.now())
        .build();
  }

  private List<TransactionCode> obtainListOfTransactionCodes(
      VatRuleRequest vatRuleRequest) {
    return vatRuleRepositoryOutPort.findVatRules(
            vatRuleRequest)
        .stream()
        .map(transactionCode -> TransactionCode.builder()
            .pkgCode(transactionCode.getPkgCode())
            .tranCode(transactionCode.getTranCode())
            .vatBearing(specialPackages.contains(transactionCode.getPkgCode())
                ? transactionCode.getVatBearing() : null)
            .build())
        .toList();
  }
}
