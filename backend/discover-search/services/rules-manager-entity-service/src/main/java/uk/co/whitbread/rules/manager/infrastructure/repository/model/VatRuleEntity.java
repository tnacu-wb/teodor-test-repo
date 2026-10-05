package uk.co.whitbread.rules.manager.infrastructure.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "vat_rule", schema = "rules_engine")
public class VatRuleEntity extends RuleEntity {

  private String vatRegion;
  private String tranCode;
  private String description;
  private String pkgCode;
  private Boolean vatBearing;

}
