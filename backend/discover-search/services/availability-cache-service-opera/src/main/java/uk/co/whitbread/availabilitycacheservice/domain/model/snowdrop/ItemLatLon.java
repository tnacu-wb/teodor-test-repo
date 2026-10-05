package uk.co.whitbread.availabilitycacheservice.domain.model.snowdrop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemLatLon {

  private double latitude;
  private double longitude;

}