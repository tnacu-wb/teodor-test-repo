package uk.co.whitbread.company.service.cdh;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.company.exceptions.EmployeeNotFoundException.ERROR_CODE;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.company.exceptions.CompanyNotFoundException;
import uk.co.whitbread.company.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.company.model.feature.FeatureFlag;
import uk.co.whitbread.company.model.feature.UnleashWrapper;
import uk.co.whitbread.shared.cdh.CompanyDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.QuestionDataService;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.GetCompanyEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesQueryParams;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;
import uk.co.whitbread.shared.cdh.model.question.CreateQuestionResponse;
import uk.co.whitbread.shared.cdh.model.question.GetQuestionResponse;
import uk.co.whitbread.shared.cdh.model.question.Question;

@ExtendWith(MockitoExtension.class)
class CdhServiceTest {

  private static final String EMAIL = "user@mail.com";
  private static final String COMPANY_ACCOUNT_ID = "COMP7d3958cc-7d31-4318-aac1-52fa50e26c0f";
  private static final String EMPLOYEE_ACCOUNT_ID = "EMPLb240ecec-41cd-43b9-a382-c2341502fa3e";
  private static final String QUESTION_ID = "e72c9798-f868-4db2-83fd-b26edba01234";

  @Mock
  private EmployeeDataService employeeDataService;
  @Mock
  private QuestionDataService questionDataService;
  @Mock
  private CompanyDataService companyDataService;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  private FeatureFlag featureFlag;

  @InjectMocks
  private CdhService cdhService;

