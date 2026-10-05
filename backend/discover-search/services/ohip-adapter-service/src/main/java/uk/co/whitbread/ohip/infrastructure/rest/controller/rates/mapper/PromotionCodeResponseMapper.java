package uk.co.whitbread.ohip.infrastructure.rest.controller.rates.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.rates.out.PromotionCodeResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out.PromotionResponseDto;

@Mapper(componentModel = "spring")
public interface PromotionCodeResponseMapper {

  List<PromotionResponseDto> toDto(List<PromotionCodeResponse> promotionCodeResponse);

}