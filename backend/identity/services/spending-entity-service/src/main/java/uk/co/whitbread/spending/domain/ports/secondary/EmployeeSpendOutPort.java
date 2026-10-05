package uk.co.whitbread.spending.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.spending.domain.model.in.EmployeeSpendRequest;
import uk.co.whitbread.spending.domain.model.out.cdh.EmployeeSpendReport;

public interface EmployeeSpendOutPort {

  List<EmployeeSpendReport> getEmployeeSpend(EmployeeSpendRequest employeeSpendRequest);
}
