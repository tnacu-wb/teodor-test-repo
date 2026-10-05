package uk.co.whitbread.company.employee.cache;

import static uk.co.whitbread.company.employee.utils.SanitizingUtils.sanitize;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.company.employee.client.HotelAccountClient;
import uk.co.whitbread.company.employee.client.model.Customer;
import uk.co.whitbread.company.employee.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.company.employee.mapper.EmployeeMapper;
import uk.co.whitbread.company.employee.model.feature.FeatureFlag;
import uk.co.whitbread.company.employee.model.feature.UnleashWrapper;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerCacheProvider {

    private final HotelAccountClient hotelAccountClient;
    private final EmployeeDataService employeeDataService;
    private final EmployeeMapper employeeMapper;
    private final UnleashWrapper<FeatureFlag> unleashWrapper;

    private static final String CUSTOMER_ID = "companyEmployee";

    public Customer getCustomer(final String sessionId) {
        log.debug("Retrieving customer from Hotel Account Service using session id {} (not cached)", sanitize(sessionId));
        return hotelAccountClient.getCustomer(CUSTOMER_ID, sessionId, true);
    }

    public Customer getCdhEmployee(String companyAccountId, String employeeAccountId, String userEmail) {
        log.debug(
            "Retrieving employee from CDH using company id {} employee id {} (not cached)",
            companyAccountId, employeeAccountId);
        Optional<GetEmployeeResponse> responseOptional =
            unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
                ? employeeDataService.getEmployeeV2(companyAccountId, employeeAccountId, userEmail)
                : employeeDataService.getEmployee(companyAccountId, employeeAccountId, userEmail);
        if (responseOptional.isEmpty()) {
            throw new EmployeeNotFoundException(
                String.format("Employee %s from company %s was not found", employeeAccountId,
                    companyAccountId));
        }
        return employeeMapper.toCustomer(responseOptional.get());
    }
}
