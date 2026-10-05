package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class MaxRoomOccupancyRule extends Rule {

  @NotEmpty
  String channelId;
  @NotNull
  Integer adults;
  @NotNull
  Integer children;
  @NotNull
  Boolean singleRoom;
  @NotNull
  Boolean doubleRoom;
  @NotNull
  Boolean twinRoom;
  @NotNull
  Boolean accessibleRoom;
  @NotNull
  Boolean familyRoom;
  @NotEmpty
  String brand;

  public MaxRoomOccupancyRule(MaxRoomOccupancyRule.MaxRoomOccupancyRuleBuilder<?, ?> b) {
    super(b);
    this.channelId = b.channelId;
    this.adults = b.adults;
    this.children = b.children;
    this.singleRoom = b.singleRoom;
    this.doubleRoom = b.doubleRoom;
    this.twinRoom = b.twinRoom;
    this.accessibleRoom = b.accessibleRoom;
    this.familyRoom = b.familyRoom;
    this.brand = b.brand;
    this.validateSelf();
  }
}
