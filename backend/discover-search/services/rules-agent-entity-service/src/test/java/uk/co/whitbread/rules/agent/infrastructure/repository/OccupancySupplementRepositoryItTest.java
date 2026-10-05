package uk.co.whitbread.rules.agent.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.OccupancySupplementEntity;

class OccupancySupplementRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  public OccupancySupplementRepository occupancySupplementRepository;

  @BeforeEach
  void setUp() {
    occupancySupplementRepository.deleteAll();
  }

  @Test
  void findAllByStatusActive__shouldReturnAListOfRecordsWithStatusActive() {
    //Arrange
    var supplementEntity1 = createSupplementRule();
    supplementEntity1.setRuleId(1);
    supplementEntity1.setHotelId("A1");
    supplementEntity1.setStatus("ACTIVE");
    var supplementEntity2 = createSupplementRule();
    supplementEntity2.setRuleId(2);
    supplementEntity2.setHotelId("A2");
    supplementEntity2.setStatus("ACTIVE");
    var supplementEntity3 = createSupplementRule();
    supplementEntity3.setRuleId(3);
    supplementEntity3.setHotelId("A3");
    supplementEntity3.setStatus("INACTIVE");
    occupancySupplementRepository.saveAll(
        List.of(supplementEntity1, supplementEntity2, supplementEntity3));

    //Act
    var foundRules = occupancySupplementRepository.findAllByStatusActive();

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(supplementEntity1, supplementEntity2));
  }

  @Test
  void findAllUpdatedAfter_shouldReturnAListOfRecordsWithUpdate() {
    //Arrange
    var supplementEntity1 = createSupplementRule();
    supplementEntity1.setRuleId(1);
    supplementEntity1.setStatus("ACTIVE");
    supplementEntity1.setLastModifiedAt(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    var supplementEntity2 = createSupplementRule();
    supplementEntity2.setRuleId(2);
    supplementEntity2.setStatus("ACTIVE");
    supplementEntity2.setLastModifiedAt(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    var supplementEntity3 = createSupplementRule();
    supplementEntity3.setRuleId(3);
    supplementEntity3.setStatus("ACTIVE");
    occupancySupplementRepository.saveAll(
        List.of(supplementEntity1, supplementEntity2, supplementEntity3));

    //Act
    var foundRules = occupancySupplementRepository.findAllUpdatedAfter(
        LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(supplementEntity1, supplementEntity2));
  }

  private OccupancySupplementEntity createSupplementRule() {
    OccupancySupplementEntity supplementEntity = new OccupancySupplementEntity();
    supplementEntity.setRefRuleId(null);
    supplementEntity.setStatus("STATUS");
    supplementEntity.setCreatedAt(NOW);
    supplementEntity.setLastModifiedAt(NOW);
    supplementEntity.setEnableTimestamp(NOW);
    supplementEntity.setDisableTimestamp(NOW);
    supplementEntity.setHotelId("hotelID");
    supplementEntity.setPricing(BigDecimal.valueOf(9.69));
    return supplementEntity;
  }
}
