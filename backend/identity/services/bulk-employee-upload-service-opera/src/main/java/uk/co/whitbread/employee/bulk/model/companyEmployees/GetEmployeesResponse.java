package uk.co.whitbread.employee.bulk.model.companyEmployees;

import java.util.List;
import lombok.Data;

@Data
public class GetEmployeesResponse {
  
  private String continuationToken;
  private Integer totalEmployeesInCompany;
  private List<GetEmployeeResponse> results = null;
}
