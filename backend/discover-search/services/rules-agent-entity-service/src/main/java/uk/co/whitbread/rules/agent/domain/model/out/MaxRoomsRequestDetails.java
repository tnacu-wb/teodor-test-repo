package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class MaxRoomsRequestDetails extends ModelValidator<MaxRoomsRequestDetails> {

  @NotEmpty
  String channelId;

  public MaxRoomsRequestDetails(String channelId) {
    this.channelId = channelId;
    this.validateSelf();
  }
}
