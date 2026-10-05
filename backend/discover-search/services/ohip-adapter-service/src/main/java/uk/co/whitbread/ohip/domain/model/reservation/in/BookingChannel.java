package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class BookingChannel implements SelfValidation<BookingChannel> {

  enum ChannelType {
    DISTRIBUTION("DISTR");

    private final String value;

    ChannelType(String s) {
      this.value = s;
    }

    public String value() {
      return value;
    }
  }

  @NotEmpty
  private String channel;
  @NotEmpty
  private String subchannel;
  private String language;

  public BookingChannel(String channel, String subchannel, String language) {
    this.channel = channel;
    this.subchannel = subchannel;
    this.language = language;
    this.validateSelf();
  }

  public boolean isAllowedFixedRate() {
    return ChannelType.DISTRIBUTION.value().equals(getChannel());
  }

}
