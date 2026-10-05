package uk.co.whitbread.content.domain.model.globalconfig.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomClassConfig {

  private List<RoomClassOrder> roomClassConfig;
}
