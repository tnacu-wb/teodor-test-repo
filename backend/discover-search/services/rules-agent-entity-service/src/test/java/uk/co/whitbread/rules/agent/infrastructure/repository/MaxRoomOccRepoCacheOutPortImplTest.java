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
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomOccupancyRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.MaxRoomOccupancyRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxRoomOccupancyRuleEntity;

@ExtendWith(MockitoExtension.class)
class MaxRoomOccRepoCacheOutPortImplTest {

  @InjectMocks
  MaxRoomOccupancyRuleRepoOutPortImpl ruleRepoOutPort;

  @Mock
  MaxRoomOccupancyCacheRepository maxRoomOccupancyCacheRepository;

  @Mock
  MaxRoomOccupancyRuleEntityMapper amendmentRuleEntityMapper;

  @Test
  void cacheRules__shouldCacheAllActiveRules() throws IllegalAccessException {
    //Arrange
    var ror1 = createRoomOccupancyRuleEntity(1, "ACTIVE");
    var ror2 = createRoomOccupancyRuleEntity(2, "ACTIVE");
    when(maxRoomOccupancyCacheRepository.findAllByStatusActive()).thenReturn(List.of(ror1, ror2));
    Map<Integer, MaxRoomOccupancyRuleEntity> expectedCachedRules = Map.of(1, ror1, 2, ror2);

    //Act
    ruleRepoOutPort.cacheRules();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(ruleRepoOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        ruleRepoOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(maxRoomOccupancyCacheRepository).findAllByStatusActive();
    verifyNoMoreInteractions(maxRoomOccupancyCacheRepository);
  }

  @Test
  void updateCache__shouldUpdateCachedRules() throws IllegalAccessException {
    //Arrange
    var ror1 = createRoomOccupancyRuleEntity(1, "ACTIVE");
    var ror2 = createRoomOccupancyRuleEntity(2, "ACTIVE");
    when(maxRoomOccupancyCacheRepository.findAllByStatusActive()).thenReturn(List.of(ror1, ror2));
    ruleRepoOutPort.cacheRules();
    var ror1inactive = createRoomOccupancyRuleEntity(1, "INACTIVE");
    var ror3 = createRoomOccupancyRuleEntity(3, "ACTIVE");
    when(maxRoomOccupancyCacheRepository.findAllUpdatedAfter(any())).thenReturn(List.of(ror1inactive, ror3));
    Map<Integer, MaxRoomOccupancyRuleEntity> expectedCachedRules = Map.of(2, ror2, 3, ror3);

    //Act
    ruleRepoOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(ruleRepoOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        ruleRepoOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(maxRoomOccupancyCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(maxRoomOccupancyCacheRepository);
  }

  @Test
  void updateCache__shouldNotUpdateForUnknownStatus() throws IllegalAccessException {
    //Arrange
    var ror1 = createRoomOccupancyRuleEntity(1, "THIS WOULD BE SOMETHING RANDOM");
    var ror2 = createRoomOccupancyRuleEntity(2, "ACTIVE");
    when(maxRoomOccupancyCacheRepository.findAllUpdatedAfter(any())).thenReturn(List.of(ror1, ror2));
    var expectedCachedRules = Map.of(2, ror2);

    //Act
    ruleRepoOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(ruleRepoOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    verify(maxRoomOccupancyCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(maxRoomOccupancyCacheRepository);
  }

  @Test
  void findRule__shouldFilterByChannelIdAndBrandWhenFoundInTheDatabase() throws IllegalAccessException {
    //Arrange
    var dummyEntity1 = createDummyEntity(1,  "c1", "b1");
    var dummyEntity2 = createDummyEntity(2, "c2", "b1");
    var dummyEntity3 = createDummyEntity(3, "c3", "b1");
    setCachedRules(List.of(dummyEntity1, dummyEntity2, dummyEntity3));
    var dummyRule1 = createDummyRule("c1", "b1");
    when(amendmentRuleEntityMapper.toModel(dummyEntity1)).thenReturn(dummyRule1);

    //Act
    var actualRule = ruleRepoOutPort.findRules("c1", "b1");

    //Assert
    assertThat(actualRule).isNotNull();
    verifyNoMoreInteractions(amendmentRuleEntityMapper);
    verifyNoInteractions(maxRoomOccupancyCacheRepository);
  }

  @Test
  void findRule__shouldFilterByChannelIdNullAndBrandWhenNotFoundInTheDatabase()
      throws IllegalAccessException {
    //Arrange
    var dummyEntity1 = createDummyEntity(1, null, "b1");
    var dummyEntity2 = createDummyEntity(2, "c2", "b1");
    var dummyEntity3 = createDummyEntity(3, "c3", "b1");
    setCachedRules(List.of(dummyEntity1, dummyEntity2, dummyEntity3));
    //Act
    var actualRule = ruleRepoOutPort.findRules("c1", "b1");

    //Assert
    assertThat(actualRule).isNotNull();
    verifyNoMoreInteractions(amendmentRuleEntityMapper);
    verifyNoInteractions(maxRoomOccupancyCacheRepository);
  }

  @Test
  void findRule__shouldReturnNothingIfChannelIdNotFoundWhenFilterByChannelIdAndBrand()
      throws IllegalAccessException {
    //Arrange
    var dummyEntity1 = createDummyEntity(1,  "c1", "b1");
    var dummyEntity2 = createDummyEntity(2,  "c2", "b1");
    var dummyEntity3 = createDummyEntity(3,  "c3", "b1");
    setCachedRules(List.of(dummyEntity1, dummyEntity2, dummyEntity3));

    //Act
    var actualRule = ruleRepoOutPort.findRules("c4", "b1");

    //Assert
    assertThat(actualRule.isEmpty());
    verifyNoInteractions(amendmentRuleEntityMapper);
    verifyNoInteractions(maxRoomOccupancyCacheRepository);
  }

  @Test
  void findRule__shouldReturnNothingIfBrandNotFoundWhenFilterByChannelIdAndBrand()
      throws IllegalAccessException {
    //Arrange
    var dummyEntity1 = createDummyEntity(1,  "c1", "b1");
    var dummyEntity2 = createDummyEntity(2,  "c2", "b1");
    var dummyEntity3 = createDummyEntity(3,  "c3", "b1");
    setCachedRules(List.of(dummyEntity1, dummyEntity2, dummyEntity3));

    //Act
    var actualRule = ruleRepoOutPort.findRules("c1", "b2");

    //Assert
    assertThat(actualRule.isEmpty());
    verifyNoInteractions(amendmentRuleEntityMapper);
    verifyNoInteractions(maxRoomOccupancyCacheRepository);
  }

  @Test
  void findRule__shouldReturnTheLastUpdatedRuleWhenDuplicateRulesFound()
      throws IllegalAccessException {
    //Arrange
    var dummyEntity1 = createDummyEntity(1, "c1", "b1");
    var dummyEntity2 = createDummyEntity(2, "c1", "b1");
    var now = LocalDateTime.now();
    dummyEntity1.setLastModifiedAt(now.minusMinutes(1));
    dummyEntity2.setLastModifiedAt(now.minusMinutes(2));
    setCachedRules(List.of(dummyEntity1, dummyEntity2));
    var dummyRule1 = createDummyRule("c1", "b1");
    dummyRule1.setLastModifiedAt(now.minusMinutes(1));
    when(amendmentRuleEntityMapper.toModel(dummyEntity1)).thenReturn(dummyRule1);

    //Act
    var actualRule = ruleRepoOutPort.findRules("c1", "b1");

    //Assert
    assertThat(actualRule).isNotNull();
    verifyNoMoreInteractions(amendmentRuleEntityMapper);
    verifyNoInteractions(maxRoomOccupancyCacheRepository);
  }

  private MaxRoomOccupancyRuleEntity createRoomOccupancyRuleEntity(Integer ruleId, String status) {
    var time = LocalDateTime.now();
    var roomOccupancyRuleEntity = new MaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity.setRuleId(ruleId);
    roomOccupancyRuleEntity.setCreatedAt(time);
    roomOccupancyRuleEntity.setLastModifiedAt(time);
    roomOccupancyRuleEntity.setStatus(status);
    roomOccupancyRuleEntity.setChannelId("CCUI");
    roomOccupancyRuleEntity.setAdults(1);
    roomOccupancyRuleEntity.setChildren(0);
    roomOccupancyRuleEntity.setSingleRoom(true);
    roomOccupancyRuleEntity.setDoubleRoom(true);
    roomOccupancyRuleEntity.setTwinRoom(false);
    roomOccupancyRuleEntity.setAccessibleRoom(true);
    roomOccupancyRuleEntity.setFamilyRoom(false);
    return roomOccupancyRuleEntity;
  }

  private MaxRoomOccupancyRuleEntity createDummyEntity(Integer ruleId, String channelId, String brand) {
    var dummyEntity = new MaxRoomOccupancyRuleEntity();
    dummyEntity.setRuleId(ruleId);
    dummyEntity.setLastModifiedAt(LocalDateTime.now());
    dummyEntity.setChannelId(channelId);
    dummyEntity.setBrand(brand);
    return dummyEntity;
  }

  private MaxRoomOccupancyRule createDummyRule(String channelId, String brand) {
    var time = LocalDateTime.now();
    return MaxRoomOccupancyRule.builder()
        .ruleId(1)
        .createdAt(time)
        .lastModifiedAt(time)
        .status(RuleStatus.ACTIVE)
        .channelId(channelId)
        .adults(1)
        .children(0)
        .singleRoom(true)
        .doubleRoom(true)
        .twinRoom(false)
        .accessibleRoom(true)
        .familyRoom(false)
        .brand(brand)
        .build();
  }

  private void setCachedRules(List<MaxRoomOccupancyRuleEntity> entities) throws IllegalAccessException {
    Map<Integer, MaxRoomOccupancyRuleEntity> cachedRules = (Map<Integer, MaxRoomOccupancyRuleEntity>) FieldUtils.readField(
        ruleRepoOutPort,
        "cachedRules",
        true);
    entities.forEach(entity -> cachedRules.put(entity.getRuleId(), entity));
  }

}
