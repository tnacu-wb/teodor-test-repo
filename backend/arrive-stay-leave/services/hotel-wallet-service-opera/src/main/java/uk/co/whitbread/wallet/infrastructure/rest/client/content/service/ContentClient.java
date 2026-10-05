package uk.co.whitbread.wallet.infrastructure.rest.client.content.service;

import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.COUNTRY;
import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.LANGUAGE;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.basket.generated.models.content.HotelInformationDto;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.wallet.infrastructure.rest.client.content.service.exceptions.ContentException;
import uk.co.whitbread.wallet.infrastructure.rest.client.content.service.properties.ContentProperties;
import uk.co.whitbread.wallet.infrastructure.rest.utils.WebClientUtils;

@Component
@Slf4j
public class ContentClient {

  private final WebClient contentWebClient;
  private final ContentProperties contentProperties;

  public ContentClient(
      @Qualifier("contentWebClient") WebClient contentWebClient,
      ContentProperties contentProperties) {

    this.contentWebClient = contentWebClient;
    this.contentProperties = contentProperties;
  }

  public HotelInformationDto getHotelInformation(String country, String language, String hotelId) {
    return contentWebClient.get().uri(
            uriBuilder ->
                uriBuilder.path(contentProperties.getHotelInformationEndpoint())
                    .queryParam(COUNTRY, country)
                    .queryParam(LANGUAGE, language)
                    .build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorStatusAndHeaders(log, response);
          return response.bodyToMono(ContentException.class);
        })
        .bodyToMono(HotelInformationDto.class)
        .doOnError(e ->
            ExceptionLogger.log(log, e, String.format(
                "Error while trying to get hotel information from content service for hotelId=%s",
                hotelId)))
        .block();
  }
}
