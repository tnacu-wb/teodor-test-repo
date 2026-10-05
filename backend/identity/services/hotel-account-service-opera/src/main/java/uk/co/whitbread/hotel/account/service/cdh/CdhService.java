package uk.co.whitbread.hotel.account.service.cdh;

import static java.util.Objects.nonNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchCriteriaDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchDto;
import uk.co.whitbread.hotel.account.client.cdh.CdhClient;
import uk.co.whitbread.hotel.account.exceptions.CdhServiceException;
import uk.co.whitbread.hotel.account.exceptions.TooManyResultsException;
import uk.co.whitbread.hotel.account.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.account.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.account.properties.CustomerAccountsProperties;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.cdh.BookingDataService;
import uk.co.whitbread.shared.cdh.CompanyDataService;
import uk.co.whitbread.shared.cdh.CustomerDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.QuestionDataService;
import uk.co.whitbread.shared.cdh.RegistrationDataService;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.CustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.EmployeeAnswers;
import uk.co.whitbread.shared.cdh.model.GetAccountBookingsQueryParamsV2;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountBookingsQueryParams;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountsResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.ReservationSearchResponse;
import uk.co.whitbread.shared.cdh.model.SearchCustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.UserDefinedAnswer;
import uk.co.whitbread.shared.cdh.model.company.CompanyQuestion;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;
import uk.co.whitbread.shared.cdh.model.question.GetQuestionResponse;

@Slf4j
@RequiredArgsConstructor
@Service
public class CdhService {

  public static final String REGISTRATION_FLOW = "R";

  private final CustomerDataService customerDataService;
  private final BookingDataService bookingDataService;
  private final CustomerAccountsProperties properties;
  private final CdhClient cdhClient;
  private final QuestionDataService questionDataService;
  private final EmployeeDataService employeeDataService;
  private final RegistrationDataService registrationDataService;
  private final CompanyDataService companyDataService;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  public Optional<GetCustomerAccountResponse> getCustomerAccount(String customerAccountId,
      String email) {
    return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
        ? customerDataService.getCustomerAccountV3(customerAccountId, email)
        : customerDataService.getCustomerAccount(customerAccountId, email);
  }

  public List<GetCustomerAccountResponse> getCustomerAccounts(SearchCustomerAccountRequest request, String email) {
    encodeSearchCustomerAccountRequestForQueryParams(request);
    var opResponse = unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
    ? customerDataService.getCustomerAccountListV3(request, email)
    : customerDataService.getCustomerAccountList(request, email);

    //TODO this is a change that should be reverted when the CDH Lib is fixed
    //customerDataService.getCustomerAccountList(request, email) should return Optional.empty and not an empty GetCustomerAccountResponse
    //this happened after upgrading CdhLib because Mono.isEmpty has changed after upgrading WebClient
    var filteredOpResponse = opResponse
            .filter(response -> Objects.nonNull(response.getTotalResults()))
            .filter(response -> Objects.nonNull(response.getResults()))
            .filter(response -> Objects.nonNull(response.getSearchResults()));

    if (filteredOpResponse.isPresent()) {
          return filteredOpResponse
              .filter(response -> response.getTotalResults() <= properties.getMaxAllowedResults())
              .map(GetCustomerAccountsResponse::getResults)
              .orElseThrow(() -> new TooManyResultsException("Too many results please refine your search by adding more criteria"));
      }
      return Collections.emptyList();
  }

  public void updateCustomerAccount(String customerAccountId,
      CustomerAccountRequest customerAccountRequest, String email) {
    try {
      customerDataService.updateCustomerAccount(customerAccountId, customerAccountRequest,
          email);
    } catch (Exception e) {
      log.error(
          String.format("Error updating CDH PI account for customer with id %s",
              customerAccountId), e);
    }
  }

  public Optional<ReservationSearchResponse> getBookings(
      GetCustomerAccountBookingsQueryParams queryParams, String email) {
    try {
      return bookingDataService.getCustomerAccountBookings(queryParams, email);
    } catch (CDHException e) {
      log.error("Error while retrieving bookings for following query params {}",
          queryParams, e);
      return Optional.empty();
    }
  }

  @Deprecated
  public ReservationSearchResponse getBookingsV2(
      GetAccountBookingsQueryParamsV2 queryParams, String email) {
    try {
      return bookingDataService.getAccountBookingsV2(queryParams, email);
    } catch (CDHException e) {
      int status = e.getStatus();
      log.error("Error with status code {} while retrieving bookings for following query params {}",
          status, queryParams, e);
      throw new CdhServiceException("Exception with status code " + status + " received from CDH", e);
    }
  }

  public CdhReservationSearchDto getBookingHistoryV2(
      CdhReservationSearchCriteriaDto queryParams) {
    try {
      return cdhClient.getAccountBookingsV2(queryParams);
    } catch (CDHException e) {
      int status = e.getStatus();
      log.error("Error with status code {} while retrieving bookings for following query params {}",
          status, queryParams, e);
      throw new CdhServiceException("Exception with status code " + status + " received from CDH", e);
    }
  }

