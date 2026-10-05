package uk.co.whitbread.basket.domain.model.basket.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrepaidDepositsRequest {
  private List<PrepaidDeposit> prepaidDeposits;

}
