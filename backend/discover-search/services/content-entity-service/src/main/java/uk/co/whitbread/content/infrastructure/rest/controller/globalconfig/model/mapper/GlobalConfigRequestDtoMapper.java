package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.globalconfig.in.GlobalConfigRequest;
import uk.co.whitbread.content.domain.model.globalconfig.in.PromoConfigRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.in.GlobalConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.promoconfig.model.in.PromoConfigRequestDto;

@Mapper(componentModel = "spring", uses = GlobalConfigQueryParamsMapper.class,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface GlobalConfigRequestDtoMapper {

  @Mapping(target = "brand", source = "brand", qualifiedByName = "toBrandModel")
  @Mapping(target = "country", source = "country", qualifiedByName = "toCountryModel")
  @Mapping(target = "language", source = "language", qualifiedByName = "toLanguageModel")
  GlobalConfigRequest toDomainModel(GlobalConfigRequestDto globalConfigRequestDto);

  @Mapping(target = "brand", source = "globalConfig.brand", qualifiedByName = "toBrandModel")
  @Mapping(target = "country", source = "globalConfig.country", qualifiedByName = "toCountryModel")
  @Mapping(target = "language", source = "globalConfig.language", qualifiedByName = "toLanguageModel")
  @Mapping(target = "channelId", source = "globalConfig.channelId")
  @Mapping(target = "promotionCode", source = "promoConfig.promotionCode")
  @Mapping(target = "stayStartDate", source = "promoConfig.stayStartDate")
  @Mapping(target = "stayEndDate", source = "promoConfig.stayEndDate")
  @Mapping(target = "rateName", source = "promoConfig.rateName")
  @Mapping(target = "roomClass", source = "promoConfig.roomClass")
  PromoConfigRequest toDomainModel(GlobalConfigRequestDto globalConfig,
      PromoConfigRequestDto promoConfig);
}
