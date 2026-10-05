package uk.co.whitbread.rules.agent.infrastructure.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "paypal_rule", schema = "rules_engine")
public class PaypalRuleEntity extends RuleEntity {

  @Column(name = "channelId")
  private String channelId;

  @Column(name = "country")
  private String countryCode;

  @Column(name = "hotelIdList")
  private String hotelId;
}
