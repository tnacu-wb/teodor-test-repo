package uk.co.whitbread.rules.agent.domain.model.in;

import static uk.co.whitbread.rules.agent.domain.constants.RuleConstants.NO_LANGUAGE;

import jakarta.validation.constraints.NotEmpty;
import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;


@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class ChannelRuleRequest extends ModelValidator<ChannelRuleRequest> {

  @NotEmpty
  String channel;
  @NotEmpty
  String subchannel;
  String language;
  @NotEmpty
  String pms;

  public ChannelRuleRequest(String channel, String subchannel,
      String language, String pms) {
    this.channel = channel;
    this.subchannel = subchannel;
    this.language = Optional.ofNullable(language).orElse(NO_LANGUAGE);
    this.pms = pms;
    this.validateSelf();
  }
}
