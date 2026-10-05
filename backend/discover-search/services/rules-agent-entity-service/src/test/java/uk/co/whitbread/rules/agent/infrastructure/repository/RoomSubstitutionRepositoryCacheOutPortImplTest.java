package uk.co.whitbread.rules.agent.infrastructure.repository;

import static java.util.List.of;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitutionRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.RoomSubstitutionRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RoomSubstitutionRuleEntity;

@ExtendWith(MockitoExtension.class)
class RoomSubstitutionRepositoryCacheOutPortImplTest {

  private static final String DOUBLE = "DB";
  private static final String FAMILY = "FAM";
  private static final String SINGLE = "SB";
  private static final String TWIN = "TWIN";
  private static final String OP = "OP";
  private static final String CHANNEL = "PI";

  @Mock
  private RoomSubstitutionCacheRepository roomSubstitutionCacheRepository;

  @InjectMocks
  private RoomSubstitutionRepositoryCacheOutPortImpl roomSubstitutionRepositoryCacheOutPort;

  @Mock
  private RoomSubstitutionRuleEntityMapper roomSubstitutionRuleEntityMapper;

  @Test
  void cacheRules__shouldCacheAllActiveRules() throws IllegalAccessException {
    //Arrange
    var roomSubstitutionRuleEntity1 = createRoomSubstitutionRuleEntity(1, "ACTIVE");
    var roomSubstitutionRuleEntity2 = createRoomSubstitutionRuleEntity(2, "ACTIVE");
    when(roomSubstitutionCacheRepository.findAllByStatusActive()).thenReturn(
        of(roomSubstitutionRuleEntity1, roomSubstitutionRuleEntity2));
    Map<Integer, RoomSubstitutionRuleEntity> expectedCachedRules = Map.of(1,
        roomSubstitutionRuleEntity1, 2,
        roomSubstitutionRuleEntity2);

    //Act
    roomSubstitutionRepositoryCacheOutPort.cacheRules();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(roomSubstitutionRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        roomSubstitutionRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(roomSubstitutionCacheRepository).findAllByStatusActive();
    verifyNoMoreInteractions(roomSubstitutionCacheRepository);
  }

  @Test
  void updateCache__shouldUpdateCachedRules() throws IllegalAccessException {
    //Arrange
    var roomSubstitutionRuleEntity1 = createRoomSubstitutionRuleEntity(1, "ACTIVE");
    var roomSubstitutionRuleEntity2 = createRoomSubstitutionRuleEntity(2, "ACTIVE");
    when(roomSubstitutionCacheRepository.findAllByStatusActive()).thenReturn(
        of(roomSubstitutionRuleEntity1, roomSubstitutionRuleEntity2));
    roomSubstitutionRepositoryCacheOutPort.cacheRules();
    var roomSubstitutionRuleEntity1Inactive = createRoomSubstitutionRuleEntity(1, "INACTIVE");
    var roomSubstitutionRuleEntity3 = createRoomSubstitutionRuleEntity(3, "ACTIVE");
    when(roomSubstitutionCacheRepository.findAllUpdatedAfter(any())).thenReturn(
        of(roomSubstitutionRuleEntity1Inactive, roomSubstitutionRuleEntity3));
    Map<Integer, RoomSubstitutionRuleEntity> expectedCachedRules = Map.of(2,
        roomSubstitutionRuleEntity2, 3, roomSubstitutionRuleEntity3);

    //Act
    roomSubstitutionRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(roomSubstitutionRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        roomSubstitutionRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(roomSubstitutionCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(roomSubstitutionCacheRepository);
  }

  @Test
  void updateCache__shouldNotUpdateForUnknownStatus() throws IllegalAccessException {
    //Arrange
    var roomSubstitutionRuleEntity1 = createRoomSubstitutionRuleEntity(1, "NON-EXISTENT STATUS");
    var roomSubstitutionRuleEntity2 = createRoomSubstitutionRuleEntity(2, "ACTIVE");
    when(roomSubstitutionCacheRepository.findAllUpdatedAfter(any())).thenReturn(
        of(roomSubstitutionRuleEntity1, roomSubstitutionRuleEntity2));
    var expectedCachedRules = Map.of(2, roomSubstitutionRuleEntity2);

    //Act
    roomSubstitutionRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(roomSubstitutionRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    verify(roomSubstitutionCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(roomSubstitutionCacheRepository);
  }

  @Test
  void findRoomSubstitutionRules__shouldReturnOk() throws IllegalAccessException {
    //Arrange
    var roomSubstitutionRuleEntity1 = createRoomSubstitutionRuleEntity("", 2, 0, 1, 1, "", OP, "DBLWIN",
        DOUBLE, 1, "DBLE", CHANNEL);
    var roomSubstitutionRuleEntity2 = createRoomSubstitutionRuleEntity("", 2, 0, 2, 2, "", OP, "WINCMB",
        DOUBLE, 2, "DBLE", CHANNEL);
    var roomSubstitutionRuleEntity3 = createRoomSubstitutionRuleEntity("", 1, 0, 3, 1, "", OP, "DBLWIN",
        DOUBLE, 3, "SING", CHANNEL);
    var roomSubstitutionRuleEntity4 = createRoomSubstitutionRuleEntity("", 1, 0, 4, 2, "", OP, "WINCMB",
        DOUBLE, 4, "TWIN", CHANNEL);
    var roomSubstitutionRequest = createRoomSubstitutionRequest(2, 0, OP, DOUBLE, CHANNEL);
    var roomSubstitutionRuleDomain1 = createRoomSubstitutionRuleDomain(roomSubstitutionRuleEntity1);
    var roomSubstitutionRuleDomain2 = createRoomSubstitutionRuleDomain(roomSubstitutionRuleEntity2);
    setCachedRules(
        of(roomSubstitutionRuleEntity1, roomSubstitutionRuleEntity2, roomSubstitutionRuleEntity3,
            roomSubstitutionRuleEntity4));
    when(roomSubstitutionRuleEntityMapper.toModel(roomSubstitutionRuleEntity1)).thenReturn(
        roomSubstitutionRuleDomain1);
    when(roomSubstitutionRuleEntityMapper.toModel(roomSubstitutionRuleEntity2)).thenReturn(
        roomSubstitutionRuleDomain2);

    //Act
    var response = roomSubstitutionRepositoryCacheOutPort.findRoomSubstitutionRules(
        roomSubstitutionRequest);

    //Assert
    assertFalse(response.isEmpty());
    assertThat(response.get(0).getAdults()).isEqualTo(
        roomSubstitutionRequest.getAdults());
    assertThat(response.get(0).getChildren()).isEqualTo(
        roomSubstitutionRequest.getChildren());
    assertThat(response.get(0).getRoomType()).isEqualTo(
        roomSubstitutionRequest.getRoomType());
    assertThat(response.get(0).getPms()).isEqualTo(
        roomSubstitutionRequest.getPms());
    assertThat(response.get(1).getAdults()).isEqualTo(
        roomSubstitutionRequest.getAdults());
    assertThat(response.get(1).getChildren()).isEqualTo(
        roomSubstitutionRequest.getChildren());
    assertThat(response.get(1).getRoomType()).isEqualTo(
        roomSubstitutionRequest.getRoomType());
    assertThat(response.get(1).getPms()).isEqualTo(
        roomSubstitutionRequest.getPms());
    assertThat(response.get(1).getChannel()).isEqualTo(
        roomSubstitutionRequest.getChannel());

    verifyNoMoreInteractions(roomSubstitutionRuleEntityMapper);
    verifyNoInteractions(roomSubstitutionCacheRepository);
  }

  @Test
  void findRoomSubstitutionRule__shouldThrowException() throws IllegalAccessException {
    //Arrange
    var roomSubstitutionRuleEntity1 = createRoomSubstitutionRuleEntity("", 1, 0, 1, 1, "", OP, "DBLWIN",
        DOUBLE, 1, "SING", CHANNEL);
    var roomSubstitutionRuleEntity2 = createRoomSubstitutionRuleEntity("", 2, 0, 2, 1, "", OP, "WINCMB",
        TWIN, 2, "TWIN", CHANNEL);
    var roomSubstitutionRuleEntity3 = createRoomSubstitutionRuleEntity("", 1, 0, 3, 1, "", OP, "DBLWIN",
        SINGLE, 3, "SING", CHANNEL);
    var roomSubstitutionRuleEntity4 = createRoomSubstitutionRuleEntity("", 2, 1, 5, 1, "", OP, "FMTHRE",
        FAMILY, 4, "TRIP", CHANNEL);
    var roomSubstitutionRequest = createRoomSubstitutionRequest(2, 0, OP, DOUBLE, CHANNEL);
    setCachedRules(
        of(roomSubstitutionRuleEntity1, roomSubstitutionRuleEntity2, roomSubstitutionRuleEntity3,
            roomSubstitutionRuleEntity4));

    //Assert
    assertThrows(RuleEngineException.class, () -> roomSubstitutionRepositoryCacheOutPort.findRoomSubstitutionRules(
        roomSubstitutionRequest), "Room Substitution Rule not found.");

    verifyNoMoreInteractions(roomSubstitutionRuleEntityMapper);
    verifyNoInteractions(roomSubstitutionCacheRepository);
  }

  private RoomSubstitutionRuleEntity createRoomSubstitutionRuleEntity(Integer ruleId,
      String status) {
    var roomSubstitutionRuleEntity = new RoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity.setRuleId(ruleId);
    roomSubstitutionRuleEntity.setStatus(status);
    return roomSubstitutionRuleEntity;
  }

  private RoomSubstitutionRuleRequest createRoomSubstitutionRequest(Integer adults,
      Integer children, String pms, String roomType, String channel) {
    return RoomSubstitutionRuleRequest.builder()
        .adults(adults)
        .children(children)
        .pms(pms)
        .roomType(roomType)
        .channel(channel)
        .build();
  }

  private RoomSubstitutionRuleEntity createRoomSubstitutionRuleEntity(String accessibleSpecialRequest, Integer adults,
      Integer children, Integer delayTime, Integer offerOrder, String pkgCode, String pms, String pmsRoomType, String roomType,
      Integer ruleId, String specialRequest, String channel) {
    var roomSubstitutionRuleEntity = new RoomSubstitutionRuleEntity();
    var time = LocalDateTime.now();
    roomSubstitutionRuleEntity.setAccessibleSpecialRequest(accessibleSpecialRequest);
    roomSubstitutionRuleEntity.setAdults(adults);
    roomSubstitutionRuleEntity.setChildren(children);
    roomSubstitutionRuleEntity.setLastModifiedAt(time.minusMinutes(delayTime));
    roomSubstitutionRuleEntity.setOfferOrder(offerOrder);
    roomSubstitutionRuleEntity.setPkgCode(pkgCode);
    roomSubstitutionRuleEntity.setPms(pms);
    roomSubstitutionRuleEntity.setPmsRoomType(pmsRoomType);
    roomSubstitutionRuleEntity.setRoomType(roomType);
    roomSubstitutionRuleEntity.setRuleId(ruleId);
    roomSubstitutionRuleEntity.setSpecialRequest(specialRequest);
    roomSubstitutionRuleEntity.setChannel(channel);
    return roomSubstitutionRuleEntity;
  }

  private RoomSubstitutionRule createRoomSubstitutionRuleDomain(
      RoomSubstitutionRuleEntity roomSubstitutionRuleEntity) {
    var time = LocalDateTime.now();
    return RoomSubstitutionRule.builder()
        .accessibleSpecialRequest(roomSubstitutionRuleEntity.getAccessibleSpecialRequest())
        .adults(roomSubstitutionRuleEntity.getAdults())
        .children(roomSubstitutionRuleEntity.getChildren())
        .codePackage(roomSubstitutionRuleEntity.getPkgCode())
        .createdAt(time)
        .lastModifiedAt(time)
        .pms(roomSubstitutionRuleEntity.getPms())
        .roomType(roomSubstitutionRuleEntity.getRoomType())
        .ruleId(1)
        .specialRequest(roomSubstitutionRuleEntity.getSpecialRequest())
        .status(RuleStatus.ACTIVE)
        .channel(roomSubstitutionRuleEntity.getChannel())
        .build();
  }

  private void setCachedRules(List<RoomSubstitutionRuleEntity> entities)
      throws IllegalAccessException {
    Map<Integer, RoomSubstitutionRuleEntity> cachedRules = (Map<Integer, RoomSubstitutionRuleEntity>) FieldUtils.readField(
        roomSubstitutionRepositoryCacheOutPort,
        "cachedRules",
        true);
    entities.forEach(entity -> cachedRules.put(entity.getRuleId(), entity));
  }
}
