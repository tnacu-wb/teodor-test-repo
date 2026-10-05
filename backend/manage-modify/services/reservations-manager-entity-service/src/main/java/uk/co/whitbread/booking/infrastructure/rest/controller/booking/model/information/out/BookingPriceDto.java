package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.out;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingPriceDto {

  private BigDecimal amount;
  private String currency;
}
