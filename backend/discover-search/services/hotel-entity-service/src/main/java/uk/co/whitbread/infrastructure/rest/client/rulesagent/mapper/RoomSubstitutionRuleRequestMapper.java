package uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.model.in.RoomSubstitutionRuleRequestDto;

@Mapper(componentModel = "spring")
public interface RoomSubstitutionRuleRequestMapper {

  RoomSubstitutionRuleRequestDto toDto(RoomSubstitutionRuleRequest roomSubstitutionRuleRequest);
}
