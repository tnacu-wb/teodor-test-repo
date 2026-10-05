package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.AccompanyingGuestDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestDetails;

@Mapper(componentModel = "spring")
public interface GuestDetailsMapper {

  StayingGuestDetails toDto(AccompanyingGuestDetails guest);

}
