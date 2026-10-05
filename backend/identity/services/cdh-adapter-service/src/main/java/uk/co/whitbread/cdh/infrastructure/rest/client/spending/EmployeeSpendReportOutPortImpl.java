package uk.co.whitbread.cdh.infrastructure.rest.client.spending;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendReport;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendRequest;
import uk.co.whitbread.cdh.domain.ports.secondary.EmployeeSpendReportOutPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeSpendReportOutPortImpl implements EmployeeSpendReportOutPort {

  private final EmployeeSpendReportClient employeeSpendReportClient;

  @Override
  public List<EmployeeSpendReport> getEmployeeSpendReport(EmployeeSpendRequest request) {
    return employeeSpendReportClient.getEmployeeSpendReport(request);
  }
}
