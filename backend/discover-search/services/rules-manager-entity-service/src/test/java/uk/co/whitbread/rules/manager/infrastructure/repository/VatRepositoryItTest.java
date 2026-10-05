package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.VatRuleEntity;

class VatRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  private VatRuleRepository vatRuleRepository;

  @BeforeEach
  void setUp() {
    vatRuleRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    vatRuleRepository.deleteAll();
  }

  @Test
  void save_shouldSaveInDb() {
    //Arrange
    VatRuleEntity vatRuleEntity = createVatRuleEntity();

    //Act
    VatRuleEntity savedVatRuleEntity = vatRuleRepository.save(
        vatRuleEntity);

    //Assert
    assertThat(savedVatRuleEntity).usingRecursiveComparison()
        .isEqualTo(vatRuleEntity);
  }

  @Test
  void findAllByStatusEqualsIgnoreCase_shouldReturnAListOfRecordsWithSameStatus() {
    //Arrange
    VatRuleEntity vatRuleEntity1 = createVatRuleEntity();
    vatRuleEntity1.setStatus("Searched Status");
    VatRuleEntity vatRuleEntity2 = createVatRuleEntity();
    vatRuleEntity2.setStatus("searched status");
    VatRuleEntity vatRuleEntity3 = createVatRuleEntity();
    vatRuleEntity3.setStatus("another status");
    vatRuleRepository.saveAll(
        List.of(vatRuleEntity1, vatRuleEntity2,
            vatRuleEntity3));

    //Act
    List<VatRuleEntity> foundRules = vatRuleRepository.findAllByStatusEqualsIgnoreCase(
        "SEARCHED STATUS");

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(vatRuleEntity1, vatRuleEntity2));
  }

  @Test
  void findAllByStatusAndDisableTimestampIsBefore_shouldReturnRulesThatNeedsToBeDisable() {
    //Arrange
    VatRuleEntity vatRuleEntity1 = createVatRuleEntity();
    vatRuleEntity1.setStatus("Searched Status");
    vatRuleEntity1.setDisableTimestamp(NOW.plusDays(1));
    VatRuleEntity vatRuleEntity2 = createVatRuleEntity();
    vatRuleEntity2.setStatus("searched status");
    vatRuleEntity2.setDisableTimestamp(NOW.minusDays(1));
    VatRuleEntity vatRuleEntity3 = createVatRuleEntity();
    vatRuleEntity3.setStatus("another status");
    vatRuleEntity3.setDisableTimestamp(NOW.plusDays(1));
    vatRuleRepository.saveAll(
        List.of(vatRuleEntity1, vatRuleEntity2,
            vatRuleEntity3));

    //Act
    List<VatRuleEntity> foundRules = vatRuleRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        "SEARCHED STATUS", LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(vatRuleEntity2));
  }

  @Test
  void findAllByStatusAndEnableTimestampIsBefore_shouldReturnRulesThatNeedsToBeEnable() {
    //Arrange
    VatRuleEntity vatRuleEntity1 = createVatRuleEntity();
    vatRuleEntity1.setStatus("Searched Status");
    vatRuleEntity1.setEnableTimestamp(NOW.minusDays(1));
    VatRuleEntity vatRuleEntity2 = createVatRuleEntity();
    vatRuleEntity2.setStatus("searched status");
    vatRuleEntity2.setEnableTimestamp(NOW.plusDays(1));
    VatRuleEntity vatRuleEntity3 = createVatRuleEntity();
    vatRuleEntity3.setStatus("another status");
    vatRuleEntity3.setEnableTimestamp(NOW.minusDays(1));
    vatRuleRepository.saveAll(
        List.of(vatRuleEntity1, vatRuleEntity2,
            vatRuleEntity3));

    //Act
    List<VatRuleEntity> foundRules = vatRuleRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
        "SEARCHED STATUS", LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(vatRuleEntity1));
  }

  private VatRuleEntity createVatRuleEntity() {
    VatRuleEntity vatRuleEntity = new VatRuleEntity();
    vatRuleEntity.setRefRuleId(null);
    vatRuleEntity.setStatus("STATUS");
    vatRuleEntity.setCreatedAt(NOW);
    vatRuleEntity.setLastModifiedAt(NOW);
    vatRuleEntity.setEnableTimestamp(NOW);
    vatRuleEntity.setDisableTimestamp(NOW);
    vatRuleEntity.setVatRegion("UK");
    vatRuleEntity.setTranCode("9026");
    vatRuleEntity.setDescription("Prepayment (20% VAT) Breakfast");
    vatRuleEntity.setVatBearing(Boolean.TRUE);

    return vatRuleEntity;
  }

}
