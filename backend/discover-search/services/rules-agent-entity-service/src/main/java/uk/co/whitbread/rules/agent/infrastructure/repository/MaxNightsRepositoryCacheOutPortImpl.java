package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.agent.domain.model.out.MaxNightsRule;
import uk.co.whitbread.rules.agent.domain.ports.secondary.MaxNightsRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.MaxNightsRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxNightsRuleEntity;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RuleEntity;

@Slf4j
public class MaxNightsRepositoryCacheOutPortImpl implements MaxNightsRuleRepositoryOutPort {

  private static final String STATUS_ACTIVE = "ACTIVE";
  private static final String STATUS_INACTIVE = "INACTIVE";

  private final MaxNightsCacheRepository maxNightsCacheRepository;
  private final ConcurrentHashMap<Integer, MaxNightsRuleEntity> cachedRules;
  private final MaxNightsRuleEntityMapper maxNightsRuleEntityMapper;
  private LocalDateTime lastCacheUpdate;

  public MaxNightsRepositoryCacheOutPortImpl(MaxNightsCacheRepository maxNightsCacheRepository,
      MaxNightsRuleEntityMapper maxNightsRuleEntityMapper) {
    cachedRules = new ConcurrentHashMap<>();
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    this.maxNightsCacheRepository = maxNightsCacheRepository;
    this.maxNightsRuleEntityMapper = maxNightsRuleEntityMapper;
  }

  @Override
  public void cacheRules() {
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    maxNightsCacheRepository.findAllByStatusActive()
        .forEach(maxNightsRuleEntity -> cachedRules.put(maxNightsRuleEntity.getRuleId(),
            maxNightsRuleEntity));
  }

  @Override
  public void updateCache() {
    LocalDateTime timeToCheck = lastCacheUpdate;
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    maxNightsCacheRepository.findAllUpdatedAfter(timeToCheck)
        .forEach(maxNightsRuleEntity -> {
          var status = maxNightsRuleEntity.getStatus();
          switch (status) {
            case STATUS_INACTIVE -> cachedRules.remove(maxNightsRuleEntity.getRuleId());
            case STATUS_ACTIVE -> cachedRules.put(maxNightsRuleEntity.getRuleId(),
                maxNightsRuleEntity);
            default -> log.warn("Unknown status:{}", status);
          }
        });
  }

  @Override
  public Optional<MaxNightsRule> findRule(String channelId) {
    log.debug("Entering find max nights rule for channelId={}", channelId);
    return cachedRules.values().stream()
        .sorted(Comparator.comparing(RuleEntity::getLastModifiedAt).reversed())
        .filter(
            maxNightsRuleEntity ->
                channelId.equalsIgnoreCase(maxNightsRuleEntity.getChannelId())
        ).map(maxNightsRuleEntityMapper::toModel).findFirst();
  }
}
