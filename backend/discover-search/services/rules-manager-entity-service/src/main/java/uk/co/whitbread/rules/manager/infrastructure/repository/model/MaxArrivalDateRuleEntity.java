package uk.co.whitbread.rules.manager.infrastructure.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "max_arrival_date_rule", schema = "rules_engine")
public class MaxArrivalDateRuleEntity extends RuleEntity {

  private String channelId;
  private Integer maxArrivalDate;
}
