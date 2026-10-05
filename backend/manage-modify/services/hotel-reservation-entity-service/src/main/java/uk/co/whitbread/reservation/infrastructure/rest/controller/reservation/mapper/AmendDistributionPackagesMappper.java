package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.in.RoomsSelectionsByReservationId;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AmendDistributionRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AmendDistributionReservationDto;

@Mapper(componentModel = "spring", uses = PackagesSelectionMapper.class,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AmendDistributionPackagesMappper {

  @Mapping(target = "roomsSelections", source = "reservations")
  UpdateReservationPackagesByIdRequest toModel(AmendDistributionRequestDto amendDistributionRequestDto);

  List<RoomsSelectionsByReservationId> toModel(List<AmendDistributionReservationDto> reservations);

  @Mapping(target = "packagesSelection", source = "reservationPackageList")
  RoomsSelectionsByReservationId toModel(AmendDistributionReservationDto reservations);
}