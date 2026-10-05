package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.agent.domain.ports.secondary.PaypalRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.PaypalRuleEntity;

@Slf4j
public class PaypalRepositoryCacheOutPortImpl implements PaypalRuleRepositoryOutPort {

  private static final String STATUS_ACTIVE = "ACTIVE";
  private static final String STATUS_INACTIVE = "INACTIVE";

  private final PaypalCacheRepository paypalCacheRepository;
  private final ConcurrentHashMap<Integer, PaypalRuleEntity> cachedRules;
  private LocalDateTime lastCacheUpdate;

  public PaypalRepositoryCacheOutPortImpl(PaypalCacheRepository paypalCacheRepository
  ) {
    cachedRules = new ConcurrentHashMap<>();
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    this.paypalCacheRepository = paypalCacheRepository;
  }

  @Override
  public void cacheRules() {
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    paypalCacheRepository.findAllByStatusActive()
        .forEach(paypalRuleEntity -> cachedRules.put(paypalRuleEntity.getRuleId(),
            paypalRuleEntity));
  }

  @Override
  public void updateCache() {
    LocalDateTime timeToCheck = lastCacheUpdate;
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    paypalCacheRepository.findAllUpdatedAfter(timeToCheck)
        .forEach(paypalRuleEntity -> {
          var status = paypalRuleEntity.getStatus();
          switch (status) {
            case STATUS_INACTIVE -> cachedRules.remove(paypalRuleEntity.getRuleId());
            case STATUS_ACTIVE -> cachedRules.put(paypalRuleEntity.getRuleId(),
                paypalRuleEntity);
            default -> log.warn("Unknown status:{}", status);
          }
        });
  }

  @Override
  public Boolean getPaypalHasAccess(String channelId, String country, String hotelId) {
    return cachedRules.values().stream()
        .anyMatch(rule -> channelId.contains(rule.getChannelId()) && country.equalsIgnoreCase(
            rule.getCountryCode()) && rule.getHotelId().contains(hotelId)) ? Boolean.TRUE : Boolean.FALSE;
  }
}
