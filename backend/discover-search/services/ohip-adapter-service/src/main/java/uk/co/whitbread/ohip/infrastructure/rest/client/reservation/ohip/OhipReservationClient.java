package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip;

import static java.util.Collections.singletonList;
import static uk.co.whitbread.ohip.domain.utils.SanitizingUtils.sanitize;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ARRIVAL_DATE_END_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ARRIVAL_DATE_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ARRIVAL_DATE_START_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.BOOKER_COMMUNICATION_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.BOOKER_POSTCODE_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.BOOKER_PROFILE_NAME_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.BOOKER_PROFILE_TYPE_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.CANCELLATION_DATE_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.EXTERNAL_REFERENCE_IDS_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.FETCH_INSTRUCTIONS_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.GUEST_LAST_NAME_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HOTEL_IDS_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HOTEL_ID_HEADER;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HOTEL_ID_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HUB_ID_HEADER;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.LIMIT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.OFFSET;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ORDER_BY_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.PARAMETER_NAME;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.PARAMETER_VALUE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.POLICY_TYPE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RATE_PLAN_CODES;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RESERVATION_IDS_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RESV_NAME_ID;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.SORT_ORDER_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.getOnStatusException;
import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.getRetrySpec;
import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.logErrorResponse;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ActivityLog;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservationDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservationDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CreateReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFolioCriteria;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PostedDepositFolio;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreCheckInReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RateInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestTypeProfileInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RetrievedDepositFolio;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileSummaries;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Status;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.enterprise.HotelDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.hotel.config.CancellationPolicyDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.hotel.config.PolicySchedulesDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.med.FileToUpload;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rsvV0.ReservationCancellationPolicyCriteria;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.PriceBreakdownDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.properties.AvailabilityOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.changelog.exception.ChangeLogException;
import uk.co.whitbread.ohip.infrastructure.rest.client.changelog.properties.ChangeLogOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.FoliosResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.ReservationStatusOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;

@Component
@Slf4j
@RequiredArgsConstructor
public class OhipReservationClient {

  public static final int PARALLELISM = 25;
  public static final String PARAMS = "hotelId=%s and reservationId=%s";
  public static final int OPERA_TIMEOUT = 15;
  public static final String RESERVATION_ID = "{ReservationId}";
  public static final String PROFILE_ID = "{profileId}";
  public static final String RESERVATION_FETCH_INSTRUCTION = "Reservation";
  private static final String RESERVATION_PAYMENT_METHODS = "ReservationPaymentMethods";
  private final WebClient ohipWebClient;
  private final ReservationOhipProperties reservationOhipProperties;
  private final AvailabilityOhipProperties availabilityOhipProperties;
  private final ChangeLogOhipProperties changeLogOhipProperties;

