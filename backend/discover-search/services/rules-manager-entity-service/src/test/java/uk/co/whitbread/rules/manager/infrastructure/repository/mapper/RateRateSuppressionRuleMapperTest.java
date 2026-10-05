package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.model.in.RateSuppressionRule;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.RateSuppressionRuleEntity;

@ExtendWith(MockitoExtension.class)
class RateRateSuppressionRuleMapperTest {

  @InjectMocks
  private RateSuppressionRuleMapperImpl suppressionRuleMapper;
  @Spy
  private RateSuppressionRuleTransformer rateSuppressionRuleTransformer;

  @Test
  void toDomain_givenEntityObject_shouldTransformToDomainObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var suppressionRuleDomain = RateSuppressionRule.builder()
        .ruleId(1)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .rateType("STANDARD")
        .priority((short)30)
        .build();

    var suppressionRuleEntity = new RateSuppressionRuleEntity();
    suppressionRuleEntity.setRuleId(1);
    suppressionRuleEntity.setRefRuleId(0);
    suppressionRuleEntity.setStatus("NEW");
    suppressionRuleEntity.setCreatedAt(now);
    suppressionRuleEntity.setLastModifiedAt(now);
    suppressionRuleEntity.setEnableTimestamp(null);
    suppressionRuleEntity.setDisableTimestamp(now.plusDays(30));
    suppressionRuleEntity.setRateType("STANDARD");
    suppressionRuleEntity.setPriority((short)30);

    //Act
    var result = suppressionRuleMapper.toDomainModel(suppressionRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison().withStrictTypeChecking()
        .isEqualTo(suppressionRuleDomain);

  }

  @Test
  void toDomain_givenNull_shouldReturnNull() {
    //Act
    var result = suppressionRuleMapper.toDomainModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenDomainObject_shouldTransformToEntityObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var suppressionRuleDomain = RateSuppressionRule.builder()
        .ruleId(1)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .rateType("STANDARD")
        .priority((short)30)
        .build();

    var suppressionRuleEntity = new RateSuppressionRuleEntity();
    suppressionRuleEntity.setRuleId(1);
    suppressionRuleEntity.setRefRuleId(0);
    suppressionRuleEntity.setStatus("NEW");
    suppressionRuleEntity.setCreatedAt(now);
    suppressionRuleEntity.setLastModifiedAt(now);
    suppressionRuleEntity.setEnableTimestamp(null);
    suppressionRuleEntity.setDisableTimestamp(now.plusDays(30));
    suppressionRuleEntity.setRateType("STANDARD");
    suppressionRuleEntity.setPriority((short)30);

    //Act
    var result = suppressionRuleMapper.toEntityDto(suppressionRuleDomain);

    //Assert
    assertThat(result).usingRecursiveComparison().withStrictTypeChecking()
        .isEqualTo(suppressionRuleEntity);
  }

  @Test
  void toEntityDto_givenNull_shouldReturnNull() {
    //Act
    var result = suppressionRuleMapper.toEntityDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenNullStatus_shouldTransformToEntityObjectWithNullStatus() {
    LocalDateTime now = LocalDateTime.now();
    var suppressionRuleDomain = RateSuppressionRule.builder()
        .ruleId(1)
        .refRuleId(0)
        .status(null)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .rateType("STANDARD")
        .priority((short)30)
        .build();

    var suppressionRuleEntity = new RateSuppressionRuleEntity();
    suppressionRuleEntity.setRuleId(1);
    suppressionRuleEntity.setRefRuleId(0);
    suppressionRuleEntity.setStatus(null);
    suppressionRuleEntity.setCreatedAt(now);
    suppressionRuleEntity.setLastModifiedAt(now);
    suppressionRuleEntity.setEnableTimestamp(null);
    suppressionRuleEntity.setDisableTimestamp(now.plusDays(30));
    suppressionRuleEntity.setRateType("STANDARD");
    suppressionRuleEntity.setPriority((short)30);

    //Act
    var result = suppressionRuleMapper.toEntityDto(suppressionRuleDomain);

    //Assert
    assertThat(result.getStatus()).isNull();
  }


}
