package uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomUpgradesDto {

  private String roomClass;
  private String heading;
  private String description;
  private String imageUrl;
}
