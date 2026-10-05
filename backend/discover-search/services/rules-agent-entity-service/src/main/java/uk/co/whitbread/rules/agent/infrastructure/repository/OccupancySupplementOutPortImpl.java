package uk.co.whitbread.rules.agent.infrastructure.repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.agent.domain.model.out.OccupancySupplement;
import uk.co.whitbread.rules.agent.domain.ports.secondary.OccupancySupplementRepositoryOutPort;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.OccupancySupplementEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.OccupancySupplementEntity;

@Slf4j
public class OccupancySupplementOutPortImpl implements OccupancySupplementRepositoryOutPort {

  private static final String STATUS_ACTIVE = "ACTIVE";
  private static final String STATUS_INACTIVE = "INACTIVE";

  private final OccupancySupplementRepository occupancySupplementRepository;
  private final ConcurrentHashMap<String, OccupancySupplementEntity> cachedRules;
  private final OccupancySupplementEntityMapper occupancySupplementEntityMapper;
  private LocalDateTime lastCacheUpdate;


  public OccupancySupplementOutPortImpl(OccupancySupplementRepository occupancySupplementRepository,
                                        OccupancySupplementEntityMapper occupancySupplementEntityMapper) {
    cachedRules = new ConcurrentHashMap<>();
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    this.occupancySupplementRepository = occupancySupplementRepository;
    this.occupancySupplementEntityMapper = occupancySupplementEntityMapper;
  }

  @Override
  public void cacheRules() {
    occupancySupplementRepository.findAllByStatusActive()
        .forEach(occupancySupplementEntity -> cachedRules.put(occupancySupplementEntity.getHotelId(),
            occupancySupplementEntity));
  }

  @Override
  public void updateCache() {
    lastCacheUpdate = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    occupancySupplementRepository.findAllUpdatedAfter(lastCacheUpdate)
        .forEach(occupancySupplementEntity -> {
          var status = occupancySupplementEntity.getStatus();
          switch (status) {
            case STATUS_INACTIVE -> cachedRules.remove(occupancySupplementEntity.getHotelId());
            case STATUS_ACTIVE -> cachedRules.put(occupancySupplementEntity.getHotelId(),
                occupancySupplementEntity);
            default -> log.warn("Unknown status:{}", status);
          }
        });
  }

  @Override
  public Optional<OccupancySupplement> findRule(String hotelId) {
    return Optional.ofNullable(cachedRules.get(hotelId))
        .map(occupancySupplementEntityMapper::toModel);
  }
}
