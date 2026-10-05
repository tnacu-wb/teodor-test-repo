package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in;

import lombok.Builder;
import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Data
@Builder
public class ReservationBookingInfoRequestDto {

  private String resNo;
  private String arrivalDate;
  private String lastName;
  private String country;
  private String language;
  private String isOldBooking;
  private String channel;
  private String subchannel;

  public MultiValueMap<String, String> toMultiValueMap() {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("resNo", this.resNo);
    map.add("arrivalDate", this.arrivalDate);
    map.add("lastName", this.lastName);
    map.add("country", this.country);
    map.add("language", this.language);
    map.add("isOldBooking", this.isOldBooking);
    map.add("channel", this.channel);
    map.add("subchannel", this.subchannel);
    return map;
  }
}
