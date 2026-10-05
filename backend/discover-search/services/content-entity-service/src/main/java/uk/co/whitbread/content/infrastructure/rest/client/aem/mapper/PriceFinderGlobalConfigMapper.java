package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.globalconfig.in.GlobalConfigRequest;
import uk.co.whitbread.content.domain.model.pricefinder.in.PriceFinderGlobalConfigRequest;
import uk.co.whitbread.content.domain.model.pricefinder.out.PriceFinderConfig;
import uk.co.whitbread.content.domain.model.pricefinder.out.PriceFinderGlobalConfig;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.in.GlobalConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.in.PriceFinderGlobalConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.out.PriceFinderConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.out.PriceFinderGlobalConfigDto;

@Mapper(componentModel = "spring")
public interface PriceFinderGlobalConfigMapper {

  PriceFinderGlobalConfig toDomainModel(PriceFinderGlobalConfigDto priceFinderGlobalConfigDto);

  PriceFinderConfig toDomainModel(PriceFinderConfigDto priceFinderConfigDto);

  PriceFinderGlobalConfigRequestDto toDto(PriceFinderGlobalConfigRequest priceFinderGlobalConfigRequest);

  GlobalConfigRequestDto toDto(GlobalConfigRequest globalConfigRequest);

}
