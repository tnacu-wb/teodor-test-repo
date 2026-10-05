package uk.co.whitbread.content.infrastructure.rest.client.countries.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.countries.out.CountryInformation;
import uk.co.whitbread.content.infrastructure.rest.client.countries.model.in.CountryInfoAemDto;

@Mapper(componentModel = "spring")
public interface CountryInfoMapper {

  @Mapping(source = "countryCodeIso", target = "countryCode")
  @Mapping(source = "countryCode", target = "countryCodeLegacy")
  @Mapping(source = "countryLegend", target = "countryName")
  @Mapping(source = "flagImg", target = "flagSrc")
  CountryInformation toModel(CountryInfoAemDto countryInfoAemDto);

}
