package uk.co.whitbread.rules.manager.infrastructure.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "paypal_rule", schema = "rules_engine")
public class PaypalRuleEntity extends RuleEntity {

  private String channelId;
  private String country;
  private String hotelIdList;
}
