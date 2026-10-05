package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationProfiles;
import uk.co.whitbread.ohip.domain.model.reservation.out.RatePerNight;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationCreationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationInfo;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPaymentMethodType;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationsDetailsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.RoomStay;
import uk.co.whitbread.ohip.domain.model.reservation.out.SaveReservationResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationProfilesDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.RatePerNightDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationCreationResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationInfoPaymentTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationsDetailsResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationsPaymentTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationsPaymentTypeResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.RoomStayByIdDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.SaveReservationResponseDto;

@Mapper(componentModel = "spring")
public interface ReservationResponseMapper {

  @Mapping(source = "reservations", target = "reservations", qualifiedByName = "toRoomStay")
  ReservationResponseDto toReservationResponseDto(ReservationResponse reservationResponse);

  ReservationsDetailsResponseDto toReservationsDetailsResponseDto(
      ReservationsDetailsResponse reservationsDetailsResponse);

  SaveReservationResponseDto toSaveReservationResponseDto(SaveReservationResponse reservationResponse);

  ReservationsPaymentTypeResponseDto toReservationsPaymentTypeDto(
      ReservationsDetailsResponse reservationsDetailsResponse);

  default ReservationsPaymentTypeResponseDto toReservationsPaymentTypeResponseDto(
      ReservationsDetailsResponse reservationsDetailsResponse) {
    var reservationsPaymentResponseDto = toReservationsPaymentTypeDto(reservationsDetailsResponse);
    var filtered = reservationsPaymentResponseDto.getReservations().getReservationInfo().stream()
        .collect(Collectors.toSet()).stream().toList();
    ReservationsPaymentTypeDto dto = new ReservationsPaymentTypeDto(filtered);
    return new ReservationsPaymentTypeResponseDto(dto);
  }

  @Mapping(expression = "java(toPaymentMethodDto(source.getReservationPaymentMethod()))",
      target = "paymentMethod")
  ReservationInfoPaymentTypeDto toReservationInfoDto(ReservationInfo source);

  default String toPaymentMethodDto(ReservationPaymentMethodType source) {
    return source != null ? source.getPaymentMethod() : null;
  }

  @Named("toRoomStay")
  default ReservationCreationResponseDto toReservationListDto(ReservationCreationResponse reservationCreationResponse) {
    ReservationCreationResponseDto reservationCreationResponseDto = ReservationCreationResponseDto.builder()
        .reservationId(reservationCreationResponse.getReservationId())
        .createDateTime(reservationCreationResponse.getCreateDateTime())
        .roomStay(Objects.isNull(reservationCreationResponse.getRoomStay()) ? null :
            toDto(reservationCreationResponse.getRoomStay()))
        .build();
    if (Objects.nonNull(reservationCreationResponseDto.getRoomStay())) {
      reservationCreationResponseDto.getRoomStay()
          .setAdults(reservationCreationResponse.getRoomStay().getAdultCount());
      reservationCreationResponseDto.getRoomStay()
          .setChildren(reservationCreationResponse.getRoomStay().getChildCount());
    }

    return reservationCreationResponseDto;
  }

  default RoomStayByIdDto toDto(RoomStay roomStay) {
    return RoomStayByIdDto.builder()
        .adults(roomStay.getAdultCount())
        .children(roomStay.getChildCount())
        .cot(roomStay.getCot())
        .arrivalDate(roomStay.getArrivalDate().toString())
        .departureDate(roomStay.getDepartureDate().toString())
        .ratePlanCode(roomStay.getRatePlanCode())
        .roomType(roomStay.getRoomType())
        .roomPrice(roomStay.getRoomPrice())
        .ratesPerNight(toDto(roomStay.getRatesPerNight()))
        .build();
  }

  default List<RatePerNightDto> toDto(List<RatePerNight> ratePerNight) {
    return ratePerNight.stream().map(rate ->
        RatePerNightDto.builder().startDate(rate.getStartDate())
            .pricePerNight(rate.getPricePerNight())
            .cityTaxPerNight(rate.getCityTaxPerNight()).build()).toList();
  }

  ReservationProfilesDto toCreateProfileDto(ReservationProfiles resProfiles);
}
