package uk.co.whitbread.promo.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.promo.domain.model.promobatch.out.RedeemPromoCodeResponse;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.RedeemPromoCodeResponseDto;

@Mapper(componentModel = "spring")
public interface RedeemPromoCodeResponseDtoMapper {

  RedeemPromoCodeResponseDto toModel(RedeemPromoCodeResponse redeemPromoCodeResponse);
}
