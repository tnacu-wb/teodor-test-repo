package uk.co.whitbread.spending.infrastructure.rest.client.cdhadapterservice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.spending.domain.model.in.EmployeeSpendRequest;
import uk.co.whitbread.spending.domain.model.out.cdh.EmployeeSpendReport;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper.EmployeeSpendReportResponseMapper;
import uk.co.whitbread.spending.infrastructure.rest.client.cdhadapterservice.model.EmployeeSpendReportResponse;

@ExtendWith(MockitoExtension.class)
class EmployeeSpendReportOutPortImplTest {

  private static final String EMAIL = "some@email.com";
  private static final String COMPANY_ACCOUNT_ID = "COMP_5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String EMPLOYEE_ACCOUNT_ID = "EMP_5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String FROM_MONTH_YEAR = "11-2024";
  private static final String TO_MONTH_YEAR = "09-2025";
  private static final Integer YEAR = 2024;
  private static final Integer MONTH = 11;
  private static final Integer NUMBER_OF_BOOKINGS = 5;
  private static final BigDecimal BOOKING_VALUE = BigDecimal.valueOf(2369.36);

  @Mock
  private EmployeeSpendReportClient employeeSpendReportClient;

  @Mock
  private EmployeeSpendReportResponseMapper employeeSpendReportResponseMapper;

  @InjectMocks
  private EmployeeSpendReportOutPortImpl employeeSpendReportOutPort;

  @Test
  void getEmployeeSpend_WhenInvoked_ThenResponseIsMapped() {
    var clientResponse = createEmployeeSpendResponseList();
    var mappedResponse = createEmployeeSpendList();
    when(employeeSpendReportClient.getEmployeeSpendReport(any(), any(), any(), any(), any(), any()))
        .thenReturn(clientResponse);
    when(employeeSpendReportResponseMapper.toDto(clientResponse)).thenReturn(mappedResponse);

    var response = employeeSpendReportOutPort.getEmployeeSpend(createEmployeeSpendRequest());

    assertThat(response, notNullValue());
    assertEquals(mappedResponse, response);
    verify(employeeSpendReportClient).getEmployeeSpendReport(
        COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, FROM_MONTH_YEAR, TO_MONTH_YEAR, "PI", EMAIL);
    verify(employeeSpendReportResponseMapper).toDto(clientResponse);
  }

  private EmployeeSpendRequest createEmployeeSpendRequest() {
    return EmployeeSpendRequest.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR)
        .accessContext("PI")
        .accessedBy(EMAIL)
        .build();
  }

  private List<EmployeeSpendReportResponse> createEmployeeSpendResponseList() {
    return List.of(EmployeeSpendReportResponse.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .year(YEAR)
        .month(MONTH)
        .noOfBookings(NUMBER_OF_BOOKINGS)
        .bookingValue(BOOKING_VALUE)
        .build());
  }

  private List<EmployeeSpendReport> createEmployeeSpendList() {
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
