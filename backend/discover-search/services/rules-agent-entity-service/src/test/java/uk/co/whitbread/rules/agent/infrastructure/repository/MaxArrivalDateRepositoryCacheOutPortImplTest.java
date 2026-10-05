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
import uk.co.whitbread.rules.agent.domain.model.in.MaxArrivalDateRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.MaxArrivalDateRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.MaxArrivalDateRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxArrivalDateRuleEntity;

@ExtendWith(MockitoExtension.class)
class MaxArrivalDateRepositoryCacheOutPortImplTest {

  @Mock
  private MaxArrivalDateCacheRepository maxArrivalDateCacheRepository;

  @InjectMocks
  private MaxArrivalDateRepositoryCacheOutPortImpl maxArrivalDateRepositoryCacheOutPort;

  @Mock
  private MaxArrivalDateRuleEntityMapper maxArrivalDateRuleEntityMapper;

  @Test
  void cacheRules__shouldCacheAllActiveRules() throws IllegalAccessException {
    //Arrange
    var rre1 = createMaxArrivalDateRuleEntity(1, "ACTIVE");
    var rre2 = createMaxArrivalDateRuleEntity(2, "ACTIVE");
    when(maxArrivalDateCacheRepository.findAllByStatusActive()).thenReturn(List.of(rre1, rre2));
    Map<Integer, MaxArrivalDateRuleEntity> expectedCachedRules = Map.of(1, rre1, 2, rre2);

    //Act
    maxArrivalDateRepositoryCacheOutPort.cacheRules();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(maxArrivalDateRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        maxArrivalDateRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(maxArrivalDateCacheRepository).findAllByStatusActive();
    verifyNoMoreInteractions(maxArrivalDateCacheRepository);
  }

  @Test
  void updateCache__shouldUpdateCachedRules() throws IllegalAccessException {
    //Arrange
    var maxArrivalDateRuleEntity1 = createMaxArrivalDateRuleEntity(1, "ACTIVE");
    var maxArrivalDateRuleEntity2 = createMaxArrivalDateRuleEntity(2, "ACTIVE");
    when(maxArrivalDateCacheRepository.findAllByStatusActive()).thenReturn(
        List.of(maxArrivalDateRuleEntity1, maxArrivalDateRuleEntity2));
    maxArrivalDateRepositoryCacheOutPort.cacheRules();
    var maxArrivalDateRuleEntityInactive = createMaxArrivalDateRuleEntity(1, "INACTIVE");
    var maxArrivalDateRuleEntity3 = createMaxArrivalDateRuleEntity(3, "ACTIVE");
    when(maxArrivalDateCacheRepository.findAllUpdatedAfter(any())).thenReturn(
        List.of(maxArrivalDateRuleEntityInactive, maxArrivalDateRuleEntity3));
    Map<Integer, MaxArrivalDateRuleEntity> expectedCachedRules = Map.of(2, maxArrivalDateRuleEntity2, 3,
        maxArrivalDateRuleEntity3);

    //Act
    maxArrivalDateRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(maxArrivalDateRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        maxArrivalDateRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(maxArrivalDateCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(maxArrivalDateCacheRepository);
  }

  @Test
  void updateCache__shouldNotUpdateForUnknownStatus() throws IllegalAccessException {
    //Arrange
    var maxArrivalDateRuleEntity1 = createMaxArrivalDateRuleEntity(1, "THIS WOULD BE SOMETHING RANDOM");
    var maxArrivalDateRuleEntity2 = createMaxArrivalDateRuleEntity(2, "ACTIVE");
    when(maxArrivalDateCacheRepository.findAllUpdatedAfter(any())).thenReturn(
        List.of(maxArrivalDateRuleEntity1, maxArrivalDateRuleEntity2));
    var expectedCachedRules = Map.of(2, maxArrivalDateRuleEntity2);

    //Act
    maxArrivalDateRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(maxArrivalDateRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    verify(maxArrivalDateCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(maxArrivalDateCacheRepository);
  }

  @Test
  void findMaxArrivalDateRule__shouldReturnOk() throws IllegalAccessException {
    //Arrange
    var maxArrivalDateEntity1 = createMaxArrivalDateRuleEntity(1, 9, "PI", 1);
    var maxArrivalDateEntity2 = createMaxArrivalDateRuleEntity(2, 9, "BB", 2);
    var maxArrivalDateEntity3 = createMaxArrivalDateRuleEntity(3, 14, "CCUI", 3);
    var maxArrivalDateRuleDomain = createMaxArrivalDateRuleDomain(maxArrivalDateEntity1);
    setCachedRules(List.of(maxArrivalDateEntity1, maxArrivalDateEntity2, maxArrivalDateEntity3));
    when(maxArrivalDateRuleEntityMapper.toModel(maxArrivalDateEntity1)).thenReturn(maxArrivalDateRuleDomain);

    //Act
    var response = maxArrivalDateRepositoryCacheOutPort.findRule("PI");

    //Assert
    assertThat(response.get()).isNotNull();
    assertThat(response.get().getChannelId()).isEqualTo("PI");

    verifyNoMoreInteractions(maxArrivalDateRuleEntityMapper);
    verifyNoInteractions(maxArrivalDateCacheRepository);
  }

  @Test
  void findMaxArrivalDateRule_withResourceId_shouldReturnOptionalEmtpy() throws IllegalAccessException {
    //Arrange
    var maxArrivalDateEntity1 = createMaxArrivalDateRuleEntity(1, 9, "PI", 1);
    var maxArrivalDateEntity2 = createMaxArrivalDateRuleEntity(2, 9, "BB", 2);
    var maxArrivalDateEntity3 = createMaxArrivalDateRuleEntity(3, 14, "CCUI", 3);
    var maxArrivalDateRequest = createMaxArrivalDateRequest("BB");
    setCachedRules(List.of(maxArrivalDateEntity1, maxArrivalDateEntity2, maxArrivalDateEntity3));

    //Act
    var response = maxArrivalDateRepositoryCacheOutPort.findRule(maxArrivalDateRequest.getChannelId());

    //Assert
    assertThat(response).isEmpty();

    verifyNoMoreInteractions(maxArrivalDateRuleEntityMapper);
    verifyNoInteractions(maxArrivalDateCacheRepository);
  }

  @Test
  void findMaxArrivalDateRule_withRoleID_shouldReturnOptionalEmtpy() throws IllegalAccessException {
    //Arrange
    var maxArrivalDateEntity1 = createMaxArrivalDateRuleEntity(1, 9, "PI", 1);
    var maxArrivalDateEntity2 = createMaxArrivalDateRuleEntity(2, 9, "BB", 2);
    var maxArrivalDateEntity3 = createMaxArrivalDateRuleEntity(3, 14, "CCUI", 3);
    var maxArrivalDateRequest = createMaxArrivalDateRequest("CCUI");
    setCachedRules(List.of(maxArrivalDateEntity1, maxArrivalDateEntity2, maxArrivalDateEntity3));

    //Act
    var response = maxArrivalDateRepositoryCacheOutPort.findRule(maxArrivalDateRequest.getChannelId());

    //Assert
    assertThat(response).isEmpty();

    verifyNoMoreInteractions(maxArrivalDateRuleEntityMapper);
    verifyNoInteractions(maxArrivalDateCacheRepository);
  }

  private MaxArrivalDateRuleEntity createMaxArrivalDateRuleEntity(Integer ruleId, String status) {
    var MaxArrivalDateRuleEntity = new MaxArrivalDateRuleEntity();
    MaxArrivalDateRuleEntity.setRuleId(ruleId);
    MaxArrivalDateRuleEntity.setStatus(status);
    return MaxArrivalDateRuleEntity;
  }

  private MaxArrivalDateRuleRequest createMaxArrivalDateRequest(String channelId) {
    return MaxArrivalDateRuleRequest.builder()
        .channelId(channelId)
        .build();
  }

  private MaxArrivalDateRuleEntity createMaxArrivalDateRuleEntity(Integer ruleId, Integer maxArrivalDate,
      String channelId, Integer delayTime) {
    var maxArrivalDateRuleEntity = new MaxArrivalDateRuleEntity();
    var time = LocalDateTime.now();
    maxArrivalDateRuleEntity.setRuleId(ruleId);
    maxArrivalDateRuleEntity.setMaxArrivalDate(maxArrivalDate);
    maxArrivalDateRuleEntity.setChannelId("PI");
    maxArrivalDateRuleEntity.setLastModifiedAt(time.minusMinutes(delayTime));
    return maxArrivalDateRuleEntity;
  }

  private MaxArrivalDateRule createMaxArrivalDateRuleDomain(MaxArrivalDateRuleEntity MaxArrivalDateRuleEntity) {
    var time = LocalDateTime.now();
    return MaxArrivalDateRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(time)
        .lastModifiedAt(time)
        .channelId(MaxArrivalDateRuleEntity.getChannelId())
        .maxArrivalDate(MaxArrivalDateRuleEntity.getMaxArrivalDate())
        .build();
  }

  private void setCachedRules(List<MaxArrivalDateRuleEntity> entities) throws IllegalAccessException {
    Map<Integer, MaxArrivalDateRuleEntity> cachedRules = (Map<Integer, MaxArrivalDateRuleEntity>) FieldUtils.readField(
        maxArrivalDateRepositoryCacheOutPort,
        "cachedRules",
        true);
    entities.forEach(entity -> cachedRules.put(entity.getRuleId(), entity));
  }
}
