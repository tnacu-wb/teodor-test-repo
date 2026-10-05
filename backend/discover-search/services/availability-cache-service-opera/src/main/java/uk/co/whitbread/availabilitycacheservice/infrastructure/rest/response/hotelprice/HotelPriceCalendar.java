package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HotelPriceCalendar {

  private String date;

  private BigDecimal price;

}
