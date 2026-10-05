package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.MaxRoomOccupancyRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxRoomOccupancyRuleEntity;

@ExtendWith(MockitoExtension.class)
class MaxRoomOccupancyMapperTest {

  @InjectMocks
  private MaxRoomOccupancyRuleMapperImpl roomOccupancyRuleMapper;
  @Spy
  private MaxRoomOccupancyRuleTransformer maxRoomOccupancyRuleTransformer;

  @Test
  void toDomain_givenEntityObject_shouldTransformToDomainObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var maxRoomOccupancyRuleDomain = MaxRoomOccupancyRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channelId("some channel")
        .adults(1)
        .children(0)
        .singleRoom(true)
        .doubleRoom(true)
        .twinRoom(false)
        .accessibleRoom(true)
        .familyRoom(false)
        .build();
    var roomOccupancyRuleEntity = new MaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity.setRuleId(1234);
    roomOccupancyRuleEntity.setRefRuleId(0);
    roomOccupancyRuleEntity.setStatus("NEW");
    roomOccupancyRuleEntity.setCreatedAt(now);
    roomOccupancyRuleEntity.setLastModifiedAt(now);
    roomOccupancyRuleEntity.setEnableTimestamp(null);
    roomOccupancyRuleEntity.setDisableTimestamp(now.plusDays(30));
    roomOccupancyRuleEntity.setChannelId("some channel");
    roomOccupancyRuleEntity.setAdults(1);
    roomOccupancyRuleEntity.setChildren(0);
    roomOccupancyRuleEntity.setSingleRoom(true);
    roomOccupancyRuleEntity.setDoubleRoom(true);
    roomOccupancyRuleEntity.setTwinRoom(false);
    roomOccupancyRuleEntity.setAccessibleRoom(true);
    roomOccupancyRuleEntity.setFamilyRoom(false);

    //Act
    var result = roomOccupancyRuleMapper.toDomainModel(roomOccupancyRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxRoomOccupancyRuleDomain);
  }

  @Test
  void toDomain_givenNull_shouldReturnNull() {
    //Arrange

    //Act
    var result = roomOccupancyRuleMapper.toDomainModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenDomainObject_shouldTransformToEntityObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var maxRoomsRuleDomain = MaxRoomOccupancyRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channelId("some channel")
        .adults(1)
        .children(0)
        .singleRoom(true)
        .doubleRoom(true)
        .twinRoom(false)
        .accessibleRoom(true)
        .familyRoom(false)
        .build();
    var roomOccupancyRuleEntity = new MaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity.setRuleId(1234);
    roomOccupancyRuleEntity.setRefRuleId(0);
    roomOccupancyRuleEntity.setStatus("NEW");
    roomOccupancyRuleEntity.setCreatedAt(now);
    roomOccupancyRuleEntity.setLastModifiedAt(now);
    roomOccupancyRuleEntity.setEnableTimestamp(null);
    roomOccupancyRuleEntity.setDisableTimestamp(now.plusDays(30));
    roomOccupancyRuleEntity.setChannelId("some channel");
    roomOccupancyRuleEntity.setAdults(1);
    roomOccupancyRuleEntity.setChildren(0);
    roomOccupancyRuleEntity.setSingleRoom(true);
    roomOccupancyRuleEntity.setDoubleRoom(true);
    roomOccupancyRuleEntity.setTwinRoom(false);
    roomOccupancyRuleEntity.setAccessibleRoom(true);
    roomOccupancyRuleEntity.setFamilyRoom(false);

    //Act
    var result = roomOccupancyRuleMapper.toEntityDto(maxRoomsRuleDomain);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(roomOccupancyRuleEntity);
  }

  @Test
  void toEntityDto_givenNull_shouldReturnNull() {
    //Arrange

    //Act
    var result = roomOccupancyRuleMapper.toEntityDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenNullStatus_shouldTransformToEntityObjectWithNullStatus() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var maxRoomsRuleDomain = MaxRoomOccupancyRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(null)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channelId("some channel")
        .adults(1)
        .children(0)
        .singleRoom(true)
        .doubleRoom(true)
        .twinRoom(false)
        .accessibleRoom(true)
        .familyRoom(false)
        .build();
    var roomOccupancyRuleEntity = new MaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity.setRuleId(1234);
    roomOccupancyRuleEntity.setRefRuleId(0);
    roomOccupancyRuleEntity.setStatus(null);
    roomOccupancyRuleEntity.setCreatedAt(now);
    roomOccupancyRuleEntity.setLastModifiedAt(now);
    roomOccupancyRuleEntity.setEnableTimestamp(null);
    roomOccupancyRuleEntity.setDisableTimestamp(now.plusDays(30));
    roomOccupancyRuleEntity.setChannelId("some channel");
    roomOccupancyRuleEntity.setAdults(1);
    roomOccupancyRuleEntity.setChildren(0);
    roomOccupancyRuleEntity.setSingleRoom(true);
    roomOccupancyRuleEntity.setDoubleRoom(true);
    roomOccupancyRuleEntity.setTwinRoom(false);
    roomOccupancyRuleEntity.setAccessibleRoom(true);
    roomOccupancyRuleEntity.setFamilyRoom(false);

    //Act
    var result = roomOccupancyRuleMapper.toEntityDto(maxRoomsRuleDomain);

    //Assert
    assertThat(result.getStatus()).isNull();
  }

}
