package uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.searchresults.data.out.SearchResultsData;
import uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out.SearchResultsDataDto;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface SearchResultsDataDtoMapper {

  SearchResultsDataDto toDtoModel(SearchResultsData searchResultsData);
}
