package uk.co.whitbread.basket.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDeposits;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDepositsRequest;

public interface DepositFolioOutPort {

  void createBasketPrepaidDeposit(PrepaidDepositsRequest request);

  void updateBasketPrepaidDeposit(PrepaidDeposits prepaidDeposits);

  PrepaidDeposits getBasketPrepaidDeposit(String reservationId);

  PrepaidDeposits getBasketPrepaidDeposits(List<String> reservationIds);
}
