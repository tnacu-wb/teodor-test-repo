package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxNightsRuleEntity;

class MaxNightsRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  private MaxNightsRepository maxNightsRepository;

  @BeforeEach
  void setUp() {
    maxNightsRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    maxNightsRepository.deleteAll();
  }

  @Test
  void save__shouldSaveInDb() {
    //Arrange
    MaxNightsRuleEntity maxNightsRuleEntity = createMaxNightsRuleEntity();

    //Act
    MaxNightsRuleEntity savedMaxNightsRuleEntity = maxNightsRepository.save(maxNightsRuleEntity);

    //Assert
    assertThat(savedMaxNightsRuleEntity).usingRecursiveComparison()
        .isEqualTo(maxNightsRuleEntity);
  }

  @Test
  void findAllByStatusEqualsIgnoreCase__shouldReturnAListOfRecordsWithSameStatus() {
    //Arrange
    MaxNightsRuleEntity maxNightsRuleEntity1 = createMaxNightsRuleEntity();
    maxNightsRuleEntity1.setStatus("Searched Status");
    MaxNightsRuleEntity maxNightsRuleEntity2 = createMaxNightsRuleEntity();
    maxNightsRuleEntity2.setStatus("searched status");
    MaxNightsRuleEntity maxNightsRuleEntity3 = createMaxNightsRuleEntity();
    maxNightsRuleEntity3.setStatus("another status");
    maxNightsRepository.saveAll(
        List.of(maxNightsRuleEntity1, maxNightsRuleEntity2, maxNightsRuleEntity3));

    //Act
    List<MaxNightsRuleEntity> foundRules = maxNightsRepository.findAllByStatusEqualsIgnoreCase(
        "SEARCHED STATUS");

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(maxNightsRuleEntity1, maxNightsRuleEntity2));
  }

  @Test
  void findAllByStatusAndDisableTimestampIsBefore__shouldReturnRulesThatNeedsToBeDisable() {
    //Arrange
    MaxNightsRuleEntity maxNightsRuleEntity1 = createMaxNightsRuleEntity();
    maxNightsRuleEntity1.setStatus("Searched Status");
    maxNightsRuleEntity1.setDisableTimestamp(NOW.plusDays(1));
    MaxNightsRuleEntity maxNightsRuleEntity2 = createMaxNightsRuleEntity();
    maxNightsRuleEntity2.setStatus("searched status");
    maxNightsRuleEntity2.setDisableTimestamp(NOW.minusDays(1));
    MaxNightsRuleEntity maxNightsRuleEntity3 = createMaxNightsRuleEntity();
    maxNightsRuleEntity3.setStatus("another status");
    maxNightsRuleEntity3.setDisableTimestamp(NOW.plusDays(1));
    maxNightsRepository.saveAll(
        List.of(maxNightsRuleEntity1, maxNightsRuleEntity2, maxNightsRuleEntity3));

    //Act
    List<MaxNightsRuleEntity> foundRules = maxNightsRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore("SEARCHED STATUS",
            LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(maxNightsRuleEntity2));
  }

  @Test
  void findAllByStatusAndEnableTimestampIsBefore__shouldReturnRulesThatNeedsToBeEnable() {
    //Arrange
    MaxNightsRuleEntity maxNightsRuleEntity1 = createMaxNightsRuleEntity();
    maxNightsRuleEntity1.setStatus("Searched Status");
    maxNightsRuleEntity1.setEnableTimestamp(NOW.minusDays(1));
    MaxNightsRuleEntity maxNightsRuleEntity2 = createMaxNightsRuleEntity();
    maxNightsRuleEntity2.setStatus("searched status");
    maxNightsRuleEntity2.setEnableTimestamp(NOW.plusDays(1));
    MaxNightsRuleEntity maxNightsRuleEntity3 = createMaxNightsRuleEntity();
    maxNightsRuleEntity3.setStatus("another status");
    maxNightsRuleEntity3.setEnableTimestamp(NOW.minusDays(1));
    maxNightsRepository.saveAll(
        List.of(maxNightsRuleEntity1, maxNightsRuleEntity2, maxNightsRuleEntity3));

    //Act
    List<MaxNightsRuleEntity> foundRules = maxNightsRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore("SEARCHED STATUS",
            LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(maxNightsRuleEntity1));
  }

  private MaxNightsRuleEntity createMaxNightsRuleEntity() {
    MaxNightsRuleEntity maxNightsRuleEntity = new MaxNightsRuleEntity();
    maxNightsRuleEntity.setRefRuleId(null);
    maxNightsRuleEntity.setStatus("STATUS");
    maxNightsRuleEntity.setCreatedAt(NOW);
    maxNightsRuleEntity.setLastModifiedAt(NOW);
    maxNightsRuleEntity.setEnableTimestamp(NOW);
    maxNightsRuleEntity.setDisableTimestamp(NOW);
    maxNightsRuleEntity.setChannelId("channel id");
    maxNightsRuleEntity.setMaxNights(9);
    return maxNightsRuleEntity;
  }

}

