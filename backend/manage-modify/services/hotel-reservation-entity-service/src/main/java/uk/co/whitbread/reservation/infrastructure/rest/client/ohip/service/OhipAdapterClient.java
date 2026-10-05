package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service;

import static java.util.Collections.singletonList;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.ARRIVAL_DATE_PARAM;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.BOOKER_EMAIL;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.BOOKER_LASTNAME;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.BOOKER_PHONE;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.BOOKER_POSTCODE;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.BOOKING_ALLOWANCE_IDS;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.BOOKING_REFERENCE;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.CANCELLATION_DATE;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.COMPANY_NAME;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.EXTERNAL_REFERENCE;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.EXTERNAL_REFERENCE_ID;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.EXTERNAL_REFERENCE_IDS;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.GUEST_LASTNAME;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.LIMIT;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.OFFSET;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.OPERA_UI_CREATED_RSV;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.PACKAGE_CODES;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.PRICE_BREAKDOWN_NEEDED;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.RATE_CODE;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.RATE_INFO_NEEDED;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.RESERVATION_ID;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.RESERVATION_IDS;
import static uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants.THIRD_PARTY_BOOKING_REF_NUMBER;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AttachReservationProfileRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AvailabilityByIdsResponseV2Dto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BookerDetailsCnpRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BusinessItemsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelInformationResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservationResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeLogResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CompanyQuestionAndAnswerDetailsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfirmAmendOnReservationsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfirmReservationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfirmReservationResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CopyReservationsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CopyReservationsResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CreateMemoRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFoliosRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFoliosResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositsResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DonationPackagesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.LinkReservationToLeisureCustomerRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MarketingPreferencesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MemosResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackagesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreCheckInRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlanChangeRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlansResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByBasketRefResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationDetailsEnhancedDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationGuestRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationGuestResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationIdDetailsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackagesRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPreferencesRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationScheduledPackagesRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetailsResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsPackagesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomTypeChangeRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.SearchBookingsResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.SpecialRequestsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateBookerEmailRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateCancellationPoliciesRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateDiscountRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReasonForStayRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReasonForStayResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReservationCcAgentIdRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReservationOverrideReasonsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReservationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReservationsRequestDto;
import uk.co.whitbread.reservation.domain.exceptions.HotelReservationNotFoundException;
import uk.co.whitbread.reservation.domain.exceptions.HotelReservationOhipException;
import uk.co.whitbread.reservation.domain.model.in.AmendSummaryAmountRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationAlertsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationUdfsRequest;
import uk.co.whitbread.reservation.domain.model.out.AmendSummaryAmountResponse;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowancesResponse;
import uk.co.whitbread.reservation.domain.model.out.PreCheckInResponse;
import uk.co.whitbread.reservation.infrastructure.rest.client.changelog.exception.ChangeLogException;
import uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.exceptions.DiscountInvalidAmountException;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.exceptions.HotelAvailabilityException;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.AmendDistributionSingleCallRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.HotelAvailabilityByIdsRequestOhipDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.HotelAvailabilityByIdsRequestOhipV2Dto;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.SearchBookingsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.packages.exceptions.PackagesException;
import uk.co.whitbread.reservation.infrastructure.rest.client.packages.model.in.PackagesRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.HotelAvailabilityByIdsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationProfilesDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CancellationPoliciesResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.UpdateReservationSingleCallResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.utils.WebClientUtils;

@Slf4j
@Component
public class OhipAdapterClient {

  public static final String RATE_PLAN_CODES = "ratePlanCodes";
  private static final String ERROR_WHILE_TRYING_TO_CREATE_RESERVATION_GUESTS =
      "Error while trying to create reservation guests";

  public static final String ARRIVAL_DATE_QUERY_PARAM = "arrivalDate";
  public static final String DEPARTURE_DATE_QUERY_PARAM = "departureDate";
  public static final String ROOM_TYPES_QUERY_PARAM = "roomTypes";
  public static final String PMS_ROOM_TYPES_QUERY_PARAM = "pmsRoomTypes";
  public static final String ADULTS_QUERY_PARAM = "adults";
  public static final String CHILDREN_QUERY_PARAM = "children";
  public static final String COTS_REQUIRED_QUERY_PARAM = "cotsRequired";
  public static final String CHANNEL_QUERY_PARAM = "channel";
  public static final String HOTEL_ID = "hotelId";
  public static final String MEAL_INCLUSIVE_RATE = "mealInclusiveRate";

  private final WebClient ohipAdapterWebClient;
  private final OhipAdapterProperties ohipAdapterProperties;

