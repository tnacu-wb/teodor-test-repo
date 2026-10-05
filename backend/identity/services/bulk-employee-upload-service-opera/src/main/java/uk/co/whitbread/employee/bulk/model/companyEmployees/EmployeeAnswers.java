package uk.co.whitbread.employee.bulk.model.companyEmployees;

import java.util.List;
import lombok.Data;

@Data
public class EmployeeAnswers {
  
  private List<CompanyAnswer> companyAnswers = null;
  private String customerReferenceAnswer;
  private String purchaseOrderAnswer;
}
