package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.agent.domain.model.in.ChannelRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.ChannelRule;
import uk.co.whitbread.rules.agent.domain.ports.secondary.ChannelRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.ChannelRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.ChannelRuleEntity;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RuleEntity;

@Slf4j
public class ChannelRepositoryCacheOutPortImpl implements ChannelRuleRepositoryOutPort {

  private static final String STATUS_ACTIVE = "ACTIVE";
  private static final String STATUS_INACTIVE = "INACTIVE";

  private final ChannelRepository channelRepository;
  private final ConcurrentHashMap<Integer, ChannelRuleEntity> cachedRules;
  private final ChannelRuleEntityMapper channelRuleEntityMapper;
  private LocalDateTime lastCacheUpdate;

  public ChannelRepositoryCacheOutPortImpl(ChannelRepository channelRepository,
      ChannelRuleEntityMapper channelRuleEntityMapper) {
    cachedRules = new ConcurrentHashMap<>();
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    this.channelRepository = channelRepository;
    this.channelRuleEntityMapper = channelRuleEntityMapper;
  }

  @Override
  public void cacheRules() {
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    channelRepository.findAllByStatusActive()
        .forEach(channelRuleEntity -> cachedRules.put(channelRuleEntity.getRuleId(),
            channelRuleEntity));
  }

  @Override
  public void updateCache() {
    LocalDateTime timeToCheck = lastCacheUpdate;
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    channelRepository.findAllUpdatedAfter(timeToCheck)
        .forEach(channelRuleEntity -> {
          var status = channelRuleEntity.getStatus();
          switch (status) {
            case STATUS_INACTIVE -> cachedRules.remove(channelRuleEntity.getRuleId());
            case STATUS_ACTIVE -> cachedRules.put(channelRuleEntity.getRuleId(),
                channelRuleEntity);
            default -> log.warn("Unknown status:{}", status);
          }
        });
  }

  @Override
  public Optional<ChannelRule> findRule(ChannelRuleRequest channelRuleRequest) {
    log.debug("Entering find channel rule for {}/{}/{} - PMS {}",
        channelRuleRequest.getChannel(),
        channelRuleRequest.getSubchannel(),
        channelRuleRequest.getLanguage(),
        channelRuleRequest.getPms());

    return cachedRules
        .values()
        .stream()
        .sorted(Comparator.comparing(RuleEntity::getLastModifiedAt).reversed())
        .filter(rule -> channelRuleRequest.getChannel().equals(rule.getChannel())
            && channelRuleRequest.getSubchannel().equals(rule.getSubchannel())
            && channelRuleRequest.getLanguage().equals(rule.getLanguage())
            && channelRuleRequest.getPms().equals(rule.getPms()))
        .map(channelRuleEntityMapper::toModel)
        .findFirst();
  }

  @Override
  public Optional<ChannelRule> findRule(String sourceId) {
    log.debug("Entering find channel rule with source id {}", sourceId);

    return cachedRules
        .values()
        .stream()
        .sorted(Comparator.comparing(RuleEntity::getLastModifiedAt).reversed())
        .filter(rule -> sourceId.equals(rule.getSourceId()))
        .map(channelRuleEntityMapper::toModel)
        .findFirst();
  }
}
