package uk.co.whitbread.basket.infrastructure.rest.client.promotion.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.promotion.out.RedeemPromoCodeResponse;
import uk.co.whitbread.basket.generated.models.promotion.RedeemPromoCodeResponseDto;

@Mapper(componentModel = "spring")
public interface RedeemPromoCodeResponseMapper {

  RedeemPromoCodeResponse toModel(RedeemPromoCodeResponseDto redeemPromoCodeResponseDto);
}
