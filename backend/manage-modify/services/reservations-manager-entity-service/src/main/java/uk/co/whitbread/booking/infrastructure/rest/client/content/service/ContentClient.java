package uk.co.whitbread.booking.infrastructure.rest.client.content.service;

import static uk.co.whitbread.booking.infrastructure.rest.client.content.config.ContentConstants.BRAND;
import static uk.co.whitbread.booking.infrastructure.rest.client.content.config.ContentConstants.BRND_PARAM;
import static uk.co.whitbread.booking.infrastructure.rest.client.content.config.ContentConstants.CHNL_PARAM;
import static uk.co.whitbread.booking.infrastructure.rest.client.content.config.ContentConstants.CNTRY_PARAM;
import static uk.co.whitbread.booking.infrastructure.rest.client.content.config.ContentConstants.HTLID_PARAM;
import static uk.co.whitbread.booking.infrastructure.rest.client.content.config.ContentConstants.LNG_PARAM;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.booking.infrastructure.rest.client.content.exceptions.ContentException;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.in.HotelInformationRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.in.MealsRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.HotelInformationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.MealsInfoResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.RateInformationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.properties.ContentProperties;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.exceptions.InternalBasketException;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.ReservationRateInformationRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationRateInformationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@Component
public class ContentClient {

  private final WebClient contentWebClient;
  private final ContentProperties contentProperties;

  public ContentClient(
      @Qualifier("contentWebClient") WebClient contentWebClient,
      ContentProperties contentProperties) {
    this.contentWebClient = contentWebClient;
    this.contentProperties = contentProperties;
  }

  public RateInformationResponseDto getHotelRateInformation(String country, String language, String hotelId) {
    return contentWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(contentProperties.getHotelRateInformationEndpoint())
            .queryParam(CNTRY_PARAM, country)
            .queryParam(LNG_PARAM, language)
            .queryParam(BRND_PARAM, BRAND)
            .queryParam(HTLID_PARAM, hotelId).build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeaderResponse(log, response);
          return response.bodyToMono(ContentException.class);
        })
        .bodyToMono(RateInformationResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format("Error while trying to get hotel rate information for hotelId=%s",
                hotelId)))
        .block();
  }

  public ReservationRateInformationResponseDto getRateInformation(
      ReservationRateInformationRequestDto request) {
    return contentWebClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(
                                contentProperties.getOperaReservationRateInformationEndpoint())
                        .queryParam(BRND_PARAM, BRAND)
                        .queryParam(CNTRY_PARAM, request.getCountry())
                        .queryParam(LNG_PARAM, request.getLanguage())
                        .queryParam(HTLID_PARAM, request.getHotelId())
                        .queryParam(CHNL_PARAM, request.getChannel().toUpperCase()).build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                  WebClientUtils.logErrorHeaderResponse(log, response);
                  return response.bodyToMono(InternalBasketException.class);
                })
                .bodyToMono(ReservationRateInformationResponseDto.class)
                .doOnError(exception -> ExceptionLogger.log(log, exception,
                    "Error while trying to get reservation rate information"))
                .block();
  }

  public MealsInfoResponseDto getMealsInformation(final MealsRequestDto request) {
    return contentWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(
                contentProperties.getAemMealsInfoEndpoint())
            .queryParam(CNTRY_PARAM, request.getCountry())
            .queryParam(LNG_PARAM, request.getLanguage())
            .queryParam(HTLID_PARAM, request.getHotelId())
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(InternalBasketException.class);
        })
        .bodyToMono(MealsInfoResponseDto.class)
        .doOnError(e -> log.error(
            "Error while trying to meals information from content service ", e))
        .block();
  }

  public HotelInformationResponseDto getHotelInformation(final HotelInformationRequestDto request) {
    return contentWebClient
          .get()
          .uri(uriBuilder -> uriBuilder
                .path(contentProperties.getHotelInfoEndpoint())
                .queryParam("channel", request.getChannel())
                .queryParam("subchannel", request.getSubchannel())
                .queryParam("language", request.getLanguage())
                .queryParam("country", request.getCountry())
                .build(request.getHotelId()))
          .retrieve()
          .onStatus(HttpStatusCode::isError, response -> {
            WebClientUtils.logErrorHeaderResponse(log, response);
            return response
                  .bodyToMono(ContentException.class)
                  .flatMap(Mono::error);
          })
          .bodyToMono(HotelInformationResponseDto.class)
          .doOnError(e -> log.error(
                "Error while trying to get hotel information from content service ", e))
          .block();
  }
}
