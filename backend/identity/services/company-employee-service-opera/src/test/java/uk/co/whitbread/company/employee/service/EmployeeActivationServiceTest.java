package uk.co.whitbread.company.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.company.employee.exceptions.EmployeeNotFoundException.ERROR_CODE;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.company.employee.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.company.employee.mapper.EmployeeMapper;
import uk.co.whitbread.company.employee.model.Employee;
import uk.co.whitbread.company.employee.model.GetEmployeeActivationResponse;
import uk.co.whitbread.company.employee.model.InnBusinessEmployeeActivationResponse;
import uk.co.whitbread.company.employee.model.feature.FeatureFlag;
import uk.co.whitbread.company.employee.model.feature.UnleashWrapper;
import uk.co.whitbread.shared.cdh.CompanyDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmployeeActivationServiceTest {

  private static final String ACTIVATION_KEY = "123456";

  @Mock
  private EmployeeDataService employeeDataService;
  @Mock
  private CompanyDataService companyDataService;
  @Mock
  private EmployeeMapper employeeMapper;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @InjectMocks
  private EmployeeActivationService sut;

  @BeforeEach
  void setup() {
    var mockedFeatureFlag = org.mockito.Mockito.mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
  }

  @Test
  void activateTravelManagerInCdh_success() {
    //Given
    String companyAccountId = "companyAccountId";
    String employeeAccountId = "employeeAccountId";
    String email = "test@mail.com";
    GetEmployeeResponse response = GetEmployeeResponse.builder()
        .companyAccountId(companyAccountId)
        .employeeAccountId(employeeAccountId)
        .emailAddress(email)
        .build();
    Employee expectedEmployee = new Employee();
    expectedEmployee.setId(employeeAccountId);
    expectedEmployee.setEmailAddress(email);

    GetEmployeesResponse getEmployeesResponse = GetEmployeesResponse.builder().results(List.of(response)).continuationToken(null).build();

    when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class),
        anyString())).thenReturn(Optional.of(getEmployeesResponse));
    when(employeeDataService.activateEmployee(anyString(), anyString(), anyString())).thenReturn(Optional.of(response));
    when(employeeMapper.toEmployee(any(GetEmployeeResponse.class))).thenReturn(expectedEmployee);

    //When
    Employee activatedEmployee = sut.activateTravelManagerInCdh(ACTIVATION_KEY);

    //Then
    assertThat(activatedEmployee).isEqualTo(expectedEmployee);
  }

  @Test
  void activateTravelManagerInCdh_invalidActivationKey() {
    //Given
    when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class),
        anyString())).thenReturn(Optional.empty());

    //Then
    assertThatThrownBy(() -> sut.activateTravelManagerInCdh(ACTIVATION_KEY))
        .isInstanceOf(EmployeeNotFoundException.class)
        .hasMessageContaining(String.format("Activation key %s is not valid", ACTIVATION_KEY));
  }

  @Test
  void getCdhEmployeeActivationDetails_shouldReturnGetEmployeeActivationResponse() {
    //Given
    GetEmployeeActivationResponse getEmployeeActivationResponse = new GetEmployeeActivationResponse();
    getEmployeeActivationResponse.setSuccess(true);
    getEmployeeActivationResponse.setEmployee(new Employee());
    when(employeeDataService.getEmployees(any(), any()))
        .thenReturn(Optional.of(GetEmployeesResponse.builder()
            .results(List.of(GetEmployeeResponse.builder().build()))
                .build()));
    when(employeeMapper.toGetEmployeeActivationResponse(any(GetEmployeeResponse.class)))
        .thenReturn(getEmployeeActivationResponse);

    //when
    GetEmployeeActivationResponse actual = sut.getEmployeeActivationDetails(ACTIVATION_KEY);

    //Then
    assertThat(actual).isNotNull().isEqualTo(getEmployeeActivationResponse);
    verify(employeeDataService).getEmployees(any(), any());
    verify(employeeMapper).toGetEmployeeActivationResponse(any(GetEmployeeResponse.class));
  }

  @Test
  void getCdhEmployeeActivationDetails_throwsEmployeeNotFoundException() {
    //Given
    GetEmployeeActivationResponse getEmployeeActivationResponse = new GetEmployeeActivationResponse();
    getEmployeeActivationResponse.setSuccess(true);
    getEmployeeActivationResponse.setEmployee(new Employee());
    when(employeeDataService.getEmployees(any(), any())).thenReturn(Optional.empty());

    //Then
    assertThatThrownBy(() -> sut.getEmployeeActivationDetails(ACTIVATION_KEY))
        .isInstanceOf(EmployeeNotFoundException.class)
        .hasFieldOrPropertyWithValue("errorCode", ERROR_CODE)
        .hasMessage("Employee with activation key " + ACTIVATION_KEY + " was not found");
  }

  @Test
  void getInnBusinessEmployeeActivationResponse_ShouldReturnResponse() {
    // Given
    String activationKey = "test-key";

    GetEmployeeActivationResponse getEmployeeActivationResponse = new GetEmployeeActivationResponse();
    getEmployeeActivationResponse.setEmployee(new Employee());
    getEmployeeActivationResponse.setCompanyId("company-id");

    when(employeeDataService.getEmployees(any(), any()))
        .thenReturn(Optional.of(GetEmployeesResponse.builder()
            .results(List.of(GetEmployeeResponse.builder().build()))
            .build()));
    when(employeeDataService.getEmployees(any(), any()))
        .thenReturn(Optional.of(GetEmployeesResponse.builder()
            .results(List.of(GetEmployeeResponse.builder().build()))
            .build()));

    InnBusinessEmployeeActivationResponse mappedResponse = new InnBusinessEmployeeActivationResponse();
    mappedResponse.setCompanyId("company-id");

    when(employeeMapper.toInnBusinessEmployeeActivationResponse(any(GetEmployeeActivationResponse.class)))
        .thenReturn(mappedResponse);
    when(companyDataService.getCompany("company-id", "dummy@email.com"))
        .thenReturn(Optional.of(GetCompanyResponse.builder().companyName("Test Company").build()));
    when(employeeMapper.toGetEmployeeActivationResponse(any(GetEmployeeResponse.class)))
        .thenReturn(getEmployeeActivationResponse);

    // When
    InnBusinessEmployeeActivationResponse response = sut.getInnBusinessEmployeeActivationResponse(
        activationKey);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getCompanyName()).isEqualTo("Test Company");
    verify(employeeMapper).toInnBusinessEmployeeActivationResponse(getEmployeeActivationResponse);
    verify(companyDataService).getCompany("company-id", "dummy@email.com");
  }

  @Test
  void getInnBusinessEmployeeActivationResponse_ShouldHandleMissingCompany() {
    // Given
    String activationKey = "test-key";

    GetEmployeeActivationResponse getEmployeeActivationResponse = new GetEmployeeActivationResponse();
    getEmployeeActivationResponse.setEmployee(new Employee());
    getEmployeeActivationResponse.setCompanyId("company-id");

    when(employeeDataService.getEmployees(any(), any()))
        .thenReturn(Optional.of(GetEmployeesResponse.builder()
            .results(List.of(GetEmployeeResponse.builder().build()))
            .build()));
    when(employeeDataService.getEmployees(any(), any()))
        .thenReturn(Optional.of(GetEmployeesResponse.builder()
            .results(List.of(GetEmployeeResponse.builder().build()))
            .build()));

    InnBusinessEmployeeActivationResponse mappedResponse = new InnBusinessEmployeeActivationResponse();
    mappedResponse.setCompanyId("company-id");

    when(employeeMapper.toInnBusinessEmployeeActivationResponse(any(GetEmployeeActivationResponse.class)))
        .thenReturn(mappedResponse);
    when(employeeMapper.toGetEmployeeActivationResponse(any(GetEmployeeResponse.class)))
        .thenReturn(getEmployeeActivationResponse);

    when(companyDataService.getCompany("company-id", "dummy@email.com"))
        .thenReturn(Optional.empty());
    // When
    InnBusinessEmployeeActivationResponse response = sut.getInnBusinessEmployeeActivationResponse(
        activationKey);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getCompanyName()).isNull();
    verify(employeeMapper).toInnBusinessEmployeeActivationResponse(getEmployeeActivationResponse);
    verify(companyDataService).getCompany("company-id", "dummy@email.com");
  }

  @Test
  void getInnBusinessEmployeeActivationResponse_ShouldThrowExceptionWhenEmployeeNotFound() {
    // Given
    String activationKey = "test-key";

    when(employeeMapper.toInnBusinessEmployeeActivationResponse(any()))
        .thenThrow(new EmployeeNotFoundException("Employee not found"));

    // Then
    assertThatThrownBy(() -> sut.getInnBusinessEmployeeActivationResponse(
        activationKey))
        .isInstanceOf(EmployeeNotFoundException.class)
        .hasMessageContaining("Employee with activation key test-key was not found");

    verifyNoInteractions(companyDataService);
  }

  @Test
  void activateTravelManagerInCdh_withFeatureFlagEnabled_success() {
    //Given
    String companyAccountId = "companyAccountId";
    String employeeAccountId = "employeeAccountId";
    String email = "test@mail.com";
    GetEmployeeResponse response = GetEmployeeResponse.builder()
        .companyAccountId(companyAccountId)
        .employeeAccountId(employeeAccountId)
        .emailAddress(email)
        .build();
    Employee expectedEmployee = new Employee();
    expectedEmployee.setId(employeeAccountId);
    expectedEmployee.setEmailAddress(email);

    GetEmployeesResponse getEmployeesResponse = GetEmployeesResponse.builder()
        .results(List.of(response)).continuationToken(null).build();

    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(employeeDataService.getEmployeesV2(any(GetEmployeesQueryParams.class),
        anyString())).thenReturn(Optional.of(getEmployeesResponse));
    when(employeeDataService.activateEmployee(anyString(), anyString(), anyString())).thenReturn(
        Optional.of(response));
    when(employeeMapper.toEmployee(any(GetEmployeeResponse.class))).thenReturn(expectedEmployee);

    //When
    Employee activatedEmployee = sut.activateTravelManagerInCdh(ACTIVATION_KEY);

    //Then
    assertThat(activatedEmployee).isEqualTo(expectedEmployee);
    verify(employeeDataService).getEmployeesV2(any(GetEmployeesQueryParams.class), anyString());
  }

  @Test
  void activateTravelManagerInCdh_withFeatureFlagEnabled_invalidActivationKey() {
    //Given
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(employeeDataService.getEmployeesV2(any(GetEmployeesQueryParams.class),
        anyString())).thenReturn(Optional.empty());

    //Then
    assertThatThrownBy(() -> sut.activateTravelManagerInCdh(ACTIVATION_KEY))
        .isInstanceOf(EmployeeNotFoundException.class)
        .hasMessageContaining(String.format("Activation key %s is not valid", ACTIVATION_KEY));
    verify(employeeDataService).getEmployeesV2(any(GetEmployeesQueryParams.class), anyString());
  }

  @Test
  void getCdhEmployeeActivationDetails_withFeatureFlagEnabled_shouldReturnGetEmployeeActivationResponse() {
    //Given
    GetEmployeeActivationResponse getEmployeeActivationResponse = new GetEmployeeActivationResponse();
    getEmployeeActivationResponse.setSuccess(true);
    getEmployeeActivationResponse.setEmployee(new Employee());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(employeeDataService.getEmployeesV2(any(), any()))
        .thenReturn(Optional.of(GetEmployeesResponse.builder()
            .results(List.of(GetEmployeeResponse.builder().build()))
            .build()));
    when(employeeMapper.toGetEmployeeActivationResponse(any(GetEmployeeResponse.class)))
        .thenReturn(getEmployeeActivationResponse);

    //when
    GetEmployeeActivationResponse actual = sut.getEmployeeActivationDetails(ACTIVATION_KEY);

    //Then
    assertThat(actual).isNotNull().isEqualTo(getEmployeeActivationResponse);
    verify(employeeDataService).getEmployeesV2(any(), any());
    verify(employeeMapper).toGetEmployeeActivationResponse(any(GetEmployeeResponse.class));
  }

  @Test
  void getCdhEmployeeActivationDetails_withFeatureFlagEnabled_throwsEmployeeNotFoundException() {
    //Given
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(employeeDataService.getEmployeesV2(any(), any())).thenReturn(Optional.empty());

    //Then
    assertThatThrownBy(() -> sut.getEmployeeActivationDetails(ACTIVATION_KEY))
        .isInstanceOf(EmployeeNotFoundException.class)
        .hasFieldOrPropertyWithValue("errorCode", ERROR_CODE)
        .hasMessage("Employee with activation key " + ACTIVATION_KEY + " was not found");
    verify(employeeDataService).getEmployeesV2(any(), any());
  }
}
