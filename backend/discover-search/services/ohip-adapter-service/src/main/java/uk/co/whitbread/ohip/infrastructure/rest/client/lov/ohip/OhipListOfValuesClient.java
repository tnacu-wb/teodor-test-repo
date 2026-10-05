package uk.co.whitbread.ohip.infrastructure.rest.client.lov.ohip;

import static uk.co.whitbread.ohip.ErrorCode.OHIP_GET_CANCELLATION_REASONS_EXCEPTION;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.CANCELLATION_REASONS_LOV_NAME;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.lov.ListOfValues;
import uk.co.whitbread.ohip.infrastructure.rest.client.lov.ohip.exceptions.ListOfValuesException;
import uk.co.whitbread.ohip.infrastructure.rest.client.lov.ohip.properties.ListOfValuesOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class OhipListOfValuesClient {

  private final WebClient ohipWebClient;

  private final ListOfValuesOhipProperties listOfValuesOhipProperties;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "ListOfCancellationReasonsCache", key = "#hotelId")
  public ListOfValues getListOfCancellationReasons(String hotelId) {
    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(
                    listOfValuesOhipProperties.getCancellationReasonsEndpoint())
                .build(CANCELLATION_REASONS_LOV_NAME))
        .headers(httpHeaders -> httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new ListOfValuesException(OHIP_GET_CANCELLATION_REASONS_EXCEPTION,
              String.format("Error while trying to get list of cancellation "
                  + "reasons for hotelId=%s ", hotelId
              )));
        })
        .bodyToMono(ListOfValues.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

}
