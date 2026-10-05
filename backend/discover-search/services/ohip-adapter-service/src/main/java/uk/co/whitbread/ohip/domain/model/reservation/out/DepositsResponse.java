package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.util.List;
import lombok.Data;

@Data
public class DepositsResponse {

  public List<Deposits> deposits;

}