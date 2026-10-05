package uk.co.whitbread.booking.infrastructure.rest.client.reservation.mapper;


import static java.time.temporal.ChronoUnit.DAYS;
import static uk.co.whitbread.booking.infrastructure.rest.client.reservation.utils.ReservationUtils.isPastBooking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.booking.domain.model.information.out.BookingDetails;
import uk.co.whitbread.booking.domain.model.information.out.BookingInfoResponse;
import uk.co.whitbread.booking.domain.model.information.out.BookingPackagesDetails;
import uk.co.whitbread.booking.domain.model.information.out.BookingPackagesDetails.BookingPackagesDetailsBuilder;
import uk.co.whitbread.booking.domain.model.information.out.BookingPrice;
import uk.co.whitbread.booking.domain.model.information.out.BookingRoom;
import uk.co.whitbread.booking.domain.model.information.out.CancelBookingResponse;
import uk.co.whitbread.booking.domain.model.information.out.Guest;
import uk.co.whitbread.booking.domain.model.information.out.PaymentCard;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.MealsInfoResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.UpsellItemsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.PackageGroupsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.ReservationPackagesDetailsResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.CancelReservationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationAllowanceDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationAllowancesDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationBasketOptionsResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationCancelInfoResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationDetailsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationGuestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationPackagesDetailsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationPaymentCardDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationRateClasificationsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationRateInformationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationResponseDto;

@Mapper(componentModel = "spring")
public interface ReservationResponseConverter {

  String DINNER_ALLOWANCE = "dinner";
  String CANCELLED_SATUTS = "CANCELLED";
  String EARLY_CHECK_IN_PACKAGE = "HSCKIN";
  String LATE_CHECK_IN_PACKAGE = "HSCOU2";
  String CHILDREN_BREAKFAST_PACKAGE = "BFCHDF";
  String CHILDREN_DINNER_PACKAGE = "DBCHDF";
  String CITY_TAX_PACKAGE = "CITYTAX";
  String ULTIMATE_WIFI = "FI24HR";
  String BOTTLE_OF_PROSECCO = "DBPROS";
  String MDP = "MDP";
  String DBR = "DBR";
  String BBIB = "BBIB";

  @Mapping(
      target = "checkInTime",
      qualifiedByName = "checkInModel",
      source = "reservationResponse.reservationByIdList"
  )
  @Mapping(
      target = "checkOutTime",
      qualifiedByName = "checkOutModel",
      source = "reservationResponse.reservationByIdList"
  )
  @Mapping(target = "reservationDetails", source = "bookingDetails")
  BookingInfoResponse toModel(ReservationResponseDto reservationResponse,
      BookingDetails bookingDetails);

