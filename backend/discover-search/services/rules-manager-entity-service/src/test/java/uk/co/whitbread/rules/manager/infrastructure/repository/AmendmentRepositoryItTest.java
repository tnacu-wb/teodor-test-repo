package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.AmendmentRuleEntity;

class AmendmentRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  private AmendmentRepository amendmentRepository;

  @BeforeEach
  void setUp() {
    amendmentRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    amendmentRepository.deleteAll();
  }

  @Test
  void save__shouldSaveInDb() {
    //Arrange
    AmendmentRuleEntity amendmentRuleEntity = createAmendmentRuleEntity();

    //Act
    AmendmentRuleEntity savedAmendmentRuleEntity = amendmentRepository.save(amendmentRuleEntity);

    //Assert
    assertThat(savedAmendmentRuleEntity).usingRecursiveComparison()
        .isEqualTo(amendmentRuleEntity);
  }

  @Test
  void findAllByStatusEqualsIgnoreCase__shouldReturnAListOfRecordsWithSameStatus() {
    //Arrange
    AmendmentRuleEntity amendmentRuleEntity1 = createAmendmentRuleEntity();
    amendmentRuleEntity1.setStatus("Searched Status");
    AmendmentRuleEntity amendmentRuleEntity2 = createAmendmentRuleEntity();
    amendmentRuleEntity2.setStatus("searched status");
    AmendmentRuleEntity amendmentRuleEntity3 = createAmendmentRuleEntity();
    amendmentRuleEntity3.setStatus("another status");
    amendmentRepository.saveAll(
        List.of(amendmentRuleEntity1, amendmentRuleEntity2, amendmentRuleEntity3));

    //Act
    List<AmendmentRuleEntity> foundRules = amendmentRepository.findAllByStatusEqualsIgnoreCase(
        "SEARCHED STATUS");

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(amendmentRuleEntity1, amendmentRuleEntity2));
  }

  @Test
  void findAllByStatusAndDisableTimestampIsBefore__shouldReturnRulesThatNeedsToBeDisable() {
    //Arrange
    AmendmentRuleEntity amendmentRuleEntity1 = createAmendmentRuleEntity();
    amendmentRuleEntity1.setStatus("Searched Status");
    amendmentRuleEntity1.setDisableTimestamp(NOW.plusDays(1));
    AmendmentRuleEntity amendmentRuleEntity2 = createAmendmentRuleEntity();
    amendmentRuleEntity2.setStatus("searched status");
    amendmentRuleEntity2.setDisableTimestamp(NOW.minusDays(1));
    AmendmentRuleEntity amendmentRuleEntity3 = createAmendmentRuleEntity();
    amendmentRuleEntity3.setStatus("another status");
    amendmentRuleEntity3.setDisableTimestamp(NOW.plusDays(1));
    amendmentRepository.saveAll(
        List.of(amendmentRuleEntity1, amendmentRuleEntity2, amendmentRuleEntity3));

    //Act
    List<AmendmentRuleEntity> foundRules = amendmentRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore("SEARCHED STATUS",
            LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(amendmentRuleEntity2));
  }

  @Test
  void findAllByStatusAndEnableTimestampIsBefore__shouldReturnRulesThatNeedsToBeEnable() {
    //Arrange
    AmendmentRuleEntity amendmentRuleEntity1 = createAmendmentRuleEntity();
    amendmentRuleEntity1.setStatus("Searched Status");
    amendmentRuleEntity1.setEnableTimestamp(NOW.minusDays(1));
    AmendmentRuleEntity amendmentRuleEntity2 = createAmendmentRuleEntity();
    amendmentRuleEntity2.setStatus("searched status");
    amendmentRuleEntity2.setEnableTimestamp(NOW.plusDays(1));
    AmendmentRuleEntity amendmentRuleEntity3 = createAmendmentRuleEntity();
    amendmentRuleEntity3.setStatus("another status");
    amendmentRuleEntity3.setEnableTimestamp(NOW.minusDays(1));
    amendmentRepository.saveAll(
        List.of(amendmentRuleEntity1, amendmentRuleEntity2, amendmentRuleEntity3));

    //Act
    List<AmendmentRuleEntity> foundRules = amendmentRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore("SEARCHED STATUS",
            LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(amendmentRuleEntity1));
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
    amendmentRuleEntity.setCountryCode("GB");
    amendmentRuleEntity.setArrivalDateLimit(3);
    return amendmentRuleEntity;
  }
}
