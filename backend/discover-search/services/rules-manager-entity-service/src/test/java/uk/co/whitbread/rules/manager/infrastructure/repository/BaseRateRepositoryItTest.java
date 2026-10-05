package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.BaseRateRuleEntity;

class BaseRateRepositoryItTest extends AbstractIntegrationTest{
  
  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
  
  @Autowired
  private BaseRateRepository baseRateRepository;
  
  @BeforeEach
  void setUp() {
    baseRateRepository.deleteAll();
  }
  
  @AfterEach
  void tearDown() {
    baseRateRepository.deleteAll();
  }
  
  @Test
  void save__shouldSaveInDb() {
    BaseRateRuleEntity entity = createBaseRateRuleEntity("");
    var savedEntity = baseRateRepository.save(entity);
    
    assertThat(savedEntity).usingRecursiveComparison().isEqualTo(entity);
  }
  
  @Test
  void findAllByStatusEqualsIgnoreCase_shouldReturnAListOfRecordsWithSameStatus() {
    BaseRateRuleEntity entity1 = createBaseRateRuleEntity("1");
    entity1.setStatus("Searched Status");
    BaseRateRuleEntity entity2 = createBaseRateRuleEntity("2");
    entity2.setStatus("searched status");
    BaseRateRuleEntity entity3 = createBaseRateRuleEntity("3");
    entity3.setStatus("another status");
    baseRateRepository.saveAll(List.of(entity1,entity2,entity3));
    
    List<BaseRateRuleEntity> foundRules = baseRateRepository.findAllByStatusEqualsIgnoreCase("SEARCHED STATUS");
    
    assertThat(foundRules).usingRecursiveComparison().isEqualTo(List.of(entity1, entity2));
  }
  
  @Test
  void findAllByStatusAndDisableTimestampIsBefore_shouldReturnRulesThatNeedsToBeDisable() {
    BaseRateRuleEntity entity1 = createBaseRateRuleEntity("1");
    entity1.setStatus("Searched Status");
    entity1.setDisableTimestamp(NOW.plusDays(1));
    BaseRateRuleEntity entity2 = createBaseRateRuleEntity("2");
    entity2.setStatus("searched status");
    entity2.setDisableTimestamp(NOW.minusDays(1));
    BaseRateRuleEntity entity3 = createBaseRateRuleEntity("3");
    entity3.setStatus("another status");
    entity3.setDisableTimestamp(NOW.plusDays(1));
    baseRateRepository.saveAll(List.of(entity1,entity2,entity3));
    
    List<BaseRateRuleEntity> foundRules = baseRateRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore("SEARCHED STATUS", LocalDateTime.now());
    
    assertThat(foundRules).usingRecursiveComparison().isEqualTo(List.of(entity2));
  }
  
  @Test
  void findAllByStatusAndEnableTimestampIsBefore_shouldReturnRulesThatNeedsToBeEnable() {
    BaseRateRuleEntity entity1 = createBaseRateRuleEntity("1");
    entity1.setStatus("Searched Status");
    entity1.setEnableTimestamp(NOW.minusDays(1));
    BaseRateRuleEntity entity2 = createBaseRateRuleEntity("2");
    entity2.setStatus("searched status");
    entity2.setEnableTimestamp(NOW.plusDays(1));
    BaseRateRuleEntity entity3 = createBaseRateRuleEntity("3");
    entity3.setStatus("another status");
    entity3.setEnableTimestamp(NOW.minusDays(1));
    baseRateRepository.saveAll(List.of(entity1,entity2,entity3));
    
    List<BaseRateRuleEntity> foundRules = baseRateRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore("SEARCHED STATUS", LocalDateTime.now());
    
    assertThat(foundRules).usingRecursiveComparison().isEqualTo(List.of(entity1));
  }
  
  private BaseRateRuleEntity createBaseRateRuleEntity(String id) {
    BaseRateRuleEntity entity = new BaseRateRuleEntity();
    entity.setRefRuleId(null);
    entity.setStatus("STATUS");
    entity.setCreatedAt(NOW);
    entity.setLastModifiedAt(NOW);
    entity.setEnableTimestamp(NOW);
    entity.setDisableTimestamp(NOW);
    entity.setBaseRate("FLEXRATE");
    entity.setRatePlanCode("BUSIFLEX");
    entity.setPromoCode("BUSIFLEX");
    return entity;
  }
  
}
