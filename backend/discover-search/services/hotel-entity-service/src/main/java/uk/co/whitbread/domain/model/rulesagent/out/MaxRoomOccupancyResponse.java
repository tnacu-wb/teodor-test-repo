package uk.co.whitbread.domain.model.rulesagent.out;

import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaxRoomOccupancyResponse {

  private String channelId;
  private List<MaxRoomOccupancyData> roomOccupancies = null;
  private Date generatedAt;
}
