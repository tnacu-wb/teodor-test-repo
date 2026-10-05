package uk.co.whitbread.content.domain.model.hotel.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TabItem {

  private String roomType;
  private String roomName;
  private String roomDescription;
  private String roomTypeCode;
  private String fileReference;
  private String rateGridRoomDescription;
  private List<HotelFacility> facilities;
  private List<GalleryImage> images;
}

