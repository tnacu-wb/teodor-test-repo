package uk.co.whitbread.infrastructure.rest.client.availability.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelInventoryRequestOhipDto {

  private String hotelId;
  private String dateRangeStart;
  private String dateRangeEnd;

}
