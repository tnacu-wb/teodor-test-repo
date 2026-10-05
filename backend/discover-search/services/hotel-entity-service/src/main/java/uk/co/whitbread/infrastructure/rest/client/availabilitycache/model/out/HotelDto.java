package uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDto {

  private String hotelCode;

  private String hotelName;

  private String hotelBrand;

  private Boolean available;

  private Boolean limitedAvailability;

  private Boolean arrivalDateToday;

  private List<RatePlanDto> rates;

}
