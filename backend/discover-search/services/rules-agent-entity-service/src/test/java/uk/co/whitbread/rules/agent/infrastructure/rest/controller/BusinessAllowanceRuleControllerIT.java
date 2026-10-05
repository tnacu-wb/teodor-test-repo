package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.model.out.BusinessAllowanceRule;
import uk.co.whitbread.rules.agent.domain.model.out.BusinessAllowanceRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.BusinessAllowanceRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.BusinessAllowanceRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.BusinessAllowanceRuleDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.BusinessAllowanceRuleResponseDto;

@ExtendWith(MockitoExtension.class)
class BusinessAllowanceRuleControllerIT {

  @InjectMocks
  private BusinessAllowanceRuleController businessAllowanceRuleController;

  @Mock
  private BusinessAllowanceRuleDtoMapper businessAllowanceRuleDtoMapper;

  @Mock
  private BusinessAllowanceRuleInPort businessAllowanceRuleInPort;

  @Test
  void shouldRetrieveBusinessAllowanceRules() {
    //Arrange
    var businessAllowancesRules = BusinessAllowanceRuleResponse.builder()
        .businessAllowances(List.of(
            BusinessAllowanceRule.builder().pms("OP").sourceId("dinner").sourceType("ALLOWANCE")
                .targetId("156").isTransactionCode(true).build())).build();

    var responseDto = BusinessAllowanceRuleResponseDto.builder().businessAllowances(List.of(
        BusinessAllowanceRuleDto.builder().pms("OP").sourceId("dinner").sourceType("ALLOWANCE")
            .targetId("156").isTransactionCode(true).build())).build();

    when(businessAllowanceRuleInPort.getBusinessAllowanceRules()).thenReturn(
        businessAllowancesRules);
    when(businessAllowanceRuleDtoMapper.toDto(any())).thenReturn(responseDto);

    //Act
    var response = businessAllowanceRuleController.getBusinessAllowanceRules();

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(1, response.getBusinessAllowances().size());
    Assertions.assertEquals("dinner", response.getBusinessAllowances().get(0).getSourceId());

  }
}
