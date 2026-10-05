package uk.co.whitbread.piba.registration.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.cdh.ApplicationDataService;
import uk.co.whitbread.shared.cdh.RegistrationDataService;
import uk.co.whitbread.shared.cdh.model.GetDashboardDetailsQueryParams;
import uk.co.whitbread.shared.cdh.model.PibaTetheredGuidResponse;
import uk.co.whitbread.shared.cdh.model.applications.UpdateApplicationStatusRequest;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationResponse;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CdhClient {

  private static final String ACCESS_CONTEXT = "InnBusiness";

  private final ApplicationDataService applicationDataService;
  private final RegistrationDataService registrationDataService;

  public ApplicationResponse fetchApplication(String applicationId, String applicationGuid, String email) {
    log.debug("Entered CDH fetchApplication with applicationId = {}, applicationGuid = {} and email = {}",
        applicationId, applicationGuid, email);
    var applicationList = applicationDataService
        .fetchApplication(applicationId, applicationGuid, email, ACCESS_CONTEXT);
    if (applicationList.isEmpty()) {
      return null;
    }
    return applicationList.get(0);
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

  public void updateAppStatus(String applicationId, String status, String userEmail) {
    log.debug("Update application status for application id {} to status {}", applicationId, status);

    var updateApplicationStatus = UpdateApplicationStatusRequest.builder()
        .applicationId(applicationId)
        .stage(status)
        .build();
    applicationDataService.updateApplicationStatus(updateApplicationStatus, userEmail, ACCESS_CONTEXT);
  }

}
