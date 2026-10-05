package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class HotelInventoryInput {

  private String hotelId;
  private String dateRangeStart;
  private String dateRangeEnd;
  private boolean dailyInventory;
  private int roomCountRequested;
  private boolean houseLevel;
}
