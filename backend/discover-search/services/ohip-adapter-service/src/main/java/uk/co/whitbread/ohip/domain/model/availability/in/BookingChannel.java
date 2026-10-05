package uk.co.whitbread.ohip.domain.model.availability.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingChannel {

  private String channel;
  private String subchannel;
  private String language;
}
