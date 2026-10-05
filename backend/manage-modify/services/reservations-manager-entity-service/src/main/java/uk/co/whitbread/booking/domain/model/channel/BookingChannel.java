package uk.co.whitbread.booking.domain.model.channel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingChannel {

  private String channel;
  private String subchannel;
  private String language;
}
