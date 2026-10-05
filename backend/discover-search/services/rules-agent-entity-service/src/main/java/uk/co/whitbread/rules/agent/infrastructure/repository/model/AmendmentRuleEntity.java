package uk.co.whitbread.rules.agent.infrastructure.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "amendment_rule", schema = "rules_engine")
public class AmendmentRuleEntity extends RuleEntity {

  private String rateType;
  private Integer arrivalDateLimit;
  private String countryCode;
}
