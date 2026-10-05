package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.MaxArrivalDateRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxArrivalDateRuleEntity;

@ExtendWith(MockitoExtension.class)
class MaxArrivalDateRuleMapperTest {

  @InjectMocks
  private MaxArrivalDateMapperImpl maxArrivalDateMapper;
  @Spy
  private MaxArrivalDateRuleTransformer maxArrivalDateRuleTransformer;

  @Test
  void toDomain_givenEntityObject_shouldTransformToDomainObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var maxArrivalDateRuleDomain = MaxArrivalDateRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channelId("some channel")
        .maxArrivalDate(9)
        .build();
    var maxArrivalDateRuleEntity = new MaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity.setRuleId(1234);
    maxArrivalDateRuleEntity.setRefRuleId(0);
    maxArrivalDateRuleEntity.setStatus("NEW");
    maxArrivalDateRuleEntity.setCreatedAt(now);
    maxArrivalDateRuleEntity.setLastModifiedAt(now);
    maxArrivalDateRuleEntity.setEnableTimestamp(null);
    maxArrivalDateRuleEntity.setDisableTimestamp(now.plusDays(30));
    maxArrivalDateRuleEntity.setChannelId("some channel");
    maxArrivalDateRuleEntity.setMaxArrivalDate(9);

    //Act
    var result = maxArrivalDateMapper.toDomainModel(maxArrivalDateRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxArrivalDateRuleDomain);
  }

  @Test
  void toDomain_givenNull_shouldReturnNull() {
    //Arrange

    //Act
    var result = maxArrivalDateMapper.toDomainModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenDomainObject_shouldTransformToEntityObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var maxArrivalDateDomain = MaxArrivalDateRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channelId("some channel")
        .maxArrivalDate(9)
        .build();
    var maxArrivalDateRuleEntity = new MaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity.setRuleId(1234);
    maxArrivalDateRuleEntity.setRefRuleId(0);
    maxArrivalDateRuleEntity.setStatus("NEW");
    maxArrivalDateRuleEntity.setCreatedAt(now);
    maxArrivalDateRuleEntity.setLastModifiedAt(now);
    maxArrivalDateRuleEntity.setEnableTimestamp(null);
    maxArrivalDateRuleEntity.setDisableTimestamp(now.plusDays(30));
    maxArrivalDateRuleEntity.setChannelId("some channel");
    maxArrivalDateRuleEntity.setMaxArrivalDate(9);

    //Act
    var result = maxArrivalDateMapper.toEntityDto(maxArrivalDateDomain);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxArrivalDateRuleEntity);
  }

  @Test
  void toEntityDto_givenNull_shouldReturnNull() {
    //Arrange

    //Act
    var result = maxArrivalDateMapper.toEntityDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenNullStatus_shouldTransformToEntityObjectWithNullStatus() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var maxArrivalDateDomain = MaxArrivalDateRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(null)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channelId("some channel")
        .maxArrivalDate(9)
        .build();
    var maxArrivalDateRuleEntity = new MaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity.setRuleId(1234);
    maxArrivalDateRuleEntity.setRefRuleId(0);
    maxArrivalDateRuleEntity.setStatus(null);
    maxArrivalDateRuleEntity.setCreatedAt(now);
    maxArrivalDateRuleEntity.setLastModifiedAt(now);
    maxArrivalDateRuleEntity.setEnableTimestamp(null);
    maxArrivalDateRuleEntity.setDisableTimestamp(now.plusDays(30));
    maxArrivalDateRuleEntity.setChannelId("some channel");
    maxArrivalDateRuleEntity.setMaxArrivalDate(9);

    //Act
    var result = maxArrivalDateMapper.toEntityDto(maxArrivalDateDomain);

    //Assert
    assertThat(result.getStatus()).isNull();
  }
}
