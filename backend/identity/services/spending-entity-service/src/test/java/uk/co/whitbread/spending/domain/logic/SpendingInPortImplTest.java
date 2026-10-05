package uk.co.whitbread.spending.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;
import uk.co.whitbread.spending.domain.exceptions.UnknownAccountException;
import uk.co.whitbread.spending.domain.exceptions.WrongTokenException;
import uk.co.whitbread.spending.domain.model.in.AccountSpendingRequest;
import uk.co.whitbread.spending.domain.model.in.CompanySpendingRequest;
import uk.co.whitbread.spending.domain.model.in.EmployeeSpendRequest;
import uk.co.whitbread.spending.domain.model.in.PaymentInfoModel;
import uk.co.whitbread.spending.domain.model.in.Scheme;
import uk.co.whitbread.spending.domain.model.out.AccountSpendingResponse;
import uk.co.whitbread.spending.domain.model.out.CompanySpendingResponse;
import uk.co.whitbread.spending.domain.model.out.cdh.AccountSpending;
import uk.co.whitbread.spending.domain.model.out.cdh.CompanySpending;
import uk.co.whitbread.spending.domain.model.out.cdh.EmployeeSpendReport;
import uk.co.whitbread.spending.domain.model.out.cdh.TetheredGuidResponse;
import uk.co.whitbread.spending.domain.model.out.cdh.TransactionDetails;
import uk.co.whitbread.spending.domain.model.out.pibaaccountservice.CustomerAccount;
import uk.co.whitbread.spending.domain.model.out.pibaaccountservice.CustomerAccountsResponse;
import uk.co.whitbread.spending.domain.model.out.worldline.AccountInfo;
import uk.co.whitbread.spending.domain.model.out.worldline.AccountInfoResponse;
import uk.co.whitbread.spending.domain.model.out.worldline.AccountValue;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentInfoResponse;
import uk.co.whitbread.spending.domain.ports.secondary.CdhOutPort;
import uk.co.whitbread.spending.domain.ports.secondary.EmployeeSpendOutPort;
import uk.co.whitbread.spending.domain.ports.secondary.PibaAccountServiceOutPort;
import uk.co.whitbread.spending.domain.ports.secondary.WorldlineOutPort;


@ExtendWith(MockitoExtension.class)
class SpendingInPortImplTest {

  private static final String EMAIL = "some@email.com";
  private static final String COMPANY_ACCOUNT_ID = "COMP_5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String EMPLOYEE_ACCOUNT_ID = "EMP_5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String PIBA_ACCOUNT_ID = "PIBA_5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String FROM_MONTH_YEAR = "11-2024";
  private static final String TO_MONTH_YEAR = "09-2025";
  private static final Integer YEAR = 2024;
  private static final Integer MONTH = 11;
  private static final Integer NUMBER_OF_BOOKINGS = 5;
  private static final BigDecimal BOOKING_VALUE = BigDecimal.valueOf(2369.36);

  @Mock
  private CdhOutPort cdhOutPort;

  @Mock
  private EmployeeSpendOutPort employeeSpendOutPort;

  @Mock
  private AuthenticatedUserService authenticatedUserServiceMock;

  @Mock
  private PibaAccountServiceOutPort pibaAccountServiceOutPortMock;

  @Mock
  private WorldlineOutPort worldlineOutPort;

  @Mock
  private Clock clock;

  @InjectMocks
  private SpendingInPortImpl spendingInPort;

  @Test
  void getCompanySpending__ShouldReturnOK() {
    // Arrange
    when(cdhOutPort.getCompanySpending(any(), eq(EMAIL))).thenReturn(createCompanySpendingResponse());
    var expectedCompanyIdFromJwtToken = "expectedCompanyIdFromJwtToken";
    var authenticationMock = mock(Authentication.class);
    var customJwtAuthenticationTokenMock = mock(CustomJwtAuthenticationToken.class);
    var accountMock = mock(Account.class);
    when(customJwtAuthenticationTokenMock.getAccount()).thenReturn(accountMock);
    when(authenticatedUserServiceMock.getAuthenticatedUser()).thenReturn(customJwtAuthenticationTokenMock);
    when(accountMock.getCompanyId()).thenReturn(expectedCompanyIdFromJwtToken);
    when(accountMock.getEmail()).thenReturn(EMAIL);
    SecurityContextHolder.getContext().setAuthentication(authenticationMock);
    var companySpendingRequest = createCompanySpendingRequest();

    // Act
    var companySpending = spendingInPort.getCompanySpending(companySpendingRequest);

    // Assert
    assertEquals(COMPANY_ACCOUNT_ID, companySpending.getCompanySpendingList().get(0).getCompanyAccountId());
    assertEquals(MONTH, companySpending.getCompanySpendingList().get(0).getMonth());
    assertEquals(YEAR, companySpending.getCompanySpendingList().get(0).getYear());
    assertEquals(NUMBER_OF_BOOKINGS, companySpending.getCompanySpendingList().get(0).getNoOfBookings());
    assertEquals(BOOKING_VALUE, companySpending.getCompanySpendingList().get(0).getBookingValue());
    var requestCaptor = ArgumentCaptor.forClass(CompanySpendingRequest.class);
    verify(cdhOutPort).getCompanySpending(requestCaptor.capture(), eq(EMAIL));
    assertEquals(expectedCompanyIdFromJwtToken, requestCaptor.getValue().getCompanyAccountId());
    assertEquals(companySpendingRequest.getFromMonthYear(), requestCaptor.getValue().getFromMonthYear());
    assertEquals(companySpendingRequest.getToMonthYear(), requestCaptor.getValue().getToMonthYear());
  }

