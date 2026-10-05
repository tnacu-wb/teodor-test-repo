package uk.co.whitbread.spending.infrastructure.rest.client.spending;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
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
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.cdh.model.AccountSpendingResponse;
import uk.co.whitbread.shared.cdh.model.CompanySpendingResponse;
import uk.co.whitbread.shared.cdh.model.PibaTetheredGuidResponse;
import uk.co.whitbread.shared.cdh.model.spending.transaction.TransactionDetailsResponse;
import uk.co.whitbread.spending.domain.model.in.AccountSpendingRequest;
import uk.co.whitbread.spending.domain.model.in.CompanySpendingRequest;
import uk.co.whitbread.spending.domain.model.out.cdh.AccountSpending;
import uk.co.whitbread.spending.domain.model.out.cdh.CompanySpending;
import uk.co.whitbread.spending.domain.model.out.cdh.TetheredGuidResponse;
import uk.co.whitbread.spending.domain.model.out.cdh.TransactionDetails;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.CdhClient;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.CdhOutPortImpl;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper.AccountSpendingResponseMapper;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper.CompanySpendingResponseMapper;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper.PibaTetheredGuidResponseMapper;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper.TransactionDetailsResponseMapper;

@ExtendWith(MockitoExtension.class)
class CdhOutPortImplTest {

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
  private CompanySpendingResponseMapper companySpendingResponseMapper;

  @Mock
  private AccountSpendingResponseMapper accountSpendingResponseMapper;

  @Mock
  private PibaTetheredGuidResponseMapper pibaTetheredGuidResponseMapper;

  @Mock
  private TransactionDetailsResponseMapper transactionDetailsResponseMapper;

  @Mock
  private CdhClient cdhClient;

  @InjectMocks
  private CdhOutPortImpl spendingOutPort;

  @Test
  void getCompanySpending__ShouldReturnOK() {
    // Arrange
    when(cdhClient.getCompanySpending(any(CompanySpendingRequest.class), eq(EMAIL))).thenReturn(
        createCompanyResponseCDHList());
    when(companySpendingResponseMapper.toDto(
        ArgumentMatchers.<List<CompanySpendingResponse>>any())).thenReturn(
        createCompanySpendingList());

    // Act
    var companySpendingResponseCDH = spendingOutPort.getCompanySpending(
        createCompanySpendingRequest(), EMAIL);

    // Assert
    assertThat(companySpendingResponseCDH, notNullValue());
    assertEquals(COMPANY_ACCOUNT_ID,
        companySpendingResponseCDH.getCompanySpendingList().get(0).getCompanyAccountId());
    assertEquals(MONTH, companySpendingResponseCDH.getCompanySpendingList().get(0).getMonth());
    assertEquals(YEAR, companySpendingResponseCDH.getCompanySpendingList().get(0).getYear());
    assertEquals(NUMBER_OF_BOOKINGS,
        companySpendingResponseCDH.getCompanySpendingList().get(0).getNoOfBookings());
    assertEquals(BOOKING_VALUE,
        companySpendingResponseCDH.getCompanySpendingList().get(0).getBookingValue());

    verify(companySpendingResponseMapper).toDto(
        ArgumentMatchers.<List<CompanySpendingResponse>>any());
    verify(cdhClient).getCompanySpending(any(CompanySpendingRequest.class), eq(EMAIL));
  }

