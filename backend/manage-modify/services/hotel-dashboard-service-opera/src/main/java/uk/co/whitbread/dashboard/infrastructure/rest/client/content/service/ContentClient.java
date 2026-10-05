package uk.co.whitbread.dashboard.infrastructure.rest.client.content.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.dashboard.infrastructure.rest.client.content.model.RoomTypeDto;
import uk.co.whitbread.dashboard.infrastructure.rest.client.content.service.properties.ContentProperties;
import uk.co.whitbread.dashboard.infrastructure.rest.client.exception.ContentClientException;


@Component
@RequiredArgsConstructor
@Slf4j
public class ContentClient {

  private final WebClient contentWebClient;
  private final ContentProperties contentProperties;

  public RoomTypeDto getRoomTypeInformation(String country, String language, String brand) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("country", country);
    queryParams.add("language", language);
    queryParams.add("brand", brand);

    return contentWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(contentProperties.getRoomTypeEndpoint()).queryParams(queryParams).build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(ContentClientException.class))
        .bodyToMono(RoomTypeDto.class)
        .doOnError(
            e -> ExceptionLogger.log(log, e, String.format("Error while trying to retrieve room type information for "
                    + "country = %s, language = %s, brand = %s", country, language, brand)))
        .block();
  }
}
