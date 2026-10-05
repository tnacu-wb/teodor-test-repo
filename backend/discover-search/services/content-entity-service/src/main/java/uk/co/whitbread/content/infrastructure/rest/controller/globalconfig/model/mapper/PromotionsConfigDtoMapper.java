package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.globalconfig.out.MetaPromoRateMapping;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsConfig;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.MetaPromoRateMappingDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.PromotionsConfigDto;

@Mapper(componentModel = "spring")
public interface PromotionsConfigDtoMapper {

  @Mapping(target = "metaPromoRateMapping", source = "metaPromoRateMapping")
  PromotionsConfigDto toDtoModel(PromotionsConfig promotionsConfig);

  MetaPromoRateMappingDto toDtoModel(MetaPromoRateMapping metaPromoRateMapping);

  List<MetaPromoRateMappingDto> toDtoModel(List<MetaPromoRateMapping> metaPromoRateMapping);

}
