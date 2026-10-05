package uk.co.whitbread.booking.domain.model.information.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HotelInformationRequest {

  String hotelId;
  String channel;
  String subChannel;
  String country;
  String language;
}
