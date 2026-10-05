package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindReservationRequestDto {

  private String resNo;

  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
  private String arrivalDate;

  private String lastName;

  private String language;
  private String country;
  private String channel;
  private String subchannel;
}
