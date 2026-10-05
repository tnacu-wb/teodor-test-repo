package uk.co.whitbread.spending.domain.model.out.cdh;

import java.math.BigDecimal;
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
  private BigDecimal bookingValue;
  private String bookingCurrency;
}
