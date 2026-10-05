package uk.co.whitbread.reservation.domain.model.payment.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRef {

  private boolean display;
  private String value;
}
