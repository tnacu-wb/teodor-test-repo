package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelDto {

  private String hotelCode;

  private String hotelName;

  private String hotelBrand;

  private Boolean available;

  private Boolean limitedAvailability;

  private Boolean arrivalDateToday;

  @JsonInclude(Include.NON_NULL)
  private Boolean euroCurrencyHotel;

  private String pmsSource;

  private List<RatePlanDto> rates;

  private Boolean hasMlosRestriction;

}
