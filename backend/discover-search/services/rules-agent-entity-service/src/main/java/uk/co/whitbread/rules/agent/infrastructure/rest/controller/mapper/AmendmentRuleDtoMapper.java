package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.rules.agent.domain.model.in.AmendmentRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.AmendmentRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.AmendmentRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.AmendmentRuleResponseDto;

@Mapper(componentModel = "spring")
public interface AmendmentRuleDtoMapper {

  @Mapping(source = "arrivalDate", target = "arrivalDate", dateFormat = "yyyyMMdd")
  @Mapping(source = "hotelLocalDateTime", target = "hotelLocalDateTime", dateFormat = "yyyyMMdd'T'HHmmss")
  AmendmentRuleRequest toModel(AmendmentRuleRequestDto amendmentRuleRequestDto);

  @Mapping(source = "requestDetails.arrivalDate", target = "requestDetails.arrivalDate", dateFormat = "yyyyMMdd")
  @Mapping(source = "requestDetails.hotelLocalDateTime", target = "requestDetails.hotelLocalDateTime",
      dateFormat = "yyyyMMdd'T'HHmmss")
  AmendmentRuleResponseDto toDto(AmendmentRuleResponse amendmentRuleResponse);

}
