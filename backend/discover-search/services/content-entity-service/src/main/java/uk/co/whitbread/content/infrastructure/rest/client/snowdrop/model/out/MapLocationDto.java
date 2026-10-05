package uk.co.whitbread.content.infrastructure.rest.client.snowdrop.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MapLocationDto {

  private Double latitude;
  private Double longitude;
}
