package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomsRule;
import uk.co.whitbread.rules.agent.domain.ports.secondary.MaxRoomsRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.MaxRoomsRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxRoomsRuleEntity;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RuleEntity;

@Slf4j
public class MaxRoomsRepositoryCacheOutPortImpl implements MaxRoomsRuleRepositoryOutPort {

  private static final String STATUS_ACTIVE = "ACTIVE";
  private static final String STATUS_INACTIVE = "INACTIVE";

  private final MaxRoomsCacheRepository maxRoomsCacheRepository;
  private final ConcurrentHashMap<Integer, MaxRoomsRuleEntity> cachedRules;
  private final MaxRoomsRuleEntityMapper maxRoomsRuleEntityMapper;
  private LocalDateTime lastCacheUpdate;

  public MaxRoomsRepositoryCacheOutPortImpl(MaxRoomsCacheRepository maxRoomsCacheRepository,
      MaxRoomsRuleEntityMapper maxRoomsRuleEntityMapper) {
    cachedRules = new ConcurrentHashMap<>();
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    this.maxRoomsCacheRepository = maxRoomsCacheRepository;
    this.maxRoomsRuleEntityMapper = maxRoomsRuleEntityMapper;
  }

  @Override
  public void cacheRules() {
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    maxRoomsCacheRepository.findAllByStatusActive()
        .forEach(maxRoomsRuleEntity -> cachedRules.put(maxRoomsRuleEntity.getRuleId(),
            maxRoomsRuleEntity));
  }

  @Override
  public void updateCache() {
    LocalDateTime timeToCheck = lastCacheUpdate;
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    maxRoomsCacheRepository.findAllUpdatedAfter(timeToCheck)
        .forEach(maxRoomsRuleEntity -> {
          var status = maxRoomsRuleEntity.getStatus();
          switch (status) {
            case STATUS_INACTIVE -> cachedRules.remove(maxRoomsRuleEntity.getRuleId());
            case STATUS_ACTIVE -> cachedRules.put(maxRoomsRuleEntity.getRuleId(),
                maxRoomsRuleEntity);
            default -> log.warn("Unknown status:{}", status);
          }
        });
  }

  @Override
  public Optional<MaxRoomsRule> findRule(String channelId) {
    log.debug("Entering find rule for max rooms with channelId={}", channelId);
    return cachedRules.values().stream()
        .sorted(Comparator.comparing(RuleEntity::getLastModifiedAt).reversed())
        .filter(
            maxRoomsRuleEntity ->
                channelId.equalsIgnoreCase(maxRoomsRuleEntity.getChannelId())
        ).map(maxRoomsRuleEntityMapper::toModel).findFirst();
  }
}
