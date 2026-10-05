package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationChannelDto {

  private String channel;
  private String subchannel;
}
