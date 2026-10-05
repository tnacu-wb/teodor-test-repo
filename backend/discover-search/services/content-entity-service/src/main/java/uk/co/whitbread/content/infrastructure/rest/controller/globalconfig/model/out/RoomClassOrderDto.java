package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomClassOrderDto {

  private String code;
  private int order;
  private List<String> availableUpgrades;
}
