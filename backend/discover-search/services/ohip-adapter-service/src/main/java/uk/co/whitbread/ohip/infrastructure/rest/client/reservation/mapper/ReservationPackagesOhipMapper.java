package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackageCodeHeaderType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackageConsumptionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResExpectedTimesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageScheduleType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.PackageCodeType;
import uk.co.whitbread.ohip.domain.model.reservation.in.PackagesSelection;
import uk.co.whitbread.ohip.domain.model.reservation.in.PackagesSelectionScheduled;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPackagesRequest;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.PackagesResponseOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.DateUtils;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
@Slf4j
public abstract class ReservationPackagesOhipMapper {

  public static final String EXPECTED_TIMES_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
  public static final String DATE_FORMAT = "yyyy-MM-dd";
  public static final String BOOKING_FEE_PACKAGE_ZN = "ZN0000";
  public static final String BOOKING_FEE_PACKAGE_ZR = "ZR0000";

  @Autowired
  public void toReservationOhipMapperForModel() {
  }

  @Mapping(expression = "java(injectReservationPackagesDetails"
      + "(reservationPackagesRequest, roomIndex, packagesResponseOhipDto))",
      target = "reservations")
  public abstract ChangeReservation toModel(ReservationPackagesRequest reservationPackagesRequest,
      int roomIndex, PackagesResponseOhipDto packagesResponseOhipDto);

  @Mapping(expression = "java(injectReservationPackagesDetailsRemove"
      + "(reservationPackagesRequest, roomIndex))",
      target = "reservations")
  public abstract ChangeReservation toRemovalModel(
      ReservationPackagesRequest reservationPackagesRequest, int roomIndex);

  protected List<HotelReservationInstructionType> injectReservationPackagesDetails(
      ReservationPackagesRequest reservationPackagesRequest,
      int roomIndex, PackagesResponseOhipDto packagesResponseOhipDto) {

    List<ReservationPackageType> reservationPackageTypes = new ArrayList<>();
    final var hotelReservation = new HotelReservationInstructionType();

    final var reservationUniqueIdType = createUniqueIdType(reservationPackagesRequest
        .getReservationsId().get(roomIndex));

    for (PackagesSelection packagesSelection : reservationPackagesRequest
        .getRoomsSelections().get(roomIndex).getPackagesSelection()) {
      reservationPackageTypes.add(createPackageTypeForUpdate(packagesResponseOhipDto,
          reservationPackagesRequest, packagesSelection));
    }

    hotelReservation.setReservationIdList(Collections.singletonList(reservationUniqueIdType));
    hotelReservation.setHotelId(reservationPackagesRequest.getHotelId());
    hotelReservation.setReservationPackages(reservationPackageTypes);
    hotelReservation.setRoomStay(createRoomStayType(reservationPackagesRequest, roomIndex));

    return List.of(hotelReservation);
  }

  private RoomStayType createRoomStayType(ReservationPackagesRequest reservationPackagesRequest,
      int roomIndex) {
    RoomStayType roomStay = new RoomStayType();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    LocalDate startDate = LocalDate.parse(reservationPackagesRequest.getArrival(), formatter);
    LocalDate endDate = LocalDate.parse(reservationPackagesRequest.getDeparture(), formatter);

    ResExpectedTimesType expectedTimes = new ResExpectedTimesType();
    for (PackagesSelection packagesSelection : reservationPackagesRequest
        .getRoomsSelections().get(roomIndex).getPackagesSelection()) {

      if (isEarlyCheckInPackage(packagesSelection.getId())) {
        String arrivalTime = String.format("%s 11:00:00.0", startDate);
        expectedTimes.setReservationExpectedArrivalTime(
            DateUtils.getDateFromString(arrivalTime, EXPECTED_TIMES_DATE_FORMAT));
      } else if (isLateCheckOutPackage(packagesSelection.getId())) {
        String departureDate = String.format("%s 14:00:00.0", endDate);
        expectedTimes.setReservationExpectedDepartureTime(
            DateUtils.getDateFromString(departureDate, EXPECTED_TIMES_DATE_FORMAT));
      }
    }

    roomStay.setArrivalDate(startDate);
    roomStay.setDepartureDate(endDate);
    roomStay.setExpectedTimes(expectedTimes);

    return roomStay;
  }

