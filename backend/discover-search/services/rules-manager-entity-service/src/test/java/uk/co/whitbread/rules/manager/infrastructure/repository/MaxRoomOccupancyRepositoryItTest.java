package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxRoomOccupancyRuleEntity;

class MaxRoomOccupancyRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  private MaxRoomOccupancyRepository maxRoomOccupancyRepository;

  @BeforeEach
  void setUp() {
    maxRoomOccupancyRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    maxRoomOccupancyRepository.deleteAll();
  }

  @Test
  void save__shouldSaveInDb() {
    //Arrange
    MaxRoomOccupancyRuleEntity roomOccupancyRuleEntity = createMaxRoomOccupancyRuleEntity();

    //Act
    MaxRoomOccupancyRuleEntity savedMaxRoomsRuleEntity = maxRoomOccupancyRepository.save(roomOccupancyRuleEntity);

    //Assert
    assertThat(savedMaxRoomsRuleEntity).usingRecursiveComparison()
        .isEqualTo(roomOccupancyRuleEntity);
  }

  @Test
  void findAllByStatusEqualsIgnoreCase__shouldReturnAListOfRecordsWithSameStatus() {
    //Arrange
    MaxRoomOccupancyRuleEntity roomOccupancyRuleEntity1 = createMaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity1.setStatus("Searched Status");
    MaxRoomOccupancyRuleEntity roomOccupancyRuleEntity2 = createMaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity2.setStatus("searched status");
    MaxRoomOccupancyRuleEntity roomOccupancyRuleEntity3 = createMaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity3.setStatus("another status");
    maxRoomOccupancyRepository.saveAll(
        List.of(roomOccupancyRuleEntity1, roomOccupancyRuleEntity2, roomOccupancyRuleEntity3));

    //Act
    List<MaxRoomOccupancyRuleEntity> foundRules = maxRoomOccupancyRepository.findAllByStatusEqualsIgnoreCase(
        "SEARCHED STATUS");

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(roomOccupancyRuleEntity1, roomOccupancyRuleEntity2));
  }

  @Test
  void findAllByStatusAndDisableTimestampIsBefore__shouldReturnRulesThatNeedsToBeDisable() {
    //Arrange
    MaxRoomOccupancyRuleEntity roomOccupancyRuleEntity1 = createMaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity1.setStatus("Searched Status");
    roomOccupancyRuleEntity1.setDisableTimestamp(NOW.plusDays(1));
    MaxRoomOccupancyRuleEntity roomOccupancyRuleEntity2 = createMaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity2.setStatus("searched status");
    roomOccupancyRuleEntity2.setDisableTimestamp(NOW.minusDays(1));
    MaxRoomOccupancyRuleEntity roomOccupancyRuleEntity3 = createMaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity3.setStatus("another status");
    roomOccupancyRuleEntity3.setDisableTimestamp(NOW.plusDays(1));
    maxRoomOccupancyRepository.saveAll(
        List.of(roomOccupancyRuleEntity1, roomOccupancyRuleEntity2, roomOccupancyRuleEntity3));

    //Act
    List<MaxRoomOccupancyRuleEntity> foundRules = maxRoomOccupancyRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore("SEARCHED STATUS",
            LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(roomOccupancyRuleEntity2));
  }

  @Test
  void findAllByStatusAndEnableTimestampIsBefore__shouldReturnRulesThatNeedsToBeEnable() {
    //Arrange
    MaxRoomOccupancyRuleEntity roomOccupancyRuleEntity1 = createMaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity1.setStatus("Searched Status");
    roomOccupancyRuleEntity1.setEnableTimestamp(NOW.minusDays(1));
    MaxRoomOccupancyRuleEntity roomOccupancyRuleEntity2 = createMaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity2.setStatus("searched status");
    roomOccupancyRuleEntity2.setEnableTimestamp(NOW.plusDays(1));
    MaxRoomOccupancyRuleEntity roomOccupancyRuleEntity3 = createMaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity3.setStatus("another status");
    roomOccupancyRuleEntity3.setEnableTimestamp(NOW.minusDays(1));
    maxRoomOccupancyRepository.saveAll(
        List.of(roomOccupancyRuleEntity1, roomOccupancyRuleEntity2, roomOccupancyRuleEntity3));

    //Act
    List<MaxRoomOccupancyRuleEntity> foundRules = maxRoomOccupancyRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore("SEARCHED STATUS",
            LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(roomOccupancyRuleEntity1));
  }

  private MaxRoomOccupancyRuleEntity createMaxRoomOccupancyRuleEntity() {
    var roomOccupancyRuleEntity = new MaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity.setRefRuleId(null);
    roomOccupancyRuleEntity.setStatus("STATUS");
    roomOccupancyRuleEntity.setCreatedAt(NOW);
    roomOccupancyRuleEntity.setLastModifiedAt(NOW);
    roomOccupancyRuleEntity.setEnableTimestamp(NOW);
    roomOccupancyRuleEntity.setDisableTimestamp(NOW);
    roomOccupancyRuleEntity.setChannelId("channel id");
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
