package uk.co.whitbread.company.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.company.employee.exceptions.EmployeeNotFoundException.ERROR_CODE;
import static uk.co.whitbread.company.employee.utils.BuildRequests.buildEmployee;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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
import uk.co.whitbread.company.employee.model.EmployeeSummary;
import uk.co.whitbread.company.employee.model.GetEmployeesRequest;
import uk.co.whitbread.company.employee.model.GetEmployeesResponse;
import uk.co.whitbread.company.employee.model.InviteRequest;
import uk.co.whitbread.company.employee.model.feature.FeatureFlag;
import uk.co.whitbread.company.employee.model.feature.UnleashWrapper;
import uk.co.whitbread.company.employee.properties.EmailProperties;
import uk.co.whitbread.company.employee.utils.UUIDGenerator;
import uk.co.whitbread.company.employee.validation.InnbEmployeeValidator;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountResponse;
import uk.co.whitbread.shared.cdh.model.GenerateActivationKeyRequest;
import uk.co.whitbread.shared.cdh.model.GenerateActivationKeyResponse;
import uk.co.whitbread.shared.cdh.model.GetCompanyEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetEmployeesQueryParams;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmployeeServiceTest {

    private static final String COMPANY_ID = "42";
    private static final String EMPLOYEE_ID = "employeeId";
    private static final String LANGUAGE = "en";
    private static final String USER_EMAIL = "a@b.c";
    private static final String COMPANY_ACCOUNT_ID_FROM_TOKEN = "companyAccountId";
    private static final String EMPLOYEE_ACCOUNT_ID_FROM_TOKEN = "employeeAccountId";
    private static final String USER_EMAIL_FROM_TOKEN = "a@b.c";
    private static final String SUPER_ACCESS_LEVEL = "SUPER";
    private static final String BUSINESS_PAY_MANAGER_ACCESS_LEVEL = "BUSINESS_PAY_MANAGER";
    private static final String ACTIVATION_KEY = "activationKey";
    private static final String ACTIVATION_LINK = "activationLink";

    private final Employee employee = new Employee();
    private final InviteRequest inviteRequest = new InviteRequest();

    @Mock
    private Auth0Service mockAuth0Service;
    @Mock
    private EmployeeDataService employeeDataService;
    @Mock
    private EmployeeMapper employeeMapper;
    @Mock
    private AzureEmailService emailService;
    @Mock
    private EmailProperties emailProperties;
    @Mock
    private InnbEmployeeValidator approveRejectValidator;
    @Mock
    private UnleashWrapper<FeatureFlag> unleashWrapper;

    @InjectMocks
    @Spy
    private EmployeeService sut;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        var mockedFeatureFlag = org.mockito.Mockito.mock(FeatureFlag.class);
        when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
        when(unleashWrapper.isEnabled(any())).thenReturn(false);
        when(emailProperties.getEmployeeActivationUrl()).thenReturn(ACTIVATION_LINK);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void updateCdhEmployee_shouldUpdateEmployee(Boolean validateTravelManager) throws IOException {
        //Given
        Employee cdhEmployee = buildEmployee();
        Employee updatedEmployee = buildEmployee();
        if (validateTravelManager)
            updatedEmployee.setAccessLevel(AccessLevel.SUPER);
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse = objectMapper.readValue(
            new File("src/test/resources/mapping/GetEmployeeResponse.json"),
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        if (validateTravelManager)
            getEmployeeResponse.setAccessLevel("SUPER");
        getEmployeeResponse.setEmployeeStatus("ACTIVE");
        EmployeeAccountRequest updateRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            EmployeeAccountRequest.class);
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .userEmail(USER_EMAIL_FROM_TOKEN)
            .build();

        when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
            .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeDataService.getEmployee(COMPANY_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, USER_EMAIL_FROM_TOKEN))
                .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeMapper.toEmployee(getEmployeeResponse)).thenReturn(cdhEmployee);
        when(employeeMapper.toEmployeeAccountRequest(cdhEmployee)).thenReturn(updateRequest);

        //When
        sut.updateCdhEmployee(COMPANY_ID, EMPLOYEE_ID, updatedEmployee, cdhEmployeeDetails, LANGUAGE);

        //Then
        verify(employeeDataService).updateEmployeeAccount(COMPANY_ID, EMPLOYEE_ID, updateRequest,
            USER_EMAIL_FROM_TOKEN);
        verify(mockAuth0Service).updateUserDetailsInAuth0(cdhEmployee, updatedEmployee);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void updateCdhEmployee_updateFailedInCdh(Boolean validateTravelManager) throws IOException {
        //Given
        Employee cdhEmployee = buildEmployee();
        Employee updatedEmployee = buildEmployee();
        if (validateTravelManager)
            updatedEmployee.setAccessLevel(AccessLevel.SUPER);
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse = objectMapper.readValue(
            new File("src/test/resources/mapping/GetEmployeeResponse.json"),
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        if (validateTravelManager)
            getEmployeeResponse.setAccessLevel("SUPER");
        getEmployeeResponse.setEmployeeStatus("ACTIVE");
        EmployeeAccountRequest updateRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            EmployeeAccountRequest.class);
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .userEmail(USER_EMAIL_FROM_TOKEN)
            .build();

        when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
            .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeDataService.getEmployee(COMPANY_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, USER_EMAIL_FROM_TOKEN))
                .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeMapper.toEmployee(getEmployeeResponse)).thenReturn(cdhEmployee);
        when(employeeMapper.toEmployeeAccountRequest(cdhEmployee)).thenReturn(updateRequest);
        when(employeeDataService.updateEmployeeAccount(COMPANY_ID, EMPLOYEE_ID, updateRequest,
            USER_EMAIL_FROM_TOKEN)).thenThrow(new CDHException());

        //When
        assertThatThrownBy(
            () -> sut.updateCdhEmployee(COMPANY_ID, EMPLOYEE_ID, updatedEmployee,
                cdhEmployeeDetails, LANGUAGE)).isInstanceOf(CDHException.class);

        //Then
        verify(mockAuth0Service, times(2)).updateUserDetailsInAuth0(cdhEmployee, updatedEmployee);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void updateCdhEmployee_cannotChangeAccessLevelAsEmployee(Boolean validateTravelManager) throws IOException {
        //Given
        Employee cdhEmployee = buildEmployee();
        cdhEmployee.setAccessLevel(AccessLevel.BOOKER);
        Employee updatedEmployee = buildEmployee();
        updatedEmployee.setAccessLevel(AccessLevel.SUPER);
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse = objectMapper.readValue(
            new File("src/test/resources/mapping/GetEmployeeResponse.json"),
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        if (validateTravelManager)
            getEmployeeResponse.setAccessLevel("BOOKER");
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .userEmail(USER_EMAIL_FROM_TOKEN)
            .build();

        when(employeeDataService.getEmployee(COMPANY_ACCOUNT_ID_FROM_TOKEN,
            EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, USER_EMAIL_FROM_TOKEN)).thenReturn(
            Optional.of(getEmployeeResponse));
        when(employeeMapper.toEmployee(getEmployeeResponse)).thenReturn(cdhEmployee);

        //When
        assertThatThrownBy(
            () -> sut.updateCdhEmployee(COMPANY_ACCOUNT_ID_FROM_TOKEN,
                EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, updatedEmployee, cdhEmployeeDetails, LANGUAGE))
            .isInstanceOf(RestrictedOperationException.class)
            .hasFieldOrPropertyWithValue("errorCode", RestrictedOperationException.ERROR_CODE)
            .hasMessageContaining("Changing access level is not allowed.");

        //Then
        verify(mockAuth0Service, times(0)).updateUserDetailsInAuth0(any(Employee.class),
            any(Employee.class));
    }

    @Test
    void updateCdhEmployee_changeEmployeeStatusToPurged_shouldDeleteEmployeeFromCdh() throws Exception {
        //Given
        Employee updatedEmployee = buildEmployee();
        updatedEmployee.setEmployeeStatus(EmployeeStatus.PURGED);
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .userEmail(USER_EMAIL_FROM_TOKEN)
            .build();
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse = objectMapper.readValue(
                new File("src/test/resources/mapping/GetEmployeeResponse.json"),
                uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
                .thenReturn(Optional.of(getEmployeeResponse));

        //When
        sut.updateCdhEmployee(COMPANY_ID, EMPLOYEE_ID, updatedEmployee, cdhEmployeeDetails, LANGUAGE);

        //Then
        verify(mockAuth0Service).deleteUser(updatedEmployee.getEmailAddress());
        verify(employeeDataService).deleteEmployeeAccount(COMPANY_ID, EMPLOYEE_ID,
            USER_EMAIL_FROM_TOKEN);
    }

    @Test
    void updateCdhEmployee_whenMainContactValidationFailed_shouldThrowError() throws Exception {
        //Given
        Employee updatedEmployee = buildEmployee();
        updatedEmployee.setEmployeeStatus(EmployeeStatus.PURGED);
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
                .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
                .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
                .userEmail(USER_EMAIL_FROM_TOKEN)
                .build();
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse = objectMapper.readValue(
                new File("src/test/resources/mapping/GetEmployeeResponse.json"),
                uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        getEmployeeResponse.setMainEmployee(true);
        when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
                .thenReturn(Optional.of(getEmployeeResponse));

        //When
        assertThatThrownBy(
                () -> sut.updateCdhEmployee(COMPANY_ID, EMPLOYEE_ID, updatedEmployee, cdhEmployeeDetails, LANGUAGE))
                .isInstanceOf(RemoveMainContactRestrictedOperationException.class)
                .hasFieldOrPropertyWithValue("errorCode", RemoveMainContactRestrictedOperationException.ERROR_CODE)
                .hasMessageContaining("You are not allowed to remove the main contact.");

        //Then
        verify(mockAuth0Service, times(0)).deleteUser(updatedEmployee.getEmailAddress());
        verify(employeeDataService, times(0)).deleteEmployeeAccount(COMPANY_ID, EMPLOYEE_ID,
                USER_EMAIL_FROM_TOKEN);
    }

    @Test
    void updateCdhEmployee_whenLastTravelManagerValidationFailed_shouldThrowError()
        throws IOException {
        //Given
        Employee cdhEmployee = buildEmployee();
        cdhEmployee.setAccessLevel(AccessLevel.SUPER);
        Employee updatedEmployee = buildEmployee();
        updatedEmployee.setAccessLevel(AccessLevel.BOOKER);
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse = objectMapper.readValue(
            new File("src/test/resources/mapping/GetEmployeeResponse.json"),
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        getEmployeeResponse.setEmployeeStatus("ACTIVE");
        getEmployeeResponse.setAccessLevel("SUPER");
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .userEmail(USER_EMAIL_FROM_TOKEN)
            .build();
        uk.co.whitbread.shared.cdh.model.GetEmployeesResponse getEmployeesResponse = new uk.co.whitbread.shared.cdh.model.GetEmployeesResponse();
        getEmployeesResponse.setResults(List.of(getEmployeeResponse));

        when(employeeDataService.getEmployee(COMPANY_ACCOUNT_ID_FROM_TOKEN,
            EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, USER_EMAIL_FROM_TOKEN)).thenReturn(
            Optional.of(getEmployeeResponse));
        when(employeeDataService.getCompanyEmployees(eq(COMPANY_ACCOUNT_ID_FROM_TOKEN), any(GetCompanyEmployeesQueryParams.class),
            eq(USER_EMAIL_FROM_TOKEN))).thenReturn(Optional.of(getEmployeesResponse));

        //When
        assertThatThrownBy(
            () -> sut.updateCdhEmployee(COMPANY_ACCOUNT_ID_FROM_TOKEN,
                EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, updatedEmployee, cdhEmployeeDetails, LANGUAGE))
            .isInstanceOf(RemoveLastTravelManagerRestrictedOperationException.class)
            .hasFieldOrPropertyWithValue("errorCode", RemoveLastTravelManagerRestrictedOperationException.ERROR_CODE)
            .hasMessageContaining("There must be at least one Travel Manager on this account.");

        //Then
        verify(mockAuth0Service, times(0)).updateUserDetailsInAuth0(any(Employee.class),
            any(Employee.class));
    }

    @Test
    void updateCdhEmployee_whenTravelManagerValidationPassed_shouldUpdateEmployee() throws IOException {
        //Given
        Employee cdhEmployee = buildEmployee();
        cdhEmployee.setAccessLevel(AccessLevel.SUPER);
        Employee updatedEmployee = buildEmployee();
        updatedEmployee.setAccessLevel(AccessLevel.STAYER);
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse = objectMapper.readValue(
            new File("src/test/resources/mapping/GetEmployeeResponse.json"),
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        getEmployeeResponse.setEmployeeStatus("ACTIVE");
        getEmployeeResponse.setAccessLevel("SUPER");
        EmployeeAccountRequest updateRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            EmployeeAccountRequest.class);
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .userEmail(USER_EMAIL_FROM_TOKEN)
            .build();
        uk.co.whitbread.shared.cdh.model.GetEmployeesResponse getEmployeesResponse = new uk.co.whitbread.shared.cdh.model.GetEmployeesResponse();
        getEmployeesResponse.setResults(List.of(getEmployeeResponse, getEmployeeResponse));

        when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
            .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeDataService.getEmployee(COMPANY_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, USER_EMAIL_FROM_TOKEN))
                .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeDataService.getCompanyEmployees(eq(COMPANY_ID), any(GetCompanyEmployeesQueryParams.class), eq(USER_EMAIL_FROM_TOKEN))).thenReturn(Optional.of(getEmployeesResponse));
        when(employeeMapper.toEmployee(getEmployeeResponse)).thenReturn(cdhEmployee);
        when(employeeMapper.toEmployeeAccountRequest(updatedEmployee)).thenReturn(updateRequest);

        //When
        sut.updateCdhEmployee(COMPANY_ID, EMPLOYEE_ID, updatedEmployee, cdhEmployeeDetails, LANGUAGE);

        //Then
        verify(employeeDataService).updateEmployeeAccount(COMPANY_ID, EMPLOYEE_ID, updateRequest,
            USER_EMAIL_FROM_TOKEN);
        verify(mockAuth0Service).updateUserDetailsInAuth0(cdhEmployee, updatedEmployee);
    }

    @Test
    void updateCdhEmployee_throwsEmployeeNotFoundException() {
        //Given
        Employee updatedEmployee = buildEmployee();
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .userEmail(USER_EMAIL_FROM_TOKEN)
            .build();

        when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
            .thenReturn(Optional.empty());

        //Then
        assertThatThrownBy(() -> sut.updateCdhEmployee(COMPANY_ID, EMPLOYEE_ID, updatedEmployee,
            cdhEmployeeDetails, LANGUAGE))
            .isInstanceOf(EmployeeNotFoundException.class)
            .hasMessageContaining(String.format("Employee %s from company %s was not found",
                EMPLOYEE_ID, COMPANY_ID));
    }

    @Test
    void activateCdhEmployee_emailAddressNotChanged_shouldActivateEmployee() throws IOException {
        //Given
        var updatedEmployee = buildEmployee();
        var getEmployeeResponse = uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
            .builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .emailAddress(updatedEmployee.getEmailAddress())
            .build();
        var getEmployeesResponse = uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
            .results(List.of(getEmployeeResponse)).build();
        var updateRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            EmployeeAccountRequest.class);

        when(employeeDataService.activateEmployee(anyString(), anyString(), anyString()))
            .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeDataService.getEmployees(argThat(params -> ACTIVATION_KEY.equals(params.getActivationKey())),
            eq(updatedEmployee.getEmailAddress()))).thenReturn(Optional.of(getEmployeesResponse));
        when(employeeMapper.toEmployeeAccountRequest(updatedEmployee)).thenReturn(updateRequest);

        //When
        sut.activateCdhEmployee(ACTIVATION_KEY, updatedEmployee, EMPLOYEE_ACCOUNT_ID_FROM_TOKEN);

        //Then
        verify(employeeDataService).updateEmployeeAccount(getEmployeeResponse.getCompanyAccountId(),
            getEmployeeResponse.getEmployeeAccountId(), updateRequest,
            updatedEmployee.getEmailAddress());
        verify(mockAuth0Service).saveCdhBusinessUserInAuth0(updatedEmployee.getEmailAddress(),
            updatedEmployee.getPassword(), getEmployeeResponse.getCompanyAccountId(),
            getEmployeeResponse.getEmployeeAccountId());
    }

    @Test
    void activateCdhEmployee_emailAddressChanged_shouldActivateEmployee() throws IOException {
        //Given
        var updatedEmployee = buildEmployee();
        var getEmployeeResponse = uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
            .builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .emailAddress("initial.email@mail.com")
            .build();
        var getEmployeesResponse = uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
            .results(List.of(getEmployeeResponse)).build();
        var updateRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            EmployeeAccountRequest.class);

        when(employeeDataService.activateEmployee(anyString(), anyString(), anyString())).thenReturn(
            Optional.of(getEmployeeResponse));
        when(
            employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(updatedEmployee.getEmailAddress())))
            .thenAnswer(invocation -> {
                GetEmployeesQueryParams params = invocation.getArgument(0);
                if (ACTIVATION_KEY.equals(params.getActivationKey())) {
                    return Optional.of(getEmployeesResponse);
                }
                return Optional.empty();
            });
        when(employeeMapper.toEmployeeAccountRequest(updatedEmployee)).thenReturn(updateRequest);

        //When
        sut.activateCdhEmployee(ACTIVATION_KEY, updatedEmployee, EMPLOYEE_ACCOUNT_ID_FROM_TOKEN);

        //Then
        verify(employeeDataService).updateEmployeeAccount(getEmployeeResponse.getCompanyAccountId(),
            getEmployeeResponse.getEmployeeAccountId(), updateRequest,
            updatedEmployee.getEmailAddress());
        verify(mockAuth0Service).saveCdhBusinessUserInAuth0(updatedEmployee.getEmailAddress(),
            updatedEmployee.getPassword(), getEmployeeResponse.getCompanyAccountId(),
            getEmployeeResponse.getEmployeeAccountId());
    }

    @Test
    void activateCdhEmployee_emailAddressChanged_throwsEmployeeAlreadyExistsException() throws IOException {
        //Given
        var updatedEmployee = buildEmployee();
        var getEmployeeResponse = uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
            .builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .emailAddress("initial.email@mail.com")
            .build();
        var existingEmployee = uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
            .builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .emailAddress(updatedEmployee.getEmailAddress())
            .build();
        var getEmployeesResponse = uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
            .results(List.of(getEmployeeResponse)).build();
        var existingEmployeeResponse = uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
            .results(List.of(existingEmployee)).build();
        EmployeeAccountRequest updateRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            EmployeeAccountRequest.class);

        when(employeeDataService.activateEmployee(anyString(), anyString(), anyString())).thenReturn(
            Optional.of(getEmployeeResponse));
        when(
            employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(updatedEmployee.getEmailAddress())))
            .thenAnswer(invocation -> {
                GetEmployeesQueryParams params = invocation.getArgument(0);
                if (ACTIVATION_KEY.equals(params.getActivationKey())) {
                    return Optional.of(getEmployeesResponse);
                }
                return Optional.of(existingEmployeeResponse);
            });
        when(employeeMapper.toEmployeeAccountRequest(updatedEmployee)).thenReturn(updateRequest);

        //When
        assertThatThrownBy(() -> sut.activateCdhEmployee(ACTIVATION_KEY, updatedEmployee, EMPLOYEE_ID))
            .isInstanceOf(EmployeeAlreadyExistsException.class)
            .hasMessageContaining(String.format("Email address %s is already registered",
                updatedEmployee.getEmailAddress()));
    }

    @Test
    void activateCdhEmployee_throwsEmployeeNotFoundException() {
        //Given
        Employee updatedEmployee = buildEmployee();
        when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), anyString()))
            .thenReturn(Optional.empty());

        //Then
        assertThatThrownBy(() -> sut.activateCdhEmployee(ACTIVATION_KEY, updatedEmployee, EMPLOYEE_ID))
            .isInstanceOf(EmployeeNotFoundException.class)
            .hasMessageContaining(String.format("Employee with activation key %s was not found",
                ACTIVATION_KEY));
    }

    @Test
    void activateCdhEmployee_shouldPreserveExistingCentralCardIdString_whenNotProvidedInRequest() throws IOException {
        //Given
        var updatedEmployee = buildEmployee();
        updatedEmployee.setCentralCardId(null); // Frontend doesn't send this field
        
        var existingCentralCardIdString = "COPC_a64dc80e-3dcc-4be6-89e8-8eaec68762f4";
        var getEmployeeResponse = uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
            .builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .emailAddress(updatedEmployee.getEmailAddress())
            .centralCardIdString(existingCentralCardIdString)
            .build();
        var getEmployeesResponse = uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
            .results(List.of(getEmployeeResponse)).build();
        var updateRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            EmployeeAccountRequest.class);
        updateRequest.setCentralCardIdString(null); // Simulates mapped from null Employee.centralCardId

        when(employeeDataService.activateEmployee(anyString(), anyString(), anyString()))
            .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeDataService.getEmployees(argThat(params -> ACTIVATION_KEY.equals(params.getActivationKey())),
            eq(updatedEmployee.getEmailAddress()))).thenReturn(Optional.of(getEmployeesResponse));
        when(employeeMapper.toEmployeeAccountRequest(updatedEmployee)).thenReturn(updateRequest);

        //When
        sut.activateCdhEmployee(ACTIVATION_KEY, updatedEmployee, EMPLOYEE_ACCOUNT_ID_FROM_TOKEN);

        //Then
        verify(employeeDataService).updateEmployeeAccount(
            eq(getEmployeeResponse.getCompanyAccountId()),
            eq(getEmployeeResponse.getEmployeeAccountId()),
            argThat(request -> existingCentralCardIdString.equals(request.getCentralCardIdString())),
            eq(updatedEmployee.getEmailAddress()));
    }

    @Test
    void activateCdhEmployee_shouldNotOverwriteCentralCardIdString_whenProvidedInRequest() throws IOException {
        //Given
        var updatedEmployee = buildEmployee();
        var newCentralCardIdString = "COPC_b75ed91f-4edd-5cf7-90f9-9fbed79873e5";
        updatedEmployee.setCentralCardId(newCentralCardIdString);
        
        var existingCentralCardIdString = "COPC_a64dc80e-3dcc-4be6-89e8-8eaec68762f4";
        var getEmployeeResponse = uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
            .builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .emailAddress(updatedEmployee.getEmailAddress())
            .centralCardIdString(existingCentralCardIdString)
            .build();
        var getEmployeesResponse = uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
            .results(List.of(getEmployeeResponse)).build();
        var updateRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            EmployeeAccountRequest.class);
        updateRequest.setCentralCardIdString(newCentralCardIdString);

        when(employeeDataService.activateEmployee(anyString(), anyString(), anyString()))
            .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeDataService.getEmployees(argThat(params -> ACTIVATION_KEY.equals(params.getActivationKey())),
            eq(updatedEmployee.getEmailAddress()))).thenReturn(Optional.of(getEmployeesResponse));
        when(employeeMapper.toEmployeeAccountRequest(updatedEmployee)).thenReturn(updateRequest);

        //When
        sut.activateCdhEmployee(ACTIVATION_KEY, updatedEmployee, EMPLOYEE_ACCOUNT_ID_FROM_TOKEN);

        //Then
        verify(employeeDataService).updateEmployeeAccount(
            eq(getEmployeeResponse.getCompanyAccountId()),
            eq(getEmployeeResponse.getEmployeeAccountId()),
            argThat(request -> newCentralCardIdString.equals(request.getCentralCardIdString())),
            eq(updatedEmployee.getEmailAddress()));
    }

    @Test
    void updateCdhEmployee_shouldPreserveExistingCentralCardIdString_whenNotProvidedInRequest() throws IOException {
        //Given
        Employee updatedEmployee = buildEmployee();
        updatedEmployee.setCentralCardId(null); // Frontend doesn't send this field
        updatedEmployee.setEmployeeStatus(EmployeeStatus.ACTIVE);

        var existingCentralCardIdString = "COPC_a64dc80e-3dcc-4be6-89e8-8eaec68762f4";
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse = objectMapper.readValue(
            new File("src/test/resources/mapping/GetEmployeeResponse.json"),
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        getEmployeeResponse.setCentralCardIdString(existingCentralCardIdString);
        getEmployeeResponse.setEmployeeStatus(EmployeeStatus.ACTIVE.toString());

        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .userEmail(USER_EMAIL_FROM_TOKEN)
            .build();

        EmployeeAccountRequest updateRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            EmployeeAccountRequest.class);
        updateRequest.setCentralCardIdString(null); // Simulates mapped from null Employee.centralCardId

        when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
            .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeDataService.getEmployee(COMPANY_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ACCOUNT_ID_FROM_TOKEN,
            USER_EMAIL_FROM_TOKEN)).thenReturn(Optional.of(getEmployeeResponse));
        when(employeeMapper.toEmployee(getEmployeeResponse)).thenReturn(updatedEmployee);
        when(employeeMapper.toEmployeeAccountRequest(updatedEmployee)).thenReturn(updateRequest);

        //When
        sut.updateCdhEmployee(COMPANY_ID, EMPLOYEE_ID, updatedEmployee, cdhEmployeeDetails, LANGUAGE);

        //Then
        verify(employeeDataService).updateEmployeeAccount(
            eq(COMPANY_ID),
            eq(EMPLOYEE_ID),
            argThat(request -> existingCentralCardIdString.equals(request.getCentralCardIdString())),
            eq(USER_EMAIL_FROM_TOKEN));
    }

    @Test
    void updateCdhEmployee_shouldNotOverwriteCentralCardIdString_whenProvidedInRequest() throws IOException {
        //Given
        Employee updatedEmployee = buildEmployee();
        var newCentralCardIdString = "COPC_b75ed91f-4edd-5cf7-90f9-9fbed79873e5";
        updatedEmployee.setCentralCardId(newCentralCardIdString);
        updatedEmployee.setEmployeeStatus(EmployeeStatus.ACTIVE);

        var existingCentralCardIdString = "COPC_a64dc80e-3dcc-4be6-89e8-8eaec68762f4";
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse = objectMapper.readValue(
            new File("src/test/resources/mapping/GetEmployeeResponse.json"),
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        getEmployeeResponse.setCentralCardIdString(existingCentralCardIdString);
        getEmployeeResponse.setEmployeeStatus(EmployeeStatus.ACTIVE.toString());

        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .userEmail(USER_EMAIL_FROM_TOKEN)
            .build();

        EmployeeAccountRequest updateRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            EmployeeAccountRequest.class);
        updateRequest.setCentralCardIdString(newCentralCardIdString);

        when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
            .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeDataService.getEmployee(COMPANY_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ACCOUNT_ID_FROM_TOKEN,
            USER_EMAIL_FROM_TOKEN)).thenReturn(Optional.of(getEmployeeResponse));
        when(employeeMapper.toEmployee(getEmployeeResponse)).thenReturn(updatedEmployee);
        when(employeeMapper.toEmployeeAccountRequest(updatedEmployee)).thenReturn(updateRequest);

        //When
        sut.updateCdhEmployee(COMPANY_ID, EMPLOYEE_ID, updatedEmployee, cdhEmployeeDetails, LANGUAGE);

        //Then
        verify(employeeDataService).updateEmployeeAccount(
            eq(COMPANY_ID),
            eq(EMPLOYEE_ID),
            argThat(request -> newCentralCardIdString.equals(request.getCentralCardIdString())),
            eq(USER_EMAIL_FROM_TOKEN));
    }

    @Test
    void getEmployeesFromCdh() {
        final String employeeAccountId1 = "1";
        final String employeeAccountId2 = "2";
        final String firstName = "FirstName";
        final String lastName = "LastName";

        final uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse1 =
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
                .builder()
                .employeeAccountId(employeeAccountId1)
                .firstName("FirstName")
                .lastName("LastName")
                .build();
        final uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse2 =
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
                .builder()
                .employeeAccountId(employeeAccountId2)
                .build();
        final uk.co.whitbread.shared.cdh.model.GetEmployeesResponse getEmployeesResponse =
            uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
                .results(List.of(getEmployeeResponse1, getEmployeeResponse2)).build();

        final EmployeeSummary expectedEmployeeSummary1 = new EmployeeSummary();
        expectedEmployeeSummary1.setId(employeeAccountId1);
        expectedEmployeeSummary1.setFirstName(firstName);
        expectedEmployeeSummary1.setLastName(lastName);
        final EmployeeSummary expectedEmployeeSummary2 = new EmployeeSummary();
        expectedEmployeeSummary2.setId(employeeAccountId2);

        final GetEmployeesResponse expectedGetEmployeesResponse = GetEmployeesResponse.builder()
            .employees(List.of(expectedEmployeeSummary1))
            .build();

        doReturn(Optional.of(getEmployeesResponse))
            .when(employeeDataService)
            .getCompanyEmployees(anyString(), any(GetCompanyEmployeesQueryParams.class),
                anyString());
        doReturn(GetCompanyEmployeesQueryParams.builder().build()).when(employeeMapper)
            .toGetCompanyEmployeesQueryParams(any(GetEmployeesRequest.class));
        doReturn(expectedGetEmployeesResponse).when(employeeMapper)
            .toGetEmployeesResponse(getEmployeesResponse);

        final GetEmployeesResponse employeesFromCdh = sut.getEmployeesFromCdh(
            new GetEmployeesRequest(), COMPANY_ID, USER_EMAIL);

        final GetEmployeesResponse employeesFromCdhEmpty = sut.getEmployeesFromCdh(
                null, COMPANY_ID, USER_EMAIL);

        assertThat(employeesFromCdh.getEmployees()).hasSize(1);
        assertThat(employeesFromCdh.getEmployees().get(0)).isEqualTo(expectedEmployeeSummary1);
        assertThat(employeesFromCdhEmpty.getEmployees()).isEmpty();
    }

    @Test
    void getCdhEmployee_Success(){
        //Given
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse =
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
                .builder()
                .employeeAccountId(EMPLOYEE_ID)
                .companyAccountId(COMPANY_ID)
                .build();
        Employee employee = new Employee();
        employee.setId(EMPLOYEE_ID);

        when(employeeDataService.getEmployee(eq(COMPANY_ID), eq(EMPLOYEE_ID), anyString()))
            .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeMapper.toEmployee(getEmployeeResponse))
            .thenReturn(employee);

        //when
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse response = sut.getCdhEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL);

        //Then
        assertNotNull(response);
        assertEquals(EMPLOYEE_ID, Mappers.getMapper(EmployeeMapper.class).toEmployee(response).getId());
    }

    @Test
    void getCdhEmployee_throwsNotFoundException() {
        //Given
        when(employeeDataService.getEmployee(eq(COMPANY_ID), eq(EMPLOYEE_ID), anyString()))
            .thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> sut.getCdhEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL))
            .isInstanceOf(EmployeeNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode", "2515");
    }

    @Test
    void addCdhEmployee_ShouldMakeRequestAndReturnEmployeeId() throws IOException {

        //Given
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel(SUPER_ACCESS_LEVEL)
            .build();
        Employee employee = buildEmployee();
        EmployeeAccountRequest employeeAccountRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            EmployeeAccountRequest.class);
        when(employeeMapper.toEmployeeAccountRequest(employee)).thenReturn(employeeAccountRequest);
        EmployeeAccountResponse employeeAccountResponse = EmployeeAccountResponse.builder()
            .employeeAccountId(EMPLOYEE_ID).build();
        when(employeeDataService
            .createEmployeeAccount(COMPANY_ID, employeeAccountRequest, USER_EMAIL_FROM_TOKEN))
            .thenReturn(employeeAccountResponse);

        // When
        String actual = sut.addCdhEmployee(COMPANY_ID, employee, cdhEmployeeDetails, LANGUAGE);

        //Then
        assertThat(actual).isEqualTo(EMPLOYEE_ID);
      verify(employeeMapper)
          .toEmployeeAccountRequest(employee);
        verify(employeeDataService)
            .createEmployeeAccount(COMPANY_ID, employeeAccountRequest, USER_EMAIL_FROM_TOKEN);
    }

    @Test
    void addEmployee_shouldSendEmployeeActivationEmailForBB() {
        // Given
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel(SUPER_ACCESS_LEVEL)
            .build();
        employee.setInnBusiness(false);
        doReturn(EmployeeAccountRequest.builder().build()).when(employeeMapper)
            .toEmployeeAccountRequest(any(Employee.class));
        final EmployeeAccountResponse employeeAccountResponse = EmployeeAccountResponse.builder()
            .employeeAccountId(UUIDGenerator.getUUID()).build();
        doReturn(employeeAccountResponse).when(employeeDataService)
            .createEmployeeAccount(anyString(), any(EmployeeAccountRequest.class), anyString());
        final uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse =
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.builder()
                .activationKey(ACTIVATION_KEY).build();
        doReturn(Optional.of(getEmployeeResponse)).when(employeeDataService)
            .getEmployee(anyString(), anyString(), anyString());

        // When
        sut.addCdhEmployee(COMPANY_ID, employee, cdhEmployeeDetails, LANGUAGE);

        // Then
        verify(sut).sendAsyncEmployeeActivationEmail(anyString(), anyString(), anyString(),
            anyString(), anyBoolean());
    }

    @Test
    void addEmployee_shouldSendEmployeeActivationEmailForInnB() {
        // Given
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel(SUPER_ACCESS_LEVEL)
            .build();
        employee.setInnBusiness(true);
        doReturn(EmployeeAccountRequest.builder().build()).when(employeeMapper)
            .toEmployeeAccountRequest(any(Employee.class));
        final EmployeeAccountResponse employeeAccountResponse = EmployeeAccountResponse.builder()
            .employeeAccountId(UUIDGenerator.getUUID()).build();
        doReturn(employeeAccountResponse).when(employeeDataService)
            .createEmployeeAccount(anyString(), any(EmployeeAccountRequest.class), anyString());
        final uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse =
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.builder()
                .activationKey(ACTIVATION_KEY).build();
        doReturn(Optional.of(getEmployeeResponse)).when(employeeDataService)
            .getEmployee(anyString(), anyString(), anyString());

        // When
        sut.addCdhEmployee(COMPANY_ID, employee, cdhEmployeeDetails, LANGUAGE);

        // Then
        verify(sut).sendAsyncEmployeeActivationEmail(anyString(), anyString(), anyString(),
            anyString(), anyBoolean());
    }

    @Test
    void updateCdhEmployeeAccessLevel_ShouldMakeRequest() throws IOException {
        //Given
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse =
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
                .builder()
                .employeeAccountId(EMPLOYEE_ID)
                .companyAccountId(COMPANY_ID)
                .accessLevel("BOOKER")
                .build();

        when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
            .thenReturn(Optional.of(getEmployeeResponse));
        EmployeeAccountRequest employeeAccountRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            EmployeeAccountRequest.class);
        when(employeeMapper.toEmployeeAccountRequest(getEmployeeResponse))
            .thenReturn(employeeAccountRequest);

        //When
        sut.updateCdhEmployeeAccessLevel(COMPANY_ID, EMPLOYEE_ID, AccessLevel.STAYER,
            USER_EMAIL_FROM_TOKEN, LANGUAGE);

        //Then
        verify(employeeDataService).getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN);
        verify(employeeMapper).toEmployeeAccountRequest(getEmployeeResponse);
        verify(employeeDataService)
            .updateEmployeeAccount(COMPANY_ID, EMPLOYEE_ID, employeeAccountRequest,
                USER_EMAIL_FROM_TOKEN);
    }


    @ParameterizedTest
    @CsvSource({"ACTIVE, DEACTIVATED", "INACTIVE, ACTIVE"})
    void updateCdhEmployee_checkStatus_successRequest(EmployeeStatus currentStatus, EmployeeStatus updatedStatus) throws IOException {
        //Given
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse =
                objectMapper.readValue(
                        new File("src/test/resources/mapping/GetEmployeeResponse.json"),
                        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        Employee updatedEmployee = buildEmployee();
        Employee cdhEmployee = buildEmployee();
        updatedEmployee.setEmployeeStatus(updatedStatus);

        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
                .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
                .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
                .userEmail(USER_EMAIL_FROM_TOKEN)
                .build();

        getEmployeeResponse.setAccessLevel("SUPER");
        getEmployeeResponse.setEmployeeStatus(currentStatus.getValue());

        EmployeeAccountRequest employeeAccountRequest = objectMapper.readValue(
                new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
                EmployeeAccountRequest.class);

        when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
                .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeDataService.getEmployee(COMPANY_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, USER_EMAIL_FROM_TOKEN))
                .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeMapper.toEmployee(getEmployeeResponse)).thenReturn(cdhEmployee);
        when(employeeMapper.toEmployeeAccountRequest(updatedEmployee)).thenReturn(employeeAccountRequest);

        //When
        sut.updateCdhEmployee(COMPANY_ID, EMPLOYEE_ID, updatedEmployee, cdhEmployeeDetails, LANGUAGE);

        //Then
        verify(employeeDataService).updateEmployeeAccount(COMPANY_ID, EMPLOYEE_ID, employeeAccountRequest,
                USER_EMAIL_FROM_TOKEN);
    }

    @ParameterizedTest
    @CsvSource({"ACTIVE, INACTIVE", "DEACTIVATED, ACTIVE"})
    void updateCdhEmployee_checkStatus_invalid(EmployeeStatus currentStatus, EmployeeStatus updatedStatus) throws IOException {
        //Given
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse =
                objectMapper.readValue(
                        new File("src/test/resources/mapping/GetEmployeeResponse.json"),
                        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        Employee updatedEmployee = buildEmployee();
        Employee cdhEmployee = buildEmployee();
        updatedEmployee.setEmployeeStatus(updatedStatus);

        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
                .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
                .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
                .userEmail(USER_EMAIL_FROM_TOKEN)
                .build();

        getEmployeeResponse.setAccessLevel("SUPER");
        getEmployeeResponse.setEmployeeStatus(currentStatus.getValue());

        EmployeeAccountRequest employeeAccountRequest = objectMapper.readValue(
                new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
                EmployeeAccountRequest.class);

        when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
                .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeDataService.getEmployee(COMPANY_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, USER_EMAIL_FROM_TOKEN))
                .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeMapper.toEmployee(getEmployeeResponse)).thenReturn(cdhEmployee);
        when(employeeMapper.toEmployeeAccountRequest(updatedEmployee)).thenReturn(employeeAccountRequest);

        //When
        assertThatThrownBy(
                () -> sut.updateCdhEmployee(COMPANY_ID, EMPLOYEE_ID, updatedEmployee, cdhEmployeeDetails, LANGUAGE))
                .isInstanceOf(RestrictedOperationException.class)
                .hasFieldOrPropertyWithValue("errorCode", RestrictedOperationException.ERROR_CODE)
                .hasMessageContaining(String.format("Changing status to %s is not allowed.", updatedStatus.getValue()));
    }

    @Test
    void updateCdhEmployeeAccessLevel_whenUserNotFound_shouldThrowError() {
        assertThatThrownBy(() -> sut
            .updateCdhEmployeeAccessLevel(COMPANY_ID, EMPLOYEE_ID, AccessLevel.STAYER,
                USER_EMAIL_FROM_TOKEN, LANGUAGE))
            .isInstanceOf(EmployeeNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode", ERROR_CODE)
            .hasMessageContaining("was not found");
    }

    @Test
    void updateCdhEmployeeAccessLevel_whenLastTravelManagerValidationFailed_shouldThrowError() {
        //Given
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse =
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
                .builder()
                .employeeAccountId(EMPLOYEE_ID)
                .companyAccountId(COMPANY_ID)
                .accessLevel("SUPER")
                .employeeStatus("ACTIVE")
                .build();

        uk.co.whitbread.shared.cdh.model.GetEmployeesResponse getEmployeesResponse = new uk.co.whitbread.shared.cdh.model.GetEmployeesResponse();
        getEmployeesResponse.setResults(List.of(getEmployeeResponse));

        when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
            .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeDataService.getCompanyEmployees(eq(COMPANY_ID), any(GetCompanyEmployeesQueryParams.class), eq(USER_EMAIL_FROM_TOKEN)))
            .thenReturn(Optional.of(getEmployeesResponse));

        //Then
        assertThatThrownBy(() -> sut.updateCdhEmployeeAccessLevel(COMPANY_ID, EMPLOYEE_ID, AccessLevel.STAYER,
            USER_EMAIL_FROM_TOKEN, LANGUAGE))
            .isInstanceOf(RemoveLastTravelManagerRestrictedOperationException.class)
            .hasFieldOrPropertyWithValue("errorCode", RemoveLastTravelManagerRestrictedOperationException.ERROR_CODE)
            .hasMessageContaining("There must be at least one Travel Manager on this account.");
    }

    @Test
    void updateCdhEmployeeAccessLevel_whenLastTravelManagerValidationPassed_ShouldMakeRequest() throws IOException {
        //Given
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse =
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
                .builder()
                .employeeAccountId(EMPLOYEE_ID)
                .companyAccountId(COMPANY_ID)
                .accessLevel("SUPER")
                .employeeStatus("ACTIVE")
                .build();

        uk.co.whitbread.shared.cdh.model.GetEmployeesResponse getEmployeesResponse = new uk.co.whitbread.shared.cdh.model.GetEmployeesResponse();
        getEmployeesResponse.setResults(List.of(getEmployeeResponse, getEmployeeResponse));

        when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
            .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeDataService.getCompanyEmployees(eq(COMPANY_ID), any(GetCompanyEmployeesQueryParams.class), eq(USER_EMAIL_FROM_TOKEN)))
            .thenReturn(Optional.of(getEmployeesResponse));
        EmployeeAccountRequest employeeAccountRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            EmployeeAccountRequest.class);
        when(employeeMapper.toEmployeeAccountRequest(getEmployeeResponse))
            .thenReturn(employeeAccountRequest);

        //When
        sut.updateCdhEmployeeAccessLevel(COMPANY_ID, EMPLOYEE_ID, AccessLevel.STAYER,
            USER_EMAIL_FROM_TOKEN, LANGUAGE);

        //Then
        verify(employeeDataService).getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN);
        verify(employeeMapper).toEmployeeAccountRequest(getEmployeeResponse);
        verify(employeeDataService)
            .updateEmployeeAccount(COMPANY_ID, EMPLOYEE_ID, employeeAccountRequest,
                USER_EMAIL_FROM_TOKEN);
    }

    @Test
    void inviteCdhEmployee_BpManager_cdhReturnsEmptyEmployeeList_Success() {
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel(BUSINESS_PAY_MANAGER_ACCESS_LEVEL)
            .build();

        final EmployeeAccountResponse response = EmployeeAccountResponse.builder()
            .employeeAccountId(EMPLOYEE_ID)
            .activationKey(ACTIVATION_KEY)
            .build();

        when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(USER_EMAIL)))
            .thenReturn(Optional.of(uk.co.whitbread.shared.cdh.model.GetEmployeesResponse
                .builder()
                .results(List.of())
                .build()));
        when(employeeDataService.createEmployeeAccount(eq(COMPANY_ID),
            any(EmployeeAccountRequest.class),
            eq(USER_EMAIL))).thenReturn(response);

        // Then
        sut.inviteCdhEmployee(COMPANY_ID, inviteRequest, cdhEmployeeDetails, LANGUAGE);
        verify(employeeDataService).createEmployeeAccount(eq(COMPANY_ID), any(EmployeeAccountRequest.class),
            eq(USER_EMAIL));
    }

    @Test
    void inviteCdhEmployee_InvalidManager_thenInvalidOperationThrown() {
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel("BUSINESS_PAY_USER")
            .build();

        final EmployeeAccountResponse response = EmployeeAccountResponse.builder()
            .employeeAccountId(EMPLOYEE_ID)
            .activationKey(ACTIVATION_KEY)
            .build();

        when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(USER_EMAIL)))
            .thenReturn(Optional.of(uk.co.whitbread.shared.cdh.model.GetEmployeesResponse
                .builder()
                .results(List.of())
                .build()));
        when(employeeDataService.createEmployeeAccount(eq(COMPANY_ID),
            any(EmployeeAccountRequest.class),
            eq(USER_EMAIL))).thenReturn(response);

        // Then
        assertThatThrownBy(
            () -> sut.inviteCdhEmployee(COMPANY_ID, inviteRequest, cdhEmployeeDetails, LANGUAGE))
            .isInstanceOf(InvalidOperationException.class)
            .hasMessageContaining(
                "The manager access level is not valid.");
    }

    @Test
    void inviteCdhEmployee_cdhReturnsEmptyEmployeeList_Success() {
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel(SUPER_ACCESS_LEVEL)
            .build();

        final EmployeeAccountResponse response = EmployeeAccountResponse.builder()
            .employeeAccountId(EMPLOYEE_ID)
            .activationKey(ACTIVATION_KEY)
            .build();

        when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(USER_EMAIL)))
            .thenReturn(Optional.of(uk.co.whitbread.shared.cdh.model.GetEmployeesResponse
                .builder()
                .results(List.of())
                .build()));
        when(employeeDataService.createEmployeeAccount(eq(COMPANY_ID),
            any(EmployeeAccountRequest.class),
            eq(USER_EMAIL))).thenReturn(response);

        // Then
        sut.inviteCdhEmployee(COMPANY_ID, inviteRequest, cdhEmployeeDetails, LANGUAGE);
        verify(employeeDataService).createEmployeeAccount(eq(COMPANY_ID), any(EmployeeAccountRequest.class),
            eq(USER_EMAIL));
    }


    @ParameterizedTest
    @ValueSource(strings = {"en", "de"})
    void inviteInnBEmployee_cdhReturnsEmptyEmployeeList_Success(String language) {
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel(SUPER_ACCESS_LEVEL)
            .build();
        final EmployeeAccountResponse response = EmployeeAccountResponse.builder()
            .employeeAccountId(EMPLOYEE_ID)
            .activationKey(ACTIVATION_KEY)
            .build();
        inviteRequest.setInnBusiness(true);

        when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(USER_EMAIL)))
            .thenReturn(Optional.of(uk.co.whitbread.shared.cdh.model.GetEmployeesResponse
                .builder()
                .results(List.of())
                .build()));
        when(employeeDataService.createEmployeeAccount(eq(COMPANY_ID),
            any(EmployeeAccountRequest.class),
            eq(USER_EMAIL))).thenReturn(response);

        // Then
        sut.inviteCdhEmployee(COMPANY_ID, inviteRequest, cdhEmployeeDetails, language);
        verify(employeeDataService).createEmployeeAccount(eq(COMPANY_ID), any(EmployeeAccountRequest.class),
            eq(USER_EMAIL));
    }

    @Test
    void inviteCdhEmployee_employeeAlreadyRegisteredInSameCompanyWithStatusActive_throwsEmployeeAlreadyExistsException()
        throws IOException {
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel(SUPER_ACCESS_LEVEL)
            .build();
        var getEmployeeResponse = objectMapper.readValue(
            new File("src/test/resources/mapping/CdhGetEmployeeResponse.json"),
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        getEmployeeResponse.setEmployeeStatus(EmployeeStatus.ACTIVE.toString());
        getEmployeeResponse.setCompanyAccountId(COMPANY_ID);

        var getEmployeesResponse = uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
            .results(List.of(getEmployeeResponse)).build();

        when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class),
            eq(USER_EMAIL))).thenReturn(Optional.of(getEmployeesResponse));

        assertThatThrownBy(
            () -> sut.inviteCdhEmployee(COMPANY_ID, inviteRequest, cdhEmployeeDetails, LANGUAGE))
            .isInstanceOf(EmployeeAlreadyExistsException.class)
            .hasMessageContaining(
                "Email address " + inviteRequest.getEmailAddress() + " is already registered.");
    }

    @Test
    void inviteCdhEmployee_employeeAlreadyRegisteredInSameCompanyWithStatusInactive_generateNewActivationKey()
        throws IOException {
        var getEmployeeResponse = objectMapper.readValue(
            new File("src/test/resources/mapping/CdhGetEmployeeResponse.json"),
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        var generateActivationKeyRequest = GenerateActivationKeyRequest.builder()
            .employeeAccountId(getEmployeeResponse.getEmployeeAccountId())
            .build();
        var response = GenerateActivationKeyResponse.builder()
            .activationKey(ACTIVATION_KEY)
            .build();
        getEmployeeResponse.setEmployeeStatus("inactive");
        getEmployeeResponse.setCompanyAccountId(COMPANY_ID);
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel(SUPER_ACCESS_LEVEL)
            .build();

        var getEmployeesResponse = uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
            .results(List.of(getEmployeeResponse)).build();

        when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(USER_EMAIL)))
            .thenReturn(Optional.of(getEmployeesResponse));
        when(employeeDataService.generateNewActivationKey(COMPANY_ID,
            getEmployeeResponse.getEmployeeAccountId(), generateActivationKeyRequest, USER_EMAIL))
            .thenReturn(response);

        sut.inviteCdhEmployee(COMPANY_ID, inviteRequest, cdhEmployeeDetails, LANGUAGE);

        verify(employeeDataService).generateNewActivationKey(COMPANY_ID,
            getEmployeeResponse.getEmployeeAccountId(), generateActivationKeyRequest, USER_EMAIL);
    }

    @ParameterizedTest
    @ValueSource(strings = {"en", "de"})
    void inviteInnBEmployee_employeeAlreadyRegisteredInSameCompanyWithStatusInactive_generateNewActivationKey()
        throws IOException {
        var getEmployeeResponse = objectMapper.readValue(
            new File("src/test/resources/mapping/CdhGetEmployeeResponse.json"),
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        var generateActivationKeyRequest = GenerateActivationKeyRequest.builder()
            .employeeAccountId(getEmployeeResponse.getEmployeeAccountId())
            .build();
        var response = GenerateActivationKeyResponse.builder()
            .activationKey(ACTIVATION_KEY)
            .build();
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel(SUPER_ACCESS_LEVEL)
            .build();
        getEmployeeResponse.setEmployeeStatus("inactive");
        getEmployeeResponse.setCompanyAccountId(COMPANY_ID);
        inviteRequest.setInnBusiness(true);

        var getEmployeesResponse = uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
            .results(List.of(getEmployeeResponse)).build();

        when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(USER_EMAIL)))
            .thenReturn(Optional.of(getEmployeesResponse));
        when(employeeDataService.generateNewActivationKey(COMPANY_ID,
            getEmployeeResponse.getEmployeeAccountId(), generateActivationKeyRequest, USER_EMAIL))
            .thenReturn(response);

        sut.inviteCdhEmployee(COMPANY_ID, inviteRequest, cdhEmployeeDetails, LANGUAGE);

        verify(employeeDataService).generateNewActivationKey(COMPANY_ID,
            getEmployeeResponse.getEmployeeAccountId(), generateActivationKeyRequest, USER_EMAIL);
    }

    @Test
    void inviteCdhEmployee_HandleEmployeeAlreadyRegisteredInOtherCompany_throwsEmployeeAlreadyExistsException()
        throws IOException {
        var getEmployeeResponse = objectMapper.readValue(
            new File("src/test/resources/mapping/CdhGetEmployeeResponse.json"),
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel(SUPER_ACCESS_LEVEL)
            .build();

        var getEmployeesRespnose = uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
            .results(List.of(getEmployeeResponse)).build();

        when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class),
            eq(USER_EMAIL))).thenReturn(Optional.of(getEmployeesRespnose));

        assertThatThrownBy(
            () -> sut.inviteCdhEmployee(COMPANY_ID, inviteRequest, cdhEmployeeDetails, LANGUAGE))
            .isInstanceOf(EmployeeAlreadyExistsException.class)
            .hasMessageContaining("Email address " + inviteRequest.getEmailAddress()
                + " is registered with another company.");
    }

    @Test
    void inviteCdhEmployee_cdhThrowsEmployeeNotFoundException_Success() {
        final EmployeeAccountResponse response = EmployeeAccountResponse.builder()
            .employeeAccountId(EMPLOYEE_ID)
            .activationKey(ACTIVATION_KEY)
            .build();
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel(SUPER_ACCESS_LEVEL)
            .build();

        String cdhErrorMessage = "No Employee Account records could be found with the provided criteria";

        when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(USER_EMAIL)))
            .thenThrow(new CDHException(HttpStatus.SC_NOT_FOUND, cdhErrorMessage, null));
        when(employeeDataService.createEmployeeAccount(eq(COMPANY_ID), any(EmployeeAccountRequest.class),
            eq(USER_EMAIL))).thenReturn(response);

        sut.inviteCdhEmployee(COMPANY_ID, inviteRequest, cdhEmployeeDetails, LANGUAGE);
        verify(employeeDataService).createEmployeeAccount(eq(COMPANY_ID), any(EmployeeAccountRequest.class),
            eq(USER_EMAIL));
    }

    @Test
    void inviteCdhEmployee_InviteEmployeeEmployeeAlreadyRegisteredInSameCompany_generateNewActivationKey()
        throws IOException {
        var getEmployeeResponse = objectMapper.readValue(
            new File("src/test/resources/mapping/CdhGetEmployeeResponse.json"),
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        var generateActivationKeyRequest = GenerateActivationKeyRequest.builder()
            .employeeAccountId(getEmployeeResponse.getEmployeeAccountId())
            .build();
        var response = GenerateActivationKeyResponse.builder()
            .activationKey(ACTIVATION_KEY)
            .build();
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel(SUPER_ACCESS_LEVEL)
            .build();
        getEmployeeResponse.setEmployeeStatus(EmployeeStatus.INACTIVE.toString());
        getEmployeeResponse.setCompanyAccountId(COMPANY_ID);

        var getEmployeesResponse = uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
            .results(List.of(getEmployeeResponse)).build();

        when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(USER_EMAIL)))
            .thenReturn(Optional.of(getEmployeesResponse));
        when(employeeDataService.generateNewActivationKey(COMPANY_ID,
            getEmployeeResponse.getEmployeeAccountId(), generateActivationKeyRequest, USER_EMAIL))
            .thenReturn(response);

        sut.inviteCdhEmployee(COMPANY_ID, inviteRequest, cdhEmployeeDetails, LANGUAGE);

        verify(employeeDataService).generateNewActivationKey(COMPANY_ID,
            getEmployeeResponse.getEmployeeAccountId(), generateActivationKeyRequest, USER_EMAIL);
    }
    @Test
    void inviteInnBEmployee_InviteEmployeeEmployeeAlreadyRegisteredInSameCompany_generateNewActivationKey()
        throws IOException {
        var getEmployeeResponse = objectMapper.readValue(
            new File("src/test/resources/mapping/CdhGetEmployeeResponse.json"),
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        var generateActivationKeyRequest = GenerateActivationKeyRequest.builder()
            .employeeAccountId(getEmployeeResponse.getEmployeeAccountId())
            .build();
        var response = GenerateActivationKeyResponse.builder()
            .activationKey(ACTIVATION_KEY)
            .build();
        getEmployeeResponse.setEmployeeStatus(EmployeeStatus.INACTIVE.toString());
        getEmployeeResponse.setCompanyAccountId(COMPANY_ID);
        inviteRequest.setInnBusiness(true);
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel(SUPER_ACCESS_LEVEL)
            .build();

        var getEmployeesResponse = uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
            .results(List.of(getEmployeeResponse)).build();

        when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(USER_EMAIL)))
            .thenReturn(Optional.of(getEmployeesResponse));
        when(employeeDataService.generateNewActivationKey(COMPANY_ID,
            getEmployeeResponse.getEmployeeAccountId(), generateActivationKeyRequest, USER_EMAIL))
            .thenReturn(response);

        sut.inviteCdhEmployee(COMPANY_ID, inviteRequest, cdhEmployeeDetails, LANGUAGE);

        verify(employeeDataService).generateNewActivationKey(COMPANY_ID,
            getEmployeeResponse.getEmployeeAccountId(), generateActivationKeyRequest, USER_EMAIL);
    }

    @Test
    void inviteCdhEmployee_cdhThrowsGenericException_throwsCdhException() {
        when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class),
            eq(USER_EMAIL))).thenThrow(new CDHException(HttpStatus.SC_INTERNAL_SERVER_ERROR,
            Strings.EMPTY, null));
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel(SUPER_ACCESS_LEVEL)
            .build();

        assertThatThrownBy(
            () -> sut.inviteCdhEmployee(COMPANY_ID, inviteRequest, cdhEmployeeDetails, LANGUAGE))
            .isInstanceOf(CDHException.class);
        verify(employeeDataService, times(0)).createEmployeeAccount(eq(COMPANY_ID),
            any(EmployeeAccountRequest.class), eq(USER_EMAIL));
    }

    @Test
    void getEmployeesFromCdhWithoutFiltering() {
        //arrange
        final String employeeAccountId1 = "1";

        final uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse1 =
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
                .builder()
                .employeeAccountId(employeeAccountId1)
                .build();

        final uk.co.whitbread.shared.cdh.model.GetEmployeesResponse getEmployeesResponse =
            uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
                .results(List.of(getEmployeeResponse1)).build();

        final EmployeeSummary expectedEmployeeSummary1 = new EmployeeSummary();
        expectedEmployeeSummary1.setId(employeeAccountId1);

        final GetEmployeesResponse expectedGetEmployeesResponse = GetEmployeesResponse.builder()
            .employees(List.of(expectedEmployeeSummary1))
            .build();

        doReturn(Optional.of(getEmployeesResponse))
            .when(employeeDataService)
            .getCompanyEmployees(anyString(), any(GetCompanyEmployeesQueryParams.class),
                anyString());
        doReturn(GetCompanyEmployeesQueryParams.builder().build()).when(employeeMapper)
            .toGetCompanyEmployeesQueryParams(any(GetEmployeesRequest.class));
        doReturn(expectedGetEmployeesResponse).when(employeeMapper)
            .toGetEmployeesResponse(getEmployeesResponse);

        var requestParams = new GetEmployeesRequest();
        requestParams.setShouldFilterEmployees(false);

        //act
        final GetEmployeesResponse employeesFromCdh = sut.getEmployeesFromCdh(
            requestParams, COMPANY_ID, USER_EMAIL);

        //assert
        assertThat(employeesFromCdh.getEmployees()).hasSize(1);
        assertThat(employeesFromCdh.getEmployees().get(0)).isEqualTo(expectedEmployeeSummary1);
    }

    @Test
    void updateCdhEmployee_deleteUserThrowsAuth0ApiException_shouldThrowAuthServiceException() throws Exception {
        //Given
        Employee updatedEmployee = buildEmployee();
        updatedEmployee.setEmployeeStatus(EmployeeStatus.PURGED);
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .userEmail(USER_EMAIL_FROM_TOKEN)
            .build();
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse = objectMapper.readValue(
            new File("src/test/resources/mapping/GetEmployeeResponse.json"),
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);
        when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
            .thenReturn(Optional.of(getEmployeeResponse));
        doThrow(new Auth0ApiException("Auth0 error", 500, "error", "description"))
            .when(mockAuth0Service).deleteUser(updatedEmployee.getEmailAddress());

        //When & Then
        assertThatThrownBy(
            () -> sut.updateCdhEmployee(COMPANY_ID, EMPLOYEE_ID, updatedEmployee, cdhEmployeeDetails, LANGUAGE))
            .isInstanceOf(AuthServiceException.class)
            .hasMessageContaining("Could not delete user")
            .hasMessageContaining(updatedEmployee.getEmailAddress());

        verify(employeeDataService, times(0)).deleteEmployeeAccount(anyString(), anyString(), anyString());
    }

    @Test
    void activateCdhEmployee_saveCdhBusinessUserThrowsAuth0ApiException_shouldThrowAuthServiceException()
        throws IOException {
        //Given
        var updatedEmployee = buildEmployee();
        var getEmployeeResponse = uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
            .builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .emailAddress(updatedEmployee.getEmailAddress())
            .build();
        var getEmployeesResponse = uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
            .results(List.of(getEmployeeResponse)).build();
        var updateRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            EmployeeAccountRequest.class);

        when(employeeDataService.activateEmployee(anyString(), anyString(), anyString()))
            .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeDataService.getEmployees(argThat(params -> ACTIVATION_KEY.equals(params.getActivationKey())),
            eq(updatedEmployee.getEmailAddress()))).thenReturn(Optional.of(getEmployeesResponse));
        when(employeeMapper.toEmployeeAccountRequest(updatedEmployee)).thenReturn(updateRequest);
        doThrow(new Auth0ApiException("Auth0 error", 500, "error", "description"))
            .when(mockAuth0Service).saveCdhBusinessUserInAuth0(
                updatedEmployee.getEmailAddress(), updatedEmployee.getPassword(),
                getEmployeeResponse.getCompanyAccountId(), getEmployeeResponse.getEmployeeAccountId());

        //When & Then
        assertThatThrownBy(
            () -> sut.activateCdhEmployee(ACTIVATION_KEY, updatedEmployee, EMPLOYEE_ACCOUNT_ID_FROM_TOKEN))
            .isInstanceOf(AuthServiceException.class)
            .hasMessageContaining("Could not save user")
            .hasMessageContaining(updatedEmployee.getEmailAddress());
    }

    @Test
    void getEmployeesFromCdh_withFeatureFlagEnabled() {
        final String employeeAccountId1 = "1";
        final String employeeAccountId2 = "2";
        final String firstName = "FirstName";
        final String lastName = "LastName";

        final uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse1 =
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
                .builder()
                .employeeAccountId(employeeAccountId1)
                .firstName("FirstName")
                .lastName("LastName")
                .build();
        final uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse2 =
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
                .builder()
                .employeeAccountId(employeeAccountId2)
                .build();
        final uk.co.whitbread.shared.cdh.model.GetEmployeesResponse getEmployeesResponse =
            uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
                .results(List.of(getEmployeeResponse1, getEmployeeResponse2)).build();

        final EmployeeSummary expectedEmployeeSummary1 = new EmployeeSummary();
        expectedEmployeeSummary1.setId(employeeAccountId1);
        expectedEmployeeSummary1.setFirstName(firstName);
        expectedEmployeeSummary1.setLastName(lastName);
        final EmployeeSummary expectedEmployeeSummary2 = new EmployeeSummary();
        expectedEmployeeSummary2.setId(employeeAccountId2);

        final GetEmployeesResponse expectedGetEmployeesResponse = GetEmployeesResponse.builder()
            .employees(List.of(expectedEmployeeSummary1))
            .build();

        when(unleashWrapper.isEnabled(any())).thenReturn(true);
        doReturn(Optional.of(getEmployeesResponse))
            .when(employeeDataService)
            .getCompanyEmployeesV2(anyString(), any(GetCompanyEmployeesQueryParams.class),
                anyString());
        doReturn(GetCompanyEmployeesQueryParams.builder().build()).when(employeeMapper)
            .toGetCompanyEmployeesQueryParams(any(GetEmployeesRequest.class));
        doReturn(expectedGetEmployeesResponse).when(employeeMapper)
            .toGetEmployeesResponse(getEmployeesResponse);

        final GetEmployeesResponse employeesFromCdh = sut.getEmployeesFromCdh(
            new GetEmployeesRequest(), COMPANY_ID, USER_EMAIL);

        assertThat(employeesFromCdh.getEmployees()).hasSize(1);
        assertThat(employeesFromCdh.getEmployees().get(0)).isEqualTo(expectedEmployeeSummary1);
        verify(employeeDataService).getCompanyEmployeesV2(anyString(),
            any(GetCompanyEmployeesQueryParams.class), anyString());
    }

    @Test
    void activateCdhEmployee_withFeatureFlagEnabled_emailAddressNotChanged_shouldActivateEmployee()
        throws IOException {
        //Given
        var updatedEmployee = buildEmployee();
        var getEmployeeResponse = uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
            .builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .emailAddress(updatedEmployee.getEmailAddress())
            .build();
        var getEmployeesResponse = uk.co.whitbread.shared.cdh.model.GetEmployeesResponse.builder()
            .results(List.of(getEmployeeResponse)).build();
        var updateRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            EmployeeAccountRequest.class);

        when(unleashWrapper.isEnabled(any())).thenReturn(true);
        when(employeeDataService.activateEmployee(anyString(), anyString(), anyString()))
            .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeDataService.getEmployeesV2(
            argThat(params -> ACTIVATION_KEY.equals(params.getActivationKey())),
            eq(updatedEmployee.getEmailAddress()))).thenReturn(Optional.of(getEmployeesResponse));
        when(employeeMapper.toEmployeeAccountRequest(updatedEmployee)).thenReturn(updateRequest);

        //When
        sut.activateCdhEmployee(ACTIVATION_KEY, updatedEmployee, EMPLOYEE_ACCOUNT_ID_FROM_TOKEN);

        //Then
        verify(employeeDataService).getEmployeesV2(
            argThat(params -> ACTIVATION_KEY.equals(params.getActivationKey())),
            eq(updatedEmployee.getEmailAddress()));
        verify(employeeDataService).updateEmployeeAccount(getEmployeeResponse.getCompanyAccountId(),
            getEmployeeResponse.getEmployeeAccountId(), updateRequest,
            updatedEmployee.getEmailAddress());
        verify(mockAuth0Service).saveCdhBusinessUserInAuth0(updatedEmployee.getEmailAddress(),
            updatedEmployee.getPassword(), getEmployeeResponse.getCompanyAccountId(),
            getEmployeeResponse.getEmployeeAccountId());
    }

    @Test
    void activateCdhEmployee_withFeatureFlagEnabled_throwsEmployeeNotFoundException() {
        //Given
        Employee updatedEmployee = buildEmployee();
        when(unleashWrapper.isEnabled(any())).thenReturn(true);
        when(employeeDataService.getEmployeesV2(any(GetEmployeesQueryParams.class), anyString()))
            .thenReturn(Optional.empty());

        //Then
        assertThatThrownBy(
            () -> sut.activateCdhEmployee(ACTIVATION_KEY, updatedEmployee, EMPLOYEE_ID))
            .isInstanceOf(EmployeeNotFoundException.class)
            .hasMessageContaining(String.format("Employee with activation key %s was not found",
                ACTIVATION_KEY));
        verify(employeeDataService).getEmployeesV2(any(GetEmployeesQueryParams.class), anyString());
    }

    @Test
    void inviteCdhEmployee_withFeatureFlagEnabled_cdhReturnsEmptyEmployeeList_Success() {
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel(SUPER_ACCESS_LEVEL)
            .build();

        final EmployeeAccountResponse response = EmployeeAccountResponse.builder()
            .employeeAccountId(EMPLOYEE_ID)
            .activationKey(ACTIVATION_KEY)
            .build();

        when(unleashWrapper.isEnabled(any())).thenReturn(true);
        when(employeeDataService.getEmployeesV2(any(GetEmployeesQueryParams.class), eq(USER_EMAIL)))
            .thenReturn(Optional.of(uk.co.whitbread.shared.cdh.model.GetEmployeesResponse
                .builder()
                .results(List.of())
                .build()));
        when(employeeDataService.createEmployeeAccount(eq(COMPANY_ID),
            any(EmployeeAccountRequest.class),
            eq(USER_EMAIL))).thenReturn(response);

        // Then
        sut.inviteCdhEmployee(COMPANY_ID, inviteRequest, cdhEmployeeDetails, LANGUAGE);
        verify(employeeDataService).getEmployeesV2(any(GetEmployeesQueryParams.class),
            eq(USER_EMAIL));
        verify(employeeDataService).createEmployeeAccount(eq(COMPANY_ID),
            any(EmployeeAccountRequest.class),
            eq(USER_EMAIL));
    }

    @Test
    void getCdhEmployee_withFeatureFlagEnabled_Success(){
        //Given
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse =
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
                .builder()
                .employeeAccountId(EMPLOYEE_ID)
                .companyAccountId(COMPANY_ID)
                .build();
        Employee employee = new Employee();
        employee.setId(EMPLOYEE_ID);

        when(unleashWrapper.isEnabled(any())).thenReturn(true);
        when(employeeDataService.getEmployeeV2(eq(COMPANY_ID), eq(EMPLOYEE_ID), anyString()))
            .thenReturn(Optional.of(getEmployeeResponse));
        when(employeeMapper.toEmployee(getEmployeeResponse))
            .thenReturn(employee);

        //when
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse response = sut.getCdhEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL);

        //Then
        assertNotNull(response);
        assertEquals(EMPLOYEE_ID, Mappers.getMapper(EmployeeMapper.class).toEmployee(response).getId());
    }

    @Test
    void addEmployee_withFeatureFlagEnabled_shouldSendEmployeeActivationEmailForBB() {
        // Given
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN)
            .accessLevel(SUPER_ACCESS_LEVEL)
            .build();
        employee.setInnBusiness(false);
        when(unleashWrapper.isEnabled(any())).thenReturn(true);
        doReturn(EmployeeAccountRequest.builder().build()).when(employeeMapper)
            .toEmployeeAccountRequest(any(Employee.class));
        final EmployeeAccountResponse employeeAccountResponse = EmployeeAccountResponse.builder()
            .employeeAccountId(UUIDGenerator.getUUID()).build();
        doReturn(employeeAccountResponse).when(employeeDataService)
            .createEmployeeAccount(anyString(), any(EmployeeAccountRequest.class), anyString());
        final uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse =
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.builder()
                .activationKey(ACTIVATION_KEY).build();
        doReturn(Optional.of(getEmployeeResponse)).when(employeeDataService)
            .getEmployeeV2(anyString(), anyString(), anyString());

        // When
        sut.addCdhEmployee(COMPANY_ID, employee, cdhEmployeeDetails, LANGUAGE);

        // Then
        verify(sut).sendAsyncEmployeeActivationEmail(anyString(), anyString(), anyString(),
            anyString(), anyBoolean());
    }

    @Test
    void updateCdhEmployeeAccessLevel_withFeatureFlagEnabled_ShouldMakeRequest() throws IOException {
        //Given
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse getEmployeeResponse =
            uk.co.whitbread.shared.cdh.model.GetEmployeeResponse
                .builder()
                .employeeAccountId(EMPLOYEE_ID)
                .companyAccountId(COMPANY_ID)
                .accessLevel("BOOKER")
                .build();

        when(unleashWrapper.isEnabled(any())).thenReturn(true);
        when(employeeDataService.getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
            .thenReturn(Optional.of(getEmployeeResponse));
        EmployeeAccountRequest employeeAccountRequest = objectMapper.readValue(
            new File("src/test/resources/mapping/EmployeeAccountRequest.json"),
            EmployeeAccountRequest.class);
        when(employeeMapper.toEmployeeAccountRequest(getEmployeeResponse))
            .thenReturn(employeeAccountRequest);

        //When
        sut.updateCdhEmployeeAccessLevel(COMPANY_ID, EMPLOYEE_ID, AccessLevel.STAYER,
            USER_EMAIL_FROM_TOKEN, LANGUAGE);

        //Then
        verify(employeeDataService).getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN);
        verify(employeeMapper).toEmployeeAccountRequest(getEmployeeResponse);
        verify(employeeDataService)
            .updateEmployeeAccount(COMPANY_ID, EMPLOYEE_ID, employeeAccountRequest,
                USER_EMAIL_FROM_TOKEN);
    }
}
