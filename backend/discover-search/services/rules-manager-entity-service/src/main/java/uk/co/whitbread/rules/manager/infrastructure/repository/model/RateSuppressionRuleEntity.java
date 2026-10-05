package uk.co.whitbread.rules.manager.infrastructure.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "suppression_rule", schema = "rules_engine")
public class RateSuppressionRuleEntity extends RuleEntity {

  private String rateType;
  private Short priority;

}
