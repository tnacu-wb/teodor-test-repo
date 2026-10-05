package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class ChannelRuleRequestDetails extends ModelValidator<ChannelRuleRequestDetails> {

  @NotEmpty
  String channel;
  @NotEmpty
  String subchannel;
  @NotEmpty
  String language;
  @NotEmpty
  String pms;

  public ChannelRuleRequestDetails(String channel, String subchannel,
      String language, String pms) {
    this.channel = channel;
    this.subchannel = subchannel;
    this.language = language;
    this.pms = pms;
    this.validateSelf();
  }
}
