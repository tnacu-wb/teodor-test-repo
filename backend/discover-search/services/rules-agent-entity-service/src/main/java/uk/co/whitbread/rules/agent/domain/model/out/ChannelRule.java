package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ChannelRule extends Rule {

  @NotEmpty
  String pms;
  @NotEmpty
  String channel;
  @NotEmpty
  String subchannel;
  @NotEmpty
  String language;
  @NotEmpty
  String sourceId;
  @NotEmpty
  List<String> ratePlanSets;

  private ChannelRule(final ChannelRule.ChannelRuleBuilder<?, ?> b) {
    super(b);
    this.pms = b.pms;
    this.channel = b.channel;
    this.subchannel = b.subchannel;
    this.language = b.language;
    this.sourceId = b.sourceId;
    this.ratePlanSets = b.ratePlanSets;
    this.validateSelf();
  }

}
