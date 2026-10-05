package uk.co.whitbread.basket.domain.model.basket.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PrepaidDeposits {
  private List<PrepaidDeposit> prepaidDeposits;
}
