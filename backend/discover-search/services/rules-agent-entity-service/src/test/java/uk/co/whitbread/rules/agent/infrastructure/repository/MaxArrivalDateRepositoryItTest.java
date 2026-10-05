package uk.co.whitbread.rules.agent.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxArrivalDateRuleEntity;

class MaxArrivalDateRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  public MaxArrivalDateCacheRepository maxArrivalDateCacheRepository;

  @BeforeEach
  void setUp() {
    maxArrivalDateCacheRepository.deleteAll();
  }

  @Test
  void findAllByStatusActive__shouldReturnAListOfRecordsWithStatusActive() {
    //Arrange
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity1 = createMaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity1.setRuleId(1);
    maxArrivalDateRuleEntity1.setStatus("ACTIVE");
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity2 = createMaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity2.setRuleId(2);
    maxArrivalDateRuleEntity2.setStatus("ACTIVE");
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity3 = createMaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity3.setRuleId(3);
    maxArrivalDateRuleEntity3.setStatus("INACTIVE");
    maxArrivalDateCacheRepository.saveAll(
        List.of(maxArrivalDateRuleEntity1, maxArrivalDateRuleEntity2, maxArrivalDateRuleEntity3));

    //Act
    List<MaxArrivalDateRuleEntity> foundRules = maxArrivalDateCacheRepository.findAllByStatusActive();

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(maxArrivalDateRuleEntity1, maxArrivalDateRuleEntity2));
  }

  @Test
  void findAllUpdatedAfter_shouldReturnAListOfRecordsWithUpdate() {
    //Arrange
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity1 = createMaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity1.setRuleId(1);
    maxArrivalDateRuleEntity1.setStatus("ACTIVE");
    maxArrivalDateRuleEntity1.setLastModifiedAt(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity2 = createMaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity2.setRuleId(2);
    maxArrivalDateRuleEntity2.setStatus("ACTIVE");
    maxArrivalDateRuleEntity2.setLastModifiedAt(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity3 = createMaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity3.setRuleId(3);
    maxArrivalDateRuleEntity3.setStatus("ACTIVE");
    maxArrivalDateCacheRepository.saveAll(
        List.of(maxArrivalDateRuleEntity1, maxArrivalDateRuleEntity2, maxArrivalDateRuleEntity3));

    //Act
    List<MaxArrivalDateRuleEntity> foundRules = maxArrivalDateCacheRepository.findAllUpdatedAfter(
        LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(maxArrivalDateRuleEntity1, maxArrivalDateRuleEntity2));
  }

  private MaxArrivalDateRuleEntity createMaxArrivalDateRuleEntity() {
    MaxArrivalDateRuleEntity maxArrivalDateRuleEntity = new MaxArrivalDateRuleEntity();
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
