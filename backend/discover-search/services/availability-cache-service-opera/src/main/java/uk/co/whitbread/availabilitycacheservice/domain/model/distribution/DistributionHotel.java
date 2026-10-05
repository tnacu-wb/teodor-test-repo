package uk.co.whitbread.availabilitycacheservice.domain.model.distribution;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.availabilitycacheservice.domain.model.common.CommonHotel;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class DistributionHotel extends CommonHotel {

  private String hotelName;

  private String hotelBrand;

  private Boolean arrivalDateToday;

  private String pmsSource;

  private List<DistributionRatePlan> rates;

}
