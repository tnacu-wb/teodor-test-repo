package uk.co.whitbread.cdh.domain.logic.spending;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendReport;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendRequest;
import uk.co.whitbread.cdh.domain.ports.primary.EmployeeSpendReportInPort;
import uk.co.whitbread.cdh.domain.ports.secondary.EmployeeSpendReportOutPort;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmployeeSpendReportService implements EmployeeSpendReportInPort {

  private final EmployeeSpendReportOutPort employeeSpendReportOutPort;

  @Override
  public List<EmployeeSpendReport> getEmployeeSpendReport(EmployeeSpendRequest request) {
    log.info("Getting employee spend report for companyAccountId={}, employeeAccountId={}, "
            + "fromMonthYear={}, toMonthYear={}",
        request.getCompanyAccountId(),
        request.getEmployeeAccountId(),
        request.getFromMonthYear(),
        request.getToMonthYear());
    
    return this.employeeSpendReportOutPort.getEmployeeSpendReport(request);
  }
}
