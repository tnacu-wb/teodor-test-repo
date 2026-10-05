package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelInformationDto {

  private String country;
  private String language;
  private String hotelId;
}
