package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.globalconfig.out.GlobalConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.MetaPromoRateMapping;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsInformationResponse;
import uk.co.whitbread.content.domain.model.globalconfig.out.UpsellItemsExtras;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.MetaPromoRateMappingDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.PromotionsConfigResponseDto;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.GlobalConfigDto;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.UpsellItemsExtrasDto;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.mapper.PriceFinderGlobalConfigDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.promoconfig.model.out.PromotionsInformationResponseDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {RoomClassConfigDtoMapper.class, SearchRulesDtoMapper.class,
        RoomUpgradeOptionsDtoMapper.class, PromotionsConfigDtoMapper.class, PriceFinderGlobalConfigDtoMapper.class})
public interface GlobalConfigDtoMapper {

  GlobalConfigDto toDtoModel(GlobalConfig globalConfig);

  PromotionsInformationResponseDto toDtoModel(PromotionsInformationResponse promotionsInformationResponse);

  @Mapping(target = "metaPromoRateMappingDtos", source = "metaPromoRateMapping")
  PromotionsConfigResponseDto  toPromotionsConfigResponseDto(
          PromotionsConfig promotionsConfig);

  MetaPromoRateMappingDto toDto(MetaPromoRateMapping model);

  UpsellItemsExtrasDto toDto(UpsellItemsExtras upsellItemsExtras);
}
