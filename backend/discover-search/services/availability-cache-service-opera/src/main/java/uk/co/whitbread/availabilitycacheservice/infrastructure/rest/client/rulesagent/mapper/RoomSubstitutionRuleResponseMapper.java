package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.rulesagent.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.rules.agent.generated.models.RoomSubstitutionRuleResponseDto;

@Mapper(componentModel = "spring")
public interface RoomSubstitutionRuleResponseMapper {

  RoomSubstitutionRuleResponse toModel(
      RoomSubstitutionRuleResponseDto roomSubstitutionRuleResponseDto);
}
