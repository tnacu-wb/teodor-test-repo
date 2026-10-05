package uk.co.whitbread.payments.infrastructure.rest.hotelentity.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelInfoDto {

  private String threeLetterId;
  private String hotelCountryCode;

}

