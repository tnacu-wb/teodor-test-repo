package uk.co.whitbread.availabilitycacheservice.domain.model.distribution;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DistributionHotelAvailabilities {

  private int total;

  private List<DistributionHotel> operaHotelAvailabilities;

}