  public Mono<ReservationStatusOhipDto> sendCreateReservationRequest(String hotelId,
      CreateReservation createReservation) {
    return ohipWebClient
        .post()
        .uri(uriBuilder ->
            uriBuilder.path(reservationOhipProperties.getReservationEndpoint()).build(hotelId))
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(createReservation), CreateReservation.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_CREATE_RESERVATION_EXCEPTION,
              String.format("Error while trying to create reservation for hotelId=%s",
                  hotelId)));
        })
        .bodyToMono(ReservationStatusOhipDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  public ReservationsDetails sendGetReservationsByExternalReferenceIdsRequest(String hotelId,
      List<String> externalReferenceIds,
      int limit, int offset) {
    return ohipWebClient
        .get()
        .uri(uriBuilder ->
            uriBuilder.path(reservationOhipProperties.getReservationEndpoint())
                .queryParam(FETCH_INSTRUCTIONS_PARAM, RESERVATION_FETCH_INSTRUCTION)
                .queryParam(EXTERNAL_REFERENCE_IDS_PARAM, externalReferenceIds)
                .queryParam(LIMIT, limit)
                .queryParam(OFFSET, offset)
                .build(hotelId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_GET_RESERVATIONS_BY_EXTERNALREFIDS_EXCEPTION,
              String.format(
                  "Error while trying to get reservations by external reference ids for hotelId=%s and %s ids",
                  hotelId,
                  (externalReferenceIds == null ? "null" : externalReferenceIds.size()))));
        })
        .bodyToMono(ReservationsDetails.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public Mono<Reservation> getReservationPaymentMethods(String hotelId, String reservationId) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(FETCH_INSTRUCTIONS_PARAM,
        Arrays.asList(RESERVATION_FETCH_INSTRUCTION, RESERVATION_PAYMENT_METHODS));
    return ohipWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(
                StringUtils.joinWith("/", reservationOhipProperties.getReservationEndpoint(),
                    RESERVATION_ID))
            .queryParams(params)
            .build(hotelId, reservationId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_GET_RESERVATION_EXCEPTION,
              String.format(
                  "Error while trying to get reservation payment method by ids for hotelId=%s and %s ids",
                  hotelId, reservationId)));
        })
        .bodyToMono(Reservation.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  public Mono<Reservation> getReservation(String hotelId, String reservationId) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(FETCH_INSTRUCTIONS_PARAM,
        Arrays.asList(RESERVATION_FETCH_INSTRUCTION, "InventoryItems", "ReservationPolicies", "Packages",
            RESERVATION_PAYMENT_METHODS, "RoutingInstructions", "Comments", "Preferences",
            "LinkedReservations", "Alerts"));
    return ohipWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(
                StringUtils.joinWith("/", reservationOhipProperties.getReservationEndpoint(),
                    RESERVATION_ID))
            .queryParams(params)
            .build(hotelId, reservationId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_GET_RESERVATION_EXCEPTION,
              String.format(
                  "Error while trying to get reservations by ids for hotelId=%s and %s ids",
                  hotelId, reservationId)));
        })
        .bodyToMono(Reservation.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  public Flux<Reservation> getReservations(String hotelId, Set<String> reservationIds) {
    return Flux.fromIterable(reservationIds)
        .flatMap(reservationId -> getReservation(hotelId, reservationId));
  }

  public Mono<ChangeReservationDetails> sendChangeReservationRequest(String hotelId,
      String reservationId,
      ChangeReservation changeReservation) {
    return ohipWebClient
        .put()
        .uri(uriBuilder ->
            uriBuilder.path(
                StringUtils.joinWith("/", reservationOhipProperties.getReservationEndpoint(),
                    RESERVATION_ID)).build(hotelId, reservationId))
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(changeReservation), ChangeReservation.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            getOnStatusException(log, response, new HotelReservationException(
                ErrorCode.OHIP_CHANGE_RESERVATION_EXCEPTION,
                String.format("Error while trying to change reservation for " + PARAMS,
                    hotelId, reservationId)), ErrorCode.OHIP_CHANGE_RESERVATION_EXCEPTION)
        )
        .bodyToMono(ChangeReservationDetails.class)
        .retryWhen(getRetrySpec(new HotelReservationException(
            ErrorCode.OHIP_RETRIES_EXHAUSTED_EXCEPTION,
            String.format("Error while trying to change reservation for " + PARAMS,
                hotelId, reservationId + ". Max retries exhausted."))))
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  public List<Profile> sendGetProfilesByProfileIds(Set<String> profileIds) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(FETCH_INSTRUCTIONS_PARAM,
        Arrays.asList("Profile", "Address", "Communication", "Correspondence", "FutureReservation",
            "HistoryReservation"));

    return Flux.fromIterable(profileIds)
        .flatMap(profileId -> ohipWebClient.get()
            .uri(uriBuilder -> uriBuilder.path(
                    StringUtils.joinWith("/", reservationOhipProperties.getProfilesEndpoint(),
                        PROFILE_ID))
                .queryParams(params)
                .build(profileId))
            .headers(
                httpHeaders -> httpHeaders.add(HUB_ID_HEADER, reservationOhipProperties.getHubId()))
            .retrieve()
            .onStatus(HttpStatusCode::isError, response -> {
              logErrorResponse(log, response);
              return Mono.error(new HotelReservationException(
                  ErrorCode.OHIP_GET_PROFILES_EXCEPTION,
                  String.format("Error while trying to get profiles for profileId=%s",
                      profileId)));
            })
            .bodyToMono(Profile.class))
        .collectList()
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public List<ResGuestTypeProfileInfo> sendGetGuestProfilesByProfileIds(Set<String> profileIds) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(FETCH_INSTRUCTIONS_PARAM,
        Arrays.asList("Profile", "Address", "Communication", "Correspondence", "FutureReservation",
            "HistoryReservation"));

    return Flux.fromIterable(profileIds)
        .flatMap(profileId -> ohipWebClient.get()
            .uri(uriBuilder -> uriBuilder.path(
                    StringUtils.joinWith("/", reservationOhipProperties.getProfilesEndpoint(),
                        PROFILE_ID))
                .queryParams(params)
                .build(profileId))
            .headers(
                httpHeaders -> httpHeaders.add(HUB_ID_HEADER, reservationOhipProperties.getHubId()))
            .retrieve()
            .onStatus(HttpStatusCode::isError, response -> {
              logErrorResponse(log, response);
              return Mono.error(new HotelReservationException(
                  ErrorCode.OHIP_GET_GUEST_PROFILE_EXCEPTION,
                  String.format("Error while trying to get profile for profileId=%s",
                      profileId)));
            })
            .bodyToMono(ResGuestTypeProfileInfo.class))
    .collectList()
    .doOnError(exception -> ExceptionLogger.log(log, exception))
    .block();
  }

  public Status sendPostProfileRequest(String hotelId, Profile profile) {
    return ohipWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(reservationOhipProperties.getProfilesEndpoint()).build())
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(profile), Profile.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(ErrorCode.OHIP_POST_PROFILE_EXCEPTION,
              String.format("Unable to post profile with Type: %s",
                  profile.getProfileDetails().getProfileType())));
        })
        .bodyToMono(Status.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public Status sendUpdateProfileRequest(String hotelId, Profile profile) {
    return ohipWebClient
        .put()
        .uri(uriBuilder -> uriBuilder.path(
            StringUtils.joinWith("/", reservationOhipProperties.getProfilesEndpoint(),
                PROFILE_ID)).build(profile.getProfileIdList().get(0).getId()))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(profile), Profile.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            getOnStatusException(log, response, new HotelReservationException(
                ErrorCode.OHIP_SEND_UPDATE_PROFILE_EXCEPTION,
                String.format(
                    "Error while trying to update profile with Type: %s",
                    profile.getProfileDetails().getProfileType()
                )), ErrorCode.OHIP_SEND_UPDATE_PROFILE_EXCEPTION)
        )
        .bodyToMono(Status.class)
        .retryWhen(getRetrySpec(new HotelReservationException(
            ErrorCode.OHIP_RETRIES_EXHAUSTED_EXCEPTION,
            String.format("Error while trying to update profile with Type: %s. Max retries exhausted.",
                profile.getProfileDetails().getProfileType()))))
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public Mono<ChangeReservationDetails> sendPutReservationsGuestRequest(String hotelId,
      String internalReservationId, ChangeReservation
      changeReservation) {
    return ohipWebClient
        .put()
        .uri(uriBuilder -> uriBuilder.path(
            StringUtils.joinWith("/", reservationOhipProperties.getReservationEndpoint(),
                "{internalReservationId}")).build(hotelId, internalReservationId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(changeReservation), ChangeReservation.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            getOnStatusException(log, response, new HotelReservationException(
                ErrorCode.OHIP_PUT_RESERVATIONS_GUEST_EXCEPTION,
                String.format("Error while trying to put reservations guest for hotelId=%s "
                    + "and internalReservationId=%s", hotelId, internalReservationId)),
                ErrorCode.OHIP_PUT_RESERVATIONS_GUEST_EXCEPTION))
        .bodyToMono(ChangeReservationDetails.class)
        .retryWhen(getRetrySpec(new HotelReservationException(
            ErrorCode.OHIP_RETRIES_EXHAUSTED_EXCEPTION,
            String.format("Error while trying to put reservations guest for hotelId=%s "
                + "and internalReservationId=%s. Max retries exhausted.", hotelId, internalReservationId))))
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  public PostedDepositFolio sendDepositFoliosRequest(String hotelId,
      String reservationId, DepositFolioCriteria depositFolioCriteria) {
    return ohipWebClient
        .post()
        .uri(uriBuilder ->
            uriBuilder.path(reservationOhipProperties.getDepositFoliosEndpoint())
                .build(hotelId, reservationId))
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(depositFolioCriteria), DepositFolioCriteria.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          log.error("Error while trying to send deposit folios for hotelId={} and reservationId={}",
              hotelId, reservationId);
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_SEND_DEPOSIT_FOLIOS_EXCEPTION,
              String.format(
                  "Error while trying to send deposit folios for hotelId=%s "
                      + "and reservationId=%s", hotelId, reservationId
              )));
        })
        .bodyToMono(PostedDepositFolio.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public Mono<CancelReservationDetails> sendPostCancelReservationRequest(String hotelId,
      String reservationId,
      CancelReservation cancelReservation) {
    return ohipWebClient
        .post()
        .uri(uriBuilder ->
            uriBuilder.path(reservationOhipProperties.getCancellationEndpoint())
                .build(hotelId, reservationId))
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(cancelReservation), CancelReservation.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_CANCEL_RESERVATION_EXCEPTION,
              String.format("Error while trying to post cancel reservation for "
                  + PARAMS, hotelId, reservationId
              )));
        })
        .bodyToMono(CancelReservationDetails.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "OperaHotelConfigCache", key = "#hotelId")
  public HotelDetails getHotelConfig(String hotelId) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(FETCH_INSTRUCTIONS_PARAM, singletonList("General"));

    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(reservationOhipProperties.getHotelConfig())
                .queryParams(params)
                .build(hotelId))
        .headers(httpHeaders ->
            httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_RETRIEVE_HOTEL_CONFIG_EXCEPTION,
              String.format("Error while trying to get hotel config for hotelId=%s", hotelId)));
        })
        .bodyToMono(HotelDetails.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public ReservationsDetails sendGetReservationsByExternalReferenceId(String externalReferenceId) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(FETCH_INSTRUCTIONS_PARAM, singletonList(RESERVATION_FETCH_INSTRUCTION));
    return ohipWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(
                reservationOhipProperties.getExternalReservationEndpoint())
            .queryParam("externalReferenceIds", externalReferenceId)
            .queryParams(params)
            .build())
        .headers(
            httpHeaders -> httpHeaders.add(HUB_ID_HEADER, reservationOhipProperties.getHubId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_GET_RESERVATIONS_BY_EXTERNALREFID_EXCEPTION,
              String.format(
                  "Error while trying to get reservations by external referenceId=%s ",
                  externalReferenceId)));
        })
        .bodyToMono(ReservationsDetails.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public Reservation sendGetReservationsByReservationId(String hotelId, String reservationId) {
    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(FETCH_INSTRUCTIONS_PARAM, RESERVATION_FETCH_INSTRUCTION);
    queryParams.add(FETCH_INSTRUCTIONS_PARAM, RESERVATION_PAYMENT_METHODS);
    queryParams.add(FETCH_INSTRUCTIONS_PARAM, "ReservationPolicies");
    queryParams.add(FETCH_INSTRUCTIONS_PARAM, "Attachments");
    queryParams.add(FETCH_INSTRUCTIONS_PARAM, "Alerts");
    return ohipWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(reservationOhipProperties.getReservationIdEndpoint())
            .queryParams(queryParams)
            .build(hotelId, reservationId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(
              new HotelReservationException(ErrorCode.OHIP_GET_RESERVATION_BY_RESID_EXCEPTION,
                  String.format("Error while trying to get reservations by reservationId=%s",
                      reservationId)));
        })
        .bodyToMono(Reservation.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public ReservationsDetails sendGetReservationsByReservationRelatedFields(
      Map<String, List<String>> searchInputParams) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(FETCH_INSTRUCTIONS_PARAM, singletonList(RESERVATION_FETCH_INSTRUCTION));
    params.put(ORDER_BY_PARAM, singletonList("ArrivalDate"));
    params.put(SORT_ORDER_PARAM, singletonList("ASC"));

    if (searchInputParams.get(RESERVATION_IDS_PARAM) != null) {
      searchInputParams.get(RESERVATION_IDS_PARAM)
          .forEach(reservationId -> params.add(RESERVATION_IDS_PARAM, reservationId));
    }

    if (searchInputParams.get(EXTERNAL_REFERENCE_IDS_PARAM) != null) {
      params.put(EXTERNAL_REFERENCE_IDS_PARAM, searchInputParams.get(EXTERNAL_REFERENCE_IDS_PARAM));
    }

    if (searchInputParams.get(ARRIVAL_DATE_PARAM) != null) {
      params.put(ARRIVAL_DATE_START_PARAM, searchInputParams.get(ARRIVAL_DATE_PARAM));
      params.put(ARRIVAL_DATE_END_PARAM, searchInputParams.get(ARRIVAL_DATE_PARAM));
    }

    if (searchInputParams.get(CANCELLATION_DATE_PARAM) != null) {
      params.put(CANCELLATION_DATE_PARAM, searchInputParams.get(CANCELLATION_DATE_PARAM));
    }

    if (searchInputParams.get(GUEST_LAST_NAME_PARAM) != null) {
      params.put(GUEST_LAST_NAME_PARAM, searchInputParams.get(GUEST_LAST_NAME_PARAM));
    }

    if (searchInputParams.get(LIMIT) != null) {
      params.put(LIMIT, searchInputParams.get(LIMIT));
    }

    if (searchInputParams.get(OFFSET) != null) {
      params.put(OFFSET, searchInputParams.get(OFFSET));
    }

    return ohipWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(
                reservationOhipProperties.getExternalReservationEndpoint())
            .queryParams(params)
            .build())
        .headers(
            httpHeaders -> httpHeaders.add(HUB_ID_HEADER, reservationOhipProperties.getHubId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_GET_RESERVATION_RELATED_FIELDS_EXCEPTION,
              String.format(
                  "Error while trying to get reservations by external reference id %s, "
                      + "arrival date %s, cancellation date %s, guest surname %s",
                  searchInputParams.get(EXTERNAL_REFERENCE_IDS_PARAM),
                  searchInputParams.get(ARRIVAL_DATE_START_PARAM),
                  searchInputParams.get(CANCELLATION_DATE_PARAM),
                  searchInputParams.get(GUEST_LAST_NAME_PARAM))));
        })
        .bodyToMono(ReservationsDetails.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public ProfileSummaries sendBookerGetProfileSummaries(
      Map<String, List<String>> searchInputParams) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    if (searchInputParams.containsKey(BOOKER_PROFILE_TYPE_PARAM)) {
      params.put(BOOKER_PROFILE_TYPE_PARAM, searchInputParams.get(BOOKER_PROFILE_TYPE_PARAM));
    }
    if (searchInputParams.containsKey(BOOKER_COMMUNICATION_PARAM)) {
      params.put(BOOKER_COMMUNICATION_PARAM, searchInputParams.get(BOOKER_COMMUNICATION_PARAM));
    }
    if (searchInputParams.containsKey(BOOKER_PROFILE_NAME_PARAM)) {
      params.put(BOOKER_PROFILE_NAME_PARAM, searchInputParams.get(BOOKER_PROFILE_NAME_PARAM));
    }
    if (searchInputParams.containsKey(BOOKER_POSTCODE_PARAM) && searchInputParams.containsKey(
        BOOKER_PROFILE_NAME_PARAM)) {
      params.put(BOOKER_POSTCODE_PARAM, searchInputParams.get(BOOKER_POSTCODE_PARAM));
    }

    if (searchInputParams.get(LIMIT) != null) {
      params.put(LIMIT, searchInputParams.get(LIMIT));
    }
    if (searchInputParams.get(OFFSET) != null) {
      params.put(OFFSET, searchInputParams.get(OFFSET));
    }

    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(reservationOhipProperties.getProfilesEndpoint())
                .queryParams(params)
                .build())
        .headers(httpHeaders ->
            httpHeaders.add(HOTEL_ID_HEADER, reservationOhipProperties.getDefaultHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_GET_BOOKER_PROFILE_EXCEPTION,
              "Error while trying to get booker profile."));
        })
        .bodyToMono(ProfileSummaries.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public ReservationsDetails sendGetReservationsByHotelRelatedFields(
      Map<String, List<String>> searchInputParams) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(FETCH_INSTRUCTIONS_PARAM, singletonList(RESERVATION_FETCH_INSTRUCTION));
    params.put(ORDER_BY_PARAM, singletonList("ArrivalDate"));
    params.put(SORT_ORDER_PARAM, singletonList("ASC"));

    if (searchInputParams.get(EXTERNAL_REFERENCE_IDS_PARAM) != null) {
      params.put(EXTERNAL_REFERENCE_IDS_PARAM, searchInputParams.get(EXTERNAL_REFERENCE_IDS_PARAM));
    }
    if (searchInputParams.get(ARRIVAL_DATE_PARAM) != null) {
      params.put(ARRIVAL_DATE_START_PARAM, searchInputParams.get(ARRIVAL_DATE_PARAM));
      params.put(ARRIVAL_DATE_END_PARAM, searchInputParams.get(ARRIVAL_DATE_PARAM));
    }
    if (searchInputParams.get(CANCELLATION_DATE_PARAM) != null) {
      params.put(CANCELLATION_DATE_PARAM, searchInputParams.get(CANCELLATION_DATE_PARAM));
    }

    if (searchInputParams.get(LIMIT) != null) {
      params.put(LIMIT, searchInputParams.get(LIMIT));
    }
    if (searchInputParams.get(OFFSET) != null) {
      params.put(OFFSET, searchInputParams.get(OFFSET));
    }

    var hotelId = searchInputParams.get(HOTEL_ID_PARAM).get(0);

    return ohipWebClient
        .get()
        .uri(uriBuilder ->
            uriBuilder.path(reservationOhipProperties.getReservationEndpoint())
                .queryParams(params)
                .build(hotelId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_GET_RESERVATIONS_EXCEPTION,
              String.format("Error while trying to get reservations by external hotelId=%s",
                  hotelId)));
        })
        .bodyToMono(ReservationsDetails.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public Mono<Tuple2<String, PriceBreakdownDto>> getReservationAmounts(String hotelId,
      String reservationId) {
    return Mono.zip(Mono.just(reservationId), ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(reservationOhipProperties.getRateInfoEndpoint())
                .queryParam("idContext", OhipConstants.CONTEXT)
                .queryParam("id", reservationId)
                .queryParam("summaryInfo", OhipConstants.BOOLEAN_TRUE)
                .queryParam("type", OhipConstants.TYPE)
                .build(hotelId))
        .headers(httpHeaders ->
            httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_GET_RESERVATION_AMOUNTS_EXCEPTION,
              String.format(
                  "Error while trying to get reservation price information for " + PARAMS,
                  hotelId, reservationId)));
        })
        .bodyToMono(PriceBreakdownDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception)));
  }

  public FoliosResponseDto getFoliosAciAmount(String hotelId, String reservationId) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(FETCH_INSTRUCTIONS_PARAM,
        Arrays.asList("Transactioncodes", "Windowbalances", "Payment", "Payee",
            "Postings", "Totalbalance"));
    return ohipWebClient.get()
        .uri(uriBuilder ->
            uriBuilder.path(reservationOhipProperties.getFoliosEndPoint()).queryParams(params)
                .queryParam(OhipConstants.INCLUDE_FOLIO_HISTORY, OhipConstants.BOOLEAN_FALSE)
                .build(hotelId, reservationId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_GET_FOLIOS_ACI_AMOUNT_EXCEPTION,
              String.format(
                  "Error while trying to get reservation aci amount information for "
                      + PARAMS, hotelId, reservationId)));
        })
        .bodyToMono(FoliosResponseDto.class)
        .filter(response -> Objects.nonNull(response.getReservationFolioInformation())
            && Objects.nonNull(response.getReservationFolioInformation().getFolioWindowType())
            && !response.getReservationFolioInformation().getFolioWindowType().isEmpty())
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public RateInfo getRateInfo(String hotelId, String reservationId, String detailDate,
      String summaryInfo) {

    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put("summaryInfo", singletonList(summaryInfo));
    params.put("type", singletonList(RESERVATION_FETCH_INSTRUCTION));
    params.put("id", singletonList(reservationId));
    params.put("detailDate", singletonList(detailDate));

    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(availabilityOhipProperties.getRateInfoEndpoint())
                .queryParams(params).build(hotelId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(ErrorCode.OHIP_GET_RATE_INFO_EXCEPTION,
              String.format(
                  "Error while trying to get rate info for " + PARAMS,
                  hotelId, reservationId)));
        })
        .bodyToMono(RateInfo.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public RetrievedDepositFolio getDepositsByReservationId(String hotelId, String reservationId) {
    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(reservationOhipProperties.getDepositsEndpoint())
                .queryParam("id", reservationId)
                .build(hotelId))
        .headers(httpHeaders ->
            httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(ErrorCode.OHIP_GET_DEPOSIT_EXCEPTION,
              String.format(
                  "Error while trying to get deposits for " + PARAMS,
                  hotelId, reservationId)));
        })
        .bodyToMono(RetrievedDepositFolio.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "OperaCancellationPoliciesCache", key = "#hotelId")
  public CancellationPolicyDetails sendGetCancellationPoliciesRequest(String hotelId) {
    return ohipWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(reservationOhipProperties.getCancelPoliciesEndpoint())
            .queryParam(HOTEL_IDS_PARAM, singletonList(hotelId))
            .build())
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_GET_CANCELLATION_POLICY_EXCEPTION,
              String.format("Error while trying to get cancellation policies by "
                  + "hotelId=%s", hotelId)));
        })
        .bodyToMono(CancellationPolicyDetails.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "OperaPolicySchedulesCache", key = "{#hotelId, #rateCode}")
  public PolicySchedulesDetails sendGetPolicySchedulesRequest(String hotelId, String rateCode) {
    return ohipWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(reservationOhipProperties.getPolicySchedulesEndpoint())
            .queryParam(POLICY_TYPE, "Cancellation")
            .queryParam(RATE_PLAN_CODES, rateCode)
            .build(hotelId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_GET_POLICY_SCHEDULES_EXCEPTION,
              String.format("Error while trying to get policy schedules by "
                  + "hotelId=%s", hotelId)));
        })
        .bodyToMono(PolicySchedulesDetails.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }


  public uk.co.whitbread.hotel.ohip.adapter.generated.models.rsvV0.Status deleteCancellationPolicy(
      String hotelId, String reservationId, String policyId) {
    return ohipWebClient
        .delete()
        .uri(uriBuilder ->
            uriBuilder.path(reservationOhipProperties.getCancellationPoliciesEndpoint())
                .queryParam("policyId", policyId)
                .build(hotelId, reservationId))
        .headers(httpHeaders -> {
          httpHeaders.add(HOTEL_ID_HEADER, hotelId);
          httpHeaders.setContentLength(0);
        })
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_DELETE_CANCELLATION_POLICY_EXCEPTION,
              String.format("Error while trying to delete cancellation policy for hotelId=%s,"
                  + " reservationId=%s and policyId=%s", hotelId, reservationId, policyId
              )));
        })
        .bodyToMono(uk.co.whitbread.hotel.ohip.adapter.generated.models.rsvV0.Status.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public uk.co.whitbread.hotel.ohip.adapter.generated.models.rsvV0.Status createCancellationPolicy(
      String hotelId, String reservationId,
      ReservationCancellationPolicyCriteria reservationCancellationPolicyCriteria) {
    return ohipWebClient
        .post()
        .uri(uriBuilder ->
            uriBuilder.path(reservationOhipProperties.getCancellationPoliciesEndpoint())
                .build(hotelId, reservationId))
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(reservationCancellationPolicyCriteria),
            ReservationCancellationPolicyCriteria.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          log.error(
              "Error while trying to create cancellation policy for hotelId={} and reservationId={}",
              hotelId, reservationId);
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_CREATE_CANCELLATION_POLICY_EXCEPTION,
              String.format("Error while trying to create cancellation policy for "
                  + PARAMS, hotelId, reservationId)));
        })
        .bodyToMono(uk.co.whitbread.hotel.ohip.adapter.generated.models.rsvV0.Status.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public Reservation getReservationWithRoutingInstructions(String hotelId, String reservationId) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(FETCH_INSTRUCTIONS_PARAM,
        Arrays.asList(RESERVATION_FETCH_INSTRUCTION, "RoutingInstructions", "Comments"));
    return ohipWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(reservationOhipProperties.getReservationEndpoint() + "/"
                + reservationId)
            .queryParams(params)
            .build(hotelId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_GET_RESERVATION_ROUTING_INSTRUCTION_EXCEPTION,
              String.format(
                  "Error while trying to get reservation with routing instructions for "
                      + PARAMS, hotelId, reservationId)));
        })
        .bodyToMono(Reservation.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public void deleteReservationRequest(String hotelId, String reservationId) {
    ohipWebClient
        .delete()
        .uri(uriBuilder ->
            uriBuilder.path(reservationOhipProperties.getDeleteReservationEndpoint())
                .build(hotelId, reservationId))
        .headers(httpHeaders -> {
          httpHeaders.add(HOTEL_ID_HEADER, hotelId);
          /*
          * content-length header explicitly mentioned
          * otherwise delete fails after boot upgrade
          * */
          httpHeaders.setContentLength(0);
        })
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_DELETE_RESERVATION_EXCEPTION,
              String.format(
                  "Error while trying to delete reservation for " + PARAMS,
                  hotelId, reservationId)));
        })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public Mono<ResponseEntity<Void>> deleteRoutingInstruction(String hotelId, String reservationId,
      MultiValueMap<String, String> params) {
    return ohipWebClient
        .delete()
        .uri(uriBuilder ->
            uriBuilder.path(reservationOhipProperties.getRoutingInstructions() + "/folio")
                .queryParams(params).build(hotelId, reservationId))
        .headers(httpHeaders -> {
          httpHeaders.add(HOTEL_ID_HEADER, hotelId);
          /*
           * content-length header explicitly mentioned
           * otherwise delete fails after boot upgrade
           * */
          httpHeaders.setContentLength(0);
        })
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_DELETE_ROUTING_INSTRUCTIONS_EXCEPTION,
              String.format(
                  "Error while trying to delete routing instruction for " + PARAMS,
                  hotelId, reservationId)));
        })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  public Reservation getReservationWithPreferences(String hotelId, String reservationId) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(FETCH_INSTRUCTIONS_PARAM, Arrays.asList(RESERVATION_FETCH_INSTRUCTION, "Preferences"));
    return ohipWebClient

        .get()
        .uri(uriBuilder -> uriBuilder.path(
                StringUtils.joinWith("/", reservationOhipProperties.getReservationEndpoint(),
                    RESERVATION_ID))
            .queryParams(params)
            .build(hotelId, reservationId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_RETRIVE_RESERVATION_PREFERENCES_EXCEPTION, String.format(
              "Error while trying to get reservation with preference for " + PARAMS,
              hotelId, reservationId)));
        })
        .bodyToMono(Reservation.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public ActivityLog getActivityLog(String hotelId, String reservationId, Integer limit,
      Integer offset) {

    return ohipWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(changeLogOhipProperties.getActivityLogEndpoint())
            .queryParam(PARAMETER_NAME, RESV_NAME_ID)
            .queryParam(PARAMETER_VALUE, reservationId)
            .queryParam(LIMIT, limit)
            .queryParam(OFFSET, offset)
            .build(hotelId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatusCode.valueOf(204)), clientResponse -> {
          var exception = new ChangeLogException(ErrorCode.DIGITAL_NO_ACTIVITY_LOG,
              "No activity log found");
          return Mono.error(exception);
        })
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new ChangeLogException(
              ErrorCode.OHIP_GET_LOG_ACTIVITY_OPERA_EXCEPTION,
              "Error while trying to get activity log from Opera"));
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new ChangeLogException(ErrorCode.OHIP_GET_LOG_ACTIVITY_EXCEPTION,
              "Unable to retrieve activity log"));
        })
        .bodyToMono(ActivityLog.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public String addAttachmentToReservation(FileToUpload request) {
    log.debug("Request to upload attachment for a Reservation {}", request.getLinkId());
    return ohipWebClient.post()
        .uri(uriBuilder -> uriBuilder.path(reservationOhipProperties.getFileAttachmentEndpoint())
            .build())
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, request.getHotelId()))
        .body(Mono.just(request), FileToUpload.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(
              new HotelReservationException(ErrorCode.OHIP_CHANGE_RESERVATION_EXCEPTION,
                  String.format("Error while trying to add attachment for " + PARAMS,
                      request.getHotelId(), request.getLinkId())));
        })
        .bodyToMono(String.class)
        .map(responseBody -> "Success")
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .timeout(Duration.ofSeconds(OPERA_TIMEOUT))
        .onErrorResume(TimeoutException.class, ex -> {
          log.error("Operation timed out after 10 seconds");
          return Mono.error(
              new HotelReservationException(ErrorCode.OHIP_CHANGE_RESERVATION_EXCEPTION,
                  "Operation timed out while adding attachment to reservation"));
        })
        .block();
  }

  public Status savePreCheckInStatus(PreCheckInReservation request, String hotelId,
      String reservationId) {
    log.debug("Request to save Digital PrecheckIn status for hotelId {} and reservation {}",
        hotelId, reservationId);
    return ohipWebClient.post()
        .uri(uriBuilder -> uriBuilder.path(reservationOhipProperties.getPreCheckInStatusEndpoint())
            .build(hotelId, reservationId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(request), PreCheckInReservation.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(ErrorCode.OHIP_POST_PROFILE_EXCEPTION,
              String.format("Unable to save Pre-CheckIn status of the reservation: %s",
                  reservationId)));
        })
        .bodyToMono(Status.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public void deleteReservationPreCheckIn(String hotelId, String reservationId) {
    log.debug("Request to update Digital PrecheckIn status for hotelId {} and reservation {}",
        sanitize(hotelId), sanitize(reservationId));
    ohipWebClient
        .delete()
        .uri(uriBuilder ->
            uriBuilder.path(reservationOhipProperties.getReservationIdEndpoint() + "/preCheckIn")
                .build(hotelId, reservationId))
        .headers(httpHeaders -> {
          httpHeaders.add(HOTEL_ID_HEADER, hotelId);
          httpHeaders.setContentLength(0);
        })
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_DELETE_RESERVATION_EXCEPTION,
              String.format(
                  "Error while trying to delete reservation pre-checkIn for " + PARAMS,
                  hotelId, reservationId)));
        })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public void deleteReservationAttachment(String hotelId, String reservationId,
      String attachmentId) {
    ohipWebClient
        .delete()
        .uri(uriBuilder ->
            uriBuilder.path(reservationOhipProperties.getDeleteFileAttachmentEndpoint())
                .build(hotelId, reservationId, attachmentId))
        .headers(httpHeaders -> {
          httpHeaders.add(HOTEL_ID_HEADER, hotelId);
          httpHeaders.setContentLength(0);
        })
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_DELETE_RESERVATION_EXCEPTION,
              String.format(
                  "Error while trying to delete reservation attachment for "
                      + "hotelId=%s, reservationId=%s and attachmentId=%s",
                  hotelId, reservationId, attachmentId)));
        })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

}
