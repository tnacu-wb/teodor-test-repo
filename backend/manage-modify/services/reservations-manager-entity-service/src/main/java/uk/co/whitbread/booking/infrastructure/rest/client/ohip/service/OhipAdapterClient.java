package uk.co.whitbread.booking.infrastructure.rest.client.ohip.service;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.exceptions.HotelReservationOhipException;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.in.PackageGroupRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.HotelInfoDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.PackageGroupOhipResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.ReservationInfoPaymentTypeDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.ReservationLightweightResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;
import uk.co.whitbread.booking.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Component
@Slf4j
@RequiredArgsConstructor
public class OhipAdapterClient {
  private static final String HOTEL_ID = "hotelId";
  private static final String REFERENCE_IDS = "reservationIds";
  private final WebClient ohipAdapterWebClient;
  private final OhipAdapterProperties ohipAdapterProperties;

  public List<HotelInfoDto> fetchMultipleHotelsInfo(Set<String> hotelIds) {
    return Flux.fromIterable(hotelIds)
        .flatMap(hotelId -> fetchSingleHotelInfo(hotelId)
            .map(dto -> {
              dto.setHotelId(hotelId);
              return dto;
            }), 5)
        .collectList()
        .block();
  }

  public List<ReservationInfoPaymentTypeDto> getReservationsPaymentTypeByReservationIds(
      String hotelId, Set<String> reservationIds) {
    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder ->
            uriBuilder.path(ohipAdapterProperties.getReservationsPaymentTypeByReservationIds())
                .queryParam(HOTEL_ID, hotelId)
                .queryParam(REFERENCE_IDS, reservationIds)
                .build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(
            new org.springframework.core.ParameterizedTypeReference<List<ReservationInfoPaymentTypeDto>>() {
            })
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to get reservations  payment type for hotelId=%s, reservation ids=%s",
            hotelId, reservationIds))
        )
        .block();
  }

  private Mono<HotelInfoDto> fetchSingleHotelInfo(String hotelId) {
    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(ohipAdapterProperties.getHotelInfoEndpoint())
            .build(hotelId))
        .retrieve()
        .bodyToMono(HotelInfoDto.class)
        .onErrorResume(e -> {
          ExceptionLogger.log(log, e,
              String.format(
                  "Error while trying to get hotel info for hotel ID %s. %s",
                  hotelId, "asd"));
          return Mono.empty();
        });
  }

  public ReservationLightweightResponseDto getLightweightReservationsByIds(String hotelId,
                                                                           Set<String> reservationIds) {
    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(ohipAdapterProperties.getLightweightReservationsByIdsEndpoint())
            .queryParam(HOTEL_ID, hotelId)
            .queryParam(REFERENCE_IDS, String.join(",", reservationIds))
            .build())
        .retrieve()
        .bodyToMono(ReservationLightweightResponseDto.class)
        .onErrorResume(e -> {
          ExceptionLogger.log(log, e,
              String.format(
                  "Error while trying to get reservations for reservationIds %s. %s",
                  String.join(",", reservationIds), "asd"));
          return Mono.empty();
        })
        .block();
  }

  public PackageGroupOhipResponseDto getPackageGroups(
      PackageGroupRequestDto packageGroupRequestDto) {
    log.info("Fetching package groups from OHIP");

    return ohipAdapterWebClient
        .post()
        .uri(ohipAdapterProperties.getPackageGroupsEndPoint())
        .bodyValue(packageGroupRequestDto)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(PackageGroupOhipResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, "Failed to fetch package groups from OHIP"))
        .block();
  }

}
