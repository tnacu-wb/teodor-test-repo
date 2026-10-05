package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepositFoliosResponse {
  private List<DepositFolio> depositFolios;
}
