package uk.co.whitbread.rules.agent.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RbacRuleEntity;

class RbacRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  public RbacCacheRepository rbacCacheRepository;

  @BeforeEach
  void setUp() {
    rbacCacheRepository.deleteAll();
  }

  @Test
  void findAllByStatusActive__shouldReturnAListOfRecordsWithStatusActive() {
    //Arrange
    RbacRuleEntity rbacRuleEntity1 = createRbacRuleEntity();
    rbacRuleEntity1.setRuleId(1);
    rbacRuleEntity1.setStatus("ACTIVE");
    RbacRuleEntity rbacRuleEntity2 = createRbacRuleEntity();
    rbacRuleEntity2.setRuleId(2);
    rbacRuleEntity2.setStatus("ACTIVE");
    RbacRuleEntity rbacRuleEntity3 = createRbacRuleEntity();
    rbacRuleEntity3.setRuleId(3);
    rbacRuleEntity3.setStatus("INACTIVE");
    rbacCacheRepository.saveAll(
        List.of(rbacRuleEntity1, rbacRuleEntity2, rbacRuleEntity3));

    //Act
    List<RbacRuleEntity> foundRules = rbacCacheRepository.findAllByStatusActive();

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(rbacRuleEntity1, rbacRuleEntity2));
  }

  @Test
  void findAllUpdatedAfter_shouldReturnAListOfRecordsWithUpdate() {
    //Arrange
    RbacRuleEntity rbacRuleEntity1 = createRbacRuleEntity();
    rbacRuleEntity1.setRuleId(1);
    rbacRuleEntity1.setStatus("ACTIVE");
    rbacRuleEntity1.setLastModifiedAt(
        LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    RbacRuleEntity rbacRuleEntity2 = createRbacRuleEntity();
    rbacRuleEntity2.setRuleId(2);
    rbacRuleEntity2.setStatus("ACTIVE");
    rbacRuleEntity2.setLastModifiedAt(
        LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    RbacRuleEntity rbacRuleEntity3 = createRbacRuleEntity();
    rbacRuleEntity3.setRuleId(3);
    rbacRuleEntity3.setStatus("ACTIVE");
    rbacCacheRepository.saveAll(
        List.of(rbacRuleEntity1, rbacRuleEntity2, rbacRuleEntity3));

    //Act
    List<RbacRuleEntity> foundRules = rbacCacheRepository.findAllUpdatedAfter(
        LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(rbacRuleEntity1, rbacRuleEntity2));
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
