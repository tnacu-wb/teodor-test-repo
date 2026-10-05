package uk.co.whitbread.reservation.domain.model.payment.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreAuthCharges {

  private boolean display;
  private List<String> charges;
}
