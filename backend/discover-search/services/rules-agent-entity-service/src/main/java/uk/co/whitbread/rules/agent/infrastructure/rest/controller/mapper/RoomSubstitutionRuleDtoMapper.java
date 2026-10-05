package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.RoomSubstitutionRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.RoomSubstitutionRuleResponseDto;

@Mapper(componentModel = "spring")
public interface RoomSubstitutionRuleDtoMapper {

  RoomSubstitutionRuleRequest toModel(
      RoomSubstitutionRuleRequestDto roomSubstitutionRuleRequestDto);

  RoomSubstitutionRuleResponseDto toDto(RoomSubstitutionRuleResponse roomSubstitutionRuleResponse);

}
