package uk.co.whitbread.rules.agent.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RateSuppressionRuleEntity;

class RateSuppressionRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  public RateSuppressionCacheRepository rateSuppressionCacheRepository;

  @BeforeEach
  void setUp() {
    rateSuppressionCacheRepository.deleteAll();
  }

  @Test
  void findAllByStatusActive__shouldReturnAListOfRecordsWithStatusActive() {
    //Arrange
    var rateSuppressionRuleEntity1 = createRateSuppressionRuleEntity();
    rateSuppressionRuleEntity1.setRuleId(1);
    rateSuppressionRuleEntity1.setStatus("ACTIVE");

    var rateSuppressionRuleEntity2 = createRateSuppressionRuleEntity();
    rateSuppressionRuleEntity2.setRuleId(2);
    rateSuppressionRuleEntity2.setStatus("ACTIVE");

    var rateSuppressionRuleEntity3 = createRateSuppressionRuleEntity();
    rateSuppressionRuleEntity3.setRuleId(3);
    rateSuppressionRuleEntity3.setStatus("INACTIVE");

    rateSuppressionCacheRepository.saveAll(
        List.of(rateSuppressionRuleEntity1, rateSuppressionRuleEntity2,
            rateSuppressionRuleEntity3));

    //Act
    var foundRules = rateSuppressionCacheRepository.findAllByStatusActive();

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(rateSuppressionRuleEntity1, rateSuppressionRuleEntity2));

  }

  @Test
  void findAllUpdatedAfter_shouldReturnAListOfRecordsWithUpdate() {
    //Arrange
    var rateSuppressionRuleEntity1 = createRateSuppressionRuleEntity();
    rateSuppressionRuleEntity1.setRuleId(1);
    rateSuppressionRuleEntity1.setStatus("ACTIVE");
    rateSuppressionRuleEntity1.setLastModifiedAt(
        LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));

    var rateSuppressionRuleEntity2 = createRateSuppressionRuleEntity();
    rateSuppressionRuleEntity2.setRuleId(2);
    rateSuppressionRuleEntity2.setStatus("ACTIVE");
    rateSuppressionRuleEntity2.setLastModifiedAt(
        LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));

    var rateSuppressionRuleEntity3 = createRateSuppressionRuleEntity();
    rateSuppressionRuleEntity3.setRuleId(3);
    rateSuppressionRuleEntity3.setStatus("INACTIVE");

    rateSuppressionCacheRepository.saveAll(
        List.of(rateSuppressionRuleEntity1, rateSuppressionRuleEntity2,
            rateSuppressionRuleEntity3));

    //Act

    var foundRules = rateSuppressionCacheRepository.findAllUpdatedAfter(
        LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(rateSuppressionRuleEntity1, rateSuppressionRuleEntity2));

  }

  private RateSuppressionRuleEntity createRateSuppressionRuleEntity() {

    var rateSuppressionRuleEntity = new RateSuppressionRuleEntity();
    rateSuppressionRuleEntity.setCreatedAt(NOW);
    rateSuppressionRuleEntity.setLastModifiedAt(NOW);
    rateSuppressionRuleEntity.setRateType("FLEX");
    rateSuppressionRuleEntity.setPriority((short) 10);
    return rateSuppressionRuleEntity;
  }

}
