package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.BookingCardInformationRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.BookingCardInformationRequestDto;

@Mapper(componentModel = "spring")
public interface BookingCardInformationRequestMapper {

  BookingCardInformationRequest toModel(
      BookingCardInformationRequestDto bookingCardInformationRequestDto);

}
