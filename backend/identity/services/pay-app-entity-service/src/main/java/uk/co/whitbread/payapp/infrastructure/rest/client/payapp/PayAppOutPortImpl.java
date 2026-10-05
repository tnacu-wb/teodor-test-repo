package uk.co.whitbread.payapp.infrastructure.rest.client.payapp;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.payapp.ErrorCode;
import uk.co.whitbread.payapp.domain.model.in.AddApplicationCardRequest;
import uk.co.whitbread.payapp.domain.model.in.AppCompanyDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.AppPreCheckRequest;
import uk.co.whitbread.payapp.domain.model.in.ApplicationDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.DeleteCardRequest;
import uk.co.whitbread.payapp.domain.model.in.DeletePayApplicationRequest;
import uk.co.whitbread.payapp.domain.model.in.DirectDebitOption;
import uk.co.whitbread.payapp.domain.model.in.DirectDebitRequest;
import uk.co.whitbread.payapp.domain.model.in.GetAppCardsRequest;
import uk.co.whitbread.payapp.domain.model.in.GetAppLookupRequest;
import uk.co.whitbread.payapp.domain.model.in.GetDdSepaFormStatusRequest;
import uk.co.whitbread.payapp.domain.model.in.InitializeApplicationRequest;
import uk.co.whitbread.payapp.domain.model.in.RemoveParticipantRequest;
import uk.co.whitbread.payapp.domain.model.in.Scheme;
import uk.co.whitbread.payapp.domain.model.in.ShareAppRequest;
import uk.co.whitbread.payapp.domain.model.in.SubmitApplicationRequest;
import uk.co.whitbread.payapp.domain.model.in.UpdateAppCompanyDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.UpdateAppContactDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.UpdateResumeUrlRequest;
import uk.co.whitbread.payapp.domain.model.in.jwt.JwtTokenClaims;
import uk.co.whitbread.payapp.domain.model.out.AddApplicationCardResponse;
import uk.co.whitbread.payapp.domain.model.out.AppCompanyDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.AppPreCheckResponse;
import uk.co.whitbread.payapp.domain.model.out.ApplicationDetails;
import uk.co.whitbread.payapp.domain.model.out.ApplicationDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.DeletePayApplicationResponse;
import uk.co.whitbread.payapp.domain.model.out.DirectDebitResponse;
import uk.co.whitbread.payapp.domain.model.out.FetchApplicationsResponse;
import uk.co.whitbread.payapp.domain.model.out.GetAppCardsResponse;
import uk.co.whitbread.payapp.domain.model.out.GetDdSepaFormStatusResponse;
import uk.co.whitbread.payapp.domain.model.out.GetUserPreferencesResponse;
import uk.co.whitbread.payapp.domain.model.out.InitializeApplicationResponse;
import uk.co.whitbread.payapp.domain.model.out.ShareAppResponse;
import uk.co.whitbread.payapp.domain.model.out.SubmitApplicationResponse;
import uk.co.whitbread.payapp.domain.model.out.UpdateAppCompanyDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.UpdateAppContactDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.UpdateResumeUrlResponse;
import uk.co.whitbread.payapp.domain.ports.secondary.EmailNotificationOutPort;
import uk.co.whitbread.payapp.domain.ports.secondary.PayAppOutPort;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.CdhAdapterClient;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.CdhClient;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.exceptions.AppAlreadySharedException;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.exceptions.CdhNotFoundException;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.exceptions.EmployeeNotInCompanyException;
import uk.co.whitbread.payapp.infrastructure.rest.client.company.CompanyClient;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.exceptions.ApplicationAccessException;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.AddApplicationCardResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.AppCompanyDetailsLookupMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.AppInitRequestMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.AppPreCheckResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.ApplicationDetailsResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.GetApplicationCardsResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.GetUserPreferencesResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.SubmitApplicationRequestMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.SubmitApplicationResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.UpdateAppCompanyDetailsRequestMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.UpdateAppContactDetailsRequestMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.UpdateApplicationRequestMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.WorldlineClient;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.exceptions.WorldlineResponseException;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.TrustedPartnerCredentialsDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineAppCancelRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineAppPreCheckRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineHeadersDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.FetchApplicationDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppInitResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLHostedPageAppInitDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLHostedPageAppInitResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.properties.WorldlineProperties;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.properties.WorldlineProperties.PropertiesByLocation;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.mapper.ApplicationDetailsMapper;
import uk.co.whitbread.payapp.infrastructure.security.JwtUtils;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;
import uk.co.whitbread.shared.cdh.model.Participants;
import uk.co.whitbread.shared.cdh.model.StartApplicationRequest;
import uk.co.whitbread.shared.cdh.model.UpdateApplicationRequest;
import uk.co.whitbread.shared.cdh.model.applications.UpdateApplicationStatusRequest;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationResponse;
import uk.co.whitbread.shared.cdh.model.spending.application.CardHolder;
import uk.co.whitbread.shared.cdh.model.spending.application.UpdateApplicationCardHoldersRequest;

@Slf4j
@RequiredArgsConstructor
public class PayAppOutPortImpl implements PayAppOutPort {

