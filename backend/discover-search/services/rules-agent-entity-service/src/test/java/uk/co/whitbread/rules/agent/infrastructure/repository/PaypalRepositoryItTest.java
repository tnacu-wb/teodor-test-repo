package uk.co.whitbread.rules.agent.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.PaypalRuleEntity;

class PaypalRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  public PaypalCacheRepository paypalCacheRepository;

  @BeforeEach
  void setUp() {
    paypalCacheRepository.deleteAll();
  }

  @Test
  void findAllByStatusActive__shouldReturnAListOfRecordsWithStatusActive() {
    //Arrange
    PaypalRuleEntity paypalRuleEntity1 = createPaypalRuleEntity();
    paypalRuleEntity1.setRuleId(1);
    paypalRuleEntity1.setStatus("ACTIVE");
    PaypalRuleEntity paypalRuleEntity2 = createPaypalRuleEntity();
    paypalRuleEntity2.setRuleId(2);
    paypalRuleEntity2.setStatus("ACTIVE");
    PaypalRuleEntity paypalRuleEntity3 = createPaypalRuleEntity();
    paypalRuleEntity3.setRuleId(3);
    paypalRuleEntity3.setStatus("INACTIVE");
    paypalCacheRepository.saveAll(
        List.of(paypalRuleEntity1, paypalRuleEntity2, paypalRuleEntity3));

    //Act
    List<PaypalRuleEntity> foundRules = paypalCacheRepository.findAllByStatusActive();

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(paypalRuleEntity1, paypalRuleEntity2));
  }

  @Test
  void findAllUpdatedAfter_shouldReturnAListOfRecordsWithUpdate() {
    //Arrange
    PaypalRuleEntity paypalRuleEntity1 = createPaypalRuleEntity();
    paypalRuleEntity1.setRuleId(1);
    paypalRuleEntity1.setStatus("ACTIVE");
    paypalRuleEntity1.setLastModifiedAt(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    PaypalRuleEntity paypalRuleEntity2 = createPaypalRuleEntity();
    paypalRuleEntity2.setRuleId(2);
    paypalRuleEntity2.setStatus("ACTIVE");
    paypalRuleEntity2.setLastModifiedAt(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    PaypalRuleEntity paypalRuleEntity3 = createPaypalRuleEntity();
    paypalRuleEntity3.setRuleId(3);
    paypalRuleEntity3.setStatus("ACTIVE");
    paypalCacheRepository.saveAll(
        List.of(paypalRuleEntity1, paypalRuleEntity2, paypalRuleEntity3));

    //Act
    List<PaypalRuleEntity> foundRules = paypalCacheRepository.findAllUpdatedAfter(
        LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(paypalRuleEntity1, paypalRuleEntity2));
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
    paypalRuleEntity.setCountryCode("GB");
    paypalRuleEntity.setHotelId("hotelID");
    return paypalRuleEntity;
  }
}
