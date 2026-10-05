package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.out.PackagesSelection;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPackagesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.RoomsSelections;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.PackagesSelectionDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.RoomsSelectionsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationsPackagesResponseDto;

@Mapper(componentModel = "spring")
public interface ReservationsPackagesResponseMapper {

  ReservationsPackagesResponseDto toDto(ReservationPackagesResponse reservationPackagesResponse);

  RoomsSelectionsDto toDto(RoomsSelections roomsSelections);

  PackagesSelectionDto toDto(PackagesSelection packagesSelection);
}
