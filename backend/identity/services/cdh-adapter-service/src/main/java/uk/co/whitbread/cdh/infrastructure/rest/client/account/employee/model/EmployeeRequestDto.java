package uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequestDto {

  private String companyAccountId;
  private String employeeAccountId;
  private String accessContext;
  private String accessedBy;
}

