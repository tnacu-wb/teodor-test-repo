package uk.co.whitbread.booking.infrastructure.rest.client.booking.mapper;

import static java.time.temporal.ChronoUnit.DAYS;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.booking.domain.model.history.out.Booking;
import uk.co.whitbread.booking.domain.model.history.out.BookingResponse;
import uk.co.whitbread.booking.domain.model.information.out.BookingDetails;
import uk.co.whitbread.booking.domain.model.information.out.BookingInfoResponse;
import uk.co.whitbread.booking.domain.model.information.out.BookingPackagesDetails;
import uk.co.whitbread.booking.domain.model.information.out.BookingPrice;
import uk.co.whitbread.booking.domain.model.information.out.BookingRoom;
import uk.co.whitbread.booking.domain.model.information.out.CancelBookingResponse;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.out.StayDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.out.StayResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.CancelBookingResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayDetailsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayInfoResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayPriceDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayRoomBreakdownDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayRoomDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayUpsellBreakdown;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayUpsellItemDto;

@Mapper(componentModel = "spring")
public interface StayResponseMapper {

  @Mapping(target = "bookings", source = "stays")
  BookingResponse toModel(StayResponseDto stayResponseDto);

  @Mapping(target = "noOfNights", qualifiedByName = "computeDateModel", source = "stay")
  @Mapping(target = "bookingReference", source = "confirmationNumber")
  Booking toModel(StayDto stay);

  BookingInfoResponse toModel(StayInfoResponseDto stayResponseDto);

  @Mapping(
      target = "donationsPackage",
      qualifiedByName = "bookingDonationsPackage",
      source = "response.upsellBreakdown"
  )
  @Mapping(target = "bookingReference", source = "confirmationNumber")
  @Mapping(target = "noOfRooms", expression = "java(response.getRooms().size())")
  @Mapping(target = "refund", qualifiedByName = "computeBalanceOutstanding", source = "response")
  @Mapping(target = "rooms", qualifiedByName = "roomPackage", source = "response")
  @Mapping(target = "outstandingAmount", qualifiedByName = "computeBalanceOutstanding", source = "response")
  @Mapping(target = "cancellationInfoResponse.amendable", qualifiedByName = "isAmendOnline", source = "response")
  @Mapping(target = "cancellationInfoResponse.cancelable", source = "cancelable")
  @Mapping(target = "paymentOption", qualifiedByName = "paymentOption", source = "response.prepaid")
  @Mapping(target = "rateType", source = "rateText")
  @Mapping(
      target = "bookingStatus",
      expression = "java(toBookingStatusModel(response.getRooms().get(0).getBookingStatus()))"
  )
  @Mapping(
      target = "hotelHasCityTaxForLeisure",
      qualifiedByName = "hotelHasCityTaxForLeisure",
      source = "cityTax"
  )
  @Mapping(target = "previousTotal", qualifiedByName = "computePreviousTotal", source = "response")
  @Mapping(target = "newTotal", source = "totalCost")
  @Mapping(target = "cancellationInfoResponse.ruleCompliant",
      qualifiedByName = "computeRuleCompliant", source = "response")
  @Mapping(target = "dinnerAllowance",
      qualifiedByName = "hasDinnerAllowance",
      source = "payment.businessAccount.dinnerAllowance")
  BookingDetails toModel(StayDetailsDto response);

  BookingRoom toModel(StayRoomDto stayRoom);

  List<BookingRoom> toModel(List<StayRoomDto> stayRoomDto);

  @Mapping(target = "packageCode", source = "stayItem.code")
  @Mapping(target = "description", source = "stayItem.legend")
  @Mapping(target = "totalPrice", source = "stayItem.unitCost")
  @Mapping(target = "noSelections", source = "noSelections")
  BookingPackagesDetails toModel(StayUpsellItemDto stayItem, Integer noSelections);

  BookingPrice toModel(StayPriceDto stayPriceDto);

  CancelBookingResponse toModel(CancelBookingResponseDto cancelBookingResponseDto);

  @Named("computeDateModel")
  default int toComputeDateModel(StayDto booking) {
    return (int) DAYS.between(booking.getArrivalDate(), booking.getDepartureDate());
  }

  @Named("computeBalanceOutstanding")
  default BookingPrice toComputeBalanceOutstandingModel(StayDetailsDto stayDetails) {

    if (stayDetails.getPrepaidAmount() == null) {
      return BookingPrice.builder().amount(BigDecimal.ZERO)
          .currency(stayDetails.getTotalCost().getCurrency()).build();
    }
    return BookingPrice.builder().amount(
            stayDetails.getTotalCost().getAmount().subtract(stayDetails.getPrepaidAmount().getAmount()))
        .currency(stayDetails.getTotalCost().getCurrency()).build();
  }

