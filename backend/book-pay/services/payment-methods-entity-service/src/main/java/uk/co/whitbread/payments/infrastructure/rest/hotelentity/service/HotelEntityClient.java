package uk.co.whitbread.payments.infrastructure.rest.hotelentity.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.payments.domain.exception.HotelInfoException;
import uk.co.whitbread.payments.infrastructure.rest.client.util.WebClientUtils;
import uk.co.whitbread.payments.infrastructure.rest.hotelentity.model.out.HotelInfoDto;
import uk.co.whitbread.payments.infrastructure.rest.hotelentity.properties.HotelEntityClientProperties;

@Component
@Slf4j
public class HotelEntityClient {

  private final WebClient hotelEntityWebClient;
  private final HotelEntityClientProperties hotelEntityClientProperties;

  public HotelEntityClient(
      @Qualifier("hotelEntityWebClient") WebClient hotelEntityWebClient,
      HotelEntityClientProperties hotelEntityClientProperties) {
    this.hotelEntityWebClient = hotelEntityWebClient;
    this.hotelEntityClientProperties = hotelEntityClientProperties;
  }


  public HotelInfoDto getHotelInfo(String hotelId) {
    return hotelEntityWebClient
        .get()
        .uri(hotelEntityClientProperties.getHotelInfoEndpoint(), hotelId)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelInfoException.class);
        })
        .bodyToMono(HotelInfoDto.class)
        .block();
  }

}
