package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
public class ReservationCancelRequestDto {

  private String hotelId;
  private String basketReference;

  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
  private String userDateTime;

  private String country;
  private String language;
  private String channel;
  private String subchannel;
  private String token;

}
