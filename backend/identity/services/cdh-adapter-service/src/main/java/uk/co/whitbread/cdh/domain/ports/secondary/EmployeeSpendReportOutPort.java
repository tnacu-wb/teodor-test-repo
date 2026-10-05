package uk.co.whitbread.cdh.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendReport;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendRequest;

public interface EmployeeSpendReportOutPort {

  List<EmployeeSpendReport> getEmployeeSpendReport(EmployeeSpendRequest request);
}
