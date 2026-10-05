package uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.basket.in.ReservationGuestRequest;
import uk.co.whitbread.basket.domain.model.basket.in.ReservationPackagesRequest;
import uk.co.whitbread.basket.domain.model.basket.out.ConfirmReservationRequest;
import uk.co.whitbread.basket.domain.model.basket.out.ConfirmReservationResponse;
import uk.co.whitbread.basket.domain.model.basket.out.ReservationProfiles;
import uk.co.whitbread.basket.generated.models.reservation.ConfirmReservationRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.ConfirmReservationResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationGuestRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationPackagesRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.ReservationProfilesDto;

@Mapper(componentModel = "spring")
public interface UpdateReservationRequestMapper {

  ConfirmReservationResponse toModel(ConfirmReservationResponseDto reservationResponse);

  ReservationProfiles toModel(ReservationProfilesDto reservationProfilesDto);

  ReservationGuestRequestDto toDto(ReservationGuestRequest reservationGuestRequest);

  ReservationPackagesRequestDto toDto(ReservationPackagesRequest reservationPackagesRequest);

  ConfirmReservationRequestDto toDto(ConfirmReservationRequest confirmReservationRequest);


}
