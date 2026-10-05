package uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.entity.service.generated.models.content.SearchRulesDto;
import uk.co.whitbread.reservation.domain.model.searchrules.out.SearchRules;

@Mapper(componentModel = "spring")
public interface SearchRulesResponseMapper {

  SearchRules toModel(SearchRulesDto searchRulesDto);
}
