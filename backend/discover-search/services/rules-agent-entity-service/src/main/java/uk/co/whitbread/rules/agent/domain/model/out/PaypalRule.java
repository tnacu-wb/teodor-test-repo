package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class PaypalRule extends Rule {

  @NotEmpty
  String channelId;

  @NotEmpty
  String countryCode;

  @NotEmpty
  String hotelId;




  private PaypalRule(final PaypalRule.PaypalRuleBuilder<?, ?> b) {
    super(b);
    this.channelId = b.channelId;
    this.countryCode = b.countryCode;
    this.hotelId = b.hotelId;
    this.validateSelf();
  }
}
