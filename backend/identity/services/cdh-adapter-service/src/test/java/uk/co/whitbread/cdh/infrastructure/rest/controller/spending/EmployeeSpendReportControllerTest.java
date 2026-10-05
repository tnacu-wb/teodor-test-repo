package uk.co.whitbread.cdh.infrastructure.rest.controller.spending;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import uk.co.whitbread.cdh.domain.ports.primary.EmployeeSpendReportInPort;
import uk.co.whitbread.cdh.infrastructure.rest.controller.spending.mapper.EmployeeSpendMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.spending.model.in.EmployeeSpendRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.spending.model.out.EmployeeSpendResponseDto;

@ExtendWith(MockitoExtension.class)
class EmployeeSpendReportControllerTest {

  @InjectMocks
  private EmployeeSpendReportController controller;

  @Mock
  private EmployeeSpendReportInPort employeeSpendReportInPort;

  @Mock
  private EmployeeSpendMapper employeeSpendMapper;

  @Test
  void testGetEmployeeSpendReport() {
    // Given
    String companyAccountId = "COMP123";
    String employeeAccountId = "EMP456";

    EmployeeSpendRequestDto requestDto = EmployeeSpendRequestDto.builder()
        .fromMonthYear("01-2024")
        .toMonthYear("03-2026")
        .accessContext("test-context")
        .accessedBy("test-user")
        .build();

    EmployeeSpendRequest domainRequest = EmployeeSpendRequest.builder()
        .companyAccountId(companyAccountId)
        .employeeAccountId(employeeAccountId)
        .fromMonthYear("01-2024")
        .toMonthYear("03-2026")
        .accessContext("test-context")
        .accessedBy("test-user")
        .build();

    List<EmployeeSpendReport> domainReports = Arrays.asList(
        EmployeeSpendReport.builder()
            .companyAccountId(companyAccountId)
            .employeeAccountId(employeeAccountId)
            .year(2024)
            .month(9)
            .noOfBookings(2)
            .bookingValue(96.00)
            .bookingCurrency("EUR")
            .build()
    );

    List<EmployeeSpendResponseDto> expectedResponse = Arrays.asList(
        EmployeeSpendResponseDto.builder()
            .companyAccountId(companyAccountId)
            .employeeAccountId(employeeAccountId)
            .year(2024)
            .month(9)
            .noOfBookings(2)
            .bookingValue(96.00)
            .bookingCurrency("EUR")
            .build()
    );

    when(employeeSpendMapper.toModel(eq(companyAccountId), eq(employeeAccountId), any(EmployeeSpendRequestDto.class)))
        .thenReturn(domainRequest);
    when(employeeSpendReportInPort.getEmployeeSpendReport(domainRequest))
        .thenReturn(domainReports);
    when(employeeSpendMapper.toDto(domainReports))
        .thenReturn(expectedResponse);

    // When
    List<EmployeeSpendResponseDto> result = controller.getEmployeeSpendReport(
        companyAccountId, employeeAccountId, requestDto);

    // Then
    assertThat(result).isNotNull().hasSize(1);
    assertThat(result.getFirst().getCompanyAccountId()).isEqualTo(companyAccountId);
    assertThat(result.getFirst().getEmployeeAccountId()).isEqualTo(employeeAccountId);
    assertThat(result.getFirst().getYear()).isEqualTo(2024);
    assertThat(result.getFirst().getMonth()).isEqualTo(9);

    verify(employeeSpendMapper).toModel(companyAccountId, employeeAccountId, requestDto);
    verify(employeeSpendReportInPort).getEmployeeSpendReport(domainRequest);
    verify(employeeSpendMapper).toDto(domainReports);
  }
}
