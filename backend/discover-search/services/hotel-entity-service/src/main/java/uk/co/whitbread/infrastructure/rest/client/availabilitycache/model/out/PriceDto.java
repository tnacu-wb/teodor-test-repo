package uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceDto {

  private BigDecimal amount;

  private String currency;
}
