package uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.pricefinder.in.PriceFinderGlobalConfigRequest;
import uk.co.whitbread.content.domain.model.pricefinder.out.PriceFinderGlobalConfig;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.in.PriceFinderGlobalConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.out.PriceFinderGlobalConfigDto;

@Mapper(componentModel = "spring", uses = PriceFinderConfigQueryParamsMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PriceFinderGlobalConfigDtoMapper {

  @Mapping(target = "brand", source = "brand", qualifiedByName = "toBrandModel")
  @Mapping(target = "country", source = "country", qualifiedByName = "toCountryModel")
  @Mapping(target = "language", source = "language", qualifiedByName = "toLanguageModel")
  @Mapping(target = "path", source = "path", qualifiedByName = "toPathModel")
  PriceFinderGlobalConfigRequest toDomainModel(PriceFinderGlobalConfigRequestDto priceFinderGlobalConfigRequestDto);

  PriceFinderGlobalConfigDto toDtoModel(PriceFinderGlobalConfig priceFinderConfig);

}
