package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.BookerDetailsCnp;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.BookerDetailsCnpDto;

@Mapper(componentModel = "spring")
public interface BookerDetailsMapper {

  BookerDetailsCnp toModel(BookerDetailsCnpDto dto);

}
