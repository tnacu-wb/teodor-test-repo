package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.rulesagent.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.rulesagent.model.in.RoomSubstitutionRuleRequestDto;

@Mapper(componentModel = "spring")
public interface RoomSubstitutionRuleRequestMapper {

  RoomSubstitutionRuleRequestDto toDto(RoomSubstitutionRuleRequest roomSubstitutionRuleRequest);
}
