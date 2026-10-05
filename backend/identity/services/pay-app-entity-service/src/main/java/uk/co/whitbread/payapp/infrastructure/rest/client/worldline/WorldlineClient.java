package uk.co.whitbread.payapp.infrastructure.rest.client.worldline;

import static uk.co.whitbread.payapp.infrastructure.rest.client.worldline.utils.WorldlineUtils.setWorldlineHeaders;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.payapp.ErrorCode;
import uk.co.whitbread.payapp.domain.model.in.AddApplicationCardDetails;
import uk.co.whitbread.payapp.domain.model.in.LookupName;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.exceptions.WorldlineResponseException;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.SubmitApplicationRequestDetailsDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WLAppCompanyDetailsUpdateRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WLAppContactDetailsUpdateRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineAppCancelRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineAppInitRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineAppPreCheckRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineHeadersDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.AppLookupDataEntryDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.FetchApplicationDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.GetAppCardsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.GetUserPreferencesResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.SubmitApplicationDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAddApplicationCardResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppCancelResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppCompanyDetailsLookupResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppCompanyDetailsUpdateResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppContactDetailsUpdateResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppInitResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppLookupResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLDeleteCardDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLHostedPageAppInitResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WorldlineAppPreCheckResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WorldlineBankDetailsStatusResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.properties.WorldlineProperties;

@Component
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("java:S6539")
public class WorldlineClient {

  private static final String INCOMPLETE_APPLICATION = "Incomplete Application";
  private final WebClient worldlineWebClient;
  private final WorldlineProperties worldlineProperties;

  private static final String WL_ON_ERROR_STATUS_MESSAGE = "Error when calling WorldLine REST API: ";

