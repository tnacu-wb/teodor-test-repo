package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out;

import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceFinderRateDto {

  @JsonView({HotelNameView.class})
  private BigDecimal price;
  @JsonView({HotelNameView.class})
  private String currency;
}