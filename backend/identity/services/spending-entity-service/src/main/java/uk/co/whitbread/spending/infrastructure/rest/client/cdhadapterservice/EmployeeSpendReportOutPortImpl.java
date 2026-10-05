package uk.co.whitbread.spending.infrastructure.rest.client.cdhadapterservice;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.spending.domain.model.in.EmployeeSpendRequest;
import uk.co.whitbread.spending.domain.model.out.cdh.EmployeeSpendReport;
import uk.co.whitbread.spending.domain.ports.secondary.EmployeeSpendOutPort;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper.EmployeeSpendReportResponseMapper;

@Slf4j
@RequiredArgsConstructor
public class EmployeeSpendReportOutPortImpl implements EmployeeSpendOutPort {

  private final EmployeeSpendReportClient employeeSpendReportClient;
  private final EmployeeSpendReportResponseMapper employeeSpendReportResponseMapper;

  @Override
  public List<EmployeeSpendReport> getEmployeeSpend(EmployeeSpendRequest employeeSpendRequest) {
    log.info("Retrieve employee spend from CDH adapter for company id {}, employee id {}",
        sanitizeInput(employeeSpendRequest.getCompanyAccountId()),
        sanitizeInput(employeeSpendRequest.getEmployeeAccountId()));

    var adapterResponse = employeeSpendReportClient.getEmployeeSpendReport(
        employeeSpendRequest.getCompanyAccountId(),
        employeeSpendRequest.getEmployeeAccountId(),
        employeeSpendRequest.getFromMonthYear(),
        employeeSpendRequest.getToMonthYear(),
        employeeSpendRequest.getAccessContext(),
        employeeSpendRequest.getAccessedBy());

    return employeeSpendReportResponseMapper.toDto(adapterResponse);
  }

  private String sanitizeInput(String input) {
    if (input == null) {
      return null;
    }
    return input.replace("\n", "").replace("\r", "");
  }
}
