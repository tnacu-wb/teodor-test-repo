package uk.co.whitbread.basket.infrastructure.rest.client.content.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.basket.generated.models.content.BusinessNotesResponseDto;
import uk.co.whitbread.basket.generated.models.content.HotelPaymentInformationDto;
import uk.co.whitbread.basket.infrastructure.rest.client.content.service.exceptions.ContentException;
import uk.co.whitbread.basket.infrastructure.rest.client.content.service.properties.ContentProperties;
import uk.co.whitbread.basket.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

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

  public HotelPaymentInformationDto getHotelPaymentDetails(String hotelCode, String country,
      String language) {
    return contentWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(contentProperties.getPaymentInformationEndpoint())
            .queryParam("country", country)
            .queryParam("language", language)
            .build(hotelCode))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(ContentException.class);
        })
        .bodyToMono(HotelPaymentInformationDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while trying to get hotel payment details from content service with "
                    + "hotelCode=%s, country=%s and language=%s",
                hotelCode, country, language)))
        .block();
  }

  public BusinessNotesResponseDto getBusinessNotes(String language) {
    return contentWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(contentProperties.getBusinessNotesEndpoint())
            .queryParam("lang", language)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(ContentException.class);
        })
        .bodyToMono(BusinessNotesResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while trying to get business notes from content service with lang=%s",
                language)))
        .block();
  }
}
