package uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelOperaDistrDto {

  private String hotelCode;

  private String hotelName;

  private String hotelBrand;

  private Boolean available;

  private Boolean limitedAvailability;

  private Boolean arrivalDateToday;

  private Boolean euroCurrencyHotel;

  private String pmsSource;

  private List<RatePlanOperaDistrDto> rates;

}