  public boolean isProfileUpdateRequired(GetCompanyResponse companyResponse,
      List<GetQuestionResponse> employeeQuestions, CdhEmployeeDetails cdhEmployeeDetails) {

    boolean customReferenceManagementNeedsAnswer = false;
    boolean purchaseOrderManagementNeedsAnswer = false;

    var companyManagementDetailsOptional = Optional.ofNullable(companyResponse)
        .map(GetCompanyResponse::getCompanyManagementDetails);

    if (companyManagementDetailsOptional.isPresent()) {
      var companyMgmtDetails = companyManagementDetailsOptional.get();
      customReferenceManagementNeedsAnswer = doesBusinessAccountQuestionRequireAnswer(companyMgmtDetails.getCustomerReferenceManagement());
      purchaseOrderManagementNeedsAnswer = doesBusinessAccountQuestionRequireAnswer(companyMgmtDetails.getPurchaseOrderManagement());
    }

    var customQuestionIds = Optional.ofNullable(employeeQuestions)
        .stream()
        .flatMap(List::stream)
        .filter(GetQuestionResponse::isMandatory)
        .filter(question -> REGISTRATION_FLOW.equals(question.getLocation()))
        .map(GetQuestionResponse::getId)
        .collect(Collectors.toSet());

    if (!customQuestionIds.isEmpty() || customReferenceManagementNeedsAnswer || purchaseOrderManagementNeedsAnswer) {
      Optional<GetEmployeeResponse> employee = unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
      ? employeeDataService.getEmployeeV2(
          cdhEmployeeDetails.getCompanyAccountId(), cdhEmployeeDetails.getEmployeeAccountId(),
          cdhEmployeeDetails.getUserEmail())
          : employeeDataService.getEmployee(
          cdhEmployeeDetails.getCompanyAccountId(), cdhEmployeeDetails.getEmployeeAccountId(),
          cdhEmployeeDetails.getUserEmail());

      if (employee.isPresent()) {
        var employeeAnswers = employee.get().getEmployeeAnswers();
        var areCustomerQuestionsUnanswered = areCustomQuestionsLeftUnanswered(employeeAnswers,customQuestionIds);
        var areBusinessQuestionsUnanswered = areBusinessAccountQuestionsUnanswered(employeeAnswers,
            purchaseOrderManagementNeedsAnswer, customReferenceManagementNeedsAnswer);
        return areCustomerQuestionsUnanswered || areBusinessQuestionsUnanswered;
      }
    }
    return false;
  }

  public GetCompanyResponse getCompanyDetails(CdhEmployeeDetails cdhEmployeeDetails) {
    return companyDataService.getCompany(cdhEmployeeDetails.getCompanyAccountId(),
        cdhEmployeeDetails.getUserEmail()).orElse(null);
  }

  public List<GetQuestionResponse> getEmployeeQuestions(CdhEmployeeDetails cdhEmployeeDetails) {
    return questionDataService.getQuestions(
        cdhEmployeeDetails.getCompanyAccountId(), cdhEmployeeDetails.getUserEmail());
  }

  private void encodeSearchCustomerAccountRequestForQueryParams(SearchCustomerAccountRequest request) {
    if (nonNull(request.getAddressLine())) {
      request.setAddressLine(URLEncoder.encode(request.getAddressLine(), StandardCharsets.UTF_8));
    }
    if (nonNull(request.getFirstName())) {
      request.setFirstName(URLEncoder.encode(request.getFirstName(), StandardCharsets.UTF_8));
    }
    if (nonNull(request.getLastName())) {
      request.setLastName(URLEncoder.encode(request.getLastName(), StandardCharsets.UTF_8));
    }
    if (nonNull(request.getCompanyName())) {
      request.setCompanyName(URLEncoder.encode(request.getCompanyName(), StandardCharsets.UTF_8));
    }
    if (nonNull(request.getPostCode())) {
      request.setPostCode(URLEncoder.encode(request.getPostCode(), StandardCharsets.UTF_8));
    }
  }

  private boolean areBusinessAccountQuestionsUnanswered(EmployeeAnswers employeeAnswers, boolean checkPom, boolean checkCrm) {
    if (checkPom && (employeeAnswers == null || StringUtils.isBlank(employeeAnswers.getPurchaseOrderAnswer()))) {
      return true;
    }

    return checkCrm && (employeeAnswers == null || StringUtils.isBlank(
        employeeAnswers.getCustomerReferenceAnswer()));
  }

  private boolean doesBusinessAccountQuestionRequireAnswer(CompanyQuestion companyQuestion) {
    return Optional.ofNullable(companyQuestion)
        .filter(CompanyQuestion::isActive)
        .filter(CompanyQuestion::isMandatory)
        .filter(question -> REGISTRATION_FLOW.equals(question.getLocation()))
        .isPresent();
  }

  private boolean areCustomQuestionsLeftUnanswered(EmployeeAnswers employeeAnswers, Set<String> questionIds) {
    var answersIds = Optional.ofNullable(employeeAnswers)
        .map(EmployeeAnswers::getUserDefinedAnswers)
        .stream()
        .flatMap(List::stream)
        .map(UserDefinedAnswer::getQuestionId)
        .collect(Collectors.toSet());

    return !answersIds.containsAll(questionIds);
  }
}
