package uk.co.whitbread.rules.agent.infrastructure.repository.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "channel_rule", schema = "rules_engine")
public class ChannelRuleEntity extends RuleEntity {

  String pms;
  String channel;
  String subchannel;
  String language;
  String sourceId;
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "channel_rate_plan_sets",
      schema = "rules_engine",
      joinColumns = @JoinColumn(name = "ref_channel_rule_id"))
  @Column(name = "rate_plan_set")
  List<String> ratePlanSets;
}
