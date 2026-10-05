package uk.co.whitbread.cdh.domain.model.spending;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSpendReport {

  private String companyAccountId;
  
  private String employeeAccountId;
  
  private Integer year;
  
  private Integer month;
  
  private Integer noOfBookings;
  
  private Double bookingValue;
  
  private String bookingCurrency;
}
