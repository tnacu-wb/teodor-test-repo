package uk.co.whitbread.company.employee.model;

import lombok.Data;

@Data
public class InnBusinessEmployeeActivationResponse extends GetEmployeeActivationResponse {
  private String companyName;
}