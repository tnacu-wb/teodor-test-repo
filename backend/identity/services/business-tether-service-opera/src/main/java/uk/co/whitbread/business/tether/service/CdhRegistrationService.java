package uk.co.whitbread.business.tether.service;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.business.tether.model.Scheme;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.TetheredGuid;
import uk.co.whitbread.shared.cdh.model.TetheredUserRequest;

@Service
@Slf4j
@AllArgsConstructor
public class CdhRegistrationService {

  private final EmployeeDataService employeeDataService;

  private static final String ACCESS_CONTEXT = "InnBusiness";

  public void registerTetheredGuids(String tetherGuid, EmployeeDetails employeeDetails, Scheme scheme, String accessedBy) {
    log.info("Save in CDH tethered user: tetherGuid {}, companyId {}, employeeId {}", tetherGuid,
        employeeDetails.getCompanyId(), employeeDetails.getEmployeeId());
    employeeDataService.registerTetheredUser(
        TetheredUserRequest.builder()
            .companyId(employeeDetails.getCompanyId())
            .scheme(scheme.toString())
            .tetheredGuids(List.of(
                new TetheredGuid(
                    employeeDetails.getEmployeeId(), tetherGuid)
            ))
            .build(),
        accessedBy,
        ACCESS_CONTEXT);
  }


}
