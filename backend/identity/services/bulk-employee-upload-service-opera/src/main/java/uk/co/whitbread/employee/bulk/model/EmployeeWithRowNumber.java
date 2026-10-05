package uk.co.whitbread.employee.bulk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;

@Getter
@AllArgsConstructor
public class EmployeeWithRowNumber {
  private final int rowNumber;
  private final EmployeeAccountRequest employee;
}
