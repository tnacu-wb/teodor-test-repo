package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationFileAttachmentRequestDto;

@Mapper(componentModel = "spring")
public interface ReservationFileAttachmentRequestMapper {

  ReservationFileAttachmentRequest toModel(
      ReservationFileAttachmentRequestDto reservationFileAttachmentRequestDto);
}
