package uk.co.whitbread.content.domain.model.hotel.out;

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
  private String hotelTimeZone;
  private String hotelCountryCode;
  private String currencyCode;
  private String languageCode;
  private String checkInTime;
  private String checkOutTime;
}