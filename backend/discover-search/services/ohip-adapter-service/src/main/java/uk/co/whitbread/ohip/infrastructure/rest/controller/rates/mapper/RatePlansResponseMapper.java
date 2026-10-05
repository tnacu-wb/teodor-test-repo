package uk.co.whitbread.ohip.infrastructure.rest.controller.rates.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlansResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out.RatePlansResponseDto;

@Mapper(componentModel = "spring")
public interface RatePlansResponseMapper {
  RatePlansResponseDto toDto(RatePlansResponse ratePlansResponse);
}
