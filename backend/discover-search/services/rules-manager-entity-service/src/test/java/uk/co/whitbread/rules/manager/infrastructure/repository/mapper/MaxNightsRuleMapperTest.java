package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.MaxNightsRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxNightsRuleEntity;

@ExtendWith(MockitoExtension.class)
class MaxNightsRuleMapperTest {

  @InjectMocks
  private MaxNightsRuleMapperImpl maxNightsRuleMapper;
  @Spy
  private MaxNightsRuleTransformer maxRoomsRuleTransformer;

  @Test
  void toDomain_givenEntityObject_shouldTransformToDomainObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var maxNightsRuleDomain = MaxNightsRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channelId("some channel")
        .maxNights(9)
        .build();
    var maxNightsRuleEntity = new MaxNightsRuleEntity();
    maxNightsRuleEntity.setRuleId(1234);
    maxNightsRuleEntity.setRefRuleId(0);
    maxNightsRuleEntity.setStatus("NEW");
    maxNightsRuleEntity.setCreatedAt(now);
    maxNightsRuleEntity.setLastModifiedAt(now);
    maxNightsRuleEntity.setEnableTimestamp(null);
    maxNightsRuleEntity.setDisableTimestamp(now.plusDays(30));
    maxNightsRuleEntity.setChannelId("some channel");
    maxNightsRuleEntity.setMaxNights(9);

    //Act
    var result = maxNightsRuleMapper.toDomainModel(maxNightsRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxNightsRuleDomain);
  }

  @Test
  void toDomain_givenNull_shouldReturnNull() {
    //Arrange

    //Act
    var result = maxNightsRuleMapper.toDomainModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenDomainObject_shouldTransformToEntityObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var maxNightsRuleDomain = MaxNightsRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channelId("some channel")
        .maxNights(9)
        .build();
    var maxNightsRuleEntity = new MaxNightsRuleEntity();
    maxNightsRuleEntity.setRuleId(1234);
    maxNightsRuleEntity.setRefRuleId(0);
    maxNightsRuleEntity.setStatus("NEW");
    maxNightsRuleEntity.setCreatedAt(now);
    maxNightsRuleEntity.setLastModifiedAt(now);
    maxNightsRuleEntity.setEnableTimestamp(null);
    maxNightsRuleEntity.setDisableTimestamp(now.plusDays(30));
    maxNightsRuleEntity.setChannelId("some channel");
    maxNightsRuleEntity.setMaxNights(9);

    //Act
    var result = maxNightsRuleMapper.toEntityDto(maxNightsRuleDomain);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxNightsRuleEntity);
  }

  @Test
  void toEntityDto_givenNull_shouldReturnNull() {
    //Arrange

    //Act
    var result = maxNightsRuleMapper.toEntityDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenNullStatus_shouldTransformToEntityObjectWithNullStatus() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var maxNightsRuleDomain = MaxNightsRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(null)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channelId("some channel")
        .maxNights(9)
        .build();
    var maxNightsRuleEntity = new MaxNightsRuleEntity();
    maxNightsRuleEntity.setRuleId(1234);
    maxNightsRuleEntity.setRefRuleId(0);
    maxNightsRuleEntity.setStatus(null);
    maxNightsRuleEntity.setCreatedAt(now);
    maxNightsRuleEntity.setLastModifiedAt(now);
    maxNightsRuleEntity.setEnableTimestamp(null);
    maxNightsRuleEntity.setDisableTimestamp(now.plusDays(30));
    maxNightsRuleEntity.setChannelId("some channel");
    maxNightsRuleEntity.setMaxNights(9);

    //Act
    var result = maxNightsRuleMapper.toEntityDto(maxNightsRuleDomain);

    //Assert
    assertThat(result.getStatus()).isNull();
  }
}
