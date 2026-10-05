package uk.co.whitbread.basket.infrastructure.rest.client.reservation.service;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.domain.exception.DiscountInvalidAmountException;
import uk.co.whitbread.basket.generated.models.reservation.AttachReservationProfileRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.BusinessItemsRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.ConfirmReservationResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.DepositFoliosRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.DepositFoliosResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.DepositsResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.MarketingPreferencesResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationByBasketRefRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationByBasketRefResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationGuestRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.SpecialRequestsDto;
import uk.co.whitbread.basket.generated.models.reservation.UpdateDiscountRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.UpdateReservationAlertsRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.UpdateReservationSingleCallRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.client.config.ReservationConstants;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.service.properties.ReservationClientProperties;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.ReservationProfilesDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.CompanyQuestionAndAnswerDetailsRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@Component
public class ReservationClient {

  private static final String HOTEL_RESERVATION_ENTITY_SERVICE_ERROR = "Hotel Reservation Entity Service Error";
  private final WebClient reservationWebClient;
  private final ReservationClientProperties reservationClientProperties;

  public ReservationClient(
      @Qualifier("reservationWebClient") WebClient reservationWebClient,
      ReservationClientProperties reservationClientProperties) {
    this.reservationWebClient = reservationWebClient;
    this.reservationClientProperties = reservationClientProperties;
  }

