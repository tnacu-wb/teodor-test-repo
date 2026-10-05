package uk.co.whitbread.basket.infrastructure.rest.client.rules.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceRuleResponse;
import uk.co.whitbread.basket.generated.models.rules.BusinessAllowanceRuleResponseDto;

@Mapper(componentModel = "spring")
public interface BusinessAllowanceRuleResponseMapper {

  BusinessAllowanceRuleResponse toModel(BusinessAllowanceRuleResponseDto businessAllowanceRuleResponseDto);

}
