package uk.co.whitbread.dashboard.domain.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeInformation {

  private List<String> roomTypeCode;
  private String roomLabel;
  private String roomCategory;
  private String roomDescription;
  private String roomInfo;
  private String roomInfoLabel;
  private String gridImage;
  private String groupId;
  private String roomImage;
  private List<String> facilities;

}
