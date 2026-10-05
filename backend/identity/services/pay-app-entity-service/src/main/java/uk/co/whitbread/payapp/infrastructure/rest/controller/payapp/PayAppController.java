package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp;

import static uk.co.whitbread.payapp.infrastructure.util.Utils.sanitizeInputString;
import static uk.co.whitbread.payapp.infrastructure.util.Utils.validateEmailWithToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.payapp.domain.model.in.Scheme;
import uk.co.whitbread.payapp.domain.ports.primary.PayAppInPort;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.mapper.PayApplicationRequestDtoMapper;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.mapper.PayApplicationResponseDtoMapper;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.AddApplicationCardRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.AppCompanyDetailsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.ApplicationDetailsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.DeleteCardRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.DeletePayApplicationRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.DirectDebitRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.GetAppCardsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.GetAppLookupRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.GetDdSepaFormStatusRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.GetUserPreferencesRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.InitializeApplicationRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.RemoveParticipantRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.ShareAppRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.SubmitApplicationRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.UpdateAppCompanyDetailsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.UpdateAppContactDetailsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.UpdateResumeUrlRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.AddApplicationCardResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.AppCompanyDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.AppPreCheckResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.ApplicationDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.DeletePayApplicationResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.DirectDebitResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.FetchApplicationsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.GetAppCardsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.GetDdSepaFormStatusResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.GetUserPreferencesResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.InitializeApplicationResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.ShareAppResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.SubmitApplicationResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.UpdateAppCompanyDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.UpdateAppContactDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.UpdateResumeUrlResponseDto;
import uk.co.whitbread.payapp.infrastructure.security.JwtUtils;
import uk.co.whitbread.payapp.infrastructure.util.Utils;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

@RequestMapping("/v1/pay-app")
@RestController
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("java:S6539")
public class PayAppController implements PayAppApi {


  private final PayApplicationRequestDtoMapper payApplicationRequestDtoMapper;
  private final PayAppInPort payAppInPort;
  private final PayApplicationResponseDtoMapper payApplicationResponseDtoMapper;
  private final JwtUtils jwtUtils;
  private final Utils utils;
  private final AuthenticatedUserService authenticatedUserService;

