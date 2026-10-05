package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.agent.domain.model.out.BaseRateRule;
import uk.co.whitbread.rules.agent.domain.ports.secondary.BaseRateRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.BaseRateRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.BaseRateRuleEntity;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RuleEntity;

@Slf4j
public class BaseRateRepositoryCacheOutPortImpl implements BaseRateRuleRepositoryOutPort {
  
  private static final String STATUS_ACTIVE = "ACTIVE";
  private static final String STATUS_INACTIVE = "INACTIVE";
  private final BaseRateCacheRepository baseRateCacheRepository;
  private final BaseRateRuleEntityMapper baseRateRuleEntityMapper;
  private final ConcurrentHashMap<Integer, BaseRateRuleEntity> cachedRules;
  private LocalDateTime lastCacheUpdate;
  
  public BaseRateRepositoryCacheOutPortImpl(BaseRateCacheRepository baseRateCacheRepository,
                                            BaseRateRuleEntityMapper baseRateRuleEntityMapper) {
    cachedRules = new ConcurrentHashMap<>();
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    this.baseRateCacheRepository = baseRateCacheRepository;
    this.baseRateRuleEntityMapper = baseRateRuleEntityMapper;
  }
  
  
  @Override
  public Optional<BaseRateRule> getBaseRate(String ratePlanCode) {
    return cachedRules
        .values()
        .stream()
        .sorted(Comparator.comparing(RuleEntity::getLastModifiedAt).reversed())
        .filter(baseRateRuleEntity -> ratePlanCode.equals(baseRateRuleEntity.getRatePlanCode()))
        .map(baseRateRuleEntityMapper::toModel)
        .findFirst();
  }
  
  @Override
  public void cacheRules() {
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    baseRateCacheRepository.findAllByStatusActive()
        .forEach(baseRateRuleEntity -> cachedRules.put(baseRateRuleEntity.getRuleId(),
            baseRateRuleEntity));
  }
  
  @Override
  public void updateCache() {
    LocalDateTime timeToCheck = lastCacheUpdate;
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    baseRateCacheRepository.findAllUpdatedAfter(timeToCheck)
        .forEach(baseRateRuleEntity -> {
          var status = baseRateRuleEntity.getStatus();
          switch (status) {
            case STATUS_INACTIVE -> cachedRules.remove(baseRateRuleEntity.getRuleId());
            case STATUS_ACTIVE -> cachedRules.put(baseRateRuleEntity.getRuleId(),
                baseRateRuleEntity);
            default -> log.warn("Unknown status:{}", status);
          }
        });
  }
}
