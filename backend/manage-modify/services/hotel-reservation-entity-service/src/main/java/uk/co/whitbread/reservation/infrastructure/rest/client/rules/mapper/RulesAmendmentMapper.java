package uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.out.RulesAmendmentResponse;
import uk.co.whitbread.rules.entity.service.generated.models.agent.AmendmentRuleResponseDto;

@Mapper(componentModel = "spring")
public interface RulesAmendmentMapper {

  @Mapping(target = "isBookingAmendable", source = "isAmendable")
  RulesAmendmentResponse toModel(AmendmentRuleResponseDto rulesAmendmentResponseDto);
}
