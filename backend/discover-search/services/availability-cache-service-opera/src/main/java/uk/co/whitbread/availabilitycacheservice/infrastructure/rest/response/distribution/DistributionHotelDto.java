package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.distribution;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DistributionHotelDto {

  private String hotelCode;

  private String hotelName;

  private String hotelBrand;

  private Boolean available;

  private Boolean arrivalDateToday;

  private String pmsSource;

  private List<DistributionRatePlanDto> rates;

}
