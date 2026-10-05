package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.PriceBreakdownDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.PriceBreakdownOhipDto;

@Mapper(componentModel = "spring")
public interface PriceBreakdownOhipMapper {

  PriceBreakdownOhipDto toDto(PriceBreakdownDto priceBreakdownDto, String roomType, Integer adults, Integer children);
}
