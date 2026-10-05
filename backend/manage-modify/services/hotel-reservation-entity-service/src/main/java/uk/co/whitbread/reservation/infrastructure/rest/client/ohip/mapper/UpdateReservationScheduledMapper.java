package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackagesScheduledSelectionDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationScheduledPackagesRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomsScheduledSelectionsDto;
import uk.co.whitbread.reservation.domain.model.in.PackagesSelection;
import uk.co.whitbread.reservation.domain.model.in.PackagesSelectionScheduled;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesScheduledRequest;
import uk.co.whitbread.reservation.domain.model.in.RoomReservationPackagesScheduledRequest;


@Mapper(componentModel = "spring", imports = {LocalDate.class, Collections.class})
public interface UpdateReservationScheduledMapper {

  @Mapping(target = "hotelId", source = "source.hotelId")
  @Mapping(target = "arrival", source = "arrival")
  @Mapping(target = "departure", source = "departure")
  @Mapping(target = "reservationsId",
      expression = "java(toExtractReservationIdsDto(source.getReservations()))")
  @Mapping(target = "roomsSelections",
      expression = "java(toRoomSelectionDto(source.getReservations(), true))")
  @Mapping(target = "previousRoomsSelections",
      expression = "java(toRoomSelectionDto(source.getReservations(), false))")
  ReservationScheduledPackagesRequestDto toDto(ReservationPackagesScheduledRequest source,
      String arrival, String departure);

  default List<String> toExtractReservationIdsDto(
      List<RoomReservationPackagesScheduledRequest> reservations) {
    return reservations.stream().map(RoomReservationPackagesScheduledRequest::getReservationsId)
        .toList();
  }

  default List<RoomsScheduledSelectionsDto> toRoomSelectionDto(
      List<RoomReservationPackagesScheduledRequest> reservations, boolean isAddPackages) {
    return reservations.stream().map(reservation -> {
      RoomsScheduledSelectionsDto dto = new RoomsScheduledSelectionsDto();
      if (isAddPackages) {
        dto.setPackagesSelection(toPackageScheduledListDto(reservation.getAddPackages()));
      } else {
        dto.setPackagesSelection(toPackageSelectionListDto(reservation.getRemovePackages()));
      }
      return dto;
    }).toList();
  }

  List<PackagesScheduledSelectionDto> toPackageScheduledListDto(
      List<PackagesSelectionScheduled> packages);

  List<PackagesScheduledSelectionDto> toPackageSelectionListDto(List<PackagesSelection> packages);

  @Mapping(target = "scheduledDates", expression = "java(source.getScheduledDates() != null "
      + "? source.getScheduledDates().stream().map(LocalDate::toString).toList() : null)")
  @Mapping(target = "noSelections", source = "noOfSelections")
  PackagesScheduledSelectionDto toPackageScheduledDto(PackagesSelectionScheduled source);

  PackagesScheduledSelectionDto toPackageSelectionDto(PackagesSelection source);

}
