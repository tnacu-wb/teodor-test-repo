package uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomOperaDistrDto {

  private String roomType;

  private Boolean cotRequired;

  private Integer qtyRequested;

  private List<AvailableCostsDistrDto> availableCosts;

}
