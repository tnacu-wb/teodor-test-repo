package uk.co.whitbread.rules.manager.infrastructure.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "amendment_rule", schema = "rules_engine")
public class AmendmentRuleEntity extends RuleEntity {

  private String rateType;
  private String countryCode;
  private Integer arrivalDateLimit;
}
