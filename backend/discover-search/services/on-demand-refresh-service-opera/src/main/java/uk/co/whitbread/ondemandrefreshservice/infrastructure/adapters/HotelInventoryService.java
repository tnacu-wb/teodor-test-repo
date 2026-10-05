package uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.HotelInventoryOutPort;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions.OperaRestApiException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.HotelInventory;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.HotelInventoryInput;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.HotelInventoryResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.inventory.opera.HotelInventoryClient;


@Slf4j
@RequiredArgsConstructor
public class HotelInventoryService implements HotelInventoryOutPort {

  private final HotelInventoryClient hotelInventoryClient;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Override
  public HotelInventoryResponse getHotelInventory(final HotelInventoryInput input) {
    log.debug("Requesting Hotel inventory for input: {}", input);
    final HotelInventoryResponse hotelInventoryResponse =
        hotelInventoryClient.getHotelInventory(input.getHotelId(), input.getDateRangeStart(),
            input.getDateRangeEnd(), input.isDailyInventory(), input.getRoomCountRequested(),
            input.isHouseLevel());
    log.trace("Opera Hotel InventoryResponse: {}", hotelInventoryResponse);
    return hotelInventoryResponse;
  }

  @Override
  public HotelInventory getOperaHotelInventory(final String hotelCode, final LocalDate startDate,
      final LocalDate endDate) {
    log.trace("Executing getHotelInventory()....");
    final HotelInventoryInput inventoryInput = HotelInventoryInput.builder()
        .hotelId(hotelCode)
        .dateRangeStart(startDate.format(formatter))
        .dateRangeEnd(endDate.format(formatter))
        .dailyInventory(true)
        .roomCountRequested(1)
        .houseLevel(false).build();
    final HotelInventoryResponse hotelInventoryRsp = getHotelInventory(
        inventoryInput);
    if (hotelInventoryRsp != null && hotelInventoryRsp.getHotelInventories() != null) {
      return hotelInventoryRsp.getHotelInventories().stream().findFirst().orElseThrow(() -> {
        final String errMsg = "Empty hotel inventory from Opera OHIP API";
        log.error(errMsg);
        return new OperaRestApiException(errMsg);
      });
    }
    return null;
  }
}