  public ReservationByBasketRefResponseDto getReservationsByBasketReference(
      String basketReference, ReservationByBasketRefRequestDto requestDto) {
    /* TODO replace this call with a specialized one that can bring the necessary data
    without invoking basket service back */
    return reservationWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(reservationClientProperties.getReservationEndpoint())
            .queryParam("priceBreakDownNeeded", requestDto.getPriceBreakDownNeeded())
            .queryParam("rateInfoNeeded", requestDto.getRateInfoNeeded())
            .build(basketReference))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          int statusCode = response.statusCode().value();
          return response.releaseBody().then(Mono.error(new HotelReservationException(
              "Reservation not found for basketReference=" + basketReference,
              "HTTP " + statusCode + " from Reservation Entity Service",
              null, statusCode)));
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(HotelReservationException.class);
        })
        .bodyToMono(ReservationByBasketRefResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while trying to get reservation by basketReference=%s. "
                    + "An error was returned by Reservation Entity!",
                basketReference)))
        .block();
  }

  public void sendPutUpdateDiscount(
      UpdateDiscountRequestDto dto) {
    reservationWebClient
        .put()
        .uri(uriBuilder ->
            uriBuilder.path(reservationClientProperties.getDiscountEndpoint()).build())
        .body(Mono.just(dto), UpdateDiscountRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError,
            response -> {
              WebClientUtils.logErrorHeader(log, response);
              return response.bodyToMono(DiscountInvalidAmountException.class);
            })
        .onStatus(HttpStatusCode::is5xxServerError,
            response -> {
              WebClientUtils.logErrorHeader(log, response);
              return response.bodyToMono(HotelReservationException.class);
            })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            HOTEL_RESERVATION_ENTITY_SERVICE_ERROR))
        .block();
  }

  public void sendUpdateCompanyQuestionAndAnswerDetailsRequests(
      CompanyQuestionAndAnswerDetailsRequestDto companyQuestionAndAnswerDetailsRequestDto) {
    reservationWebClient.put().uri(
            uriBuilder -> uriBuilder.path(
                reservationClientProperties.getCompanyQuestionAndAnswerEndpoint()).build())
        .body(Mono.just(companyQuestionAndAnswerDetailsRequestDto),
            CompanyQuestionAndAnswerDetailsRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::is5xxServerError,
            response -> {
              WebClientUtils.logErrorHeader(log, response);
              return response.bodyToMono(HotelReservationException.class);
            })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Hotel Reservation Entity Service Error, while update the Company Question And Answer(s)"))
        .block();
  }

  public void sendPutUpdateBusinessItems(
      BusinessItemsRequestDto businessItemsRequestDto) {
    reservationWebClient.put()
        .uri(uriBuilder ->
            uriBuilder.path(reservationClientProperties.getBusinessItemsEndpoint()).build())
        .body(Mono.just(businessItemsRequestDto), BusinessItemsRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::is5xxServerError,
            response -> {
              WebClientUtils.logErrorHeader(log, response);
              return response.bodyToMono(HotelReservationException.class);
            })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            HOTEL_RESERVATION_ENTITY_SERVICE_ERROR))
        .block();
  }

  public void sendPutReservationsSpecialRequests(SpecialRequestsDto specialRequestsDto) {
    reservationWebClient.put()
        .uri(uriBuilder ->
            uriBuilder.path(reservationClientProperties.getSpecialRequestsEndpoint()).build())
        .body(Mono.just(specialRequestsDto), SpecialRequestsDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::is5xxServerError,
            response -> {
              WebClientUtils.logErrorHeader(log, response);
              return response.bodyToMono(HotelReservationException.class);
            })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            HOTEL_RESERVATION_ENTITY_SERVICE_ERROR))
        .block();
  }

  public DepositsResponseDto getDepositsForReservationId(String hotelId, String reservationId) {
    return reservationWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(reservationClientProperties.getDepositsEndpoint())
            .queryParam(ReservationConstants.HOTEL_ID, hotelId)
            .queryParam(ReservationConstants.RESERVATION_ID, reservationId)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError,
            response -> {
              WebClientUtils.logErrorHeader(log, response);
              return response.bodyToMono(HotelReservationException.class);
            })
        .bodyToMono(DepositsResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            HOTEL_RESERVATION_ENTITY_SERVICE_ERROR))
        .block();
  }

  public MarketingPreferencesResponseDto getMarketingPreferences(String hotelId, String reservationId) {
    return reservationWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(reservationClientProperties.getMarketingPreferencesEndpoint())
            .queryParam("hotelId", hotelId)
            .queryParam("reservationId", reservationId)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(HotelReservationException.class);
        })
        .bodyToMono(MarketingPreferencesResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while trying to get marketingPreferences by hotelId=%s and reservationId=&s."
                    + "An error was returned by Reservation Entity!",
                hotelId, reservationId)))
        .block();
  }

  public void attachProfileToReservations(
      AttachReservationProfileRequestDto attachReservationProfileRequestDto) {

    reservationWebClient.post()
        .uri(uriBuilder ->
            uriBuilder.path(reservationClientProperties.getAttachProfileToReservationsEndpoint()).build())
        .body(Mono.just(attachReservationProfileRequestDto), AttachReservationProfileRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(HotelReservationException.class);
        })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            HOTEL_RESERVATION_ENTITY_SERVICE_ERROR))
        .block();
  }

  public void deleteRoutingInstructions(String hotelId, Set<String> reservationIds) {
    reservationWebClient.delete()
        .uri(uriBuilder -> uriBuilder
            .path(reservationClientProperties.getRoutingInstructionsEndpoint())
            .queryParam(ReservationConstants.HOTEL_ID, hotelId)
            .queryParam(ReservationConstants.RESERVATION_IDS, reservationIds)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(HotelReservationException.class);
        })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            HOTEL_RESERVATION_ENTITY_SERVICE_ERROR))
        .block();
  }

  public ConfirmReservationResponseDto sendPutUpdateReservation(
      UpdateReservationSingleCallRequestDto updateReservationReq) {
    return reservationWebClient
        .put()
        .uri(uriBuilder ->
            uriBuilder.path(reservationClientProperties.getUpdateReservationEndPoint()).build())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(updateReservationReq)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          log.error("UpdateReservation: an error has returned with status code={} ",
              response.statusCode().value());
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(HotelReservationException.class);
        })
        .bodyToMono(ConfirmReservationResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "An error was returned by HotelReservation Service while updating the reservations!"))
        .block();
  }

  public ReservationProfilesDto createProfileIds(ReservationGuestRequestDto guestRequestDto) {
    return reservationWebClient.post()
        .uri(uriBuilder ->
            uriBuilder.path(reservationClientProperties.getCreateProfilesEndPoint()).build())
        .bodyValue(guestRequestDto)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(HotelReservationException.class);
        })
        .bodyToMono(ReservationProfilesDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "An error was returned by Reservation Entity during Profile Creation!"))
        .block();
  }

  public void updateReservationAlerts(
      final UpdateReservationAlertsRequestDto updateReservationUdfRequestDto) {
    reservationWebClient.put()
        .uri(uriBuilder -> uriBuilder.path(reservationClientProperties.getUpdateAlertsEndPoint()).build())
        .bodyValue(updateReservationUdfRequestDto)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(HotelReservationException.class);
        })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "An error was returned by HotelReservation Service while updating the "
                + "reservations alerts!"))
        .block();
  }


  public DepositFoliosResponseDto getPreviewDepositsForReservationId(String hotelId,
      Set<String> reservationIds) {
    return reservationWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(reservationClientProperties.getPreviewDepositsEndpoint())
            .queryParam(ReservationConstants.HOTEL_ID, hotelId)
            .queryParam(ReservationConstants.RESERVATION_IDS, reservationIds)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError,
            response -> {
              WebClientUtils.logErrorHeader(log, response);
              return response.bodyToMono(HotelReservationException.class);
            })
        .bodyToMono(DepositFoliosResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            HOTEL_RESERVATION_ENTITY_SERVICE_ERROR))
        .block();
  }


  public void saveDepositsFolios(DepositFoliosRequestDto depositFoliosRequestDto) {
    reservationWebClient.post()
        .uri(uriBuilder -> uriBuilder.path(
                reservationClientProperties.getSaveDepositFoliosEndpoint())
            .build())
        .bodyValue(depositFoliosRequestDto)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(HotelReservationException.class);
        })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            HOTEL_RESERVATION_ENTITY_SERVICE_ERROR))
        .block();
  }
}
