package uk.co.whitbread.rules.agent.infrastructure.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "max_room_occupancy_rule", schema = "rules_engine")
public class MaxRoomOccupancyRuleEntity extends RuleEntity {

  private String channelId;
  private Integer adults;
  private Integer children;
  private Boolean singleRoom;
  private Boolean doubleRoom;
  private Boolean twinRoom;
  private Boolean accessibleRoom;
  private Boolean familyRoom;
  private String brand;

}
