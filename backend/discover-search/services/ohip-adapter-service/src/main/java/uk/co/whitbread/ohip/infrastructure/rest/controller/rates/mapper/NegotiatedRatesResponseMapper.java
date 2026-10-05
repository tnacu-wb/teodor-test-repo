package uk.co.whitbread.ohip.infrastructure.rest.controller.rates.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.rates.out.NegotiatedRatesResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out.NegotiatedRatesResponseDto;

@Mapper(componentModel = "spring")
public interface NegotiatedRatesResponseMapper {
  NegotiatedRatesResponseDto toDto(NegotiatedRatesResponse negotiatedRatesResponse);
}
