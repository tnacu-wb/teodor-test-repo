package uk.co.whitbread.rules.agent.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class PaypalRuleRequest extends ModelValidator<PaypalRuleRequest> {

  @NotEmpty
  String channelId;

  @NotEmpty
  String country;

  @NotEmpty
  String hotelId;


  public PaypalRuleRequest(String channelId, String country, String hotelId) {
    this.channelId = channelId;
    this.country = country;
    this.hotelId = hotelId;
    this.validateSelf();
  }
}
