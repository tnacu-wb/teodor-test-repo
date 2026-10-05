package uk.co.whitbread.payments.infrastructure.rest.client.hotels.service;

import static uk.co.whitbread.payments.infrastructure.rest.client.config.WebClientConstants.COUNTRY;
import static uk.co.whitbread.payments.infrastructure.rest.client.config.WebClientConstants.LANGUAGE;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.payments.domain.exception.HotelInfoException;
import uk.co.whitbread.payments.infrastructure.rest.client.hotels.model.out.HotelInfoDto;
import uk.co.whitbread.payments.infrastructure.rest.client.hotels.service.properties.HotelInfoClientProperties;
import uk.co.whitbread.payments.infrastructure.rest.client.util.WebClientUtils;

@Component
@Slf4j
public class HotelInfoClient {

  private final WebClient hotelInfoWebClient;
  private final HotelInfoClientProperties hotelInfoClientProperties;

  public HotelInfoClient(
      @Qualifier("hotelInfoWebClient") WebClient hotelInfoWebClient,
      HotelInfoClientProperties hotelInfoClientProperties) {
    this.hotelInfoWebClient = hotelInfoWebClient;
    this.hotelInfoClientProperties = hotelInfoClientProperties;
  }

  public HotelInfoDto findHotelPaymentDetails(String hotelCode, String country,
      String language) {
    String errorMessage = String.format("Error while trying to get hotel payment details "
        + "from content service with hotelCode=%s, country=%s and language=%s",
        hotelCode, country, language);
    return hotelInfoWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(hotelInfoClientProperties.getPaymentInformationEndpoint())
            .queryParam(COUNTRY, country)
            .queryParam(LANGUAGE, language)
            .build(hotelCode))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(HotelInfoException.class);
        })
        .bodyToMono(HotelInfoDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, errorMessage))
        .block();
  }

}
