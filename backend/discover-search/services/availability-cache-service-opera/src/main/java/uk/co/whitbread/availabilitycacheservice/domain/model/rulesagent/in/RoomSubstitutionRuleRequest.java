package uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.availabilitycacheservice.domain.model.validation.SelfValidation;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class RoomSubstitutionRuleRequest implements SelfValidation<RoomSubstitutionRuleRequest> {

  @NotNull
  private Integer adults;
  @NotNull
  private Integer children;
  @NotEmpty
  private String roomType;
  @NotEmpty
  private String pms;
  @NotEmpty
  private String channel;

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
