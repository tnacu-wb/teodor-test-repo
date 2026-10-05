package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnpRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.BookerDetailsCnpRequestDto;

@Mapper(componentModel = "spring")
public interface BookerDetailsCnpRequestMapper {

  BookerDetailsCnpRequest toModel(BookerDetailsCnpRequestDto bookerDetailsCnpRequestDto);
}
