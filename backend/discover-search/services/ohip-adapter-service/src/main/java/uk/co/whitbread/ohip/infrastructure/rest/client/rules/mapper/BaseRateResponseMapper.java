package uk.co.whitbread.ohip.infrastructure.rest.client.rules.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.ohip.domain.model.rules.model.out.BaseRateRuleResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.BaseRateRuleResponseDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BaseRateResponseMapper {
  
  BaseRateRuleResponse toModel(BaseRateRuleResponseDto baseRateRuleResponseDto);
}
