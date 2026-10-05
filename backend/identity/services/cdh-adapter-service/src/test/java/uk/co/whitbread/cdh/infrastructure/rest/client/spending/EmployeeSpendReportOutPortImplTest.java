package uk.co.whitbread.cdh.infrastructure.rest.client.spending;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendReport;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendRequest;

@ExtendWith(MockitoExtension.class)
class EmployeeSpendReportOutPortImplTest {

  @Mock
  private EmployeeSpendReportClient employeeSpendReportClient;

  private EmployeeSpendReportOutPortImpl outPort;

  @BeforeEach
  void setUp() {
    outPort = new EmployeeSpendReportOutPortImpl(employeeSpendReportClient);
  }

  @Test
  void testGetEmployeeSpendReport_Success() {
    // Given
    EmployeeSpendRequest request = EmployeeSpendRequest.builder()
        .companyAccountId("COMP123")
        .employeeAccountId("EMP456")
        .fromMonthYear("01-2024")
        .toMonthYear("03-2026")
        .accessContext("test-context")
        .accessedBy("test-user")
        .build();

    List<EmployeeSpendReport> domainReports = Arrays.asList(
        EmployeeSpendReport.builder()
            .companyAccountId("COMP123")
            .employeeAccountId("EMP456")
            .year(2025)
            .month(9)
            .noOfBookings(2)
            .bookingValue(96.00)
            .bookingCurrency("EUR")
            .build(),
        EmployeeSpendReport.builder()
            .companyAccountId("COMP123")
            .employeeAccountId("EMP456")
            .year(2026)
            .month(2)
            .noOfBookings(1)
            .bookingValue(71.00)
            .bookingCurrency("GBP")
            .build()
    );

    when(employeeSpendReportClient.getEmployeeSpendReport(request)).thenReturn(domainReports);

    // When
    var result = outPort.getEmployeeSpendReport(request);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("COMP123", result.getFirst().getCompanyAccountId());
    assertEquals("EMP456", result.getFirst().getEmployeeAccountId());
    assertEquals(2025, result.getFirst().getYear());
    assertEquals(9, result.getFirst().getMonth());

    verify(employeeSpendReportClient).getEmployeeSpendReport(request);
  }
}