  @Test
  void getCompanySpending__ShouldReturnEmptyList() {
    // Arrange
    var companySpendingRequest = createCompanySpendingRequest();
    when(cdhClient.getCompanySpending(any(CompanySpendingRequest.class), eq(EMAIL))).thenReturn(List.of());

    // Act
    var response = spendingOutPort.getCompanySpending(companySpendingRequest, EMAIL);

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getCompanySpendingList(), empty());
    verify(cdhClient).getCompanySpending(any(CompanySpendingRequest.class), eq(EMAIL));
  }

  @Test
  void getAccountSpending__ShouldReturnOK() {
    // Arrange
    when(cdhClient.getAccountSpending(any(AccountSpendingRequest.class), eq(EMAIL))).thenReturn(
        createAccountResponseCDHList());
    when(accountSpendingResponseMapper.toDto(
        ArgumentMatchers.<List<AccountSpendingResponse>>any())).thenReturn(
        createAccountSpendingList());

    // Act
    var accountSpendigResponseCDH = spendingOutPort.getAccountSpending(
        createAccountSpendigRequest(), EMAIL);

    // Assert
    assertThat(accountSpendigResponseCDH, notNullValue());
    assertEquals(PIBA_ACCOUNT_ID,
        accountSpendigResponseCDH.getAccountSpendingList().get(0).getPibaAccountId());
    assertEquals(YEAR, accountSpendigResponseCDH.getAccountSpendingList().get(0).getYear());
    assertEquals(MONTH, accountSpendigResponseCDH.getAccountSpendingList().get(0).getMonth());
    assertEquals(NUMBER_OF_BOOKINGS,
        accountSpendigResponseCDH.getAccountSpendingList().get(0).getNoOfBookings());
    assertEquals(BOOKING_VALUE,
        accountSpendigResponseCDH.getAccountSpendingList().get(0).getBookingValue());

    verify(accountSpendingResponseMapper).toDto(
        ArgumentMatchers.<List<AccountSpendingResponse>>any());
    verify(cdhClient).getAccountSpending(any(AccountSpendingRequest.class), eq(EMAIL));
  }

  @Test
  void getAccountSpending__ShouldReturnEmptyList() {
    // Arrange
    when(cdhClient.getAccountSpending(any(AccountSpendingRequest.class), eq(EMAIL))).thenReturn(List.of());

    // Act
    var response = spendingOutPort.getAccountSpending(createAccountSpendigRequest(), EMAIL);

    // Assert
    assertThat(response, notNullValue());
    assertThat(response.getAccountSpendingList(), empty());
    verify(cdhClient).getAccountSpending(any(AccountSpendingRequest.class), eq(EMAIL));
  }

  @Test
  void getTetheredGuids_WhenInvoked_ThenParamsAreSentAndResponseIsMapped() {
    var cdhClientResponse = List.of(PibaTetheredGuidResponse.builder().tetheredGuid("22-33").build());
    var infraResponse = List.of(TetheredGuidResponse.builder().tetheredGuid("44-33").build());
    when(cdhClient.getTetheredGuids("c1", "e1", "email")).thenReturn(cdhClientResponse);
    when(pibaTetheredGuidResponseMapper.toDto(cdhClientResponse)).thenReturn(infraResponse);

    var result = spendingOutPort.getTetheredGuids("c1", "e1", "email");

    verify(pibaTetheredGuidResponseMapper).toDto(cdhClientResponse);
    assertEquals(infraResponse, result);
  }

  @Test
  void getTransactions_WhenInvoked_ThenParamsAreSentAndResponseIsMapped() {
    var cdhClientResponse = TransactionDetailsResponse.builder().totalBookingValue(BigDecimal.valueOf(100)).build();
    var infraResponse = TransactionDetails.builder().totalBookingValue(BigDecimal.valueOf(101)).build();
    var fromDate = LocalDate.now().minusDays(1);
    var toDate = LocalDate.now().plusDays(1);
    when(cdhClient.getTransactions("123456", "email@email.com", fromDate, toDate, 1, 10)).thenReturn(cdhClientResponse);
    when(transactionDetailsResponseMapper.toDto(cdhClientResponse)).thenReturn(infraResponse);

    var result = spendingOutPort.getTransactions("123456", "email@email.com",
          LocalDate.now().minusDays(1),
          LocalDate.now().plusDays(1), 1, 10);

    verify(transactionDetailsResponseMapper).toDto(cdhClientResponse);
    assertEquals(infraResponse, result);
  }

  private AccountSpendingRequest createAccountSpendigRequest() {
    return AccountSpendingRequest.builder()
        .pibaAccountId(PIBA_ACCOUNT_ID)
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR)
        .build();
  }

  private List<AccountSpending> createAccountSpendingList() {
    return List.of(AccountSpending.builder()
        .pibaAccountId(PIBA_ACCOUNT_ID)
        .month(MONTH)
        .year(YEAR)
        .noOfBookings(NUMBER_OF_BOOKINGS)
        .bookingValue(BOOKING_VALUE)
        .build());
  }

  private List<AccountSpendingResponse> createAccountResponseCDHList() {
    return List.of(AccountSpendingResponse.builder()
        .bookingValue(BOOKING_VALUE)
        .noOfBookings(NUMBER_OF_BOOKINGS)
        .month(MONTH)
        .year(YEAR)
        .pibaAccountId(PIBA_ACCOUNT_ID)
        .build()
    );
  }


  CompanySpendingRequest createCompanySpendingRequest() {
    return CompanySpendingRequest.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR)
        .build();
  }

  List<CompanySpendingResponse> createCompanyResponseCDHList() {
    return List.of(CompanySpendingResponse.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .year(YEAR)
        .month(MONTH)
        .noOfBookings(NUMBER_OF_BOOKINGS)
        .bookingValue(BOOKING_VALUE)
        .build());
  }

  List<CompanySpending> createCompanySpendingList() {
    return List.of(CompanySpending.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .year(YEAR)
        .month(MONTH)
        .noOfBookings(NUMBER_OF_BOOKINGS)
        .bookingValue(BOOKING_VALUE)
        .build());
  }

}
