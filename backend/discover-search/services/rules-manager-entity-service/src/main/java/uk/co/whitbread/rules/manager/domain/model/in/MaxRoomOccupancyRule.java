package uk.co.whitbread.rules.manager.domain.model.in;

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

}
