package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.rules.agent.domain.model.out.RateSuppressionRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.RateSuppressionRuleResponseDto;

public class RateSuppressionRuleDtoMapperTest {

  private final RateSuppressionRuleDtoMapper rateSuppressionRuleDtoMapper = new RateSuppressionRuleDtoMapperImpl();

  @Test
  void toDto_givenDomainObject_shouldTransformToDtoObject() {
    //Arrange
    var time = LocalDateTime.now();
    var rateSuppressionRuleResponse = RateSuppressionRuleResponse.builder()
        .rateSuppressionList(List.of("FLEX", "SEMI-FLEX", "ADVANCE", "STANDARD", "NON-FLEX"))
        .generatedAt(time)
        .expiryDate(time.plusDays(1))
        .build();
    var rateSuppressionRuleResponseDto = RateSuppressionRuleResponseDto.builder()
        .rateSuppressionList(List.of("FLEX", "SEMI-FLEX", "ADVANCE", "STANDARD", "NON-FLEX"))
        .generatedAt(time)
        .expiryDate(time.plusDays(1))
        .build();

    //Act
    var result = rateSuppressionRuleDtoMapper.toDto(rateSuppressionRuleResponse);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(rateSuppressionRuleResponseDto);
  }

  @Test
  void toDto_givenNullDomainObject_shouldReturnNullObject() {
    //Arrange

    //Act
    var result = rateSuppressionRuleDtoMapper.toDto(null);

    //Assert
    assertThat(result).isNull();
  }

}
