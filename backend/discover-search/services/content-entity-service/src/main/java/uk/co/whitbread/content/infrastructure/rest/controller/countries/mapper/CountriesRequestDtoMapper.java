package uk.co.whitbread.content.infrastructure.rest.controller.countries.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.countries.in.CountriesRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.countries.model.in.CountriesRequestDto;

@Mapper(componentModel = "spring")
public interface CountriesRequestDtoMapper {

  CountriesRequest toDomainModel(CountriesRequestDto countriesRequestDto);
}