  public OhipAdapterClient(
      @Qualifier("ohipAdapterWebClient") WebClient ohipAdapterWebClient,
      OhipAdapterProperties ohipAdapterProperties) {
    this.ohipAdapterWebClient = ohipAdapterWebClient;
    this.ohipAdapterProperties = ohipAdapterProperties;
  }

  public ReservationResponseDto createReservation(
      ReservationRequestDto createReservation) {
    return ohipAdapterWebClient
        .post()
        .uri(ohipAdapterProperties.getReservationEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(createReservation), ReservationRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(ReservationResponseDto.class)
        .doOnError(
            ex -> ExceptionLogger.log(log, ex, ERROR_WHILE_TRYING_TO_CREATE_RESERVATION_GUESTS))
        .block();
  }

  public ReservationsDetailsResponseDto getReservationsByBasketReference(
      String hotelId,
      String basketReference, int limit,
      int offset) {
    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder ->
            uriBuilder.path(ohipAdapterProperties.getReservationEndpoint())
                .queryParam(HOTEL_ID, hotelId)
                .queryParam(EXTERNAL_REFERENCE_IDS, basketReference)
                .queryParam(LIMIT, limit)
                .queryParam(OFFSET, offset)
                .build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationNotFoundException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(ReservationsDetailsResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to get reservations by basket reference for hotelId=%s, basketReference=%s",
            hotelId, basketReference))
        )
        .block();
  }

  public ConfirmReservationResponseDto sendConfirmReservationRequest(
      ConfirmReservationRequestDto confirmReservationRequest) {
    return ohipAdapterWebClient
        .post()
        .uri(ohipAdapterProperties.getConfirmReservationEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(confirmReservationRequest), ConfirmReservationRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(ConfirmReservationResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to get reservations by basket reference for reservationId=%s",
            confirmReservationRequest.getReservationId())))
        .block();
  }

  public ReservationByBasketRefResponseDto sendGetReservationsByIds(
      String hotelId,
      List<String> reservationsIds,
      Boolean priceBreakdownNeeded,
      Boolean operaUiCreated,
      Boolean rateInfoNeeded) {
    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder ->
            uriBuilder.path(ohipAdapterProperties.getReservationsByBasketReservationIds())
                .queryParam(RESERVATION_IDS,
                    String.join(",", reservationsIds.stream().map(Object::toString).toList()))
                .queryParam(HOTEL_ID, hotelId)
                .queryParam(PRICE_BREAKDOWN_NEEDED, priceBreakdownNeeded)
                .queryParam(OPERA_UI_CREATED_RSV, operaUiCreated)
                .queryParam(RATE_INFO_NEEDED, rateInfoNeeded)
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationNotFoundException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(ReservationByBasketRefResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to get reservations by ids for hotelId=%s, reservationsIds=%s, "
                + "priceBreakdownNeeded=%s, operaUiCreated=%s, rateInfoNeeded=%s.",
            hotelId, reservationsIds, priceBreakdownNeeded, operaUiCreated, rateInfoNeeded)))
        .block();
  }

  public void sendUpdateReservationPackagesRequest(
      ReservationPackagesRequestDto savePackagesRequestDto) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getReservationsPackagesEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(savePackagesRequestDto), ReservationPackagesRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to save reservation packages for reservationIds=%s",
            savePackagesRequestDto.getReservationsId())))
        .block();

  }

  public ReservationsPackagesResponseDto getReservationsPackagesByIdsRequest(
      String hotelId, List<String> reservationIds, boolean mealInclusiveRate) {

    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(ohipAdapterProperties.getReservationsPackagesEndpoint())
            .queryParam(RESERVATION_IDS, reservationIds)
            .queryParam(HOTEL_ID, hotelId)
            .queryParam(MEAL_INCLUSIVE_RATE, mealInclusiveRate)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(ReservationsPackagesResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public ReservationGuestResponseDto sendReservationGuestRequest(
      ReservationGuestRequestDto guestReservationRequest) {
    return ohipAdapterWebClient
        .post()
        .uri(ohipAdapterProperties.getReservationGuestEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(guestReservationRequest), ReservationGuestRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(ReservationGuestResponseDto.class)
        .doOnError(
            ex -> ExceptionLogger.log(log, ex, ERROR_WHILE_TRYING_TO_CREATE_RESERVATION_GUESTS))
        .block();
  }

  public void sendUpdateRateCodeRequest(
      RatePlanChangeRequestDto updateRateCodeRequestDto) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getUpdateReservationRateCodeEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(updateRateCodeRequestDto), RatePlanChangeRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public void sendUpdateRoomTypeRequest(
      RoomTypeChangeRequestDto updateRoomTypeRequestDto) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getUpdateRoomTypeEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(updateRoomTypeRequestDto), RoomTypeChangeRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(RoomTypeChangeRequestDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public CancelInformationResponseDto sendGetCancelInformationRequest(
      String hotelId,
      Set<String> reservationIds,
      String userDateTime) {

    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(ohipAdapterProperties.getCancelEndpoint())
            .queryParam(RESERVATION_IDS,
                String.join(",", reservationIds.stream().map(Object::toString).toList()))
            .queryParam(HOTEL_ID, hotelId)
            .queryParam("userDateTime", userDateTime)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(CancelInformationResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public CancelReservationResponseDto sendCancelReservationRequest(
      CancelReservationRequestDto cancelReservationRequestDto) {
    return ohipAdapterWebClient
        .post()
        .uri(ohipAdapterProperties.getCancelReservationEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(cancelReservationRequestDto), CancelReservationRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(CancelReservationResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public HotelInfoDto sendGetHotelInformationRequest(String hotelId) {
    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(ohipAdapterProperties.getHotelInfoEndpoint())
            .build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(HotelInfoDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public ReservationIdDetailsDto sendGetReservationsByReservationId(
      String reservationId, String hotelId) {
    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder ->
            uriBuilder.path(ohipAdapterProperties.getReservationIdEndpoint())
                .queryParam(RESERVATION_ID, reservationId)
                .queryParam(HOTEL_ID, hotelId)
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationNotFoundException.class);
        })
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(ReservationIdDetailsDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            "Error while trying to fetch reservation by reservation id."))
        .block();
  }

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
          if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
            log.info(String.format("Reservation with external reference id %s was not found.",
                externalReferenceId));
            return Mono.empty();
          }
          return response.bodyToMono(HotelReservationNotFoundException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(ReservationDetailsEnhancedDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            "Error while trying to fetch reservation by external reference id."))
        .block();
  }

  public void sendUpdateDiscountRequest(
      UpdateDiscountRequestDto updateDiscountRequestDto) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getUpdateDiscountEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(updateDiscountRequestDto), UpdateDiscountRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError,
            response -> {
              WebClientUtils.logErrorResponse(log, response);
              return response.bodyToMono(DiscountInvalidAmountException.class);
            })
        .onStatus(HttpStatusCode::is5xxServerError,
            response -> {
              WebClientUtils.logErrorResponse(log, response);
              return response.bodyToMono(HotelReservationOhipException.class);
            })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public void sendUpdateCompanyQuestionAndAnswerDetailsRequests(
      CompanyQuestionAndAnswerDetailsRequestDto companyQuestionAndAnswerDetailsRequestDto) {
    ohipAdapterWebClient.put().uri(
            uriBuilder -> uriBuilder.path(
                ohipAdapterProperties.getUpdateCompanyQuestionAndAnswerRequestEndpoint()).build())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(companyQuestionAndAnswerDetailsRequestDto),
            CompanyQuestionAndAnswerDetailsRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::is5xxServerError,
            response -> {
              WebClientUtils.logErrorResponse(log, response);
              return response.bodyToMono(HotelReservationOhipException.class);
            })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public AvailabilityByIdsResponseV2Dto getHotelAvailabilityByIdsV2(
      HotelAvailabilityByIdsRequestOhipV2Dto ohipRequest) {
    return ohipAdapterWebClient
        .post()
        .uri(ohipAdapterProperties.getAvailabilityByIdsEndpointV2())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(ohipRequest), HotelAvailabilityByIdsRequestOhipV2Dto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelAvailabilityException.class);
        })
        .bodyToMono(AvailabilityByIdsResponseV2Dto.class)
        .doOnError(e -> log.error("Error while trying to get availability by ids v2", e))
        .block();
  }

  public HotelAvailabilityByIdsDto getHotelAvailabilityByIds(
      HotelAvailabilityByIdsRequestOhipDto requestDto) {
    return ohipAdapterWebClient.get().uri(uriBuilder -> uriBuilder
            .path(ohipAdapterProperties.getAvailabilityByIdsEndpoint())
            .queryParam("hotelIds", String.join(",", requestDto.getHotelIds()))
            .queryParam(ARRIVAL_DATE_QUERY_PARAM, requestDto.getArrivalDate())
            .queryParam(DEPARTURE_DATE_QUERY_PARAM, requestDto.getDepartureDate())
            .queryParam(ROOM_TYPES_QUERY_PARAM, String.join(",", requestDto.getRoomTypes()))
            .queryParam(PMS_ROOM_TYPES_QUERY_PARAM,
                requestDto.getPmsRoomTypes() != null ? String.join(",",
                    requestDto.getPmsRoomTypes().stream().map(Object::toString).toList()) : null)
            .queryParam(ADULTS_QUERY_PARAM, String.join(",",
                requestDto.getAdultsNumber().stream().map(Object::toString).toList()))
            .queryParam(CHILDREN_QUERY_PARAM,
                requestDto.getChildrenNumber() != null ? String.join(",",
                    requestDto.getChildrenNumber().stream()
                        .map(Object::toString).toList()) : String.join(",",
                    Collections.nCopies(requestDto.getRoomTypes().size(), "0")))
            .queryParam(COTS_REQUIRED_QUERY_PARAM,
                requestDto.getCotsRequired() != null ? String.join(",",
                    requestDto.getCotsRequired().stream().map(Object::toString)
                        .toList()) : String.join(",",
                    Collections.nCopies(requestDto.getRoomTypes().size(),
                        "false")))
            .queryParamIfPresent("ratePlanCodes", Optional.ofNullable(
                requestDto.getRatePlanCodes() != null
                    ? String.join(",", requestDto.getRatePlanCodes()) : null))
            .queryParamIfPresent("globalCompanyId", Optional.ofNullable(requestDto.getGlobalCompanyId()))
            .queryParamIfPresent("negotiatedRateDisplaySets",
                Optional.ofNullable(requestDto.getNegotiatedRateDisplaySets()))
            .queryParam(CHANNEL_QUERY_PARAM, requestDto.getChannel())
            .queryParam("subchannel", requestDto.getSubchannel())
            .queryParam("language", requestDto.getLanguage())
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorResponse(log, clientResponse);
          return clientResponse.bodyToMono(HotelAvailabilityException.class);
        })
        .bodyToMono(HotelAvailabilityByIdsDto.class)
        .doOnError(e -> log.error(
            "Error while trying to create HotelAvailabilityByIds for hotelAvailabilityByIdsRequestOhipDto={}",
            requestDto, e))
        .block();
  }

  public Mono<RatePlansResponseDto> getRatePlans(List<String> ratePlanCodes, String hotelId) {
    return ohipAdapterWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(
                ohipAdapterProperties.getRatePlansEndpoint())
            .queryParam(RATE_PLAN_CODES, ratePlanCodes)
            .queryParam(HOTEL_ID, hotelId)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(RatePlansResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex));
  }

  public void sendUpdateBusinessItemsRequest(
      BusinessItemsRequestDto businessItemsRequestDto) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getUpdateBusinessItemsEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(businessItemsRequestDto), BusinessItemsRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public void sendUpdateSpecialRequests(SpecialRequestsDto specialRequestsDto) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getUpdateSpecialRequestsEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(specialRequestsDto), SpecialRequestsDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public SearchBookingsResponseDto sendSearchBookings(
      SearchBookingsRequestDto searchBookingsRequestDto) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    if (StringUtils.isNotBlank(searchBookingsRequestDto.getBookingReference())) {
      params.put(BOOKING_REFERENCE, singletonList(searchBookingsRequestDto.getBookingReference()));
    }
    if (StringUtils.isNotBlank(searchBookingsRequestDto.getBookerLastName())) {
      params.put(BOOKER_LASTNAME, singletonList(searchBookingsRequestDto.getBookerLastName()));
    }
    if (StringUtils.isNotBlank(searchBookingsRequestDto.getGuestLastName())) {
      params.put(GUEST_LASTNAME, singletonList(searchBookingsRequestDto.getGuestLastName()));
    }
    if (StringUtils.isNotBlank(searchBookingsRequestDto.getBookerPostcode())) {
      params.put(BOOKER_POSTCODE, singletonList(searchBookingsRequestDto.getBookerPostcode()));
    }
    if (StringUtils.isNotBlank(searchBookingsRequestDto.getHotelId())) {
      params.put(HOTEL_ID, singletonList(searchBookingsRequestDto.getHotelId()));
    }
    if (StringUtils.isNotBlank(searchBookingsRequestDto.getBookerEmail())) {
      params.put(BOOKER_EMAIL, singletonList(searchBookingsRequestDto.getBookerEmail()));
    }
    if (StringUtils.isNotBlank(searchBookingsRequestDto.getBookerPhone())) {
      params.put(BOOKER_PHONE, singletonList(searchBookingsRequestDto.getBookerPhone()));
    }
    if (StringUtils.isNotBlank(searchBookingsRequestDto.getArrivalDate())) {
      params.put(ARRIVAL_DATE_PARAM, singletonList(searchBookingsRequestDto.getArrivalDate()));
    }
    if (StringUtils.isNotBlank(searchBookingsRequestDto.getCancellationDate())) {
      params.put(CANCELLATION_DATE, singletonList(searchBookingsRequestDto.getCancellationDate()));
    }
    if (StringUtils.isNotBlank(searchBookingsRequestDto.getCompanyName())) {
      params.put(COMPANY_NAME, singletonList(searchBookingsRequestDto.getCompanyName()));
    }
    if (StringUtils.isNotBlank(searchBookingsRequestDto.getThirdPartyBookingReferenceNumber())) {
      params.put(THIRD_PARTY_BOOKING_REF_NUMBER,
          singletonList(searchBookingsRequestDto.getThirdPartyBookingReferenceNumber()));
    }

    return ohipAdapterWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(ohipAdapterProperties.getSearchBookingsEndpoint())
            .queryParams(params)
            .queryParam(LIMIT, searchBookingsRequestDto.getLimit())
            .queryParam(OFFSET, searchBookingsRequestDto.getOffset())
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(SearchBookingsResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public void sendUpdateReasonForStayRequest(
      UpdateReasonForStayRequestDto updateReasonForStayRequestDto) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getUpdateReasonForStayEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(updateReasonForStayRequestDto), UpdateReasonForStayRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(UpdateReasonForStayResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public void sendUpdateReservationOverrideReasons(
      UpdateReservationOverrideReasonsRequestDto updateReservationOverrideReasonsRequestDto) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getUpdateReservationOverrideReasonsEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(updateReservationOverrideReasonsRequestDto),
            UpdateReservationOverrideReasonsRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to save override reasons for reservationIds=%s",
                updateReservationOverrideReasonsRequestDto.getReservationIds())))
        .block();
  }

  public void sendUpdateReservationCcAgentId(
      UpdateReservationCcAgentIdRequestDto updateReservationCcAgentIdRequestDto) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getUpdateReservationCcAgentIdEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(updateReservationCcAgentIdRequestDto),
            UpdateReservationCcAgentIdRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to save CC agent ID for reservationIds=%s",
                updateReservationCcAgentIdRequestDto.getReservationIds())))
        .block();
  }

  public void sendUpdateReservationAmend(UpdateReservationsRequestDto updateReservationRequest) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getReservationEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(updateReservationRequest), UpdateReservationsRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to update a list of reservations=%s",
                String.join(",", updateReservationRequest.getUpdateReservationsRequest()
                    .stream().map(UpdateReservationRequestDto::getReservationId).toList()))))
        .block();
  }

  public DepositsResponseDto getDepositsByReservationId(
      String hotelId, String reservationId) {
    return ohipAdapterWebClient.get().uri(
            uriBuilder -> uriBuilder.path(ohipAdapterProperties.getDepositsEndpoint())
                .queryParam(HOTEL_ID, hotelId)
                .queryParam(RESERVATION_ID, reservationId)
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(DepositsResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public CancellationPoliciesResponseDto getCancellationPolicies(
      Set<String> reservationIds, String hotelId, String rateCode, String arrivalDate) {
    return ohipAdapterWebClient.get().uri(
            uriBuilder -> uriBuilder.path(ohipAdapterProperties.getCancelPoliciesEndpoint())
                .queryParam(HOTEL_ID, hotelId)
                .queryParam(RESERVATION_IDS, reservationIds)
                .queryParam(RATE_CODE, rateCode)
                .queryParam(ARRIVAL_DATE_PARAM, arrivalDate)
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(CancellationPoliciesResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public MarketingPreferencesResponseDto getMarketingPreferences(String hotelId,
                                                                 String reservationId) {
    return ohipAdapterWebClient.get().uri(
            uriBuilder -> uriBuilder.path(ohipAdapterProperties.getMarketingPreferencesEndpoint())
                .queryParam(HOTEL_ID, hotelId)
                .queryParam(RESERVATION_ID, reservationId)
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(MarketingPreferencesResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public CopyReservationsResponseDto sendCopyReservationsRequest(
      CopyReservationsRequestDto copyReservationsRequestDto) {
    return ohipAdapterWebClient
        .post()
        .uri(ohipAdapterProperties.getCopyReservationsEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(copyReservationsRequestDto), CopyReservationsRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(CopyReservationsResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to copy reservations for reservationIds=%s",
                copyReservationsRequestDto.getReservationIds())))
        .block();
  }

  public AmendSummaryAmountResponse getAmendSummaryDetails(
      AmendSummaryAmountRequest amendSummaryAmountRequest) {
    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder ->
            uriBuilder.path(ohipAdapterProperties.getAmendSummaryDetails())
                .queryParam(HOTEL_ID, amendSummaryAmountRequest.getHotelId())
                .queryParam(RESERVATION_IDS, String.join(",",
                    amendSummaryAmountRequest.getReservationIds().stream().map(Object::toString)
                        .toList()))
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(AmendSummaryAmountResponse.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public void deleteReservationRequest(String hotelId, String reservationId) {
    ohipAdapterWebClient.delete().uri(uriBuilder ->
            uriBuilder.path(ohipAdapterProperties.getReservationEndpoint())
                .queryParam(HOTEL_ID, hotelId)
                .queryParam(RESERVATION_ID, reservationId).build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to delete reservation=%s", reservationId)))
        .block();
  }

  public BookingAllowancesResponse getBookingAllowances(String hotelId, String reservationId,
      List<String> basketBookingAllowances) {
    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder ->
            uriBuilder.path(ohipAdapterProperties.getBookingAllowancesEndpoint())
                .queryParam(HOTEL_ID, hotelId)
                .queryParam(RESERVATION_ID, reservationId)
                .queryParamIfPresent(BOOKING_ALLOWANCE_IDS,
                    Optional.ofNullable(basketBookingAllowances).filter(list -> !list.isEmpty()))
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(BookingAllowancesResponse.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public ReservationByBasketRefResponseDto sendConfirmAmend(
      ConfirmAmendOnReservationsRequestDto confirmAmendRequestDto) {
    return ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getConfirmAmend())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(confirmAmendRequestDto), ConfirmAmendOnReservationsRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(ReservationByBasketRefResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to confirm amend for reservations = %s",
                String.join(",", confirmAmendRequestDto.getOriginalReservations()))))
        .block();
  }

  public ReservationByBasketRefResponseDto sendConfirmAmendForSingleCall(
            AmendDistributionSingleCallRequestDto amendDistributionSingleCallRequestDto) {
    return ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getConfirmAmendForSingleCall())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(amendDistributionSingleCallRequestDto), AmendDistributionSingleCallRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(ReservationByBasketRefResponseDto.class)
        .doOnError(
            ex -> ExceptionLogger.log(log, ex, "Error while trying to confirm amend for reservations"))
        .block();
  }

  public void updateReservations(UpdateReservationsRequestDto updateReservationsRequest) {
    ohipAdapterWebClient
        .put()
        .uri(uriBuilder -> uriBuilder.path(ohipAdapterProperties.getUpdateReservations())
            .build())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(updateReservationsRequest), UpdateReservationsRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationNotFoundException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            "Error while trying to triggerHotelsOpeningSoonCacheUpdate"))
        .block();
  }

  public RatePlansResponseDto sendGetRatePlansRequest(List<String> ratePlanCodes, String hotelId) {
    return ohipAdapterWebClient.get()
        .uri(
            uriBuilder -> uriBuilder.path(
                    ohipAdapterProperties.getRatePlansEndpoint())
                .queryParam(RATE_PLAN_CODES, ratePlanCodes)
                .queryParam(HOTEL_ID, hotelId)
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(RatePlansResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

  public DonationPackagesResponseDto sendCharityPackagesDetailsRequest(String hotelId,
                                                                       List<String> packageCodes) {
    return ohipAdapterWebClient.get()
        .uri(
            uriBuilder -> uriBuilder.path(
                    ohipAdapterProperties.getCharityPackagesDetailsEndpoint())
                .queryParam(PACKAGE_CODES, packageCodes)
                .build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(DonationPackagesResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to get charity package details for hotelId=%s and reservationIds=%s",
            hotelId, packageCodes)))
        .block();
  }

  public void movePaymentDetails(String hotelId, Set<String> reservationIds) {
    ohipAdapterWebClient.put()
        .uri(uriBuilder -> uriBuilder
            .path(ohipAdapterProperties.getMoveReservationPaymentEndpoint())
            .queryParam(HOTEL_ID, hotelId)
            .queryParam(RESERVATION_IDS, reservationIds)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "An error was returned by Ohip Adapter while moving payment details for hotelId=%s and reservationIds=%s",
            hotelId, reservationIds)))
        .block();
  }

  public DepositFoliosResponseDto getGeneratedDepositFolios(String hotelId,
                                                            Set<String> reservationIds) {
    return ohipAdapterWebClient.get()
        .uri(
            uriBuilder -> uriBuilder.path(
                    ohipAdapterProperties.getGeneratedDepositFoliosEndpoint())
                .queryParam(HOTEL_ID, hotelId)
                .queryParam(RESERVATION_IDS, reservationIds)
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(DepositFoliosResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to get generated Deposit Folios for hotelId=%s and reservationIds=%s",
            hotelId, reservationIds)))
        .block();
  }

  public void sendUpdateBookerDetailsRequest(
      BookerDetailsCnpRequestDto bookerDetailsCnpRequestDto) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getReservationBookerEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(bookerDetailsCnpRequestDto), BookerDetailsCnpRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to update booker details hotelId=%s and reservationIds=%s",
            bookerDetailsCnpRequestDto.getHotelId(),
            bookerDetailsCnpRequestDto.getReservationIds())))
        .block();
  }

  public void sendUpdateBookerEmailRequest(
      UpdateBookerEmailRequestDto updateBookerEmailRequestDto) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getUpdateEmailReservationEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(updateBookerEmailRequestDto), UpdateBookerEmailRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to update booker details hotelId=%s and reservationIds=%s",
            updateBookerEmailRequestDto.getHotelId(),
            updateBookerEmailRequestDto.getReservationIds())))
        .block();
  }

  public void deleteRoutingInstructions(String hotelId, Set<String> reservationIds) {
    ohipAdapterWebClient.delete()
        .uri(uriBuilder -> uriBuilder
            .path(ohipAdapterProperties.getRoutingInstructionsEndpoint())
            .queryParam(OhipAdapterConstants.HOTEL_ID, hotelId)
            .queryParam(OhipAdapterConstants.RESERVATION_IDS, reservationIds)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to delete routing instructions hotelId=%s and reservationIds=%s",
            hotelId, reservationIds)))
        .block();
  }

  public void saveCharges(DepositFoliosRequestDto dto) {
    ohipAdapterWebClient.post()
        .uri(ohipAdapterProperties.getGeneratedDepositFoliosEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(dto), DepositFoliosRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying create DepositFolios: %s",
                dto.getDepositFolios())))
        .block();
  }

  public MemosResponseDto createMemo(CreateMemoRequestDto createMemoRequestDto) {
    return ohipAdapterWebClient
        .post()
        .uri(ohipAdapterProperties.getMemosEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(createMemoRequestDto), CreateMemoRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(MemosResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while creating memo for request %s", createMemoRequestDto)))
        .block();
  }

  public MemosResponseDto getMemos(String hotelId, Set<String> reservationIds) {
    return ohipAdapterWebClient.get()
        .uri(
            uriBuilder -> uriBuilder.path(
                    ohipAdapterProperties.getMemosEndpoint())
                .queryParam(HOTEL_ID, hotelId)
                .queryParam(RESERVATION_IDS, reservationIds)
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(MemosResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to get memos for hotelId=%s and reservationIds=%s",
            hotelId, reservationIds)))
        .block();
  }

  public void attachProfileToReservations(
      AttachReservationProfileRequestDto attachReservationProfileRequestDto) {

    ohipAdapterWebClient.post()
        .uri(ohipAdapterProperties.getAttachProfileToReservationsEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(attachReservationProfileRequestDto),
            AttachReservationProfileRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            "An error was returned while attaching profile to reservation!"))
        .block();
  }

  public PackagesResponseDto getPackages(PackagesRequestDto packagesRequestDto) {
    return ohipAdapterWebClient.get().uri(uriBuilder -> uriBuilder
            .path(ohipAdapterProperties.getPackagesEndpoint())
            .queryParam("startDate",
                packagesRequestDto.getStartDate())
            .queryParam("endDate",
                packagesRequestDto.getEndDate())
            .queryParam("adults",
                packagesRequestDto.getAdultsNumber())
            .queryParamIfPresent("children",
                Optional.of(packagesRequestDto.getChildrenNumber()))
            .queryParam("nrNights",
                Optional.of(packagesRequestDto.getNightsNumber()))
            .build(packagesRequestDto.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(PackagesException.class);
        })
        .bodyToMono(PackagesResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to create package response for packagesRequestDto=%s",
                packagesRequestDto)))
        .block();
  }

  public ChangeLogResponseDto getChangeLog(String hotelId, String reservationId, Integer limit,
                                           Integer offset) {

    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(ohipAdapterProperties.getChangeLogEndpoint())
            .queryParam("reservationId", reservationId)
            .queryParam(LIMIT, limit)
            .queryParam(OFFSET, offset)
            .build(hotelId))
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatusCode.valueOf(204)), response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(ChangeLogException.class);
        })
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(ChangeLogException.class);
        })
        .bodyToMono(ChangeLogResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to create changelog for hotelId=%s and reservationId %s", hotelId,
            reservationId)))
        .block();
  }

  public ConfirmReservationResponseDto sendUpdateReservation(
      UpdateReservationSingleCallResponseDto
          updateReservationSingleCallResponseDto) {
    return ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getUpdateReservationSingleCall())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(updateReservationSingleCallResponseDto),
            UpdateReservationSingleCallResponseDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(ConfirmReservationResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to confirm reservation for reservationId=%s",
            updateReservationSingleCallResponseDto.getPaymentDetails().getReservationId())))
        .block();
  }

  public ReservationProfilesDto sendCreateProfiles(
      ReservationGuestRequestDto reservationGuestRequestDto) {
    return ohipAdapterWebClient
        .post()
        .uri(ohipAdapterProperties.getCreateProfilesEndPoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(reservationGuestRequestDto), ReservationGuestRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(ReservationProfilesDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to Create Profile=%s",
                reservationGuestRequestDto)))
        .block();
  }

  public PreCheckInResponse sendSaveReservationPreCheckInStatus(
      PreCheckInRequestDto preCheckInRequestDto) {
    log.debug("Entered sendSaveReservationPreCheckInStatus with Pre-CheckIn request {}",
        preCheckInRequestDto);
    return ohipAdapterWebClient.post()
        .uri(ohipAdapterProperties.getPreCheckInStatus())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(preCheckInRequestDto),
            PreCheckInRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(PreCheckInResponse.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to save pre-checkIn status for hotelId=%s and reservationId %s",
            preCheckInRequestDto.getHotelId(),
            preCheckInRequestDto.getReservationId())))
        .block();
  }

  public void sendDeleteRegCardAttachment(
      String hotelId, String reservationId) {
    log.debug("Entered sendDeleteRegCardAttachment with hotelId={} and reservationId={}", hotelId,
        reservationId);
    ohipAdapterWebClient.delete().uri(uriBuilder ->
            uriBuilder.path(ohipAdapterProperties.getFileAttachmentEndpoint())
                .queryParam(HOTEL_ID, hotelId)
                .queryParam(RESERVATION_ID, reservationId).build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to delete attachment for "
                + "hotelId=%s and reservation=%s", hotelId, reservationId)))
        .block();
  }

  public void sendDeleteReservationPreCheckInStatus(
      String hotelId, String reservationId) {
    log.debug("Entered sendDeleteReservationPreCheckInStatus "
        + "with hotelId={} and reservationId={}", hotelId, reservationId);
    ohipAdapterWebClient.delete().uri(uriBuilder ->
            uriBuilder.path(ohipAdapterProperties.getPreCheckInStatus())
                .queryParam(HOTEL_ID, hotelId)
                .queryParam(RESERVATION_ID, reservationId).build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format(
                "Error while trying to delete pre-checkIn for hotelId=%s "
                    + "and reservation=%s", hotelId, reservationId)))
        .block();
  }

  public void updateReservationExternalReference(String hotelId, List<String> reservationIds,
      String externalReference) {
    ohipAdapterWebClient
        .put()
        .uri(uriBuilder -> uriBuilder.path(ohipAdapterProperties.getUpdateExternalReferenceEndpoint())
            .queryParam(HOTEL_ID, hotelId)
            .queryParam(RESERVATION_IDS, reservationIds)
            .queryParam(EXTERNAL_REFERENCE, externalReference)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationNotFoundException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying save external reference for reservationIds=%s", externalReference)))
        .block();
  }

  public void sendUpdateReservationPackagesScheduledRequest(
      ReservationScheduledPackagesRequestDto savePackagesRequestDto) {

    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getReservationsPackagesScheduledEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(savePackagesRequestDto), ReservationScheduledPackagesRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to add/update reservation scheduled packages for reservationIds=%s",
            savePackagesRequestDto.getReservationsId())))
        .block();
  }

  public void sendLinkReservationToLeisureCustomer(
      LinkReservationToLeisureCustomerRequestDto linkReservationToLeisureCustomerRequestDto) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getLinkReservationToLeisureCustomerEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(linkReservationToLeisureCustomerRequestDto),
            LinkReservationToLeisureCustomerRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to link reservationIds=%s to leisure customer",
                linkReservationToLeisureCustomerRequestDto.getReservationIds())))
        .block();
  }

  public void updateReservationPreferences(ReservationPreferencesRequestDto reservationPreferencesRequestDto) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getReservationsPreferencesEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(reservationPreferencesRequestDto), ReservationPreferencesRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to update reservation preferences for reservationIds=%s",
            reservationPreferencesRequestDto.getReservationsIds())))
        .block();
  }

  public void updateReservationAlerts(UpdateReservationAlertsRequest updateReservationAlertsRequest) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getReservationsAlertsEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(updateReservationAlertsRequest), UpdateReservationAlertsRequest.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to update reservation preferences for reservationIds=%s",
            updateReservationAlertsRequest.getReservationIds())))
        .block();
  }

  public void updateCancellationPolicies(
      UpdateCancellationPoliciesRequestDto updateCancellationPoliciesRequest) {

    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getUpdateCancellationPoliciesEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(updateCancellationPoliciesRequest), UpdateCancellationPoliciesRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to update cancellation policies for reservationIds=%s",
            updateCancellationPoliciesRequest.getReservationIds())))
        .block();
  }

  public void updateUdfc20(UpdateReservationUdfsRequest updateReservationUdfsRequest) {
    ohipAdapterWebClient
        .put()
        .uri(ohipAdapterProperties.getUpdateUdfc20Endpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(updateReservationUdfsRequest), UpdateReservationUdfsRequest.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }

}
