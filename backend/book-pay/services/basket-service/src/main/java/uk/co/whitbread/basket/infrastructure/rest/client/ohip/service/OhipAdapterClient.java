package uk.co.whitbread.basket.infrastructure.rest.client.ohip.service;

import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.HOTEL_ID;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.REFERENCE_IDS;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.ReservationConstants.RESERVATION_IDS;
import static uk.co.whitbread.basket.infrastructure.rest.utils.WebClientUtils.logErrorResponse;

import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.model.basket.out.ReservationInfoPaymentType;
import uk.co.whitbread.basket.generated.models.ohip.BillingAddressCaptRequestDto;
import uk.co.whitbread.basket.generated.models.ohip.NegotiatedRatesResponseDto;
import uk.co.whitbread.basket.generated.models.ohip.PreCheckInRequestDto;
import uk.co.whitbread.basket.generated.models.ohip.PreCheckInResponse;
import uk.co.whitbread.basket.generated.models.ohip.ReservationByBasketRefResponseDto;
import uk.co.whitbread.basket.generated.models.ohip.UdfsRequestDto;
import uk.co.whitbread.basket.generated.models.ohip.UpdateCustomReferenceNumberRequestDto;
import uk.co.whitbread.basket.generated.models.ohip.UpdateReservationCcAgentIdRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.exceptions.OhipHotelReservationException;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;
import uk.co.whitbread.basket.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;


@Slf4j
@Component
public class OhipAdapterClient {
  private final WebClient ohipAdapterWebClient;
  private final OhipAdapterProperties ohipAdapterProperties;

  public OhipAdapterClient(
      @Qualifier("ohipAdapterWebClient") WebClient ohipAdapterWebClient,
      OhipAdapterProperties ohipAdapterProperties) {
    this.ohipAdapterWebClient = ohipAdapterWebClient;
    this.ohipAdapterProperties = ohipAdapterProperties;
  }

  public void sendReservationBillingAddress(
      BillingAddressCaptRequestDto billingAddressUpdateRequest) {
    log.debug("calling ohip adapter service with request {}", billingAddressUpdateRequest);
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getReservationBillingAddressEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(billingAddressUpdateRequest), BillingAddressCaptRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(PaymentException.class);
        })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to update reservation billing address"))
        .block();
  }

  public void sendUpdateCustomReferenceNumber(UpdateCustomReferenceNumberRequestDto requestDto) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getCustomReferenceNumberEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(requestDto), UpdateCustomReferenceNumberRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(OhipHotelReservationException.class);
        })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to update custom reference number. An error was returned by Ohip Adapter!"))
        .block();
  }

  public void sendUpdateReservationCcAgentId(UpdateReservationCcAgentIdRequestDto requestDto) {
    ohipAdapterWebClient
            .put()
            .uri(ohipAdapterProperties.getUpdateReservationCcAgentIdEndpoint())
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestDto),
                    UpdateReservationCcAgentIdRequestDto.class)
            .retrieve()
            .onStatus(HttpStatusCode::isError, response -> {
              logErrorResponse(log, response);
              return response.bodyToMono(OhipHotelReservationException.class);
            })
            .bodyToMono(ResponseEntity.class)
            .doOnError(ex -> ExceptionLogger.log(log, ex,
                    String.format("Error while trying to save CC agent ID for reservationIds=%s",
                            requestDto.getReservationIds())))
            .block();
  }

  public NegotiatedRatesResponseDto getNegotiatedRates(String profileId) {
    return ohipAdapterWebClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(ohipAdapterProperties.getGetNegotiatedRatesEndpoint())
                        .build(profileId))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                  WebClientUtils.logErrorHeader(log, response);
                  return response.bodyToMono(OhipHotelReservationException.class);
                })
                .bodyToMono(NegotiatedRatesResponseDto.class)
                .doOnError(exception -> ExceptionLogger.log(log, exception,
                        "Error while trying to get the negotiated rates."))
                .block();
  }

  public void updateCharacterUdfs(UdfsRequestDto udfsDto) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getUpdateCharacterUdfsEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(udfsDto), UdfsRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(OhipHotelReservationException.class);
        })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to update udfs. An error was returned by Ohip Adapter!"))
        .block();
  }

  public List<ReservationInfoPaymentType> getPaymentType(String hotelId, Set<String> reservationIds) {
    return ohipAdapterWebClient
      .get()
      .uri(uriBuilder -> uriBuilder.path(ohipAdapterProperties.getReservationsPaymentTypeByReservationIds())
        .queryParam(HOTEL_ID, hotelId)
        .queryParam(REFERENCE_IDS, reservationIds)
        .build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return response.bodyToMono(OhipHotelReservationException.class);
        })
        .bodyToMono(new ParameterizedTypeReference<List<ReservationInfoPaymentType>>() {})
      .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
                    "Error while trying to get reservations  payment type for hotelId=%s, reservation ids=%s",
                    hotelId, reservationIds)))
      .block();
  }

  public ResponseEntity<PreCheckInResponse> saveReservationPreRegister(PreCheckInRequestDto preCheckInRequestDto) {
    return ohipAdapterWebClient
        .post()
        .uri(ohipAdapterProperties.getSaveReservationPreRegister())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(preCheckInRequestDto), PreCheckInRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(OhipHotelReservationException.class).flatMap(Mono::error);
        })
        .toEntity(PreCheckInResponse.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to save pre-checkIn status for hotelId=%s and reservationId %s",
            preCheckInRequestDto.getHotelId(),
            preCheckInRequestDto.getReservationId())))
        .block();
  }

  public ReservationByBasketRefResponseDto getReservationDetails(String hotelId, String reservationId) {
    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder ->
            uriBuilder.path(ohipAdapterProperties.getGetReservationByBasketEndpoint())
                .queryParam(RESERVATION_IDS, reservationId)
                .queryParam(HOTEL_ID, hotelId)
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return response.bodyToMono(OhipHotelReservationException.class);
        })
        .bodyToMono(ReservationByBasketRefResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to get reservation details for hotelId=%s, reservationsIds=%s",
            hotelId, reservationId)))
        .block();
  }
}
