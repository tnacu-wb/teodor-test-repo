package uk.co.whitbread.company.employee.utils;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmployeeHeaderDetails {

  private final String sessionId;
  private final String companyId;
  private final String employeeId;
}
