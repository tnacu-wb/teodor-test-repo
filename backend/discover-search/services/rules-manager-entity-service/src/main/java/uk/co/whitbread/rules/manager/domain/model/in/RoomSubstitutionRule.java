package uk.co.whitbread.rules.manager.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class RoomSubstitutionRule extends Rule {

  @NotEmpty
  String pms;
  @NotNull
  Integer adults;
  @NotNull
  Integer children;
  @NotEmpty
  String roomType;
  @NotEmpty
  String pmsRoomType;
  @NotNull
  Integer offerOrder;
  @NotEmpty
  String specialRequest;
  String accessibleSpecialRequest;
  String pkgCode;
  String channel;
}
