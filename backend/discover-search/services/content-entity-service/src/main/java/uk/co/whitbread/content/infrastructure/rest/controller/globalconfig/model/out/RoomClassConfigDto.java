package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomClassConfigDto {

  private List<RoomClassOrderDto> roomClassConfig;
}
