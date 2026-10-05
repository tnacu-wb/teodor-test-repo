package uk.co.whitbread.availabilitycacheservice.domain.model.snowdrop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelDetails {

  private String code;
  private String name;
  private String brand;
  private Double distance;
  private ItemLatLon location;
}