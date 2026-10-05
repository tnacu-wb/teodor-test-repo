package uk.co.whitbread.company.employee.validation;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.company.employee.exceptions.InvalidOperationException;
import uk.co.whitbread.company.employee.exceptions.InvalidTokenException;
import uk.co.whitbread.company.employee.exceptions.RestrictedOperationException;
import uk.co.whitbread.company.employee.model.AccessLevel;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@Slf4j
@Component
public class InnbEmployeeValidator {

  private static final String SUPER_ACCESS_LEVEL = "SUPER";
  private static final String BUSINESS_PAY_MANAGER_ACCESS_LEVEL = "BUSINESS_PAY_MANAGER";
  private static final String INACTIVE_STATUS = "INACTIVE";

  public void validateTokenClaims(CdhEmployeeDetails travelManagerDetails) {
    if (StringUtils.isBlank(travelManagerDetails.getCompanyAccountId())
          || StringUtils.isBlank(travelManagerDetails.getEmployeeAccountId())
          || StringUtils.isBlank(travelManagerDetails.getUserEmail())) {
      log.error("The provided token is missing or invalid.");
      throw new InvalidTokenException("Invalid or missing token.");
    }
  }

  public void validateManagerAccessLevel(GetEmployeeResponse manager) {
    if (!manager.getAccessLevel().equalsIgnoreCase(SUPER_ACCESS_LEVEL) &&
        !manager.getAccessLevel().equals(BUSINESS_PAY_MANAGER_ACCESS_LEVEL)) {
      log.error("The provided token does not belong to a Manager (Id = {}, Company = {}).",
          manager.getEmployeeAccountId(), manager.getCompanyAccountId());
      throw new RestrictedOperationException("The provided token does not belong to a Manager.");
    }
  }

  public void validateEmployeeAccessLevelsBasedOnManagerAccessLevel(String managerAccessLevel,
      AccessLevel employeeAccessLevel) {
    Set<AccessLevel> validAccessLevels;

    if (managerAccessLevel.equalsIgnoreCase(SUPER_ACCESS_LEVEL)) {
      validAccessLevels = Set.of(
          AccessLevel.STAYER,
          AccessLevel.SELF,
          AccessLevel.BOOKER,
          AccessLevel.SUPER
      );
    } else if (managerAccessLevel.equalsIgnoreCase(BUSINESS_PAY_MANAGER_ACCESS_LEVEL)) {
      validAccessLevels = Set.of(
          AccessLevel.BUSINESS_PAY_MANAGER,
          AccessLevel.BUSINESS_PAY_USER
      );
    } else {
      validAccessLevels = Set.of();
    }

    if (!validAccessLevels.contains(employeeAccessLevel)) {
      throw new InvalidOperationException("The employee access level is not valid.");
    }
  }

  public void validateEmployeeSameCompanyWithManager(GetEmployeeResponse manager,
        GetEmployeeResponse cdhEmployee) {
    if (manager.getCompanyAccountId() == null || cdhEmployee.getCompanyAccountId() == null
          || !manager.getCompanyAccountId().equals(cdhEmployee.getCompanyAccountId())) {
      log.error("The employee (Id = {}, CompanyId = {}) does not belong to the same company"
                  + " (Id = {}) as the Manager (Id = {}).", cdhEmployee.getEmployeeAccountId(),
            cdhEmployee.getCompanyAccountId(), manager.getCompanyAccountId(),
            manager.getEmployeeAccountId());
      throw new InvalidOperationException("The employee does not belong to the same company as the"
            + " Manager.");
    }
  }

  public void validateUserAndManagerStatus(GetEmployeeResponse manager,
        GetEmployeeResponse cdhEmployee) {
    if ((cdhEmployee.getEmployeeStatus() != null
          && !cdhEmployee.getEmployeeStatus().equalsIgnoreCase(INACTIVE_STATUS))
          || !cdhEmployee.isAwaitingApproval()
          || StringUtils.isBlank(cdhEmployee.getActivationKey())
          || manager.getEmployeeStatus().equalsIgnoreCase(INACTIVE_STATUS)) {
      log.error("The employee (Id = {}, Status = {}) or the Manager (Id = {}, Status = {})"
                  + " is not in the correct state.", cdhEmployee.getEmployeeAccountId(),
            cdhEmployee.getEmployeeStatus(), manager.getEmployeeAccountId(),
            manager.getEmployeeStatus());
      throw new InvalidOperationException("The employee or the Manager is not in the correct"
            + " state.");
    }
  }
}
