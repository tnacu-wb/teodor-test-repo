package uk.co.whitbread.infrastructure.rest.controller.availability.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.availability.out.RateCodePricingResult;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.RateCodePricingRsDto;

@Mapper(componentModel = "spring")
public interface RateCodeResponseDtoMapper {

  RateCodePricingRsDto toDto(RateCodePricingResult rateCodePricingResult);
}

