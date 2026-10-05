package uk.co.whitbread.ohip.infrastructure.rest.controller.rates.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlanInfoResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out.RatePlanInfoResponseDto;

@Mapper(componentModel = "spring")
public interface RatePlanInfoResponseMapper {

  RatePlanInfoResponseDto toDto(RatePlanInfoResponse ratePlanInfoResponse);
}

