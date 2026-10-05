package uk.co.whitbread.cdh.infrastructure.rest.client.account.employee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.domain.model.account.in.EmployeeSearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.in.GetEmployeeRequest;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeeResponse;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeesResponse;
import uk.co.whitbread.cdh.domain.model.feature.FeatureFlag;
import uk.co.whitbread.cdh.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.mapper.CdhEmployeeMapper;
import uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.model.CompanyEmployeeSearchCriteriaDto;
import uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.model.EmployeeRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.model.EmployeeSearchCriteriaDto;

@ExtendWith(MockitoExtension.class)
class EmployeeOutPortImplTest {

  private static final String EMAIL = "jhon@yopmail.com";
  @InjectMocks
  private EmployeeOutPortImpl employeeOutPort;
  @Mock
  private EmployeeClient employeeClient;
  @Mock
  private CdhEmployeeMapper cdhEmployeeMapper;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  private FeatureFlag featureFlag;

  @Test
  void getEmployee_whenFeatureFlagEnabled_shouldCallGetEmployeeV2() {
    var request = GetEmployeeRequest.builder()
        .companyAccountId("COMP123").employeeAccountId("EMP456")
        .accessContext("PI").accessedBy(EMAIL).build();
    var requestDto = EmployeeRequestDto.builder()
        .companyAccountId("COMP123").employeeAccountId("EMP456")
        .accessContext("PI").accessedBy(EMAIL).build();
    var expectedResponse = GetEmployeeResponse.builder().emailAddress(EMAIL).build();

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);
    when(cdhEmployeeMapper.toDto(request)).thenReturn(requestDto);
    when(employeeClient.getEmployeeV2(requestDto)).thenReturn(expectedResponse);

    GetEmployeeResponse result = employeeOutPort.getEmployee(request);

    assertThat(result, Matchers.is(expectedResponse));
    verify(employeeClient).getEmployeeV2(requestDto);
  }

  @Test
  void getEmployee_whenFeatureFlagDisabled_shouldCallGetEmployee() {
    var request = GetEmployeeRequest.builder()
        .companyAccountId("COMP123").employeeAccountId("EMP456")
        .accessContext("PI").accessedBy(EMAIL).build();
    var expectedResponse = GetEmployeeResponse.builder().emailAddress(EMAIL).build();

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(false);
    when(employeeClient.getEmployee(request)).thenReturn(expectedResponse);

    GetEmployeeResponse result = employeeOutPort.getEmployee(request);

    assertThat(result, Matchers.is(expectedResponse));
    verify(employeeClient).getEmployee(request);
  }

  @Test
  void getCompanyEmployees_whenFeatureFlagEnabled_shouldCallGetCompanyEmployeesV2() {
    var companyEmployeeDto = CompanyEmployeeSearchCriteriaDto.builder()
        .pageSize("200").pageToken("testToken").awaitingApproval("false").build();
    var expectedResponse = GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
        .emailAddress(EMAIL).build())).build();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);
    when(cdhEmployeeMapper.toCompanyEmployeeDto(200, "testToken", false)).thenReturn(companyEmployeeDto);
    when(employeeClient.getCompanyEmployeesV2(companyEmployeeDto, "testCompanyAccountId", EMAIL, "PI"))
        .thenReturn(expectedResponse);

    GetEmployeesResponse result = employeeOutPort.getCompanyEmployees(
        "testCompanyAccountId", EMAIL, 200, "testToken", "PI", false);

    assertThat(result, Matchers.is(expectedResponse));
    verify(employeeClient).getCompanyEmployeesV2(companyEmployeeDto, "testCompanyAccountId", EMAIL, "PI");
  }

  @Test
  void getCompanyEmployees_whenFeatureFlagDisabled_shouldCallGetCompanyEmployees() {
    var expectedResponse = GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
        .emailAddress(EMAIL).build())).build();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(false);
    when(employeeClient.getCompanyEmployees("testCompanyAccountId", EMAIL, 200, "testToken", "PI", false))
        .thenReturn(expectedResponse);

    GetEmployeesResponse result = employeeOutPort.getCompanyEmployees(
        "testCompanyAccountId", EMAIL, 200, "testToken", "PI", false);

    assertThat(result, Matchers.is(expectedResponse));
    verify(employeeClient).getCompanyEmployees("testCompanyAccountId", EMAIL, 200, "testToken", "PI", false);
  }

  @Test
  void getEmployees_whenFeatureFlagEnabled_shouldCallGetEmployeesV2() {
    var searchCriteria = EmployeeSearchCriteria.builder().emailAddress("abc@test.com").accessedBy("abc@test.com").accessContext("PI").build();
    var searchCriteriaDto = EmployeeSearchCriteriaDto.builder().emailAddress("abc@test.com").build();
    var expectedResponse = GetEmployeesResponse.builder().results(List.of()).build();

    when(cdhEmployeeMapper.toDto(searchCriteria)).thenReturn(searchCriteriaDto);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);
    when(employeeClient.getEmployeesV2(any(), any(), any())).thenReturn(expectedResponse);

    GetEmployeesResponse result = employeeOutPort.getEmployees(searchCriteria);

    assertThat(result, Matchers.is(expectedResponse));
    verify(employeeClient).getEmployeesV2(searchCriteriaDto, "abc@test.com", "PI");
  }

  @Test
  void getEmployees_whenFeatureFlagDisabled_shouldCallGetEmployees() {
    var searchCriteria = EmployeeSearchCriteria.builder().emailAddress("abc@test.com").build();
    var expectedResponse = GetEmployeesResponse.builder().results(List.of()).build();

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(false);
    when(employeeClient.getEmployees(searchCriteria)).thenReturn(expectedResponse);

    GetEmployeesResponse result = employeeOutPort.getEmployees(searchCriteria);

    assertThat(result, Matchers.is(expectedResponse));
    verify(employeeClient).getEmployees(searchCriteria);
  }
}
