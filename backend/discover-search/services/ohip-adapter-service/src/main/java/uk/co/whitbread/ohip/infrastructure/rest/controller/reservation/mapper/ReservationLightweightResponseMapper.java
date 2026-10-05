package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.ohip.domain.model.reservation.out.LightweightReservationById;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationLightweightResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.LightweightReservationByIdDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationLightweightResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationPackagesDetailsResponseDto;

@Mapper(componentModel = "spring")
public interface ReservationLightweightResponseMapper {

  @Mapping(source = "reservationResponse", target = "reservationByIdList",
      qualifiedByName = "toDtoFromModel")
  ReservationLightweightResponseDto toDto(
      ReservationLightweightResponse reservationResponse);

  @Named("toDtoFromModel")
  default List<LightweightReservationByIdDto> toDtoFromModel(
      ReservationLightweightResponse reservationResponse) {
    return reservationResponse.getReservationByIdList().stream()
        .map(this::toReservationDto).toList();
  }

  @Named("toReservationDto")
  default LightweightReservationByIdDto toReservationDto(LightweightReservationById reservation) {
    final List<ReservationPackagesDetailsResponseDto> reservationPackagesDetailsResponseDtos = reservation
        .getReservationPackageList().stream().map(
            reservationPackagesDetailsResponse -> new ReservationPackagesDetailsResponseDto(
                reservationPackagesDetailsResponse.getPackageCode(),
                reservationPackagesDetailsResponse.getDescription(),
                reservationPackagesDetailsResponse.getUnitPrice(),
                reservationPackagesDetailsResponse.getTotalQuantity(),
                reservationPackagesDetailsResponse.getPackageGroup(),
                reservationPackagesDetailsResponse.getComputedPrice(),
                reservationPackagesDetailsResponse.getGrossPrice(),
                reservationPackagesDetailsResponse.getVatTax(),
                reservationPackagesDetailsResponse.getStartDate(),
                reservationPackagesDetailsResponse.getEndDate())).toList();

    return LightweightReservationByIdDto.builder()
        .reservationId(reservation.getReservationId())
        .hotelId(reservation.getHotelId())
        .checkInTime(reservation.getCheckInTime())
        .checkOutTime(reservation.getCheckOutTime())
        .email(reservation.getEmail())
        .purposeOfStay(reservation.getPurposeOfStay())
        .reservationPackageList(reservationPackagesDetailsResponseDtos)
        .build();
  }
}
