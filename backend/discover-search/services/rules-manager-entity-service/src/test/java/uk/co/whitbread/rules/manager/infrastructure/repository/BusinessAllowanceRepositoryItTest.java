package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.BusinessAllowanceRuleEntity;

class BusinessAllowanceRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  private BusinessAllowanceRuleRepository businessAllowanceRuleRepository;

  @BeforeEach
  void setUp() {
    businessAllowanceRuleRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    businessAllowanceRuleRepository.deleteAll();
  }

  @Test
  void save_shouldSaveInDb() {
    //Arrange
    BusinessAllowanceRuleEntity businessAllowanceRuleEntity = createBusinessAllowanceRuleEntity("");

    //Act
    var savedBusinessAllowanceRuleEntity =
        businessAllowanceRuleRepository.save(businessAllowanceRuleEntity);

    //Assert
    assertThat(savedBusinessAllowanceRuleEntity).usingRecursiveComparison()
        .isEqualTo(businessAllowanceRuleEntity);
    
  }

  @Test
  void findAllByStatusEqualsIgnoreCase_shouldReturnAListOfRecordsWithSameStatus() {
    //Arrange
    var businessAllowanceRuleEntity1 = createBusinessAllowanceRuleEntity("1");
    businessAllowanceRuleEntity1.setStatus("Searched Status");
    var businessAllowanceRuleEntity2 = createBusinessAllowanceRuleEntity("2");
    businessAllowanceRuleEntity2.setStatus("searched status");
    var businessAllowanceRuleEntity3 = createBusinessAllowanceRuleEntity("3");
    businessAllowanceRuleEntity3.setStatus("another status");
    businessAllowanceRuleRepository.saveAll(
        List.of(businessAllowanceRuleEntity1, businessAllowanceRuleEntity2,
            businessAllowanceRuleEntity3));

    //Act
    List<BusinessAllowanceRuleEntity> foundRules =
        businessAllowanceRuleRepository.findAllByStatusEqualsIgnoreCase("SEARCHED STATUS");

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(businessAllowanceRuleEntity1, businessAllowanceRuleEntity2));
  }

  @Test
  void findAllByStatusAndDisableTimestampIsBefore_shouldReturnRulesThatNeedsToBeDisable() {
    //Arrange
    var businessAllowanceRuleEntity1 = createBusinessAllowanceRuleEntity("1");
    businessAllowanceRuleEntity1.setStatus("Searched Status");
    businessAllowanceRuleEntity1.setDisableTimestamp(NOW.plusDays(1));
    var businessAllowanceRuleEntity2 = createBusinessAllowanceRuleEntity("2");
    businessAllowanceRuleEntity2.setStatus("searched status");
    businessAllowanceRuleEntity2.setDisableTimestamp(NOW.minusDays(1));
    var businessAllowanceRuleEntity3 = createBusinessAllowanceRuleEntity("3");
    businessAllowanceRuleEntity3.setStatus("another status");
    businessAllowanceRuleEntity3.setDisableTimestamp(NOW.plusDays(1));
    businessAllowanceRuleRepository.saveAll(
        List.of(businessAllowanceRuleEntity1, businessAllowanceRuleEntity2,
            businessAllowanceRuleEntity3));

    //Act
    List<BusinessAllowanceRuleEntity> foundRules =
        businessAllowanceRuleRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
            "SEARCHED STATUS", LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(businessAllowanceRuleEntity2));
  }

  @Test
  void findAllByStatusAndEnableTimestampIsBefore_shouldReturnRulesThatNeedsToBeEnable() {
    //Arrange
    var businessAllowanceRuleEntity1 = createBusinessAllowanceRuleEntity("1");
    businessAllowanceRuleEntity1.setStatus("Searched Status");
    businessAllowanceRuleEntity1.setEnableTimestamp(NOW.minusDays(1));
    var businessAllowanceRuleEntity2 = createBusinessAllowanceRuleEntity("2");
    businessAllowanceRuleEntity2.setStatus("searched status");
    businessAllowanceRuleEntity2.setEnableTimestamp(NOW.plusDays(1));
    var businessAllowanceRuleEntity3 = createBusinessAllowanceRuleEntity("3");
    businessAllowanceRuleEntity3.setStatus("another status");
    businessAllowanceRuleEntity3.setEnableTimestamp(NOW.minusDays(1));
    businessAllowanceRuleRepository.saveAll(
        List.of(businessAllowanceRuleEntity1, businessAllowanceRuleEntity2,
            businessAllowanceRuleEntity3));

    //Act
    List<BusinessAllowanceRuleEntity> foundRules =
        businessAllowanceRuleRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
            "SEARCHED STATUS", LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(businessAllowanceRuleEntity1));
  }

  private BusinessAllowanceRuleEntity createBusinessAllowanceRuleEntity(String id) {
    var businessAllowanceRuleEntity = new BusinessAllowanceRuleEntity();
    businessAllowanceRuleEntity.setRefRuleId(null);
    businessAllowanceRuleEntity.setStatus("STATUS");
    businessAllowanceRuleEntity.setCreatedAt(NOW);
    businessAllowanceRuleEntity.setLastModifiedAt(NOW);
    businessAllowanceRuleEntity.setEnableTimestamp(NOW);
    businessAllowanceRuleEntity.setDisableTimestamp(NOW);
    businessAllowanceRuleEntity.setPms("OP");
    businessAllowanceRuleEntity.setSourceId("dinner" + id);
    businessAllowanceRuleEntity.setTargetId("156" + id);
    businessAllowanceRuleEntity.setAemId("boxedBreakfast");
    businessAllowanceRuleEntity.setIsApplicableDaily(true);

    return businessAllowanceRuleEntity;
  }

}
