package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservationDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.out.UpdateReasonForStayResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UpdateReasonForStayResponseOhipMapper {

  @Named("toUpdateReasonForStayResponseModel")
  default UpdateReasonForStayResponse toUpdateReasonForStayResponseModel(
      List<ChangeReservationDetails> changeReservationDetails) {
    List<String> reservationIds = changeReservationDetails.stream()
        .flatMap(reservationDetails ->
            reservationDetails.getReservations().getReservation()
                .get(0).getReservationIdList().stream())
        .filter(uniqueIDType -> uniqueIDType.getType()
            .equalsIgnoreCase(UniqueIdTypeEnumDto.RESERVATION_TYPE.value()))
        .map(UniqueIDType::getId)
        .toList();

    String hotelId = changeReservationDetails.get(0).getReservations()
        .getReservation().get(0).getHotelId();

    return new UpdateReasonForStayResponse(hotelId, reservationIds);
  }

}
