package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationFileAttachmentRequestDto;
import uk.co.whitbread.reservation.domain.model.in.ReservationFileAttachmentRequest;

@Mapper(componentModel = "spring")
public interface ReservationFileAttachmentRequestOhipMapper {

  ReservationFileAttachmentRequestDto toDto(
      ReservationFileAttachmentRequest reservationFileAttachmentRequest);
}
