package uk.co.whitbread.shared.cdh;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.cdh.model.AccountSpendingResponse;
import uk.co.whitbread.shared.cdh.model.CompanySpendingResponse;
import uk.co.whitbread.shared.cdh.model.GetAccountLevelDetailsQueryParams;
import uk.co.whitbread.shared.cdh.model.GetCompanyLevelDetailsQueryParams;
import uk.co.whitbread.shared.cdh.model.spending.transaction.Paging;
import uk.co.whitbread.shared.cdh.model.spending.transaction.Transaction;
import uk.co.whitbread.shared.cdh.model.spending.transaction.TransactionDetailsRequest;
import uk.co.whitbread.shared.cdh.model.spending.transaction.TransactionDetailsResponse;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;

@ExtendWith(MockitoExtension.class)

class ReportDataServiceTest {

  private static final String COMPANY_ACCOUNT_ID = "COMP_5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String PIBA_ACCOUNT_ID = "PIBA_97a20ce4-923a-4675-82cf-4c7e6b2f932b";
  private static final String ACCESSED_BY = "customer@mail.com";
  private static final String ACCESS_CONTEXT = "InBusiness";
  private static final String HOST = "https://localhost";
  private static final String SUBSCRIPTION_KEY = "subscription-key";

  @Mock
  private CdhApiProperties cdhApiProperties;
  @Mock
  private CdhApiOauthProperties cdhApiOauthProperties;
  @Mock
  private CustomerDataHubClient cdhClient;

  @InjectMocks
  private ReportDataService reportDataService;

  @BeforeEach
  public void setup() {
    when(cdhApiProperties.getHost()).thenReturn(HOST);
    when(cdhApiOauthProperties.getSubscriptionKey()).thenReturn(SUBSCRIPTION_KEY);
  }

  @Test
  void getCompanyLevelDetails_success() {
    var getCompanyLevelDetailsQueryParams = GetCompanyLevelDetailsQueryParams
        .builder()
        .fromMonthYear("11-2024")
        .toMonthYear("09-2025")
        .build();
    var expectedCompanyLevelDetailsResponse = buildCompanyResponse();
    when(cdhClient.getListCDH(anyString(), any(HttpHeaders.class),
        eq(CompanySpendingResponse.class)))
        .thenReturn(expectedCompanyLevelDetailsResponse);

    var responseList = reportDataService
        .getCompanyLevelDetails(COMPANY_ACCOUNT_ID, getCompanyLevelDetailsQueryParams, ACCESSED_BY,
            ACCESS_CONTEXT);

    assertEquals(1, responseList.size());
    assertEquals(expectedCompanyLevelDetailsResponse.get(0), responseList.get(0));
    assertEquals("GBP", responseList.get(0).getBookingCurrency());
  }

  @Test
  void getAccountLevelDetails_success() {

    GetAccountLevelDetailsQueryParams getAccountLevelDetailsQueryParams = GetAccountLevelDetailsQueryParams
        .builder()
        .fromMonthYear("11-2024")
        .toMonthYear("09-2025")
        .build();

    when(cdhClient.getListCDH(anyString(), any(HttpHeaders.class),
        eq(AccountSpendingResponse.class)))
        .thenReturn(buildAccountResponse());

    var responseList = reportDataService
        .getAccountLevelDetails(PIBA_ACCOUNT_ID, getAccountLevelDetailsQueryParams, ACCESSED_BY,
            ACCESS_CONTEXT);

    assertEquals(1, responseList.size());
    assertEquals(PIBA_ACCOUNT_ID, responseList.get(0).getPibaAccountId());
  }

  @Test
  void testGetTransactionDetails() {
    TransactionDetailsRequest requestBody = new TransactionDetailsRequest();
    TransactionDetailsResponse expectedResponse = buildTransactionsResponse();
    when(cdhClient.postCDH(
        anyString(),
        eq(requestBody),
        any(),
        eq(TransactionDetailsRequest.class),
        eq(TransactionDetailsResponse.class)
    )).thenReturn(expectedResponse);

    TransactionDetailsResponse actualResponse = reportDataService.getTransactionDetails(requestBody, ACCESSED_BY,
        ACCESS_CONTEXT);

    assertEquals(expectedResponse, actualResponse);
  }

  private TransactionDetailsResponse buildTransactionsResponse() {
    return TransactionDetailsResponse.builder()
        .totalBookingValue(BigDecimal.ONE)
        .transactions(List.of(
            Transaction.builder()
                .bookingReference("20")
                .customerAccountId("11111111-1111-1111-1111-111111111111")
                .companyAccountId("11111111-1111-1111-1111-111111111111")
                .employeeAccountId("11111111-1111-1111-1211-111111111111")
                .bookingType("BookingType1")
                .cardType("CreditCard")
                .cardToken("token1234")
                .cardMaskedPAN("1234-5678-****-****")
                .bookerName("FirstName1 Lastname1")
                .bookingValue(BigDecimal.valueOf(100.00))
                .bookingDate(LocalDateTime.now())
                .arrivalDate(LocalDateTime.now())
                .departureDate(LocalDateTime.now())
                .pibaAccountId("123")
                .pibaAccountNo("123456")
                .pibaCostCentre(null)
                .pibaCardNo("2345")
                .pibaUser("567")
                .bookingStatus("UNARRIVED")
                .hotelCode("HotelCode1")
                .hotelName("HotelName1")
                .build()))
        .paging(Paging.builder().currentPage(1).pageSize(10).totalResults(3).build())
        .build();
  }

  private List<CompanySpendingResponse> buildCompanyResponse() {

    List<CompanySpendingResponse> list = new ArrayList<>();
    list.add(CompanySpendingResponse.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .year(2024)
        .month(11)
        .noOfBookings(5)
        .bookingValue(BigDecimal.valueOf(2369.36))
        .bookingCurrency("GBP")
        .build());

    return list;
  }

  private List<AccountSpendingResponse> buildAccountResponse() {

    List<AccountSpendingResponse> list = new ArrayList<>();
    list.add(AccountSpendingResponse.builder()
        .pibaAccountId(PIBA_ACCOUNT_ID)
        .year(2024)
        .month(12)
        .noOfBookings(34)
        .bookingValue(BigDecimal.valueOf(2122.123))
        .build());

    return list;
  }

}
