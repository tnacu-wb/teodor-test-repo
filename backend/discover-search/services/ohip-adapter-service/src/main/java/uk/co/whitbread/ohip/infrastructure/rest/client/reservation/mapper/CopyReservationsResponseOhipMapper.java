package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.out.CopyReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CopyReservationsResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;

@Mapper(componentModel = "spring")
public interface CopyReservationsResponseOhipMapper {

  @Named("toCopyReservationsResponseModel")
  default CopyReservationsResponse toCopyReservationsResponseModel(List<Reservation> reservations,
      List<String> originalReservations, List<String> copiedReservations) {
    Map<String, String> linkBetweenReservations = new HashMap<>();
    for (int i = 0; i < originalReservations.size(); i++) {
      linkBetweenReservations.put(originalReservations.get(i),
          copiedReservations.get(i));
    }
    return CopyReservationsResponse.builder()
        .linkBetweenReservations(linkBetweenReservations)
        .reservations(reservations.stream().map(this::toCopyReservationResponseModel).toList())
        .build();
  }

  @Mapping(target = "reservationId", source = "reservation", qualifiedByName = "toReservationIdModel")
  @Mapping(target = "createDateTime", source = "reservation", qualifiedByName = "toCreateDateTimeModel")
  CopyReservationResponse toCopyReservationResponseModel(Reservation reservation);

  @Named("toReservationIdModel")
  default String toReservationIdModel(Reservation reservation) {
    final var reservationIdList = reservation
        .getReservations()
        .getReservation()
        .get(0)
        .getReservationIdList();
    return reservationIdList.stream()
        .filter(uniqueIDType -> uniqueIDType.getType()
            .equalsIgnoreCase(UniqueIdTypeEnumDto.RESERVATION_TYPE.value()))
        .map(UniqueIDType::getId)
        .toList()
        .get(0);
  }

  @Named("toCreateDateTimeModel")
  default String toCreateDateTimeModel(Reservation reservation) {
    return reservation
        .getReservations()
        .getReservation()
        .get(0)
        .getCreateDateTime()
        .toInstant()
        .truncatedTo(ChronoUnit.SECONDS)
        .toString();
  }
}
