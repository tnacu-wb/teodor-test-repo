package uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service;

import static uk.co.whitbread.digitalkey.domain.logic.CommonConstants.HOTEL_ID;
import static uk.co.whitbread.digitalkey.domain.logic.CommonConstants.RESERVATION_IDS;
import static uk.co.whitbread.digitalkey.infrastructure.rest.client.config.OhipAdapterConstants.EXTERNAL_REFERENCE_ID;
import static uk.co.whitbread.digitalkey.infrastructure.rest.client.utils.WebClientUtils.logErrorResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.digitalkey.domain.model.checkin.in.KioskCheckInRequest;
import uk.co.whitbread.digitalkey.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.exceptions.OhipAdapterException;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.exceptions.HotelReservationNotFoundException;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.exceptions.HotelReservationOhipException;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.utils.WebClientUtils;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.in.UpdateUdfc20Request;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.ReservationByBasketRefResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationDetailsEnhancedDto;

@Component
@Slf4j
public class OhipAdapterClient {

  private final WebClient ohipAdapterWebClient;
  private final OhipAdapterProperties ohipAdapterProperties;

  public OhipAdapterClient(
      @Qualifier("ohipAdapterWebClient") WebClient ohipAdapterWebClient,
      OhipAdapterProperties ohipAdapterProperties) {
    this.ohipAdapterWebClient = ohipAdapterWebClient;
    this.ohipAdapterProperties = ohipAdapterProperties;
  }

  public CheckInResponse doCheckIn(KioskCheckInRequest kioskCheckInRequest) {
    return ohipAdapterWebClient.post()
        .uri(uriBuilder -> uriBuilder
            .path(ohipAdapterProperties.getCheckInEndpoint())
            .build())
        .body(Mono.just(kioskCheckInRequest), KioskCheckInRequest.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(OhipAdapterException.class))
        .bodyToMono(CheckInResponse.class)
        .doOnError(e -> ExceptionLogger.log(log, e))
        .block();
  }

  public ReservationByBasketRefResponseDto getReservationDetails(String hotelId, String reservationIds) {
    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder ->
            uriBuilder.path(ohipAdapterProperties.getReservationsByBasketReservationIds())
                .queryParam(RESERVATION_IDS, reservationIds)
                .queryParam(HOTEL_ID, hotelId)
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return response.bodyToMono(OhipAdapterException.class);
        })
        .bodyToMono(ReservationByBasketRefResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to get reservation details for hotelId=%s, reservationsIds=%s",
            hotelId, reservationIds)))
        .block();
  }

  //ALLIANTS
  public ReservationDetailsEnhancedDto sendGetReservationsByExternalReferenceId(
      String externalReferenceId) {
    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder ->
            uriBuilder.path(ohipAdapterProperties.getExternalReservationEndpoint())
                .queryParam(EXTERNAL_REFERENCE_ID, externalReferenceId)
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationNotFoundException.class).flatMap(Mono::error);
        })
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(ReservationDetailsEnhancedDto.class)
        .switchIfEmpty(Mono.error(new HotelReservationNotFoundException(
            "Reservation not found",
            "Reservation not found for reference: " + externalReferenceId,
            null,
            HttpStatus.NOT_FOUND.value())))
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            "Error while trying to fetch reservation by external reference id."))
        .block();
  }

  public void updateUdfc20(UpdateUdfc20Request updateUdfc20Request) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getUpdateUdfc20Endpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(updateUdfc20Request), UpdateUdfc20Request.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(OhipAdapterException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

}