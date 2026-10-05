package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingChannel {

  public static final String PI_BOOKING_CHANNEL = "PI";
  public static final String BB_BOOKING_CHANNEL = "BB";
  public static final String CCUI_BOOKING_CHANNEL = "CCUI";
  public static final String DISTR_BOOKING_CHANNEL = "DISTR";
  public static final String KIOSK_BOOKING_CHANNEL = "KIOSK";
  public static final String WEB_SUBCHANNEL = "WEB";

  @NotEmpty
  private String channel;

  @NotEmpty
  private String subchannel;

  private String language;

  public Boolean isCcui() {
    return CCUI_BOOKING_CHANNEL.equals(this.getChannel());
  }

  public Boolean isPi() {
    return PI_BOOKING_CHANNEL.equals(this.getChannel());
  }

  public Boolean isBb() {
    return BB_BOOKING_CHANNEL.equals(this.getChannel());
  }

  public Boolean isDistr() {
    return DISTR_BOOKING_CHANNEL.equals(this.getChannel());
  }

  public Boolean isKiosk() {
    return KIOSK_BOOKING_CHANNEL.equals(this.getChannel());
  }

  public Boolean isWebSubchannel() {
    return WEB_SUBCHANNEL.equals(this.getSubchannel());
  }

  public Boolean isAllowedFixedRate() {
    return isDistr();
  }
}