package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.MaxRoomsRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxRoomsRuleEntity;

@ExtendWith(MockitoExtension.class)
class MaxRoomsRuleMapperTest {

  @InjectMocks
  private MaxRoomsRuleMapperImpl maxRoomsRuleMapper;
  @Spy
  private MaxRoomsRuleTransformer maxRoomsRuleTransformer;

  @Test
  void toDomain_givenEntityObject_shouldTransformToDomainObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var maxRoomsRuleDomain = MaxRoomsRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channelId("some channel")
        .maxRooms(9)
        .build();
    var maxRoomsRuleEntity = new MaxRoomsRuleEntity();
    maxRoomsRuleEntity.setRuleId(1234);
    maxRoomsRuleEntity.setRefRuleId(0);
    maxRoomsRuleEntity.setStatus("NEW");
    maxRoomsRuleEntity.setCreatedAt(now);
    maxRoomsRuleEntity.setLastModifiedAt(now);
    maxRoomsRuleEntity.setEnableTimestamp(null);
    maxRoomsRuleEntity.setDisableTimestamp(now.plusDays(30));
    maxRoomsRuleEntity.setChannelId("some channel");
    maxRoomsRuleEntity.setMaxRooms(9);

    //Act
    var result = maxRoomsRuleMapper.toDomainModel(maxRoomsRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxRoomsRuleDomain);
  }

  @Test
  void toDomain_givenNull_shouldReturnNull() {
    //Arrange

    //Act
    var result = maxRoomsRuleMapper.toDomainModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenDomainObject_shouldTransformToEntityObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var maxRoomsRuleDomain = MaxRoomsRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channelId("some channel")
        .maxRooms(9)
        .build();
    var maxRoomsRuleEntity = new MaxRoomsRuleEntity();
    maxRoomsRuleEntity.setRuleId(1234);
    maxRoomsRuleEntity.setRefRuleId(0);
    maxRoomsRuleEntity.setStatus("NEW");
    maxRoomsRuleEntity.setCreatedAt(now);
    maxRoomsRuleEntity.setLastModifiedAt(now);
    maxRoomsRuleEntity.setEnableTimestamp(null);
    maxRoomsRuleEntity.setDisableTimestamp(now.plusDays(30));
    maxRoomsRuleEntity.setChannelId("some channel");
    maxRoomsRuleEntity.setMaxRooms(9);

    //Act
    var result = maxRoomsRuleMapper.toEntityDto(maxRoomsRuleDomain);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxRoomsRuleEntity);
  }

  @Test
  void toEntityDto_givenNull_shouldReturnNull() {
    //Arrange

    //Act
    var result = maxRoomsRuleMapper.toEntityDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenNullStatus_shouldTransformToEntityObjectWithNullStatus() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var maxRoomsRuleDomain = MaxRoomsRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(null)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channelId("some channel")
        .maxRooms(9)
        .build();
    var maxRoomsRuleEntity = new MaxRoomsRuleEntity();
    maxRoomsRuleEntity.setRuleId(1234);
    maxRoomsRuleEntity.setRefRuleId(0);
    maxRoomsRuleEntity.setStatus(null);
    maxRoomsRuleEntity.setCreatedAt(now);
    maxRoomsRuleEntity.setLastModifiedAt(now);
    maxRoomsRuleEntity.setEnableTimestamp(null);
    maxRoomsRuleEntity.setDisableTimestamp(now.plusDays(30));
    maxRoomsRuleEntity.setChannelId("some channel");
    maxRoomsRuleEntity.setMaxRooms(9);

    //Act
    var result = maxRoomsRuleMapper.toEntityDto(maxRoomsRuleDomain);

    //Assert
    assertThat(result.getStatus()).isNull();
  }
}
