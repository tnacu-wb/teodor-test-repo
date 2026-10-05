package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.RbacRuleEntity;

class RbacRuleRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  private RbacRuleRepository rbacRuleRepository;

  @BeforeEach
  void setUp() {
    rbacRuleRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    rbacRuleRepository.deleteAll();
  }

  @Test
  void save__shouldSaveInDb() {
    //Arrange
    RbacRuleEntity rbacRuleEntity = createRbacRuleEntity();

    //Act
    RbacRuleEntity savedRbacRuleEntity = rbacRuleRepository.save(rbacRuleEntity);

    //Assert
    assertThat(savedRbacRuleEntity).usingRecursiveComparison()
        .isEqualTo(rbacRuleEntity);
  }

  @Test
  void findAllByStatusEqualsIgnoreCase__shouldReturnAListOfRecordsWithSameStatus() {
    //Arrange
    RbacRuleEntity rbacRuleEntity1 = createRbacRuleEntity();
    rbacRuleEntity1.setStatus("Searched Status");
    RbacRuleEntity rbacRuleEntity2 = createRbacRuleEntity();
    rbacRuleEntity2.setStatus("searched status");
    RbacRuleEntity rbacRuleEntity3 = createRbacRuleEntity();
    rbacRuleEntity3.setStatus("another status");
    rbacRuleRepository.saveAll(
        List.of(rbacRuleEntity1, rbacRuleEntity2, rbacRuleEntity3));

    //Act
    List<RbacRuleEntity> foundRules = rbacRuleRepository.findAllByStatusEqualsIgnoreCase(
        "SEARCHED STATUS");

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(rbacRuleEntity1, rbacRuleEntity2));
  }

  @Test
  void findAllByStatusAndDisableTimestampIsBefore__shouldReturnRulesThatNeedsToBeDisable() {
    //Arrange
    RbacRuleEntity rbacRuleEntity1 = createRbacRuleEntity();
    rbacRuleEntity1.setStatus("Searched Status");
    rbacRuleEntity1.setDisableTimestamp(NOW.plusDays(1));
    RbacRuleEntity rbacRuleEntity2 = createRbacRuleEntity();
    rbacRuleEntity2.setStatus("searched status");
    rbacRuleEntity2.setDisableTimestamp(NOW.minusDays(1));
    RbacRuleEntity rbacRuleEntity3 = createRbacRuleEntity();
    rbacRuleEntity3.setStatus("another status");
    rbacRuleEntity3.setDisableTimestamp(NOW.plusDays(1));
    rbacRuleRepository.saveAll(
        List.of(rbacRuleEntity1, rbacRuleEntity2, rbacRuleEntity3));

    //Act
    List<RbacRuleEntity> foundRules = rbacRuleRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore("SEARCHED STATUS",
            LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(rbacRuleEntity2));
  }

  @Test
  void findAllByStatusAndEnableTimestampIsBefore__shouldReturnRulesThatNeedsToBeEnable() {
    //Arrange
    RbacRuleEntity rbacRuleEntity1 = createRbacRuleEntity();
    rbacRuleEntity1.setStatus("Searched Status");
    rbacRuleEntity1.setEnableTimestamp(NOW.minusDays(1));
    RbacRuleEntity rbacRuleEntity2 = createRbacRuleEntity();
    rbacRuleEntity2.setStatus("searched status");
    rbacRuleEntity2.setEnableTimestamp(NOW.plusDays(1));
    RbacRuleEntity rbacRuleEntity3 = createRbacRuleEntity();
    rbacRuleEntity3.setStatus("another status");
    rbacRuleEntity3.setEnableTimestamp(NOW.minusDays(1));
    rbacRuleRepository.saveAll(
        List.of(rbacRuleEntity1, rbacRuleEntity2, rbacRuleEntity3));

    //Act
    List<RbacRuleEntity> foundRules = rbacRuleRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore("SEARCHED STATUS",
            LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(rbacRuleEntity1));
  }

  private RbacRuleEntity createRbacRuleEntity() {
    RbacRuleEntity rbacRuleEntity = new RbacRuleEntity();
    rbacRuleEntity.setRefRuleId(null);
    rbacRuleEntity.setStatus("STATUS");
    rbacRuleEntity.setCreatedAt(NOW);
    rbacRuleEntity.setLastModifiedAt(NOW);
    rbacRuleEntity.setEnableTimestamp(NOW);
    rbacRuleEntity.setDisableTimestamp(NOW);
    rbacRuleEntity.setResourceId("resource id");
    rbacRuleEntity.setRoleId("role id");
    rbacRuleEntity.setHasAccess(true);
    return rbacRuleEntity;
  }
}
