package uk.co.whitbread.company.cache;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.company.client.model.Customer;
import uk.co.whitbread.company.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.company.mapper.EmployeeMapper;
import uk.co.whitbread.company.model.feature.FeatureFlag;
import uk.co.whitbread.company.model.feature.UnleashWrapper;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerCacheProvider {

  private final EmployeeDataService employeeDataService;
  private final EmployeeMapper employeeMapper;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  public Customer getCdhEmployee(String companyAccountId, String employeeAccountId,
      String userEmail) {
    log.debug("Retrieving employee from CDH using company id {} employee id {} (not cached)",
        companyAccountId, employeeAccountId);
    Optional<GetEmployeeResponse> responseOptional = isCdhApiDeprecationEnabled()
        ? employeeDataService.getEmployeeV2(companyAccountId, employeeAccountId, userEmail)
        : employeeDataService.getEmployee(companyAccountId, employeeAccountId, userEmail);
    if (responseOptional.isEmpty()) {
      throw new EmployeeNotFoundException(
          String.format("Employee %s from company %s was not found", employeeAccountId,
              companyAccountId));
    }
    return employeeMapper.toCustomer(responseOptional.get());
  }

  private boolean isCdhApiDeprecationEnabled() {
    return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation());
  }
}
