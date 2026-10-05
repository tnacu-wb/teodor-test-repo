package uk.co.whitbread.basket.domain.model.basket.in;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargeAmount implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private BigDecimal amount;
  private String currencyCode;

}
