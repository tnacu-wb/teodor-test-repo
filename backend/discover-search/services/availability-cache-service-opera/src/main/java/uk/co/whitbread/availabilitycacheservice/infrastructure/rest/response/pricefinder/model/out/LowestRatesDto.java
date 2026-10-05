package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out;

import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LowestRatesDto {

  @JsonView({HotelNameView.class, HotelCodeView.class})
  private LocalDate availableDate;
  @JsonView({HotelNameView.class, HotelCodeView.class})
  private BigDecimal minimumRate;

}
