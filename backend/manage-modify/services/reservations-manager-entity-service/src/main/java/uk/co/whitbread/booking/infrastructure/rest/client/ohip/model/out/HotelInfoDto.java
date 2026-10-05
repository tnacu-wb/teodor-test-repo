package uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HotelInfoDto {

  private String hotelTimeZone;
  private String hotelCountryCode;
  private String hotelId;

}
