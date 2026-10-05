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
import uk.co.whitbread.rules.agent.domain.model.in.VatRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.VatRule;
import uk.co.whitbread.rules.agent.domain.ports.secondary.VatRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.VatRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RuleEntity;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.VatRuleEntity;

@Slf4j
public class VatRepositoryCacheOutPortImpl implements VatRuleRepositoryOutPort {

  private static final String STATUS_ACTIVE = "ACTIVE";
  private static final String STATUS_INACTIVE = "INACTIVE";

  private final VatCacheRepository vatCacheRepository;
  private final ConcurrentHashMap<Integer, VatRuleEntity> cachedRules;
  private final VatRuleEntityMapper vatRuleEntityMapper;
  private LocalDateTime lastCacheUpdate;

  public VatRepositoryCacheOutPortImpl(VatCacheRepository vatCacheRepository,
      VatRuleEntityMapper vatRuleEntityMapper) {
    this.vatCacheRepository = vatCacheRepository;
    this.vatRuleEntityMapper = vatRuleEntityMapper;
    cachedRules = new ConcurrentHashMap<>();
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
  }

  @Override
  public void cacheRules() {
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    vatCacheRepository.findAllByStatusActive()
        .forEach(
            vatRuleEntity -> cachedRules.put(vatRuleEntity.getRuleId(),
                vatRuleEntity));
  }

  @Override
  public void updateCache() {
    LocalDateTime timeToCheck = lastCacheUpdate;
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    vatCacheRepository.findAllUpdatedAfter(timeToCheck)
        .forEach(vatRuleEntity -> {
          var status = vatRuleEntity.getStatus();
          switch (status) {
            case STATUS_INACTIVE -> cachedRules.remove(vatRuleEntity.getRuleId());
            case STATUS_ACTIVE -> cachedRules.put(vatRuleEntity.getRuleId(),
                vatRuleEntity);
            default -> log.warn("Unknown status:{}", status);
          }
        });
  }

  @Override
  public List<VatRule> findVatRules(VatRuleRequest vatRuleRequest) {

    return of(cachedRules.values()
        .stream()
        .sorted(Comparator.comparing(RuleEntity::getLastModifiedAt).reversed())
        .filter(vatRuleEntity -> isVatFound(vatRuleRequest,
            vatRuleEntity))
        .map(vatRuleEntityMapper::toModel)
        .toList())
        .filter(list -> !list.isEmpty())
        .orElseThrow(() -> {
          var message = "VAT rule not found.";
          var exception = new RuleEngineException(ErrorCode.DIGITAL_VAT_RULE_EXCEPTION, message);
          ExceptionLogger.log(log, exception);
          return exception;
        });
  }

  private boolean isVatFound(VatRuleRequest vatRuleRequest,
      VatRuleEntity vatRuleEntity) {
    return vatRuleRequest.getVatRegion().equals(vatRuleEntity.getVatRegion())
        && vatRuleRequest.getPkgCodeArr().contains(vatRuleEntity.getPkgCode());
  }
}
