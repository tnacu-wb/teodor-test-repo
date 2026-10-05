package uk.co.whitbread.domain.model.availabilitycache.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelResponse {

  private String hotelCode;
  private String hotelName;
  private String distance;
  private String hotelBrand;
  private Boolean available;
  private Boolean limitedAvailability;
  private Boolean arrivalDateToday;
  private List<RatePlanResponse> rates;
}
