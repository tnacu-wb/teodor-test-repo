package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.ohip.domain.model.reservation.in.PackagesSelection;
import uk.co.whitbread.ohip.domain.model.reservation.in.PackagesSelectionScheduled;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPackagesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationType;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationAlertsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationById;
import uk.co.whitbread.ohip.domain.model.reservation.out.RoomStay;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.PackagesScheduledSelectionDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationPackagesRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationScheduledPackagesRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateReservationAlertsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateReservationRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateReservationsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationByBasketRefResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationByIdDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.RoomStayByIdDto;

@Mapper(componentModel = "spring")
public interface UpdateReservationRequestMapper {

  ReservationPackagesRequest toModel(ReservationPackagesRequestDto reservationPackagesRequestDto);

  ReservationPackagesRequest toModel(
      ReservationScheduledPackagesRequestDto updatePackagesScheduled);

  UpdateReservationsRequest toUpdateReservationsRequestModel(
      UpdateReservationsRequestDto updateReservationsRequestDto);

  @Mapping(target = "reservationType", source = "reservationId", qualifiedByName = "reservationsTypeMapping")
  UpdateReservationRequest toUpdateReservationRequestModel(UpdateReservationRequestDto updateReservationRequestDto);

  ReservationByBasketRefResponse toResByBasketRefResponseModel(ReservationByBasketRefResponseDto tempReservations);

  List<UpdateReservationRequest> toUpdateReservationRequestsModel(
      List<UpdateReservationRequestDto> updateReservationsRequestDto);

  List<ReservationById> toResByIdModel(List<ReservationByIdDto> reservationById);

  @Mapping(target = "adultCount", source = "adults")
  @Mapping(target = "childCount", source = "children")
  RoomStay toRoomStayModel(RoomStayByIdDto roomStay);

  @Named("reservationsTypeMapping")
  default ReservationType toReservationsTypeMappingModel(String reservationIdDto) {
    return new ReservationType("Reservation", reservationIdDto);
  }

  @Named("toBasePackageSelectionModel")
  default PackagesSelection toBasePackageSelectionModel(PackagesScheduledSelectionDto dto) {
    if (dto == null) {
      return null;
    }
    if (dto.getScheduledDates() != null && !dto.getScheduledDates().isEmpty()) {
      return toSchedulePackagesSelectionModel(dto);
    } else {
      PackagesSelection baseSelection = new PackagesSelection();
      baseSelection.setId(dto.getId());
      baseSelection.setNoSelections(dto.getNoSelections());
      return baseSelection;
    }
  }

  @Mapping(target = "scheduledDates", source = "scheduledDates")
  PackagesSelectionScheduled toSchedulePackagesSelectionModel(PackagesScheduledSelectionDto dto);

  UpdateReservationAlertsRequest toAlertsModel(
      UpdateReservationAlertsRequestDto updateReservationAlertsRequestDto);
}
