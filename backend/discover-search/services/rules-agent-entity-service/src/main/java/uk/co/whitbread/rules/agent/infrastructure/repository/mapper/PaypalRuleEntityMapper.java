package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.PaypalRule;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.PaypalRuleEntity;

@Mapper(componentModel = "spring")
public interface PaypalRuleEntityMapper {

  PaypalRule toModel(PaypalRuleEntity paypalRuleEntity);
}
