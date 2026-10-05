package uk.co.whitbread.cdh.domain.ports.primary;

import java.util.List;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendReport;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendRequest;

public interface EmployeeSpendReportInPort {

  List<EmployeeSpendReport> getEmployeeSpendReport(EmployeeSpendRequest request);
}
