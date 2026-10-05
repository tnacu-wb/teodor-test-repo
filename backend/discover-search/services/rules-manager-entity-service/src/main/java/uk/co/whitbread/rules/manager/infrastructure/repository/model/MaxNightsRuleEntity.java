package uk.co.whitbread.rules.manager.infrastructure.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "max_nights_rule", schema = "rules_engine")
public class MaxNightsRuleEntity extends RuleEntity {

  private String channelId;
  private Integer maxNights;
}
