package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomOccupancyRule;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RoomOccRuleRepoOutPort;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.MaxRoomOccupancyRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxRoomOccupancyRuleEntity;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RuleEntity;

@Slf4j
public class MaxRoomOccupancyRuleRepoOutPortImpl implements RoomOccRuleRepoOutPort {

  private static final String STATUS_ACTIVE = "ACTIVE";
  private static final String STATUS_INACTIVE = "INACTIVE";

  private final MaxRoomOccupancyCacheRepository maxRoomOccupancyCacheRepository;
  private final ConcurrentHashMap<Integer, MaxRoomOccupancyRuleEntity> cachedRules;
  private final MaxRoomOccupancyRuleEntityMapper roomOccupancyRuleEntityMapper;
  private LocalDateTime lastCacheUpdate;

  public MaxRoomOccupancyRuleRepoOutPortImpl(
      MaxRoomOccupancyCacheRepository maxRoomOccupancyCacheRepository,
      MaxRoomOccupancyRuleEntityMapper roomOccupancyRuleEntityMapper) {
    cachedRules = new ConcurrentHashMap<>();
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    this.maxRoomOccupancyCacheRepository = maxRoomOccupancyCacheRepository;
    this.roomOccupancyRuleEntityMapper = roomOccupancyRuleEntityMapper;
  }

  @Override
  public List<MaxRoomOccupancyRule> findRules(String channelId, String brand) {
    log.debug("Entering find rule for max room occupancy for channelId={} and brand={}", channelId, brand);
    return cachedRules.values().stream()
        .sorted(Comparator.comparing(RuleEntity::getLastModifiedAt).reversed())
        .filter(
            maxRoomOccupancyRuleEntity ->
                channelId.equalsIgnoreCase(maxRoomOccupancyRuleEntity.getChannelId())
                    && brand.equalsIgnoreCase(maxRoomOccupancyRuleEntity.getBrand())
        ).map(roomOccupancyRuleEntityMapper::toModel).toList();
  }

  @Override
  public void cacheRules() {
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    maxRoomOccupancyCacheRepository.findAllByStatusActive()
        .forEach(roomOccupancyRuleEntity -> cachedRules.put(roomOccupancyRuleEntity.getRuleId(),
            roomOccupancyRuleEntity));
  }

  @Override
  public void updateCache() {
    LocalDateTime timeToCheck = lastCacheUpdate;
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    maxRoomOccupancyCacheRepository.findAllUpdatedAfter(timeToCheck)
        .forEach(maxRoomOccupancyRuleEntity -> {
          var status = maxRoomOccupancyRuleEntity.getStatus();
          switch (status) {
            case STATUS_INACTIVE -> cachedRules.remove(maxRoomOccupancyRuleEntity.getRuleId());
            case STATUS_ACTIVE -> cachedRules.put(maxRoomOccupancyRuleEntity.getRuleId(),
                maxRoomOccupancyRuleEntity);
            default -> log.warn("Unknown status:{}", status);
          }
        });
  }
}
