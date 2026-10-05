package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.AmendmentRule;
import uk.co.whitbread.rules.manager.domain.model.in.CountryCode;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.AmendmentRuleEntity;

@ExtendWith(MockitoExtension.class)
class AmendmentRuleMapperTest {

  @InjectMocks
  private AmendmentRuleMapperImpl amendmentRuleMapper;

  @Spy
  private AmendmentRuleTransformer amendmentRuleTransformer;

  @Test
  void toDomain_givenEntityObject_shouldTransformToDomainObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var amendmentRuleDomain = AmendmentRule.builder()
        .ruleId(123)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .rateType("Flex")
        .countryCode(CountryCode.GB)
        .arrivalDateLimit(2)
        .build();
    var amendmentRuleEntity = new AmendmentRuleEntity();
    amendmentRuleEntity.setRuleId(123);
    amendmentRuleEntity.setRefRuleId(0);
    amendmentRuleEntity.setStatus("new");
    amendmentRuleEntity.setCreatedAt(now);
    amendmentRuleEntity.setLastModifiedAt(now);
    amendmentRuleEntity.setEnableTimestamp(null);
    amendmentRuleEntity.setDisableTimestamp(now.plusDays(30));
    amendmentRuleEntity.setRateType("Flex");
    amendmentRuleEntity.setCountryCode("GB");
    amendmentRuleEntity.setArrivalDateLimit(2);

    //Act
    var result = amendmentRuleMapper.toDomainModel(amendmentRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(amendmentRuleDomain);
  }

  @Test
  void toDomain_givenNull_shouldReturnNull() {
    //Arrange

    //Act
    var result = amendmentRuleMapper.toDomainModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenDomainObject_shouldTransformToEntityObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var amendmentRuleDomain = AmendmentRule.builder()
        .ruleId(123)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .rateType("Flex")
        .arrivalDateLimit(2)
        .build();
    var amendmentRuleEntity = new AmendmentRuleEntity();
    amendmentRuleEntity.setRuleId(123);
    amendmentRuleEntity.setRefRuleId(0);
    amendmentRuleEntity.setStatus("NEW");
    amendmentRuleEntity.setCreatedAt(now);
    amendmentRuleEntity.setLastModifiedAt(now);
    amendmentRuleEntity.setEnableTimestamp(null);
    amendmentRuleEntity.setDisableTimestamp(now.plusDays(30));
    amendmentRuleEntity.setRateType("Flex");
    amendmentRuleEntity.setArrivalDateLimit(2);

    //Act
    var result = amendmentRuleMapper.toEntityDto(amendmentRuleDomain);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(amendmentRuleEntity);
  }

  @Test
  void toEntityDto_givenNull_shouldReturnNull() {
    //Arrange

    //Act
    var result = amendmentRuleMapper.toEntityDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenNullStatus_shouldTransformToEntityObjectWithNullStatus() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var amendmentRuleDomain = AmendmentRule.builder()
        .ruleId(123)
        .refRuleId(0)
        .status(null)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .rateType("Flex")
        .arrivalDateLimit(2)
        .build();
    var amendmentRuleEntity = new AmendmentRuleEntity();
    amendmentRuleEntity.setRuleId(123);
    amendmentRuleEntity.setRefRuleId(0);
    amendmentRuleEntity.setStatus(null);
    amendmentRuleEntity.setCreatedAt(now);
    amendmentRuleEntity.setLastModifiedAt(now);
    amendmentRuleEntity.setEnableTimestamp(null);
    amendmentRuleEntity.setDisableTimestamp(now.plusDays(30));
    amendmentRuleEntity.setRateType("Flex");
    amendmentRuleEntity.setArrivalDateLimit(2);

    //Act
    var result = amendmentRuleMapper.toEntityDto(amendmentRuleDomain);

    //Assert
    assertThat(result.getStatus()).isNull();
  }

  @Test
  void toEntityDto_givenNullRateType_shouldTransformToEntityObjectWithNullRateType() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var amendmentRuleDomain = AmendmentRule.builder()
        .ruleId(123)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .rateType(null)
        .arrivalDateLimit(2)
        .build();
    var amendmentRuleEntity = new AmendmentRuleEntity();
    amendmentRuleEntity.setRuleId(123);
    amendmentRuleEntity.setRefRuleId(0);
    amendmentRuleEntity.setStatus("NEW");
    amendmentRuleEntity.setCreatedAt(now);
    amendmentRuleEntity.setLastModifiedAt(now);
    amendmentRuleEntity.setEnableTimestamp(null);
    amendmentRuleEntity.setDisableTimestamp(now.plusDays(30));
    amendmentRuleEntity.setRateType(null);
    amendmentRuleEntity.setArrivalDateLimit(2);

    //Act
    var result = amendmentRuleMapper.toEntityDto(amendmentRuleDomain);

    //Assert
    assertThat(result.getRateType()).isNull();
  }
}