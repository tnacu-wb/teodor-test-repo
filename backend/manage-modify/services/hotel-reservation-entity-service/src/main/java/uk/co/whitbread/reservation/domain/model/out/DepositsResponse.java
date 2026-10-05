package uk.co.whitbread.reservation.domain.model.out;

import java.util.List;
import lombok.Data;

@Data
public class DepositsResponse {

  public List<Deposits> deposits;

}