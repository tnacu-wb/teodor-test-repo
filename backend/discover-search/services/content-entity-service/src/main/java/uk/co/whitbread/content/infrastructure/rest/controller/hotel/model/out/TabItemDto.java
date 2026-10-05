package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TabItemDto {

  private String roomType;
  private String roomName;
  private String roomDescription;
  private String roomTypeCode;
  private String fileReference;
  private String rateGridRoomDescription;
  private List<HotelFacilityDto> facilities;
  private List<GalleryImageDto> images;
}

