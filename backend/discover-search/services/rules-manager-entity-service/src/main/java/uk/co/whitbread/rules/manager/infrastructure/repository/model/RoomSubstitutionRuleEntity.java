package uk.co.whitbread.rules.manager.infrastructure.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "room_substitution_rule", schema = "rules_engine")
public class RoomSubstitutionRuleEntity extends RuleEntity {

  private String pms;
  private Integer adults;
  private Integer children;
  private String roomType;
  private String pmsRoomType;
  private Integer offerOrder;
  private String specialRequest;
  private String accessibleSpecialRequest;
  private String pkgCode;
  private String channel;
}
