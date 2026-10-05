package uk.co.whitbread.domain.model.hotel.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelInfo {

  private String threeLetterId;
  private String hotelCountryCode;
  private String hotelTimeZone;
  private String currencyCode;
  private String languageCode;
  private String checkInTime;
  private String checkOutTime;

}
