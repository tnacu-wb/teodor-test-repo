package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

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

  private List<String> renditions;
  private String roomType;
  private String roomTitle;
  private String roomDescriptionText;
  private String roomTypeCode;
  private String fileReference;
  private String rateGridRoomDescription;
  private List<NewFacility> newFacilities;
  private String readMoreLabel;
  private String roomInfoLabel;
  private String roomInfoText;
  private String room;
  private List<Facility> facilityList;
  private List<TopSectionImage> images;
}

