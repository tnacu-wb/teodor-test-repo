package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomUpgradesDto {

  private String roomClass;
  private String heading;
  private String description;
  private String imageUrl;
}
