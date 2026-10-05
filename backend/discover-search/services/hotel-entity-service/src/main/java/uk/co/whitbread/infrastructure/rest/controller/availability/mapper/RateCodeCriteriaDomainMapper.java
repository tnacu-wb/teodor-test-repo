package uk.co.whitbread.infrastructure.rest.controller.availability.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.RateCodePricingRequestDto;

@Mapper(componentModel = "spring")
public interface RateCodeCriteriaDomainMapper {

  RateCodeCriteria toDomainModel(String hotelId, RateCodePricingRequestDto rateCodePricingRqDto);

}
