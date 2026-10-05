package uk.co.whitbread.availabilitycacheservice.domain.model.availabilities;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.availabilitycacheservice.domain.model.common.CommonHotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.hotelprice.BestRoomPrice;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Hotel extends CommonHotel {

  private String hotelName;

  private String hotelBrand;

  private Double distance;

  private Boolean arrivalDateToday;

  private Boolean euroCurrencyHotel;

  private String unit;

  private List<RatePlan> rates;

  private BestRoomPrice bestRoomPrice;

  private String date;

  private String placeId;

  private String pmsSource;

}