  @Override
  @PostMapping(value = "/application/initialize", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InitializeApplicationResponseDto> initializeApplication(
      @Valid @RequestBody InitializeApplicationRequestDto initializeApplicationRequestDto,
      HttpServletRequest httpServletRequest) {

    log.debug("Called POST /application/initialize with email={}",
        sanitizeInputString(initializeApplicationRequestDto.getEmail()));

    var emailFromToken = authenticatedUserService.getCurrentUserAccount().map(Account::getEmail)
        .orElse("notauth@email.com");
    validateEmailWithToken(initializeApplicationRequestDto.getEmail(), emailFromToken);

    String clientIp = utils.getClientIp(httpServletRequest);

    var initializeApplicationRequest = payApplicationRequestDtoMapper.toModel(
        initializeApplicationRequestDto);

    var initializeApplicationDto = payApplicationResponseDtoMapper.toDto(
        payAppInPort.initializeApplication(initializeApplicationRequest, clientIp));

    return ResponseEntity.status(HttpStatus.OK).body(initializeApplicationDto);
  }

  @Override
  @GetMapping(value = "/applications", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FetchApplicationsResponseDto> fetchPayApplications(
      HttpServletRequest httpServletRequest) {
    var jwtTokenClaims = jwtUtils.parseToken();
    String clientIp = utils.getClientIp(httpServletRequest);

    var applicationsDetailsDto = payApplicationResponseDtoMapper.toDto(
        payAppInPort.fetchApplicationsDetails(jwtTokenClaims, clientIp));

    return ResponseEntity.status(HttpStatus.OK).body(applicationsDetailsDto);
  }

  @Override
  @GetMapping(value = "/application/lookup", produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, List<String>> getAppLookup(
      @Valid @ParameterObject GetAppLookupRequestDto getAppLookupRequestDto,
      HttpServletRequest httpServletRequest) {

    log.debug("Called GET /appLookup with scheme={}, lookupNames={}",
        sanitizeInputString(getAppLookupRequestDto.getScheme().name()),
        sanitizeInputString(getAppLookupRequestDto.getLookupNames().toString()));

    String clientIp = utils.getClientIp(httpServletRequest);

    var request = payApplicationRequestDtoMapper.toModel(getAppLookupRequestDto);

    return payAppInPort.getAppLookup(request, clientIp);
  }

  @Override
  @PostMapping(value = "/application/contactDetails", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UpdateAppContactDetailsResponseDto> updateAppContactDetails(
      @Valid @RequestBody UpdateAppContactDetailsRequestDto updateAppContactDetailsRequestDto,
      HttpServletRequest httpServletRequest) {

    log.debug(
        "Called POST /application/contactDetails with applicationGuid={}, applicationId={}, email={}, scheme={}",
        sanitizeInputString(updateAppContactDetailsRequestDto.getApplicationGuid()),
        sanitizeInputString(updateAppContactDetailsRequestDto.getApplicationId()),
        sanitizeInputString(updateAppContactDetailsRequestDto.getEmail()),
        sanitizeInputString(updateAppContactDetailsRequestDto.getScheme().name()));
    var jwtTokenClaims = jwtUtils.parseToken();
    String clientIp = utils.getClientIp(httpServletRequest);

    var updateAppContactDetailsRequest = payApplicationRequestDtoMapper.toModel(
        updateAppContactDetailsRequestDto);

    var updateAppContactDetailsDto = payApplicationResponseDtoMapper.toDto(
        payAppInPort.updateAppContactDetails(updateAppContactDetailsRequest, jwtTokenClaims,
            clientIp));

    return ResponseEntity.status(HttpStatus.OK).body(updateAppContactDetailsDto);
  }

  @Override
  @PostMapping(value = "/application/companyDetails", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UpdateAppCompanyDetailsResponseDto> updateAppCompanyDetails(
      @Valid @RequestBody UpdateAppCompanyDetailsRequestDto updateAppCompanyDetailsRequestDto,
      HttpServletRequest httpServletRequest) {

    log.debug("Called POST /application/companyDetails with applicationGuid={}, applicationId={}, "
            + "companyName={}, companyType={}, scheme={}",
        sanitizeInputString(updateAppCompanyDetailsRequestDto.getApplicationGuid()),
        sanitizeInputString(updateAppCompanyDetailsRequestDto.getApplicationId()),
        sanitizeInputString(
            updateAppCompanyDetailsRequestDto.getAppCompanyDetailsDto().getCompanyName()),
        sanitizeInputString(
            updateAppCompanyDetailsRequestDto.getAppCompanyDetailsDto().getCompanyType()),
        sanitizeInputString(updateAppCompanyDetailsRequestDto.getScheme().name()));

    String clientIp = utils.getClientIp(httpServletRequest);

    var updateAppCompanyDetailsRequest = payApplicationRequestDtoMapper.toModel(
        updateAppCompanyDetailsRequestDto);

    var updateAppCompanyDetailsDto = payApplicationResponseDtoMapper.toDto(
        payAppInPort.updateAppCompanyDetails(updateAppCompanyDetailsRequest, clientIp));

    return ResponseEntity.status(HttpStatus.OK).body(updateAppCompanyDetailsDto);
  }

  @Override
  @PostMapping(value = "/application/delete", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DeletePayApplicationResponseDto> deleteApplication(
      @Valid @RequestBody DeletePayApplicationRequestDto requestDto,
      HttpServletRequest httpServletRequest) {
    log.debug("Called POST /application/delete with applicationId={} and applicationGuid={}",
        sanitizeInputString(requestDto.getApplicationId()),
        sanitizeInputString(requestDto.getApplicationGuid()));

    var request = payApplicationRequestDtoMapper.toModel(sanitizeRequestDto(requestDto));
    var jwtTokenClaims = jwtUtils.parseToken();
    String clientIp = utils.getClientIp(httpServletRequest);

    var deleteApplicationDto = payApplicationResponseDtoMapper.toDto(
        payAppInPort.deleteApplication(request, jwtTokenClaims, clientIp));

    return ResponseEntity.status(HttpStatus.OK).body(deleteApplicationDto);
  }

  @Override
  @GetMapping(value = "/application/companyDetailsLookup", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AppCompanyDetailsResponseDto> lookupCompanyDetails(
      @Valid @ModelAttribute AppCompanyDetailsRequestDto appCompanyDetailsRequestDto,
      HttpServletRequest httpServletRequest) {

    log.debug(
        "Called GET /application/companyDetailsLookup with companyRegistrationNumber={} and scheme={}",
        sanitizeInputString(appCompanyDetailsRequestDto.getCompanyRegistrationNumber()),
        sanitizeInputString(appCompanyDetailsRequestDto.getScheme().name()));

    var clientIp = utils.getClientIp(httpServletRequest);

    var companyDetailsRequest = payApplicationRequestDtoMapper.toModel(appCompanyDetailsRequestDto);
    var companyDetailsDto = payApplicationResponseDtoMapper.toDto(
        payAppInPort.lookupCompanyDetails(companyDetailsRequest, clientIp));

    return ResponseEntity.status(HttpStatus.OK).body(companyDetailsDto);
  }

  @Override
  @GetMapping(value = "/user/preferences", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<GetUserPreferencesResponseDto>> getUserPreferences(
      @Valid @ParameterObject GetUserPreferencesRequestDto userPreferencesRequestDto,
      HttpServletRequest httpServletRequest) {

    log.debug("Called GET /user/preferences for tetheredUserGuids={}",
        sanitizeInputString(userPreferencesRequestDto.getTetheredUserGuids().toString()));

    var jwtTokenClaims = jwtUtils.parseToken();
    var clientIp = utils.getClientIp(httpServletRequest);
    var userPreferences = payAppInPort
        .getUserPreferences(payApplicationRequestDtoMapper.toModel(userPreferencesRequestDto),
            jwtTokenClaims, clientIp);
    return ResponseEntity.status(HttpStatus.OK)
        .body(payApplicationResponseDtoMapper.toDto(userPreferences));
  }

  @Override
  @GetMapping(value = "/application/details", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApplicationDetailsResponseDto> getApplicationDetails(
      @Valid @ParameterObject ApplicationDetailsRequestDto applicationDetailsRequestDto,
      HttpServletRequest httpServletRequest) {
    log.debug("Called GET /application/details with applicationId={} and applicationGuid={}",
        sanitizeInputString(applicationDetailsRequestDto.getApplicationId()),
        sanitizeInputString(applicationDetailsRequestDto.getApplicationGuid()));

    var jwtTokenClaims = jwtUtils.parseToken();
    var clientIp = utils.getClientIp(httpServletRequest);
    var applicationDetailsRequest = payApplicationRequestDtoMapper.toModel(
        applicationDetailsRequestDto);
    var applicationDetailsResponse = payAppInPort.applicationDetails(applicationDetailsRequest,
        jwtTokenClaims, clientIp);

    return ResponseEntity.status(HttpStatus.OK)
        .body(payApplicationResponseDtoMapper.toDto(applicationDetailsResponse));
  }

  @PreAuthorize("isAuthenticated()")
  @Override
  @PostMapping(value = "/application/share", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ShareAppResponseDto> shareApp(
      @Valid @RequestBody ShareAppRequestDto shareAppRequestDto) {
    log.debug("Called POST /application/share with employeeId={} and email={}",
        shareAppRequestDto.getEmployeeId(),
        sanitizeInputString(shareAppRequestDto.getEmail()));

    var shareAppRequest = payApplicationRequestDtoMapper.toModel(shareAppRequestDto);
    var shareAppResponse = payApplicationResponseDtoMapper.toDto(
        payAppInPort.shareApp(shareAppRequest));

    return ResponseEntity.status(HttpStatus.OK).body(shareAppResponse);
  }

  @Override
  @PostMapping(value = "/application/removeParticipant", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> removeParticipant(
      @Valid @RequestBody RemoveParticipantRequestDto removeParticipantRequestDto) {
    log.debug("Called POST /application/removeParticipant with applicationId={} applicationGuid={} and employeeId={}",
        sanitizeInputString(removeParticipantRequestDto.applicationId()),
        sanitizeInputString(removeParticipantRequestDto.applicationGuid()),
        removeParticipantRequestDto.employeeId());

    var removeParticipantRequest = payApplicationRequestDtoMapper.toModel(
        removeParticipantRequestDto);

    payAppInPort.removeParticipant(removeParticipantRequest);

    return ResponseEntity.noContent().build();
  }

  @Override
  @PostMapping("/application/delete-card")
  public ResponseEntity<Void> deleteApplicationCard(
      @Valid @RequestBody DeleteCardRequestDto deleteCardRequestDto,
      HttpServletRequest httpServletRequest) {
    log.debug("Called POST /delete-card with applicationId={} and cardId={}",
        sanitizeInputString(deleteCardRequestDto.applicationGuid()),
        sanitizeInputString(deleteCardRequestDto.cardGuid()));

    var tokenClaims = jwtUtils.parseToken();
    var clientIp = utils.getClientIp(httpServletRequest);
    var deleteCardRequest = payApplicationRequestDtoMapper.toModel(
        deleteCardRequestDto);

    payAppInPort.deleteApplicationCard(deleteCardRequest, tokenClaims, clientIp);

    return ResponseEntity.noContent().build();
  }

  @Override
  @PostMapping(value = "/application/card", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<AddApplicationCardResponseDto> addApplicationCard(
      @Valid @RequestBody AddApplicationCardRequestDto addApplicationCardRequestDto,
      HttpServletRequest httpServletRequest) {

    log.debug("Called POST /application/card with applicationGuid={} and scheme={}",
        sanitizeInputString(addApplicationCardRequestDto.getApplicationGuid()),
        sanitizeInputString(addApplicationCardRequestDto.getScheme().name()));

    var clientIp = utils.getClientIp(httpServletRequest);
    var addApplicationCard = payApplicationRequestDtoMapper.toModel(
        addApplicationCardRequestDto);
    var addApplicationCardResponse = payAppInPort.addApplicationCard(
        addApplicationCard, clientIp);

    return ResponseEntity.ok(payApplicationResponseDtoMapper.toDto(addApplicationCardResponse));
  }

  @Override
  @GetMapping(value = "/application/cards", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GetAppCardsResponseDto> getApplicationCards(
      @Valid @ParameterObject GetAppCardsRequestDto getAppCardsRequestDto,
      HttpServletRequest httpServletRequest) {
    log.debug("Called GET /application/cards with applicationGuid={} and scheme={}",
        sanitizeInputString(getAppCardsRequestDto.getApplicationGuid()),
        sanitizeInputString(getAppCardsRequestDto.getScheme().name()));

    var clientIp = utils.getClientIp(httpServletRequest);
    var getAppCardsRequest = payApplicationRequestDtoMapper.toModel(
        getAppCardsRequestDto);
    var getAppCardsResponse = payApplicationResponseDtoMapper.toDto(
        payAppInPort.getApplicationCards(getAppCardsRequest, clientIp));

    return ResponseEntity.status(HttpStatus.OK).body(getAppCardsResponse);
  }

  @Override
  @PostMapping(value = "/application/submit", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<SubmitApplicationResponseDto> submitApplication(
      @Valid @RequestBody SubmitApplicationRequestDto submitApplicationRequestDto,
      HttpServletRequest httpServletRequest) {
    log.debug("Called POST /application/submit with applicationGuid={}",
        sanitizeInputString(submitApplicationRequestDto.getApplicationGuid()));

    var clientIp = utils.getClientIp(httpServletRequest);
    var submitApplicationRequest = payApplicationRequestDtoMapper.toModel(
        submitApplicationRequestDto);
    var submitApplicationResponse = payApplicationResponseDtoMapper.toDto(
        payAppInPort.submitApplication(submitApplicationRequest, clientIp));

    return ResponseEntity.ok(submitApplicationResponse);
  }

  @Override
  @GetMapping(value = "/application/pre-check", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AppPreCheckResponseDto> appPreCheck(
      @RequestParam Scheme scheme,
      HttpServletRequest httpServletRequest) {

    var jwtTokenClaims = jwtUtils.parseToken();
    log.debug("Called GET /application/pre-check for email={}", jwtTokenClaims.getEmail());

    String clientIp = utils.getClientIp(httpServletRequest);
    var appPreCheckResponse = payAppInPort
        .appPreCheck(payApplicationRequestDtoMapper.toModel(scheme, jwtTokenClaims.getEmail()), clientIp);

    return ResponseEntity.status(HttpStatus.OK)
        .body(payApplicationResponseDtoMapper.toDto(appPreCheckResponse));
  }

  @Override
  @PostMapping(value = "/application/directDebit", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DirectDebitResponseDto> directDebit(
      @Valid @RequestBody DirectDebitRequestDto directDebitRequestDto,
      HttpServletRequest httpServletRequest) {
    log.debug(
        "Called POST /application/directDebit with applicationGuid={}, applicationId={}, directDebitOption={}",
        sanitizeInputString(directDebitRequestDto.applicationGuid()),
        sanitizeInputString(directDebitRequestDto.applicationId()),
        directDebitRequestDto.directDebitOption().toString());

    var clientIp = utils.getClientIp(httpServletRequest);
    var directDebitRequest = payApplicationRequestDtoMapper.toModel(
        directDebitRequestDto);
    var directDebitResponse = payApplicationResponseDtoMapper.toDto(
        payAppInPort.directDebit(directDebitRequest, clientIp));

    return ResponseEntity.status(HttpStatus.OK).body(directDebitResponse);
  }

  @Override
  @PostMapping(value = "/application/updateResumeUrl", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UpdateResumeUrlResponseDto> updateResumeUrl(
      @Valid @RequestBody UpdateResumeUrlRequestDto updateResumeUrlRequestDto,
      HttpServletRequest httpServletRequest) {
    log.debug("Called POST /application/updateResumeUrl with applicationId={}, applicationGuid={} "
            + "and resumeUrl={}",
        sanitizeInputString(updateResumeUrlRequestDto.applicationId()),
        sanitizeInputString(updateResumeUrlRequestDto.applicationGuid()),
        sanitizeInputString(updateResumeUrlRequestDto.resumeUrl()));

    var updateResumeUrlRequest = payApplicationRequestDtoMapper.toModel(
        updateResumeUrlRequestDto);
    var response = payAppInPort.updateResumeUrl(updateResumeUrlRequest);

    return ResponseEntity.ok(payApplicationResponseDtoMapper.toDto(response));
  }

  @Override
  @GetMapping(value = "/application/ddSepaFormStatus", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<GetDdSepaFormStatusResponseDto> getDdSepaFormStatus(
      @Valid @ParameterObject GetDdSepaFormStatusRequestDto getDdSepaFormStatusRequestDto,
      HttpServletRequest httpServletRequest) {
    log.debug("Called GET /application/ddSepaFormStatus with hostedPageGuid={} and scheme={}",
        sanitizeInputString(getDdSepaFormStatusRequestDto.hostedPageGuid()),
        getDdSepaFormStatusRequestDto.scheme());

    var clientIp = utils.getClientIp(httpServletRequest);

    var getDdSepaFormStatusRequest = payApplicationRequestDtoMapper.toModel(
        getDdSepaFormStatusRequestDto);
    var getDdSepaFormStatusResponse = payAppInPort.getDdSepaFormStatus(
        getDdSepaFormStatusRequest, clientIp);

    return ResponseEntity.status(HttpStatus.OK)
        .body(payApplicationResponseDtoMapper.toDto(getDdSepaFormStatusResponse));
  }

  private DeletePayApplicationRequestDto sanitizeRequestDto(
      DeletePayApplicationRequestDto requestDto) {
    return DeletePayApplicationRequestDto.builder()
        .applicationId(sanitizeInputString(requestDto.getApplicationId()))
        .applicationGuid(sanitizeInputString(requestDto.getApplicationGuid()))
        .scheme(requestDto.getScheme())
        .build();
  }

}
