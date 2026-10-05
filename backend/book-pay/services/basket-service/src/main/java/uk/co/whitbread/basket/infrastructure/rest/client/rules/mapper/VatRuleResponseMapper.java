package uk.co.whitbread.basket.infrastructure.rest.client.rules.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.rules.out.VatRuleResponse;
import uk.co.whitbread.basket.generated.models.rules.VatRuleResponseDto;

@Mapper(componentModel = "spring")
public interface VatRuleResponseMapper {

  VatRuleResponse toModel(VatRuleResponseDto responseDto);
}
