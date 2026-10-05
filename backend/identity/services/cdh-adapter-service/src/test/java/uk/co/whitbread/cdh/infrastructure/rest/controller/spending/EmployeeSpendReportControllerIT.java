package uk.co.whitbread.cdh.infrastructure.rest.controller.spending;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendReport;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendRequest;
import uk.co.whitbread.cdh.domain.ports.primary.EmployeeSpendReportInPort;
import uk.co.whitbread.cdh.infrastructure.rest.controller.spending.mapper.EmployeeSpendMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.spending.model.in.EmployeeSpendRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.spending.model.out.EmployeeSpendResponseDto;

@WebMvcTest(EmployeeSpendReportController.class)
class EmployeeSpendReportControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private EmployeeSpendReportInPort employeeSpendReportInPort;

  @MockitoBean
  private EmployeeSpendMapper employeeSpendMapper;

  @Test
  void testGetEmployeeSpendReport_Success() throws Exception {
    // Given
    String companyAccountId = "COMP123";
    String employeeAccountId = "EMP456";

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
            .year(2025)
            .month(9)
            .noOfBookings(2)
            .bookingValue(96.00)
            .bookingCurrency("EUR")
            .build()
    );

    List<EmployeeSpendResponseDto> responseDtos = Arrays.asList(
        EmployeeSpendResponseDto.builder()
            .companyAccountId(companyAccountId)
            .employeeAccountId(employeeAccountId)
            .year(2025)
            .month(9)
            .noOfBookings(2)
            .bookingValue(96.00)
            .bookingCurrency("EUR")
            .build()
    );

    when(employeeSpendMapper.toModel(any(), any(), any(EmployeeSpendRequestDto.class)))
        .thenReturn(domainRequest);
    when(employeeSpendReportInPort.getEmployeeSpendReport(any(EmployeeSpendRequest.class)))
        .thenReturn(domainReports);
    when(employeeSpendMapper.toDto(any(List.class)))
        .thenReturn(responseDtos);

    // When & Then
    mockMvc.perform(get("/v1/cdh/companies/{companyAccountId}/employees/{employeeAccountId}/reports/employee-spend",
            companyAccountId, employeeAccountId)
            .param("fromMonthYear", "01-2024")
            .param("toMonthYear", "03-2026")
            .param("accessContext", "test-context")
            .param("accessedBy", "test-user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].companyAccountId").value(companyAccountId))
        .andExpect(jsonPath("$[0].employeeAccountId").value(employeeAccountId))
        .andExpect(jsonPath("$[0].year").value(2025))
        .andExpect(jsonPath("$[0].month").value(9))
        .andExpect(jsonPath("$[0].bookingValue").value(96.00));
  }

  @Test
  void testGetEmployeeSpendReport_InvalidMonthYearFormat() throws Exception {
    // When & Then
    mockMvc.perform(get("/v1/cdh/companies/{companyAccountId}/employees/{employeeAccountId}/reports/employee-spend",
            "COMP123", "EMP456")
            .param("fromMonthYear", "2024-01")  // Invalid format
            .param("toMonthYear", "03-2026")
            .param("accessContext", "test-context")
            .param("accessedBy", "test-user"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testGetEmployeeSpendReport_MissingRequiredParameter() throws Exception {
    // When & Then - missing accessContext
    mockMvc.perform(get("/v1/cdh/companies/{companyAccountId}/employees/{employeeAccountId}/reports/employee-spend",
            "COMP123", "EMP456")
            .param("fromMonthYear", "01-2024")
            .param("toMonthYear", "03-2026")
            .param("accessedBy", "test-user"))
        .andExpect(status().isBadRequest());
  }
}
