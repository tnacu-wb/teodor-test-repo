package uk.co.whitbread.shared.cdh;

import static uk.co.whitbread.shared.cdh.properties.UriPaths.IB_PAY_SERVICES_ENDPOINT;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.REGISTRATION;
import static uk.co.whitbread.shared.cdh.utils.RequestUtils.buildHeaders;
import static uk.co.whitbread.shared.cdh.utils.RequestUtils.queryParamsToMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.shared.cdh.model.GetDashboardDetailsQueryParams;
import uk.co.whitbread.shared.cdh.model.PibaTetheredGuidResponse;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationDataService {

  static final String REGISTRATION_DETAILS_CACHE = "CdhRegistrationDetails";

  private final CustomerDataHubClient cdhClient;
  private final CdhApiProperties cdhApiProperties;
  private final CdhApiOauthProperties cdhApiOauthProperties;

  /**
   * Get a list of PIBA GUIDs
   *
   * @param queryParams wrapper over query parameters
   * @param accessedBy  information about who is making the request
   * @return list of PIBA GUID's matching the query criteria
   */
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1HourCdh",
      value = REGISTRATION_DETAILS_CACHE, keyGenerator = "registrationDetailsKeyGenerator")
  public List<PibaTetheredGuidResponse> getDashboardDetails(
      GetDashboardDetailsQueryParams queryParams, String accessedBy, String accessContext) {

    log.info("Getting dashboard details for {}", queryParams);
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + IB_PAY_SERVICES_ENDPOINT + REGISTRATION);
    builder.queryParams(queryParamsToMap(queryParams));

    return cdhClient.getListCDH(
        builder.toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy, accessContext),
        PibaTetheredGuidResponse.class).stream()
        .map(response -> {
          String scheme = Optional.ofNullable(response.getScheme())
              .map(String::toUpperCase)
              .orElse("GB");
          response.setScheme(scheme);
          return response;
        }).collect(Collectors.toCollection(ArrayList::new));
  }
}
