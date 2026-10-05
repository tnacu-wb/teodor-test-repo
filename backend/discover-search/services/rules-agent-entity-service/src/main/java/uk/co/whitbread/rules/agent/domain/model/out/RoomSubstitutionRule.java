package uk.co.whitbread.rules.agent.domain.model.out;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class RoomSubstitutionRule extends Rule {

  String pms;
  Integer adults;
  Integer children;
  String roomType;
  String pmsRoomType;
  Integer offerOrder;
  String specialRequest;
  String accessibleSpecialRequest;
  String codePackage;
  String channel;
}