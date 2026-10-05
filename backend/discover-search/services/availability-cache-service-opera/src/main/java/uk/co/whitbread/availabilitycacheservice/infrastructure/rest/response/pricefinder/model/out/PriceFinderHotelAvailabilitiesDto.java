package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceFinderHotelAvailabilitiesDto {

  @JsonView({HotelNameView.class})
  private PriceFinderRateDto lowestMonthlyRate;
  @JsonView({HotelNameView.class, HotelCodeView.class})
  private List<PriceFinderOperaHotelAvailabilitiesDto> priceFinderOperaHotelAvailabilitiesDtoList;
  @JsonView({HotelNameView.class})
  private Integer page;
  @JsonView({HotelNameView.class})
  private Integer pageSize;
  @JsonView({HotelNameView.class})
  private Integer total;
}
