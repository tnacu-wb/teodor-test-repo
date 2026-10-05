package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.agent.domain.model.out.MaxArrivalDateRule;
import uk.co.whitbread.rules.agent.domain.ports.secondary.MaxArrivalDateRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.MaxArrivalDateRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxArrivalDateRuleEntity;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RuleEntity;

@Slf4j
public class MaxArrivalDateRepositoryCacheOutPortImpl implements
    MaxArrivalDateRuleRepositoryOutPort {

  private static final String STATUS_ACTIVE = "ACTIVE";
  private static final String STATUS_INACTIVE = "INACTIVE";

  private final MaxArrivalDateCacheRepository maxArrivalDateCacheRepository;
  private final ConcurrentHashMap<Integer, MaxArrivalDateRuleEntity> cachedRules;
  private final MaxArrivalDateRuleEntityMapper maxArrivalDateRuleEntityMapper;
  private LocalDateTime lastCacheUpdate;

  public MaxArrivalDateRepositoryCacheOutPortImpl(
      MaxArrivalDateCacheRepository maxArrivalDateCacheRepository,
      MaxArrivalDateRuleEntityMapper maxArrivalDateRuleEntityMapper) {
    cachedRules = new ConcurrentHashMap<>();
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    this.maxArrivalDateCacheRepository = maxArrivalDateCacheRepository;
    this.maxArrivalDateRuleEntityMapper = maxArrivalDateRuleEntityMapper;
  }

  @Override
  public void cacheRules() {
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    maxArrivalDateCacheRepository.findAllByStatusActive()
        .forEach(maxArrivalDateRuleEntity -> cachedRules.put(maxArrivalDateRuleEntity.getRuleId(),
            maxArrivalDateRuleEntity));
  }

  @Override
  public void updateCache() {
    LocalDateTime timeToCheck = lastCacheUpdate;
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    maxArrivalDateCacheRepository.findAllUpdatedAfter(timeToCheck)
        .forEach(maxArrivalDateRuleEntity -> {
          var status = maxArrivalDateRuleEntity.getStatus();
          switch (status) {
            case STATUS_INACTIVE -> cachedRules.remove(maxArrivalDateRuleEntity.getRuleId());
            case STATUS_ACTIVE -> cachedRules.put(maxArrivalDateRuleEntity.getRuleId(),
                maxArrivalDateRuleEntity);
            default -> log.warn("Unknown status:{}", status);
          }
        });
  }

  @Override
  public Optional<MaxArrivalDateRule> findRule(String channelId) {
    log.debug("Entering find max arrival date rule for channelId={}", channelId);
    return cachedRules.values().stream()
        .sorted(Comparator.comparing(RuleEntity::getLastModifiedAt).reversed())
        .filter(
            maxArrivalDateRuleEntity ->
                channelId.equalsIgnoreCase(maxArrivalDateRuleEntity.getChannelId())
        ).map(maxArrivalDateRuleEntityMapper::toModel).findFirst();
  }
}
