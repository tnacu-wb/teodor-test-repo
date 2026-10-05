package uk.co.whitbread.company.employee.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
@Data
public class EmployeeActivationResponse extends Employee  {
  private String companyId;
  private String companyName;
}
