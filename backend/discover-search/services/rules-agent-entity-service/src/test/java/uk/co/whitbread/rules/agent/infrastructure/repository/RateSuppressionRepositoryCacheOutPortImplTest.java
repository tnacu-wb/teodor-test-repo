package uk.co.whitbread.rules.agent.infrastructure.repository;

import static java.util.List.of;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.out.RateSuppressionRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.RateSuppressionRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RateSuppressionRuleEntity;

@ExtendWith(MockitoExtension.class)
class RateSuppressionRepositoryCacheOutPortImplTest {

  @Mock
  private RateSuppressionCacheRepository rateSuppressionCacheRepository;

  @InjectMocks
  private RateSuppressionRepositoryCacheOutPortImpl rateSuppressionRepositoryCacheOutPort;

  @Mock
  private RateSuppressionRuleEntityMapper rateSuppressionRuleEntityMapper;

  @Test
  void cacheRules__shouldCacheAllActiveRules() throws IllegalAccessException {
    // Arrange
    var rateSuppressionRuleEntity1 = createRateSuppressionRuleEntity(1, "ACTIVE");
    var rateSuppressionRuleEntity2 = createRateSuppressionRuleEntity(2, "ACTIVE");
    when(rateSuppressionCacheRepository.findAllByStatusActive()).thenReturn(
        List.of(rateSuppressionRuleEntity1, rateSuppressionRuleEntity2));

    Map<Integer, RateSuppressionRuleEntity> expectedCachedRules = Map.of(1,
        rateSuppressionRuleEntity1, 2,
        rateSuppressionRuleEntity2);

    //Act
    rateSuppressionRepositoryCacheOutPort.cacheRules();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(rateSuppressionRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        rateSuppressionRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(rateSuppressionCacheRepository).findAllByStatusActive();
    verifyNoMoreInteractions(rateSuppressionCacheRepository);
  }

  @Test
  void updateCache__shouldUpdateCachedRules() throws IllegalAccessException {
    // Arrange
    var rateSuppressionRuleEntity1 = createRateSuppressionRuleEntity(1, "ACTIVE");
    var rateSuppressionRuleEntity2 = createRateSuppressionRuleEntity(2, "ACTIVE");
    when(rateSuppressionCacheRepository.findAllByStatusActive()).thenReturn(
        List.of(rateSuppressionRuleEntity1, rateSuppressionRuleEntity2));
    rateSuppressionRepositoryCacheOutPort.cacheRules();

    var rateSuppressionRuleEntityInactive1 = createRateSuppressionRuleEntity(1, "INACTIVE");
    var rateSuppressionRuleEntity3 = createRateSuppressionRuleEntity(3, "ACTIVE");

    when(rateSuppressionCacheRepository.findAllUpdatedAfter(any())).thenReturn(
        List.of(rateSuppressionRuleEntityInactive1, rateSuppressionRuleEntity3));

    Map<Integer, RateSuppressionRuleEntity> expectedCachedRules = Map.of(2,
        rateSuppressionRuleEntity2, 3, rateSuppressionRuleEntity3);

    //Act
    rateSuppressionRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(rateSuppressionRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        rateSuppressionRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(rateSuppressionCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(rateSuppressionCacheRepository);

  }

  @Test
  void updateCache__shouldNotUpdateForUnknownStatus() throws IllegalAccessException {
    // Arrange
    var rateSuppressionRuleEntity1 = createRateSuppressionRuleEntity(1, "NON-EXISTENT STATUS");
    var rateSuppressionRuleEntity2 = createRateSuppressionRuleEntity(2, "ACTIVE");

    when(rateSuppressionCacheRepository.findAllUpdatedAfter(any())).thenReturn(
        List.of(rateSuppressionRuleEntity1, rateSuppressionRuleEntity2));
    var expectedCachedRules = Map.of(2, rateSuppressionRuleEntity2);

    //Act
    rateSuppressionRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(rateSuppressionRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    verify(rateSuppressionCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(rateSuppressionCacheRepository);
  }

  @Test
  void findRateSuppressionRules__shouldReturnOk() throws IllegalAccessException {
    // Arrange
    var rateSuppressionRuleEntity1 = createRateSuppressionRuleEntity(1, "ACTIVE");
    var rateSuppressionRuleEntity2 = createRateSuppressionRuleEntity(2, "ACTIVE");

    var rateSuppressionRuleDomain1 = createRoomSubstitutionRuleDomain(rateSuppressionRuleEntity1);
    var rateSuppressionRuleDomain2 = createRoomSubstitutionRuleDomain(rateSuppressionRuleEntity2);

    setCachedRules(
        of(rateSuppressionRuleEntity1, rateSuppressionRuleEntity2));

    doReturn(rateSuppressionRuleDomain1).when(rateSuppressionRuleEntityMapper).toModel(rateSuppressionRuleEntity1);
    doReturn(rateSuppressionRuleDomain2).when(rateSuppressionRuleEntityMapper).toModel(rateSuppressionRuleEntity2);

    //Act
    var response = rateSuppressionRepositoryCacheOutPort.findRateSuppressionRule();

    //Assert
    assertFalse(response.isEmpty());
    assertThat(response.get(0).getRateType()).isEqualTo(
        rateSuppressionRuleEntity1.getRateType());
    assertThat(response.get(0).getPriority()).isEqualTo(
        rateSuppressionRuleEntity1.getPriority());
    assertThat(response.get(1).getRateType()).isEqualTo(
        rateSuppressionRuleEntity2.getRateType());
    assertThat(response.get(1).getPriority()).isEqualTo(
        rateSuppressionRuleEntity2.getPriority());

    verifyNoMoreInteractions(rateSuppressionRuleEntityMapper);
    verifyNoInteractions(rateSuppressionCacheRepository);
  }

  @Test
  void findRateSuppressionRules__shouldThrowException() throws IllegalAccessException {
    // Arrange
    setCachedRules(
        Collections.emptyList());

    //Assert
    assertThrows(RuleEngineException.class,
        () -> rateSuppressionRepositoryCacheOutPort.findRateSuppressionRule(),
        "Rate Suppression Rules not found.");

    verifyNoMoreInteractions(rateSuppressionRuleEntityMapper);
    verifyNoInteractions(rateSuppressionCacheRepository);
  }

  private RateSuppressionRuleEntity createRateSuppressionRuleEntity(Integer ruleId,
      String status) {
    var time = LocalDateTime.now();
    var rateSuppressionRuleEntity = new RateSuppressionRuleEntity();
    rateSuppressionRuleEntity.setRuleId(ruleId);
    rateSuppressionRuleEntity.setStatus(status);
    rateSuppressionRuleEntity.setCreatedAt(time);
    rateSuppressionRuleEntity.setLastModifiedAt(time);
    rateSuppressionRuleEntity.setRateType("FLEX");
    rateSuppressionRuleEntity.setPriority((short) 10);
    return rateSuppressionRuleEntity;
  }

  private RateSuppressionRule createRoomSubstitutionRuleDomain(
      RateSuppressionRuleEntity roomSubstitutionRuleEntity) {
    var time = LocalDateTime.now();
    return RateSuppressionRule.builder()
        .rateType(roomSubstitutionRuleEntity.getRateType())
        .priority(roomSubstitutionRuleEntity.getPriority())
        .ruleId(roomSubstitutionRuleEntity.getRuleId())
        .createdAt(time)
        .lastModifiedAt(time)
        .status(RuleStatus.ACTIVE)
        .build();

  }

  private void setCachedRules(List<RateSuppressionRuleEntity> entities)
      throws IllegalAccessException {
    Map<Integer, RateSuppressionRuleEntity> cachedRules = (Map<Integer, RateSuppressionRuleEntity>) FieldUtils.readField(
        rateSuppressionRepositoryCacheOutPort,
        "cachedRules",
        true);
    entities.forEach(entity -> cachedRules.put(entity.getRuleId(), entity));
  }
}
