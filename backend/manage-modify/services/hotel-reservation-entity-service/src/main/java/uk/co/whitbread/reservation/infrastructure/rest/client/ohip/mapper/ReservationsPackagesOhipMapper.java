package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackagesSelectionDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsPackagesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomsSelectionsDto;
import uk.co.whitbread.reservation.domain.model.out.PackagesSelection;
import uk.co.whitbread.reservation.domain.model.out.ReservationsPackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomsSelections;

@Mapper(componentModel = "spring")
public interface ReservationsPackagesOhipMapper {

  ReservationsPackagesResponse toModel(
      ReservationsPackagesResponseDto reservationsPackagesResponseDto);

  RoomsSelections toModel(RoomsSelectionsDto roomsSelectionsDto);

  @Mapping(source = "noSelections", target = "noOfSelections")
  PackagesSelection toModel(PackagesSelectionDto packagesSelectionDto);
}
