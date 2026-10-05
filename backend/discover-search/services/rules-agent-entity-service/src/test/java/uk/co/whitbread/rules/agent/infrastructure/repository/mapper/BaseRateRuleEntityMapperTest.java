package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.rules.agent.domain.model.out.BaseRateRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.BaseRateRuleEntity;

public class BaseRateRuleEntityMapperTest {

  private final BaseRateRuleEntityMapper baseRateRuleEntityMapper = new BaseRateRuleEntityMapperImpl();

  @Test
  void toModel_givenEntityObject_shouldTransformToModelObject() {
    //Arrange
    var time = LocalDateTime.now();
    var baseRateRuleEntity = new BaseRateRuleEntity();
    baseRateRuleEntity.setRuleId(1);
    baseRateRuleEntity.setCreatedAt(time);
    baseRateRuleEntity.setLastModifiedAt(time);
    baseRateRuleEntity.setStatus("ACTIVE");
    baseRateRuleEntity.setBaseRate("FLEXRATE");
    baseRateRuleEntity.setRatePlanCode("BUSIFLEX");
    baseRateRuleEntity.setPromoCode("BUSIFLEX");
    var baseRateRule = BaseRateRule.builder()
        .ruleId(1)
        .createdAt(time)
        .lastModifiedAt(time)
        .status(RuleStatus.ACTIVE)
        .baseRate("FLEXRATE")
        .ratePlanCode("BUSIFLEX")
        .promoCode("BUSIFLEX")
        .build();

    //Act
    var result = baseRateRuleEntityMapper.toModel(baseRateRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(baseRateRule);
  }
}
