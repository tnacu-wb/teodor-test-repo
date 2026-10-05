package uk.co.whitbread.reservation.domain.model.availability.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BookingChannel {

  private String channel;
  private String subchannel;
  private String language;
}