  @Mapping(
      target = "donationsPackage",
      source = "reservationResponse",
      qualifiedByName = "donationPackageModel"
  )
  @Mapping(target = "hotelCode", source = "reservationResponse.hotelId")
  @Mapping(target = "totalCost.amount", source = "reservationResponse.totalCost")
  @Mapping(target = "totalCost.currency", source = "reservationResponse.currencyCode")
  @Mapping(target = "outstandingAmount.amount", source = "reservationResponse.balanceOutstanding")
  @Mapping(target = "outstandingAmount.currency", source = "reservationResponse.currencyCode")
  @Mapping(target = "previousTotal.amount", source = "reservationResponse.previousTotal")
  @Mapping(target = "previousTotal.currency", source = "reservationResponse.currencyCode")
  @Mapping(
      target = "arrivalDate",
      qualifiedByName = "arrivalDateModel",
      source = "reservationResponse.reservationByIdList"
  )
  @Mapping(
      target = "departureDate",
      qualifiedByName = "departureDateModel",
      source = "reservationResponse.reservationByIdList"
  )
  @Mapping(
      target = "nights",
      qualifiedByName = "computedDateModel",
      source = "reservationResponse.reservationByIdList"
  )
  @Mapping(
      target = "noOfRooms",
      expression = "java(reservationResponse.getReservationByIdList().size())"
  )
  @Mapping(
      target = "rooms",
      expression = "java(toCurrencyModel(reservationResponse, packageGroupsResponse))"
  )
  @Mapping(target = "refund.amount", qualifiedByName = "computePriceModel", source = "reservationResponse")
  @Mapping(target = "refund.currency", source = "reservationResponse.currencyCode")
  @Mapping(
      target = "prepaidAmount.amount",
      qualifiedByName = "computePriceModel",
      source = "reservationResponse"
  )
  @Mapping(target = "prepaidAmount.currency", source = "reservationResponse.currencyCode")
  @Mapping(
      target = "rateDescription",
      expression = "java(toRateMessageModel(reservationResponse, rateResponse))"
  )
  @Mapping(target = "paymentOption", source = "basketOptions.paymentOption")
  @Mapping(target = "cancellationInfoResponse.amendable", source = "cancelResponse.isAmendable")
  @Mapping(target = "cancellationInfoResponse.cancelable", source = "cancelResponse.isCancellable")
  @Mapping(target = "cancellationInfoResponse.ruleCompliant", source = "cancelResponse.isRuleCompliant")
  @Mapping(target = "cancellationInfoResponse.aemLabelKey", source = "cancelResponse.aemLabelKey")
  @Mapping(target = "bookingReference", source = "bookingReference")
  @Mapping(target = "rateType", qualifiedByName = "roomTypeModel", source = "reservationResponse")
  @Mapping(target = "bookingStatus", qualifiedByName = "bookingStatus", source = "reservationResponse")
  @Mapping(target = "newTotal.amount", source = "reservationResponse.newTotal")
  @Mapping(target = "newTotal.currency", source = "reservationResponse.currencyCode")
  @Mapping(
      target = "hotelHasCityTaxForLeisure",
      qualifiedByName = "hotelHasCityTax",
      source = "reservationResponse.reservationByIdList"
  )
  @Mapping(
      target = "payment",
      qualifiedByName = "paymentCardModel",
      source = "reservationResponse.reservationByIdList"
  )
  @Mapping(
      target = "dinnerAllowance",
      expression = "java(toDinnerAllowancePriceModel(reservationAllowances, reservationResponse.getCurrencyCode()))"
  )
  @Mapping(target = "basketReference", source = "reservationResponse.basketReference")
  @Mapping(
      target = "cityTaxTotal",
      expression = "java(toBookingPriceModel(cityTaxTotal, reservationResponse.getCurrencyCode()))"
  )
  BookingDetails toModel(ReservationResponseDto reservationResponse,
      ReservationBasketOptionsResponseDto basketOptions,
      ReservationRateInformationResponseDto rateResponse,
      ReservationCancelInfoResponseDto cancelResponse,
      String bookingReference,
      ReservationAllowancesDto reservationAllowances,
      @Context List<PackageGroupsDto> packageGroupsResponse,
      BigDecimal cityTaxTotal
  );

  @Mapping(target = "amount", source = "reservationAllowance.budget")
  @Mapping(target = "currency", source = "currencyCode")
  BookingPrice toModel(ReservationAllowanceDto reservationAllowance, String currencyCode);

  @Mapping(target = "roomType", source = "roomStay.roomType")
  @Mapping(target = "cot", source = "roomStay.cot")
  @Mapping(target = "roomCost.amount", source = "roomStay.roomPrice")
  @Mapping(target = "children", source = "roomStay.childrenNumber")
  @Mapping(target = "adults", source = "roomStay.adultsNumber")
  @Mapping(target = "guest", qualifiedByName = "guest", source = "reservationGuestList")
  @Mapping(
      target = "adultsMeal",
      expression = "java(toAdultsMealModel("
          + "reservationDetailsDto.getReservationPackageList(), packageGroupsResponse))"
  )
  @Mapping(target = "kidsMeal", qualifiedByName = "kidsMeal", source = "reservationPackageList")
  @Mapping(target = "extrasItems", qualifiedByName = "extrasItems", source = "reservationPackageList")
  @Mapping(target = "checkInTime", source = "roomStay.checkInTime")
  @Mapping(target = "checkOutTime", source = "roomStay.checkOutTime")
  BookingRoom toModel(ReservationDetailsDto reservationDetailsDto,
      @Context List<PackageGroupsDto> packageGroupsResponse);

