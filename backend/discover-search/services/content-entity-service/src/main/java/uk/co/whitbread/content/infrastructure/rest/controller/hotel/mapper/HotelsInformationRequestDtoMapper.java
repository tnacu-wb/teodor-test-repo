package uk.co.whitbread.content.infrastructure.rest.controller.hotel.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.hotel.in.HotelInformationRequest;
import uk.co.whitbread.content.domain.model.hotel.in.HotelsInformationRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.HotelInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.HotelsInformationRequestDto;

@Mapper(componentModel = "spring")
public interface HotelsInformationRequestDtoMapper {

  HotelsInformationRequest toDomainModel(HotelsInformationRequestDto hotelsInformationRequestDto);

  HotelInformationRequest toDomainModel(HotelInformationRequestDto hotelInformationRequestDto);
}
