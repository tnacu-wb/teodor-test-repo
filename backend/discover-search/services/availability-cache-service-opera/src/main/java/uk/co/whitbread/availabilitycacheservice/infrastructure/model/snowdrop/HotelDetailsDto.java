package uk.co.whitbread.availabilitycacheservice.infrastructure.model.snowdrop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelDetailsDto {

  private String code;

  private String name;

  private String brand;

  private Double distance;

  private ItemLatLonDto location;

}
