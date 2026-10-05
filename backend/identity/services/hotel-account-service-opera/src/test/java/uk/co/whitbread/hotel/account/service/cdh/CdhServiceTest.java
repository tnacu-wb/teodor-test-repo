package uk.co.whitbread.hotel.account.service.cdh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.account.fixture.CdhCustomerFixture.createCustomerAccountRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountBookingsQueryParams;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountsResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.ReservationSearchResponse;
import uk.co.whitbread.shared.cdh.model.SearchCustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.UserDefinedAnswer;
import uk.co.whitbread.shared.cdh.model.company.CompanyManagementDetails;
import uk.co.whitbread.shared.cdh.model.company.CompanyQuestion;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;
import uk.co.whitbread.shared.cdh.model.question.GetQuestionResponse;

@ExtendWith(MockitoExtension.class)
class CdhServiceTest {

  static final String QUESTION_ID = "questionId";
  static final String OTHER_QUESTION_ID = "other-questionId";
  private final String EMAIL = "email@test.com";
  private final String CDH_CUSTOMER_ACCOUNT_ID = "25700d4a-beb1-44a3-801c-ce467ac4e8dc";

  @Mock
  private CustomerDataService mockCustomerDataService;
  @Mock
  private BookingDataService bookingDataService;

  @Mock
  private QuestionDataService questionDataService;

  @Mock
  private EmployeeDataService employeeDataService;

  @Mock
  private CompanyDataService companyDataService;

  @Mock
  private RegistrationDataService registrationDataService;

  @InjectMocks
  private CdhService cdhService;

  @Mock
  private CdhClient cdhClient;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Mock
  private CustomerAccountsProperties properties;

  @BeforeEach
  void setUp() {
    var featureFlag = new FeatureFlag();
    featureFlag.setCdhApiDeprecation(new FeatureFlag.Feature());
    lenient().when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    lenient().when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(false);
  }

  @Test
  void getCustomerAccount_success() {
    //When
    cdhService.getCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID, EMAIL);

