package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import java.util.Collections;
import java.util.Optional;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.co.whitbread.ohip.domain.model.checkin.out.CheckInPrimaryDetails;
import uk.co.whitbread.ohip.domain.model.checkin.out.PackageHeaderType;
import uk.co.whitbread.ohip.domain.model.checkin.out.PostingAttributes;
import uk.co.whitbread.ohip.domain.model.checkin.out.TransactionDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationRequestDto;

@Mapper(componentModel = "spring", uses = {BookerAddressMapper.class, ConsumptionDetailsMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ReservationRequestMapper {

  String PACKAGE_CODE_HSATWN = "HSATWN";
  String TWIN_ROOM_PACAKAGE_DESCRIPTION = "Twin room reservation package";

  @BeanMapping(qualifiedByName = "updatePackageHeaderForTwinRooms")
  ReservationRequest toReservationRequestModel(ReservationRequestDto reservationRequestDto);

  @Named("updatePackageHeaderForTwinRooms")
  @AfterMapping()
  default void toUpdatedReservationRequestModel(
      @MappingTarget ReservationRequest reservationRequest) {
    Optional.ofNullable(reservationRequest.getReservations()).orElse(Collections.emptyList())
        .stream()
        .forEach(reservation -> {
          Optional.ofNullable(reservation.getReservationPackages()).orElse(Collections.emptyList())
              .stream()
              .filter(reservationPackages ->
                reservationPackages.getPackageCode().equalsIgnoreCase(PACKAGE_CODE_HSATWN)
              )
              .forEach(reservationPackage -> {
                CheckInPrimaryDetails checkInPrimaryDetails = CheckInPrimaryDetails.builder()
                    .description(TWIN_ROOM_PACAKAGE_DESCRIPTION)
                    .build();
                TransactionDetails transactionDetails = TransactionDetails.builder()
                    .allowance(false)
                    .build();
                PostingAttributes postingAttributes = PostingAttributes.builder()
                    .addToRate(false)
                    .printSeparateLine(false)
                    .postNextDay(false)
                    .forecastNextDay(true)
                    .build();
                PackageHeaderType packageHeaderType
                    = PackageHeaderType.builder()
                    .primaryDetails(checkInPrimaryDetails)
                    .postingAttributes(postingAttributes)
                    .transactionDetails(transactionDetails)
                        .build();
                reservationPackage.setPackageHeaderType(packageHeaderType);
              });
        });
  }
}