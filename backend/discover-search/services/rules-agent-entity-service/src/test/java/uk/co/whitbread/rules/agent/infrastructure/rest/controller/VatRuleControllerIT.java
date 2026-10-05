package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.exception.ErrorCode;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.out.TransactionCode;
import uk.co.whitbread.rules.agent.domain.model.out.VatRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.VatRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.VatRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.VatRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.TransactionCodeDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.VatRuleResponseDto;

@ExtendWith(MockitoExtension.class)
class VatRuleControllerIT {

  private static final String VAT_REGION = "UK";
  private static final String PKG_CODE = "MDBEVA";
  private static final String TRAN_CODE = "9028";

  @InjectMocks
  private VatRuleController vatRuleController;

  @Mock
  private VatRuleInPort vatRuleInPort;

  @Mock
  private VatRuleDtoMapper vatRuleDtoMapper;

  @Test
  void shouldRetrieveVatRule() {
    //Arrange
    when(vatRuleInPort.getVatRule(any())).thenReturn(mockVatRuleResponse());
    when(vatRuleDtoMapper.toDto(any())).thenReturn(mockVatRuleResponseDto());

    //Act
    var response = vatRuleController.getVatRule(getVatRuleRequestDto());

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(1, response.getTranCodes().size());
    Assertions.assertEquals(PKG_CODE, response.getTranCodes().get(0).getPkgCode());
  }

  @Test
  void shouldHandleVatRuleNotFound() {

    when(vatRuleDtoMapper.toModel(any())).thenReturn(null);
    when(vatRuleInPort.getVatRule(any()))
        .thenThrow(new RuleEngineException(ErrorCode.DIGITAL_VAT_RULE_EXCEPTION,
              "VAT not found"));

    //Act
    var thrownException = assertThrowsExactly(RuleEngineException.class,
        () -> vatRuleController.getVatRule(getVatRuleRequestDto()));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("VAT not found", debugMessage);
  }

  private VatRuleResponse mockVatRuleResponse() {
    return VatRuleResponse.builder()
        .vatRegion(VAT_REGION)
        .tranCodes(List.of(TransactionCode.builder()
            .pkgCode(PKG_CODE)
            .tranCode(TRAN_CODE)
            .build()))
        .generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .build();
  }

  private VatRuleResponseDto mockVatRuleResponseDto() {
    return VatRuleResponseDto.builder()
        .vatRegion(VAT_REGION)
        .tranCodes(List.of(TransactionCodeDto.builder()
            .pkgCode(PKG_CODE)
            .tranCode(TRAN_CODE)
            .build()))
        .generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .build();
  }

  private VatRuleRequestDto getVatRuleRequestDto() {
    return VatRuleRequestDto.builder()
        .vatRegion(VAT_REGION)
        .pkgCodeArr(List.of(PKG_CODE))
        .build();
  }
}
