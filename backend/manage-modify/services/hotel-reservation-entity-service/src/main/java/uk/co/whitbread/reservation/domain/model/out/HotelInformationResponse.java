package uk.co.whitbread.reservation.domain.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HotelInformationResponse {

  private String hotelTimeZone;
  private String hotelCountryCode;
}
