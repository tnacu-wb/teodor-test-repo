package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.PaypalRuleEntity;

class PaypalRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  private PaypalRepository paypalRepository;

  @BeforeEach
  void setUp() {
    paypalRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    paypalRepository.deleteAll();
  }

  @Test
  void save__shouldSaveInDb() {
    //Arrange
    PaypalRuleEntity paypalRuleEntity = createPaypalRuleEntity();

    //Act
    PaypalRuleEntity savedPaypalRuleEntity = paypalRepository.save(paypalRuleEntity);

    //Assert
    assertThat(savedPaypalRuleEntity).usingRecursiveComparison()
        .isEqualTo(paypalRuleEntity);
  }

  @Test
  void findAllByStatusEqualsIgnoreCase__shouldReturnAListOfRecordsWithSameStatus() {
    //Arrange
    PaypalRuleEntity paypalRuleEntity1 = createPaypalRuleEntity();
    paypalRuleEntity1.setStatus("Searched Status");
    PaypalRuleEntity paypalRuleEntity2 = createPaypalRuleEntity();
    paypalRuleEntity2.setStatus("searched status");
    PaypalRuleEntity paypalRuleEntity3 = createPaypalRuleEntity();
    paypalRuleEntity3.setStatus("another status");
    paypalRepository.saveAll(
        List.of(paypalRuleEntity1, paypalRuleEntity2, paypalRuleEntity3));

    //Act
    List<PaypalRuleEntity> foundRules = paypalRepository.findAllByStatusEqualsIgnoreCase(
        "SEARCHED STATUS");

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(paypalRuleEntity1, paypalRuleEntity2));
  }

  @Test
  void findAllByStatusAndDisableTimestampIsBefore__shouldReturnRulesThatNeedsToBeDisable() {
    //Arrange
    PaypalRuleEntity paypalRuleEntity1 = createPaypalRuleEntity();
    paypalRuleEntity1.setStatus("Searched Status");
    paypalRuleEntity1.setDisableTimestamp(NOW.plusDays(1));
    PaypalRuleEntity paypalRuleEntity2 = createPaypalRuleEntity();
    paypalRuleEntity2.setStatus("searched status");
    paypalRuleEntity2.setDisableTimestamp(NOW.minusDays(1));
    PaypalRuleEntity paypalRuleEntity3 = createPaypalRuleEntity();
    paypalRuleEntity3.setStatus("another status");
    paypalRuleEntity3.setDisableTimestamp(NOW.plusDays(1));
    paypalRepository.saveAll(
        List.of(paypalRuleEntity1, paypalRuleEntity2, paypalRuleEntity3));

    //Act
    List<PaypalRuleEntity> foundRules = paypalRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore("SEARCHED STATUS",
            LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(paypalRuleEntity2));
  }

  @Test
  void findAllByStatusAndEnableTimestampIsBefore__shouldReturnRulesThatNeedsToBeEnable() {
    //Arrange
    PaypalRuleEntity paypalRuleEntity1 = createPaypalRuleEntity();
    paypalRuleEntity1.setStatus("Searched Status");
    paypalRuleEntity1.setEnableTimestamp(NOW.minusDays(1));
    PaypalRuleEntity paypalRuleEntity2 = createPaypalRuleEntity();
    paypalRuleEntity2.setStatus("searched status");
    paypalRuleEntity2.setEnableTimestamp(NOW.plusDays(1));
    PaypalRuleEntity paypalRuleEntity3 = createPaypalRuleEntity();
    paypalRuleEntity3.setStatus("another status");
    paypalRuleEntity3.setEnableTimestamp(NOW.minusDays(1));
    paypalRepository.saveAll(
        List.of(paypalRuleEntity1, paypalRuleEntity2, paypalRuleEntity3));

    //Act
    List<PaypalRuleEntity> foundRules = paypalRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore("SEARCHED STATUS",
            LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(paypalRuleEntity1));
  }

  private PaypalRuleEntity createPaypalRuleEntity() {
    PaypalRuleEntity paypalRuleEntity = new PaypalRuleEntity();
    paypalRuleEntity.setRefRuleId(null);
    paypalRuleEntity.setStatus("STATUS");
    paypalRuleEntity.setCreatedAt(NOW);
    paypalRuleEntity.setLastModifiedAt(NOW);
    paypalRuleEntity.setEnableTimestamp(NOW);
    paypalRuleEntity.setDisableTimestamp(NOW);
    paypalRuleEntity.setChannelId("channel id");
    paypalRuleEntity.setCountry("GB");
    paypalRuleEntity.setHotelIdList("hotelID");
    return paypalRuleEntity;
  }

}

