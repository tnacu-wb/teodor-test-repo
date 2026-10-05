package uk.co.whitbread.ohip.infrastructure.rest.client.packages;

import static java.util.Collections.singletonList;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.ConfigPostingAttributesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.PackageCodeHeaderType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.PackageCodeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.PkgInventoryItemType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageGroupsInfo;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.packages.in.DonationPackagesRequest;
import uk.co.whitbread.ohip.domain.model.packages.in.PackageGroupRequest;
import uk.co.whitbread.ohip.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.ohip.domain.model.packages.out.DonationPackagesResponse;
import uk.co.whitbread.ohip.domain.model.packages.out.PackageGroupsResponse;
import uk.co.whitbread.ohip.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.ohip.domain.ports.secondary.PackagesOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.exceptions.PackagesException;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.DonationPackagesRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.DonationPackagesResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackageGroupResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackageGroupsRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackagesRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackagesResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.PackagesResponseOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.RestaurantsResponseOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip.OhipPackagesClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip.OhipRestaurantsClient;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Slf4j
@RequiredArgsConstructor
public class PackagesOutPortImpl implements PackagesOutPort {

  private final PackagesRequestOhipMapper packagesRequestOhipMapper;

  private final DonationPackagesRequestOhipMapper donationPackagesRequestOhipMapper;

  private final PackagesResponseOhipMapper packagesResponseOhipMapper;

  private final DonationPackagesResponseOhipMapper donationPackagesResponseOhipMapper;

  private final OhipPackagesClient ohipPackagesClient;

  private final OhipRestaurantsClient ohipRestaurantsClient;

  private final ConcurrentTracer concurrentTracer;
  private final PackageGroupsRequestOhipMapper packageGroupsRequestOhipMapper;
  private final PackageGroupResponseOhipMapper packageGroupsResponseOhipMapper;


  @Override
  public PackagesResponse getPackages(PackagesRequest packagesRequest) {
    try {
      log.debug("Entered getPackages for hotelId={}",
          packagesRequest.getHotelId());
      var requestOhip = packagesRequestOhipMapper.toOhipDto(packagesRequest);

      //Change meant to fix the incongruity between meal deal and the other meal packages
      requestOhip.setAdults(1);
      final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
      if (packagesRequest.getMealInclusiveRate() == null
          || !packagesRequest.getMealInclusiveRate()) {
        params.put(OhipConstants.SELL_SEPARATE_PARAM, singletonList(OhipConstants.BOOLEAN_TRUE));
      }
      params.put(OhipConstants.INCLUDE_GROUP_PARAM, singletonList(OhipConstants.BOOLEAN_TRUE));

      // FIXME: Upgrade to 6.1 needed to use @Cacheable with reactive types
      // FIXME: This is a workaround
      var restaurants =
          CompletableFuture.supplyAsync(concurrentTracer.wrap((Supplier<RestaurantsResponseOhipDto>)
              () -> ohipRestaurantsClient.getHotelRestaurants(requestOhip)));

      var packages =
          CompletableFuture.supplyAsync(concurrentTracer.wrap((Supplier<PackagesResponseOhipDto>)
              () -> ohipPackagesClient.getPackages(requestOhip, params).block()));

      return CompletableFuture.allOf(restaurants, packages)
              .thenApply(result ->
                      buildPackagesResponse(
                              restaurants.join(),
                              packages.join()))
              .join();
    } catch (RuntimeException error) {
      var message = String.format("Error while trying to get packages for hotelId=%s",
          packagesRequest.getHotelId());
      throw new PackagesException(ErrorCode.DIGITAL_NO_HOTEL_RESTAURANTS, message);

    }
  }


  private PackagesResponse buildPackagesResponse(
          RestaurantsResponseOhipDto restaurantsResponse,
          PackagesResponseOhipDto packagesResponse) {

    Map<String, String> packageToArticle =
            buildPackageToArticleMap(packagesResponse);

    return packagesResponseOhipMapper.toModel(
            restaurantsResponse,
            packagesResponse,
            packageToArticle);
  }

  private Map<String, String> buildPackageToArticleMap(
          PackagesResponseOhipDto packagesResponse
  ) {
    return packagesResponse.getPackageCodesList()
            .getPackageCodes()
            .stream()
            .flatMap(pkg ->
                    Optional.ofNullable(pkg.getPackageCodeInfo())
                            .orElse(Collections.emptyList())
                            .stream()
            )
            .collect(Collectors.toMap(
                    PackageCodeType::getCode,
                    info -> Optional.ofNullable(info.getHeader())
                            .map(PackageCodeHeaderType::getPostingAttributes)
                            .map(ConfigPostingAttributesType::getInventoryItems)
                            .orElse(Collections.emptyList())
                            .stream()
                            .findFirst()
                            .map(PkgInventoryItemType::getArticleNumber)
                            .orElse("")
            ));
  }

  @Override
  public DonationPackagesResponse getDonationPackagesDetails(
      DonationPackagesRequest donationPackagesRequest) {

    log.debug("Entered getDonationPackagesDetails for hotelId={}",
        donationPackagesRequest.getHotelId());
    var donationPackagesOhipRequest =
        donationPackagesRequestOhipMapper.toOhipDto(donationPackagesRequest);
    var donationPackageCodeOhipResponse =
        ohipPackagesClient.getDonationPackagesDetails(donationPackagesOhipRequest);
    return donationPackagesResponseOhipMapper.toModel(donationPackageCodeOhipResponse);
  }

  @Override
  public PackageGroupsResponse getPackagesGroups(PackageGroupRequest packageGroupRequest) {
    log.debug("Entered getPackagesGroups for hotelId={}",
        packageGroupRequest.getHotelId());

    Set<String> packageGroups = cleanPackageGroupList(
        packageGroupRequest.getPackageGroupList());
    packageGroupRequest.setPackageGroupList(packageGroups);

    Flux<PackageGroupsInfo> responseFlux;
    if (!ObjectUtils.isEmpty(packageGroups)) {
      responseFlux = Flux.fromIterable(packageGroups)
          .flatMap(packageGroup -> getPackageGroupClientInfo(packageGroupRequest.getHotelId(),
              packageGroup));
    } else {
      responseFlux = getPackageGroupClientInfo(packageGroupRequest.getHotelId(), null).flux();
    }
    List<PackageGroupsInfo> responseList = responseFlux.collectList().block();

    return packageGroupsResponseOhipMapper.toModel(responseList, packageGroupRequest);
  }

  private Set<String> cleanPackageGroupList(Set<String> packageGroupList) {
    if (ObjectUtils.isEmpty(packageGroupList)) {
      return Collections.emptySet();
    }
    return packageGroupList.stream()
        .filter(Objects::nonNull)
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.toSet());
  }

  private Mono<PackageGroupsInfo> getPackageGroupClientInfo(String hotelId, String packageGroup) {
    return ohipPackagesClient.getPackageGroups(
        packageGroupsRequestOhipMapper.toOhipDto(hotelId, packageGroup));
  }
}