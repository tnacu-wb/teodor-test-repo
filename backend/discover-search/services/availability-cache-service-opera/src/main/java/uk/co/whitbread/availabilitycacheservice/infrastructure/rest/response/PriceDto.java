package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceDto {

  private BigDecimal amount;

  private String currency;
}
