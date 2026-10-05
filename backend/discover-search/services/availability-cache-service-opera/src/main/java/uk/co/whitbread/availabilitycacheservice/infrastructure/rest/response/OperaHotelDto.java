package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperaHotelDto {

  private String hotelCode;

  private String hotelName;

  private String hotelBrand;

  private Boolean available;

  private Boolean hasMlosRestriction;

  private Boolean limitedAvailability;

  private Boolean arrivalDateToday;

  private String pmsSource;

  private List<OperaRatePlanDto> rates;

}
