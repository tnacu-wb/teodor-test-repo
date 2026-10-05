package uk.co.whitbread.spending.infrastructure.rest.client.cdh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.cdh.RegistrationDataService;
import uk.co.whitbread.shared.cdh.ReportDataService;
import uk.co.whitbread.shared.cdh.model.AccountSpendingResponse;
import uk.co.whitbread.shared.cdh.model.CompanySpendingResponse;
import uk.co.whitbread.shared.cdh.model.GetDashboardDetailsQueryParams;
import uk.co.whitbread.shared.cdh.model.PibaTetheredGuidResponse;
import uk.co.whitbread.shared.cdh.model.spending.transaction.TransactionDetailsRequest;
import uk.co.whitbread.shared.cdh.model.spending.transaction.TransactionDetailsResponse;
import uk.co.whitbread.spending.domain.model.in.AccountSpendingRequest;
import uk.co.whitbread.spending.domain.model.in.CompanySpendingRequest;

@ExtendWith(MockitoExtension.class)
class CdhClientTests {

  private static final String EMAIL = "some@email.com";
  private static final String COMPANY_ACCOUNT_ID = "COMP_5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String PIBA_ACCOUNT_ID = "PIBA_5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String FROM_MONTH_YEAR = "11-2024";
  private static final String TO_MONTH_YEAR = "09-2025";
  private static final Integer YEAR = 2024;
  private static final Integer MONTH = 11;
  private static final Integer NUMBER_OF_BOOKINGS = 5;
  private static final BigDecimal BOOKING_VALUE = BigDecimal.valueOf(2369.36);

  @Mock
  private ReportDataService reportDataService;

  @Mock
  private RegistrationDataService registrationDataService;

  @InjectMocks
  private CdhClient cdhClient;

  @Test
  void getCompanySpending__success() {
    // Arrange
    when(reportDataService.getCompanyLevelDetails(any(), any(), any(), any())).thenReturn(createCompanySpendingResponseList());

    // Act
    var response = cdhClient.getCompanySpending(createCompanySpendingRequest(), EMAIL);

    // Assert
    assertThat(response, notNullValue());
    verify(reportDataService).getCompanyLevelDetails(any(), any(), eq(EMAIL), any());
  }


  @Test
  void getAccountSpending__success() {
    //Arrange
    when(reportDataService.getAccountLevelDetails(any(), any(), any(), any())).thenReturn(createAccountSpendingResponseList());

    //Act
    var response = cdhClient.getAccountSpending(createAccountSpendingRequest(), EMAIL);

    //Assert
    assertThat(response, notNullValue());
    verify(reportDataService).getAccountLevelDetails(any(), any(), eq(EMAIL), any());
  }

  @Test
  void getTetheredGuids_WhenInvoked_ThenParamsArePassedCorrectly() {
    var cdhResponse = List.of(PibaTetheredGuidResponse.builder().tetheredGuid("aaa-bbb").build());
    when(registrationDataService.getDashboardDetails(any(), eq("email@email.com"), eq("InnBusiness")))
          .thenReturn(cdhResponse);

    var response = cdhClient.getTetheredGuids("compId", "empId", "email@email.com");

    assertEquals(cdhResponse, response);
    var queryParams = ArgumentCaptor.forClass(GetDashboardDetailsQueryParams.class);
    verify(registrationDataService).getDashboardDetails(queryParams.capture(), eq("email@email.com"), eq("InnBusiness"));
    assertEquals("compId", queryParams.getValue().getCompanyId());
    assertEquals("empId", queryParams.getValue().getEmployeeId());
  }

  @Test
  void getTransactions_WhenInvoked_ThenParamsArePassedCorrectly() {
    var unsanitizedAccountId = System.lineSeparator() + "123" + System.lineSeparator() + "456";
    var fromDate = LocalDate.now().minusDays(1);
    var toDate = LocalDate.now().plusDays(1);
    var cdhResponse = TransactionDetailsResponse.builder().totalBookingValue(BigDecimal.valueOf(100)).build();
    when(reportDataService.getTransactionDetails(any(), eq("email@email.com"), eq("InnBusiness")))
          .thenReturn(cdhResponse);

    var response = cdhClient.getTransactions(unsanitizedAccountId, "email@email.com", fromDate, toDate,1, 10);

    assertEquals(cdhResponse, response);
    var transactionDetailsRequest = ArgumentCaptor.forClass(TransactionDetailsRequest.class);
    verify(reportDataService).getTransactionDetails(transactionDetailsRequest.capture(), eq("email@email.com"), eq("InnBusiness"));
    assertEquals("123456", transactionDetailsRequest.getValue().getPibaAccountNo());
    assertEquals(fromDate, transactionDetailsRequest.getValue().getFromDate());
    assertEquals(toDate, transactionDetailsRequest.getValue().getToDate());
    assertEquals(1, transactionDetailsRequest.getValue().getPageNumber());
    assertEquals(10, transactionDetailsRequest.getValue().getPageSize());
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

  List<CompanySpendingResponse> createCompanySpendingResponseList() {
    return List.of(CompanySpendingResponse.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID)
            .year(YEAR)
            .month(MONTH)
            .noOfBookings(NUMBER_OF_BOOKINGS)
            .bookingValue(BOOKING_VALUE)
        .build());
  }

  List<AccountSpendingResponse> createAccountSpendingResponseList() {
    return List.of(AccountSpendingResponse.builder()
            .pibaAccountId(PIBA_ACCOUNT_ID)
            .year(YEAR)
            .month(MONTH)
            .noOfBookings(NUMBER_OF_BOOKINGS)
            .bookingValue(BOOKING_VALUE)
        .build());
  }

}