  @Named("computePreviousTotal")
  default BookingPrice toComputePreviousTotalModel(StayDetailsDto stayDetails) {

    if (stayDetails.getPrepaidAmount() == null) {
      return BookingPrice.builder().amount(stayDetails.getTotalCost().getAmount())
          .currency(stayDetails.getTotalCost().getCurrency()).build();
    }
    return BookingPrice.builder()
        .amount(stayDetails.getTotalCost().getAmount()
            .subtract(stayDetails.getTotalCost().getAmount()
                .subtract(stayDetails.getPrepaidAmount().getAmount())))
        .currency(stayDetails.getTotalCost().getCurrency()).build();
  }

  @Named("bookingDonationsPackage")
  default BookingPackagesDetails toBookingDonationsPackageModel(StayUpsellBreakdown response) {

    var donationPackage = response.getUpsellItems().stream()
        .filter(upsellItem -> upsellItem.getCategory().equals("D")).findFirst().map(item -> {
          response.getUpsellItems().remove(item);
          return item;
        });

    return donationPackage.map(stayUpsellItemDto -> toModel(stayUpsellItemDto, 1)).orElse(null);

  }

  @Named("paymentOption")
  default String toAddPaymentOptionModel(boolean prepaid) {
    if (prepaid) {
      return "PAY_NOW";
    }

    return "PAY_ON_ARRIVAL";
  }

  @Named("hotelHasCityTaxForLeisure")
  default Boolean toCheckHotelCityTaxDto(StayPriceDto price) {
    return price != null;
  }

  @Named("roomPackage")
  default List<BookingRoom> toBookingRoomPackagesModel(StayDetailsDto response) {

    List<BookingRoom> bookingRooms = toModel(response.getRooms());

    return bookingRooms.stream().map(room -> {
      toSplitBookingPackagesModel(response.getUpsellBreakdown(), room);
      toRoomCostModel(response.getRoomBreakdown(), room);
      return room;
    }).toList();
  }

  @Named("bookingStatus")
  default String toBookingStatusModel(String bookingStatus) {
    if (StringUtils.isEmpty(bookingStatus)) {
      return null;
    }
    return switch (bookingStatus.trim().replaceAll(" ", "").toUpperCase()) {
      case "UNARRIVED", "PREPAID", "FUTURE", "DUEIN", "RESERVED", "REQUESTED", "WAITLISTED" -> "FUTURE";
      case "RELEASED", "PAST", "NOSHOW", "CHECKEDOUT" -> "PAST";
      case "CANCELLED" -> "CANCELLED";
      case "ARRIVED", "CHECKEDIN", "INHOUSE", "DUEOUT", "PENDINGCHECKOUT" -> "CHECKED_IN";
      default -> null;
    };
  }

  default void toSplitBookingPackagesModel(StayUpsellBreakdown breakdowns, BookingRoom room) {
    if (room.getAdultsMeal() == null) {
      room.setAdultsMeal(new HashSet<>());
    }
    if (room.getKidsMeal() == null) {
      room.setKidsMeal(new HashSet<>());
    }

    breakdowns.getUpsellItems().stream()
        .filter(roomB -> roomB.getRoomId().equals(room.getRoomId()))
        .forEach(roomMeal -> {
          if (roomMeal.getCode().equals("15")) {
            room.getKidsMeal().add(toModel(roomMeal, roomMeal.getQuantity()));
          } else {
            room.getAdultsMeal().add(toModel(roomMeal, roomMeal.getQuantity()));
          }
        });
  }

  default void toRoomCostModel(List<StayRoomBreakdownDto> roomBreakdowns, BookingRoom room) {

    roomBreakdowns.stream()
        .filter(roomBreakdown -> roomBreakdown.getRoomId().equals(room.getRoomId()))
        .forEach(roomB -> room.setRoomCost(toModel(roomB.getTotalRoomCost())));
  }

  @Named("computeRuleCompliant")
  default Boolean toRuleCompliantModel(StayDetailsDto stayDetails) {
    if (stayDetails.getPayment().getBusinessAccount() != null) {
      return stayDetails.getNights() <= 14 && stayDetails.getRooms().size() <= 4;
    }
    return stayDetails.getNights() <= 9 && stayDetails.getRooms().size() <= 4;
  }

  @Named("isAmendOnline")
  default Boolean toAmendOnlineModel(StayDetailsDto stayDetailsDto) {
    return !stayDetailsDto.getAmendOnline() ? stayDetailsDto.getAmendOnline()
        : stayDetailsDto.getAmendable();
  }

  @Named("hasDinnerAllowance")
  default BookingPrice toDinnerAllowanceModel(StayPriceDto dinnerAllowance) {
    if (dinnerAllowance != null) {
      return dinnerAllowance.getAmount() != null ? toModel(dinnerAllowance) : null;
    }

    return null;
  }
}
