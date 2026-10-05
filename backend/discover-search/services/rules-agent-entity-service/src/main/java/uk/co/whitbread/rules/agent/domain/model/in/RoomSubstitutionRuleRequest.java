package uk.co.whitbread.rules.agent.domain.model.in;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class RoomSubstitutionRuleRequest extends ModelValidator<RoomSubstitutionRuleRequest> {

  @NotNull
  @Min(1)
  @Max(2)
  Integer adults;

  @NotNull
  @Min(0)
  @Max(3)
  Integer children;
  @NotEmpty
  String roomType;
  @NotEmpty
  String pms;
  @NotEmpty
  String channel;

  public RoomSubstitutionRuleRequest(Integer adults, Integer children, String roomType,
      String pms, String channel) {
    this.adults = adults;
    this.children = children;
    this.roomType = roomType;
    this.pms = pms;
    this.channel = channel;
    this.validateSelf();
  }
}