  public WLAppInitResponseDto appInitWorldline(
      WorldlineAppInitRequestDto worldlineAppInitRequestDto,
      WorldlineHeadersDto worldlineHeadersDto) {
    log.debug("Entered appInitWorldline with email={}", worldlineAppInitRequestDto.getEmail());
    return worldlineWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(worldlineProperties.getAppInitEndpoint()).build())
        .headers(setWorldlineHeaders(worldlineHeadersDto))
        .bodyValue(worldlineAppInitRequestDto)
        .retrieve()
        .onStatus(HttpStatusCode::isError,
            response -> response.bodyToMono(Object.class)
                .map(wlResponse -> new WorldlineResponseException(
                    ErrorCode.WORLDLINE_APP_INIT_ERROR, WL_ON_ERROR_STATUS_MESSAGE + wlResponse
                )))
        .bodyToMono(WLAppInitResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while initiating application for email=%s from WorldLine.",
                worldlineAppInitRequestDto.getEmail())))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "FetchApplicationDetailsCache", key = "#applicationGuid")
  public FetchApplicationDetailsResponseDto fetchWorldlineApplicationDetails(
      String applicationGuid,
      WorldlineHeadersDto worldlineHeadersDto) {
    log.debug("Entered getWorldlineApplicationDetails with applicationGuid={}", applicationGuid);

    var fetchApplicationDetailsResponseDto = worldlineWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(worldlineProperties.getApplicationDetailsEndpoint())
            .build(applicationGuid))
        .headers(setWorldlineHeaders(worldlineHeadersDto))
        .retrieve()
        .onStatus(HttpStatusCode::isError,
            response -> response.bodyToMono(Object.class)
                .map(wlResponse -> new WorldlineResponseException(
                    ErrorCode.WORLDLINE_APP_FETCH_ERROR, WL_ON_ERROR_STATUS_MESSAGE + wlResponse
                )))
        .bodyToMono(FetchApplicationDetailsResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while fetching application details for applicationGuid=%s from Worldline.",
                applicationGuid)))
        .block();

    // Map the WorldLine status to the internal status
    Optional.ofNullable(fetchApplicationDetailsResponseDto)
        .filter(response -> Objects.nonNull(response.getData())
            && Objects.nonNull(response.getData().getApplicationDetails())
            && Objects.isNull(response.getData().getApplicationDetails().getSubmittedDate())
        ).ifPresent(response ->
            response.getData().getApplicationDetails().setStatus(
                mapWorldLineStatusToInternal(response.getData().getApplicationDetails().getStatus()))
        );

    return fetchApplicationDetailsResponseDto;
  }

  public Map<String, List<String>> getAppLookup(
      List<LookupName> lookupNames, WorldlineHeadersDto worldlineHeadersDto) {
    log.debug("Entered getAppLookup with lookupNames={}", lookupNames);

    return Flux.fromIterable(lookupNames)
        .flatMap(lookupName -> worldlineWebClient
            .get()
            .uri(uriBuilder -> uriBuilder.path(worldlineProperties.getAppLookupEndpoint())
                .build(lookupName.getName()))
            .headers(setWorldlineHeaders(worldlineHeadersDto))
            .retrieve()
            .onStatus(HttpStatusCode::isError,
                response -> response.bodyToMono(Object.class)
                    .map(wlResponse -> new WorldlineResponseException(
                        ErrorCode.WORLDLINE_APP_LOOKUP_ERROR,
                        WL_ON_ERROR_STATUS_MESSAGE + wlResponse
                    )))
            .bodyToMono(WLAppLookupResponseDto.class)
            .map(result -> new AppLookupDataEntryDto(
                lookupName.getName(),
                result.getData().getLookupData().get(lookupName.getName()))))
        .collectMap(AppLookupDataEntryDto::getLookupName, AppLookupDataEntryDto::getLookupValues)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while getting app lookup info with lookupNames=%s", lookupNames)))
        .block();
  }

  public WLAppContactDetailsUpdateResponseDto appContactDetailsUpdateWorldline(
      String applicationGuid,
      WLAppContactDetailsUpdateRequestDto wlAppContactDetailsUpdateRequestDto,
      WorldlineHeadersDto worldlineHeadersDto) {
    log.debug("Entered appContactDetailsUpdateWorldline with email={}",
        wlAppContactDetailsUpdateRequestDto.getEmail());
    return worldlineWebClient
        .put()
        .uri(uriBuilder -> uriBuilder.path(worldlineProperties.getAppContactDetailsEndpoint())
            .build(applicationGuid))
        .headers(setWorldlineHeaders(worldlineHeadersDto))
        .bodyValue(wlAppContactDetailsUpdateRequestDto)
        .retrieve()
        .onStatus(HttpStatusCode::isError,
            response -> response.bodyToMono(Object.class)
                .map(wlResponse -> new WorldlineResponseException(
                    ErrorCode.WORLDLINE_APP_CONTACT_DETAILS_UPDATE_ERROR,
                    WL_ON_ERROR_STATUS_MESSAGE + wlResponse
                )))
        .bodyToMono(WLAppContactDetailsUpdateResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while updating application contact details for applicationGuid=%s and email=%s from WorldLine.",
                applicationGuid, wlAppContactDetailsUpdateRequestDto.getEmail())))
        .block();
  }

  public WLAppCompanyDetailsUpdateResponseDto appCompanyDetailsUpdate(
      String applicationGuid,
      WLAppCompanyDetailsUpdateRequestDto wlAppCompanyDetailsUpdateRequestDto,
      WorldlineHeadersDto worldlineHeadersDto) {

    log.debug("Entered appCompanyDetailsUpdateWorldline with companyName={}, companyType={}",
        wlAppCompanyDetailsUpdateRequestDto.getCompanyName(),
        wlAppCompanyDetailsUpdateRequestDto.getCompanyType());

    return worldlineWebClient
        .put()
        .uri(uriBuilder -> uriBuilder.path(worldlineProperties.getAppCompanyDetailsEndpoint())
            .build(applicationGuid))
        .headers(setWorldlineHeaders(worldlineHeadersDto))
        .bodyValue(wlAppCompanyDetailsUpdateRequestDto)
        .retrieve()
        .onStatus(HttpStatusCode::isError,
            response -> response.bodyToMono(Object.class)
                .map(wlResponse -> new WorldlineResponseException(
                    ErrorCode.WORLDLINE_APP_COMPANY_DETAILS_UPDATE_ERROR,
                    WL_ON_ERROR_STATUS_MESSAGE + wlResponse
                )))
        .bodyToMono(WLAppCompanyDetailsUpdateResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while updating application company details for applicationGuid=%s, "
                    + "companyName=%s and companyType=%s from WorldLine.",
                applicationGuid, wlAppCompanyDetailsUpdateRequestDto.getCompanyName(),
                wlAppCompanyDetailsUpdateRequestDto.getCompanyType())))
        .block();
  }

  public WLAppCancelResponseDto appCancelWorldline(String applicationGuid,
      WorldlineHeadersDto worldlineHeadersDto,
      WorldlineAppCancelRequestDto worldlineAppCancelRequestDto) {
    log.debug("Entered appCancelWorldline with applicationGUID={}", applicationGuid);
    return worldlineWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(worldlineProperties.getAppCancelEndpoint())
            .build(applicationGuid))
        .headers(setWorldlineHeaders(worldlineHeadersDto))
        .bodyValue(worldlineAppCancelRequestDto)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(Object.class)
                .flatMap(body -> {
                  log.error(WL_ON_ERROR_STATUS_MESSAGE + body);
                  return Mono.error(new WorldlineResponseException(
                      ErrorCode.WORLDLINE_APP_CANCEL_ERROR, WL_ON_ERROR_STATUS_MESSAGE + body));
                }))
        .bodyToMono(WLAppCancelResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public WLAppCompanyDetailsLookupResponseDto lookupCompanyDetailsWorldline(
      String companyRegistrationNumber, WorldlineHeadersDto wlHeaders) {
    log.debug("Entered lookupCompanyDetailsWorldline with companyRegistrationNumber={}",
        companyRegistrationNumber);

    return worldlineWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(worldlineProperties.getAppCompanyDetailsLookupEndpoint())
            .build(companyRegistrationNumber))
        .headers(setWorldlineHeaders(wlHeaders))
        .retrieve()
        .onStatus(HttpStatusCode::isError,
            response -> response.bodyToMono(Object.class)
                .map(wlResponse -> new WorldlineResponseException(
                    ErrorCode.WORLDLINE_COMPANY_DETAILS_LOOKUP_ERROR,
                    WL_ON_ERROR_STATUS_MESSAGE + wlResponse
                )))
        .bodyToMono(WLAppCompanyDetailsLookupResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while looking up company details for companyRegistrationNumber=%s from Worldline.",
                companyRegistrationNumber)))
        .block();
  }

  public GetUserPreferencesResponseDto getUserPreferences(String tetheredUserGuid,
      WorldlineHeadersDto worldlineHeadersDto) {
    log.debug("Entered getUserPreferences with tetheredUserGuid={}", tetheredUserGuid);

    return worldlineWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(worldlineProperties.getUserPreferencesEndpoint())
            .build(tetheredUserGuid))
        .headers(setWorldlineHeaders(worldlineHeadersDto))
        .header("tetheredUserGuid", tetheredUserGuid)
        .retrieve()
        .onStatus(HttpStatusCode::isError,
            response -> response.bodyToMono(Object.class)
                .map(wlResponse -> new WorldlineResponseException(
                    ErrorCode.WORLDLINE_USER_PREFERENCES_FETCH_ERROR,
                    WL_ON_ERROR_STATUS_MESSAGE + wlResponse
                )))
        .bodyToMono(GetUserPreferencesResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while fetching user preferences for tetheredUserGuid=%s from Worldline.",
                tetheredUserGuid)))
        .block();
  }

  public WLAddApplicationCardResponseDto addApplicationCard(String applicationGuid,
      AddApplicationCardDetails cardDetails, WorldlineHeadersDto worldlineHeadersDto) {
    log.debug("Entered addApplicationCard with applicationGuid={}", applicationGuid);

    return worldlineWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(worldlineProperties.getAppCardAddEndpoint())
            .build(applicationGuid))
        .headers(setWorldlineHeaders(worldlineHeadersDto))
        .bodyValue(cardDetails)
        .retrieve()
        .onStatus(HttpStatusCode::isError,
            response -> response.bodyToMono(Object.class)
                .map(wlResponse -> new WorldlineResponseException(
                    ErrorCode.WORLDLINE_APP_ADD_CARD_ERROR, WL_ON_ERROR_STATUS_MESSAGE + wlResponse
                )))
        .bodyToMono(WLAddApplicationCardResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while adding card for applicationGuid=%s from WorldLine.",
                applicationGuid)))
        .block();
  }

  public WLDeleteCardDto deleteApplicationCard(String applicationGuid, String cardGuid,
      WorldlineHeadersDto worldlineHeadersDto) {
    log.debug("Entered deleteCard with applicationGuid={} and cardGuid={}", applicationGuid,
        cardGuid);

    return worldlineWebClient
        .delete()
        .uri(uriBuilder -> uriBuilder
            .path(worldlineProperties.getAppCardDeleteEndpoint())
            .build(applicationGuid, cardGuid))
        .headers(setWorldlineHeaders(worldlineHeadersDto))
        .retrieve()
        .onStatus(HttpStatusCode::isError,
            response -> response.bodyToMono(Object.class)
                .map(wlResponse -> new WorldlineResponseException(
                    ErrorCode.WORLDLINE_APP_CARD_DELETE_ERROR,
                    WL_ON_ERROR_STATUS_MESSAGE + wlResponse
                )))
        .bodyToMono(WLDeleteCardDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while deleting card with applicationGuid=%s and cardGuid=%s from Worldline.",
                applicationGuid, cardGuid)))
        .block();
  }

  public GetAppCardsResponseDto getAppCards(String applicationGuid,
      int page, int maxDisplayRows, WorldlineHeadersDto worldlineHeadersDto) {
    log.debug("Entered getAppCards with applicationGuid={}, page={}, maxDisplayRows={}",
        applicationGuid, page, maxDisplayRows);

    return worldlineWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(worldlineProperties.getAppCardListEndpoint())
            .queryParam("page", page)
            .queryParam("maxDisplayRows", maxDisplayRows)
            .build(applicationGuid))
        .headers(setWorldlineHeaders(worldlineHeadersDto))
        .retrieve()
        .onStatus(HttpStatusCode::isError,
            response -> response.bodyToMono(Object.class)
                .map(wlResponse -> new WorldlineResponseException(
                    ErrorCode.WORLDLINE_APP_CARDS_GET_ERROR,
                    WL_ON_ERROR_STATUS_MESSAGE + wlResponse
                )))
        .bodyToMono(GetAppCardsResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while fetching app cards for applicationGuid=%s from Worldline.",
                applicationGuid)))
        .block();
  }

  public SubmitApplicationDto submitApplication(
      SubmitApplicationRequestDetailsDto submitApplicationRequestDetailsDto,
      WorldlineHeadersDto worldlineHeadersDto) {
    log.info("Entered submitApplication with applicationGuid={}",
        submitApplicationRequestDetailsDto.getApplicationGuid());

    return worldlineWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(worldlineProperties.getAppSubmitEndpoint()).build())
        .headers(setWorldlineHeaders(worldlineHeadersDto))
        .bodyValue(submitApplicationRequestDetailsDto)
        .retrieve()
        .onStatus(HttpStatusCode::isError,
            response -> response.bodyToMono(Object.class)
                .map(wlResponse -> new WorldlineResponseException(
                    ErrorCode.WORLDLINE_APP_SUBMIT_ERROR,
                    WL_ON_ERROR_STATUS_MESSAGE + wlResponse
                )))
        .bodyToMono(SubmitApplicationDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while submit application for applicationGuid=%s from Worldline.",
                submitApplicationRequestDetailsDto.getApplicationGuid())))
        .block();
  }

  public WorldlineAppPreCheckResponseDto appPreCheck(WorldlineAppPreCheckRequestDto appPreCheckRequestDto,
                                                     WorldlineHeadersDto wlHeaders) {
    log.info("Entered appPreCheck with email={}", appPreCheckRequestDto.email());

    return worldlineWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(worldlineProperties.getAppPreCheckEndpoint()).build())
        .headers(setWorldlineHeaders(wlHeaders))
        .bodyValue(appPreCheckRequestDto)
        .retrieve()
        .onStatus(HttpStatusCode::isError,
            response -> response.bodyToMono(Object.class)
                .map(wlResponse -> new WorldlineResponseException(
                    ErrorCode.WORLDLINE_APP_PRE_CHECK_ERROR,
                    WL_ON_ERROR_STATUS_MESSAGE + wlResponse
                )))
        .bodyToMono(WorldlineAppPreCheckResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error calling application pre check for email=%s from Worldline.",
                appPreCheckRequestDto.email())))
        .block();
  }

  public WLHostedPageAppInitResponseDto hostedPageAppInit(String applicationGuid,
      WorldlineHeadersDto worldlineHeadersDto) {
    log.debug("Entered hostedPageAppInit with applicationGuid={}", applicationGuid);

    return worldlineWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(worldlineProperties.getHostedPageAppInitEndpoint())
            .build(applicationGuid))
        .headers(setWorldlineHeaders(worldlineHeadersDto))
        .retrieve()
        .onStatus(HttpStatusCode::isError,
            response -> response.bodyToMono(Object.class)
                .map(wlResponse -> new WorldlineResponseException(
                    ErrorCode.WORLDLINE_HOSTED_PAGE_APP_INIT_ERROR,
                    WL_ON_ERROR_STATUS_MESSAGE + wlResponse
                )))
        .bodyToMono(WLHostedPageAppInitResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while hosted page app init for applicationGuid=%s from WorldLine.",
                applicationGuid)))
        .block();
  }

  public WorldlineBankDetailsStatusResponseDto bankDetailsStatus(String hostedPageGuid,
      WorldlineHeadersDto worldlineHeadersDto) {
    log.debug("Entered bankDetailsStatus with hostedPageGuid={}", hostedPageGuid);

    return worldlineWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(worldlineProperties.getBankDetailsStatusEndpoint())
            .build(hostedPageGuid))
        .headers(setWorldlineHeaders(worldlineHeadersDto))
        .retrieve()
        .onStatus(HttpStatusCode::isError,
            response -> response.bodyToMono(Object.class)
                .map(wlResponse -> new WorldlineResponseException(
                    ErrorCode.WORLDLINE_BANK_DETAILS_STATUS_ERROR,
                    WL_ON_ERROR_STATUS_MESSAGE + wlResponse
                )))
        .bodyToMono(WorldlineBankDetailsStatusResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while getting bank details status for hostedPageGuid=%s from WorldLine.",
                hostedPageGuid)))
        .block();
  }

  private String mapWorldLineStatusToInternal(String status) {
    return switch (status) {
      case "Outstanding" -> INCOMPLETE_APPLICATION;
      default -> status;
    };
  }

}
