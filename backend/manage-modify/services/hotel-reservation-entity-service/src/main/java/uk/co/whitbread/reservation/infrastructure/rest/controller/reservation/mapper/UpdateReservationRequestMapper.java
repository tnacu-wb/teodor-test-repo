package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesScheduledRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationProfiles;
import uk.co.whitbread.reservation.domain.model.in.UpdateRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationSingleCallRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationPackagesRequestByIdDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationPackagesRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationPackagesScheduledRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationProfilesDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateRateCodeRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateReservationSingleCallRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateRoomTypeRequestDto;

@Mapper(componentModel = "spring")
public interface UpdateReservationRequestMapper {

  ReservationPackagesRequest toModel(ReservationPackagesRequestDto reservationPackagesRequestDto);

  UpdateRequest toModel(UpdateRateCodeRequestDto updateRateCodeRequestDto);

  UpdateReservationPackagesByIdRequest toModel(ReservationPackagesRequestByIdDto reservationPackagesRequestByIdDto);

  UpdateReservationSingleCallRequest toModel(
      UpdateReservationSingleCallRequestDto updateReservationRequestDto);

  ReservationProfiles toModel(ReservationProfilesDto reservationProfilesDto);

  ReservationPackagesScheduledRequest toModel(
      ReservationPackagesScheduledRequestDto reservationPackagesScheduledRequestDto);

  UpdateRequest toModel(UpdateRoomTypeRequestDto updateRoomTypeRequestDto);
}
