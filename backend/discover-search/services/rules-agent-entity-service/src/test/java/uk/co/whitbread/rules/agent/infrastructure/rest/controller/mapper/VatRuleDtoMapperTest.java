package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.rules.agent.domain.model.in.VatRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.TransactionCode;
import uk.co.whitbread.rules.agent.domain.model.out.VatRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.VatRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.TransactionCodeDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.VatRuleResponseDto;

class VatRuleDtoMapperTest {

  private static final String VAT_REGION = "UK";
  private static final String PKG_CODE = "MDBEVA";
  private static final String TRAN_CODE = "9028";
  private final VatRuleDtoMapper vatRuleDtoMapper = new VatRuleDtoMapperImpl();

  @Test
  void toModel_givenDtoObject_shouldTransformToModelObject() {
    //Arrange
    var vatRuleRequestDto = VatRuleRequestDto.builder()
        .vatRegion(VAT_REGION)
        .pkgCodeArr(List.of(PKG_CODE))
        .build();
    var vatRuleRequest = VatRuleRequest.builder()
        .vatRegion(VAT_REGION)
        .pkgCodeArr(List.of(PKG_CODE))
        .build();

    //Act
    var result = vatRuleDtoMapper.toModel(vatRuleRequestDto);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(vatRuleRequest);
  }

  @Test
  void toModel_givenNullDtoObject_shouldReturnNullModelObject() {
    //Arrange

    //Act
    var result = vatRuleDtoMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toDto_givenDomainObject_shouldTransformToDtoObject() {
    //Arrange
    var time = LocalDateTime.now();
    var vatRuleResponse = VatRuleResponse.builder()
        .generatedAt(time)
        .vatRegion(VAT_REGION)
        .tranCodes(List.of(TransactionCode.builder()
            .tranCode(TRAN_CODE)
            .pkgCode(PKG_CODE)
            .build()))
        .build();
    var vatRuleResponseDto = VatRuleResponseDto.builder()
        .generatedAt(time)
        .vatRegion(VAT_REGION)
        .tranCodes(List.of(TransactionCodeDto.builder()
            .tranCode(TRAN_CODE)
            .pkgCode(PKG_CODE)
            .build()))
        .build();

    //Act
    var result = vatRuleDtoMapper.toDto(vatRuleResponse);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(vatRuleResponseDto);
  }

  @Test
  void toDto_givenNullDomainObject_shouldReturnNullObject() {
    //Arrange

    //Act
    var result = vatRuleDtoMapper.toDto(null);

    //Assert
    assertThat(result).isNull();
  }

}
