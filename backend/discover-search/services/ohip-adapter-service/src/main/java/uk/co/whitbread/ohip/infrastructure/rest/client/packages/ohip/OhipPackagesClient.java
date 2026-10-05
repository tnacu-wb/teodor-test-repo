package uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip;

import static java.util.Collections.singletonList;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RATE_PLAN_CODE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageGroupsInfo;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.DonationPackagesRequestOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.PackagesResponseOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.out.PackageGroupsRequestOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.out.PackagesRequestOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip.properties.PackagesOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.properties.PackagesProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelPackageException;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class OhipPackagesClient {

  private final WebClient ohipWebClient;
  private final PackagesOhipProperties packagesOhipProperties;
  private final PackagesProperties packagesProperties;

  //  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
  //        value = "PackagesCache")
  public Mono<PackagesResponseOhipDto> getPackages(PackagesRequestOhipDto packagesRequestOhip,
      MultiValueMap<String, String> optionalParams) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(OhipConstants.HOTEL_ID_PARAM, singletonList(packagesRequestOhip.getHotelId()));
    params.put(OhipConstants.START_DATE_PARAM, singletonList(packagesRequestOhip.getStartDate()));
    params.put(OhipConstants.END_DATE_PARAM,
        singletonList(packagesRequestOhip.getEndDate()));
    params
        .put(OhipConstants.ADULTS_PARAM, singletonList(packagesRequestOhip.getAdults().toString()));

    if (packagesRequestOhip.getChildren() != null) {
      params.put(OhipConstants.CHILDREN_PARAM,
          singletonList(packagesRequestOhip.getChildren().toString()));
    }

    if (packagesRequestOhip.getRatePlan() != null) {
      params.add(RATE_PLAN_CODE, packagesRequestOhip.getRatePlan());
    }

    if (!optionalParams.isEmpty()) {
      params.putAll(optionalParams);
    }

    params.put(OhipConstants.FETCH_INSTRUCTIONS_PARAM, packagesProperties.getFetchInstructions());

    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(packagesOhipProperties.getPackagesEndpoint())
                .queryParams(params)
                .build(packagesRequestOhip.getHotelId()))
        .headers(httpHeaders ->
            httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, packagesRequestOhip.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelPackageException(ErrorCode.OHIP_GET_PACKAGES_EXCEPTION,
              String.format("Error while trying to get packages for hotelId=%s",
                  packagesRequestOhip.getHotelId())));
        })
        .bodyToMono(PackagesResponseOhipDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "DonationPackagesDetailsCache")
  public PackagesResponseOhipDto getDonationPackagesDetails(
      final DonationPackagesRequestOhipDto donationPackagesOhipRequest) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(OhipConstants.HOTEL_ID_PARAM,
        singletonList(donationPackagesOhipRequest.getHotelId()));
    params.put(OhipConstants.PACKAGE_CODE, donationPackagesOhipRequest.getPackageCodes());
    params.put(OhipConstants.FETCH_INSTRUCTIONS_PARAM,
        packagesProperties.getDonationFetchInstructions());

    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(packagesOhipProperties.getPackagesEndpoint())
                .queryParams(params)
                .build(donationPackagesOhipRequest.getHotelId()))
        .headers(httpHeaders ->
            httpHeaders.add(OhipConstants.HOTEL_ID_HEADER,
                donationPackagesOhipRequest.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelPackageException(
              ErrorCode.OHIP_GET_DONATION_PACKAGES_DETAILS_EXCEPTION,
              String.format(
                  "Error while trying to get donation packages details for hotelId=%s with %s package codes",
                  donationPackagesOhipRequest.getHotelId(),
                  (donationPackagesOhipRequest.getPackageCodes() == null ? "null" :
                      donationPackagesOhipRequest.getPackageCodes().size()))));
        })
        .bodyToMono(PackagesResponseOhipDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "PackageGroupsCache")
  public Mono<PackageGroupsInfo> getPackageGroups(PackageGroupsRequestOhipDto packageGroups) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(OhipConstants.CODE, singletonList(packageGroups.getPackageCode()));
    params.put(OhipConstants.LIMIT, singletonList(packagesProperties.getPackageGroupsLimit()));
    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(packagesOhipProperties.getPackageGroupsEndpoint())
                .queryParams(params)
                .build(packageGroups.getHotelId()))
        .headers(httpHeaders ->
            httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, packageGroups.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelPackageException(
              ErrorCode.OHIP_GET_PACKAGE_GROUPS_EXCEPTION,
              String.format(
                  "Error while trying to get package groups for hotelId=%s and packageCode=%s",
                  packageGroups.getHotelId(), packageGroups.getPackageCode())));
        })
        .bodyToMono(PackageGroupsInfo.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }
}
