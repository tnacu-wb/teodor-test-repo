package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.rules.agent.domain.ports.primary.VatRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.documentation.VatRuleApiDocumentation;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.VatRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.VatRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.VatRuleResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/rules")
public class VatRuleController implements VatRuleApiDocumentation {

  private final VatRuleInPort vatRuleInPort;
  private final VatRuleDtoMapper vatRuleDtoMapper;

  @GetMapping(value = "/vat-codes", produces = MediaType.APPLICATION_JSON_VALUE)
  public VatRuleResponseDto getVatRule(VatRuleRequestDto vatRuleRequestDto) {
    log.debug("Request to get Vat Rule for: {}", vatRuleRequestDto);
    var domainVatRuleRequest = vatRuleDtoMapper.toModel(
        vatRuleRequestDto);
    var domainVatRuleResponse = vatRuleInPort.getVatRule(
        domainVatRuleRequest);

    return vatRuleDtoMapper.toDto(domainVatRuleResponse);
  }
}
