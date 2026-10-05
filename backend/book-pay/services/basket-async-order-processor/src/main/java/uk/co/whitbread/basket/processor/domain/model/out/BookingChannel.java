package uk.co.whitbread.basket.processor.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BookingChannel {

  private String channel;
  private String subchannel;
  private String language;
}