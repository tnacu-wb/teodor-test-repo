package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.rules.agent.domain.model.out.PaypalRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.PaypalRuleResponseDto;

class PaypalRuleDtoMapperTest {

  private final PaypalRuleDtoMapper paypalRuleDtoMapper = new PaypalRuleDtoMapperImpl();


  @Test
  void toDto_givenDomainObject_shouldTransformToDtoObject() {
    //Arrange
    var time = LocalDateTime.now();
    var rbacRuleResponse = PaypalRuleResponse.builder()
        .isPayPalPaymentEnabled(true)
        .generatedAt(time)
        .build();
    var paypalRuleResponseDto = PaypalRuleResponseDto.builder()
        .isPayPalPaymentEnabled(true)
        .generatedAt(time)
        .build();

    //Act
    var result = paypalRuleDtoMapper.toDto(rbacRuleResponse);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(paypalRuleResponseDto);
  }

  @Test
  void toDto_givenNullDomainObject_shouldReturnNullObject() {
    //Arrange

    //Act
    var result = paypalRuleDtoMapper.toDto(null);

    //Assert
    assertThat(result).isNull();
  }
}
