package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class MaxNightsRequestDetails extends ModelValidator<MaxNightsRequestDetails> {

  @NotEmpty
  String channelId;

  public MaxNightsRequestDetails(String channelId) {
    this.channelId = channelId;
    this.validateSelf();
  }
}