  protected List<HotelReservationInstructionType> injectReservationPackagesDetailsRemove(
      ReservationPackagesRequest reservationPackagesRequest, int roomIndex) {

    List<ReservationPackageType> reservationPackageTypes = new ArrayList<>();
    final var hotelReservation = new HotelReservationInstructionType();

    final var reservationUniqueIdType = createUniqueIdType(
        reservationPackagesRequest.getReservationsId().get(roomIndex));

    for (PackagesSelection packagesSelection : reservationPackagesRequest
        .getPreviousRoomsSelections().get(roomIndex).getPackagesSelection()) {
      reservationPackageTypes
          .add(createPackageTypeForRemove(reservationPackagesRequest, packagesSelection));
    }

    hotelReservation.setReservationIdList(Collections.singletonList(reservationUniqueIdType));
    hotelReservation.setHotelId(reservationPackagesRequest.getHotelId());
    hotelReservation.setReservationPackages(reservationPackageTypes);

    return List.of(hotelReservation);

  }

  private UniqueIDType createUniqueIdType(String reservationId) {
    final var reservationUniqueIdType =
        new UniqueIDType();
    reservationUniqueIdType.setType(UniqueIdTypeEnumDto.RESERVATION_TYPE.value());
    reservationUniqueIdType.setId(reservationId);

    return reservationUniqueIdType;
  }


  private BigDecimal findPackagePrice(PackagesResponseOhipDto packagesResponseOhipDto, String packageCode) {
    PackageCodeType packageCodeTypeResponse = packagesResponseOhipDto.getPackageCodesList().getPackageCodes().stream()
        .flatMap(packageCodeTypes -> packageCodeTypes.getPackageCodeInfo()
            .stream()
            .filter(packageCodeType -> packageCodeType.getCode().equals(packageCode)))
        .findFirst()
        .orElse(null);

    BigDecimal response = null;
    if (packageCodeTypeResponse != null) {
      response = packageCodeTypeResponse.getHeader().getPostingAttributes().getCalculatedPrice();
    }
    return response;
  }

  private boolean isDonationPackage(String packagesSelectionId) {
    return packagesSelectionId.contains("CHRTY")
            || packagesSelectionId.contains("ZCHRY")
            || packagesSelectionId.contains("ZR0705")
            || packagesSelectionId.contains("ZCHR10")
            || packagesSelectionId.contains("ZCHR11")
            || packagesSelectionId.contains("ZCHR12")
            || packagesSelectionId.contains("ZCHR13");
  }

  private boolean isEarlyCheckInPackage(String packagesSelectionId) {
    return "HSCKIN".equals(packagesSelectionId);
  }

  private boolean isLateCheckOutPackage(String packagesSelectionId) {
    return "HSCOU2".equals(packagesSelectionId);
  }

  private boolean isBookingFeePackage(String packagesSelectionId) {
    return BOOKING_FEE_PACKAGE_ZR.equals(packagesSelectionId)
        || BOOKING_FEE_PACKAGE_ZN.equals(packagesSelectionId);
  }

