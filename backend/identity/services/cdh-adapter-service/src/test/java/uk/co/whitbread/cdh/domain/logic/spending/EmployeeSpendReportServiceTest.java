package uk.co.whitbread.cdh.domain.logic.spending;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendReport;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendRequest;
import uk.co.whitbread.cdh.domain.ports.secondary.EmployeeSpendReportOutPort;

@ExtendWith(MockitoExtension.class)
class EmployeeSpendReportServiceTest {

  @InjectMocks
  private EmployeeSpendReportService employeeSpendReportService;

  @Mock
  private EmployeeSpendReportOutPort employeeSpendReportOutPort;

  @Test
  void testGetEmployeeSpendReport() {
    // Given
    EmployeeSpendRequest request = EmployeeSpendRequest.builder()
        .companyAccountId("COMP123")
        .employeeAccountId("EMP456")
        .fromMonthYear("01-2024")
        .toMonthYear("03-2026")
        .accessContext("test-context")
        .accessedBy("test-user")
        .build();

    List<EmployeeSpendReport> expectedReports = Arrays.asList(
        EmployeeSpendReport.builder()
            .companyAccountId("COMP123")
            .employeeAccountId("EMP456")
            .year(2024)
            .month(1)
            .noOfBookings(5)
            .bookingValue(500.00)
            .bookingCurrency("GBP")
            .build(),
        EmployeeSpendReport.builder()
            .companyAccountId("COMP123")
            .employeeAccountId("EMP456")
            .year(2024)
            .month(2)
            .noOfBookings(3)
            .bookingValue(300.00)
            .bookingCurrency("GBP")
            .build()
    );

    when(employeeSpendReportOutPort.getEmployeeSpendReport(any(EmployeeSpendRequest.class)))
        .thenReturn(expectedReports);

    // When
    List<EmployeeSpendReport> result = employeeSpendReportService.getEmployeeSpendReport(request);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("COMP123", result.get(0).getCompanyAccountId());
    assertEquals("EMP456", result.get(0).getEmployeeAccountId());
    assertEquals(2024, result.get(0).getYear());
    assertEquals(1, result.get(0).getMonth());
    assertEquals(500.00, result.get(0).getBookingValue());

    verify(employeeSpendReportOutPort).getEmployeeSpendReport(request);
  }
}
