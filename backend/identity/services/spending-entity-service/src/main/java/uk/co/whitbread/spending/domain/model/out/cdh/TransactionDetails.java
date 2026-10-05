package uk.co.whitbread.spending.domain.model.out.cdh;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetails {

  private BigDecimal totalBookingValue;
  private List<Transaction> transactions;
  private Paging paging;
}
