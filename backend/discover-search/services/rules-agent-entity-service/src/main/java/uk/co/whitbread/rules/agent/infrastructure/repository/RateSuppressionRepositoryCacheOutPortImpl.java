package uk.co.whitbread.rules.agent.infrastructure.repository;

import static java.util.Optional.of;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.rules.agent.domain.exception.ErrorCode;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.out.RateSuppressionRule;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RateSuppressionRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.RateSuppressionRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RateSuppressionRuleEntity;

@Slf4j
public class RateSuppressionRepositoryCacheOutPortImpl implements
    RateSuppressionRuleRepositoryOutPort {

  private static final String STATUS_ACTIVE = "ACTIVE";
  private static final String STATUS_INACTIVE = "INACTIVE";

  private final RateSuppressionCacheRepository rateSuppressionCacheRepository;
  private final ConcurrentHashMap<Integer, RateSuppressionRuleEntity> cachedRules;
  private final RateSuppressionRuleEntityMapper rateSuppressionRuleEntityMapper;
  private LocalDateTime lastCacheUpdate;

  public RateSuppressionRepositoryCacheOutPortImpl(
      RateSuppressionCacheRepository rateSuppressionCacheRepository,
      RateSuppressionRuleEntityMapper rateSuppressionRuleEntityMapper) {
    cachedRules = new ConcurrentHashMap<>();
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    this.rateSuppressionRuleEntityMapper = rateSuppressionRuleEntityMapper;
    this.rateSuppressionCacheRepository = rateSuppressionCacheRepository;
  }

  @Override
  public List<RateSuppressionRule> findRateSuppressionRule() {
    return of(cachedRules.values()
        .stream()
        .map(rateSuppressionRuleEntityMapper::toModel)
        .toList())
        .filter(list -> !list.isEmpty())
        .orElseThrow(() -> {
          var message = "Rate Suppression Rules not found.";
          var exception = new RuleEngineException(ErrorCode.DIGITAL_RATE_SUPPRESSION_RULE_EXCEPTION,
                message);
          ExceptionLogger.log(log, exception);
          return exception;
        });
  }

  @Override
  public void cacheRules() {
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    rateSuppressionCacheRepository.findAllByStatusActive()
        .forEach(rateSuppressionRuleEntity -> cachedRules.put(rateSuppressionRuleEntity.getRuleId(),
            rateSuppressionRuleEntity));
  }

  @Override
  public void updateCache() {
    LocalDateTime timeToCheck = lastCacheUpdate;
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    rateSuppressionCacheRepository.findAllUpdatedAfter(timeToCheck)
        .forEach(rateSuppressionRuleEntity -> {
          var status = rateSuppressionRuleEntity.getStatus();
          switch (status) {
            case STATUS_INACTIVE -> cachedRules.remove(rateSuppressionRuleEntity.getRuleId());
            case STATUS_ACTIVE -> cachedRules.put(rateSuppressionRuleEntity.getRuleId(),
                rateSuppressionRuleEntity);
            default -> log.warn("Unknown status:{}", status);
          }
        });
  }

}
