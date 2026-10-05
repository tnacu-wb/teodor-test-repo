package uk.co.whitbread.payapp.infrastructure.rest.client.cdh;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payapp.ErrorCode;
import uk.co.whitbread.payapp.domain.model.in.jwt.JwtTokenClaims;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.exceptions.CdhResponseException;
import uk.co.whitbread.payapp.infrastructure.security.JwtUtils;
import uk.co.whitbread.shared.cdh.ApplicationDataService;
import uk.co.whitbread.shared.cdh.RegistrationDataService;
import uk.co.whitbread.shared.cdh.model.GetDashboardDetailsQueryParams;
import uk.co.whitbread.shared.cdh.model.PibaTetheredGuidResponse;
import uk.co.whitbread.shared.cdh.model.StartApplicationRequest;
import uk.co.whitbread.shared.cdh.model.StartApplicationResponse;
import uk.co.whitbread.shared.cdh.model.UpdateApplicationRequest;
import uk.co.whitbread.shared.cdh.model.UpdateApplicationResponse;
import uk.co.whitbread.shared.cdh.model.applications.UpdateApplicationStatusRequest;
import uk.co.whitbread.shared.cdh.model.applications.UpdateApplicationStatusResponse;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationResponse;
import uk.co.whitbread.shared.cdh.model.spending.application.UpdateApplicationCardHoldersRequest;

@Component
@Slf4j
@RequiredArgsConstructor
public class CdhClient {

  private static final String ACCESS_CONTEXT = "InnBusiness";

  private final ApplicationDataService applicationDataService;
  private final RegistrationDataService registrationDataService;
  private final CacheManager cacheManager1Hour;
  private final JwtUtils jwtUtils;

  public StartApplicationResponse startApplication(
      StartApplicationRequest startApplicationRequest) {
    log.debug("Entered CDH startApplication with applicationGUID = {}, companyId = {}, employeeId = {}, and email = {}",
        startApplicationRequest.getApplicationGuid(),
        startApplicationRequest.getCompanyId(),
        startApplicationRequest.getParticipants().getParticipantId(),
        startApplicationRequest.getParticipants().getEmail());

    return applicationDataService.startApplication(
        startApplicationRequest,
        startApplicationRequest.getParticipants().getEmail(),
        ACCESS_CONTEXT);
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "ListApplicationsCache",
      key = "#jwtTokenClaims.getCompanyId() + ':' + #jwtTokenClaims.getEmployeeId() + ':' + #jwtTokenClaims.getEmail()")
  public List<ApplicationResponse> fetchApplicationsByUser(JwtTokenClaims jwtTokenClaims) {
    log.debug("Entered CDH fetchApplicationsByUser with companyId = {}, participantId = {} and email = {}",
        jwtTokenClaims.getCompanyId(),
        jwtTokenClaims.getEmployeeId(),
        jwtTokenClaims.getEmail());
    return applicationDataService.fetchApplicationsByUser(
        jwtTokenClaims.getCompanyId(),
        jwtTokenClaims.getEmployeeId(),
        jwtTokenClaims.getEmail(),
        ACCESS_CONTEXT);
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "ApplicationCache", key = "#applicationGuid")
  public ApplicationResponse fetchApplication(String applicationId, String applicationGuid,
      String email) {
    log.debug(
        "Entered CDH fetchApplication with applicationId = {}, applicationGuid = {} and email = {}",
        applicationId, applicationGuid, email);
    var applicationList = applicationDataService
        .fetchApplication(applicationId, applicationGuid, email, ACCESS_CONTEXT);
    if (applicationList.isEmpty()) {
      return null;
    }
    return applicationList.get(0);
  }

  public UpdateApplicationResponse updateApplication(
      UpdateApplicationRequest updateApplicationRequest, String accessedBy) {
    log.debug("Entered CDH updateApplication with applicationID = {}",
        updateApplicationRequest.getApplicationId());
    processListApplicationsEviction(jwtUtils.parseToken());
    return applicationDataService.updateApplication(
        updateApplicationRequest,
        accessedBy,
        ACCESS_CONTEXT);
  }

  public UpdateApplicationStatusResponse updateApplicationStatus(
      UpdateApplicationStatusRequest updateApplicationStatusRequest, String email) {
    log.debug("Entered CDH updateApplicationStatus with applicationId = {}, status = {}",
        updateApplicationStatusRequest.getApplicationId(),
        updateApplicationStatusRequest.getStage());
    processListApplicationsEviction(jwtUtils.parseToken());
    try {
      return applicationDataService.updateApplicationStatus(
          updateApplicationStatusRequest,
          email,
          ACCESS_CONTEXT);
    } catch (Exception e) {
      log.error("Exception during CDH update status for application id = {}",
          updateApplicationStatusRequest.getApplicationId(), e);
      throw new CdhResponseException(ErrorCode.CDH_APP_CANCEL_ERROR,
          String.format("Failed to update application status in CDH for applicationId = %s",
              updateApplicationStatusRequest.getApplicationId()));
    }
  }

  public List<PibaTetheredGuidResponse> getTetheredGuids(String companyId, String employeeId, String email) {
    log.debug("Retrieve tethered guids from CDH request for company id {}, employee id {}", companyId, employeeId);

    return registrationDataService.getDashboardDetails(
        GetDashboardDetailsQueryParams.builder()
            .companyId(companyId)
            .employeeId(employeeId)
            .build(),
        email,
        ACCESS_CONTEXT);
  }

  public void updateApplicationCardHolders(UpdateApplicationCardHoldersRequest request, String email) {
    log.debug("Entered CDH updateApplicationCardHolders with applicationId = {}, cardHolderId = {}",
        request.getApplicationId(), request.getCardHolders().toString());
    processListApplicationsEviction(jwtUtils.parseToken());
    applicationDataService.updateApplicationCardHolders(request, email, ACCESS_CONTEXT);
  }

  private void processListApplicationsEviction(JwtTokenClaims jwtTokenClaims) {
    Cache cache = cacheManager1Hour.getCache("ListApplicationsCache");
    if (cache != null && jwtTokenClaims != null
        && Objects.nonNull(jwtTokenClaims.getCompanyId())
        && Objects.nonNull(jwtTokenClaims.getEmployeeId())
        && Objects.nonNull(jwtTokenClaims.getEmail())) {
      String cacheKey = jwtTokenClaims.getCompanyId() + ":" + jwtTokenClaims.getEmployeeId() + ":"
          + jwtTokenClaims.getEmail();
      log.info("Evicting cache for key: {}", cacheKey);
      cache.evict(cacheKey);
    }
  }
}
