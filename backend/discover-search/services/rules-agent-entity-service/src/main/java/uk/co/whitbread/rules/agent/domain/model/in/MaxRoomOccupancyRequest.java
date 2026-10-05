package uk.co.whitbread.rules.agent.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class MaxRoomOccupancyRequest extends ModelValidator<MaxRoomOccupancyRequest> {

  @NotEmpty
  String channelId;
  @NotEmpty
  String brand;

  public MaxRoomOccupancyRequest(String channelId, String brand) {
    this.channelId = channelId;
    this.brand = brand;
    this.validateSelf();
  }
}