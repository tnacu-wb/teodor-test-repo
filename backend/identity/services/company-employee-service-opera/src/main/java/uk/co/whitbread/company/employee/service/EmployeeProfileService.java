package uk.co.whitbread.company.employee.service;

import static uk.co.whitbread.company.employee.service.ServiceUtils.sendAsyncRequestToJoinAccepted;
import static uk.co.whitbread.company.employee.service.ServiceUtils.sendRequestToJoinRejectedEmail;
import static uk.co.whitbread.company.employee.utils.Auth0ManagementTransformer.EMPLOYEE_STATUS_LABEL;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.company.employee.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.company.employee.mapper.BookingPreferenceMapper;
import uk.co.whitbread.company.employee.mapper.EmployeeMapper;
import uk.co.whitbread.company.employee.model.BookingPreference;
import uk.co.whitbread.company.employee.model.EmployeeStatus;
import uk.co.whitbread.company.employee.model.LanguageCode;
import uk.co.whitbread.company.employee.model.UpdateRegistrationRequest;
import uk.co.whitbread.company.employee.model.feature.FeatureFlag;
import uk.co.whitbread.company.employee.model.feature.UnleashWrapper;
import uk.co.whitbread.company.employee.properties.Auth0Properties;
import uk.co.whitbread.company.employee.properties.EmailProperties;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;
import uk.co.whitbread.shared.cdh.CompanyDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeProfileService {
    private final WebServiceTemplate webServiceTemplateGuestDetails;
    private final EmployeeService employeeService;
    private final Auth0Service auth0Service;
    private final Auth0Properties auth0Properties;
    private final EmployeeDataService employeeDataService;
    private final BookingPreferenceMapper bookingPreferenceMapper;
    private final EmployeeMapper employeeMapper;
    private final AzureEmailService emailService;
    private final EmailProperties emailProperties;
    private final CompanyDataService companyDataService;
    private final UnleashWrapper<FeatureFlag> unleashWrapper;

    public BookingPreference getCdhEmployeeBookingPreferences(String companyId, String employeeId,
        String userEmail) {
        final Optional<uk.co.whitbread.shared.cdh.model.GetEmployeeResponse> responseOptional =
            unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
                ? employeeDataService.getEmployeeV2(companyId, employeeId, userEmail)
                :  employeeDataService.getEmployee(companyId, employeeId, userEmail);
        if (responseOptional.isEmpty()) {
            throw new EmployeeNotFoundException(
                String.format("Employee %s from company %s was not found", employeeId, companyId));
        }
        return bookingPreferenceMapper.toBookingPreference(
            responseOptional.get().getBookingPreference());
    }

    public void updateCdhRegistration(UpdateRegistrationRequest payload,
        CdhEmployeeDetails travelMangerDetails, String language) {

        log.debug("Called EmployeeService.updateCdhRegistration");
        String travelManagerEmail = travelMangerDetails.getUserEmail();
        String emailAddress = payload.getEmailAddress();
        Optional<GetEmployeesResponse> employees =
          unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
              ? employeeDataService.getEmployeesV2(
              GetEmployeesQueryParams.builder().emailAddress(emailAddress).build(),
              travelManagerEmail)
              : employeeDataService.getEmployees(
                  GetEmployeesQueryParams.builder().emailAddress(emailAddress).build(),
                  travelManagerEmail);
        if (employees.isEmpty() || employees.get().getResults().isEmpty()) {
            throw new EmployeeNotFoundException(
                String.format("Employee with email %s was not found", emailAddress));
        }
        GetEmployeeResponse employee = employees.get().getResults().get(0);

        if (payload.isApproved()) {
            if (Strings.isNotEmpty(employee.getActivationKey()) && !EmployeeStatus.ACTIVE.getValue()
                .equals(employee.getEmployeeStatus())) {
                employee = activateEmployee(employee, travelManagerEmail);
            }
            EmployeeAccountRequest employeeAccountRequest = employeeMapper.toEmployeeAccountRequest(employee);
            employeeAccountRequest.setAccessLevel(payload.getAccessLevel().toString());
            employeeAccountRequest.setEmployeeStatus(EmployeeStatus.ACTIVE.getValue());
            employeeAccountRequest.setAwaitingApproval(false);
            employeeDataService
                .updateEmployeeAccount(employee.getCompanyAccountId(), employee.getEmployeeAccountId(),
                    employeeAccountRequest, travelManagerEmail);
            updateAuth0AppMetadataForCdh(emailAddress);
            sendAsyncRequestToJoinAccepted(companyDataService, emailService, employee, language, getBbHomePage(language));
        } else {
            employeeDataService
                .deleteEmployeeAccount(employee.getCompanyAccountId(), employee.getEmployeeAccountId(),
                    travelManagerEmail);
            try {
                auth0Service.deleteUser(emailAddress);
            } catch (Auth0ApiException e) {
                throw new AuthServiceException(String.format("Could not delete user %s from Auth0",
                    emailAddress));
            }
            sendRequestToJoinRejectedEmail(companyDataService, emailService, employee, travelMangerDetails, language);
        }
    }

    @NotNull
    private GetEmployeeResponse activateEmployee(GetEmployeeResponse employee,
        String travelManagerEmail) {

        Optional<GetEmployeeResponse> getEmployeeResponse = employeeDataService.activateEmployee(
            employee.getEmployeeAccountId(),
            employee.getActivationKey(), travelManagerEmail);
        if (getEmployeeResponse.isEmpty()) {
            throw new EmployeeNotFoundException(
                String.format("Could not activate employee: %s, %s",
                    employee.getEmailAddress(), employee.getEmployeeAccountId()));
        }
        return getEmployeeResponse.get();
    }

    private void updateAuth0AppMetadataForCdh(String email) {
        Map<String, Object> appMetadata = new HashMap<>(
            Map.of(EMPLOYEE_STATUS_LABEL, EmployeeStatus.ACTIVE));
        auth0Service.updateAppMetadata(email, appMetadata);
    }

    public void updateCdhEmployeeBookingPreferences(String companyAccountId,
        String employeeAccountId, BookingPreference bookingPreference, String accessedBy) {
        var optionalEmployeeAccountResponse =
            unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
                ? employeeDataService.getEmployeeV2(companyAccountId, employeeAccountId, accessedBy)
                : employeeDataService.getEmployee(companyAccountId,
                employeeAccountId, accessedBy);
        if (optionalEmployeeAccountResponse.isEmpty()) {
            throw new EmployeeNotFoundException(
                "No Employee Account with companyId " + companyAccountId + " and employeeId "
                    + employeeAccountId
                    + " was found.");
        }
        var employeeAccountResponse = optionalEmployeeAccountResponse.get();
        var cdhEmployeeBookingPreferences = bookingPreferenceMapper.toBookingPreference(
            bookingPreference);
        employeeAccountResponse.setBookingPreference(cdhEmployeeBookingPreferences);
        EmployeeAccountRequest employeeAccountRequest = employeeMapper.toEmployeeAccountRequest(
            employeeAccountResponse);
        employeeDataService.updateEmployeeAccount(companyAccountId, employeeAccountId,
            employeeAccountRequest, accessedBy);
    }

    private String getBbHomePage(String language) {
        if (LanguageCode.DE.asLowerCase().equalsIgnoreCase(language)) {
            return emailProperties.getLoginUrlDe();
        }
        return emailProperties.getLoginUrl();
    }
}
