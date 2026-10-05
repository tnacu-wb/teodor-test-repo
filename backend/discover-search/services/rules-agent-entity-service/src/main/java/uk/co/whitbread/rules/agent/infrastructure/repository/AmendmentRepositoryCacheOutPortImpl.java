package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.agent.domain.model.out.AmendmentRule;
import uk.co.whitbread.rules.agent.domain.ports.secondary.AmendmentRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.AmendmentRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.AmendmentRuleEntity;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RuleEntity;

@Slf4j
public class AmendmentRepositoryCacheOutPortImpl implements AmendmentRuleRepositoryOutPort {

  private static final String STATUS_ACTIVE = "ACTIVE";
  private static final String STATUS_INACTIVE = "INACTIVE";

  private final AmendmentRepository amendmentRepository;
  private final ConcurrentHashMap<Integer, AmendmentRuleEntity> cachedRules;
  private final AmendmentRuleEntityMapper amendmentRuleEntityMapper;
  private LocalDateTime lastCacheUpdate;

  public AmendmentRepositoryCacheOutPortImpl(AmendmentRepository amendmentRepository,
      AmendmentRuleEntityMapper amendmentRuleEntityMapper) {
    cachedRules = new ConcurrentHashMap<>();
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    this.amendmentRepository = amendmentRepository;
    this.amendmentRuleEntityMapper = amendmentRuleEntityMapper;
  }

  @Override
  public void cacheRules() {
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    amendmentRepository.findAllByStatusActive()
        .forEach(amendmentRuleEntity -> cachedRules.put(amendmentRuleEntity.getRuleId(),
            amendmentRuleEntity));
  }

  @Override
  public void updateCache() {
    LocalDateTime timeToCheck = lastCacheUpdate;
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    amendmentRepository.findAllUpdatedAfter(timeToCheck)
        .forEach(amendmentRuleEntity -> {
          var status = amendmentRuleEntity.getStatus();
          switch (status) {
            case STATUS_INACTIVE -> cachedRules.remove(amendmentRuleEntity.getRuleId());
            case STATUS_ACTIVE -> cachedRules.put(amendmentRuleEntity.getRuleId(),
                amendmentRuleEntity);
            default -> log.warn("Unknown status:{}", status);
          }
        });
  }

  @Override
  public Optional<AmendmentRule> findRule(String rateType, String countryCode) {
    log.debug("Entering find amend rule for rateType={}", rateType);
    return cachedRules
        .values()
        .stream()
        .sorted(Comparator.comparing(RuleEntity::getLastModifiedAt).reversed())
        .filter(rule -> rateType.equals(rule.getRateType()) && countryCode.equals(
            rule.getCountryCode()))
        .map(amendmentRuleEntityMapper::toModel)
        .findFirst();
  }
}
