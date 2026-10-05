package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.globalconfig.out.MetaPromoRateMapping;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsInformationResponse;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.MetaPromoRateMappingDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.PromoBoxDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.PromotionsConfigDto;

@Mapper(componentModel = "spring")
public interface PromotionsConfigMapper {

  PromotionsConfig toDomainModel(PromotionsConfigDto promotionsConfigDto);

  @Mapping(target = "metaPromoRateMapping", source = "metaPromoRateMapping")
  List<MetaPromoRateMapping> toDomainModel(List<MetaPromoRateMappingDto> metaPromoRateMappingDto);

  MetaPromoRateMapping toDomainModel(MetaPromoRateMappingDto dto);

  @Mapping(source = "promoBox", target = "promoBox")
  PromotionsInformationResponse toPromotionsInformationResponseModel(
          PromotionsInformationResponse promotionsInformationResponse, PromoBoxDto promoBox);
}