package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.rules.agent.domain.model.out.BaseRateRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.BaseRateRuleResponseDto;

public class BaseRateRuleDtoMapperTest {
  
  private final BaseRateRuleDtoMapper baseRateRuleDtoMapper = new BaseRateRuleDtoMapperImpl();
  
  @Test
  void toDto_givenDomainObject_shouldTransformToDtoObject() {
    var baseRateRuleResponse = BaseRateRuleResponse.builder()
        .baseRate("FLEXRATE")
        .ratePlanCode("BUSIFLEX")
        .promoCode("BUSIFLEX")
        .build();
    var baseRateRuleResponseDto = BaseRateRuleResponseDto.builder()
        .baseRate("FLEXRATE")
        .promoCode("BUSIFLEX")
        .ratePlanCode("BUSIFLEX")
        .build();
    
    var result = baseRateRuleDtoMapper.toDto(baseRateRuleResponse);
    
    assertThat(result).usingRecursiveComparison().withStrictTypeChecking()
        .isEqualTo(baseRateRuleResponseDto);
  }
  
  @Test
  void toDto_givenNullDomainObject_shouldReturnNull() {
    //Arrange
    
    //Act
    var result = baseRateRuleDtoMapper.toDto(null);
    
    //Assert
    assertThat(result).isNull();
  }
}
