package uk.co.whitbread.spending.domain.model.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingSpendingResponse {
  private LocalDate expectedSpendTodayDate;
  private BigDecimal expectedSpendToday;
  private LocalDate expectedNextBillingStartDate;
  private LocalDate expectedNextBillingEndDate;
  private BigDecimal expectedNextBilling;
  private LocalDate expectedNextPeriodStartDate;
  private LocalDate expectedNextPeriodEndDate;
  private BigDecimal expectedNextPeriod;
  private String currency;
  private String accountStatus;
}