  @Test
  void getCompanySpending__WhenCalled_ThenCompanyIdIsExtractedFromToken() {
    var expectedCompanyIdFromJwtToken = "expectedCompanyIdFromJwtToken";
    var authenticationMock = mock(Authentication.class);
    var customJwtAuthenticationTokenMock = mock(CustomJwtAuthenticationToken.class);
    var accountMock = mock(Account.class);
    when(customJwtAuthenticationTokenMock.getAccount()).thenReturn(accountMock);
    when(authenticatedUserServiceMock.getAuthenticatedUser()).thenReturn(customJwtAuthenticationTokenMock);
    when(accountMock.getCompanyId()).thenReturn(expectedCompanyIdFromJwtToken);
    when(accountMock.getEmail()).thenReturn(EMAIL);
    SecurityContextHolder.getContext().setAuthentication(authenticationMock);
    var companySpendingRequest = CompanySpendingRequest.builder()
          .fromMonthYear("from")
          .toMonthYear("to")
          .build();
    var companySpendingResponse = createCompanySpendingResponse();
    when(cdhOutPort.getCompanySpending(any(), eq(EMAIL))).thenReturn(companySpendingResponse);

    var companySpending = spendingInPort.getCompanySpending(companySpendingRequest);

    assertEquals(companySpendingResponse, companySpending);
    var requestCaptor = ArgumentCaptor.forClass(CompanySpendingRequest.class);
    verify(cdhOutPort).getCompanySpending(requestCaptor.capture(), eq(EMAIL));
    assertEquals(expectedCompanyIdFromJwtToken, requestCaptor.getValue().getCompanyAccountId());
    assertEquals(companySpendingRequest.getFromMonthYear(), requestCaptor.getValue().getFromMonthYear());
    assertEquals(companySpendingRequest.getToMonthYear(), requestCaptor.getValue().getToMonthYear());
    verify(authenticatedUserServiceMock).getAuthenticatedUser();
    verify(accountMock).getCompanyId();
  }

  @Test
  void getEmployeeSpend_WhenClaimsAreValid_ThenRequestIsDerivedFromAuthenticatedUser() {
    var authenticationMock = mock(Authentication.class);
    var customJwtAuthenticationTokenMock = mock(CustomJwtAuthenticationToken.class);
    var accountMock = mock(Account.class);
    var employeeSpendReports = createEmployeeSpendReports();
    when(customJwtAuthenticationTokenMock.getAccount()).thenReturn(accountMock);
    when(authenticatedUserServiceMock.getAuthenticatedUser()).thenReturn(customJwtAuthenticationTokenMock);
    when(accountMock.getCompanyId()).thenReturn(COMPANY_ACCOUNT_ID);
    when(accountMock.getEmployeeId()).thenReturn(EMPLOYEE_ACCOUNT_ID);
    when(accountMock.getEmail()).thenReturn(EMAIL);
    when(employeeSpendOutPort.getEmployeeSpend(any())).thenReturn(employeeSpendReports);
    SecurityContextHolder.getContext().setAuthentication(authenticationMock);

    var request = EmployeeSpendRequest.builder()
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR)
        .build();

    var result = spendingInPort.getEmployeeSpend(request);

