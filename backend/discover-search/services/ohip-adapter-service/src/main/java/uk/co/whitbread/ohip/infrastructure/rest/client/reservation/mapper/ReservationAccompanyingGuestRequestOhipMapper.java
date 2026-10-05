package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AlertAreaType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AlertType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestTypeProfileInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public abstract class ReservationAccompanyingGuestRequestOhipMapper {

  @Mapping(expression =
      "java(injectReservationAccompanyingGuestDetails(reservationId, profileIds))",
      target = "reservations")
  public abstract ChangeReservation toDto(String reservationId, Map<String, Boolean> profileIds);

  protected List<HotelReservationInstructionType> injectReservationAccompanyingGuestDetails(
      String reservationId, Map<String, Boolean> profileIds) {

    if (StringUtils.isEmpty(reservationId) || ObjectUtils.isEmpty(profileIds)) {
      return Collections.emptyList();
    }

    List<ResGuestType> resGuestTypes = new ArrayList<>();
    profileIds.entrySet().stream()
        .sorted((entry1, entry2) -> Boolean.compare(entry2.getValue(), entry1.getValue()))
        .forEach(entry -> {
          final String profileId = entry.getKey();
          final Boolean primary = entry.getValue();

          if (StringUtils.isEmpty(profileId)) {
            throw new IllegalArgumentException("Profile ID cannot be null or empty");
          }
          final var resGuestType = new ResGuestType();
          ResGuestTypeProfileInfo resGuestTypeProfileInfo = new ResGuestTypeProfileInfo();
          UniqueIDType profileIdType = new UniqueIDType();
          profileIdType.setId(profileId);
          profileIdType.setType("Profile");

          resGuestTypeProfileInfo.setProfileIdList(List.of(profileIdType));
          resGuestType.setProfileInfo(resGuestTypeProfileInfo);
          resGuestType.setPrimary(primary);

          resGuestTypes.add(resGuestType);
        });

    final var reservationUniqueIdType = new UniqueIDType();
    reservationUniqueIdType.setType(UniqueIdTypeEnumDto.RESERVATION_TYPE.value());
    reservationUniqueIdType.setId(reservationId);

    final var hotelReservation = new HotelReservationInstructionType();
    hotelReservation.setReservationGuests(resGuestTypes);
    hotelReservation.setReservationIdList(List.of(reservationUniqueIdType));

    return List.of(hotelReservation);
  }

  @Mapping(expression =
      "java(injectReservationAlertDetails(hotelId, reservationId, description, alertId))",
      target = "reservations")
  public abstract ChangeReservation toAddOrDeleteAlertModel(String hotelId, String reservationId,
      String description, String alertId);

  protected List<HotelReservationInstructionType> injectReservationAlertDetails(String hotelId,
      String reservationId, String description, String alertId) {

    if (StringUtils.isEmpty(hotelId) || StringUtils.isEmpty(reservationId)) {
      throw new IllegalArgumentException("Invalid input parameters");
    }

    final var reservationUniqueIdType = new UniqueIDType();
    reservationUniqueIdType.setType(UniqueIdTypeEnumDto.RESERVATION_TYPE.value());
    reservationUniqueIdType.setId(reservationId);

    AlertType alertType = new AlertType();
    if (StringUtils.isEmpty(alertId)) {
      alertType.setArea(AlertAreaType.CHECKIN);
      alertType.setCode("RESERVATION");
      alertType.setDescription(description);
      alertType.setScreenNotification(true);
      alertType.setPrinterNotification(false);
    } else {
      alertType.setId(alertId);
      alertType.setIdContext("OPERA");
      alertType.setType("Alert");
    }
    final var hotelReservation = new HotelReservationInstructionType();
    hotelReservation.setReservationIdList(List.of(reservationUniqueIdType));
    hotelReservation.setAlerts(List.of(alertType));
    hotelReservation.setHotelId(hotelId);

    return List.of(hotelReservation);
  }

}