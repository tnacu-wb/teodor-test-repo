package uk.co.whitbread.dashboard.infrastructure.rest.client.content.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeInformationDto {

  private List<String> roomTypeCode;
  private String roomCategory;
  private String roomLabel;
  private String roomDescription;
  private String roomInfoLabel;
  private String roomInfo;
  private String gridImage;
  private String roomImage;
  private String groupId;
  private List<String> facilities;

}
