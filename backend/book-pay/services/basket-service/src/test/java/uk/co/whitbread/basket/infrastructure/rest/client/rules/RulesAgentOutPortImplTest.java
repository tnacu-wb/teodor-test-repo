package uk.co.whitbread.basket.infrastructure.rest.client.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceRule;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceRuleResponse;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceSourceType;
import uk.co.whitbread.basket.domain.model.rules.out.TransactionCode;
import uk.co.whitbread.basket.domain.model.rules.out.VatRuleResponse;
import uk.co.whitbread.basket.generated.models.rules.BusinessAllowanceRuleResponseDto;
import uk.co.whitbread.basket.generated.models.rules.VatRuleResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.client.rules.mapper.BusinessAllowanceRuleResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.rules.mapper.VatRuleResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.rules.service.RulesAgentClient;

@ExtendWith(MockitoExtension.class)
class RulesAgentOutPortImplTest {

  private RulesAgentOutPortImpl rulesAgentOutPort;

  @Mock
  private RulesAgentClient rulesAgentClient;

  @Mock
  private BusinessAllowanceRuleResponseMapper businessAllowanceRuleResponseMapper;

  @Mock
  private VatRuleResponseMapper vatRuleResponseMapper;

  @BeforeEach
  public void before() {
    rulesAgentOutPort = new RulesAgentOutPortImpl(rulesAgentClient,
        businessAllowanceRuleResponseMapper, vatRuleResponseMapper);
  }

  @Test
  void testGetBusinessAllowanceRules_success() {
    // Arrange
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(new BusinessAllowanceRuleResponseDto());

    when(businessAllowanceRuleResponseMapper.toModel(any(BusinessAllowanceRuleResponseDto.class)))
        .thenReturn(mockBusinessAllowanceRuleResponse());

    // Act
    var businessAllowances = rulesAgentOutPort.getBusinessAllowanceRules();

    // Assert
    verify(rulesAgentClient).getBusinessAllowances();
    verify(businessAllowanceRuleResponseMapper).toModel(any(BusinessAllowanceRuleResponseDto.class));

    assertNotNull(businessAllowances);
    assertEquals(1, businessAllowances.getBusinessAllowances().size());
    assertEquals("BFADBF", businessAllowances.getBusinessAllowances().get(0).getSourceId());
    assertEquals("11", businessAllowances.getBusinessAllowances().get(0).getTargetId());
  }

  @Test
  void getVatCodes_returnsMappedVatRules_whenClientReturnsResponse() {
    var vatRegion = "GB";
    var packageCodes = List.of("CITYTAX", "CITYEXP");
    var vatRuleResponseDto = new VatRuleResponseDto();
    var mappedVatRuleResponse = mockVatRuleResponse();

    when(rulesAgentClient.getVatCodes(vatRegion, packageCodes)).thenReturn(vatRuleResponseDto);
    when(vatRuleResponseMapper.toModel(vatRuleResponseDto)).thenReturn(mappedVatRuleResponse);

    var response = rulesAgentOutPort.getVatCodes(vatRegion, packageCodes);

    verify(rulesAgentClient).getVatCodes(vatRegion, packageCodes);
    verify(vatRuleResponseMapper).toModel(vatRuleResponseDto);
    assertNotNull(response);
    assertEquals("GB", response.getVatRegion());
    assertEquals(1, response.getTranCodes().size());
    assertEquals("CITYTAX", response.getTranCodes().get(0).getPkgCode());
    assertEquals("TX01", response.getTranCodes().get(0).getTranCode());
  }

  @Test
  void getVatCodes_returnsMappedResult_whenPackageCodesListIsEmpty() {
    var vatRegion = "GB";
    var emptyPackageCodes = Collections.<String>emptyList();
    var vatRuleResponseDto = new VatRuleResponseDto();

    when(rulesAgentClient.getVatCodes(vatRegion, emptyPackageCodes)).thenReturn(vatRuleResponseDto);
    when(vatRuleResponseMapper.toModel(vatRuleResponseDto)).thenReturn(VatRuleResponse.builder()
        .vatRegion(vatRegion)
        .tranCodes(Collections.emptyList())
        .build());

    var response = rulesAgentOutPort.getVatCodes(vatRegion, emptyPackageCodes);

    verify(rulesAgentClient).getVatCodes(vatRegion, emptyPackageCodes);
    verify(vatRuleResponseMapper).toModel(vatRuleResponseDto);
    assertNotNull(response);
    assertNotNull(response.getTranCodes());
    assertEquals(0, response.getTranCodes().size());
  }

  @Test
  void getVatCodes_returnsNull_whenMapperReturnsNull() {
    var vatRegion = "GB";
    var packageCodes = List.of("CITYTAX");
    var vatRuleResponseDto = new VatRuleResponseDto();

    when(rulesAgentClient.getVatCodes(vatRegion, packageCodes)).thenReturn(vatRuleResponseDto);
    when(vatRuleResponseMapper.toModel(vatRuleResponseDto)).thenReturn(null);

    var response = rulesAgentOutPort.getVatCodes(vatRegion, packageCodes);

    verify(rulesAgentClient).getVatCodes(vatRegion, packageCodes);
    verify(vatRuleResponseMapper).toModel(vatRuleResponseDto);
    assertNull(response);
  }

  @Test
  void getVatCodes_propagatesException_whenClientFails() {
    var vatRegion = "GB";
    var packageCodes = List.of("CITYTAX");

    when(rulesAgentClient.getVatCodes(vatRegion, packageCodes))
        .thenThrow(new RuntimeException("rules-agent unavailable"));

    assertThrows(RuntimeException.class, () -> rulesAgentOutPort.getVatCodes(vatRegion, packageCodes));
  }

  private BusinessAllowanceRuleResponse mockBusinessAllowanceRuleResponse() {

    var businessAllowanceRule = BusinessAllowanceRule.builder()
        .pms("OP")
        .sourceId("BFADBF")
        .sourceType(BusinessAllowanceSourceType.PACKAGE.name())
        .targetId("11")
        .isTransactionCode(true)
        .build();

    return BusinessAllowanceRuleResponse.builder()
        .businessAllowances(Collections.singletonList(businessAllowanceRule))
        .build();
  }

  private VatRuleResponse mockVatRuleResponse() {
    var transactionCode = TransactionCode.builder()
        .pkgCode("CITYTAX")
        .tranCode("TX01")
        .vatBearing(Boolean.TRUE)
        .build();

    return VatRuleResponse.builder()
        .vatRegion("GB")
        .tranCodes(List.of(transactionCode))
        .build();
  }
}
