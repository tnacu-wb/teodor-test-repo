package uk.co.whitbread.company.service.cdh;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
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
import uk.co.whitbread.shared.cdh.model.company.CompanyAccountRequest;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesQueryParams;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;
import uk.co.whitbread.shared.cdh.model.question.CreateQuestionResponse;
import uk.co.whitbread.shared.cdh.model.question.GetQuestionResponse;
import uk.co.whitbread.shared.cdh.model.question.Question;

@Slf4j
@Service
@RequiredArgsConstructor
public class CdhService {

  private final EmployeeDataService employeeDataService;
  private final CompanyDataService companyDataService;
  private final QuestionDataService questionDataService;

  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  public GetEmployeeResponse getEmployeeByEmail(String email) {
    var queryParams = GetEmployeesQueryParams.builder()
        .emailAddress(email)
        .build();
    var response = isCdhApiDeprecationEnabled()
        ? employeeDataService.getEmployeesV2(queryParams, email)
        : employeeDataService.getEmployees(queryParams, email);
    if (response.isEmpty() || response.get().getResults().isEmpty()) {
      throw new EmployeeNotFoundException(
          String.format("Employee %s was not found", email));
    }
    return response.get().getResults().getFirst();
  }


  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1HourCdh",
      value = "CompanyEmployees", key = "#companyId")
  public Optional<GetEmployeesResponse> getCompanyEmployees(String companyId, String accessedBy) {

    GetCompanyEmployeesQueryParams queryParams = GetCompanyEmployeesQueryParams.builder()
        .pageSize(1).build();
    return employeeDataService.getCompanyEmployeesV2(companyId, queryParams, accessedBy);

  }

  public GetEmployeeResponse getEmployeeById(String companyId, String employeeId,
      String accessedBy) {
    var response = isCdhApiDeprecationEnabled()
        ? employeeDataService.getEmployeeV2(companyId, employeeId, accessedBy)
        : employeeDataService.getEmployee(companyId, employeeId, accessedBy);
    return response.orElseThrow(() -> new EmployeeNotFoundException(
        String.format("Employee %s from company %s was not found", employeeId, companyId)));
  }

  private boolean isCdhApiDeprecationEnabled() {
    return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation());
  }

  public GetCompanyResponse getCompanyDetails(String companyId, String accessedBy) {
    Optional<GetCompanyResponse> optionalGetCompanyResponse = companyDataService
        .getCompany(companyId, accessedBy);

    return optionalGetCompanyResponse.orElseThrow(
        () -> new CompanyNotFoundException("Could not find company with id " + companyId));
  }

  public List<GetCompanyResponse> getCompanyDetailsByCompanyNameAndAddress(
      GetCompaniesQueryParams queryParams, String accessedBy) {

    final Optional<GetCompaniesResponse> companiesResponse = companyDataService.getCompanies(
        queryParams, accessedBy);
    if (companiesResponse.isPresent()) {
      final List<GetCompanyResponse> results = companiesResponse.get().getResults();
      if (Objects.nonNull(results)) {
        return results;
      }
    }
    return List.of();
  }

  public List<GetQuestionResponse> getQuestions(String companyAccountId, String accessedBy) {
    try {
      return questionDataService.getQuestions(companyAccountId, accessedBy);
    } catch (CDHException e) {
      return Collections.emptyList();
    }
  }

  public void updateQuestion(String companyAccountId, String questionId, Question question,
      String accessedBy) {
    questionDataService.updateQuestion(companyAccountId, questionId, question, accessedBy);
  }

  public void deleteQuestion(String companyAccountId, String questionId, String accessedBy) {
    questionDataService.deleteQuestion(companyAccountId, questionId, accessedBy);
  }

  public CreateQuestionResponse createQuestion(String companyAccountId, Question question,
      String accessedBy) {
    return questionDataService.createQuestion(companyAccountId, question, accessedBy);
  }

  public void updateCompanyDetails(String companyId, CompanyAccountRequest getCompanyResponse,
      String accessedBy) {
    companyDataService.updateCompany(companyId, getCompanyResponse, accessedBy);
  }
}