  private List<ReservationPackageScheduleType> createReservationPackageScheduleType(
      LocalDate startDate, LocalDate endDate, BigDecimal packagePrice, String packageId) {

    List<ReservationPackageScheduleType> scheduleTypes = new ArrayList<>();

    if (BOOKING_FEE_PACKAGE_ZR.equals(packageId)) {
      ReservationPackageScheduleType scheduleType = new ReservationPackageScheduleType();
      scheduleType.setConsumptionDate(startDate);
      scheduleType.setUnitPrice(packagePrice);
      scheduleType.setReservationDate(startDate);

      scheduleTypes.add(scheduleType);
    } else {
      for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
        ReservationPackageScheduleType scheduleType = new ReservationPackageScheduleType();
        scheduleType.setConsumptionDate(date);
        scheduleType.setUnitPrice(packagePrice);
        scheduleType.setReservationDate(date);

        scheduleTypes.add(scheduleType);
      }
    }
    return scheduleTypes;
  }

  private PackageConsumptionType createPackageConsumptionType(PackagesSelection packagesSelection) {
    PackageConsumptionType consumptionDetails = new PackageConsumptionType();
    consumptionDetails.setDefaultQuantity(packagesSelection.getNoSelections());
    consumptionDetails.setTotalQuantity(packagesSelection.getNoSelections());

    return consumptionDetails;
  }

  private ReservationPackageType createPackageTypeForUpdate(PackagesResponseOhipDto packagesResponseOhipDto,
      ReservationPackagesRequest reservationPackagesRequest,
      PackagesSelection packagesSelection) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    LocalDate startDate = LocalDate.parse(reservationPackagesRequest.getArrival(), formatter);
    LocalDate endDate = LocalDate.parse(reservationPackagesRequest.getDeparture(), formatter);

    ReservationPackageType reservationPackageType = new ReservationPackageType();
    reservationPackageType.setPackageHeaderType(new PackageCodeHeaderType());
    var scheduleList = createScheduleList(packagesResponseOhipDto, startDate, endDate,
        packagesSelection, formatter);
    reservationPackageType.setScheduleList(scheduleList);
    reservationPackageType.setConsumptionDetails(createPackageConsumptionType(packagesSelection));
    reservationPackageType.setPackageCode(packagesSelection.getId());
    reservationPackageType.setStartDate(startDate);
    reservationPackageType.setEndDate(endDate);
    reservationPackageType.setPackageGroup(packagesSelection.getPackageGroup());

    return reservationPackageType;
  }

  private List<ReservationPackageScheduleType> createScheduleList(
      PackagesResponseOhipDto packagesResponseOhipDto, LocalDate startDate, LocalDate endDate,
      PackagesSelection packagesSelection, DateTimeFormatter formatter) {

    var packageId = packagesSelection.getId();
    BigDecimal packagePrice = findPackagePrice(packagesResponseOhipDto, packageId);

    if (isDonationPackage(packageId)) {
      endDate = startDate.plusDays(1);
    } else if (isEarlyCheckInPackage(packageId)) {
      endDate = startDate.plusDays(1);
    } else if (isLateCheckOutPackage(packageId)) {
      startDate = endDate.minusDays(1);
    }

    if (isBookingFeePackage(packageId) && packagesSelection.getPrice() != null) {
      packagePrice = BigDecimal.valueOf(packagesSelection.getPrice());
    }

    if (packagesSelection instanceof PackagesSelectionScheduled schedulePackagesSelection
        && isScheduleListPresent(schedulePackagesSelection)) {
      return createScheduleBasedOnSchedulePackages(schedulePackagesSelection, formatter,
          packagePrice);
    } else {
      return createReservationPackageScheduleType(startDate, endDate, packagePrice, packageId);
    }
  }

  private ReservationPackageType createPackageTypeForRemove(ReservationPackagesRequest reservationPackagesRequest,
      PackagesSelection packagesSelection) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    LocalDate startDate = LocalDate.parse(reservationPackagesRequest.getArrival(), formatter);
    LocalDate endDate = LocalDate.parse(reservationPackagesRequest.getDeparture(), formatter);

    ReservationPackageType reservationPackageType = new ReservationPackageType();

    reservationPackageType.setPackageCode(packagesSelection.getId());
    reservationPackageType.setPackageGroup(packagesSelection.getPackageGroup());
    reservationPackageType.setStartDate(startDate);
    reservationPackageType.setEndDate(endDate);

    return reservationPackageType;
  }

  private boolean isScheduleListPresent(PackagesSelectionScheduled schedulePackagesSelection) {
    return schedulePackagesSelection.getScheduledDates() != null
        && !schedulePackagesSelection.getScheduledDates().isEmpty();
  }


  private List<ReservationPackageScheduleType> createScheduleBasedOnSchedulePackages(
      PackagesSelectionScheduled schedulePackagesSelection, DateTimeFormatter formatter,
      BigDecimal packagePrice) {
    return schedulePackagesSelection.getScheduledDates().stream().map(scheduleDate -> {
      LocalDate scheduledDateFormated = LocalDate.parse(scheduleDate, formatter);
      ReservationPackageScheduleType scheduleType = new ReservationPackageScheduleType();
      scheduleType.setConsumptionDate(scheduledDateFormated);
      scheduleType.setUnitPrice(packagePrice);
      scheduleType.setReservationDate(scheduledDateFormated);
      return scheduleType;
    }).toList();
  }

}
