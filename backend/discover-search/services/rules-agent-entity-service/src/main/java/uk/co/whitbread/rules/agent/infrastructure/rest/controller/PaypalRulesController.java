package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.rules.agent.domain.ports.primary.PaypalRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.documentation.PaypalRulesApiDocumentation;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.PaypalRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.PaypalRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.PaypalRuleResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/rules")
public class PaypalRulesController implements PaypalRulesApiDocumentation {

  private final PaypalRuleDtoMapper paypalRuleDtoMapper;
  private final PaypalRuleInPort paypalRuleInPort;


  @GetMapping(value = "/paypal", produces = MediaType.APPLICATION_JSON_VALUE)
  public PaypalRuleResponseDto getPaypalRule(
      @Valid @ParameterObject PaypalRuleRequestDto paypalRuleRequestDto) {

    var domainPaypalRuleResponse =  paypalRuleInPort.getPaypalHasAccess(paypalRuleRequestDto.getChannelId(),
        paypalRuleRequestDto.getCountry(), paypalRuleRequestDto.getHotelId());
    return paypalRuleDtoMapper.toDto(domainPaypalRuleResponse);

  }

}
