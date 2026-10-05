package uk.co.whitbread.rules.agent.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.AmendmentRuleEntity;

class AmendmentRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
  @Autowired
  private AmendmentRepository amendmentRepository;

  @BeforeEach
  void setUp() {
    amendmentRepository.deleteAll();
  }

  @Test
  void findAllByStatusActive__shouldReturnAListOfRecordsWithStatusActive() {
    //Arrange
    AmendmentRuleEntity amendmentRuleEntity1 = createAmendmentRuleEntity();
    amendmentRuleEntity1.setRuleId(1);
    amendmentRuleEntity1.setStatus("ACTIVE");
    AmendmentRuleEntity amendmentRuleEntity2 = createAmendmentRuleEntity();
    amendmentRuleEntity2.setRuleId(2);
    amendmentRuleEntity2.setStatus("ACTIVE");
    AmendmentRuleEntity amendmentRuleEntity3 = createAmendmentRuleEntity();
    amendmentRuleEntity3.setRuleId(3);
    amendmentRuleEntity3.setStatus("INACTIVE");
    amendmentRepository.saveAll(
        List.of(amendmentRuleEntity1, amendmentRuleEntity2, amendmentRuleEntity3));

    //Act
    List<AmendmentRuleEntity> foundRules = amendmentRepository.findAllByStatusActive();

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(amendmentRuleEntity1, amendmentRuleEntity2));
  }

  @Test
  void findAllUpdatedAfter_shouldReturnAListOfRecordsWithUpdate() {
    //Arrange
    AmendmentRuleEntity amendmentRuleEntity1 = createAmendmentRuleEntity();
    amendmentRuleEntity1.setRuleId(1);
    amendmentRuleEntity1.setStatus("ACTIVE");
    amendmentRuleEntity1.setLastModifiedAt(
        LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    AmendmentRuleEntity amendmentRuleEntity2 = createAmendmentRuleEntity();
    amendmentRuleEntity2.setRuleId(2);
    amendmentRuleEntity2.setStatus("ACTIVE");
    amendmentRuleEntity2.setLastModifiedAt(
        LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    AmendmentRuleEntity amendmentRuleEntity3 = createAmendmentRuleEntity();
    amendmentRuleEntity3.setRuleId(3);
    amendmentRuleEntity3.setStatus("ACTIVE");
    amendmentRepository.saveAll(
        List.of(amendmentRuleEntity1, amendmentRuleEntity2, amendmentRuleEntity3));

    //Act
    List<AmendmentRuleEntity> foundRules = amendmentRepository.findAllUpdatedAfter(
        LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(amendmentRuleEntity1, amendmentRuleEntity2));
  }

  private AmendmentRuleEntity createAmendmentRuleEntity() {
    AmendmentRuleEntity amendmentRuleEntity = new AmendmentRuleEntity();
    amendmentRuleEntity.setRefRuleId(null);
    amendmentRuleEntity.setStatus("STATUS");
    amendmentRuleEntity.setCreatedAt(NOW);
    amendmentRuleEntity.setLastModifiedAt(NOW);
    amendmentRuleEntity.setEnableTimestamp(NOW);
    amendmentRuleEntity.setDisableTimestamp(NOW);
    amendmentRuleEntity.setRateType("rate type");
    amendmentRuleEntity.setArrivalDateLimit(3);
    return amendmentRuleEntity;
  }
}
