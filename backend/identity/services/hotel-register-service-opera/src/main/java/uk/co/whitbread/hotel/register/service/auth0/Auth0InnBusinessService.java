package uk.co.whitbread.hotel.register.service.auth0;

import static uk.co.whitbread.hotel.register.utils.register.Auth0ManagementTransformer.EMPLOYEE_STATUS_LABEL;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.register.model.companyemployee.EmployeeStatus;
import uk.co.whitbread.hotel.register.utils.register.Auth0ManagementTransformer;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.model.Auth0User;
import uk.co.whitbread.shared.auth.service.ManagementService;

@Slf4j
@Service
public class Auth0InnBusinessService {

  public static final String COMPANY_ACCOUNT_ID_LABEL = "company_account_id";
  public static final String EMPLOYEE_ACCOUNT_ID_LABEL = "employee_account_id";

  private final ManagementService businessManagementService;
  private final Auth0ManagementTransformer auth0ManagementTransformer;

  public Auth0InnBusinessService(ManagementService businessManagementService,
      Auth0ManagementTransformer auth0ManagementTransformer) {
    this.businessManagementService = businessManagementService;
    this.auth0ManagementTransformer = auth0ManagementTransformer;
  }

  public void saveUserInAuth0(String email, String password, String companyAccountId,
      String employeeAccountId) {

    try {
      businessManagementService.checkUserNotAlreadyInAuth0(email);
    } catch (AuthServiceException _) {
      log.warn("User already exists in Auth0");
      return;
    }
    Auth0User auth0User = auth0ManagementTransformer.transform(email, password,
        Map.of(EMPLOYEE_STATUS_LABEL, EmployeeStatus.ACTIVE,
            COMPANY_ACCOUNT_ID_LABEL, companyAccountId,
            EMPLOYEE_ACCOUNT_ID_LABEL, employeeAccountId));
    auth0User.setConnection(businessManagementService.getManagementProperties().getConnection());

    log.debug("Creating user {} in Auth0", email);
    businessManagementService.createUser(auth0User);
  }

}
