package uk.co.whitbread.rules.manager.infrastructure.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "base_rate_rule", schema = "rules_engine")
public class BaseRateRuleEntity extends RuleEntity {
  
  private String promoCode;
  private String ratePlanCode;
  private String baseRate;
}
