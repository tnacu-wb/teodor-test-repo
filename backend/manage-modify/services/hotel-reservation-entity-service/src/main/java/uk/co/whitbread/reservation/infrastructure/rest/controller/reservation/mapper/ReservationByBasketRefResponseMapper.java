package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.co.whitbread.reservation.domain.model.out.CharacterUDFs;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationPreferencesResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomStayByIdResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationByBasketRefResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationByIdDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationPackagesDetailsResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationPreferencesDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.RoomStayByIdDto;

@Mapper(componentModel = "spring",
    uses = {ReservationGuestsMapper.class, BillingMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ReservationByBasketRefResponseMapper {

  String PACKAGE_CODE_HSATWN = "HSATWN";
  String UDFC_07 = "UDFC07";

  @BeanMapping(qualifiedByName = "toUpdateRoomStayByIdResponse")
  @Mapping(target = "idContext", expression = "java(toIdContextDto(refResponse))")
  ReservationByBasketRefResponseDto toDto(ReservationByBasketRefResponse refResponse);

  @Mapping(target = "wifiCode", expression = "java(toConvertWifiCodeDto(idResponse))")
  ReservationByIdDto toDto(ReservationByIdResponse idResponse);

  ReservationPreferencesDto toDto(ReservationPreferencesResponse preferences);

  RoomStayByIdDto toDto(RoomStayByIdResponse roomStayByIdResponse);

  @Named("toUpdateRoomStayByIdResponse")
  @AfterMapping()
  default void toUpdatedRoomStayByIdDto(
      @MappingTarget ReservationByBasketRefResponseDto reservationByBasketRefResponseDto) {
    List<ReservationByIdDto> reservationByIdList =
        reservationByBasketRefResponseDto.getReservationByIdList();
    if (reservationByIdList.get(0) == null) {
      return;
    }
    reservationByIdList.forEach(reservationByIdDto -> {
      List<ReservationPackagesDetailsResponseDto> reservationPackagesList =
          reservationByIdDto.getReservationPackageList();
      if (!reservationPackagesList.stream().anyMatch(packageItem ->
          packageItem.getPackageCode().equalsIgnoreCase(PACKAGE_CODE_HSATWN))) {
        return;
      }

      int numberOfNight = reservationByIdDto.getRoomStay().getRatesPerNight().size();
      final BigDecimal[] packageUnitPrice = new BigDecimal[1];
      final int[] packageIndex = {-1};
      reservationPackagesList.forEach(p -> {
        if (p.getPackageCode().equalsIgnoreCase(PACKAGE_CODE_HSATWN)) {
          packageUnitPrice[0] = p.getUnitPrice();
          packageIndex[0] = reservationPackagesList.indexOf(p);
        }
      });
      reservationPackagesList.remove(packageIndex[0]);

      BigDecimal totalPackagesPrice = packageUnitPrice[0].multiply(
          BigDecimal.valueOf(numberOfNight));
      reservationByIdDto.getRoomStay().setRoomPrice(
          reservationByIdDto.getRoomStay().getRoomPrice().add(totalPackagesPrice));

      reservationByIdDto.getRoomStay().getRatesPerNight().forEach(rpn ->
          rpn.setPricePerNight(rpn.getPricePerNight().add(packageUnitPrice[0])));

      reservationByIdDto.setReservationPackageList(reservationPackagesList);
    });
  }

  default String toConvertWifiCodeDto(ReservationByIdResponse idResponse) {

    if (idResponse == null || idResponse.getUserDefinedFields() == null
        || idResponse.getUserDefinedFields().getCharacterUDFs() == null) {
      return null;
    }

    return idResponse.getUserDefinedFields().getCharacterUDFs().stream()
        .filter(field -> UDFC_07.equals(field.getName()))
        .filter(Objects::nonNull)
        .map(CharacterUDFs::getValue)
        .findFirst()
        .orElse(null);
  }

  default String toIdContextDto(ReservationByBasketRefResponse refResponse) {

    if (refResponse == null) {
      return null;
    }

    return refResponse.getIdContext() == null ? "WB_DIGITAL" : refResponse.getIdContext();
  }
}