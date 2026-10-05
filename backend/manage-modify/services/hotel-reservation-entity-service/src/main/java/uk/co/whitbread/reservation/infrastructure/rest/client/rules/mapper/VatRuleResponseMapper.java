package uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.VatRuleResponse;
import uk.co.whitbread.rules.entity.service.generated.models.agent.VatRuleResponseDto;

@Mapper(componentModel = "spring")
public interface VatRuleResponseMapper {

  VatRuleResponse toModel(VatRuleResponseDto responseDto);

}
