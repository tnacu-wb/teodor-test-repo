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
import uk.co.whitbread.rules.agent.domain.model.in.MaxRoomsRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomsRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.MaxRoomsRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxRoomsRuleEntity;

@ExtendWith(MockitoExtension.class)
class MaxRoomsRepositoryCacheOutPortImplTest {

  @Mock
  private MaxRoomsCacheRepository maxRoomsCacheRepository;

  @InjectMocks
  private MaxRoomsRepositoryCacheOutPortImpl maxRoomsRepositoryCacheOutPort;

  @Mock
  private MaxRoomsRuleEntityMapper maxRoomsRuleEntityMapper;

  @Test
  void cacheRules__shouldCacheAllActiveRules() throws IllegalAccessException {
    //Arrange
    var rre1 = createMaxRoomsRuleEntity(1, "ACTIVE");
    var rre2 = createMaxRoomsRuleEntity(2, "ACTIVE");
    when(maxRoomsCacheRepository.findAllByStatusActive()).thenReturn(List.of(rre1, rre2));
    Map<Integer, MaxRoomsRuleEntity> expectedCachedRules = Map.of(1, rre1, 2, rre2);

    //Act
    maxRoomsRepositoryCacheOutPort.cacheRules();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(maxRoomsRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        maxRoomsRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(maxRoomsCacheRepository).findAllByStatusActive();
    verifyNoMoreInteractions(maxRoomsCacheRepository);
  }

  @Test
  void updateCache__shouldUpdateCachedRules() throws IllegalAccessException {
    //Arrange
    var maxRoomsRuleEntity1 = createMaxRoomsRuleEntity(1, "ACTIVE");
    var maxRoomsRuleEntity2 = createMaxRoomsRuleEntity(2, "ACTIVE");
    when(maxRoomsCacheRepository.findAllByStatusActive()).thenReturn(
        List.of(maxRoomsRuleEntity1, maxRoomsRuleEntity2));
    maxRoomsRepositoryCacheOutPort.cacheRules();
    var maxRoomsRuleEntityInactive = createMaxRoomsRuleEntity(1, "INACTIVE");
    var maxRoomsRuleEntity3 = createMaxRoomsRuleEntity(3, "ACTIVE");
    when(maxRoomsCacheRepository.findAllUpdatedAfter(any())).thenReturn(
        List.of(maxRoomsRuleEntityInactive, maxRoomsRuleEntity3));
    Map<Integer, MaxRoomsRuleEntity> expectedCachedRules = Map.of(2, maxRoomsRuleEntity2, 3,
        maxRoomsRuleEntity3);

    //Act
    maxRoomsRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(maxRoomsRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        maxRoomsRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(maxRoomsCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(maxRoomsCacheRepository);
  }

  @Test
  void updateCache__shouldNotUpdateForUnknownStatus() throws IllegalAccessException {
    //Arrange
    var maxRoomsRuleEntity1 = createMaxRoomsRuleEntity(1, "THIS WOULD BE SOMETHING RANDOM");
    var maxRoomsRuleEntity2 = createMaxRoomsRuleEntity(2, "ACTIVE");
    when(maxRoomsCacheRepository.findAllUpdatedAfter(any())).thenReturn(
        List.of(maxRoomsRuleEntity1, maxRoomsRuleEntity2));
    var expectedCachedRules = Map.of(2, maxRoomsRuleEntity2);

    //Act
    maxRoomsRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(maxRoomsRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    verify(maxRoomsCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(maxRoomsCacheRepository);
  }

  @Test
  void findMaxRoomsRule__shouldReturnOk() throws IllegalAccessException {
    //Arrange
    var maxRoomsEntity1 = createMaxRoomsRuleEntity(1, 9, "PI", 1);
    var maxRoomsEntity2 = createMaxRoomsRuleEntity(2, 9, "BB", 2);
    var maxRoomsEntity3 = createMaxRoomsRuleEntity(3, 14, "CCUI", 3);
    var maxRoomsRuleDomain = createMaxRoomsRuleDomain(maxRoomsEntity1);
    setCachedRules(List.of(maxRoomsEntity1, maxRoomsEntity2, maxRoomsEntity3));
    when(maxRoomsRuleEntityMapper.toModel(maxRoomsEntity1)).thenReturn(maxRoomsRuleDomain);

    //Act
    var response = maxRoomsRepositoryCacheOutPort.findRule("PI");

    //Assert
    assertThat(response.get()).isNotNull();
    assertThat(response.get().getChannelId()).isEqualTo("PI");

    verifyNoMoreInteractions(maxRoomsRuleEntityMapper);
    verifyNoInteractions(maxRoomsCacheRepository);
  }

  @Test
  void findMaxRoomsRule_withResourceId_shouldReturnOptionalEmtpy() throws IllegalAccessException {
    //Arrange
    var maxRoomsEntity1 = createMaxRoomsRuleEntity(1, 9, "PI", 1);
    var maxRoomsEntity2 = createMaxRoomsRuleEntity(2, 9, "BB", 2);
    var maxRoomsEntity3 = createMaxRoomsRuleEntity(3, 14, "CCUI", 3);
    var maxRoomsRequest = createMaxRoomsRequest("BB");
    setCachedRules(List.of(maxRoomsEntity1, maxRoomsEntity2, maxRoomsEntity3));

    //Act
    var response = maxRoomsRepositoryCacheOutPort.findRule(maxRoomsRequest.getChannelId());

    //Assert
    assertThat(response).isEmpty();

    verifyNoMoreInteractions(maxRoomsRuleEntityMapper);
    verifyNoInteractions(maxRoomsCacheRepository);
  }

  @Test
  void findMaxRoomsRule_withRoleID_shouldReturnOptionalEmtpy() throws IllegalAccessException {
    //Arrange
    var maxRoomsEntity1 = createMaxRoomsRuleEntity(1, 9, "PI", 1);
    var maxRoomsEntity2 = createMaxRoomsRuleEntity(2, 9, "BB", 2);
    var maxRoomsEntity3 = createMaxRoomsRuleEntity(3, 14, "CCUI", 3);
    var maxRoomsRequest = createMaxRoomsRequest("CCUI");
    setCachedRules(List.of(maxRoomsEntity1, maxRoomsEntity2, maxRoomsEntity3));

    //Act
    var response = maxRoomsRepositoryCacheOutPort.findRule(maxRoomsRequest.getChannelId());

    //Assert
    assertThat(response).isEmpty();

    verifyNoMoreInteractions(maxRoomsRuleEntityMapper);
    verifyNoInteractions(maxRoomsCacheRepository);
  }

  private MaxRoomsRuleEntity createMaxRoomsRuleEntity(Integer ruleId, String status) {
    var MaxRoomsRuleEntity = new MaxRoomsRuleEntity();
    MaxRoomsRuleEntity.setRuleId(ruleId);
    MaxRoomsRuleEntity.setStatus(status);
    return MaxRoomsRuleEntity;
  }

  private MaxRoomsRuleRequest createMaxRoomsRequest(String channelId) {
    return MaxRoomsRuleRequest.builder()
        .channelId(channelId)
        .build();
  }

  private MaxRoomsRuleEntity createMaxRoomsRuleEntity(Integer ruleId, Integer maxRooms,
      String channelId, Integer delayTime) {
    var maxRoomsRuleEntity = new MaxRoomsRuleEntity();
    var time = LocalDateTime.now();
    maxRoomsRuleEntity.setRuleId(ruleId);
    maxRoomsRuleEntity.setMaxRooms(maxRooms);
    maxRoomsRuleEntity.setChannelId("PI");
    maxRoomsRuleEntity.setLastModifiedAt(time.minusMinutes(delayTime));
    return maxRoomsRuleEntity;
  }

  private MaxRoomsRule createMaxRoomsRuleDomain(MaxRoomsRuleEntity MaxRoomsRuleEntity) {
    var time = LocalDateTime.now();
    return MaxRoomsRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(time)
        .lastModifiedAt(time)
        .channelId(MaxRoomsRuleEntity.getChannelId())
        .maxRooms(MaxRoomsRuleEntity.getMaxRooms())
        .build();
  }

  private void setCachedRules(List<MaxRoomsRuleEntity> entities) throws IllegalAccessException {
    Map<Integer, MaxRoomsRuleEntity> cachedRules = (Map<Integer, MaxRoomsRuleEntity>) FieldUtils.readField(
        maxRoomsRepositoryCacheOutPort,
        "cachedRules",
        true);
    entities.forEach(entity -> cachedRules.put(entity.getRuleId(), entity));
  }
}
