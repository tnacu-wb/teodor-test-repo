package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class MaxArrivalDateRequestDetails extends ModelValidator<MaxArrivalDateRequestDetails> {

  @NotEmpty
  String channelId;

  public MaxArrivalDateRequestDetails(String channelId) {
    this.channelId = channelId;
    this.validateSelf();
  }
}
