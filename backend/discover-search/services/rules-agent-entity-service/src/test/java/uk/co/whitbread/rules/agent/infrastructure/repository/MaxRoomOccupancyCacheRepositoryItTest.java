package uk.co.whitbread.rules.agent.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxRoomOccupancyRuleEntity;

class MaxRoomOccupancyCacheRepositoryItTest extends AbstractIntegrationTest {

  @Autowired
  private MaxRoomOccupancyCacheRepository repository;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
  }

  @Test
  void findAllByStatusActive__shouldReturnAListOfRecordsWithStatusActive() {
    //Arrange
    var roomOccupancyRuleEntity1 = createRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity1.setRuleId(1);
    roomOccupancyRuleEntity1.setStatus("ACTIVE");
    var roomOccupancyRuleEntity2 = createRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity2.setRuleId(2);
    roomOccupancyRuleEntity2.setStatus("ACTIVE");
    var roomOccupancyRuleEntity3 = createRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity3.setRuleId(3);
    roomOccupancyRuleEntity3.setStatus("INACTIVE");
    repository.saveAll(
        List.of(roomOccupancyRuleEntity1, roomOccupancyRuleEntity2, roomOccupancyRuleEntity3));

    //Act
    List<MaxRoomOccupancyRuleEntity> foundRules = repository.findAllByStatusActive();

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(roomOccupancyRuleEntity1, roomOccupancyRuleEntity2));
  }

  @Test
  void findAllUpdatedAfter_shouldReturnAListOfRecordsWithUpdate() {
    //Arrange
    var roomOccupancyRuleEntity1 = createRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity1.setRuleId(1);
    roomOccupancyRuleEntity1.setStatus("ACTIVE");
    roomOccupancyRuleEntity1.setLastModifiedAt(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    var roomOccupancyRuleEntity2 = createRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity2.setRuleId(2);
    roomOccupancyRuleEntity2.setStatus("ACTIVE");
    roomOccupancyRuleEntity2.setLastModifiedAt(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    var roomOccupancyRuleEntity3 = createRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity3.setRuleId(3);
    roomOccupancyRuleEntity3.setStatus("ACTIVE");
    repository.saveAll(
        List.of(roomOccupancyRuleEntity1, roomOccupancyRuleEntity2, roomOccupancyRuleEntity3));

    //Act
    List<MaxRoomOccupancyRuleEntity> foundRules = repository.findAllUpdatedAfter(
        LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(roomOccupancyRuleEntity1, roomOccupancyRuleEntity2));
  }

  private MaxRoomOccupancyRuleEntity createRoomOccupancyRuleEntity() {
    var time = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    var roomOccupancyRuleEntity = new MaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity.setRuleId(1);
    roomOccupancyRuleEntity.setCreatedAt(time);
    roomOccupancyRuleEntity.setLastModifiedAt(time);
    roomOccupancyRuleEntity.setStatus("ACTIVE");
    roomOccupancyRuleEntity.setChannelId("CCUI");
    roomOccupancyRuleEntity.setAdults(1);
    roomOccupancyRuleEntity.setChildren(0);
    roomOccupancyRuleEntity.setSingleRoom(true);
    roomOccupancyRuleEntity.setDoubleRoom(true);
    roomOccupancyRuleEntity.setTwinRoom(false);
    roomOccupancyRuleEntity.setAccessibleRoom(true);
    roomOccupancyRuleEntity.setFamilyRoom(false);
    return roomOccupancyRuleEntity;
  }

}
