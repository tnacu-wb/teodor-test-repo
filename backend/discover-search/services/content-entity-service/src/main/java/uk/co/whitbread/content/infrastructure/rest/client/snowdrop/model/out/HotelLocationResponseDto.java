package uk.co.whitbread.content.infrastructure.rest.client.snowdrop.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelLocationResponseDto {

  private String code;
  private String distance;
  private String name;
  private String brand;
  private MapLocationDto location;
}
