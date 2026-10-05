package uk.co.whitbread.availabilitycacheservice.domain.model.distribution;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DistributionRoom {

  private String roomType;

  private Boolean cotRequired;

  private int qtyRequested;

  private List<AvailableCost> availableCosts;

}
