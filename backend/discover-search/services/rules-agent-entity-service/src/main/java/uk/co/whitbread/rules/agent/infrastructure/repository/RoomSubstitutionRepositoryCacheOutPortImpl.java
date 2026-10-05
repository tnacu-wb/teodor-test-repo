package uk.co.whitbread.rules.agent.infrastructure.repository;

import static java.util.Optional.of;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.rules.agent.domain.exception.ErrorCode;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitutionRule;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RoomSubstitutionRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.RoomSubstitutionRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RoomSubstitutionRuleEntity;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RuleEntity;

@Slf4j
public class RoomSubstitutionRepositoryCacheOutPortImpl implements
    RoomSubstitutionRuleRepositoryOutPort {

  private static final String STATUS_ACTIVE = "ACTIVE";
  private static final String STATUS_INACTIVE = "INACTIVE";

  private final RoomSubstitutionCacheRepository roomSubstitutionCacheRepository;
  private final ConcurrentHashMap<Integer, RoomSubstitutionRuleEntity> cachedRules;
  private final RoomSubstitutionRuleEntityMapper roomSubstitutionRuleEntityMapper;
  private LocalDateTime lastCacheUpdate;

  public RoomSubstitutionRepositoryCacheOutPortImpl(
      RoomSubstitutionCacheRepository roomSubstitutionCacheRepository,
      RoomSubstitutionRuleEntityMapper roomSubstitutionRuleEntityMapper) {
    this.roomSubstitutionRuleEntityMapper = roomSubstitutionRuleEntityMapper;
    cachedRules = new ConcurrentHashMap<>();
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    this.roomSubstitutionCacheRepository = roomSubstitutionCacheRepository;
  }

  @Override
  public void cacheRules() {
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    roomSubstitutionCacheRepository.findAllByStatusActive()
        .forEach(
            roomSubstitutionRuleEntity -> cachedRules.put(roomSubstitutionRuleEntity.getRuleId(),
                roomSubstitutionRuleEntity));
  }

  @Override
  public void updateCache() {
    LocalDateTime timeToCheck = lastCacheUpdate;
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    roomSubstitutionCacheRepository.findAllUpdatedAfter(timeToCheck)
        .forEach(roomSubstitutionRuleEntity -> {
          var status = roomSubstitutionRuleEntity.getStatus();
          switch (status) {
            case STATUS_INACTIVE -> cachedRules.remove(roomSubstitutionRuleEntity.getRuleId());
            case STATUS_ACTIVE -> cachedRules.put(roomSubstitutionRuleEntity.getRuleId(),
                roomSubstitutionRuleEntity);
            default -> log.warn("Unknown status:{}", status);
          }
        });
  }


  @Override
  public List<RoomSubstitutionRule> findRoomSubstitutionRules(
      RoomSubstitutionRuleRequest roomSubstitutionRuleRequest) {
    log.debug("Entering find substitution rules for roomSubstitutionRuleRequest={}",
        roomSubstitutionRuleRequest);
    return of(cachedRules.values()
        .stream()
        .sorted(Comparator.comparing(RuleEntity::getLastModifiedAt).reversed())
        .filter(
            roomSubstitutionRuleEntity -> isRoomSubstitutionFound(roomSubstitutionRuleRequest,
                roomSubstitutionRuleEntity))
        .map(roomSubstitutionRuleEntityMapper::toModel)
        .toList())
        .filter(list -> !list.isEmpty())
        .orElseThrow(() -> {
          var message = "Room Substitution rule not found.";
          var exception = new RuleEngineException(ErrorCode.DIGITAL_ROOM_SUBSTITUTION_RULE_EXCEPTION,
                message);
          ExceptionLogger.log(log, exception);
          return exception;
        });
  }

  private boolean isRoomSubstitutionFound(RoomSubstitutionRuleRequest roomSubstitutionRuleRequest,
      RoomSubstitutionRuleEntity roomSubstitutionRuleEntity) {
    return roomSubstitutionRuleRequest.getPms().equals(roomSubstitutionRuleEntity.getPms())
        && roomSubstitutionRuleRequest.getAdults()
        .equals(roomSubstitutionRuleEntity.getAdults())
        && roomSubstitutionRuleRequest.getChildren()
        .equals(roomSubstitutionRuleEntity.getChildren())
        && roomSubstitutionRuleRequest.getRoomType()
        .equals(roomSubstitutionRuleEntity.getRoomType())
        && roomSubstitutionRuleRequest.getChannel()
        .equals(roomSubstitutionRuleEntity.getChannel())
        ;
  }

}