  private static final String INCOMPLETE_APPLICATION = "Incomplete Application";
  private static final String TEMPORARY_NAME = "Temporary Name";
  private static final String STATUS_APP_DELETE = "Cancelled";
  private static final String STATUS_APP_REJECTED = "Rejected";
  private static final String CANCEL_REASON_DESCRIPTION = "User triggered cancel.";
  private static final String FALLBACK_COMPANY_NAME = "Fallback Company Name";
  private static final String INNB_WB_EMAIL = "InnBusiness@whitbread.com";
  private static final String APPLICATION_NOT_FOUND_IN_CDH =
      "Application not found in CDH: applicationId=%s ; applicationGuid=%s";
  private static final String OK_RESPONSE_STATUS = "200";
  private static final String HOTEL_BOOKING_ROLE = "I don't usually book hotels";
  static final String APP_ALREADY_SHARED_MESSAGE =
      "Application with applicationId=%s and applicationGuid=%s is already shared with employeeId=%d.";
  private static final String EMPLOYEE_NOT_IN_COMPANY_MESSAGE = "Employee not found within your company";

  private final WorldlineClient worldlineClient;
  private final CdhClient cdhClient;
  private final CompanyClient companyClient;
  private final AppInitRequestMapper appInitRequestMapper;
  private final UpdateAppContactDetailsRequestMapper updateAppContactDetailsRequestMapper;
  private final UpdateAppCompanyDetailsRequestMapper updateAppCompanyDetailsRequestMapper;
  private final ApplicationDetailsMapper applicationDetailsMapper;
  private final AppCompanyDetailsLookupMapper appCompanyDetailsLookupMapper;
  private final GetUserPreferencesResponseMapper getUserPreferencesMapper;
  private final ApplicationDetailsResponseMapper applicationDetailsResponseMapper;
  private final GetApplicationCardsResponseMapper getApplicationCardsResponseMapper;
  private final AddApplicationCardResponseMapper addApplicationCardResponseMapper;
  private final SubmitApplicationResponseMapper submitApplicationResponseMapper;
  private final SubmitApplicationRequestMapper submitApplicationRequestMapper;
  private final AppPreCheckResponseMapper appPreCheckResponseMapper;
  private final WorldlineProperties worldlineProperties;
  private final AuthenticatedUserService authenticatedUserService;
  private final CdhAdapterClient cdhAdapterClient;
  private final JwtUtils jwtUtils;
  private final EmailNotificationOutPort emailNotificationOutPort;
  private final UpdateApplicationRequestMapper updateApplicationRequestMapper;
  private final Executor worldlineExecutor;
  private final CacheManager cacheManager1Hour;

  @Override
  public FetchApplicationsResponse fetchApplicationsDetails(JwtTokenClaims jwtTokenClaims,
      String clientIp) {
    var applicationResponses = cdhClient.fetchApplicationsByUser(jwtTokenClaims).stream()
        .filter(app -> !STATUS_APP_DELETE.equalsIgnoreCase(app.getStage()))
        .filter(app -> !STATUS_APP_REJECTED.equalsIgnoreCase(app.getStage()))
        .toList();

    var headersMap = applicationResponses.stream()
        .map(ApplicationResponse::getScheme)
        .distinct()
        .collect(Collectors.toMap(
            this::getSchemeOrDefault,
            scheme -> buildWorldlineHeadersDto(getSchemeOrDefault(scheme), clientIp),
            (existing, replacement) -> existing
        ));

    List<CompletableFuture<ApplicationDetails>> futures = applicationResponses.stream()
        .map(application -> CompletableFuture.supplyAsync(() -> {
          var wlHeaders = headersMap.get(getSchemeOrDefault(application.getScheme()));
          var applicationDetails = worldlineClient.fetchWorldlineApplicationDetails(
              application.getApplicationGuid(), wlHeaders);
          return mapToApplicationDetails(application, applicationDetails);
        }, worldlineExecutor))
        .toList();

    List<ApplicationDetails> applications = futures.stream()
        .map(CompletableFuture::join)
        .toList();

    return new FetchApplicationsResponse(applications);
  }

  private ApplicationDetails mapToApplicationDetails(ApplicationResponse cdhApplicationResponse,
      FetchApplicationDetailsResponseDto wlApplicationDetails) {
    var applicationDetailsData = applicationDetailsMapper.toModel(cdhApplicationResponse);
    applicationDetailsData.setStatus(
        wlApplicationDetails.getData().getApplicationDetails().getStatus());
    if (StringUtils.isNotBlank(
        wlApplicationDetails.getData().getCompanyDetails().getCompanyName())) {
      applicationDetailsData.setAccountName(
          wlApplicationDetails.getData().getCompanyDetails().getCompanyName());
    }
    return applicationDetailsData;
  }

  @Override
  public InitializeApplicationResponse initializeApplication(
      InitializeApplicationRequest initializeApplicationRequest, String clientIp) {

    var wlHeaders = buildWorldlineHeadersDto(initializeApplicationRequest.getScheme(), clientIp);

    WLAppInitResponseDto wlAppInitResponseDto =
        worldlineClient.appInitWorldline(
            appInitRequestMapper.toModel(initializeApplicationRequest), wlHeaders);

    var companyId = authenticatedUserService.getCurrentUserAccount().isPresent()
        ? authenticatedUserService.getCurrentUserAccount().get().getCompanyId()
        : FALLBACK_COMPANY_NAME;

    var authorizationToken = authenticatedUserService.getAuthenticatedUser().getToken()
        .getTokenValue();

    var companyName = companyClient.getCompanyDetails(companyId, authorizationToken)
        .getRequestedCompany().getCompanyDetails().getCompanyName();

    var cdhResponse = cdhClient.startApplication(
        generateCdhStartApplicationRequest(initializeApplicationRequest, companyName,
            wlAppInitResponseDto, authenticatedUserService.getCurrentUserAccount().get()));
    log.debug("CDH startApplication status = {}, message = {}", cdhResponse.getStatus(),
        cdhResponse.getMessage());

    return InitializeApplicationResponse.builder()
        .applicationGUID(wlAppInitResponseDto.getData().getApplicationGUID())
        .applicationId(wlAppInitResponseDto.getData().getApplicationNumber())
        .build();
  }

