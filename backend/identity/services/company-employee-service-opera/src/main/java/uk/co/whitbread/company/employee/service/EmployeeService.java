package uk.co.whitbread.company.employee.service;

import static uk.co.whitbread.company.employee.model.EmployeeStatus.ACTIVE;
import static uk.co.whitbread.company.employee.utils.SanitizingUtils.sanitize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.company.employee.exceptions.CompanyNotFoundException;
import uk.co.whitbread.company.employee.exceptions.EmployeeAlreadyExistsException;
import uk.co.whitbread.company.employee.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.company.employee.exceptions.InvalidOperationException;
import uk.co.whitbread.company.employee.exceptions.RemoveLastTravelManagerRestrictedOperationException;
import uk.co.whitbread.company.employee.exceptions.RemoveMainContactRestrictedOperationException;
import uk.co.whitbread.company.employee.exceptions.RestrictedOperationException;
import uk.co.whitbread.company.employee.mapper.EmployeeMapper;
import uk.co.whitbread.company.employee.model.AccessLevel;
import uk.co.whitbread.company.employee.model.Employee;
import uk.co.whitbread.company.employee.model.EmployeeStatus;
import uk.co.whitbread.company.employee.model.GetEmployeesRequest;
import uk.co.whitbread.company.employee.model.GetEmployeesResponse;
import uk.co.whitbread.company.employee.model.InviteRequest;
import uk.co.whitbread.company.employee.model.LanguageCode;
import uk.co.whitbread.company.employee.model.UserDefinedAnswer;
import uk.co.whitbread.company.employee.model.feature.FeatureFlag;
import uk.co.whitbread.company.employee.model.feature.UnleashWrapper;
import uk.co.whitbread.company.employee.properties.EmailProperties;
import uk.co.whitbread.company.employee.validation.InnbEmployeeValidator;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.azureemail.model.EmployeeAccessLevelChange;
import uk.co.whitbread.shared.azureemail.model.EmployeeAccountActivation;
import uk.co.whitbread.shared.azureemail.model.EmployeeInvite;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;
import uk.co.whitbread.shared.cdh.CompanyDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.GenerateActivationKeyRequest;
import uk.co.whitbread.shared.cdh.model.GenerateActivationKeyResponse;
import uk.co.whitbread.shared.cdh.model.GetCompanyEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetEmployeesQueryParams;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService {
    private final WebServiceTemplate webServiceTemplate;
    private final Auth0Service auth0Service;
    private final EmployeeDataService employeeDataService;
    private final CompanyDataService companyDataService;
    private final EmployeeMapper employeeMapper;
    private final AzureEmailService emailService;
    private final EmailProperties emailProperties;
    private final InnbEmployeeValidator innbEmployeeValidator;
    private final UnleashWrapper<FeatureFlag> unleashWrapper;

    private static final String RESTRICTED_OPERATION = "Changing access level is not allowed.";
    private static final String REMOVE_LAST_TRAVEL_MANAGER_RESTRICTED_OPERATION = "There must be at least one Travel Manager on this account.";
    private static final String REMOVE_MAIN_CONTACT_RESTRICTED_OPERATION = "You are not allowed to remove the main contact.";
    private static final String RESTRICTED_STATUS_CHANGE = "Changing status to %s is not allowed.";
    private static final String INVALID_ACTIVATION_KEY = "Employee with activation key %s was not found";
    private static final String EMPLOYEE_FROM_COMPANY_NOT_FOUND = "Employee %s from company %s was not found";

    private static final String SUPER_ACCESS_LEVEL = "SUPER";
    private static final String BUSINESS_PAY_MANAGER_ACCESS_LEVEL = "BUSINESS_PAY_MANAGER";

    public void updateCdhEmployee(String companyId, String employeeId, Employee updatedEmployee,
        CdhEmployeeDetails loggedInEmployee, String language) {
        String userEmail = loggedInEmployee.getUserEmail();
        var cdhEmployee = getCdhEmployee(companyId, employeeId, userEmail);
        if (updatedEmployee.getEmployeeStatus() == EmployeeStatus.PURGED) {
            if (cdhEmployee.isMainEmployee()) {
                log.warn("You are not allowed to remove the main contact!");
                throw new RemoveMainContactRestrictedOperationException(REMOVE_MAIN_CONTACT_RESTRICTED_OPERATION);
            }
            String emailAddress = updatedEmployee.getEmailAddress();
            try {
                auth0Service.deleteUser(emailAddress);
            } catch (Auth0ApiException e) {
                throw new AuthServiceException(String.format("Could not delete user %s from Auth0",
                    emailAddress));
            }
            employeeDataService.deleteEmployeeAccount(companyId, employeeId, userEmail);
            return;
        }


        var cdhLoggedInEmployee = getCdhEmployee(loggedInEmployee.getCompanyAccountId(),
                loggedInEmployee.getEmployeeAccountId(), userEmail);

        if (SUPER_ACCESS_LEVEL.equals(cdhEmployee.getAccessLevel())
            && updatedEmployee.getAccessLevel() != AccessLevel.SUPER
            && ACTIVE.getValue().equals(cdhEmployee.getEmployeeStatus())) {
            validateLastTravelManager(companyId, userEmail);
        }

        validateStatusChange(cdhLoggedInEmployee, cdhEmployee, updatedEmployee);

        var employeeFromCdh = employeeMapper.toEmployee(cdhEmployee);
        if (loggedInEmployee.getEmployeeAccountId().equals(employeeId)) {
            validateCdhAccessLevels(employeeFromCdh, updatedEmployee);
        }
        auth0Service.updateUserDetailsInAuth0(employeeFromCdh, updatedEmployee);
        try {
            var employeeAccountRequest = employeeMapper.toEmployeeAccountRequest(updatedEmployee);
            employeeAccountRequest.setGlobalCompanyId(cdhEmployee.getGlobalCompanyId());
            employeeAccountRequest.setBartEmployeeId(cdhEmployee.getBartEmployeeId());
            employeeAccountRequest.setPaymentPreference(cdhEmployee.getPaymentPreference());
            employeeAccountRequest.setBookingPreference(cdhEmployee.getBookingPreference());
            if (StringUtils.isEmpty(employeeAccountRequest.getCentralCardIdString())) {
                employeeAccountRequest.setCentralCardIdString(cdhEmployee.getCentralCardIdString());
            }
            employeeDataService.updateEmployeeAccount(companyId, employeeId, employeeAccountRequest,
                userEmail);
            if (isAccessLevelDifferent(employeeFromCdh.getAccessLevel(), updatedEmployee.getAccessLevel())) {
                sendAsyncBBChangeEmployeeAccessLevel(employeeAccountRequest, language);
            }
        } catch (CDHException ex) {
            auth0Service.updateUserDetailsInAuth0(updatedEmployee, employeeFromCdh);
            throw ex;
        }
    }

    private void validateStatusChange(uk.co.whitbread.shared.cdh.model.GetEmployeeResponse cdhLoggedInEmployee,
                                      uk.co.whitbread.shared.cdh.model.GetEmployeeResponse cdhEmployee,
                                      Employee updatedEmployee) {
        if (SUPER_ACCESS_LEVEL.equals(cdhLoggedInEmployee.getAccessLevel())
                && !getAllowedStatusList(EmployeeStatus.valueOf(cdhEmployee.getEmployeeStatus().toUpperCase()))
                .contains(updatedEmployee.getEmployeeStatus())) {
            throw new RestrictedOperationException(
                    String.format(RESTRICTED_STATUS_CHANGE, updatedEmployee.getEmployeeStatus()));
        }
    }

    private List<EmployeeStatus> getAllowedStatusList(EmployeeStatus currentStatus) {

        List<EmployeeStatus> allowedStatusList = new ArrayList<>();
        switch (currentStatus) {
            case ACTIVE -> allowedStatusList.addAll(List.of(ACTIVE, EmployeeStatus.DEACTIVATED));
            case INACTIVE -> allowedStatusList.addAll(List.of(EmployeeStatus.INACTIVE, ACTIVE,
                    EmployeeStatus.DEACTIVATED));
            case DEACTIVATED -> allowedStatusList.addAll(List.of(EmployeeStatus.DEACTIVATED, EmployeeStatus.PURGED));
            default -> log.warn(String.format("Status %s can not be changed", currentStatus.getValue()));
        }
        return allowedStatusList;
    }

    public void activateCdhEmployee(String activationKey, Employee employee, String employeeId) {
        var emailAddress = employee.getEmailAddress();
        validateEmailAddress(activationKey, emailAddress);

        var getEmployeeResponse = employeeDataService.activateEmployee(
            employeeId, activationKey, emailAddress);

        if (getEmployeeResponse.isPresent()) {
            var cdhEmployee = getEmployeeResponse.get();
            var employeeAccountRequest = employeeMapper.toEmployeeAccountRequest(employee);
            employeeAccountRequest.setGlobalCompanyId(cdhEmployee.getGlobalCompanyId());
            employeeAccountRequest.setBartEmployeeId(cdhEmployee.getBartEmployeeId());
            employeeAccountRequest.setEmployeeStatus(cdhEmployee.getEmployeeStatus());
            if (StringUtils.isEmpty(employeeAccountRequest.getCentralCardIdString())) {
                employeeAccountRequest.setCentralCardIdString(cdhEmployee.getCentralCardIdString());
            }
            employeeDataService.updateEmployeeAccount(cdhEmployee.getCompanyAccountId(),
                cdhEmployee.getEmployeeAccountId(), employeeAccountRequest,
                emailAddress);
            try {
                auth0Service.saveCdhBusinessUserInAuth0(emailAddress, employee.getPassword(),
                    cdhEmployee.getCompanyAccountId(), cdhEmployee.getEmployeeAccountId());
            } catch (Auth0ApiException e) {
                throw new AuthServiceException(
                    String.format("Could not save user %s in Auth0 ", emailAddress));
            }
        } else {
            throw new EmployeeNotFoundException(
                String.format(INVALID_ACTIVATION_KEY, activationKey));
        }
    }

    public GetEmployeesResponse getEmployeesFromCdh(GetEmployeesRequest getEmployeesRequest,
        String companyId, String userEmail) {

        final GetCompanyEmployeesQueryParams queryParameters =
            employeeMapper.toGetCompanyEmployeesQueryParams(getEmployeesRequest);

        final Optional<uk.co.whitbread.shared.cdh.model.GetEmployeesResponse> companyEmployees =
            unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
                ? employeeDataService.getCompanyEmployeesV2(companyId, queryParameters, userEmail)
                : employeeDataService.getCompanyEmployees(companyId, queryParameters, userEmail);

        if(companyEmployees.isEmpty() || Objects.isNull(companyEmployees.get().getResults())){
            return GetEmployeesResponse.builder()
                .success(true)
                .employees(Collections.emptyList())
                .build();
        }
        uk.co.whitbread.shared.cdh.model.GetEmployeesResponse employees = companyEmployees.get();
        if (getEmployeesRequest.isShouldFilterEmployees()) {
            filterEmployeesWithoutName(employees);
        }

        return employeeMapper.toGetEmployeesResponse(employees);
    }

    public uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getCdhEmployee(String companyId,
        String employeeId, String userEmail) {

        final Optional<uk.co.whitbread.shared.cdh.model.GetEmployeeResponse> responseOptional =
            unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
                ?  employeeDataService.getEmployeeV2(companyId, employeeId, userEmail)
                : employeeDataService.getEmployee(companyId, employeeId, userEmail);
        if (responseOptional.isEmpty()) {
            throw new EmployeeNotFoundException(
                String.format(EMPLOYEE_FROM_COMPANY_NOT_FOUND, employeeId, companyId));
        }
        return responseOptional.get();
    }

    public Employee getCdhEmployee(String activationKey, String userEmail) {
        final var getEmployeeResponse = getCdhEmployeeFromList(activationKey, userEmail);
        return employeeMapper.toEmployee(getEmployeeResponse);
    }

    public String addCdhEmployee(String companyId, Employee employee,
        CdhEmployeeDetails cdhEmployeeDetails, String language) {
        log.debug("Called EmployeeService.addCdhEmployee");
        if (Objects.nonNull(employee) && Objects.nonNull(employee.getEmployeeAnswers()) &&
            Objects.nonNull(employee.getEmployeeAnswers().getUserDefinedAnswers()) &&
            !employee.getEmployeeAnswers().getUserDefinedAnswers().isEmpty()) {
            List<UserDefinedAnswer> filterNullList = employee.getEmployeeAnswers()
                .getUserDefinedAnswers()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            employee.getEmployeeAnswers().setUserDefinedAnswers(filterNullList);
        }
        innbEmployeeValidator.validateEmployeeAccessLevelsBasedOnManagerAccessLevel(
            cdhEmployeeDetails.getAccessLevel(), employee.getAccessLevel());

        EmployeeAccountRequest employeeAccountRequest = employeeMapper
            .toEmployeeAccountRequest(employee);

        final String employeeAccountId = employeeDataService.createEmployeeAccount(companyId,
                employeeAccountRequest, cdhEmployeeDetails.getUserEmail())
            .getEmployeeAccountId();

        sendAsyncEmployeeActivationEmail(companyId, employeeAccountId,
            cdhEmployeeDetails.getUserEmail(), language, employee.getInnBusiness());

        return employeeAccountId;
    }

    void sendAsyncEmployeeActivationEmail(String companyAccountId, String employeeAccountId,
        String accessedBy, String language, Boolean innBusiness) {

        CompletableFuture.runAsync(() -> {
            final Optional<uk.co.whitbread.shared.cdh.model.GetEmployeeResponse> optionalEmployeeAccount =
                unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
                    ? employeeDataService.getEmployeeV2(companyAccountId, employeeAccountId, accessedBy)
                    : employeeDataService.getEmployee(companyAccountId, employeeAccountId, accessedBy);
            optionalEmployeeAccount.ifPresentOrElse(employeeAccount -> {
                String activationLink =  Boolean.TRUE.equals(innBusiness) ? getInnBActivationLink(
                    language, employeeAccount.getActivationKey())
                    : getActivationLink(language, employeeAccount.getActivationKey());
                final EmployeeAccountActivation employeeActivation = EmployeeAccountActivation.builder()
                    .activationLink(activationLink)
                    .accessLevel(employeeAccount.getAccessLevel())
                    .employeeEmail(employeeAccount.getEmailAddress())
                    .firstName(employeeAccount.getFirstName())
                    .lastName(employeeAccount.getLastName())
                    .companyName(getCompanyName(companyAccountId, accessedBy))
                    .language(language)
                    .build();
                try {
                    emailService.sendEmployeeAccountActivationEmail(employeeActivation);
                } catch (Exception e) {
                    log.error(String.format(
                        "Error while triggering employee activation email; companyAccountId=%s , employeeAccountId=%s",
                        sanitize(companyAccountId), employeeAccountId), e);
                }
            }, () -> {
                throw new EmployeeNotFoundException(
                    String.format(EMPLOYEE_FROM_COMPANY_NOT_FOUND, employeeAccountId,
                        companyAccountId));
            });
        });
    }

    private String getCompanyName(String companyAccountId, String accessedBy) {
        var companyOptional = companyDataService.getCompany(companyAccountId, accessedBy);
        return companyOptional
            .orElseThrow(() -> new CompanyNotFoundException(
                "Company with id " + companyAccountId + " was not found"))
            .getCompanyName();
    }

    public void updateCdhEmployeeAccessLevel(String companyId, String employeeId,
        AccessLevel accessLevel, String travelManagerEmail, String language) {
        log.debug("Called EmployeeService.updateCdhEmployeeAccessLevel");

        final var employee = unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
            ? employeeDataService.getEmployeeV2(companyId, employeeId, travelManagerEmail)
            : employeeDataService.getEmployee(companyId, employeeId, travelManagerEmail);
        if (employee.isEmpty()) {
            throw new EmployeeNotFoundException(
                String.format(EMPLOYEE_FROM_COMPANY_NOT_FOUND, employeeId, companyId));
        }

        if (employee.get().getAccessLevel().equals(SUPER_ACCESS_LEVEL)) {
            validateLastTravelManager(companyId, travelManagerEmail);
        }

        EmployeeAccountRequest employeeAccountRequest = employeeMapper.toEmployeeAccountRequest(employee.get());
        employeeAccountRequest.setAccessLevel(accessLevel.toString());
        employeeDataService
            .updateEmployeeAccount(companyId, employeeId, employeeAccountRequest, travelManagerEmail);
        sendAsyncBBChangeEmployeeAccessLevel(employeeAccountRequest, language);
    }

    private void sendAsyncBBChangeEmployeeAccessLevel(EmployeeAccountRequest employeeAccountRequest, String language) {
        CompletableFuture.runAsync(() -> {
          String loginUrl = getLoginUrl(language);
          var employeeAccessLevel = EmployeeAccessLevelChange.builder()
              .email(employeeAccountRequest.getEmailAddress())
              .accessLevel(employeeAccountRequest.getAccessLevel())
              .language(language)
              .firstName(employeeAccountRequest.getFirstName())
              .loginUrl(loginUrl)
              .build();
          emailService.sendBBChangeEmployeeAccessLevel(employeeAccessLevel);
      });
    }

  private String getLoginUrl(String language) {
    if (LanguageCode.DE.asLowerCase().equalsIgnoreCase(language)) {
      return emailProperties.getLoginUrlDe();
    }
    return emailProperties.getLoginUrl();
  }

    private void validateCdhAccessLevels(Employee cdhEmployee, Employee employee) {
        if (!Objects.equals(cdhEmployee.getAccessLevel(), AccessLevel.SUPER)
            && isAccessLevelDifferent(cdhEmployee.getAccessLevel(), employee.getAccessLevel())) {
            log.warn("User tried to update their access level");
            throw new RestrictedOperationException(RESTRICTED_OPERATION);
        }
    }

    private boolean isAccessLevelDifferent(AccessLevel cdhAccessLevel, AccessLevel requestAccessLevel) {
        return !Objects.equals(cdhAccessLevel, requestAccessLevel);
    }

    private void validateLastTravelManager(String companyId, String travelManagerEmail) {
        if (countActiveTravelManagers(companyId, travelManagerEmail) < 2) {
            log.warn("There is only one active Travel Manager left!");
            throw new RemoveLastTravelManagerRestrictedOperationException(
                REMOVE_LAST_TRAVEL_MANAGER_RESTRICTED_OPERATION);
        }
    }

    private uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getCdhEmployeeFromList(
        String activationKey, String userEmail) {
        GetEmployeesQueryParams employeesQueryParams = GetEmployeesQueryParams.builder()
            .activationKey(activationKey).build();
        final Optional<uk.co.whitbread.shared.cdh.model.GetEmployeesResponse> employees =
            unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
                ? employeeDataService.getEmployeesV2(employeesQueryParams, userEmail)
                : employeeDataService.getEmployees(employeesQueryParams, userEmail);
        if (employees.isEmpty() || employees.get().getResults().isEmpty()) {
            throw new EmployeeNotFoundException(
                String.format(INVALID_ACTIVATION_KEY, activationKey));
        }
        return employees.get().getResults().get(0);
    }

    public void inviteCdhEmployee(String companyId, InviteRequest inviteRequest,
        CdhEmployeeDetails managerDetails, String language) {
        final var employeeAccountRequest = createEmployeeAccountRequest(inviteRequest, managerDetails.getAccessLevel());
        try {
            List<uk.co.whitbread.shared.cdh.model.GetEmployeeResponse> foundEmployees =
                getEmployeesFromCdh(inviteRequest.getEmailAddress(), managerDetails.getUserEmail());
            if (CollectionUtils.isEmpty(foundEmployees)) {
                var response = employeeDataService.createEmployeeAccount(companyId,
                    employeeAccountRequest, managerDetails.getUserEmail());
                sendAsyncBBInviteEmployeeEmail(inviteRequest, response.getActivationKey(),
                    language);
                return;
            }
            var employeeResponse = foundEmployees.get(0);
            if (!employeeResponse.getCompanyAccountId().equals(companyId)) {
                throw new EmployeeAlreadyExistsException(
                    "Email address " + inviteRequest.getEmailAddress()
                        + " is registered with another company.");
            }
            if (!EmployeeStatus.INACTIVE.getValue().equalsIgnoreCase(employeeResponse.getEmployeeStatus())) {
                throw new EmployeeAlreadyExistsException(
                    "Email address " + inviteRequest.getEmailAddress()
                        + " is already registered.");
            }
            var response = generateNewActivationKey(
                employeeResponse.getCompanyAccountId(),
                employeeResponse.getEmployeeAccountId(), managerDetails.getUserEmail());
            sendAsyncBBInviteEmployeeEmail(inviteRequest, response.getActivationKey(), language);
        } catch (CDHException cdhException) {
            if (cdhException.getStatus() == HttpStatus.SC_NOT_FOUND) {
                var response = employeeDataService.createEmployeeAccount(companyId,
                    employeeAccountRequest, managerDetails.getUserEmail());
                sendAsyncBBInviteEmployeeEmail(inviteRequest, response.getActivationKey(),
                    language);
                return;
            }
            throw cdhException;
        }
    }

    private List<uk.co.whitbread.shared.cdh.model.GetEmployeeResponse> getEmployeesFromCdh(
        String searchingEmail, String accessedBy) {
        GetEmployeesQueryParams employeesQueryParams = GetEmployeesQueryParams.builder()
            .emailAddress(searchingEmail).build();
        var getEmployeesResponse =
            unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
                ? employeeDataService.getEmployeesV2(employeesQueryParams, accessedBy)
                : employeeDataService.getEmployees(employeesQueryParams, accessedBy);
        if (getEmployeesResponse.isPresent()) {
            return getEmployeesResponse.get().getResults();
        }
        return List.of();
    }

    private GenerateActivationKeyResponse generateNewActivationKey(String companyAccountId,
        String employeeAccountId, String accessedBy) {
        GenerateActivationKeyRequest generateActivationKeyRequest = GenerateActivationKeyRequest.builder()
            .employeeAccountId(employeeAccountId)
            .build();
        return employeeDataService.generateNewActivationKey(companyAccountId, employeeAccountId,
            generateActivationKeyRequest, accessedBy);
    }

    private void sendAsyncBBInviteEmployeeEmail(InviteRequest inviteRequest, String activationKey,
        String language) {
        CompletableFuture.runAsync(() -> {
            String activationLink =
                Boolean.TRUE.equals(inviteRequest.getInnBusiness()) ? getInnBActivationLink(
                    language, activationKey)
                    : getActivationLink(language, activationKey);
            var employeeInvite = EmployeeInvite.builder()
                .email(inviteRequest.getEmailAddress())
                .accessLevel(AccessLevel.SELF.name())
                .activationLink(activationLink)
                .language(language)
                .build();
            emailService.sendBBInviteEmployeeEmail(employeeInvite);
        });
    }

    private String getActivationLink(String language, String activationKey) {
        if (LanguageCode.DE.asLowerCase().equalsIgnoreCase(language)) {
            return emailProperties.getEmployeeActivationUrlDe() + activationKey;
        }
        return emailProperties.getEmployeeActivationUrl() + activationKey;
    }

    private String getInnBActivationLink(String language, String activationKey) {
        if (LanguageCode.DE.asLowerCase().equalsIgnoreCase(language)) {
            return emailProperties.getInnbEmployeeActivationUrlDe() + activationKey;
        }
        return emailProperties.getInnbEmployeeActivationUrl() + activationKey;
    }

    private EmployeeAccountRequest createEmployeeAccountRequest(InviteRequest inviteRequest,
        String managerAccessLevel){
        AccessLevel employeeAccessLevel = switch (managerAccessLevel) {
            case SUPER_ACCESS_LEVEL -> AccessLevel.SELF;
            case BUSINESS_PAY_MANAGER_ACCESS_LEVEL -> AccessLevel.BUSINESS_PAY_USER;
            default -> throw new InvalidOperationException("The manager access level is not valid.");
        };

        return EmployeeAccountRequest.builder()
            .emailAddress(inviteRequest.getEmailAddress())
            .centralCardId(inviteRequest.getCentralCardId())
            .accessLevel(employeeAccessLevel.name())
            .build();
    }

    private void filterEmployeesWithoutName(uk.co.whitbread.shared.cdh.model.GetEmployeesResponse companyEmployees) {
        var filteredEmployees = companyEmployees.getResults().stream()
            .filter(employee ->
                StringUtils.isNotEmpty(employee.getFirstName()) && StringUtils.isNotEmpty(employee.getLastName()))
            .toList();
        companyEmployees.setResults(filteredEmployees);
    }

    private int countActiveTravelManagers(String companyId, String travelManagerEmail) {
        final GetCompanyEmployeesQueryParams queryParams =
            GetCompanyEmployeesQueryParams.builder()
                .accessLevel(uk.co.whitbread.shared.cdh.AccessLevel.SUPER)
                .pageSize(20)
                .build();
        Optional<uk.co.whitbread.shared.cdh.model.GetEmployeesResponse> response;
        int totalTravelManagers = 0;

        do {
            response = unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
                ? employeeDataService.getCompanyEmployeesV2(companyId, queryParams, travelManagerEmail)
                : employeeDataService.getCompanyEmployees(companyId, queryParams, travelManagerEmail);
            if (response.isPresent()) {
                filterInactiveTravelManagers(response.get());
                totalTravelManagers += response.get().getResults().size();
                queryParams.setPageToken(response.get().getContinuationToken());
            }
        } while (totalTravelManagers < 2 && queryParams.getPageToken() != null);

        return totalTravelManagers;
    }

    private void filterInactiveTravelManagers(
        uk.co.whitbread.shared.cdh.model.GetEmployeesResponse travelManagers) {
        var activeTravelManagers = travelManagers.getResults().stream()
            .filter(tm -> tm.getEmployeeStatus().equals("ACTIVE"))
            .toList();
        travelManagers.setResults(activeTravelManagers);
    }

    private void validateEmailAddress(String activationKey, String emailAddress) {
        GetEmployeesQueryParams employeesQueryParams = GetEmployeesQueryParams.builder()
            .activationKey(activationKey).build();
        var optionalEmployeeResponse =
            unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
                ? employeeDataService.getEmployeesV2(employeesQueryParams, emailAddress)
                : employeeDataService.getEmployees(employeesQueryParams, emailAddress);
        if (optionalEmployeeResponse.isEmpty() || CollectionUtils.isEmpty(
            optionalEmployeeResponse.get().getResults())) {
            throw new EmployeeNotFoundException(
                String.format(INVALID_ACTIVATION_KEY, activationKey));
        }
        var cdhEmployee = optionalEmployeeResponse.get().getResults().get(0);
        if (emailAddress.equalsIgnoreCase(cdhEmployee.getEmailAddress())) {
            return;
        }

        employeesQueryParams = GetEmployeesQueryParams.builder().emailAddress(emailAddress).build();
        optionalEmployeeResponse =
            unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
                ? employeeDataService.getEmployeesV2(employeesQueryParams, emailAddress)
                : employeeDataService.getEmployees(employeesQueryParams, emailAddress);
        if (optionalEmployeeResponse.isPresent() && CollectionUtils.isNotEmpty(
            optionalEmployeeResponse.get().getResults())) {
            throw new EmployeeAlreadyExistsException(
                String.format("Email address %s is already registered", emailAddress));
        }
    }
}
