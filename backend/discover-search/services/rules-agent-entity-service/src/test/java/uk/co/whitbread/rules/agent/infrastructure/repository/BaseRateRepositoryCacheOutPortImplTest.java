package uk.co.whitbread.rules.agent.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.BaseRateRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.BaseRateRuleEntity;

@ExtendWith(MockitoExtension.class)
public class BaseRateRepositoryCacheOutPortImplTest {

  @InjectMocks
  private BaseRateRepositoryCacheOutPortImpl baseRateRepositoryCacheOutPort;
  @Mock
  private BaseRateCacheRepository baseRateCacheRepository;
  @Mock
  private BaseRateRuleEntityMapper baseRateRuleEntityMapper;

  @Test
  void cacheRules__shouldCacheAllActiveRules() throws IllegalAccessException {
    //Arrange
    var are1 = createBaseRateRuleEntity(1, "ACTIVE");
    var are2 = createBaseRateRuleEntity(2, "ACTIVE");
    when(baseRateCacheRepository.findAllByStatusActive()).thenReturn(List.of(are1, are2));
    Map<Integer, BaseRateRuleEntity> expectedCachedRules = Map.of(1, are1, 2, are2);

    //Act
    baseRateRepositoryCacheOutPort.cacheRules();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(baseRateRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        baseRateRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(baseRateCacheRepository).findAllByStatusActive();
    verifyNoMoreInteractions(baseRateCacheRepository);
  }

  @Test
  void updateCache__shouldUpdateCachedRules() throws IllegalAccessException {
    //Arrange
    var are1 = createBaseRateRuleEntity(1, "ACTIVE");
    var are2 = createBaseRateRuleEntity(2, "ACTIVE");
    when(baseRateCacheRepository.findAllByStatusActive()).thenReturn(List.of(are1, are2));
    baseRateRepositoryCacheOutPort.cacheRules();
    var are1inactive = createBaseRateRuleEntity(1, "INACTIVE");
    var are3 = createBaseRateRuleEntity(3, "ACTIVE");
    when(baseRateCacheRepository.findAllUpdatedAfter(any())).thenReturn(
        List.of(are1inactive, are3));
    Map<Integer, BaseRateRuleEntity> expectedCachedRules = Map.of(2, are2, 3, are3);

    //Act
    baseRateRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(baseRateRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        baseRateRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(baseRateCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(baseRateCacheRepository);
  }

  @Test
  void updateCache__shouldNotUpdateForUnknownStatus() throws IllegalAccessException {
    //Arrange
    var are1 = createBaseRateRuleEntity(1, "THIS WOULD BE SOMETHING RANDOM");
    var are2 = createBaseRateRuleEntity(2, "ACTIVE");
    when(baseRateCacheRepository.findAllUpdatedAfter(any())).thenReturn(List.of(are1, are2));
    var expectedCachedRules = Map.of(2, are2);

    //Act
    baseRateRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(baseRateRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    verify(baseRateCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(baseRateCacheRepository);
  }

  @Test
  void findBaseRateRule__shouldReturnOk() throws IllegalAccessException {
    var entity1 = createBaseRateRuleEntity(1, "ACTIVE");
    setCachedRules(List.of(entity1));

    var actualRule = baseRateRepositoryCacheOutPort.getBaseRate("FLEXRATE");

    assertThat(actualRule).isNotNull();
    verifyNoMoreInteractions(baseRateRuleEntityMapper);
    verifyNoInteractions(baseRateCacheRepository);
  }

  private BaseRateRuleEntity createBaseRateRuleEntity(Integer ruleId, String status) {
    var baseRateRuleEntity = new BaseRateRuleEntity();
    baseRateRuleEntity.setStatus(status);
    baseRateRuleEntity.setRuleId(ruleId);
    return baseRateRuleEntity;
  }

  private void setCachedRules(List<BaseRateRuleEntity> entities) throws IllegalAccessException {
    Map<Integer, BaseRateRuleEntity> cachedRules = (Map<Integer, BaseRateRuleEntity>) FieldUtils.readField(
        baseRateRepositoryCacheOutPort,
        "cachedRules",
        true);
    entities.forEach(entity -> cachedRules.put(entity.getRuleId(), entity));
  }
}