  @Override
  public Map<String, List<String>> getAppLookup(GetAppLookupRequest request, String clientIp) {

    var wlHeaders = buildWorldlineHeadersDto(request.getScheme(), clientIp);

    return worldlineClient.getAppLookup(request.getLookupNames(), wlHeaders);
  }

  @Override
  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = {"ApplicationCache",
          "FetchApplicationDetailsCache"}, key = "#updateAppContactDetailsRequest.getApplicationGuid()")
  public UpdateAppContactDetailsResponse updateAppContactDetails(
      UpdateAppContactDetailsRequest updateAppContactDetailsRequest, JwtTokenClaims jwtTokenClaims,
      String clientIp) {

    var application = cdhClient
        .fetchApplication(updateAppContactDetailsRequest.getApplicationId(),
            updateAppContactDetailsRequest.getApplicationGuid(), jwtTokenClaims.getEmail());

    if (Objects.nonNull(application)) {
      validateApplicationAccess(application, jwtTokenClaims.getEmployeeId());

      var wlHeaders = buildWorldlineHeadersDto(updateAppContactDetailsRequest.getScheme(),
          clientIp);

      var worldlineResponse = worldlineClient.appContactDetailsUpdateWorldline(
          updateAppContactDetailsRequest.getApplicationGuid(),
          updateAppContactDetailsRequestMapper.toModel(updateAppContactDetailsRequest),
          wlHeaders);
      log.debug("WorldLine update appContactDetails responseCode = {}, responseData = {}",
          worldlineResponse.getResponseCode(),
          worldlineResponse.getData());

      var cdhResponse = cdhClient.updateApplication(
          generateCdhUpdateApplicationRequest(application, updateAppContactDetailsRequest,
              jwtTokenClaims), jwtTokenClaims.getEmail());
      return UpdateAppContactDetailsResponse.builder()
          .status(cdhResponse.getStatus())
          .message(cdhResponse.getMessage())
          .build();
    }
    throw new CdhNotFoundException(ErrorCode.CDH_APPLICATION_NOT_FOUND_ERROR, String
        .format(APPLICATION_NOT_FOUND_IN_CDH,
            updateAppContactDetailsRequest.getApplicationId(),
            updateAppContactDetailsRequest.getApplicationGuid()));
  }

  @Override
  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = {"ApplicationCache",
          "FetchApplicationDetailsCache"}, key = "#updateAppCompanyDetailsRequest.getApplicationGuid()")
  public UpdateAppCompanyDetailsResponse updateAppCompanyDetails(
      UpdateAppCompanyDetailsRequest updateAppCompanyDetailsRequest, String clientIp) {

    var emailAddress = authenticatedUserService.getCurrentUserAccount().isPresent()
        ? authenticatedUserService.getCurrentUserAccount().get().getEmail() : INNB_WB_EMAIL;

    var application = cdhClient
        .fetchApplication(updateAppCompanyDetailsRequest.getApplicationId(),
            updateAppCompanyDetailsRequest.getApplicationGuid(), emailAddress);

    if (Objects.nonNull(application)) {
      validateApplicationAccess(application,
          authenticatedUserService.getCurrentUserAccount().get().getBartEmployeeId());

      var wlHeaders = buildWorldlineHeadersDto(updateAppCompanyDetailsRequest.getScheme(),
          clientIp);
      var wlAppCompanyDetailsUpdateRequestDto = updateAppCompanyDetailsRequestMapper.toModel(
          updateAppCompanyDetailsRequest.getAppCompanyDetails());

      // if the scheme is DE, set the number of employees and industry sector to the default values
      if (Scheme.DE.equals(updateAppCompanyDetailsRequest.getScheme())) {
        wlAppCompanyDetailsUpdateRequestDto.setNumberOfEmployees("51-250");
        wlAppCompanyDetailsUpdateRequestDto.setIndustrySector("ES");
      }

      var worldlineResponse = worldlineClient.appCompanyDetailsUpdate(
          updateAppCompanyDetailsRequest.getApplicationGuid(),
          wlAppCompanyDetailsUpdateRequestDto, wlHeaders);
      log.debug("WorldLine update appCompanyDetails responseCode = {}, responseData = {}",
          worldlineResponse.getResponseCode(),
          worldlineResponse.getData());

      var cdhResponse = cdhClient.updateApplication(
          generateCdhUpdateApplicationRequest(application,
              updateAppCompanyDetailsRequest.getResumeUrl()),
          emailAddress);
      return UpdateAppCompanyDetailsResponse.builder()
          .status(cdhResponse.getStatus())
          .message(cdhResponse.getMessage())
          .build();
    }
    throw new CdhNotFoundException(ErrorCode.CDH_APPLICATION_NOT_FOUND_ERROR, String
        .format(APPLICATION_NOT_FOUND_IN_CDH,
            updateAppCompanyDetailsRequest.getApplicationId(),
            updateAppCompanyDetailsRequest.getApplicationGuid()));
  }

  @Override
  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = { "ApplicationCache", "FetchApplicationDetailsCache"}, key = "#request.getApplicationGuid()")
  public DeletePayApplicationResponse deleteApplication(DeletePayApplicationRequest request,
      JwtTokenClaims jwtTokenClaims, String clientIp) {
    log.debug("CDH deleteApplication applicationId = {}, applicationGUID = {}, scheme = {}",
        request.getApplicationId(),
        request.getApplicationGuid(), request.getScheme());

    validateApplicationAccess(request.getApplicationId(), request.getApplicationGuid(),
        jwtTokenClaims);

    // cancel application in CDH
    cdhClient.updateApplicationStatus(generateDeleteAppCdhRequest(request), jwtTokenClaims.getEmail());

    // cancel application in Worldline
    worldlineClient.appCancelWorldline(request.getApplicationGuid(),
        buildWorldlineHeadersDto(request.getScheme(), clientIp),
        WorldlineAppCancelRequestDto.builder().reasonDescription(CANCEL_REASON_DESCRIPTION)
            .build());

    return DeletePayApplicationResponse.builder()
        .status(200)
        .message("Successful application deletion.")
        .build();
  }

  @Override
  public AppCompanyDetailsResponse lookupCompanyDetails(
      AppCompanyDetailsRequest companyDetailsRequest, String clientIp) {

    var wlHeaders = buildWorldlineHeadersDto(companyDetailsRequest.getScheme(), clientIp);

    var worldlineResponse = worldlineClient.lookupCompanyDetailsWorldline(
        companyDetailsRequest.getCompanyRegistrationNumber(),
        wlHeaders);
    return AppCompanyDetailsResponse.builder()
        .data(appCompanyDetailsLookupMapper.toModel(worldlineResponse.getData()))
        .build();
  }

  @Override
  public List<GetUserPreferencesResponse> getUserPreferences(
      Set<String> tetheredGuids, String clientIp) {

    // WL user prefs API is only supported for PIBA UK, so we use GB scheme by default
    var worldlineHeaders = buildWorldlineHeadersDto(Scheme.GB, clientIp);
    var response = new LinkedList<GetUserPreferencesResponse>();
    tetheredGuids.forEach(tetheredGuid -> {
      try {
        var getUserPreferencesResponse = getUserPreferencesMapper
            .toModel(worldlineClient.getUserPreferences(tetheredGuid, worldlineHeaders));
        getUserPreferencesResponse.setTetheredUserGuid(tetheredGuid);
        response.add(getUserPreferencesResponse);
      } catch (Exception e) {
        // ignore and just log the error message, which is already done inside the called method
      }
    });
    return response;
  }

  @Override
  public ApplicationDetailsResponse applicationDetails(
      ApplicationDetailsRequest applicationDetailsRequest, JwtTokenClaims jwtTokenClaims,
      String clientIp) {

    var cdhResponse = cdhClient.fetchApplication(
        applicationDetailsRequest.getApplicationId(),
        applicationDetailsRequest.getApplicationGuid(), jwtTokenClaims.getEmail());

    validateApplicationAccess(cdhResponse, jwtTokenClaims.getEmployeeId());

    var worldlineResponse = worldlineClient.fetchWorldlineApplicationDetails(
        applicationDetailsRequest.getApplicationGuid(),
        buildWorldlineHeadersDto(applicationDetailsRequest.getScheme(), clientIp));

    return applicationDetailsResponseMapper.toModel(cdhResponse, worldlineResponse);
  }

  @Override
  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = {"ApplicationCache",
          "FetchApplicationDetailsCache"}, key = "#addApplicationCardRequest.getApplicationGuid()")
  public AddApplicationCardResponse addApplicationCard(
      AddApplicationCardRequest addApplicationCardRequest, String clientIp) {

    var cdhApplicationDetails = cdhClient.fetchApplication(
        addApplicationCardRequest.getApplicationId(),
        addApplicationCardRequest.getApplicationGuid(),
        authenticatedUserService.getAuthenticatedUser().getAccount().getEmail());

    if (!jwtUtils.hasRights(cdhApplicationDetails)) {
      throw new WorldlineResponseException(ErrorCode.USER_ROLE_MISMATCH_ERROR,
          "User does not have rights to add card");
    }

    if (Scheme.DE.equals(addApplicationCardRequest.getScheme())) {
      addApplicationCardRequest.getCardDetails().setIsConsentGiven(true);
    }

    var response =  worldlineClient.addApplicationCard(
        addApplicationCardRequest.getApplicationGuid(),
        addApplicationCardRequest.getCardDetails(),
        buildWorldlineHeadersDto(addApplicationCardRequest.getScheme(), clientIp));

    if (!addApplicationCardRequest.getCardDetails().isMyCard()
        && OK_RESPONSE_STATUS.equals(response.getResponseCode())) {
      var cardHolders = Optional.ofNullable(cdhApplicationDetails)
          .filter(Objects::nonNull)
          .map(ApplicationResponse::getCardHolders)
          .orElse(List.of());
      cardHolders = Stream.concat(
          cardHolders.stream(),
          Stream.of(CardHolder.builder()
              .employeeId(addApplicationCardRequest.getEmployeeId())
              .userGuid(response.getData().getCardGuid())
              .build())
      ).toList();
      UpdateApplicationCardHoldersRequest request = UpdateApplicationCardHoldersRequest.builder()
          .applicationId(addApplicationCardRequest.getApplicationId())
          .cardHolders(cardHolders)
          .build();

      cdhClient.updateApplicationCardHolders(request,
          authenticatedUserService.getAuthenticatedUser().getAccount().getEmail());
    }

    return addApplicationCardResponseMapper.toModel(response);
  }

  @Override
  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = {"ApplicationCache",
          "FetchApplicationDetailsCache"}, key = "#shareAppRequest.getApplicationGuid()")
  public ShareAppResponse shareApp(ShareAppRequest shareAppRequest) {

    var emailAddress = authenticatedUserService.getCurrentUserAccount()
        .map(Account::getEmail)
        .orElse(INNB_WB_EMAIL);

    GetEmployeeResponse getEmployeeResponse = Optional.ofNullable(
            cdhAdapterClient.getEmployees(shareAppRequest.getEmail(), emailAddress))
        .stream()
        .map(GetEmployeesResponse::getResults)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .filter(employee ->
            Objects.equals(employee.getBartEmployeeId(),
                String.valueOf(shareAppRequest.getEmployeeId()))
                && Objects.equals(employee.getCompanyAccountId(),
                authenticatedUserService.getCurrentUserAccount().get().getCompanyId())
        )
        .findAny()
        .orElseThrow(() -> new EmployeeNotInCompanyException(
            ErrorCode.EMPLOYEE_NOT_IN_COMPANY_EXCEPTION, EMPLOYEE_NOT_IN_COMPANY_MESSAGE));
    processListApplicationsEviction(getEmployeeResponse);

    var application = cdhClient.fetchApplication(
        shareAppRequest.getApplicationId(),
        shareAppRequest.getApplicationGuid(), emailAddress);

    if (Objects.nonNull(application)) {
      var existingParticipant = application.getParticipants().stream().filter(
          appParticipant -> Objects.equals(appParticipant.getParticipantId(),
              shareAppRequest.getEmployeeId())).findFirst();
      existingParticipant.ifPresent(applicationParticipant -> {
        throw new AppAlreadySharedException(ErrorCode.APP_ALREADY_SHARED_WITH_PARTICIPANT,
            String.format(APP_ALREADY_SHARED_MESSAGE, shareAppRequest.getApplicationId(),
                shareAppRequest.getApplicationGuid(), shareAppRequest.getEmployeeId()));
      });
      var cdhResponse = cdhClient.updateApplication(
          generateCdhShareApplicationRequest(application, shareAppRequest),
          emailAddress);
      emailNotificationOutPort.sendShareAppEmailNotificationEvent(shareAppRequest.getEmail(),
          emailAddress, application.getApplicationGuid(), application.getScheme());
      return ShareAppResponse.builder()
          .status(cdhResponse.getStatus())
          .message(cdhResponse.getMessage())
          .build();
    }
    throw new CdhNotFoundException(ErrorCode.CDH_APPLICATION_NOT_FOUND_ERROR, String
        .format(APPLICATION_NOT_FOUND_IN_CDH,
            shareAppRequest.getApplicationId(),
            shareAppRequest.getApplicationGuid()));
  }

  @Override
  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = {"ApplicationCache",
          "FetchApplicationDetailsCache"}, key = "#removeParticipantRequest.applicationGuid()")
  public void removeParticipant(RemoveParticipantRequest removeParticipantRequest) {

    var emailAddress = authenticatedUserService.getCurrentUserAccount()
        .map(Account::getEmail)
        .orElse(INNB_WB_EMAIL);

    var application = cdhClient.fetchApplication(
        removeParticipantRequest.applicationId(),
        removeParticipantRequest.applicationGuid(), emailAddress);

    if (application == null) {
      throw new CdhNotFoundException(ErrorCode.CDH_APPLICATION_NOT_FOUND_ERROR, String
          .format(APPLICATION_NOT_FOUND_IN_CDH,
              removeParticipantRequest.applicationId(),
              removeParticipantRequest.applicationGuid()));
    }

    var updateApplicationRequest = generateCdhRemoveParticipantRequest(
        application, removeParticipantRequest);

    cdhClient.updateApplication(updateApplicationRequest, emailAddress);
  }

  @Override
  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = { "ApplicationCache", "FetchApplicationDetailsCache"}, key = "#request.applicationGuid()")
  public void deleteApplicationCard(DeleteCardRequest request, JwtTokenClaims jwtTokenClaims,
      String clientIp) {
    validateApplicationAccess(request.applicationId(), request.applicationGuid(),
        jwtTokenClaims);

    var cdhApplication = cdhClient.fetchApplication(
        request.applicationId(),
        request.applicationGuid(), jwtTokenClaims.getEmail());

    if (cdhApplication == null) {
      throw new CdhNotFoundException(ErrorCode.CDH_APPLICATION_NOT_FOUND_ERROR, String
          .format(APPLICATION_NOT_FOUND_IN_CDH,
              request.applicationId(),
              request.applicationGuid()));
    }

    var updatedCardHolders = filterOutDeletedCard(cdhApplication, request.cardGuid());

    if (Objects.nonNull(updatedCardHolders)) {
      updateCardHoldersInCdh(request, updatedCardHolders, jwtTokenClaims);
    }

    worldlineClient.deleteApplicationCard(
        request.applicationGuid(),
        request.cardGuid(),
        buildWorldlineHeadersDto(getSchemeOrDefault(cdhApplication.getScheme()), clientIp));
  }

  @Override
  public GetAppCardsResponse getApplicationCards(GetAppCardsRequest getAppCardsRequest,
      String clientIp) {

    var wlHeaders = buildWorldlineHeadersDto(getAppCardsRequest.getScheme(), clientIp);

    var worldlineResponse = worldlineClient.getAppCards(
        getAppCardsRequest.getApplicationGuid(),
        getAppCardsRequest.getPage(),
        getAppCardsRequest.getMaxDisplayRows(),
        wlHeaders);

    return getApplicationCardsResponseMapper.toModel(worldlineResponse);
  }

  @Override
  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = {"ApplicationCache",
          "FetchApplicationDetailsCache"}, key = "#submitApplicationRequest.getApplicationGuid()")
  public SubmitApplicationResponse submitApplication(
      SubmitApplicationRequest submitApplicationRequest, String clientIp) {

    log.info("Submitting application with applicationID: {} and applicationGUID: {}",
        submitApplicationRequest.getApplicationId(),
        submitApplicationRequest.getApplicationGuid());

    var wlHeaders = buildWorldlineHeadersDto(submitApplicationRequest.getScheme(), clientIp);
    var submitApplicationRequestDetails = submitApplicationRequestMapper
        .toModel(submitApplicationRequest);

    var emailAddress = authenticatedUserService.getCurrentUserAccount()
        .map(Account::getEmail)
        .orElse(INNB_WB_EMAIL);

    if (Scheme.GB.equals(submitApplicationRequest.getScheme())) {
      submitApplicationRequestDetails.setHotelBookingRole(HOTEL_BOOKING_ROLE);
    }

    var submitApplicationResponseDto =
        worldlineClient.submitApplication(submitApplicationRequestDetails, wlHeaders);

    if (!OK_RESPONSE_STATUS.equals(submitApplicationResponseDto.getResponseCode())) {
      throw new WorldlineResponseException(ErrorCode.WORLDLINE_APP_SUBMIT_ERROR,
          String.format("Worldline submit application error: %s",
              submitApplicationResponseDto.getErrors()));
    }
    var updateApplicationStatus = UpdateApplicationStatusRequest.builder()
        .applicationId(submitApplicationRequest.getApplicationId())
        .stage("Outstanding")
        .build();
    cdhClient.updateApplicationStatus(updateApplicationStatus, emailAddress);

    return submitApplicationResponseMapper.toModel(submitApplicationResponseDto);
  }

  @Override
  public AppPreCheckResponse appPreCheck(AppPreCheckRequest appPreCheckRequest, String clientIp) {
    var wlHeaders = buildWorldlineHeadersDto(appPreCheckRequest.scheme(), clientIp);

    var wlAppPreCheckResponseDto =
        worldlineClient.appPreCheck(new WorldlineAppPreCheckRequestDto(appPreCheckRequest.email()), wlHeaders);
    return appPreCheckResponseMapper.toModel(wlAppPreCheckResponseDto);
  }

  @Override
  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = {"ApplicationCache",
          "FetchApplicationDetailsCache"}, key = "#directDebitRequest.applicationGuid()")
  public DirectDebitResponse directDebit(DirectDebitRequest directDebitRequest, String clientIp) {

    var emailAddress = authenticatedUserService.getCurrentUserAccount()
        .map(Account::getEmail)
        .orElse(INNB_WB_EMAIL);

    var cdhApplication = cdhClient.fetchApplication(
        directDebitRequest.applicationId(),
        directDebitRequest.applicationGuid(), emailAddress);

    if (cdhApplication == null) {
      throw new CdhNotFoundException(ErrorCode.CDH_APPLICATION_NOT_FOUND_ERROR, String
          .format(APPLICATION_NOT_FOUND_IN_CDH,
              directDebitRequest.applicationId(),
              directDebitRequest.applicationGuid()));
    }

    if (directDebitRequest.directDebitOption().equals(DirectDebitOption.BY_POST)) {
      var updateApplicationRequest = generateCdhDirectDebitRequest(
          cdhApplication, directDebitRequest, cdhApplication.getHostedPageGuid());

      log.info("Updating CDH application with applicationID: {}, applicationGUID: {}, directDebitOption: {}",
          directDebitRequest.applicationId(),
          directDebitRequest.applicationGuid(),
          directDebitRequest.directDebitOption());
      cdhClient.updateApplication(updateApplicationRequest, emailAddress);

      return DirectDebitResponse.builder().build();
    }

    // DirectDebitOption is: DIRECT
    if (cdhApplication.getHostedPageGuid() != null) {
      log.info("Hosted page already exists for applicationGUID: {}",
          directDebitRequest.applicationGuid());

      // Return the existing hosted page GUID and update CDH
      cdhClient.updateApplication(generateCdhDirectDebitRequest(
          cdhApplication, directDebitRequest, cdhApplication.getHostedPageGuid()), emailAddress);

      return DirectDebitResponse.builder()
          .hostedPageGuid(cdhApplication.getHostedPageGuid())
          .build();
    }

    // DirectDebitOption is: DIRECT and no previous hosted page exists. Call WorldLine to create a new one.
    log.info("Calling WorldLine hostedPageAppInit with applicationGuid: {}",
        directDebitRequest.applicationGuid());
    String hostedPageGuid;
    // if hostedPageGuid fails to be created, we still need to update CDH with the direct debit
    // to know that the user has chosen this option
    try {
      var worldlineResponse = worldlineClient.hostedPageAppInit(
          directDebitRequest.applicationGuid(),
          buildWorldlineHeadersDto(getSchemeOrDefault(cdhApplication.getScheme()), clientIp)
      );
      hostedPageGuid = Optional.ofNullable(worldlineResponse)
          .map(WLHostedPageAppInitResponseDto::getData)
          .map(WLHostedPageAppInitDto::getHostedPageGuid)
          .orElseThrow(() -> new WorldlineResponseException(ErrorCode.WORLDLINE_APP_SUBMIT_ERROR,
              "HostedPageGuid not returned"));
    } catch (WorldlineResponseException e) {
      log.error(
          "Update the cdh application with direct debit applicationGuid: {} applicationID : {} "
              + "because hostedPageGuid creation failed",
          directDebitRequest.applicationGuid(),
          directDebitRequest.applicationId());
      cdhClient.updateApplication(
          generateCdhDirectDebitRequest(cdhApplication, directDebitRequest, null), emailAddress);
      throw e;
    }

    // Update CDH with the new hosted page GUID
    cdhClient.updateApplication(generateCdhDirectDebitRequest(
        cdhApplication, directDebitRequest, hostedPageGuid), emailAddress);

    return DirectDebitResponse.builder()
        .hostedPageGuid(hostedPageGuid)
        .build();
  }

  @Override
  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = {"ApplicationCache",
          "FetchApplicationDetailsCache"}, key = "#updateResumeUrlRequest.applicationGuid()")
  public UpdateResumeUrlResponse updateResumeUrl(UpdateResumeUrlRequest updateResumeUrlRequest) {
    log.info("Updating resume URL for application with applicationID: {} and applicationGUID: {}",
        updateResumeUrlRequest.applicationId(),
        updateResumeUrlRequest.applicationGuid());

    var emailAddress = authenticatedUserService.getCurrentUserAccount()
        .map(Account::getEmail)
        .orElse(INNB_WB_EMAIL);
    var application = Optional.ofNullable(
            cdhClient.fetchApplication(updateResumeUrlRequest.applicationId(),
                updateResumeUrlRequest.applicationGuid(), emailAddress))
        .orElseThrow(
            () -> new CdhNotFoundException(ErrorCode.CDH_APPLICATION_NOT_FOUND_ERROR, String
                .format(APPLICATION_NOT_FOUND_IN_CDH,
                    updateResumeUrlRequest.applicationId(),
                    updateResumeUrlRequest.applicationGuid())));

    var updateApplicationRequest = generateCdhUpdateApplicationRequest(application,
        updateResumeUrlRequest.resumeUrl());
    var response = cdhClient.updateApplication(updateApplicationRequest, emailAddress);

    return UpdateResumeUrlResponse.builder()
        .status(response.getStatus())
        .message(response.getMessage())
        .build();
  }

  @Override
  public GetDdSepaFormStatusResponse getDdSepaFormStatus(
      GetDdSepaFormStatusRequest getDdSepaFormStatusRequest, String clientIp) {
    var wlHeaders = buildWorldlineHeadersDto(getDdSepaFormStatusRequest.scheme(), clientIp);

    var wlBankDetailsStatusResponseDto = worldlineClient.bankDetailsStatus(
        getDdSepaFormStatusRequest.hostedPageGuid(), wlHeaders);

    return GetDdSepaFormStatusResponse.builder()
        .status(wlBankDetailsStatusResponseDto.data().status())
        .build();
  }

  private List<CardHolder> filterOutDeletedCard(ApplicationResponse applicationResponse,
      String cardGuid) {
    if (applicationResponse.getCardHolders() != null) {
      return applicationResponse.getCardHolders().stream()
          .filter(cardHolder -> !cardHolder.getUserGuid().equals(cardGuid))
          .toList();
    }
    return applicationResponse.getCardHolders();
  }

  private void updateCardHoldersInCdh(DeleteCardRequest request,
      List<CardHolder> updatedCardHolders, JwtTokenClaims jwtTokenClaims) {
    var updateApplicationCardHolders = UpdateApplicationCardHoldersRequest.builder()
        .applicationId(request.applicationId())
        .cardHolders(updatedCardHolders)
        .build();

    cdhClient.updateApplicationCardHolders(updateApplicationCardHolders, jwtTokenClaims.getEmail());
  }

  private void validateApplicationAccess(String applicationId, String applicationGuid,
      JwtTokenClaims jwtTokenClaims) {
    cdhClient.fetchApplicationsByUser(jwtTokenClaims).stream()
        .filter(application -> application.getApplicationId().equals(applicationId)
            && application.getApplicationGuid().equals(applicationGuid))
        .findFirst()
        .orElseThrow(() -> {
          var ex = new ApplicationAccessException(ErrorCode.APPLICATION_ACCESS_DENIED,
              String.format(
                  "User does not have access to application with applicationId = %s and applicationGuid = %s",
                  applicationId, applicationGuid));
          ExceptionLogger.log(log, ex);
          return ex;
        });
  }

  private void validateApplicationAccess(ApplicationResponse cdhResponse, String participantId) {
    var hasAccess = Optional.ofNullable(cdhResponse)
        .map(ApplicationResponse::getParticipants)
        .stream()
        .flatMap(List::stream)
        .anyMatch(participant -> participantId.equals(String.valueOf(participant.getParticipantId())));
    var applicationId = Optional.ofNullable(cdhResponse)
        .map(ApplicationResponse::getApplicationId)
        .orElse("null");
    var applicationGuid = Optional.ofNullable(cdhResponse)
        .map(ApplicationResponse::getApplicationGuid)
        .orElse("null");

    if (!hasAccess) {
      throw new ApplicationAccessException(ErrorCode.APPLICATION_ACCESS_DENIED,
          String.format(
              "User does not have access to application with applicationId = %s and applicationGuid = %s",
              applicationId, applicationGuid));
    }
  }

  private WorldlineHeadersDto buildWorldlineHeadersDto(Scheme scheme, String clientIp) {
    var worldlinePropertiesByScheme = getWorldlinePropertiesByScheme(scheme);

    return WorldlineHeadersDto.builder()
        .companyNumber(worldlinePropertiesByScheme.getCompanyNumber())
        .cultureCode(worldlinePropertiesByScheme.getCultureCode())
        .ipAddress(clientIp != null ? clientIp : worldlineProperties.getDefaultIpAddress())
        .trustedPartnerCredentialsDto(TrustedPartnerCredentialsDto.builder()
            .password(worldlinePropertiesByScheme.getPassword())
            .username(worldlinePropertiesByScheme.getUsername())
            .build())
        .build();
  }

  PropertiesByLocation getWorldlinePropertiesByScheme(Scheme scheme) {
    if (scheme == Scheme.DE) {
      return worldlineProperties.getDe();
    } else {
      return worldlineProperties.getGb();
    }
  }

  private StartApplicationRequest generateCdhStartApplicationRequest(
      InitializeApplicationRequest initializeApplicationRequest, String companyName,
      WLAppInitResponseDto wlAppInitResponseDto, Account userAccount) {

    return StartApplicationRequest.builder()
        .applicationId(wlAppInitResponseDto.getData().getApplicationNumber())
        .applicationNumber(wlAppInitResponseDto.getData().getApplicationNumber())
        .applicationGuid(wlAppInitResponseDto.getData().getApplicationGUID())
        .startedDate(Instant.now().toString())
        .companyId(Integer.parseInt(userAccount.getBartId()))
        .accountName(companyName)
        .scheme(initializeApplicationRequest.getScheme().toString())
        .stage(INCOMPLETE_APPLICATION)
        .participants(Participants.builder()
            .initiator(true)
            .participantId(Integer.parseInt(userAccount.getBartEmployeeId()))
            .delegated(false)
            .terms(false)
            .directdebit(false)
            .email(initializeApplicationRequest.getEmail())
            .shared(Instant.now().toString())
            .name(TEMPORARY_NAME)
            .build())
        .build();
  }

  private Scheme getSchemeOrDefault(String scheme) {
    try {
      return Scheme.valueOf(scheme);
    } catch (IllegalArgumentException | NullPointerException e) {
      return Scheme.GB;
    }
  }

  private UpdateApplicationStatusRequest generateDeleteAppCdhRequest(
      DeletePayApplicationRequest deletePayApplicationRequest) {

    return UpdateApplicationStatusRequest.builder()
        .applicationId(deletePayApplicationRequest.getApplicationId())
        .stage(STATUS_APP_DELETE)
        .build();
  }

  private UpdateApplicationRequest generateCdhUpdateApplicationRequest(
      ApplicationResponse applicationToUpdate,
      UpdateAppContactDetailsRequest updateAppContactDetailsRequest,
      JwtTokenClaims jwtTokenClaims) {

    var request = updateApplicationRequestMapper.toModel(applicationToUpdate);
    request.setResumeUrl(updateAppContactDetailsRequest.getResumeUrl());
    request.setUpdateDate(Instant.now().toString());

    request.getParticipants().stream()
        .filter(participant -> participant.getParticipantId() == Integer.parseInt(
            jwtTokenClaims.getEmployeeId()))
        .findFirst()
        .ifPresent(participant -> {
          participant.setEmail(updateAppContactDetailsRequest.getEmail());
          participant.setName(String.format("%s %s",
              updateAppContactDetailsRequest.getForeName(),
              updateAppContactDetailsRequest.getLastName()));
        });

    return request;
  }

  private UpdateApplicationRequest generateCdhUpdateApplicationRequest(
      ApplicationResponse applicationToUpdate,
      String resumeUrl) {

    UpdateApplicationRequest request = updateApplicationRequestMapper.toModel(applicationToUpdate);
    request.setResumeUrl(resumeUrl);
    request.setUpdateDate(Instant.now().toString());
    return request;
  }

  private UpdateApplicationRequest generateCdhShareApplicationRequest(
      ApplicationResponse applicationToShare,
      ShareAppRequest shareAppRequest) {

    var request = updateApplicationRequestMapper.toModel(applicationToShare);
    request.setUpdateDate(Instant.now().toString());

    request.getParticipants().add(
        Participants.builder()
            .initiator(false)
            .participantId(shareAppRequest.getEmployeeId())
            .delegated(false)
            .terms(false)
            .directdebit(false)
            .email(shareAppRequest.getEmail())
            .shared(Instant.now().toString())
            .name(shareAppRequest.getFullName())
        .build());

    return request;
  }

  private UpdateApplicationRequest generateCdhRemoveParticipantRequest(
      ApplicationResponse applicationToUpdate,
      RemoveParticipantRequest removeParticipantRequest) {

    var request = updateApplicationRequestMapper.toModel(applicationToUpdate);
    request.setUpdateDate(Instant.now().toString());
    request.getParticipants()
        .removeIf(participant -> participant.getParticipantId() == removeParticipantRequest
            .employeeId());

    return request;
  }

  private UpdateApplicationRequest generateCdhDirectDebitRequest(
      ApplicationResponse applicationToUpdate,
      DirectDebitRequest directDebitRequest,
      String hostedPageGuid) {

    var request = updateApplicationRequestMapper.toModel(applicationToUpdate);
    request.setResumeUrl(directDebitRequest.resumeUrl());
    request.setDirectDebitOption(directDebitRequest.directDebitOption().toString());
    request.setHostedPageGuid(hostedPageGuid);
    request.setUpdateDate(Instant.now().toString());

    return request;
  }

  private void processListApplicationsEviction(GetEmployeeResponse employee) {
    Cache cache = cacheManager1Hour.getCache("ListApplicationsCache");
    if (cache != null && employee != null
        && Objects.nonNull(employee.getGlobalCompanyId())
        && Objects.nonNull(employee.getBartEmployeeId())
        && Objects.nonNull(employee.getEmailAddress())) {
      String cacheKey = employee.getGlobalCompanyId() + ":" + employee.getBartEmployeeId() + ":"
          + employee.getEmailAddress();
      log.info("Evicting cache for key: {}", cacheKey);
      cache.evict(cacheKey);
    }
  }

}
