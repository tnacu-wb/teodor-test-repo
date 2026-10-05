package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;


import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationRequestDto;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuestRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationProfiles;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationSingleCallRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationProfilesDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.UpdateReservationSingleCallResponseDto;

@Mapper(componentModel = "spring", uses = {ReservationOhipMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationRequestOhipMapper {

  ReservationRequestDto toDto(ReservationRequest reservationRequest);

  UpdateReservationSingleCallResponseDto toUpdateReservationDto(
      UpdateReservationSingleCallRequest updateDistrReservationRequest);

  ReservationProfiles toCreateProfileModel(ReservationProfilesDto reservationProfilesDto);
}
