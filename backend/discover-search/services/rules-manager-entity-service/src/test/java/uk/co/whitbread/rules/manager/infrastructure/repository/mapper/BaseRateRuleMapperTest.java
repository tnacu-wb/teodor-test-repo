package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.BaseRateRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.BaseRateRuleEntity;

@ExtendWith(MockitoExtension.class)
class BaseRateRuleMapperTest {
  
  @InjectMocks
  private BaseRateRuleMapperImpl baseRateRuleMapper;
  
  @Spy
  private BaseRateRuleTransformer baseRateRuleTransformer;
  
  @Test
  void toDomain_givenEntityObject_shouldTransformToDomainObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var baseRateRuleDomain = BaseRateRule.builder()
        .ruleId(123)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .ratePlanCode("BUSIFLEX")
        .promoCode("BUSIFLEX")
        .baseRate("FLEXRATE")
        .build();
    var baseRateRuleEntity = new BaseRateRuleEntity();
    baseRateRuleEntity.setRuleId(123);
    baseRateRuleEntity.setRefRuleId(0);
    baseRateRuleEntity.setStatus("new");
    baseRateRuleEntity.setCreatedAt(now);
    baseRateRuleEntity.setLastModifiedAt(now);
    baseRateRuleEntity.setEnableTimestamp(null);
    baseRateRuleEntity.setDisableTimestamp(now.plusDays(30));
    baseRateRuleEntity.setBaseRate("FLEXRATE");
    baseRateRuleEntity.setRatePlanCode("BUSIFLEX");
    baseRateRuleEntity.setPromoCode("BUSIFLEX");
    
    //Act
    var result = baseRateRuleMapper.toDomainModel(baseRateRuleEntity);
    
    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(baseRateRuleDomain);
  }
  
  @Test
  void toDomain_givenNull_shouldReturnNull() {
    //Act
    var result = baseRateRuleMapper.toDomainModel(null);
    
    //Assert
    assertThat(result).isNull();
  }
  
  @Test
  void toEntityDto_givenDomainObject_shouldTransformToEntityObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var baseRateRuleDomain = BaseRateRule.builder()
        .ruleId(123)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .ratePlanCode("BUSIFLEX")
        .promoCode("BUSIFLEX")
        .baseRate("FLEXRATE")
        .build();
    var baseRateRuleEntity = new BaseRateRuleEntity();
    baseRateRuleEntity.setRuleId(123);
    baseRateRuleEntity.setRefRuleId(0);
    baseRateRuleEntity.setStatus("NEW");
    baseRateRuleEntity.setCreatedAt(now);
    baseRateRuleEntity.setLastModifiedAt(now);
    baseRateRuleEntity.setEnableTimestamp(null);
    baseRateRuleEntity.setDisableTimestamp(now.plusDays(30));
    baseRateRuleEntity.setBaseRate("FLEXRATE");
    baseRateRuleEntity.setRatePlanCode("BUSIFLEX");
    baseRateRuleEntity.setPromoCode("BUSIFLEX");
    
    //Act
    var result = baseRateRuleMapper.toEntityDto(baseRateRuleDomain);
    
    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(baseRateRuleEntity);
  }
  
  @Test
  void toEntityDto_givenNull_shouldReturnNull() {
    //Arrange
    
    //Act
    var result = baseRateRuleMapper.toEntityDto(null);
    
    //Assert
    assertThat(result).isNull();
  }
  
  @Test
  void toEntityDto_givenNullStatus_shouldTransformToEntityObjectWithNullStatus() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var baseRateRuleDomain = BaseRateRule.builder()
        .ruleId(123)
        .refRuleId(0)
        .status(null)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .ratePlanCode("BUSIFLEX")
        .promoCode("BUSIFLEX")
        .baseRate("FLEXRATE")
        .build();
    var baseRateRuleEntity = new BaseRateRuleEntity();
    baseRateRuleEntity.setRuleId(123);
    baseRateRuleEntity.setRefRuleId(0);
    baseRateRuleEntity.setStatus(null);
    baseRateRuleEntity.setCreatedAt(now);
    baseRateRuleEntity.setLastModifiedAt(now);
    baseRateRuleEntity.setEnableTimestamp(null);
    baseRateRuleEntity.setDisableTimestamp(now.plusDays(30));
    baseRateRuleEntity.setBaseRate("FLEXRATE");
    baseRateRuleEntity.setRatePlanCode("BUSIFLEX");
    baseRateRuleEntity.setPromoCode("BUSIFLEX");
    
    //Act
    var result = baseRateRuleMapper.toEntityDto(baseRateRuleDomain);
    
    //Assert
    assertThat(result.getStatus()).isNull();
  }
}
