package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateBookerEmailRequestDto;
import uk.co.whitbread.reservation.domain.model.in.UpdateBookerEmailRequest;

@Mapper(componentModel = "spring")
public interface UpdateBookerEmailRequestOhipMapper {

  UpdateBookerEmailRequestDto toDto(UpdateBookerEmailRequest updateBookerEmailRequest);
}
