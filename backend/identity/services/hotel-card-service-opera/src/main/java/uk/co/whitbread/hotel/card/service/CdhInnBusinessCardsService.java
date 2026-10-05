package uk.co.whitbread.hotel.card.service;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.card.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.hotel.card.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.card.model.feature.UnleashWrapper;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class CdhInnBusinessCardsService {

  public static final String EMPLOYEE_NOT_FOUND = "Employee %s from company %s was not found";

  private final EmployeeDataService employeeDataService;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  public GetEmployeeResponse getEmployee(String companyAccountId, String employeeAccountId, String userEmail) {
    var optionalGetCustomerResponse = getEmployeeFromCdh(companyAccountId, employeeAccountId, userEmail);

    return optionalGetCustomerResponse
        .filter( employee -> Objects.nonNull(employee.getBartEmployeeId()))
        .orElseThrow(
        () -> new EmployeeNotFoundException(String
            .format(EMPLOYEE_NOT_FOUND,
                employeeAccountId, companyAccountId)));
  }

  private Optional<GetEmployeeResponse> getEmployeeFromCdh(String companyAccountId,
      String employeeAccountId, String accessedBy) {
    if (isCdhApiDeprecationEnabled()) {
      return employeeDataService.getEmployeeV2(companyAccountId, employeeAccountId, accessedBy);
    }
    return employeeDataService.getEmployee(companyAccountId, employeeAccountId, accessedBy);
  }

  private boolean isCdhApiDeprecationEnabled() {
    return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation());
  }

}
