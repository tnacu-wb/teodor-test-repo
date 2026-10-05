package uk.co.whitbread.content.infrastructure.rest.client.countries.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.content.domain.model.countries.out.CountriesInformation;
import uk.co.whitbread.content.infrastructure.rest.client.countries.model.in.CountriesResponseAemDto;

@Mapper(componentModel = "spring", uses = {CountryInfoMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CountriesResponseMapper {

  CountriesInformation toModel(CountriesResponseAemDto countriesResponseAemDto);

}
