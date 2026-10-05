package uk.co.whitbread.rules.agent.infrastructure.repository;


import static java.util.Comparator.comparing;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.agent.domain.model.out.RbacRule;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RbacRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.RbacRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RbacRuleEntity;

@Slf4j
public class RbacRepositoryCacheOutPortImpl implements RbacRuleRepositoryOutPort {

  private static final String STATUS_ACTIVE = "ACTIVE";
  private static final String STATUS_INACTIVE = "INACTIVE";

  private final RbacCacheRepository rbacCacheRepository;
  private final ConcurrentHashMap<Integer, RbacRuleEntity> cachedRules;
  private final RbacRuleEntityMapper rbacRuleEntityMapper;
  private LocalDateTime lastCacheUpdate;

  public RbacRepositoryCacheOutPortImpl(RbacCacheRepository rbacCacheRepository,
      RbacRuleEntityMapper rbacRuleEntityMapper) {
    this.rbacRuleEntityMapper = rbacRuleEntityMapper;
    cachedRules = new ConcurrentHashMap<>();
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    this.rbacCacheRepository = rbacCacheRepository;
  }

  @Override
  public void cacheRules() {
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    rbacCacheRepository.findAllByStatusActive()
        .forEach(rbacRuleEntity -> cachedRules.put(rbacRuleEntity.getRuleId(),
            rbacRuleEntity));
  }

  @Override
  public void updateCache() {
    LocalDateTime timeToCheck = lastCacheUpdate;
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    rbacCacheRepository.findAllUpdatedAfter(timeToCheck)
        .forEach(rbacRuleEntity -> {
          var status = rbacRuleEntity.getStatus();
          switch (status) {
            case STATUS_INACTIVE -> cachedRules.remove(rbacRuleEntity.getRuleId());
            case STATUS_ACTIVE -> cachedRules.put(rbacRuleEntity.getRuleId(),
                rbacRuleEntity);
            default -> log.warn("Unknown status:{}", status);
          }
        });
  }


  @Override
  public List<RbacRule> findAllRbacRuleByRoles(List<String> rbacRoleIds) {
    return cachedRules.values().stream()
        .filter(rule -> rbacRoleIds.contains(rule.getRoleId()) && rule.getHasAccess())
        .map(rbacRuleEntityMapper::toModel)
        .toList();
  }

  @Override
  public Optional<Boolean> getRbacHasAccess(List<String> roleIdList, String resourceId) {
    return cachedRules.values().stream()
        .filter(rule -> roleIdList.contains(rule.getRoleId()) && resourceId.equalsIgnoreCase(
            rule.getResourceId()))
        .map(RbacRuleEntity::getHasAccess)
        .max(comparing(Boolean::booleanValue));
  }
}