    //Then
    verify(mockCustomerDataService).getCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID, EMAIL);
  }

  @Test
  void getCustomerAccount_withCdhFeatureFlagEnabled_success() {
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCdhApiDeprecation()))
        .thenReturn(true);

    //When
    cdhService.getCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID, EMAIL);

    //Then
    verify(mockCustomerDataService).getCustomerAccountV3(CDH_CUSTOMER_ACCOUNT_ID, EMAIL);
  }

  @Test
  void updateCustomerAccount_success() {
    //Given
    CustomerAccountRequest customerAccountRequest = createCustomerAccountRequest(EMAIL);

    //When
    cdhService.updateCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID, customerAccountRequest, EMAIL);

    //Then
    verify(mockCustomerDataService).updateCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID,
        customerAccountRequest, EMAIL);
  }

  @Test
  void getBookings_success() {
    cdhService.getBookings(GetCustomerAccountBookingsQueryParams.builder().build(), EMAIL);

    verify(bookingDataService).getCustomerAccountBookings(any(), eq(EMAIL));
  }

  @Test
  void getBookings_handleCdhException() {

    when(bookingDataService.getCustomerAccountBookings(any(), any())).thenThrow(new CDHException());
    Optional<ReservationSearchResponse> optionalReservationSearchResponse = cdhService.getBookings(
        any(), eq(EMAIL));

    assertTrue(optionalReservationSearchResponse.isEmpty());
  }

  @Test
  void getBookingHistoryV2_success() {
    //Given
    when(cdhClient.getAccountBookingsV2(any())).thenReturn(new CdhReservationSearchDto());
    CdhReservationSearchCriteriaDto request = new CdhReservationSearchCriteriaDto();
    //When
    CdhReservationSearchDto response = cdhService.getBookingHistoryV2(request);
    //Then
    assertNotNull(response);
  }

  @Test
  void getBookingHistoryV2_shouldThrowCdhServiceException_whenCdhExceptionIsThrown() {
    // Given
    CdhReservationSearchCriteriaDto queryParams = new CdhReservationSearchCriteriaDto();
    CDHException cdhException = new CDHException();
    when(cdhClient.getAccountBookingsV2(any())).thenThrow(cdhException);

    // When & Then
    CdhServiceException exception = assertThrows(CdhServiceException.class, () -> {
      cdhService.getBookingHistoryV2(queryParams);
    });

    // Assert
    assertEquals("Exception with status code 0 received from CDH", exception.getMessage());
  }

  @Test
  void isProfileUpdateRequired_shouldReturnTrue_CustomQuestionsAnswered_BusinessAccountNoAnswers() {
    //Given
    when(employeeDataService.getEmployee(any(), any(), any())).thenReturn(
        getEmployeeResponse(QUESTION_ID));

    //When
    boolean isProfileUpdateRequired = cdhService.isProfileUpdateRequired(
        getCompanyResponse(true, true, true), getGetQuestionResponses(true), getCdhEmployeeDetails());

    //Then
    assertTrue(isProfileUpdateRequired);
  }

  @Test
  void isProfileUpdateRequired_shouldReturnFalse_CustomQuestionsAnswered() {
    //Given
    var featureFlag = mock(FeatureFlag.class);
    lenient().when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    lenient().when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(false);
    when(employeeDataService.getEmployee(any(), any(), any())).thenReturn(
        getEmployeeResponse(QUESTION_ID));

    //When
    boolean hasProfileUpdateRequired = cdhService.isProfileUpdateRequired(
        getCompanyResponse(false, false, false), getGetQuestionResponses(true), getCdhEmployeeDetails());

    //Then
    assertFalse(hasProfileUpdateRequired);
  }

  @Test
  void isProfileUpdateRequired_shouldReturnFalse_CustomQuestionsAnswered_BusinessQuestionsAnswered() {
    //Given
    when(employeeDataService.getEmployee(any(), any(), any())).thenReturn(
        getEmployeeResponseWithBusinessAccountAnswers(QUESTION_ID));

    //When
    boolean hasProfileUpdateRequired = cdhService.isProfileUpdateRequired(
        getCompanyResponse(true, true, true), getGetQuestionResponses(true), getCdhEmployeeDetails());

    //Then
    assertFalse(hasProfileUpdateRequired);
  }

  @Test
  void isProfileUpdateRequired_shouldReturnTrue_whenNotContainsEmployeeAnswerToCompanyQuestion() {
    //Given
    when(employeeDataService.getEmployee(any(), any(), any())).thenReturn(
        getEmployeeResponse(OTHER_QUESTION_ID));

    //When
    boolean hasProfileUpdateRequired = cdhService.isProfileUpdateRequired(
        getCompanyResponse(false, false, false), getGetQuestionResponses(true), getCdhEmployeeDetails());
    //Then
    assertTrue(hasProfileUpdateRequired);
  }

  @Test
  void isProfileUpdateRequired_shouldReturnFalse_whenCompanyDoesNotHaveMandatoryQuestion() {
    //When
    boolean hasProfileUpdateRequired = cdhService.isProfileUpdateRequired(
        getCompanyResponse(true, false, false), getGetQuestionResponses(true), getCdhEmployeeDetails());

    //Then
    assertFalse(hasProfileUpdateRequired);
  }

  @Test
  void isProfileUpdateRequired_shouldReturnTrue_whenCompanyDoesHasCRMQuestionUnaswered() {
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCdhApiDeprecation()))
        .thenReturn(true);
    when(employeeDataService.getEmployeeV2(any(), any(), any())).thenReturn(
        getEmployeeResponse(QUESTION_ID));
    //When
    boolean hasProfileUpdateRequired = cdhService.isProfileUpdateRequired(
        getCompanyResponse(true, true, false), getGetQuestionResponses(true), getCdhEmployeeDetails());

    //Then
    assertTrue(hasProfileUpdateRequired);
  }

  @Test
  void isProfileUpdateRequired_shouldReturnTrue_whenCompanyDoesHasPOAQuestionUnaswered() {
    when(employeeDataService.getEmployee(any(), any(), any())).thenReturn(
        getEmployeeResponse(QUESTION_ID));
    //When
    boolean hasProfileUpdateRequired = cdhService.isProfileUpdateRequired(
        getCompanyResponse(true, false, true), getGetQuestionResponses(true), getCdhEmployeeDetails());

    //Then
    assertTrue(hasProfileUpdateRequired);
  }

  @Test
  void getCustomerAccounts_success() {
    //Given
    var request = SearchCustomerAccountRequest.builder().email(EMAIL).build();
    var customerAccountResponse = new GetCustomerAccountResponse();
    var response = GetCustomerAccountsResponse.builder()
        .totalResults(1)
        .searchResults(1)
        .results(List.of(customerAccountResponse))
        .build();
    when(mockCustomerDataService.getCustomerAccountList(any(SearchCustomerAccountRequest.class), eq(EMAIL)))
        .thenReturn(Optional.of(response));
    when(properties.getMaxAllowedResults()).thenReturn(10);

    //When
    var result = cdhService.getCustomerAccounts(request, EMAIL);

    //Then
    verify(mockCustomerDataService).getCustomerAccountList(any(SearchCustomerAccountRequest.class), eq(EMAIL));
    assertEquals(1, result.size());
  }

  @Test
  void getCustomerAccounts_withCdhFeatureFlagEnabled_success() {
    //Given
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCdhApiDeprecation())).thenReturn(true);

    var request = SearchCustomerAccountRequest.builder().email(EMAIL).build();
    var customerAccountResponse = new GetCustomerAccountResponse();
    var response = GetCustomerAccountsResponse.builder()
        .totalResults(1)
        .searchResults(1)
        .results(List.of(customerAccountResponse))
        .build();
    when(mockCustomerDataService.getCustomerAccountListV3(any(SearchCustomerAccountRequest.class), eq(EMAIL)))
        .thenReturn(Optional.of(response));
    when(properties.getMaxAllowedResults()).thenReturn(10);

    //When
    var result = cdhService.getCustomerAccounts(request, EMAIL);

    //Then
    verify(mockCustomerDataService).getCustomerAccountListV3(any(SearchCustomerAccountRequest.class), eq(EMAIL));
    assertEquals(1, result.size());
  }

  @Test
  void getCustomerAccounts_returnsEmptyList_whenNoResults() {
    //Given
    var request = SearchCustomerAccountRequest.builder().email(EMAIL).build();
    when(mockCustomerDataService.getCustomerAccountList(any(SearchCustomerAccountRequest.class), eq(EMAIL)))
        .thenReturn(Optional.empty());

    //When
    var result = cdhService.getCustomerAccounts(request, EMAIL);

    //Then
    assertEquals(Collections.emptyList(), result);
  }

  @Test
  void getCustomerAccounts_throwsTooManyResultsException_whenExceedingMaxResults() {
    //Given
    var request = SearchCustomerAccountRequest.builder().email(EMAIL).build();
    var response = GetCustomerAccountsResponse.builder()
        .totalResults(100)
        .searchResults(100)
        .results(List.of(new GetCustomerAccountResponse()))
        .build();
    when(mockCustomerDataService.getCustomerAccountList(any(SearchCustomerAccountRequest.class), eq(EMAIL)))
        .thenReturn(Optional.of(response));
    when(properties.getMaxAllowedResults()).thenReturn(10);

    //When & Then
    assertThrows(TooManyResultsException.class, () -> cdhService.getCustomerAccounts(request, EMAIL));
  }

  @Test
  void getCustomerAccounts_encodesQueryParams() {
    //Given
    var request = SearchCustomerAccountRequest.builder()
        .email(EMAIL)
        .firstName("John")
        .lastName("Doe")
        .addressLine("123 Main St")
        .companyName("Acme Inc")
        .postCode("SW1A 1AA")
        .build();
    var response = GetCustomerAccountsResponse.builder()
        .totalResults(1)
        .searchResults(1)
        .results(List.of(new GetCustomerAccountResponse()))
        .build();
    when(mockCustomerDataService.getCustomerAccountList(any(SearchCustomerAccountRequest.class), eq(EMAIL)))
        .thenReturn(Optional.of(response));
    when(properties.getMaxAllowedResults()).thenReturn(10);

    //When
    var result = cdhService.getCustomerAccounts(request, EMAIL);

    //Then
    assertEquals(1, result.size());
  }

  @Test
  void getCustomerAccounts_withCdhFeatureFlagEnabled_encodesQueryParams() {
    //Given
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCdhApiDeprecation())).thenReturn(true);

    var request = SearchCustomerAccountRequest.builder()
        .email(EMAIL)
        .firstName("John")
        .lastName("Doe")
        .addressLine("123 Main St")
        .companyName("Acme Inc")
        .postCode("SW1A 1AA")
        .build();
    var response = GetCustomerAccountsResponse.builder()
        .totalResults(1)
        .searchResults(1)
        .results(List.of(new GetCustomerAccountResponse()))
        .build();
    when(mockCustomerDataService.getCustomerAccountListV3(any(SearchCustomerAccountRequest.class), eq(EMAIL)))
        .thenReturn(Optional.of(response));
    when(properties.getMaxAllowedResults()).thenReturn(10);

    //When
    var result = cdhService.getCustomerAccounts(request, EMAIL);

    //Then
    verify(mockCustomerDataService).getCustomerAccountListV3(any(SearchCustomerAccountRequest.class), eq(EMAIL));
    assertEquals(1, result.size());
  }

  @Test
  void getCompanyDetails_shouldReturnCompanyResponse_whenPresent() {
    CdhEmployeeDetails details = getCdhEmployeeDetails();
    GetCompanyResponse companyResponse = getCompanyResponse(true, true, true);
    when(companyDataService.getCompany(details.getCompanyAccountId(), details.getUserEmail()))
        .thenReturn(Optional.of(companyResponse));

    GetCompanyResponse result = cdhService.getCompanyDetails(details);

    assertNotNull(result);
    assertEquals(companyResponse, result);
  }

  @Test
  void getCompanyDetails_shouldReturnNull_whenNotPresent() {
    CdhEmployeeDetails details = getCdhEmployeeDetails();
    when(companyDataService.getCompany(details.getCompanyAccountId(), details.getUserEmail()))
        .thenReturn(Optional.empty());

    GetCompanyResponse result = cdhService.getCompanyDetails(details);

    assertNull(result);
  }

  @Test
  void getEmployeeQuestions_shouldReturnQuestions() {
    CdhEmployeeDetails details = getCdhEmployeeDetails();
    List<GetQuestionResponse> questions = getGetQuestionResponses(true);
    when(questionDataService.getQuestions(details.getCompanyAccountId(), details.getUserEmail()))
        .thenReturn(questions);

    List<GetQuestionResponse> result = cdhService.getEmployeeQuestions(details);

    assertNotNull(result);
    assertEquals(questions, result);
  }

  private static CdhEmployeeDetails getCdhEmployeeDetails() {
    return CdhEmployeeDetails.builder()
        .employeeAccountId("EMPL_20f9a1c0-8c55-45e9-b44b-1ef5fd53532d")
        .companyAccountId("COMP_80eade56-67a5-4eb9-ac07-2e6fe5c24739").build();
  }

  private static Optional<GetEmployeeResponse> getEmployeeResponse(String questionId) {
    var userDefinedAnswer = UserDefinedAnswer.builder()
        .answer("answer")
        .questionId(questionId)
        .build();

    return Optional.of(GetEmployeeResponse.builder()
        .employeeAnswers(EmployeeAnswers.builder()
            .userDefinedAnswers(List.of(userDefinedAnswer))
            .build())
        .build());
  }

  private static Optional<GetEmployeeResponse> getEmployeeResponseWithBusinessAccountAnswers(String questionId) {
    var userDefinedAnswer = UserDefinedAnswer.builder()
        .answer("answer")
        .questionId(questionId)
        .build();

    return Optional.of(GetEmployeeResponse.builder()
        .employeeAnswers(EmployeeAnswers.builder()
            .userDefinedAnswers(List.of(userDefinedAnswer))
            .customerReferenceAnswer("crm")
            .purchaseOrderAnswer("poa")
            .build())
        .build());
  }

  private static List<GetQuestionResponse> getGetQuestionResponses(boolean mandatory) {
    return List.of(GetQuestionResponse.builder()
        .id(QUESTION_ID)
        .mandatory(mandatory)
        .location("R")
        .build());
  }

  private GetCompanyResponse getCompanyResponse(boolean active, boolean mandatoryCrm, boolean mandatoryPoa) {
    var cmd = CompanyManagementDetails.builder()
        .customerReferenceManagement(
            CompanyQuestion.builder().active(active).mandatory(mandatoryCrm).location("R").label("CRM").build())
        .purchaseOrderManagement(
            CompanyQuestion.builder().active(active).mandatory(mandatoryPoa).location("R").label("CRM").build())
        .build();
    return GetCompanyResponse.builder().companyManagementDetails(cmd).build();
  }
}
