package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxArrivalDateRuleEntity;

class MaxArrivalDateRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  private MaxArrivalDateRepository maxArrivalDateRepository;

  @BeforeEach
  void setUp() {
    maxArrivalDateRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    maxArrivalDateRepository.deleteAll();
  }

  @Test
  void save__shouldSaveInDb() {
    //Arrange
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity = createMaxArrivalDateRuleEntity();

    //Act
    MaxArrivalDateRuleEntity savedMaxNightsRuleEntity =
        maxArrivalDateRepository.save(maxArrivalDateRuleEntity);

    //Assert
    assertThat(savedMaxNightsRuleEntity).usingRecursiveComparison()
        .isEqualTo(maxArrivalDateRuleEntity);
  }

  @Test
  void findAllByStatusEqualsIgnoreCase__shouldReturnAListOfRecordsWithSameStatus() {
    //Arrange
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity1 = createMaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity1.setStatus("Searched Status");
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity2 = createMaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity2.setStatus("searched status");
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity3 = createMaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity3.setStatus("another status");
    maxArrivalDateRepository.saveAll(
        List.of(maxArrivalDateRuleEntity1, maxArrivalDateRuleEntity2, maxArrivalDateRuleEntity3));

    //Act
    List<MaxArrivalDateRuleEntity> foundRules =
        maxArrivalDateRepository.findAllByStatusEqualsIgnoreCase(
            "SEARCHED STATUS");

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(maxArrivalDateRuleEntity1, maxArrivalDateRuleEntity2));
  }

  @Test
  void findAllByStatusAndDisableTimestampIsBefore__shouldReturnRulesThatNeedsToBeDisable() {
    //Arrange
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity1 = createMaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity1.setStatus("Searched Status");
    maxArrivalDateRuleEntity1.setDisableTimestamp(NOW.plusDays(1));
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity2 = createMaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity2.setStatus("searched status");
    maxArrivalDateRuleEntity2.setDisableTimestamp(NOW.minusDays(1));
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity3 = createMaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity3.setStatus("another status");
    maxArrivalDateRuleEntity3.setDisableTimestamp(NOW.plusDays(1));
    maxArrivalDateRepository.saveAll(
        List.of(maxArrivalDateRuleEntity1, maxArrivalDateRuleEntity2, maxArrivalDateRuleEntity3));

    //Act
    List<MaxArrivalDateRuleEntity> foundRules = maxArrivalDateRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore("SEARCHED STATUS",
            LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(maxArrivalDateRuleEntity2));
  }

  @Test
  void findAllByStatusAndEnableTimestampIsBefore__shouldReturnRulesThatNeedsToBeEnable() {
    //Arrange
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity1 = createMaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity1.setStatus("Searched Status");
    maxArrivalDateRuleEntity1.setEnableTimestamp(NOW.minusDays(1));
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity2 = createMaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity2.setStatus("searched status");
    maxArrivalDateRuleEntity2.setEnableTimestamp(NOW.plusDays(1));
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity3 = createMaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity3.setStatus("another status");
    maxArrivalDateRuleEntity3.setEnableTimestamp(NOW.minusDays(1));
    maxArrivalDateRepository.saveAll(
        List.of(maxArrivalDateRuleEntity1, maxArrivalDateRuleEntity2, maxArrivalDateRuleEntity3));

    //Act
    List<MaxArrivalDateRuleEntity> foundRules = maxArrivalDateRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore("SEARCHED STATUS",
            LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(maxArrivalDateRuleEntity1));
  }

  private MaxArrivalDateRuleEntity createMaxArrivalDateRuleEntity() {
    var maxArrivalDateRuleEntity = new MaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity.setRefRuleId(null);
    maxArrivalDateRuleEntity.setStatus("STATUS");
    maxArrivalDateRuleEntity.setCreatedAt(NOW);
    maxArrivalDateRuleEntity.setLastModifiedAt(NOW);
    maxArrivalDateRuleEntity.setEnableTimestamp(NOW);
    maxArrivalDateRuleEntity.setDisableTimestamp(NOW);
    maxArrivalDateRuleEntity.setChannelId("channel id");
    maxArrivalDateRuleEntity.setMaxArrivalDate(9);
    return maxArrivalDateRuleEntity;
  }

}

