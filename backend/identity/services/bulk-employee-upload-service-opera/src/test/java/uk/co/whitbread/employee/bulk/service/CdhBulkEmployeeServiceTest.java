package uk.co.whitbread.employee.bulk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import uk.co.whitbread.employee.bulk.client.CdhBulkEmployeeClient;
import uk.co.whitbread.employee.bulk.converter.CdhBulkEmployeeConverter;
import uk.co.whitbread.employee.bulk.exception.BulkEmployeeUploadException;
import uk.co.whitbread.employee.bulk.exception.BulkEmployeeUploadFileConversionException;
import uk.co.whitbread.employee.bulk.exception.CompanyDoesntMatchException;
import uk.co.whitbread.employee.bulk.exception.CompanyNotFoundException;
import uk.co.whitbread.employee.bulk.exception.GetEmployeeCSVListException;
import uk.co.whitbread.employee.bulk.exception.ValidationError;
import uk.co.whitbread.employee.bulk.mapper.EmployeeMapper;
import uk.co.whitbread.employee.bulk.model.EmployeeWithRowNumber;
import uk.co.whitbread.employee.bulk.model.HeaderProperties;
import uk.co.whitbread.employee.bulk.model.companyEmployees.GetEmployeesResponse;
import uk.co.whitbread.employee.bulk.properties.EmailProperties;
import uk.co.whitbread.employee.bulk.utils.EmployeeFileWriter;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.GetEmployeeResponseDto;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.GetEmployeesResponseDto;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;
import uk.co.whitbread.shared.cdh.CompanyDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class CdhBulkEmployeeServiceTest {

  private static final String COMPANY_ID = "42";
  private static final String ACCESSED_BY = "random@email.com";
  private static final String LANGUAGE_EN = "en";
  private static final String COMPANY_ACCOUNT_ID_FROM_TOKEN = "42";
  private static final String EMPLOYEE_ACCOUNT_ID_FROM_TOKEN = "employeeId";
  private static final String AUTHORIZATION = "Bearer token";
  private static final String ACCESS_LEVEL = "SUPER";


  @Mock
  private CdhBulkEmployeeConverter cdhBulkEmployeeConverter;
  @Mock
  private EmployeeDataService employeeDataService;
  @Mock
  private CdhBulkEmployeeClient cdhBulkEmployeeClient;
  @Mock
  private CompanyDataService companyDataService;
  @Mock
  private EmployeeFileWriter employeeFileWriter;
  @Mock
  private  AzureEmailService emailService;
  @Mock
  private EmailProperties emailProperties;
  @Mock
  private TokenService mockTokenService;
  @Mock
  private EmployeeMapper employeeMapper;

  @InjectMocks
  private CdhBulkEmployeeService cdhBulkEmployeeService;

  private MockMultipartFile upFile;
  
  @BeforeEach
  void setup() throws Exception {
    upFile = new MockMultipartFile("upFile",
        getClass().getResourceAsStream("__files/employees_with-data.xlsx"));
    setupEmployeeProfile();
  }


  @Test
   void bulkAddEmployees_shouldMakeRequest() throws IOException {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(ACCESSED_BY)
        .accessLevel(ACCESS_LEVEL)
        .build();
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
            .thenReturn(cdhEmployeeDetails);
    when(cdhBulkEmployeeConverter.convertEmployeeSpreadsheetToEmployeeAccountRequest(eq(upFile), anyList()))
        .thenReturn(List.of(new EmployeeWithRowNumber(1, EmployeeAccountRequest.builder().build())));
    when(employeeDataService.createEmployeeAccount(any(), any(), any())).thenReturn(new EmployeeAccountResponse(
        "EMPL_1111-2222-3333-4444-5555","activationKey1"));

    assertThat(cdhBulkEmployeeService.bulkAddEmployees(AUTHORIZATION, COMPANY_ID, upFile, LANGUAGE_EN, false)).isTrue();
  }

  @Test
   void bulkAddEmployees_whenSpreadsheetEmpty_returnsFalse() throws IOException {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel(ACCESS_LEVEL)
        .userEmail(ACCESSED_BY).build();
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
            .thenReturn(cdhEmployeeDetails);
    when(cdhBulkEmployeeConverter.convertEmployeeSpreadsheetToEmployeeAccountRequest(eq(upFile), anyList()))
        .thenReturn(Collections.emptyList());

    assertThat(cdhBulkEmployeeService.bulkAddEmployees(AUTHORIZATION, COMPANY_ID, upFile, LANGUAGE_EN, false)).isFalse();
  }

     void bulkAddEmployees_whenCdhCallFails_throwsBulkEmployeeUploadException()
      throws IOException {
    EmployeeAccountRequest emp = EmployeeAccountRequest.builder().build();
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel(ACCESS_LEVEL)
        .userEmail(ACCESSED_BY).build();
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
            .thenReturn(cdhEmployeeDetails);
    when(cdhBulkEmployeeConverter.convertEmployeeSpreadsheetToEmployeeAccountRequest(eq(upFile), anyList()))
        .thenReturn(List.of(new EmployeeWithRowNumber(1, emp)));

    when(employeeDataService.createEmployeeAccount(COMPANY_ID, emp, ACCESSED_BY))
        .thenThrow(new CDHException());

    assertThatThrownBy(
        () -> cdhBulkEmployeeService.bulkAddEmployees(AUTHORIZATION, COMPANY_ID, upFile, "en", false))
        .isInstanceOf(BulkEmployeeUploadException.class);
  }

  @Test
   void bulkAddEmployees_whenCdhCallFails_throwsBulkEmployeeUploadFileConversionException()
      throws IOException {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel(ACCESS_LEVEL)
        .userEmail(ACCESSED_BY).build();
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
            .thenReturn(cdhEmployeeDetails);
    when(cdhBulkEmployeeConverter.convertEmployeeSpreadsheetToEmployeeAccountRequest(eq(upFile), anyList()))
        .thenThrow(new IOException());

    assertThatThrownBy(
        () -> cdhBulkEmployeeService.bulkAddEmployees(AUTHORIZATION, COMPANY_ID, upFile, LANGUAGE_EN, false))
        .isInstanceOf(BulkEmployeeUploadFileConversionException.class);
  }

  @Test
   void bulkGetEmployees_shouldMakeRequest() throws IOException {

    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel(ACCESS_LEVEL)
        .userEmail(ACCESSED_BY).build();
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
            .thenReturn(cdhEmployeeDetails);
    
    when(cdhBulkEmployeeClient.getCompanyEmployees(any(), any(), anyInt(), any(), any(), anyBoolean()))
        .thenReturn(setEmployeesresponseDto());
    when(employeeMapper.toModel(any())).thenReturn(setEmployeesResponse());
    when(companyDataService.getCompany(COMPANY_ID, ACCESSED_BY))
        .thenReturn(Optional.of(GetCompanyResponse.builder().build()));
    when(employeeFileWriter.getCsvHeaderFieldsForCdh(any()))
        .thenReturn(HeaderProperties.builder().build());
    assertThat(
        cdhBulkEmployeeService.bulkGetEmployees(AUTHORIZATION, COMPANY_ID))
        .isNotNull();
    verify(employeeFileWriter).populateCsv(any(), any(), anyList());
    verify(companyDataService).getCompany(any(), any());
    verify(cdhBulkEmployeeClient, times(1))
        .getCompanyEmployees(any(), any(), anyInt(), any(), any(), anyBoolean());

  }

  @Test
   void bulkGetEmployees_shouldThrowGetEmployeeCSVListException () throws IOException {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel(ACCESS_LEVEL)
        .userEmail(ACCESSED_BY).build();

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
            .thenReturn(cdhEmployeeDetails);
    when(cdhBulkEmployeeClient.getCompanyEmployees(any(), any(), anyInt(), any(), any(), anyBoolean()))
        .thenReturn(setEmployeesresponseDto());
    when(companyDataService.getCompany(COMPANY_ID, ACCESSED_BY))
        .thenReturn(Optional.of(GetCompanyResponse.builder().build()));
    when(employeeFileWriter.getCsvHeaderFieldsForCdh(any()))
        .thenReturn(HeaderProperties.builder().build());
    doThrow(new IOException()).when(employeeFileWriter).populateCsv(any(), any(), anyList());

    assertThatThrownBy(
        () -> cdhBulkEmployeeService.bulkGetEmployees(AUTHORIZATION, COMPANY_ID))
        .isInstanceOf(GetEmployeeCSVListException.class);
    verify(employeeFileWriter).populateCsv(any(),any(),anyList());
  }

  @Test
   void bulkGetEmployees_shouldThrowCompanyNotFoundException () throws IOException {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel(ACCESS_LEVEL)
        .userEmail(ACCESSED_BY).build();

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
            .thenReturn(cdhEmployeeDetails);
    when(cdhBulkEmployeeClient.getCompanyEmployees(any(), any(), anyInt(), any(), any(), anyBoolean()))
        .thenReturn(setEmployeesresponseDto());
    when(companyDataService.getCompany(COMPANY_ID, ACCESSED_BY))
        .thenReturn(Optional.empty());

    assertThatThrownBy(
        () -> cdhBulkEmployeeService.bulkGetEmployees(AUTHORIZATION, COMPANY_ID))
        .isInstanceOf(CompanyNotFoundException.class);
    verify(employeeFileWriter, never()).getCsvHeaderFieldsForCdh(any());
    verify(employeeFileWriter, never()).populateCsv(any(),any(),anyList());
  }

  @Test
   void bulkGetEmployees_shouldThrowCompanyDoesntMatchException () {

    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel(ACCESS_LEVEL)
        .userEmail(ACCESSED_BY).build();

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
            .thenReturn(cdhEmployeeDetails);

    assertThatThrownBy(
            () -> cdhBulkEmployeeService.bulkGetEmployees(AUTHORIZATION, "COMPANY_ID"))
            .isInstanceOf(CompanyDoesntMatchException.class);
  }

  @Test
   void bulkGetEmployees_shouldThrowTokenVerificationException () {

    assertThatThrownBy(
            () -> cdhBulkEmployeeService.bulkGetEmployees(null, COMPANY_ID))
            .isInstanceOf(TokenVerificationException.class);
  }

  @Test
  void bulkGetEmployees_shouldThrowTokenVerificationExceptionIfNonSuperUser() throws IOException {

    var employeeProfile = setupEmployeeProfile();
    employeeProfile.setAccessLevel("BOOKER");

    var cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(ACCESSED_BY).build();

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);

    assertThatThrownBy(
        () -> cdhBulkEmployeeService.bulkGetEmployees(AUTHORIZATION, COMPANY_ID))
        .isInstanceOf(TokenVerificationException.class);
    verify(employeeFileWriter, never()).getCsvHeaderFieldsForCdh(any());
    verify(employeeFileWriter, never()).populateCsv(any(),any(),anyList());
  }

  @Test
  void bulkGetEmployees_shouldThrowTokenVerificationExceptionIfProfileNotFoundInCdh() throws IOException {

    var cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(ACCESSED_BY).build();

    when(employeeDataService.getEmployee(any(), any(), any())).thenReturn(Optional.empty());
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);

    assertThatThrownBy(
        () -> cdhBulkEmployeeService.bulkGetEmployees(AUTHORIZATION, COMPANY_ID))
        .isInstanceOf(TokenVerificationException.class);
    verify(employeeFileWriter, never()).getCsvHeaderFieldsForCdh(any());
    verify(employeeFileWriter, never()).populateCsv(any(),any(),anyList());
  }

  @ParameterizedTest
  @CsvSource({
      "false, en, https://employee-activation-url.com/key=",
      "true, en, https://innbusiness-activation-url.com/key=",
      "false, de, https://employee-activation-url-de.com/key=",
      "true, de, https://innbusiness-activation-url-de.com/key="
  })
  void bulkAddEmployees_shouldGenerateCorrectActivationLinkBasedOnInnBusinessFlagAndLanguage(
      boolean innBusiness, String language, String expectedBaseUrl) throws IOException, InterruptedException {
      // Arrange
      String activationKey = "activationKey123";
      String expectedActivationLink = expectedBaseUrl + activationKey;

      CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
          .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
          .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
          .userEmail(ACCESSED_BY)
          .accessLevel(ACCESS_LEVEL)
          .build();

      EmployeeAccountRequest employeeRequest = EmployeeAccountRequest.builder()
          .emailAddress(ACCESSED_BY)
          .accessLevel(ACCESS_LEVEL)
          .build();

      EmployeeAccountResponse employeeResponse = new EmployeeAccountResponse("EMPL_12345", activationKey);

      when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION)).thenReturn(cdhEmployeeDetails);
      when(cdhBulkEmployeeConverter.convertEmployeeSpreadsheetToEmployeeAccountRequest(eq(upFile), anyList())).thenReturn(List.of(new EmployeeWithRowNumber(1, employeeRequest)));
      when(employeeDataService.createEmployeeAccount(COMPANY_ID, employeeRequest, ACCESSED_BY)).thenReturn(employeeResponse);
      when(emailProperties.getInnBusinessEmployeeActivationUrl()).thenReturn("https://innbusiness-activation-url.com/key=");
      when(emailProperties.getEmployeeActivationUrl()).thenReturn("https://employee-activation-url.com/key=");
      when(emailProperties.getInnBusinessEmployeeActivationUrlDe()).thenReturn("https://innbusiness-activation-url-de.com/key=");
      when(emailProperties.getEmployeeActivationUrlDe()).thenReturn("https://employee-activation-url-de.com/key=");

      // Wait for the async e-mail sending task
      CountDownLatch latch = new CountDownLatch(1);
      doAnswer(invocation -> {
          CompletableFuture.runAsync(latch::countDown);
          return null;
      }).when(emailService).sendBBInviteEmployeeEmail(any());

      // Act
      boolean result = cdhBulkEmployeeService.bulkAddEmployees(AUTHORIZATION, COMPANY_ID, upFile, language, innBusiness);

      latch.await();

      // Assert
      assertThat(result).isTrue();
      verify(emailService).sendBBInviteEmployeeEmail(argThat(invite ->
          invite.getActivationLink().equals(expectedActivationLink) &&
          invite.getEmail().equals(ACCESSED_BY) &&
          invite.getLanguage().equals(language) &&
          invite.getAccessLevel().equals(employeeRequest.getAccessLevel())
      ));
  }

  private GetEmployeeResponse setupEmployeeProfile() {
    var enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandom();
    var employeeProfile = enhancedRandom.nextObject(GetEmployeeResponse.class);
    employeeProfile.setAccessLevel(ACCESS_LEVEL);
    employeeProfile.setCompanyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN);
    employeeProfile.setEmployeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN);
    when(employeeDataService.getEmployee(any(), any(), any())).thenReturn(Optional.of(employeeProfile));
    return employeeProfile;
  }
  
  private GetEmployeesResponseDto setEmployeesresponseDto() {
    GetEmployeesResponseDto getEmployeesResponseDto = new GetEmployeesResponseDto();
    List<GetEmployeeResponseDto> getEmployeeResponseDtoList = new ArrayList<>();
    getEmployeesResponseDto.setResults(getEmployeeResponseDtoList);
    getEmployeesResponseDto.setContinuationToken("token");
    return getEmployeesResponseDto;
  }
  
  private GetEmployeesResponse setEmployeesResponse() {
    GetEmployeesResponse getEmployeesResponse = new GetEmployeesResponse();
    List<uk.co.whitbread.employee.bulk.model.companyEmployees.GetEmployeeResponse> getEmployeeResponseList =
        new ArrayList<>();
    getEmployeesResponse.setResults(getEmployeeResponseList);
    getEmployeesResponse.setContinuationToken("token");
    return getEmployeesResponse;
  }

  @ParameterizedTest
  @CsvSource({
    "Cannot create employee as it already exists,282",
    "EmailAddress field is mandatory,283",
    "EmailAddress field is invalid,284",
    "Empty error,Empty error",
  })
  void bulkAddEmployees_whenEmployeeCreationFails_shouldMapErrorCorrectly(String errorMessage, String expectedErrorCode) throws IOException {
    // Arrange
    EmployeeAccountRequest emp = EmployeeAccountRequest.builder().build();
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel(ACCESS_LEVEL)
        .userEmail(ACCESSED_BY).build();
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
    when(cdhBulkEmployeeConverter.convertEmployeeSpreadsheetToEmployeeAccountRequest(eq(upFile), anyList()))
        .thenReturn(List.of(new EmployeeWithRowNumber(1, emp)));
    // Simulate CDHException with a message that should be mapped
    String errorJson = String.format("{\"ErrorMessage\":\"%s\"}", errorMessage);
    CDHException cdhException = new CDHException(400, errorJson, null);
    when(employeeDataService.createEmployeeAccount(COMPANY_ID, emp, ACCESSED_BY))
        .thenThrow(cdhException);

    // Act & Assert
    assertThatThrownBy(() -> cdhBulkEmployeeService.bulkAddEmployees(AUTHORIZATION, COMPANY_ID, upFile, LANGUAGE_EN, false))
        .isInstanceOf(BulkEmployeeUploadException.class)
        .satisfies(ex -> {
          BulkEmployeeUploadException beue = (BulkEmployeeUploadException) ex;
          assertThat(beue.getErrorExceptions()).hasSize(1);
          var error = beue.getErrorExceptions().getFirst();
          assertThat(error.getErrorCode()).isEqualTo(expectedErrorCode); // mapped code
          assertThat(error.getErrorDescription()).isEqualTo("1"); // entryIndex as string
        });
  }

  @ParameterizedTest
  @CsvSource({
      "EmailAddress field is invalid,284",
      "Empty error,Empty error",
  })
  void bulkAddEmployees_whenEmployeeCreationFails_shouldMapError(String errorMessage, String expectedErrorCode) throws IOException {
    // Arrange
    EmployeeAccountRequest emp = EmployeeAccountRequest.builder().build();
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel(ACCESS_LEVEL)
        .userEmail(ACCESSED_BY).build();
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
    when(cdhBulkEmployeeConverter.convertEmployeeSpreadsheetToEmployeeAccountRequest(eq(upFile), anyList()))
        .thenReturn(List.of(new EmployeeWithRowNumber(1, emp)));
    // Simulate CDHException with a message that should be mapped
    String errorJson = String.format("{\"message\":\"%s\"}", errorMessage);
    CDHException cdhException = new CDHException(400, errorJson, null);
    when(employeeDataService.createEmployeeAccount(COMPANY_ID, emp, ACCESSED_BY))
        .thenThrow(cdhException);

    // Act & Assert
    assertThatThrownBy(() -> cdhBulkEmployeeService.bulkAddEmployees(AUTHORIZATION, COMPANY_ID, upFile, LANGUAGE_EN, false))
        .isInstanceOf(BulkEmployeeUploadException.class)
        .satisfies(ex -> {
          BulkEmployeeUploadException beue = (BulkEmployeeUploadException) ex;
          assertThat(beue.getErrorExceptions()).hasSize(1);
          var error = beue.getErrorExceptions().getFirst();
          assertThat(error.getErrorCode()).isEqualTo(expectedErrorCode); // mapped code
          assertThat(error.getErrorDescription()).isEqualTo("1"); // entryIndex as string
        });
  }

  @Test
  void bulkAddEmployees_whenPartialSuccess_shouldAddError293() throws IOException {
    // Arrange
    EmployeeAccountRequest emp1 = EmployeeAccountRequest.builder().emailAddress("emp1@test.com").build();
    EmployeeAccountRequest emp2 = EmployeeAccountRequest.builder().emailAddress("emp2@test.com").build();
    
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel(ACCESS_LEVEL)
        .userEmail(ACCESSED_BY).build();
    
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
    when(cdhBulkEmployeeConverter.convertEmployeeSpreadsheetToEmployeeAccountRequest(eq(upFile), anyList()))
        .thenReturn(List.of(
            new EmployeeWithRowNumber(1, emp1),
            new EmployeeWithRowNumber(2, emp2)
        ));
    
    // First employee succeeds, second fails
    when(employeeDataService.createEmployeeAccount(eq(COMPANY_ID), any(EmployeeAccountRequest.class), eq(ACCESSED_BY)))
        .thenReturn(new EmployeeAccountResponse("EMPL_1111", "key1"))
        .thenThrow(new CDHException(400, "{\"ErrorMessage\":\"Cannot create employee as it already exists\"}", null));

    // Act & Assert
    assertThatThrownBy(() -> cdhBulkEmployeeService.bulkAddEmployees(AUTHORIZATION, COMPANY_ID, upFile, LANGUAGE_EN, false))
        .isInstanceOf(BulkEmployeeUploadException.class)
        .satisfies(ex -> {
          BulkEmployeeUploadException beue = (BulkEmployeeUploadException) ex;
          assertThat(beue.getErrorExceptions()).hasSize(2);
          
          // First error: the original failure
          var firstError = beue.getErrorExceptions().getFirst();
          assertThat(firstError.getErrorCode()).isEqualTo("282");
          assertThat(firstError.getErrorDescription()).isEqualTo("2");
          
          // Second error: error 293
          var error293 = beue.getErrorExceptions().get(1);
          assertThat(error293.getErrorCode()).isEqualTo("293");
          assertThat(error293.getErrorDescription()).isEmpty();
        });
  }

  @Test
  void bulkAddEmployees_whenAllFail_shouldNotAddError293() throws IOException {
    // Arrange
    EmployeeAccountRequest emp1 = EmployeeAccountRequest.builder().emailAddress("emp1@test.com").build();
    EmployeeAccountRequest emp2 = EmployeeAccountRequest.builder().emailAddress("emp2@test.com").build();
    
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel(ACCESS_LEVEL)
        .userEmail(ACCESSED_BY).build();
    
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
    when(cdhBulkEmployeeConverter.convertEmployeeSpreadsheetToEmployeeAccountRequest(eq(upFile), anyList()))
        .thenReturn(List.of(
            new EmployeeWithRowNumber(1, emp1),
            new EmployeeWithRowNumber(2, emp2)
        ));
    
    // Both employees fail
    when(employeeDataService.createEmployeeAccount(eq(COMPANY_ID), any(EmployeeAccountRequest.class), eq(ACCESSED_BY)))
        .thenThrow(new CDHException(400, "{\"ErrorMessage\":\"EmailAddress field is mandatory\"}", null))
        .thenThrow(new CDHException(400, "{\"ErrorMessage\":\"EmailAddress field is invalid\"}", null));

    // Act & Assert
    assertThatThrownBy(() -> cdhBulkEmployeeService.bulkAddEmployees(AUTHORIZATION, COMPANY_ID, upFile, LANGUAGE_EN, false))
        .isInstanceOf(BulkEmployeeUploadException.class)
        .satisfies(ex -> {
          BulkEmployeeUploadException beue = (BulkEmployeeUploadException) ex;
          assertThat(beue.getErrorExceptions()).hasSize(2);
          
          // Verify no error 293
          assertThat(beue.getErrorExceptions()).noneMatch(error -> "293".equals(error.getErrorCode()));
          
          // Verify both are the original failures
          assertThat(beue.getErrorExceptions().get(0).getErrorCode()).isEqualTo("283");
          assertThat(beue.getErrorExceptions().get(1).getErrorCode()).isEqualTo("284");
        });
  }

  @Test
  void bulkAddEmployees_whenAllSucceed_shouldNotThrowException() throws IOException {
    // Arrange
    EmployeeAccountRequest emp1 = EmployeeAccountRequest.builder().emailAddress("emp1@test.com").build();
    EmployeeAccountRequest emp2 = EmployeeAccountRequest.builder().emailAddress("emp2@test.com").build();
    
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel(ACCESS_LEVEL)
        .userEmail(ACCESSED_BY).build();
    
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
    when(cdhBulkEmployeeConverter.convertEmployeeSpreadsheetToEmployeeAccountRequest(eq(upFile), anyList()))
        .thenReturn(List.of(
            new EmployeeWithRowNumber(1, emp1),
            new EmployeeWithRowNumber(2, emp2)
        ));
    
    // Both employees succeed
    when(employeeDataService.createEmployeeAccount(eq(COMPANY_ID), any(EmployeeAccountRequest.class), eq(ACCESSED_BY)))
        .thenReturn(new EmployeeAccountResponse("EMPL_1111", "key1"))
        .thenReturn(new EmployeeAccountResponse("EMPL_2222", "key2"));

    // Act
    boolean result = cdhBulkEmployeeService.bulkAddEmployees(AUTHORIZATION, COMPANY_ID, upFile, LANGUAGE_EN, false);

    // Assert
    assertThat(result).isTrue();
  }

  @Test
  void bulkAddEmployees_whenPartialSuccessWithValidationErrors_shouldAddError293() throws IOException {
    // Arrange
    EmployeeAccountRequest emp1 = EmployeeAccountRequest.builder().emailAddress("emp1@test.com").build();
    EmployeeAccountRequest emp2 = EmployeeAccountRequest.builder().emailAddress("emp2@test.com").build();
    EmployeeAccountRequest emp3 = EmployeeAccountRequest.builder().emailAddress("emp3@test.com").build();
    
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel(ACCESS_LEVEL)
        .userEmail(ACCESSED_BY).build();
    
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
    
    // Mock converter to add validation error for first employee
    when(cdhBulkEmployeeConverter.convertEmployeeSpreadsheetToEmployeeAccountRequest(eq(upFile), anyList()))
        .thenAnswer(invocation -> {
          List<ValidationError> failedEmployees = invocation.getArgument(1);
          // Add validation error for first employee
          failedEmployees.add(new ValidationError("290", "1"));
          
          return List.of(
              new EmployeeWithRowNumber(1, emp1),
              new EmployeeWithRowNumber(2, emp2),
              new EmployeeWithRowNumber(3, emp3)
          );
        });
    
    // Second employee succeeds, third fails
    when(employeeDataService.createEmployeeAccount(eq(COMPANY_ID), any(EmployeeAccountRequest.class), eq(ACCESSED_BY)))
        .thenReturn(new EmployeeAccountResponse("EMPL_2222", "key2"))
        .thenThrow(new CDHException(400, "{\"ErrorMessage\":\"Cannot create employee as it already exists\"}", null));

    // Act & Assert
    assertThatThrownBy(() -> cdhBulkEmployeeService.bulkAddEmployees(AUTHORIZATION, COMPANY_ID, upFile, LANGUAGE_EN, false))
        .isInstanceOf(BulkEmployeeUploadException.class)
        .satisfies(ex -> {
          BulkEmployeeUploadException beue = (BulkEmployeeUploadException) ex;
          assertThat(beue.getErrorExceptions()).hasSize(3);
          
          // First error: validation error
          assertThat(beue.getErrorExceptions().get(0).getErrorCode()).isEqualTo("290");
          
          // Second error: creation failure
          assertThat(beue.getErrorExceptions().get(1).getErrorCode()).isEqualTo("282");
          
          // Third error: error 293
          assertThat(beue.getErrorExceptions().get(2).getErrorCode()).isEqualTo("293");
          assertThat(beue.getErrorExceptions().get(2).getErrorDescription()).isEmpty();
        });
  }
}
