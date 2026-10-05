package uk.co.whitbread.rules.agent.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class MaxRoomsRuleRequest extends ModelValidator<MaxRoomsRuleRequest> {

  @NotEmpty
  String channelId;

  public MaxRoomsRuleRequest(String channelId) {
    this.channelId = channelId;
    this.validateSelf();
  }
}
