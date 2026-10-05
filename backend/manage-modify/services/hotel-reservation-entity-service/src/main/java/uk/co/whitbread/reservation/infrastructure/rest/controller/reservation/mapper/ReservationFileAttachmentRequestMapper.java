package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationFileAttachmentRequestDto;

@Mapper(componentModel = "spring")
public interface ReservationFileAttachmentRequestMapper {

  ReservationFileAttachmentRequest toModel(
      ReservationFileAttachmentRequestDto reservationFileAttachmentRequestDto);
}