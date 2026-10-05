package uk.co.whitbread.availabilitycacheservice.infrastructure.model.snowdrop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ItemLatLonDto {

  private double latitude;

  private double longitude;

}
