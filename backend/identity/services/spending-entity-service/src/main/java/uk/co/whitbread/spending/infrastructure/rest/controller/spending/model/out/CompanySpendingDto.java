package uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySpendingDto {

  private String companyAccountId;
  private Integer year;
  private Integer month;
  private Integer noOfBookings;
  private BigDecimal bookingValue;
  private String bookingCurrency;

}
