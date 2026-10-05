package uk.co.whitbread.rules.agent.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.model.out.BusinessAllowanceRule;
import uk.co.whitbread.rules.agent.domain.ports.secondary.BusinessAllowanceRuleRepositoryOutPort;

@ExtendWith(MockitoExtension.class)
class BusinessAllowanceRuleInPortImplTest {

  private static final String OP = "OP";
  private static final String SOURCE_ID = "dinner";
  private static final String SOURCE_TYPE = "ALLOWANCE";
  private static final String TARGET_ID = "156";
  private static final Boolean IS_TRANSACTION_CODE = true;
  private static final String AEM_ID = "boxedBreakfast";
  @Mock
  private BusinessAllowanceRuleRepositoryOutPort businessAllowanceOutPort;

  @InjectMocks
  private BusinessAllowanceRuleInPortImpl businessAllowanceRuleInPortImpl;

  @Test
  void getBusinessAllowances__shouldReturnOk() {
    // Arrange
    when(businessAllowanceOutPort.findRules()).thenReturn(createBusinessAllowances());

    // Act
    var response = businessAllowanceRuleInPortImpl.getBusinessAllowanceRules();

    // Assert
    assertThat(response, notNullValue());
    assertThat(response.getBusinessAllowances().get(0).getPms(), is("OP"));
    assertThat(response.getBusinessAllowances().get(0).getSourceId(), is("dinner"));
    assertThat(response.getBusinessAllowances().get(0).getSourceType(), is("ALLOWANCE"));
    assertThat(response.getBusinessAllowances().get(0).getTargetId(), is("156"));
    assertThat(response.getBusinessAllowances().get(0).getIsTransactionCode(), is(true));
    assertThat(response.getBusinessAllowances().get(0).getAemId(), is(AEM_ID));
    verifyNoMoreInteractions(businessAllowanceOutPort);
  }


  private List<BusinessAllowanceRule> createBusinessAllowances() {
    return List.of(
        BusinessAllowanceRule.builder()
            .pms(OP)
            .sourceId(SOURCE_ID)
            .sourceType(SOURCE_TYPE)
            .targetId(TARGET_ID)
            .isTransactionCode(IS_TRANSACTION_CODE)
            .aemId(AEM_ID)
            .build());
  }
}
