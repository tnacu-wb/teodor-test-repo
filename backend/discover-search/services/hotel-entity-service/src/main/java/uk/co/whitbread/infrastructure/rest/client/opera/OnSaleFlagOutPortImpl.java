package uk.co.whitbread.infrastructure.rest.client.opera;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.domain.model.migrationstatus.in.HotelsMigrationStatusRequest;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelMigrationStatusResponse;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelsMigrationStatusResponse;
import uk.co.whitbread.domain.model.opera.out.HotelStatus;
import uk.co.whitbread.domain.ports.secondary.CacheSearchOutPort;
import uk.co.whitbread.domain.ports.secondary.OnSaleFlagOutPort;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelStatusDto;
import uk.co.whitbread.infrastructure.rest.client.opera.mapper.HotelStatusMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class OnSaleFlagOutPortImpl implements OnSaleFlagOutPort {

  private final HotelStatusMapper hotelStatusMapper;
  private final CacheSearchOutPort cacheSearchOutPort;

  @Override
  public HotelsMigrationStatusResponse getOnSaleFlag(
      HotelsMigrationStatusRequest hotelsMigrationStatusRequest) {
    log.debug(
        "Entered getMigrationStatus with hotelsMigrationStatusRequest={}",
        hotelsMigrationStatusRequest);

    List<HotelStatus> hotelStatuses =
        getHotelStatus(hotelsMigrationStatusRequest.getHotelIds());

    List<HotelMigrationStatusResponse> hotelMigrationStatuses = hotelStatuses.stream()
        .map(hotelStatus -> HotelMigrationStatusResponse.builder()
            .hotelId(hotelStatus.getHotelId())
            .pmsSource(hotelStatus.getPmsSource())
            .onSale(hotelStatus.getOnSale())
            .build())
        .toList();

    return HotelsMigrationStatusResponse.builder()
        .migrationStatusList(hotelMigrationStatuses)
        .build();
  }

  @Override
  public List<HotelStatus> getHotelStatus(List<String> hotelIds) {
    log.debug("Fetching hotel status for hotelIds: {}", hotelIds);
    if (hotelIds == null || hotelIds.isEmpty()) {
      return List.of();
    }

    List<HotelStatusDto> hotelStatusDtos =
        cacheSearchOutPort.getOnsaleFlagFromCache(hotelIds);

    log.debug("Received hotel status DTOs: {}", hotelStatusDtos);
    if (hotelStatusDtos == null || hotelStatusDtos.isEmpty()) {
      return List.of();
    }

    return hotelStatusDtos.stream()
        .map(hotelStatusMapper::toDomainModel)
        .toList();
  }
}

