package uk.co.whitbread.rules.agent.infrastructure.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "occupancy_supplement", schema = "rules_engine")
public class OccupancySupplementEntity extends RuleEntity {

  private String hotelId;
  private BigDecimal pricing;
}
