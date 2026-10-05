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
import uk.co.whitbread.rules.agent.domain.model.in.MaxNightsRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.MaxNightsRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.MaxNightsRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxNightsRuleEntity;

@ExtendWith(MockitoExtension.class)
class MaxNightsRepositoryCacheOutPortImplTest {

  @Mock
  private MaxNightsCacheRepository maxNightsCacheRepository;

  @InjectMocks
  private MaxNightsRepositoryCacheOutPortImpl maxNightsRepositoryCacheOutPort;

  @Mock
  private MaxNightsRuleEntityMapper maxNightsRuleEntityMapper;

  @Test
  void cacheRules__shouldCacheAllActiveRules() throws IllegalAccessException {
    //Arrange
    var rre1 = createMaxNightsRuleEntity(1, "ACTIVE");
    var rre2 = createMaxNightsRuleEntity(2, "ACTIVE");
    when(maxNightsCacheRepository.findAllByStatusActive()).thenReturn(List.of(rre1, rre2));
    Map<Integer, MaxNightsRuleEntity> expectedCachedRules = Map.of(1, rre1, 2, rre2);

    //Act
    maxNightsRepositoryCacheOutPort.cacheRules();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(maxNightsRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        maxNightsRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(maxNightsCacheRepository).findAllByStatusActive();
    verifyNoMoreInteractions(maxNightsCacheRepository);
  }

  @Test
  void updateCache__shouldUpdateCachedRules() throws IllegalAccessException {
    //Arrange
    var maxNightsRuleEntity1 = createMaxNightsRuleEntity(1, "ACTIVE");
    var maxNightsRuleEntity2 = createMaxNightsRuleEntity(2, "ACTIVE");
    when(maxNightsCacheRepository.findAllByStatusActive()).thenReturn(
        List.of(maxNightsRuleEntity1, maxNightsRuleEntity2));
    maxNightsRepositoryCacheOutPort.cacheRules();
    var maxNightsRuleEntityInactive = createMaxNightsRuleEntity(1, "INACTIVE");
    var maxNightsRuleEntity3 = createMaxNightsRuleEntity(3, "ACTIVE");
    when(maxNightsCacheRepository.findAllUpdatedAfter(any())).thenReturn(
        List.of(maxNightsRuleEntityInactive, maxNightsRuleEntity3));
    Map<Integer, MaxNightsRuleEntity> expectedCachedRules = Map.of(2, maxNightsRuleEntity2, 3,
        maxNightsRuleEntity3);

    //Act
    maxNightsRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(maxNightsRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        maxNightsRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(maxNightsCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(maxNightsCacheRepository);
  }

  @Test
  void updateCache__shouldNotUpdateForUnknownStatus() throws IllegalAccessException {
    //Arrange
    var maxNightsRuleEntity1 = createMaxNightsRuleEntity(1, "THIS WOULD BE SOMETHING RANDOM");
    var maxNightsRuleEntity2 = createMaxNightsRuleEntity(2, "ACTIVE");
    when(maxNightsCacheRepository.findAllUpdatedAfter(any())).thenReturn(
        List.of(maxNightsRuleEntity1, maxNightsRuleEntity2));
    var expectedCachedRules = Map.of(2, maxNightsRuleEntity2);

    //Act
    maxNightsRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(maxNightsRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    verify(maxNightsCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(maxNightsCacheRepository);
  }

  @Test
  void findMaxNightsRule__shouldReturnOk() throws IllegalAccessException {
    //Arrange
    var maxNightsEntity1 = createMaxNightsRuleEntity(1, 9, "PI", 1);
    var maxNightsEntity2 = createMaxNightsRuleEntity(2, 9, "BB", 2);
    var maxNightsEntity3 = createMaxNightsRuleEntity(3, 14, "CCUI", 3);
    var maxNightsRuleDomain = createMaxNightsRuleDomain(maxNightsEntity1);
    setCachedRules(List.of(maxNightsEntity1, maxNightsEntity2, maxNightsEntity3));
    when(maxNightsRuleEntityMapper.toModel(maxNightsEntity1)).thenReturn(maxNightsRuleDomain);

    //Act
    var response = maxNightsRepositoryCacheOutPort.findRule("PI");

    //Assert
    assertThat(response.get()).isNotNull();
    assertThat(response.get().getChannelId()).isEqualTo("PI");

    verifyNoMoreInteractions(maxNightsRuleEntityMapper);
    verifyNoInteractions(maxNightsCacheRepository);
  }

  @Test
  void findMaxNightsRule_withResourceId_shouldReturnOptionalEmtpy() throws IllegalAccessException {
    //Arrange
    var maxNightsEntity1 = createMaxNightsRuleEntity(1, 9, "PI", 1);
    var maxNightsEntity2 = createMaxNightsRuleEntity(2, 9, "BB", 2);
    var maxNightsEntity3 = createMaxNightsRuleEntity(3, 14, "CCUI", 3);
    var maxNightsRequest = createMaxNightsRequest("BB");
    setCachedRules(List.of(maxNightsEntity1, maxNightsEntity2, maxNightsEntity3));

    //Act
    var response = maxNightsRepositoryCacheOutPort.findRule(maxNightsRequest.getChannelId());

    //Assert
    assertThat(response).isNotPresent();

    verifyNoMoreInteractions(maxNightsRuleEntityMapper);
    verifyNoInteractions(maxNightsCacheRepository);
  }

  @Test
  void findMaxNightsRule_withRoleID_shouldReturnOptionalEmtpy() throws IllegalAccessException {
    //Arrange
    var maxNightsEntity1 = createMaxNightsRuleEntity(1, 9, "PI", 1);
    var maxNightsEntity2 = createMaxNightsRuleEntity(2, 9, "BB", 2);
    var maxNightsEntity3 = createMaxNightsRuleEntity(3, 14, "CCUI", 3);
    var maxNightsRequest = createMaxNightsRequest("CCUI");
    setCachedRules(List.of(maxNightsEntity1, maxNightsEntity2, maxNightsEntity3));

    //Act
    var response = maxNightsRepositoryCacheOutPort.findRule(maxNightsRequest.getChannelId());

    //Assert
    assertThat(response).isNotPresent();

    verifyNoMoreInteractions(maxNightsRuleEntityMapper);
    verifyNoInteractions(maxNightsCacheRepository);
  }

  private MaxNightsRuleEntity createMaxNightsRuleEntity(Integer ruleId, String status) {
    var MaxNightsRuleEntity = new MaxNightsRuleEntity();
    MaxNightsRuleEntity.setRuleId(ruleId);
    MaxNightsRuleEntity.setStatus(status);
    return MaxNightsRuleEntity;
  }

  private MaxNightsRuleRequest createMaxNightsRequest(String channelId) {
    return MaxNightsRuleRequest.builder()
        .channelId(channelId)
        .build();
  }

  private MaxNightsRuleEntity createMaxNightsRuleEntity(Integer ruleId, Integer maxNights,
      String channelId, Integer delayTime) {
    var maxNightsRuleEntity = new MaxNightsRuleEntity();
    var time = LocalDateTime.now();
    maxNightsRuleEntity.setRuleId(ruleId);
    maxNightsRuleEntity.setMaxNights(maxNights);
    maxNightsRuleEntity.setChannelId("PI");
    maxNightsRuleEntity.setLastModifiedAt(time.minusMinutes(delayTime));
    return maxNightsRuleEntity;
  }

  private MaxNightsRule createMaxNightsRuleDomain(MaxNightsRuleEntity MaxNightsRuleEntity) {
    var time = LocalDateTime.now();
    return MaxNightsRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(time)
        .lastModifiedAt(time)
        .channelId(MaxNightsRuleEntity.getChannelId())
        .maxNights(MaxNightsRuleEntity.getMaxNights())
        .build();
  }

  private void setCachedRules(List<MaxNightsRuleEntity> entities) throws IllegalAccessException {
    Map<Integer, MaxNightsRuleEntity> cachedRules = (Map<Integer, MaxNightsRuleEntity>) FieldUtils.readField(
        maxNightsRepositoryCacheOutPort,
        "cachedRules",
        true);
    entities.forEach(entity -> cachedRules.put(entity.getRuleId(), entity));
  }
}
