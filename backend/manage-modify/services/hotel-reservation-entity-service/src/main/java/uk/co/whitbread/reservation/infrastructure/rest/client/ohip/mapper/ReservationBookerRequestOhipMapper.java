package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BookerDetailsCnpRequestDto;
import uk.co.whitbread.reservation.domain.model.in.BookerDetailsCnpRequest;

@Mapper(componentModel = "spring")
public interface ReservationBookerRequestOhipMapper {

  BookerDetailsCnpRequestDto toDto(BookerDetailsCnpRequest bookerDetailsCnpRequest);
}
