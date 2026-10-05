package uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingWidgetConfigDto {

  private Integer maxRooms;
  private Integer maxRoomsAmend;
  private Integer numberOfNights;
  private Integer maxArrivalDate;
  private List<AllowedRoomTypesByOccupancy> allowedRoomTypesByOccupancy;

}
