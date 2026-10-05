package uk.co.whitbread.rules.agent.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.exception.ErrorCode;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.in.VatRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.VatRule;
import uk.co.whitbread.rules.agent.domain.ports.secondary.VatRuleRepositoryOutPort;

@ExtendWith(MockitoExtension.class)
class VatRuleInPortImplTest {

  private static final String VAT_REGION = "UK";
  private static final String PKG_CODE = "MDBEVA";
  private static final String TRAN_CODE = "9028";

  @Mock
  private VatRuleRepositoryOutPort vatRuleRepositoryOutPort;
  @InjectMocks
  private VatRuleInPortImpl vatRuleInPort;

  @Test
  void getVatRule__shouldThrowException() {
    //Arrange
    var request = createVatRuleRequest();
    final String expectedMessage = "VAT Rule not found.";
    when(vatRuleRepositoryOutPort.findVatRules(request)).thenThrow(
        new RuleEngineException(ErrorCode.DIGITAL_VAT_RULE_EXCEPTION,
              "VAT Rule not found."));

    //Act
    Exception exception = assertThrows(RuleEngineException.class,
        () -> vatRuleInPort.getVatRule(request));

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(expectedMessage));
    verifyNoMoreInteractions(vatRuleRepositoryOutPort);
  }

  @Test
  void getVatRule__shouldReturnOk() {
    //Arrange
    var vatRuleRequest = createVatRuleRequest();
    when(vatRuleRepositoryOutPort.findVatRules(vatRuleRequest)).thenReturn(creatVatRule());

    //Act
    var response = vatRuleInPort.getVatRule(vatRuleRequest);

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getVatRegion(), is("UK"));
    assertThat(response.getTranCodes().get(0).getPkgCode(), is("MDBEVA"));
    assertThat(response.getTranCodes().get(0).getTranCode(), is("9028"));
    verifyNoMoreInteractions(vatRuleRepositoryOutPort);
  }

  private List<VatRule> creatVatRule() {
    var time = LocalDateTime.now();
    return List.of(VatRule.builder()
        .vatRegion(VAT_REGION)
        .pkgCode(PKG_CODE)
        .tranCode(TRAN_CODE)
        .createdAt(time)
        .lastModifiedAt(time)
        .build());
  }

  private VatRuleRequest createVatRuleRequest() {
    return VatRuleRequest.builder()
        .vatRegion(VAT_REGION)
        .pkgCodeArr(List.of(PKG_CODE))
        .build();
  }

}
