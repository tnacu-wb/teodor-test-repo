package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.distribution;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DistributionRoomDto {

  private String roomType;

  private Boolean cotRequired;

  private int qtyRequested;

  private List<AvailableCostDto> availableCosts;

}
