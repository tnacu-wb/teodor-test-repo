package uk.co.whitbread.content.infrastructure.rest.client.roomtype.adapter;

import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_ROOM_TYPE_EXCEPTION;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.model.in.RoomTypeDto;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.model.out.RoomTypeRequestAemDto;

@Slf4j
@RequiredArgsConstructor
@Component
public class RoomTypeAemClient {

  private final AemProperties aemProperties;
  private final WebClient aemWebClient;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
          value = "RoomTypeCache", key = "{#roomTypeRequestAemDto.country, "
          + "#roomTypeRequestAemDto.language, #roomTypeRequestAemDto.brand}")
  public RoomTypeDto getRoomType(
      RoomTypeRequestAemDto roomTypeRequestAemDto) {
    log.debug("Entered getRoomType with country={}, language={}, brand={}",
        roomTypeRequestAemDto.getCountry(), roomTypeRequestAemDto.getLanguage(),
        roomTypeRequestAemDto.getBrand());
    return aemWebClient.get()
        .uri(
            uriBuilder -> uriBuilder.path(aemProperties.getRoomTypeEndpoint())
                .build(roomTypeRequestAemDto.getCountry(),
                    roomTypeRequestAemDto.getLanguage(),
                    roomTypeRequestAemDto.getBrand()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_ROOM_TYPE_EXCEPTION,
                  String.format("Unable to get room type with country=%s, language=%s, brand=%s",
                      roomTypeRequestAemDto.getCountry(), roomTypeRequestAemDto.getLanguage(),
                      roomTypeRequestAemDto.getBrand()),
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(RoomTypeDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception)).block();
  }
}
