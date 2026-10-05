package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.BusinessAllowanceRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.BusinessAllowanceRuleEntity;

@ExtendWith(MockitoExtension.class)
class BusinessAllowanceRuleMapperTest {

  public static final LocalDateTime now = LocalDateTime.now();

  @InjectMocks
  private BusinessAllowanceRuleMapperImpl businessAllowanceRuleMapper;
  @Spy
  private BusinessAllowanceRuleTransformer businessAllowanceRuleTransformer;

  public static BusinessAllowanceRule createValidBusinessAllowanceRuleDomain() {
    return BusinessAllowanceRule.builder()
        .ruleId(1)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .pms("OP")
        .sourceId("dinner")
        .targetId("156")
        .build();
  }

  public static BusinessAllowanceRuleEntity createValidBusinessAllowanceRuleEntity() {
    var businessAllowanceRuleEntity = new BusinessAllowanceRuleEntity();
    businessAllowanceRuleEntity.setRuleId(1);
    businessAllowanceRuleEntity.setRefRuleId(0);
    businessAllowanceRuleEntity.setStatus("NEW");
    businessAllowanceRuleEntity.setCreatedAt(now);
    businessAllowanceRuleEntity.setLastModifiedAt(now);
    businessAllowanceRuleEntity.setEnableTimestamp(null);
    businessAllowanceRuleEntity.setDisableTimestamp(now.plusDays(30));
    businessAllowanceRuleEntity.setPms("OP");
    businessAllowanceRuleEntity.setSourceId("dinner");
    businessAllowanceRuleEntity.setTargetId("156");

    return businessAllowanceRuleEntity;
  }

  @Test
  void toDomain_givenEntityObject_shouldTransformToDomainObject() {
    //Arrange
    var businessAllowanceRuleDomain = createValidBusinessAllowanceRuleDomain();
    var businessAllowanceRuleEntity = createValidBusinessAllowanceRuleEntity();

    //Act
    var result = businessAllowanceRuleMapper.toDomainModel(businessAllowanceRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison().withStrictTypeChecking()
        .isEqualTo(businessAllowanceRuleDomain);
  }

  @Test
  void toDomain_givenNull_shouldReturnNull() {
    //Act
    var result = businessAllowanceRuleMapper.toDomainModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenDomainObject_shouldTransformToEntityObject() {
    //Arrange
    var businessAllowanceRuleDomain = createValidBusinessAllowanceRuleDomain();
    var businessAllowanceRuleEntity = createValidBusinessAllowanceRuleEntity();

    //Act
    var result = businessAllowanceRuleMapper.toEntityDto(businessAllowanceRuleDomain);

    //Assert
    assertThat(result).usingRecursiveComparison().withStrictTypeChecking()
        .isEqualTo(businessAllowanceRuleEntity);
  }

  @Test
  void toEntityDto_givenNull_shouldReturnNull() {
    //Act
    var result = businessAllowanceRuleMapper.toEntityDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenNullStatus_shouldTransformToEntityObjectWithNullStatus() {
    // Arrange
    var businessAllowanceRuleDomain = createValidBusinessAllowanceRuleDomain();
    businessAllowanceRuleDomain.setStatus(null);

    //Act
    var result = businessAllowanceRuleMapper.toEntityDto(businessAllowanceRuleDomain);

    //Assert
    assertThat(result.getStatus()).isNull();
  }

}
