package uk.co.whitbread.infrastructure.rest.client.content.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.searchrules.out.SearchRules;
import uk.co.whitbread.hotel.content.generated.models.SearchRulesDto;

@Mapper(componentModel = "spring")
public interface SearchRulesResponseMapper {

  SearchRules toModel(SearchRulesDto searchRulesDto);
}
