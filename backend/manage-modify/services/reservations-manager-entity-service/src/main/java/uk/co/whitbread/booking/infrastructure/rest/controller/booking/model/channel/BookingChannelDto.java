package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingChannelDto {

  private String channel;
  private String subchannel;
}
