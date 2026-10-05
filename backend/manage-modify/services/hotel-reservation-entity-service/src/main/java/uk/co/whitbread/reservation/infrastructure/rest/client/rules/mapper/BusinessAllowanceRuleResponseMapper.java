package uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.BusinessAllowanceRuleResponse;
import uk.co.whitbread.rules.entity.service.generated.models.agent.BusinessAllowanceRuleResponseDto;

@Mapper(componentModel = "spring")
public interface BusinessAllowanceRuleResponseMapper {

  BusinessAllowanceRuleResponse toModel(
      BusinessAllowanceRuleResponseDto businessAllowanceRuleResponseDto);

}
