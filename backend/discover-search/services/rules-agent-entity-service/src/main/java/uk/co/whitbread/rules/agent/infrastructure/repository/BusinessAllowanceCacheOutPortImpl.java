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
import uk.co.whitbread.rules.agent.domain.model.out.BusinessAllowanceRule;
import uk.co.whitbread.rules.agent.domain.ports.secondary.BusinessAllowanceRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.BusinessAllowanceRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.BusinessAllowanceRuleEntity;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RuleEntity;

@Slf4j
public class BusinessAllowanceCacheOutPortImpl implements BusinessAllowanceRuleRepositoryOutPort {

  private static final String STATUS_ACTIVE = "ACTIVE";
  private static final String STATUS_INACTIVE = "INACTIVE";
  private final BusinessAllowanceRepository businessAllowanceRepository;
  private final ConcurrentHashMap<Integer, BusinessAllowanceRuleEntity> cachedRules;
  private final BusinessAllowanceRuleEntityMapper businessAllowanceRuleEntityMapper;
  private LocalDateTime lastCacheUpdate;


  public BusinessAllowanceCacheOutPortImpl(
      BusinessAllowanceRepository businessAllowanceRepository,
      BusinessAllowanceRuleEntityMapper businessAllowanceRuleEntityMapper
  ) {
    cachedRules = new ConcurrentHashMap<>();
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    this.businessAllowanceRepository = businessAllowanceRepository;
    this.businessAllowanceRuleEntityMapper = businessAllowanceRuleEntityMapper;
  }

  @Override
  public List<BusinessAllowanceRule> findRules() {
    return of(cachedRules.values()
        .stream()
        .sorted(Comparator.comparing(RuleEntity::getLastModifiedAt).reversed())
        .map(businessAllowanceRuleEntityMapper::toModel)
        .toList())
        .filter(list -> !list.isEmpty())
        .orElseThrow(() -> {
          var message = "No business allowances found.";
          var exception = new RuleEngineException(ErrorCode.DIGITAL_NO_BUSINESS_ALLOWANCE_EXCEPTION, message);
          ExceptionLogger.log(log, exception);
          return exception;
        });
  }

  @Override
  public void cacheRules() {
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    businessAllowanceRepository.findAllByStatusActive()
        .forEach(
            businessAllowanceRuleEntity -> cachedRules.put(businessAllowanceRuleEntity.getRuleId(),
                businessAllowanceRuleEntity));
  }

  @Override
  public void updateCache() {
    LocalDateTime timeToCheck = lastCacheUpdate;
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    businessAllowanceRepository.findAllUpdatedAfter(timeToCheck)
        .forEach(businessAllowanceRuleEntity -> {
          var status = businessAllowanceRuleEntity.getStatus();
          switch (status) {
            case STATUS_INACTIVE -> cachedRules.remove(businessAllowanceRuleEntity.getRuleId());
            case STATUS_ACTIVE -> cachedRules.put(businessAllowanceRuleEntity.getRuleId(),
                businessAllowanceRuleEntity);
            default -> log.warn("Unknown status:{}", status);
          }
        });
  }
}
