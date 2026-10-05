package uk.co.whitbread.ohip.infrastructure.rest.client.profile.ohip;


import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ACCOUNT_RECEIVABLE_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.AR_NUMBER_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.COMPANY_PROFILE_NAME_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.COMPANY_PROFILE_TYPE_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.EXCLUDE_INACTIVE_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.FETCH_INSTRUCTIONS_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HOTEL_ID_HEADER;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HUB_ID_HEADER;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.INCLUDE_ANONYMIZED_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.INCLUDE_PURGE_PROFILES_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.LIMIT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.getOnStatusException;
import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.getRetrySpec;
import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.logErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Company;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileSummaries;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.profile.in.AddProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.out.CreateProfileResponse;
import uk.co.whitbread.ohip.domain.model.profile.out.ProfileReservationDetailsResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.exception.ProfileException;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.ohip.properties.ProfileOhipProperties;

@Component
@RequiredArgsConstructor
@Slf4j
public class OhipProfileClient {

  private final WebClient ohipWebClient;

  private final ProfileOhipProperties profileOhipProperties;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "OperaCompaniesCache", key = "#globalCompanyId", unless = "#result == null")
  public Company getCompanyByCorporateId(String globalCompanyId) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(FETCH_INSTRUCTIONS_PARAM, "ADDRESS");
    queryParams.add(FETCH_INSTRUCTIONS_PARAM, "COMMUNICATION");
    queryParams.add(FETCH_INSTRUCTIONS_PARAM, "SalesInfo");
    queryParams.add(FETCH_INSTRUCTIONS_PARAM, "Keyword");
    queryParams.add(FETCH_INSTRUCTIONS_PARAM, "Profile");
    return ohipWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(profileOhipProperties.getCompaniesEndpoint())
            .queryParams(queryParams)
            .build(globalCompanyId))
        .headers(httpHeaders -> httpHeaders.add(HUB_ID_HEADER, profileOhipProperties.getHubId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new ProfileException(ErrorCode.OHIP_GET_COMPANY_PROFILE_EXCEPTION,
              "Error while trying to get company profile"));
        })
        .bodyToMono(Company.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "OperaCompaniesProfileCache", key = "#companyId", unless = "#result == null")
  public Profile getCompanyProfile(String companyId) {
    return ohipWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(profileOhipProperties.getCompaniesByCompanyIdEndpoint())
            .build(companyId))
        .headers(httpHeaders -> httpHeaders.add(HUB_ID_HEADER, profileOhipProperties.getHubId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new ProfileException(ErrorCode.OHIP_GET_COMPANY_PROFILE_EXCEPTION,
              "Error while trying to get company profile"));
        })
        .bodyToMono(Profile.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public CreateProfileResponse createProfile(Profile createProfileRequest,
      String hotelId) {
    return ohipWebClient.post()
        .uri(
            uriBuilder -> uriBuilder.path(profileOhipProperties.getProfilesEndpoint())
                .build())
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(createProfileRequest), Profile.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new ProfileException(ErrorCode.OHIP_CREATE_PROFILE_EXCEPTION,
              "Error while trying to create profile"));
        })
        .bodyToMono(CreateProfileResponse.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public void addProfile(AddProfileRequest addProfileRequest, String hotelId) {
    ohipWebClient.put()
        .uri(uriBuilder -> uriBuilder.path(profileOhipProperties.getAddProfileEndpoint())
            .build(hotelId,
                addProfileRequest.getReservations().get(0).getReservationIdList().get(0)
                    .getId()))
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(addProfileRequest), AddProfileRequest.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            getOnStatusException(log, response, new ProfileException(
                ErrorCode.OHIP_ADD_PROFILE_RESERVATION_EXCEPTION,
                "Error while trying to add profile to reservation"), ErrorCode.OHIP_ADD_PROFILE_RESERVATION_EXCEPTION))
        .toBodilessEntity()
        .retryWhen(getRetrySpec(new ProfileException(ErrorCode.OHIP_RETRIES_EXHAUSTED_EXCEPTION,
            "Error while trying to add profile to reservation. Max retries exhausted")))
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public ProfileReservationDetailsResponse getProfileIdByReservation(String reservationId,
      String hotelId) {

    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(FETCH_INSTRUCTIONS_PARAM, "GuestLastStay");
    return ohipWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(profileOhipProperties.getReservationGuestEndpoint())
            .queryParams(queryParams)
            .build(hotelId, reservationId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new ProfileException(ErrorCode.OHIP_GET_PROFILEID_EXCEPTION,
              "Error while trying to get ProfileId"));
        })
        .bodyToMono(ProfileReservationDetailsResponse.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "CompaniesProfileCache")
  public ProfileSummaries getCompaniesProfile(String hotelId, String arNumber, String companyName,
      int limit) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    if (ObjectUtils.isNotEmpty(arNumber)) {
      queryParams.add(AR_NUMBER_PARAM, arNumber);
    } else {
      queryParams.add(COMPANY_PROFILE_NAME_PARAM, String.format("%%25%s", companyName));
    }
    queryParams.add(COMPANY_PROFILE_TYPE_PARAM, "Company");
    queryParams.add(INCLUDE_PURGE_PROFILES_PARAM, "false");
    queryParams.add(ACCOUNT_RECEIVABLE_PARAM, "true");
    queryParams.add(EXCLUDE_INACTIVE_PARAM, "true");
    queryParams.add(INCLUDE_ANONYMIZED_PARAM, "true");
    queryParams.add(FETCH_INSTRUCTIONS_PARAM, "SalesInfo");
    queryParams.add(LIMIT, Integer.toString(limit));
    return ohipWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(profileOhipProperties.getProfilesEndpoint())
            .queryParams(queryParams)
            .build())
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new ProfileException(ErrorCode.OHIP_GET_COMPANIES_PROFILE_EXCEPTION,
              "Error while trying to get Companies Profile"));
        })
        .bodyToMono(ProfileSummaries.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public void updateProfile(Profile updateProfileRequest, String hotelId) {
    ohipWebClient.put()
        .uri(uriBuilder -> uriBuilder.path(
                StringUtils.joinWith("", profileOhipProperties.getProfilesEndpoint(), "/{profileId}"))
            .build(updateProfileRequest.getProfileIdList().get(0).getId()))
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(updateProfileRequest), Profile.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            getOnStatusException(log, response, new ProfileException(ErrorCode.OHIP_UPDATE_PROFILE_EXCEPTION,
                "Error while trying to update the profile"), ErrorCode.OHIP_UPDATE_PROFILE_EXCEPTION))
        .toBodilessEntity()
        .retryWhen(getRetrySpec(new ProfileException(ErrorCode.OHIP_RETRIES_EXHAUSTED_EXCEPTION,
            "Error while trying to update the profile. Max retries exhausted")))
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}
