package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.inventory.opera;

import static uk.co.whitbread.ondemandrefreshservice.ErrorCode.OPERA_HOTEL_INVENTORY_EXCEPTION;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.DAILY_INVENTORY;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.DATE_RANGE_END;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.DATE_RANGE_START;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.HOTEL_ID_HEADER;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.HOUSE_LEVEL;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.ROOM_COUNT_REQUESTED;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.util.WebClientUtils.logErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaProperties;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.HotelInventoryResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.inventory.exceptions.HotelInventoryException;

@RequiredArgsConstructor
@Slf4j
@Component
public class HotelInventoryClient {

  private final WebClient operaWebClient;
  private final OperaProperties operaProperties;


  public HotelInventoryResponse getHotelInventory(String hotelId, String dateRangeStart,
      String dateRangeEnd, boolean dailyInventory, int roomCountRequested, boolean houseLevel) {
    return operaWebClient.get().uri(uriBuilder -> uriBuilder.path(operaProperties.getHotelInventoryEndpoint())
            .queryParam(DATE_RANGE_START, dateRangeStart).queryParam(DATE_RANGE_END, dateRangeEnd)
            .queryParam(DAILY_INVENTORY, dailyInventory).queryParam(ROOM_COUNT_REQUESTED, roomCountRequested)
            .queryParam(HOUSE_LEVEL, houseLevel).build(hotelId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId)).retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelInventoryException(OPERA_HOTEL_INVENTORY_EXCEPTION,
              "Error while trying to get inventory details."));
        }).bodyToMono(HotelInventoryResponse.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}