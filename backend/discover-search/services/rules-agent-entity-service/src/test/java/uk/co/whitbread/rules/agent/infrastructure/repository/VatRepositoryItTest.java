package uk.co.whitbread.rules.agent.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.co.whitbread.rules.agent.domain.model.out.RuleStatus.ACTIVE;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.VatRuleEntity;

class VatRepositoryItTest extends AbstractIntegrationTest {

  private static final String VAT_REGION = "UK";
  private static final String PKG_CODE = "MDBEVA";
  private static final String TRAN_CODE = "9028";

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  public VatCacheRepository vatCacheRepository;

  @BeforeEach
  void setUp() {
    vatCacheRepository.deleteAll();
  }

  @Test
  void findAllByStatusActive__shouldReturnAListOfRecordsWithStatusActive() {
    //Arrange
    VatRuleEntity vatRuleEntity1 = createVatRuleEntity();
    vatRuleEntity1.setRuleId(1);
    vatRuleEntity1.setStatus("ACTIVE");
    VatRuleEntity vatRuleEntity2 = createVatRuleEntity();
    vatRuleEntity2.setRuleId(2);
    vatRuleEntity2.setStatus("ACTIVE");
    VatRuleEntity vatRuleEntity3 = createVatRuleEntity();
    vatRuleEntity3.setRuleId(3);
    vatRuleEntity3.setStatus("INACTIVE");
    vatCacheRepository.saveAll(
        List.of(vatRuleEntity1, vatRuleEntity2, vatRuleEntity3));

    //Act
    List<VatRuleEntity> foundRules = vatCacheRepository.findAllByStatusActive();

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(vatRuleEntity1, vatRuleEntity2));
  }

  @Test
  void findAllUpdatedAfter_shouldReturnAListOfRecordsWithUpdate() {
    //Arrange
    VatRuleEntity vatRuleEntity1 = createVatRuleEntity();
    vatRuleEntity1.setRuleId(1);
    vatRuleEntity1.setStatus("ACTIVE");
    vatRuleEntity1.setLastModifiedAt(LocalDateTime.now().plusMinutes(1)
        .truncatedTo(ChronoUnit.MILLIS));
    VatRuleEntity vatRuleEntity2 = createVatRuleEntity();
    vatRuleEntity2.setRuleId(2);
    vatRuleEntity2.setStatus("ACTIVE");
    vatRuleEntity2.setLastModifiedAt(LocalDateTime.now().plusMinutes(1)
        .truncatedTo(ChronoUnit.MILLIS));
    VatRuleEntity vatRuleEntity3 = createVatRuleEntity();
    vatRuleEntity3.setRuleId(3);
    vatRuleEntity3.setStatus("ACTIVE");
    vatCacheRepository.saveAll(
        List.of(vatRuleEntity1, vatRuleEntity2, vatRuleEntity3));

    //Act
    List<VatRuleEntity> foundRules = vatCacheRepository.findAllUpdatedAfter(
        LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(vatRuleEntity1, vatRuleEntity2));
  }

  private VatRuleEntity createVatRuleEntity() {
    VatRuleEntity vatRuleEntity = new VatRuleEntity();
    vatRuleEntity.setRefRuleId(null);
    vatRuleEntity.setStatus(ACTIVE.name());
    vatRuleEntity.setCreatedAt(NOW);
    vatRuleEntity.setLastModifiedAt(NOW);
    vatRuleEntity.setEnableTimestamp(NOW);
    vatRuleEntity.setDisableTimestamp(NOW);
    vatRuleEntity.setVatRegion(VAT_REGION);
    vatRuleEntity.setPkgCode(PKG_CODE);
    vatRuleEntity.setTranCode(TRAN_CODE);
    return vatRuleEntity;
  }

}