  @Test
  void getEmployeeByEmail_success() {
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(false);

    var employee = GetEmployeeResponse.builder().build();
    when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(EMAIL)))
        .thenReturn(Optional.of(GetEmployeesResponse.builder().results(List.of(employee)).build()));

    var response = cdhService.getEmployeeByEmail(EMAIL);

    assertEquals(employee, response);
  }

  @Test
  void getEmployeeByEmail_withDeprecationFlag_success() {
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);

    var employee = GetEmployeeResponse.builder().build();
    when(employeeDataService.getEmployeesV2(any(GetEmployeesQueryParams.class), eq(EMAIL)))
        .thenReturn(Optional.of(GetEmployeesResponse.builder().results(List.of(employee)).build()));

    var response = cdhService.getEmployeeByEmail(EMAIL);

    assertEquals(employee, response);
  }

  @Test
  void getEmployeeByEmail_throwsEmployeeNotFoundException() {
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(false);

    when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(EMAIL)))
        .thenReturn(Optional.empty());

    assertThatThrownBy(
        () -> cdhService.getEmployeeByEmail(EMAIL))
        .isInstanceOf(EmployeeNotFoundException.class)
        .hasFieldOrPropertyWithValue("errorCode", ERROR_CODE)
        .hasMessageContaining(String.format("Employee %s was not found", EMAIL));
  }

  @Test
  void getCompanyDetails_success() {
    var company = GetCompanyResponse.builder().build();
    when(companyDataService.getCompany(COMPANY_ACCOUNT_ID, EMAIL)).thenReturn(Optional.of(company));

    var response = cdhService.getCompanyDetails(COMPANY_ACCOUNT_ID, EMAIL);

    assertNotNull(response);
    assertEquals(company, response);
  }

  @Test
  void getCompanyDetailsByCompanyNameAndAddress_success() {
    var company = GetCompanyResponse.builder().build();
    var queryParams = GetCompaniesQueryParams.builder().build();
    when(companyDataService.getCompanies(queryParams, EMAIL)).thenReturn(Optional.of(
        GetCompaniesResponse.builder().results(List.of(company)).build()));

    var response = cdhService.getCompanyDetailsByCompanyNameAndAddress(queryParams, EMAIL);

    assertNotNull(response);
    assertEquals(company, response.getFirst());
  }

  @Test
  void getCompanyDetails_throwsCompanyNotFound() {
    when(companyDataService.getCompany(COMPANY_ACCOUNT_ID, EMAIL)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> cdhService.getCompanyDetails(COMPANY_ACCOUNT_ID, EMAIL))
        .isInstanceOf(CompanyNotFoundException.class)
        .hasFieldOrPropertyWithValue("errorCode", CompanyNotFoundException.ERROR_CODE)
        .hasMessageContaining(
            String.format("Could not find company with id %s", COMPANY_ACCOUNT_ID));
  }

  @Test
  void getEmployeeById_success() {
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(false);

    var employee = GetEmployeeResponse.builder().build();
    when(employeeDataService.getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, EMAIL))
        .thenReturn(Optional.of(employee));

    var response = cdhService.getEmployeeById(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, EMAIL);

    assertEquals(employee, response);
  }

  @Test
  void getEmployeeById_withDeprecationFlag_success() {
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);

    var employee = GetEmployeeResponse.builder().build();
    when(employeeDataService.getEmployeeV2(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, EMAIL))
        .thenReturn(Optional.of(employee));

    var response = cdhService.getEmployeeById(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, EMAIL);

    assertEquals(employee, response);
  }

  @Test
  void getEmployeeById_throwsEmployeeNotFoundException() {
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(false);

    when(employeeDataService.getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, EMAIL))
        .thenReturn(Optional.empty());

    assertThatThrownBy(
        () -> cdhService.getEmployeeById(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, EMAIL))
        .isInstanceOf(EmployeeNotFoundException.class)
        .hasFieldOrPropertyWithValue("errorCode", ERROR_CODE)
        .hasMessageContaining(
            String.format("Employee %s from company %s was not found", EMPLOYEE_ACCOUNT_ID,
                COMPANY_ACCOUNT_ID));
  }

  @Test
  void getQuestions_success() {
    List<GetQuestionResponse> questions = List.of(GetQuestionResponse.builder().build());
    when(questionDataService.getQuestions(COMPANY_ACCOUNT_ID, EMAIL)).thenReturn(questions);

    var response = cdhService.getQuestions(COMPANY_ACCOUNT_ID, EMAIL);

    assertEquals(1, response.size());
    assertEquals(questions, response);
  }

  @Test
  void getQuestions_cdhException_returnEmptyList() {
    when(questionDataService.getQuestions(COMPANY_ACCOUNT_ID, EMAIL)).thenThrow(new CDHException());

    var response = cdhService.getQuestions(COMPANY_ACCOUNT_ID, EMAIL);

    assertTrue(response.isEmpty());
  }

  @Test
  void updateQuestions_success() {
    Question question = Question.builder().build();
    cdhService.updateQuestion(COMPANY_ACCOUNT_ID, QUESTION_ID, question, EMAIL);

    verify(questionDataService).updateQuestion(any(), any(), any(), any());
  }

  @Test
  void deleteQuestion_success() {
    cdhService.deleteQuestion(COMPANY_ACCOUNT_ID, QUESTION_ID, EMAIL);

    verify(questionDataService).deleteQuestion(COMPANY_ACCOUNT_ID, QUESTION_ID, EMAIL);
  }

  @Test
  void createQuestions_success() {
    var question = Question.builder().build();
    var position = 1;
    var createQuestionResponse = CreateQuestionResponse.builder()
        .id(QUESTION_ID)
        .position(position)
        .build();
    when(questionDataService.createQuestion(COMPANY_ACCOUNT_ID, question, EMAIL))
        .thenReturn(createQuestionResponse);

    var response = cdhService.createQuestion(COMPANY_ACCOUNT_ID, question, EMAIL);

    assertEquals(QUESTION_ID, response.getId());
    assertEquals(position, response.getPosition());
  }

  @Test
  void getCompanyEmployees_callsGetCompanyEmployeesV2WithPageSizeOne() {
    var employeesResponse = GetEmployeesResponse.builder()
        .totalEmployeesInCompany(5)
        .results(List.of())
        .build();
    when(employeeDataService.getCompanyEmployeesV2(eq(COMPANY_ACCOUNT_ID), any(GetCompanyEmployeesQueryParams.class), eq(EMAIL)))
        .thenReturn(Optional.of(employeesResponse));

    var result = cdhService.getCompanyEmployees(COMPANY_ACCOUNT_ID, EMAIL);

    assertTrue(result.isPresent());
    assertEquals(5, result.get().getTotalEmployeesInCompany());
    var captor = ArgumentCaptor.forClass(GetCompanyEmployeesQueryParams.class);
    verify(employeeDataService).getCompanyEmployeesV2(eq(COMPANY_ACCOUNT_ID), captor.capture(), eq(EMAIL));
    assertEquals(1, captor.getValue().getPageSize());
  }

  @Test
  void getCompanyEmployees_whenNoneFound_returnsEmpty() {
    when(employeeDataService.getCompanyEmployeesV2(eq(COMPANY_ACCOUNT_ID), any(GetCompanyEmployeesQueryParams.class), eq(EMAIL)))
        .thenReturn(Optional.empty());

    var result = cdhService.getCompanyEmployees(COMPANY_ACCOUNT_ID, EMAIL);

    assertFalse(result.isPresent());
  }
}