    assertEquals(employeeSpendReports, result);
    var requestCaptor = ArgumentCaptor.forClass(EmployeeSpendRequest.class);
    verify(employeeSpendOutPort).getEmployeeSpend(requestCaptor.capture());
    assertEquals(COMPANY_ACCOUNT_ID, requestCaptor.getValue().getCompanyAccountId());
    assertEquals(EMPLOYEE_ACCOUNT_ID, requestCaptor.getValue().getEmployeeAccountId());
    assertEquals(FROM_MONTH_YEAR, requestCaptor.getValue().getFromMonthYear());
    assertEquals(TO_MONTH_YEAR, requestCaptor.getValue().getToMonthYear());
    assertEquals("InnB", requestCaptor.getValue().getAccessContext());
    assertEquals(EMAIL, requestCaptor.getValue().getAccessedBy());
  }

  @Test
  void getEmployeeSpend_WhenTokenContainsInvalidClaims_ThenThrowException() {
    var authenticationMock = mock(Authentication.class);
    var customJwtAuthenticationTokenMock = mock(CustomJwtAuthenticationToken.class);
    var accountMock = mock(Account.class);
    when(accountMock.getCompanyId()).thenReturn("");
    when(accountMock.getEmail()).thenReturn(EMAIL);
    when(customJwtAuthenticationTokenMock.getAccount()).thenReturn(accountMock);
    when(authenticatedUserServiceMock.getAuthenticatedUser()).thenReturn(customJwtAuthenticationTokenMock);
    SecurityContextHolder.getContext().setAuthentication(authenticationMock);

    var request = EmployeeSpendRequest.builder()
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR)
        .build();

    assertThrows(WrongTokenException.class, () -> spendingInPort.getEmployeeSpend(request));
    verify(employeeSpendOutPort, never()).getEmployeeSpend(any());
  }
  
  @ParameterizedTest
  @MethodSource("getClaimsTestData")
  void getUpcomingSpending_WhenTokenContainsInvalidClaims_ThenThrowException(ClaimsTestData testEntry) {
    var authenticationMock = mock(Authentication.class);
    var customJwtAuthenticationTokenMock = mock(CustomJwtAuthenticationToken.class);
    var accountMock = mock(Account.class);
    when(accountMock.getEmail()).thenReturn(testEntry.getEmail());
    when(accountMock.getBartEmployeeId()).thenReturn(testEntry.getEmployeeId());
    when(accountMock.getBartId()).thenReturn(testEntry.getCompanyId());
    when(customJwtAuthenticationTokenMock.getAccount()).thenReturn(accountMock);
    when(authenticatedUserServiceMock.getAuthenticatedUser()).thenReturn(customJwtAuthenticationTokenMock);
    SecurityContextHolder.getContext().setAuthentication(authenticationMock);

    assertThrows(
          WrongTokenException.class, () -> spendingInPort.getUpcomingSpending("token", "12345", "1.1.1.1",null));
  }

  @Test
  void getUpcomingSpending_WhenNoTetheringGuidMatchesTheUserAccountId_ThenThrowException() {
    var authenticationMock = mock(Authentication.class);
    var customJwtAuthenticationTokenMock = mock(CustomJwtAuthenticationToken.class);
    var accountMock = mock(Account.class);
    when(accountMock.getEmail()).thenReturn("email@email.com");
    when(accountMock.getBartEmployeeId()).thenReturn("employeeId");
    when(accountMock.getBartId()).thenReturn("companyId");
    when(customJwtAuthenticationTokenMock.getAccount()).thenReturn(accountMock);
    when(authenticatedUserServiceMock.getAuthenticatedUser()).thenReturn(customJwtAuthenticationTokenMock);
    SecurityContextHolder.getContext().setAuthentication(authenticationMock);
    when(pibaAccountServiceOutPortMock.getAccounts("token"))
          .thenReturn(null);

    assertThrows(
          UnknownAccountException.class, () -> spendingInPort.getUpcomingSpending("token", "12345", "1.1.1.1",""));
    verify(pibaAccountServiceOutPortMock).getAccounts("token");
  }

  @Test
  void getUpcomingSpending_WhenBillingFrequencyIsWeekly_ThenDatesComputedCorrectly() {
    var today = LocalDateTime.of(2025, 2, 27, 13, 0, 0);
    prepareMocksForUpcomingSpendingDatesTests("Weekly", DateTimeFormatter.ISO_INSTANT.format(today.atZone(ZoneOffset.UTC)));

    var response = spendingInPort.getUpcomingSpending("token", "12345",
          "1.1.1.1"," ");

    verify(pibaAccountServiceOutPortMock).getAccounts("token");
    verify(pibaAccountServiceOutPortMock, never()).getTetheredUserDetails("token", "t-g-3", "EN");
    verify(worldlineOutPort).getAccountInfo("GB", "1.1.1.1", "t-g-1");
    assertEquals("Hold", response.getAccountStatus());
    assertEquals("826", response.getCurrency());
    assertEquals(LocalDate.of(2025, 2, 27), response.getExpectedSpendTodayDate());
    assertEquals(BigDecimal.valueOf(10L), response.getExpectedSpendToday());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 27), LocalDate.of(2025, 2, 27), 1, 1);
    assertEquals(LocalDate.of(2025, 2, 27), response.getExpectedNextBillingStartDate());
    assertEquals(LocalDate.of(2025, 3, 3), response.getExpectedNextBillingEndDate());
    assertEquals(BigDecimal.valueOf(20L), response.getExpectedNextBilling());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 27), LocalDate.of(2025, 3, 3), 1, 1);
    assertEquals(LocalDate.of(2025, 3, 4), response.getExpectedNextPeriodStartDate());
    assertEquals(LocalDate.of(2025, 3, 10), response.getExpectedNextPeriodEndDate());
    assertEquals(BigDecimal.valueOf(30L), response.getExpectedNextPeriod());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 3, 4), LocalDate.of(2025, 3, 10), 1, 1);
  }

  @Test
  void getUpcomingSpending_WhenBillingFrequencyIsWeeklyAndTodayIsBillingDate_ThenDatesComputedCorrectly() {
    var today = LocalDateTime.of(2025, 2, 24, 13, 0, 0);
    prepareMocksForUpcomingSpendingDatesTests("Weekly", DateTimeFormatter.ISO_INSTANT.format(today.atZone(ZoneOffset.UTC)));

    var response = spendingInPort.getUpcomingSpending("token", "12345",
          "1.1.1.1",null);

    assertEquals(LocalDate.of(2025, 2, 24), response.getExpectedSpendTodayDate());
    assertEquals(BigDecimal.valueOf(10L), response.getExpectedSpendToday());
    assertEquals(LocalDate.of(2025, 2, 24), response.getExpectedNextBillingStartDate());
    assertEquals(LocalDate.of(2025, 2, 24), response.getExpectedNextBillingEndDate());
    assertEquals(BigDecimal.valueOf(10L), response.getExpectedNextBilling());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 24), LocalDate.of(2025, 2, 24), 1, 1);
    assertEquals(LocalDate.of(2025, 2, 25), response.getExpectedNextPeriodStartDate());
    assertEquals(LocalDate.of(2025, 3, 3), response.getExpectedNextPeriodEndDate());
    assertEquals(BigDecimal.valueOf(20L), response.getExpectedNextPeriod());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 25), LocalDate.of(2025, 3, 3), 1, 1);
  }

  @Test
  void getUpcomingSpending_WhenBillingFrequencyIsMonthlyAndTodayIs1st_ThenDatesComputedCorrectly() {
    var today = LocalDateTime.of(2025, 2, 1, 13, 0, 0);
    prepareMocksForUpcomingSpendingDatesTests("Monthly", DateTimeFormatter.ISO_INSTANT.format(today.atZone(ZoneOffset.UTC)));

    var response = spendingInPort.getUpcomingSpending("token", "12345",
          "1.1.1.1",null);

    assertEquals(LocalDate.of(2025, 2, 1), response.getExpectedSpendTodayDate());
    assertEquals(BigDecimal.valueOf(10L), response.getExpectedSpendToday());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 1), 1, 1);
    assertEquals(LocalDate.of(2025, 2, 1), response.getExpectedNextBillingStartDate());
    assertEquals(LocalDate.of(2025, 2, 2), response.getExpectedNextBillingEndDate());
    assertEquals(BigDecimal.valueOf(20L), response.getExpectedNextBilling());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 2), 1, 1);
    assertEquals(LocalDate.of(2025, 2, 3), response.getExpectedNextPeriodStartDate());
    assertEquals(LocalDate.of(2025, 3, 2), response.getExpectedNextPeriodEndDate());
    assertEquals(BigDecimal.valueOf(30L), response.getExpectedNextPeriod());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 3), LocalDate.of(2025, 3, 2), 1, 1);
  }

  @Test
  void getUpcomingSpending_WhenBillingFrequencyIsMonthlyAndTodayIs2nd_ThenDatesComputedCorrectly() {
    var today = LocalDateTime.of(2025, 2, 2, 13, 0, 0);
    prepareMocksForUpcomingSpendingDatesTests("Monthly", DateTimeFormatter.ISO_INSTANT.format(today.atZone(ZoneOffset.UTC)));

    var response = spendingInPort.getUpcomingSpending("token", "12345",
          "1.1.1.1","");

    assertEquals(LocalDate.of(2025, 2, 2), response.getExpectedSpendTodayDate());
    assertEquals(BigDecimal.valueOf(10L), response.getExpectedSpendToday());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 2), LocalDate.of(2025, 2, 2), 1, 1);
    assertEquals(LocalDate.of(2025, 2, 2), response.getExpectedNextBillingStartDate());
    assertEquals(LocalDate.of(2025, 2, 2), response.getExpectedNextBillingEndDate());
    assertEquals(BigDecimal.valueOf(10L), response.getExpectedNextBilling());
    assertEquals(LocalDate.of(2025, 2, 3), response.getExpectedNextPeriodStartDate());
    assertEquals(LocalDate.of(2025, 3, 2), response.getExpectedNextPeriodEndDate());
    assertEquals(BigDecimal.valueOf(20L), response.getExpectedNextPeriod());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 3), LocalDate.of(2025, 3, 2), 1, 1);
  }

  @Test
  void getUpcomingSpending_WhenBillingFrequencyIsMonthlyAndTodayIsMidMonth_ThenDatesComputedCorrectly() {
    var today = LocalDateTime.of(2025, 2, 15, 13, 0, 0);
    prepareMocksForUpcomingSpendingDatesTests("Monthly", DateTimeFormatter.ISO_INSTANT.format(today.atZone(ZoneOffset.UTC)));

    var response = spendingInPort.getUpcomingSpending("token", "12345",
          "1.1.1.1",null);

    assertEquals(LocalDate.of(2025, 2, 15), response.getExpectedSpendTodayDate());
    assertEquals(BigDecimal.valueOf(10L), response.getExpectedSpendToday());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 15), LocalDate.of(2025, 2, 15), 1, 1);
    assertEquals(LocalDate.of(2025, 2, 15), response.getExpectedNextBillingStartDate());
    assertEquals(LocalDate.of(2025, 3, 2), response.getExpectedNextBillingEndDate());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 15), LocalDate.of(2025, 3, 2), 1, 1);
    assertEquals(BigDecimal.valueOf(20L), response.getExpectedNextBilling());
    assertEquals(LocalDate.of(2025, 3, 3), response.getExpectedNextPeriodStartDate());
    assertEquals(LocalDate.of(2025, 4, 2), response.getExpectedNextPeriodEndDate());
    assertEquals(BigDecimal.valueOf(30L), response.getExpectedNextPeriod());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 3, 3), LocalDate.of(2025, 4, 2), 1, 1);
  }

  @Test
  void getUpcomingSpending_WhenBillingFrequencyIsFortnightAndTodayIs1st_ThenDatesComputedCorrectly() {
    var today = LocalDateTime.of(2025, 2, 1, 13, 0, 0);
    prepareMocksForUpcomingSpendingDatesTests("Fortnight", DateTimeFormatter.ISO_INSTANT.format(today.atZone(ZoneOffset.UTC)));

    var response = spendingInPort.getUpcomingSpending("token", "12345",
          "1.1.1.1",null);

    assertEquals(LocalDate.of(2025, 2, 1), response.getExpectedSpendTodayDate());
    assertEquals(BigDecimal.valueOf(10L), response.getExpectedSpendToday());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 1), 1, 1);
    assertEquals(LocalDate.of(2025, 2, 1), response.getExpectedNextBillingStartDate());
    assertEquals(LocalDate.of(2025, 2, 2), response.getExpectedNextBillingEndDate());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 2), 1, 1);
    assertEquals(BigDecimal.valueOf(20L), response.getExpectedNextBilling());
    assertEquals(LocalDate.of(2025, 2, 3), response.getExpectedNextPeriodStartDate());
    assertEquals(LocalDate.of(2025, 2, 15), response.getExpectedNextPeriodEndDate());
    assertEquals(BigDecimal.valueOf(30L), response.getExpectedNextPeriod());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 3), LocalDate.of(2025, 2, 15), 1, 1);
  }

  @Test
  void getUpcomingSpending_WhenBillingFrequencyIsFortnightAndTodayIs2nd_ThenDatesComputedCorrectly() {
    var today = LocalDateTime.of(2025, 2, 2, 13, 0, 0);
    prepareMocksForUpcomingSpendingDatesTests("Fortnight", DateTimeFormatter.ISO_INSTANT.format(today.atZone(ZoneOffset.UTC)));

    var response = spendingInPort.getUpcomingSpending("token", "12345",
          "1.1.1.1","");

    assertEquals(LocalDate.of(2025, 2, 2), response.getExpectedSpendTodayDate());
    assertEquals(BigDecimal.valueOf(10L), response.getExpectedSpendToday());
    assertEquals(LocalDate.of(2025, 2, 2), response.getExpectedNextBillingStartDate());
    assertEquals(LocalDate.of(2025, 2, 2), response.getExpectedNextBillingEndDate());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 2), LocalDate.of(2025, 2, 2), 1, 1);
    assertEquals(BigDecimal.valueOf(10L), response.getExpectedNextBilling());
    assertEquals(LocalDate.of(2025, 2, 3), response.getExpectedNextPeriodStartDate());
    assertEquals(LocalDate.of(2025, 2, 15), response.getExpectedNextPeriodEndDate());
    assertEquals(BigDecimal.valueOf(20L), response.getExpectedNextPeriod());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 3), LocalDate.of(2025, 2, 15), 1, 1);
  }

  @Test
  void getUpcomingSpending_WhenBillingFrequencyIsFortnightAndTodayIsBetween2ndAnd15th_ThenDatesComputedCorrectly() {
    var today = LocalDateTime.of(2025, 2, 10, 13, 0, 0);
    prepareMocksForUpcomingSpendingDatesTests("Fortnight", DateTimeFormatter.ISO_INSTANT.format(today.atZone(ZoneOffset.UTC)));

    var response = spendingInPort.getUpcomingSpending("token", "12345",
          "1.1.1.1",null);

    assertEquals(LocalDate.of(2025, 2, 10), response.getExpectedSpendTodayDate());
    assertEquals(BigDecimal.valueOf(10L), response.getExpectedSpendToday());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 10), LocalDate.of(2025, 2, 10), 1, 1);
    assertEquals(LocalDate.of(2025, 2, 10), response.getExpectedNextBillingStartDate());
    assertEquals(LocalDate.of(2025, 2, 15), response.getExpectedNextBillingEndDate());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 10), LocalDate.of(2025, 2, 15), 1, 1);
    assertEquals(BigDecimal.valueOf(20L), response.getExpectedNextBilling());
    assertEquals(LocalDate.of(2025, 2, 16), response.getExpectedNextPeriodStartDate());
    assertEquals(LocalDate.of(2025, 3, 2), response.getExpectedNextPeriodEndDate());
    assertEquals(BigDecimal.valueOf(30L), response.getExpectedNextPeriod());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 16), LocalDate.of(2025, 3, 2), 1, 1);
  }

  @Test
  void getUpcomingSpending_WhenBillingFrequencyIsFortnightAndTodayIs15th_ThenDatesComputedCorrectly() {
    var today = LocalDateTime.of(2025, 2, 15, 13, 0, 0);
    prepareMocksForUpcomingSpendingDatesTests("Fortnight", DateTimeFormatter.ISO_INSTANT.format(today.atZone(ZoneOffset.UTC)));

    var response = spendingInPort.getUpcomingSpending("token", "12345",
          "1.1.1.1","");

    assertEquals(LocalDate.of(2025, 2, 15), response.getExpectedSpendTodayDate());
    assertEquals(BigDecimal.valueOf(10L), response.getExpectedSpendToday());
    assertEquals(LocalDate.of(2025, 2, 15), response.getExpectedNextBillingStartDate());
    assertEquals(LocalDate.of(2025, 2, 15), response.getExpectedNextBillingEndDate());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 15), LocalDate.of(2025, 2, 15), 1, 1);
    assertEquals(BigDecimal.valueOf(10L), response.getExpectedNextBilling());
    assertEquals(LocalDate.of(2025, 2, 16), response.getExpectedNextPeriodStartDate());
    assertEquals(LocalDate.of(2025, 3, 2), response.getExpectedNextPeriodEndDate());
    assertEquals(BigDecimal.valueOf(20L), response.getExpectedNextPeriod());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 16), LocalDate.of(2025, 3, 2), 1, 1);
  }

  @Test
  void getUpcomingSpending_WhenBillingFrequencyIsFortnightAndTodayIsGreaterThan15th_ThenDatesComputedCorrectly() {
    var today = LocalDateTime.of(2025, 2, 20, 13, 0, 0);
    prepareMocksForUpcomingSpendingDatesTests("Fortnight", DateTimeFormatter.ISO_INSTANT.format(today.atZone(ZoneOffset.UTC)));

    var response = spendingInPort.getUpcomingSpending("token", "12345",
          "1.1.1.1","");

    assertEquals(LocalDate.of(2025, 2, 20), response.getExpectedSpendTodayDate());
    assertEquals(BigDecimal.valueOf(10L), response.getExpectedSpendToday());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 20), LocalDate.of(2025, 2, 20), 1, 1);
    assertEquals(LocalDate.of(2025, 2, 20), response.getExpectedNextBillingStartDate());
    assertEquals(LocalDate.of(2025, 3, 2), response.getExpectedNextBillingEndDate());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 2, 20), LocalDate.of(2025, 3, 2), 1, 1);
    assertEquals(BigDecimal.valueOf(20L), response.getExpectedNextBilling());
    assertEquals(LocalDate.of(2025, 3, 3), response.getExpectedNextPeriodStartDate());
    assertEquals(LocalDate.of(2025, 3, 15), response.getExpectedNextPeriodEndDate());
    assertEquals(BigDecimal.valueOf(30L), response.getExpectedNextPeriod());
    verify(cdhOutPort).getTransactions("12345", "email@email.com", LocalDate.of(2025, 3, 3), LocalDate.of(2025, 3, 15), 1, 1);
  }

  CompanySpendingRequest createCompanySpendingRequest() {
    return CompanySpendingRequest.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR)
        .build();
  }

  AccountSpendingRequest createAccountSpendingRequest() {
    return AccountSpendingRequest.builder()
        .pibaAccountId(PIBA_ACCOUNT_ID)
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR)
        .build();
  }

  AccountSpendingRequest createAccountSpendingRequestWithTetheredUserGuid() {
    return AccountSpendingRequest.builder()
        .pibaAccountId(PIBA_ACCOUNT_ID)
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR)
        .tetheredUserGuid("test")
        .build();
  }

  CompanySpendingResponse createCompanySpendingResponse() {
    return CompanySpendingResponse.builder()
        .companySpendingList(
            List.of(CompanySpending.builder()
                .companyAccountId(COMPANY_ACCOUNT_ID)
                .year(YEAR)
                .month(MONTH)
                .noOfBookings(NUMBER_OF_BOOKINGS)
                .bookingValue(BOOKING_VALUE)
                .build()))
        .build();
  }

  AccountSpendingResponse createAccountSpendingResponse() {
    return AccountSpendingResponse.builder()
        .accountSpendingList(
            List.of(AccountSpending.builder()
                .pibaAccountId(PIBA_ACCOUNT_ID)
                .year(YEAR)
                .month(MONTH)
                .noOfBookings(NUMBER_OF_BOOKINGS)
                .bookingValue(BOOKING_VALUE)
                .build()))
        .build();
  }

  @Test
  void getAccountSpending_ShouldReturnResponse() {
    // Arrange
    var accountSpendingRequest = createAccountSpendingRequest();
    var accountSpendingResponse = createAccountSpendingResponse();
    var authenticationMock = mock(Authentication.class);
    var customJwtAuthenticationTokenMock = mock(CustomJwtAuthenticationToken.class);
    var accountMock = mock(Account.class);
    var token = "Bearer token";
    when(accountMock.getEmail()).thenReturn("email@email.com");
    when(accountMock.getBartEmployeeId()).thenReturn("employeeId");
    when(accountMock.getBartId()).thenReturn("companyId");
    when(customJwtAuthenticationTokenMock.getAccount()).thenReturn(accountMock);
    when(authenticatedUserServiceMock.getAuthenticatedUser()).thenReturn(customJwtAuthenticationTokenMock);
    SecurityContextHolder.getContext().setAuthentication(authenticationMock);

    CustomerAccountsResponse customerAccountsResponse = CustomerAccountsResponse.builder()
        .accounts(List.of(
            CustomerAccount.builder()
                .accountNumber(accountSpendingRequest.getPibaAccountId())
                .tetheredGuid("t-g-1")
                .scheme(Scheme.GB)
                .build()
        ))
        .build();
    when(pibaAccountServiceOutPortMock.getAccounts(token))
        .thenReturn(customerAccountsResponse);
    when(cdhOutPort.getAccountSpending(any(), any())).thenReturn(accountSpendingResponse);

    // Act
    var result = spendingInPort.getAccountSpending(accountSpendingRequest, token);

    // Assert
    assertEquals(accountSpendingResponse, result);
    verify(cdhOutPort).getAccountSpending(accountSpendingRequest, "email@email.com");
  }

  @Test
  void getPaymentInfo_ShouldReturnResponse() {
    //Arrange
    var response = new PaymentInfoResponse();
    var request = new PaymentInfoModel("123123", 1, 2, true, "Bearer token", "1.1.1.1");
    var authenticationMock = mock(Authentication.class);
    var customJwtAuthenticationTokenMock = mock(CustomJwtAuthenticationToken.class);
    var accountMock = mock(Account.class);
    var token = "Bearer token";
    when(accountMock.getEmail()).thenReturn("email@email.com");
    when(accountMock.getBartEmployeeId()).thenReturn("employeeId");
    when(accountMock.getBartId()).thenReturn("companyId");
    when(customJwtAuthenticationTokenMock.getAccount()).thenReturn(accountMock);
    when(authenticatedUserServiceMock.getAuthenticatedUser()).thenReturn(customJwtAuthenticationTokenMock);
    SecurityContextHolder.getContext().setAuthentication(authenticationMock);
    CustomerAccountsResponse customerAccountsResponse = CustomerAccountsResponse.builder()
        .accounts(List.of(
            CustomerAccount.builder()
                .accountNumber("123123")
                .tetheredGuid("t-g-1")
                .scheme(Scheme.GB)
                .build()
        ))
        .build();
    when(pibaAccountServiceOutPortMock.getAccounts(token))
        .thenReturn(customerAccountsResponse);
    when(worldlineOutPort.getPaymentInfo(any(),any(),any())).thenReturn(response);

    //Act
    var paymentInfo = spendingInPort.getPaymentInfo(request);

    //Assert
    assertNotNull(paymentInfo);
  }

  @Test
  void getPaymentInfoWithTetheredUserGuid_ShouldReturnResponse() {
    //Arrange
    var response = new PaymentInfoResponse();
    var request = new PaymentInfoModel("123123", 1, 2, true, "Bearer token", "1.1.1.1","test");
    var authenticationMock = mock(Authentication.class);
    var customJwtAuthenticationTokenMock = mock(CustomJwtAuthenticationToken.class);
    var accountMock = mock(Account.class);
    when(accountMock.getEmail()).thenReturn("email@email.com");
    when(accountMock.getBartEmployeeId()).thenReturn("employeeId");
    when(accountMock.getBartId()).thenReturn("companyId");
    when(customJwtAuthenticationTokenMock.getAccount()).thenReturn(accountMock);
    when(authenticatedUserServiceMock.getAuthenticatedUser()).thenReturn(customJwtAuthenticationTokenMock);
    SecurityContextHolder.getContext().setAuthentication(authenticationMock);
    TetheredGuidResponse cdhResponse = TetheredGuidResponse.builder()
        .scheme("GB")
        .tetheredGuid("test")
        .build();

    when(cdhOutPort.getTetheredGuids(any(),any(),anyString()))
        .thenReturn(List.of(cdhResponse));
    when(worldlineOutPort.getPaymentInfo(any(),any(),any())).thenReturn(response);

    //Act
    var paymentInfo = spendingInPort.getPaymentInfo(request);

    //Assert
    assertNotNull(paymentInfo);
  }

  @Test
  void getPaymentInfoWithTetheredUserGuid_TetheredUserNotFound() {
    //Arrange
    var request = new PaymentInfoModel("123123", 1, 2, true, "Bearer token", "1.1.1.1","test");
    var authenticationMock = mock(Authentication.class);
    var customJwtAuthenticationTokenMock = mock(CustomJwtAuthenticationToken.class);
    var accountMock = mock(Account.class);
    when(accountMock.getEmail()).thenReturn("email@email.com");
    when(accountMock.getBartEmployeeId()).thenReturn("employeeId");
    when(accountMock.getBartId()).thenReturn("companyId");
    when(customJwtAuthenticationTokenMock.getAccount()).thenReturn(accountMock);
    when(authenticatedUserServiceMock.getAuthenticatedUser()).thenReturn(customJwtAuthenticationTokenMock);
    SecurityContextHolder.getContext().setAuthentication(authenticationMock);
    TetheredGuidResponse cdhResponse = TetheredGuidResponse.builder()
        .scheme("GB")
        .tetheredGuid("notFound")
        .build();

    when(cdhOutPort.getTetheredGuids(any(),any(),anyString()))
        .thenReturn(List.of(cdhResponse));

    //Act and assert
    assertThrows(UnknownAccountException.class, () -> spendingInPort.getPaymentInfo(request));
  }

  @Test
  void getPaymentInfoWithTetheredUserGuid_TetheredUserNullInCdh() {
    //Arrange
    var request = new PaymentInfoModel("123123", 1, 2, true, "Bearer token", "1.1.1.1","test");
    var authenticationMock = mock(Authentication.class);
    var customJwtAuthenticationTokenMock = mock(CustomJwtAuthenticationToken.class);
    var accountMock = mock(Account.class);
    when(accountMock.getEmail()).thenReturn("email@email.com");
    when(accountMock.getBartEmployeeId()).thenReturn("employeeId");
    when(accountMock.getBartId()).thenReturn("companyId");
    when(customJwtAuthenticationTokenMock.getAccount()).thenReturn(accountMock);
    when(authenticatedUserServiceMock.getAuthenticatedUser()).thenReturn(customJwtAuthenticationTokenMock);
    SecurityContextHolder.getContext().setAuthentication(authenticationMock);
    TetheredGuidResponse cdhResponse = TetheredGuidResponse.builder()
        .scheme("GB")
        .build();

    when(cdhOutPort.getTetheredGuids(any(),any(),anyString()))
        .thenReturn(List.of(cdhResponse));

    //Act and assert
    assertThrows(UnknownAccountException.class, () -> spendingInPort.getPaymentInfo(request));
  }

  @Test
  void getAccountSpendingWithTetheredUserGuid_ShouldReturnResponse() {
    // Arrange
    var accountSpendingRequest = createAccountSpendingRequestWithTetheredUserGuid();
    var accountSpendingResponse = createAccountSpendingResponse();
    var authenticationMock = mock(Authentication.class);
    var customJwtAuthenticationTokenMock = mock(CustomJwtAuthenticationToken.class);
    var accountMock = mock(Account.class);
    var token = "Bearer token";
    when(accountMock.getEmail()).thenReturn("email@email.com");
    when(accountMock.getBartEmployeeId()).thenReturn("employeeId");
    when(accountMock.getBartId()).thenReturn("companyId");
    when(customJwtAuthenticationTokenMock.getAccount()).thenReturn(accountMock);
    when(authenticatedUserServiceMock.getAuthenticatedUser()).thenReturn(customJwtAuthenticationTokenMock);
    SecurityContextHolder.getContext().setAuthentication(authenticationMock);
    TetheredGuidResponse cdhResponse = TetheredGuidResponse.builder()
        .tetheredGuid("test")
        .scheme("gb")
        .build();

    when(cdhOutPort.getTetheredGuids(any(),any(),anyString())).thenReturn(List.of(cdhResponse));
    when(cdhOutPort.getAccountSpending(any(), any())).thenReturn(accountSpendingResponse);

    // Act
    var result = spendingInPort.getAccountSpending(accountSpendingRequest, token);

    // Assert
    assertEquals(accountSpendingResponse, result);
    verify(cdhOutPort).getAccountSpending(accountSpendingRequest, "email@email.com");
  }

  @AllArgsConstructor
  @Getter
  static class ClaimsTestData {
    private String companyId;
    private String employeeId;
    private String email;
  }

  private static Stream<ClaimsTestData> getClaimsTestData() {
    return Stream.of(
          new ClaimsTestData(null, "employeeId", "email"),
          new ClaimsTestData("", "employeeId", "email"),
          new ClaimsTestData("company", null, "email"),
          new ClaimsTestData("company", "", "email"),
          new ClaimsTestData("company", "employeeId", null),
          new ClaimsTestData("company", "employeeId", "")
    );
  }


  private void prepareMocksForUpcomingSpendingDatesTests(String billingFrequency, String today) {
    var authenticationMock = mock(Authentication.class);
    var customJwtAuthenticationTokenMock = mock(CustomJwtAuthenticationToken.class);
    var accountMock = mock(Account.class);
    when(accountMock.getEmail()).thenReturn("email@email.com");
    when(accountMock.getBartEmployeeId()).thenReturn("employeeId");
    when(accountMock.getBartId()).thenReturn("companyId");
    when(customJwtAuthenticationTokenMock.getAccount()).thenReturn(accountMock);
    when(authenticatedUserServiceMock.getAuthenticatedUser()).thenReturn(customJwtAuthenticationTokenMock);
    SecurityContextHolder.getContext().setAuthentication(authenticationMock);

    CustomerAccountsResponse customerAccountsResponse = CustomerAccountsResponse.builder()
        .accounts(List.of(
            CustomerAccount.builder()
                .accountNumber("12345")
                .tetheredGuid("t-g-1")
                .scheme(Scheme.GB)
                .build()
        ))
        .build();
    when(pibaAccountServiceOutPortMock.getAccounts("token"))
        .thenReturn(customerAccountsResponse);

    AccountInfoResponse worldlineAccount = AccountInfoResponse.builder()
        .data(AccountInfo.builder()
            .billingFrequency(billingFrequency)
            .status("Hold")
            .statementValue(AccountValue.builder()
                .currencyCode("826")
                .build())
            .build())
        .build();
    when(worldlineOutPort.getAccountInfo("GB", "1.1.1.1", "t-g-1"))
        .thenReturn(worldlineAccount);

    when(cdhOutPort.getTransactions(eq("12345"), eq("email@email.com"), any(), any(), eq(1), eq(1)))
        .thenAnswer(invocation -> {
          LocalDate startDate = invocation.getArgument(2);
          LocalDate endDate = invocation.getArgument(3);

          if (startDate.equals(endDate)) {
            return TransactionDetails.builder().totalBookingValue(BigDecimal.valueOf(10L)).build();
          }

          String dateRange = startDate + " to " + endDate;

          if (dateRange.equals("2025-02-01 to 2025-02-02") ||
              dateRange.equals("2025-02-10 to 2025-02-15") ||
              dateRange.equals("2025-02-15 to 2025-03-02") ||
              dateRange.equals("2025-02-20 to 2025-03-02") ||
              dateRange.equals("2025-02-27 to 2025-03-03") ||
              dateRange.equals("2025-02-25 to 2025-03-03")) {
            return TransactionDetails.builder().totalBookingValue(BigDecimal.valueOf(20L)).build();
          }

          if ((dateRange.equals("2025-02-03 to 2025-03-02") && billingFrequency.equals("Monthly") && today.contains("2025-02-02")) ||
              (dateRange.equals("2025-02-03 to 2025-02-15") && today.contains("2025-02-02")) ||
              (dateRange.equals("2025-02-16 to 2025-03-02") && today.contains("2025-02-15"))) {
            return TransactionDetails.builder().totalBookingValue(BigDecimal.valueOf(20L)).build();
          }

          return TransactionDetails.builder().totalBookingValue(BigDecimal.valueOf(30L)).build();
        });

    when(clock.instant()).thenReturn(Instant.parse(today));
    when(clock.getZone()).thenReturn(ZoneId.of("UCT"));
  }

  private List<EmployeeSpendReport> createEmployeeSpendReports() {
    return List.of(EmployeeSpendReport.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .year(YEAR)
        .month(MONTH)
        .noOfBookings(NUMBER_OF_BOOKINGS)
        .bookingValue(BOOKING_VALUE)
        .build());
  }

}
