package uk.co.whitbread.company.employee.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.company.employee.cache.CustomerCacheProvider;
import uk.co.whitbread.company.employee.client.model.AccessLevel;
import uk.co.whitbread.company.employee.client.model.Business;
import uk.co.whitbread.company.employee.client.model.Customer;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;

@Slf4j
@Service
@RequiredArgsConstructor
public class CdhAuthorizationService {

    private final CustomerCacheProvider cacheProvider;

    public boolean isSuperAccessLevelUser(CdhEmployeeDetails tokenDetails) {
        return Optional.of(
                getCustomer(tokenDetails.getCompanyAccountId(), tokenDetails.getEmployeeAccountId(),
                    tokenDetails.getUserEmail()))
            .map(Customer::getBusiness)
            .map(Business::getAccessLevel)
            .map(accessLevel -> accessLevel.equals(AccessLevel.SUPER))
            .orElse(false);
    }

    public boolean isBusinessPayManagerLevelUser(CdhEmployeeDetails tokenDetails) {
        return Optional.of(
                getCustomer(tokenDetails.getCompanyAccountId(), tokenDetails.getEmployeeAccountId(),
                    tokenDetails.getUserEmail()))
            .map(Customer::getBusiness)
            .map(Business::getAccessLevel)
            .map(accessLevel -> accessLevel.equals(AccessLevel.BUSINESS_PAY_MANAGER))
            .orElse(false);
    }

    public boolean isSameEmployee(String employeeAccountIdFromToken, String employeeIdFromPath) {
        return Optional.ofNullable(employeeAccountIdFromToken)
            .map(actualEmployeeId -> actualEmployeeId.equals(employeeIdFromPath))
            .orElse(false);
    }

    public boolean isSameCompany(String companyAccountIdFromToken, String companyIdFromPath) {
        return Optional.ofNullable(companyAccountIdFromToken)
                .map(actualCompanyId -> actualCompanyId.equals(companyIdFromPath))
                .orElse(false);
    }

    private Customer getCustomer(String companyAccountId, String employeeAccountId, String userEmail) {
        log.debug("Called CdhAuthorizationService.getCustomer for companyId={} and employeeId={}",
            companyAccountId, employeeAccountId);
        Customer customer = cacheProvider.getCdhEmployee(companyAccountId, employeeAccountId, userEmail);
        log.debug("Customer with company ID {} and employee ID {} successfully returned.",
            customer.getCompanyId(), customer.getBusiness().getEmployeeId());
        return customer;
    }
}
