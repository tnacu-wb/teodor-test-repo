package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.exception.ErrorCode;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.out.BaseRateRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.BaseRateRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.BaseRateRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.BaseRateRuleResponseDto;

@ExtendWith(MockitoExtension.class)
public class BaseRateControllerIT {
  
  @InjectMocks
  private BaseRateController baseRateController;
  @Mock
  private BaseRateRuleInPort baseRateRuleInPort;
  @Mock
  private BaseRateRuleDtoMapper baseRateRuleDtoMapper;
  
  @Test
  void shouldRetrieveBaseRAteRule() {
    String ratePlanCode = "BUSIFLEX";
    when(baseRateRuleInPort.getBaseRate(anyString())).thenReturn(mockBaseRateRuleResponse());
    when(baseRateRuleDtoMapper.toDto(any())).thenReturn(mockBaseRateRuleResponseDto());
    
    var response = baseRateController.getBaseRateRule(ratePlanCode);
    
    Assertions.assertEquals("FLEXRATE", response.getBaseRate());
  }
  
  @Test
  void shouldHandleRuleNotFound() {
    String ratePlanCode = "BUSIFLEX";
    when(baseRateRuleInPort.getBaseRate(anyString())).thenThrow(
        new RuleEngineException(ErrorCode.DIGITAL_NO_BASE_RATE_EXCEPTION, "Error while trying to get base rate rule"));
    
    assertThrows(RuleEngineException.class, () -> baseRateController.getBaseRateRule(ratePlanCode));
  }
  
  private BaseRateRuleResponse mockBaseRateRuleResponse() {
    return BaseRateRuleResponse.builder()
        .baseRate("FLEXRATE")
        .promoCode("BUSIFLEX")
        .ratePlanCode("BUSIFLEX")
        .build();
  }
  
  private BaseRateRuleResponseDto mockBaseRateRuleResponseDto() {
    return BaseRateRuleResponseDto.builder()
        .baseRate("FLEXRATE")
        .promoCode("BUSIFLEX")
        .ratePlanCode("BUSIFLEX")
        .build();
  }
  
}
