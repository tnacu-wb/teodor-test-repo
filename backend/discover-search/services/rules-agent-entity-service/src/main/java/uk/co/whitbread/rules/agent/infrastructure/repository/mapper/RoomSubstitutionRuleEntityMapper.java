package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitutionRule;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RoomSubstitutionRuleEntity;

@Mapper(componentModel = "spring")
public interface RoomSubstitutionRuleEntityMapper {

  @Mapping(source = "pkgCode", target = "codePackage")
  RoomSubstitutionRule toModel(RoomSubstitutionRuleEntity roomSubstitutionRuleEntity);
}
