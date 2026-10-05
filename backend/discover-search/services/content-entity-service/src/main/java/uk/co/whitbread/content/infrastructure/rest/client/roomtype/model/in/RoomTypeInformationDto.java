package uk.co.whitbread.content.infrastructure.rest.client.roomtype.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomTypeInformationDto {

  private String roomTypeCode;
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
