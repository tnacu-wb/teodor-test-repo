package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.globalconfig.out.SearchRules;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.SearchRulesDto;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface SearchRulesDtoMapper {

  SearchRulesDto toDtoModel(SearchRules searchRules);

}
