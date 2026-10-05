package uk.co.whitbread.promo.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchResponse;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoKindResponse;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.PromoBatchResponseDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.PromoKindResponseDto;

@Mapper(componentModel = "spring")
public interface PromoBatchDtoMapper {

  PromoBatchResponseDto toDto(PromoBatchResponse promoBatchEntity);

  PromoKindResponseDto toPromoKindDto(PromoKindResponse promoKindResponse);
}
