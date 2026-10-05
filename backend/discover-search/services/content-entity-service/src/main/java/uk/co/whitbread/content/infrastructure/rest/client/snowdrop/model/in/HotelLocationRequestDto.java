package uk.co.whitbread.content.infrastructure.rest.client.snowdrop.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelLocationRequestDto {
  private Double latitude;
  private Double longitude;
  private String radius;
  private String radiusUnit;
}
