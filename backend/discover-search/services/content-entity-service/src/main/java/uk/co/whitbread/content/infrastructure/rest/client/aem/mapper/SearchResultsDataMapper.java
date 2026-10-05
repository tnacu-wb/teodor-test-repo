package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.searchresults.data.out.SearchResultsData;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.searchresults.data.AemSearchResultsDataDto;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface SearchResultsDataMapper {

  SearchResultsData toDomainModel(AemSearchResultsDataDto aemSearchResultsDataDto);
}
