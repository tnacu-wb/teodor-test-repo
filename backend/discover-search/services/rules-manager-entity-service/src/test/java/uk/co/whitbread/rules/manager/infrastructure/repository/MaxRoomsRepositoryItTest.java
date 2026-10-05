package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxRoomsRuleEntity;

class MaxRoomsRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  private MaxRoomsRepository maxRoomsRepository;

  @BeforeEach
  void setUp() {
    maxRoomsRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    maxRoomsRepository.deleteAll();
  }

  @Test
  void save__shouldSaveInDb() {
    //Arrange
    MaxRoomsRuleEntity maxRoomsRuleEntity = createMaxRoomsRuleEntity();

    //Act
    MaxRoomsRuleEntity savedMaxRoomsRuleEntity = maxRoomsRepository.save(maxRoomsRuleEntity);

    //Assert
    assertThat(savedMaxRoomsRuleEntity).usingRecursiveComparison()
        .isEqualTo(maxRoomsRuleEntity);
  }

  @Test
  void findAllByStatusEqualsIgnoreCase__shouldReturnAListOfRecordsWithSameStatus() {
    //Arrange
    MaxRoomsRuleEntity maxRoomsRuleEntity1 = createMaxRoomsRuleEntity();
    maxRoomsRuleEntity1.setStatus("Searched Status");
    MaxRoomsRuleEntity maxRoomsRuleEntity2 = createMaxRoomsRuleEntity();
    maxRoomsRuleEntity2.setStatus("searched status");
    MaxRoomsRuleEntity maxRoomsRuleEntity3 = createMaxRoomsRuleEntity();
    maxRoomsRuleEntity3.setStatus("another status");
    maxRoomsRepository.saveAll(
        List.of(maxRoomsRuleEntity1, maxRoomsRuleEntity2, maxRoomsRuleEntity3));

    //Act
    List<MaxRoomsRuleEntity> foundRules = maxRoomsRepository.findAllByStatusEqualsIgnoreCase(
        "SEARCHED STATUS");

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(maxRoomsRuleEntity1, maxRoomsRuleEntity2));
  }

  @Test
  void findAllByStatusAndDisableTimestampIsBefore__shouldReturnRulesThatNeedsToBeDisable() {
    //Arrange
    MaxRoomsRuleEntity maxRoomsRuleEntity1 = createMaxRoomsRuleEntity();
    maxRoomsRuleEntity1.setStatus("Searched Status");
    maxRoomsRuleEntity1.setDisableTimestamp(NOW.plusDays(1));
    MaxRoomsRuleEntity maxRoomsRuleEntity2 = createMaxRoomsRuleEntity();
    maxRoomsRuleEntity2.setStatus("searched status");
    maxRoomsRuleEntity2.setDisableTimestamp(NOW.minusDays(1));
    MaxRoomsRuleEntity maxRoomsRuleEntity3 = createMaxRoomsRuleEntity();
    maxRoomsRuleEntity3.setStatus("another status");
    maxRoomsRuleEntity3.setDisableTimestamp(NOW.plusDays(1));
    maxRoomsRepository.saveAll(
        List.of(maxRoomsRuleEntity1, maxRoomsRuleEntity2, maxRoomsRuleEntity3));

    //Act
    List<MaxRoomsRuleEntity> foundRules = maxRoomsRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore("SEARCHED STATUS",
            LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(maxRoomsRuleEntity2));
  }

  @Test
  void findAllByStatusAndEnableTimestampIsBefore__shouldReturnRulesThatNeedsToBeEnable() {
    //Arrange
    MaxRoomsRuleEntity maxRoomsRuleEntity1 = createMaxRoomsRuleEntity();
    maxRoomsRuleEntity1.setStatus("Searched Status");
    maxRoomsRuleEntity1.setEnableTimestamp(NOW.minusDays(1));
    MaxRoomsRuleEntity maxRoomsRuleEntity2 = createMaxRoomsRuleEntity();
    maxRoomsRuleEntity2.setStatus("searched status");
    maxRoomsRuleEntity2.setEnableTimestamp(NOW.plusDays(1));
    MaxRoomsRuleEntity maxRoomsRuleEntity3 = createMaxRoomsRuleEntity();
    maxRoomsRuleEntity3.setStatus("another status");
    maxRoomsRuleEntity3.setEnableTimestamp(NOW.minusDays(1));
    maxRoomsRepository.saveAll(
        List.of(maxRoomsRuleEntity1, maxRoomsRuleEntity2, maxRoomsRuleEntity3));

    //Act
    List<MaxRoomsRuleEntity> foundRules = maxRoomsRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore("SEARCHED STATUS",
            LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(maxRoomsRuleEntity1));
  }

  private MaxRoomsRuleEntity createMaxRoomsRuleEntity() {
    MaxRoomsRuleEntity maxRoomsRuleEntity = new MaxRoomsRuleEntity();
    maxRoomsRuleEntity.setRefRuleId(null);
    maxRoomsRuleEntity.setStatus("STATUS");
    maxRoomsRuleEntity.setCreatedAt(NOW);
    maxRoomsRuleEntity.setLastModifiedAt(NOW);
    maxRoomsRuleEntity.setEnableTimestamp(NOW);
    maxRoomsRuleEntity.setDisableTimestamp(NOW);
    maxRoomsRuleEntity.setChannelId("channel id");
    maxRoomsRuleEntity.setMaxRooms(9);
    return maxRoomsRuleEntity;
  }

}

