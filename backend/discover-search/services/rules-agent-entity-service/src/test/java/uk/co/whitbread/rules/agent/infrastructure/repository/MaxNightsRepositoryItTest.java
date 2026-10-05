package uk.co.whitbread.rules.agent.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxNightsRuleEntity;

class MaxNightsRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  public MaxNightsCacheRepository maxNightsCacheRepository;

  @BeforeEach
  void setUp() {
    maxNightsCacheRepository.deleteAll();
  }

  @Test
  void findAllByStatusActive__shouldReturnAListOfRecordsWithStatusActive() {
    //Arrange
    MaxNightsRuleEntity maxNightsRuleEntity1 = createMaxNightsRuleEntity();
    maxNightsRuleEntity1.setRuleId(1);
    maxNightsRuleEntity1.setStatus("ACTIVE");
    MaxNightsRuleEntity maxNightsRuleEntity2 = createMaxNightsRuleEntity();
    maxNightsRuleEntity2.setRuleId(2);
    maxNightsRuleEntity2.setStatus("ACTIVE");
    MaxNightsRuleEntity maxNightsRuleEntity3 = createMaxNightsRuleEntity();
    maxNightsRuleEntity3.setRuleId(3);
    maxNightsRuleEntity3.setStatus("INACTIVE");
    maxNightsCacheRepository.saveAll(
        List.of(maxNightsRuleEntity1, maxNightsRuleEntity2, maxNightsRuleEntity3));

    //Act
    List<MaxNightsRuleEntity> foundRules = maxNightsCacheRepository.findAllByStatusActive();

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(maxNightsRuleEntity1, maxNightsRuleEntity2));
  }

  @Test
  void findAllUpdatedAfter_shouldReturnAListOfRecordsWithUpdate() {
    //Arrange
    MaxNightsRuleEntity maxNightsRuleEntity1 = createMaxNightsRuleEntity();
    maxNightsRuleEntity1.setRuleId(1);
    maxNightsRuleEntity1.setStatus("ACTIVE");
    maxNightsRuleEntity1.setLastModifiedAt(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    MaxNightsRuleEntity maxNightsRuleEntity2 = createMaxNightsRuleEntity();
    maxNightsRuleEntity2.setRuleId(2);
    maxNightsRuleEntity2.setStatus("ACTIVE");
    maxNightsRuleEntity2.setLastModifiedAt(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    MaxNightsRuleEntity maxNightsRuleEntity3 = createMaxNightsRuleEntity();
    maxNightsRuleEntity3.setRuleId(3);
    maxNightsRuleEntity3.setStatus("ACTIVE");
    maxNightsCacheRepository.saveAll(
        List.of(maxNightsRuleEntity1, maxNightsRuleEntity2, maxNightsRuleEntity3));

    //Act
    List<MaxNightsRuleEntity> foundRules = maxNightsCacheRepository.findAllUpdatedAfter(
        LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(maxNightsRuleEntity1, maxNightsRuleEntity2));
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
