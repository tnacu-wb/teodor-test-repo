package uk.co.whitbread.domain.model.availability.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingChannel {
  public static final String PI_BOOKING_CHANNEL = "PI";
  public static final String BB_BOOKING_CHANNEL = "BB";
  public static final String CCUI_BOOKING_CHANNEL = "CCUI";
  public static final String DISTR_BOOKING_CHANNEL = "DISTR";

  public static final String WEB_BOOKING_SUBCHANNEL = "WEB";
  public static final String MOBILE_BOOKING_SUBCHANNEL = "MOBILE";

  private String channel;
  private String subchannel;
  private String language;
}