  List<BookingRoom> toModel(List<ReservationDetailsDto> reservationDetails,
      @Context  List<PackageGroupsDto> packageGroupsResponse);

  List<BookingRoom> toModel(List<ReservationDetailsDto> reservationDetails);

  @Mapping(target = "totalPrice.amount", source = "reservationPackagesDetailsDto.computedPrice")
  @Mapping(target = "noSelections", source = "noSelections")
  BookingPackagesDetails toModel(ReservationPackagesDetailsDto reservationPackagesDetailsDto,
      Integer noSelections);

  @Mapping(source = "basketReference", target = "bookingReference")
  CancelBookingResponse toModel(CancelReservationResponseDto cancelReservationResponseDto);

  @Mapping(
      target = "totalPrice",
      expression = "java(toBookingPriceModel(reservationPackagesDetailsDto.getComputedPrice(), currencyCode))"
  )
  @Mapping(target = "noSelections", source = "reservationPackagesDetailsDto.totalQuantity")
  BookingPackagesDetails toDonationsModel(
      ReservationPackagesDetailsDto reservationPackagesDetailsDto, String currencyCode);

  @Mapping(target = "packageCode", source = "packageGroup")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "totalQuantity", source = "totalQuantity")
  @Mapping(target = "computedPrice", source = "totalPrice")
  ReservationPackagesDetailsDto toSingleDynamicPackageDto(BigDecimal totalPrice,
      Integer totalQuantity, String packageGroup, String description
  );

  @Mapping(target = "packageCode", constant = "BBIB")
  @Mapping(target = "description", constant = "Premier Inn Breakfast")
  @Mapping(target = "totalQuantity", source = "totalQuantity")
  @Mapping(target = "computedPrice", source = "totalPrice")
  ReservationPackagesDetailsDto toSinglePiBreakfastDto(ReservationPackagesDetailsDto packageDto,
      BigDecimal totalPrice, Integer totalQuantity);

  @Mapping(target = "amount", source = "amount")
  @Mapping(target = "currency", source = "currency")
  BookingPrice toBookingPriceModel(BigDecimal amount, String currency);

  PaymentCard toPaymentCardModel(ReservationPaymentCardDto paymentCard);

  @Named("paymentCardModel")
  default PaymentCard toPaymentCardModel(List<ReservationDetailsDto> reservationDetails) {
    var paymentCardDto = reservationDetails.stream()
        .map(ReservationDetailsDto::getPaymentCard).findFirst();

    return toPaymentCardModel(paymentCardDto.orElse(null));

  }

  @Named("arrivalDateModel")
  default LocalDate toArrivalDateModel(List<ReservationDetailsDto> reservationDetails) {
    var arrivalDate = reservationDetails.stream()
        .map(details -> details.getRoomStay().getArrivalDate()).findFirst();

    return arrivalDate.map(this::toLocalDateModel).orElse(null);

  }

  @Named("departureDateModel")
  default LocalDate toDepartureDateModel(List<ReservationDetailsDto> reservationDetails) {
    var departureDate = reservationDetails.stream()
        .map(details -> details.getRoomStay().getDepartureDate()).findFirst();

    return departureDate.map(this::toLocalDateModel).orElse(null);
  }

  default Set<BookingPackagesDetails> toAdultsMealModel(
      List<ReservationPackagesDetailsDto> reservationPackages,
      @Context List<PackageGroupsDto> packageGroupsResponse) {

    if (reservationPackages.isEmpty()) {
      return Collections.emptySet();
    }

    var adultsPackages = new ArrayList<>(reservationPackages.stream()
        .filter(
            reservationPackage -> !reservationPackage.getPackageCode().equalsIgnoreCase(CHILDREN_BREAKFAST_PACKAGE))
        .filter(
            reservationPackage -> !reservationPackage.getPackageCode().equalsIgnoreCase(CHILDREN_DINNER_PACKAGE))
        .filter(
            reservationPackage -> !reservationPackage.getPackageCode().equalsIgnoreCase(CITY_TAX_PACKAGE))
        .filter(
            reservationPackage -> !reservationPackage.getPackageCode().equalsIgnoreCase(EARLY_CHECK_IN_PACKAGE))
        .filter(
            reservationPackage -> !reservationPackage.getPackageCode().equalsIgnoreCase(LATE_CHECK_IN_PACKAGE))
        .filter(
            reservationPackage -> !reservationPackage.getPackageCode().equalsIgnoreCase(ULTIMATE_WIFI))
        .filter(
            reservationPackage -> !reservationPackage.getPackageCode().equalsIgnoreCase(BOTTLE_OF_PROSECCO))
        .toList());

    if (!adultsPackages.isEmpty()) {
      toCheckAndAddMultiPackagesDto(adultsPackages, packageGroupsResponse);

      return adultsPackages.stream()
          .map(packageDetails -> toBookingPackageDetails(packageDetails, packageDetails.getTotalQuantity()))
          .collect(Collectors.toSet());
    }

    return Collections.emptySet();

  }

  private BookingPackagesDetails toBookingPackageDetails(
      ReservationPackagesDetailsResponseDto reservationPackagesDetailsDto,
      Integer noSelections) {
    BookingPackagesDetailsBuilder bookingPackagesDetails = BookingPackagesDetails.builder();
    bookingPackagesDetails.totalPrice(
        buildBookingPrice(reservationPackagesDetailsDto.getPackageCode(),
            reservationPackagesDetailsDto.getComputedPrice(),
            reservationPackagesDetailsDto.getUnitPrice()));
    bookingPackagesDetails.packageCode(reservationPackagesDetailsDto.getPackageCode());
    bookingPackagesDetails.description(reservationPackagesDetailsDto.getDescription());

    if (noSelections != null) {
      bookingPackagesDetails.noSelections(noSelections);
    }
    return bookingPackagesDetails.build();
  }

  default BookingPackagesDetails toBookingPackageDetails(
      ReservationPackagesDetailsDto reservationPackagesDetailsDto,
      Integer noSelections) {
    if (reservationPackagesDetailsDto == null && noSelections == null) {
      return null;
    }
    BookingPackagesDetailsBuilder bookingPackagesDetails =
        BookingPackagesDetails.builder();
    if (reservationPackagesDetailsDto != null) {
      bookingPackagesDetails.totalPrice(
          buildBookingPrice(reservationPackagesDetailsDto.getPackageCode(),
              reservationPackagesDetailsDto.getComputedPrice(),
              reservationPackagesDetailsDto.getUnitPrice()));
      bookingPackagesDetails.packageCode(reservationPackagesDetailsDto.getPackageCode());
      bookingPackagesDetails.description(reservationPackagesDetailsDto.getDescription());
    }
    if (noSelections != null) {
      bookingPackagesDetails.noSelections(noSelections);
    }
    return bookingPackagesDetails.build();
  }

  private BookingPrice buildBookingPrice(String packageCode, BigDecimal computedPrice,
      BigDecimal unitPrice) {
    List<String> adultsPackageGroup = List.of(MDP, DBR, BBIB);
    return BookingPrice.builder().amount(
        adultsPackageGroup.stream().anyMatch(code -> code.equalsIgnoreCase(packageCode))
            ? computedPrice : unitPrice).build();
  }

  @Named("kidsMeal")
  default Set<BookingPackagesDetails> toKidsMealModel(
      List<ReservationPackagesDetailsDto> reservationPackages) {

    return reservationPackages.stream()
        .filter(
            reservationPackage ->
                reservationPackage.getPackageCode().equalsIgnoreCase(CHILDREN_BREAKFAST_PACKAGE)
                    || reservationPackage.getPackageCode()
                    .equalsIgnoreCase(CHILDREN_DINNER_PACKAGE))
        .map(
            reservationPackage -> toBookingPackageDetails(reservationPackage,
                reservationPackage.getTotalQuantity())).collect(Collectors.toSet());

  }

  @Named("extrasItems")
  default Set<BookingPackagesDetails> toExtrasItemsModel(
          List<ReservationPackagesDetailsDto> reservationPackages) {

    return reservationPackages.stream()
        .filter(
            reservationPackage ->
                reservationPackage.getPackageCode().equalsIgnoreCase(EARLY_CHECK_IN_PACKAGE)
                    || reservationPackage.getPackageCode().equalsIgnoreCase(LATE_CHECK_IN_PACKAGE)
                    || reservationPackage.getPackageCode().equalsIgnoreCase(ULTIMATE_WIFI)
                    || reservationPackage.getPackageCode().equalsIgnoreCase(BOTTLE_OF_PROSECCO))
        .map(
            reservationPackage -> toBookingPackageDetails(reservationPackage,
                reservationPackage.getTotalQuantity())).collect(Collectors.toSet());
  }

  default Set<BookingPackagesDetails> toBookingPackagesModel(
      List<ReservationPackagesDetailsResponseDto> reservationPackages) {

    return reservationPackages.stream()
        .filter(Objects::nonNull)
        .filter(
            reservationPackage -> reservationPackage.getPackageCode().equalsIgnoreCase(EARLY_CHECK_IN_PACKAGE)
                || reservationPackage.getPackageCode().equalsIgnoreCase(LATE_CHECK_IN_PACKAGE)
                || reservationPackage.getPackageCode().equalsIgnoreCase(ULTIMATE_WIFI)
                || reservationPackage.getPackageCode().equalsIgnoreCase(BOTTLE_OF_PROSECCO))
                .map(
                    reservationPackage -> toBookingPackageDetails(reservationPackage,
                        reservationPackage.getTotalQuantity())).collect(Collectors.toSet());
  }

  @Named("checkOutModel")
  default LocalTime toCheckOutTimeModel(List<ReservationDetailsDto> reservationDetails) {
    var checkOut = reservationDetails.stream()
        .map(details -> details.getRoomStay().getCheckOutTime()).findFirst();

    return checkOut.map(this::toLocalTimeModel).orElse(null);

  }

  @Named("checkInModel")
  default LocalTime toCheckInTimeModel(List<ReservationDetailsDto> reservationDetails) {
    var checkIn = reservationDetails.stream()
        .map(details -> details.getRoomStay().getCheckInTime())
        .findFirst();

    return checkIn.map(this::toLocalTimeModel).orElse(null);
  }

  @Named("computedDateModel")
  default int toComputedDateModel(List<ReservationDetailsDto> reservationDetails) {

    var arrivalDate = toArrivalDateModel(reservationDetails);
    var departureDate = toDepartureDateModel(reservationDetails);

    return (int) DAYS.between(arrivalDate, departureDate);
  }

  default List<BookingRoom> toCurrencyModel(ReservationResponseDto reservationResponse,
      @Context List<PackageGroupsDto> packageGroupsResponse) {

    List<BookingRoom> bookingRooms = toModel(reservationResponse.getReservationByIdList(),
        packageGroupsResponse);

    return bookingRooms.stream().map(room -> {
      room.getRoomCost().setCurrency(reservationResponse.getCurrencyCode());
      if (room.getAdultsMeal() != null) {
        room.getAdultsMeal().forEach(roomPackage -> roomPackage.getTotalPrice()
            .setCurrency(reservationResponse.getCurrencyCode()));
      }
      if (room.getKidsMeal() != null) {
        room.getKidsMeal().forEach(roomPackage -> roomPackage.getTotalPrice()
            .setCurrency(reservationResponse.getCurrencyCode()));
      }
      return room;
    }).toList();

  }

  @Named("computePriceModel")
  default BigDecimal toComputePriceModel(ReservationResponseDto reservationResponse) {

    return reservationResponse.getTotalCost().subtract(reservationResponse.getBalanceOutstanding());
  }

  @Named("donationPackageModel")
  default BookingPackagesDetails toDonationPackageModel(ReservationResponseDto response) {

    var optionalDonationPackage = response.getReservationByIdList().stream().map(
        reservationDetail -> reservationDetail.getReservationPackageList().stream()
            .filter(packageDetail -> packageDetail.getPackageCode().toLowerCase().contains("zchry"))
            .findFirst()
            .map(reservationPackage -> {
              reservationDetail.getReservationPackageList().remove(reservationPackage);
              return toDonationsModel(reservationPackage, response.getCurrencyCode());
            })).findFirst().orElseThrow();

    return optionalDonationPackage.orElse(null);
  }

  @Named("roomTypeModel")
  default String toRoomTypeModel(ReservationResponseDto reservationResponse) {
    var optionalReservationResponse = reservationResponse.getReservationByIdList().stream()
        .map(details -> details.getRoomStay().getRatePlanCode()).findFirst();

    return optionalReservationResponse.orElse(null);
  }

  @Named("bookingStatus")
  default String toBookingStatusModel(ReservationResponseDto response) {
    var uncancelledReservations = response.getReservationByIdList().stream()
        .filter(rsv -> !rsv.getReservationStatus().equalsIgnoreCase(CANCELLED_SATUTS)).toList();

    var status = uncancelledReservations.isEmpty() ? Optional.of(CANCELLED_SATUTS) : uncancelledReservations.stream()
        .map(ReservationDetailsDto::getReservationStatus)
        .findFirst();

    if (status.isPresent()) {
      if (isPastBooking(response) && !status.get().equalsIgnoreCase(CANCELLED_SATUTS)) {
        return "PAST";
      }

      if (status.get().equalsIgnoreCase("RESERVED")) {
        return "FUTURE";
      }

      return "CANCELED";
    }

    return null;
  }

  @Named("hotelHasCityTax")
  default Boolean toHotelHasCityTaxForLeisureModel(List<ReservationDetailsDto> response) {

    return response.stream().map(reservation -> {
      var existingCityTax = reservation.getRoomStay().getRatesPerNight().stream()
          .filter(rate -> rate.getCityTaxPerNight().compareTo(BigDecimal.ZERO) != 0).findFirst();

      return existingCityTax.isPresent();
    }).findAny().orElseThrow();
  }

  default BookingPrice toDinnerAllowancePriceModel(ReservationAllowancesDto reservationAllowances,
      String currencyCode) {

    if (reservationAllowances != null) {
      var dinnerAllowance = reservationAllowances.getBookingAllowances().stream()
          .filter(allowance -> DINNER_ALLOWANCE.equalsIgnoreCase(allowance.getAllowance()))
          .findFirst();

      return dinnerAllowance.map(allowance -> toModel(allowance, currencyCode)).orElse(null);
    }

    return null;
  }

  default LocalDate toLocalDateModel(String date) {

    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return LocalDate.parse(date, format);
  }

  default LocalTime toLocalTimeModel(String date) {

    DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");
    return LocalTime.parse(date, format);
  }

  default String toRateMessageModel(ReservationResponseDto response,
      ReservationRateInformationResponseDto rateResponse) {

    var optionalRateMessage =
        response.getReservationByIdList().stream().map(room -> rateResponse.getRateClassifications()
                .stream()
                .filter(rateInfo -> rateInfo.getRateClassification()
                    .equals(room.getRoomStay().getRatePlanCode()))
                .map(ReservationRateClasificationsDto::getRateNotes)
                .findFirst())
            .findAny().orElseThrow();

    return optionalRateMessage.orElse(null);
  }

  default void toCheckAndAddMultiPackagesDto(
      List<ReservationPackagesDetailsDto> adultsPackages,
      List<PackageGroupsDto> ohipPackageGroups) {

    if (adultsPackages == null
        || adultsPackages.isEmpty()
        || ohipPackageGroups == null
        || ohipPackageGroups.isEmpty()) {
      return;
    }
    final var packagesWithNullGroup = adultsPackages.stream()
        .filter(pkg -> StringUtils.isEmpty(pkg.getPackageGroup()))
        .toList();

    Map<String, List<ReservationPackagesDetailsDto>> reservationPackagesGroupMap =
        adultsPackages.stream()
            .filter(pkg -> pkg.getPackageGroup() != null)
            .collect(Collectors.groupingBy(ReservationPackagesDetailsDto::getPackageGroup));

    List<ReservationPackagesDetailsDto> consolidatedPackages = new ArrayList<>();

    for (var entry : reservationPackagesGroupMap.entrySet()) {
      String packageGroupKey = entry.getKey();
      List<ReservationPackagesDetailsDto> groupPackages = entry.getValue();

      var totalPrice = toComputedMultiPackagesTotalPriceModel(groupPackages);

      Optional<PackageGroupsDto> packageGroupDetailsOpt = ohipPackageGroups.stream()
          .filter(pg -> pg.getPackageGroup().equals(packageGroupKey))
          .findFirst();

      String groupCode = packageGroupDetailsOpt.map(PackageGroupsDto::getPackageGroup).orElse("");
      String groupDescription = packageGroupDetailsOpt
          .map(PackageGroupsDto::getPackageGroupDescription)
          .orElse("");

      ReservationPackagesDetailsDto consolidated = toSingleDynamicPackageDto(
          totalPrice,
          groupPackages.get(0).getTotalQuantity(),
          groupCode,
          groupDescription
      );

      consolidatedPackages.add(consolidated);
    }

    adultsPackages.clear();
    adultsPackages.addAll(consolidatedPackages);
    adultsPackages.addAll(packagesWithNullGroup);
  }

  default BigDecimal toComputedMultiPackagesTotalPriceModel(List<ReservationPackagesDetailsDto> extraPackages) {
    return extraPackages.stream()
        .map(ReservationPackagesDetailsDto::getUnitPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Named("guest")
  default Guest toGuest(List<ReservationGuestDto> guests) {
    if (guests == null || guests.isEmpty()) {
      return null;
    }
    ReservationGuestDto leadGuest = guests.get(0);
    return Guest.builder()
        .title(leadGuest.getNameTitle())
        .firstName(leadGuest.getGivenName())
        .lastName(leadGuest.getSurName())
        .email(leadGuest.getEmail())
        .build();
  }

  default Set<BookingPackagesDetails> getUpdatedMealPackages(
      final Set<BookingPackagesDetails> mealPackages,
      final MealsInfoResponseDto aemMealsInfo) {
    final Set<BookingPackagesDetails> updatedMealPackages = mealPackages;
    if (mealPackages != null && aemMealsInfo != null) {
      updatedMealPackages.stream().forEach(mealPackage -> {
        final String aemMealPackageDescription =
            getAemMealPackageDescription(aemMealsInfo, mealPackage.getPackageCode());
        if (StringUtils.isNotEmpty(aemMealPackageDescription)) {
          mealPackage.setDescription(aemMealPackageDescription);
        }
      });
    }
    return updatedMealPackages;
  }

  default String getAemMealPackageDescription(final MealsInfoResponseDto aemMealsInfo,
      final String packageCode) {
    final UpsellItemsDto aemMealPackageDescription = aemMealsInfo.getUpsellItems()
        .stream()
        .filter(upsellItemsDto -> upsellItemsDto.getCode().equalsIgnoreCase(packageCode))
        .findFirst().orElse(new UpsellItemsDto());
    return aemMealPackageDescription.getName();
  }

}
