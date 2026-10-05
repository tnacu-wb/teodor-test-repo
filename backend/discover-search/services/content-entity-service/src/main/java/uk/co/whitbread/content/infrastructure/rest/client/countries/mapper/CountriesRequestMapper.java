package uk.co.whitbread.content.infrastructure.rest.client.countries.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.countries.in.CountriesRequest;
import uk.co.whitbread.content.infrastructure.rest.client.countries.model.out.CountriesRequestAemDto;

@Mapper(componentModel = "spring")
public interface CountriesRequestMapper {

  CountriesRequestAemDto toDto(CountriesRequest request);
}
