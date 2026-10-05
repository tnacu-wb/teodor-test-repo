package uk.co.whitbread.content.infrastructure.rest.controller.countries.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.countries.out.CountriesInformation;
import uk.co.whitbread.content.infrastructure.rest.controller.countries.model.out.CountriesDto;

@Mapper(componentModel = "spring")
public interface CountriesResponseDtoMapper {

  CountriesDto toDto(CountriesInformation countries);
}
