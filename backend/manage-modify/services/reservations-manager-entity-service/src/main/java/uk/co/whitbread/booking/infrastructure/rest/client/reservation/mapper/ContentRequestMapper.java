package uk.co.whitbread.booking.infrastructure.rest.client.reservation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.booking.domain.model.information.in.BookingInfoRequest;
import uk.co.whitbread.booking.domain.model.information.in.HotelInformationRequest;
import uk.co.whitbread.booking.domain.model.information.out.HotelInformationResponse;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.in.HotelInformationRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.in.MealsRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.HotelInformationResponseDto;

@Mapper(componentModel = "spring")
public interface ContentRequestMapper {
  @Mapping(target = "hotelId", source = "request.hotelId")
  MealsRequestDto toModel(BookingInfoRequest request);

  HotelInformationResponse toModel(HotelInformationResponseDto request);

  HotelInformationRequestDto toDto(HotelInformationRequest request);
}
