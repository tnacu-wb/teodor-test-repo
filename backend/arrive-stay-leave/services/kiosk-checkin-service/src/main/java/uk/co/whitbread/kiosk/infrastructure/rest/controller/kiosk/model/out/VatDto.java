package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VatDto {

  private String vatRate;
  private BigDecimal gross;
  private BigDecimal net;
  private String vatMessage;
  private String vatNumber;

}
