package uk.co.whitbread.promo.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.promo.domain.model.promobatch.in.RedeemPromoCodeRequest;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.in.RedeemPromoCodeRequestDto;

@Mapper(componentModel = "spring")
public interface RedeemPromoCodeRequestDtoMapper {

  RedeemPromoCodeRequest toModel(RedeemPromoCodeRequestDto redeemPromoCodeRequestDto);
}
