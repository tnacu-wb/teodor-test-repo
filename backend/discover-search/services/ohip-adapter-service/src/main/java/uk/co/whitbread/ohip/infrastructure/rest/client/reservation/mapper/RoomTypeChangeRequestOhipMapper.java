package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static uk.co.whitbread.ohip.ErrorCode.DIGITAL_MATCH_PRICE_BREAKDOWN_EXCEPTION;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AmountType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomRateType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.TotalType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.in.RatePlanRoomTypeChangeRequest;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.AmountTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelAvailabilityDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.RatesTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.RoomRateTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;

@Mapper(componentModel = "spring", uses = {
    ReservationOhipMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {Arrays.class})
public interface RoomTypeChangeRequestOhipMapper {

  default ChangeReservation toChangeReservationDto(
      RatePlanRoomTypeChangeRequest roomTypeChangeRequest,
      List<HotelReservationType> hotelReservationTypeList,
      List<HotelAvailabilityDto> hotelAvailabilityList,
      String defaultMarketCode) {

    List<HotelReservationInstructionType> reservations = hotelReservationTypeList
        .stream()
        .map(hotelReservationType -> {
          var reservationId = hotelReservationType.getReservationIdList()
              .stream()
              .filter(id -> "Reservation".equals(id.getType()))
              .map(UniqueIDType::getId)
              .findFirst().get();
          var reservationIdIndex = roomTypeChangeRequest.getReservationIds().indexOf(
              reservationId);
          var requiredRoomType = roomTypeChangeRequest.getRoomTypes().get(reservationIdIndex);
          var matchingPriceResponse = findPriceBreakdownByRoomCriteria(hotelReservationType,
              hotelAvailabilityList, requiredRoomType);
          return createHotelReservationInstructionType(hotelReservationType, matchingPriceResponse,
              defaultMarketCode);
        })
        .toList();

    var changeReservation = new ChangeReservation();
    changeReservation.setReservations(reservations);
    return changeReservation;
  }

  private RoomRateTypeDto findPriceBreakdownByRoomCriteria(HotelReservationType reservation,
      List<HotelAvailabilityDto> hotelAvailabilitiesList, String requiredRoomType) {

    var adults = reservation.getRoomStay().getGuestCounts().getAdults();
    var children = reservation.getRoomStay().getGuestCounts().getChildren();

    return hotelAvailabilitiesList.stream()
        .flatMap(hotelAvailabilityDto -> hotelAvailabilityDto.getRoomStays().stream())
        .flatMap(roomStayTypeDto -> roomStayTypeDto.getRoomRates().stream())
        .filter(roomRateTypeDto -> requiredRoomType.equals(roomRateTypeDto.getRoomType()))
        .findFirst()
        .orElseThrow(() ->
            new HotelReservationException(DIGITAL_MATCH_PRICE_BREAKDOWN_EXCEPTION,
                String.format(
                    "Could not match price breakdown for roomStay, adults and children %s %s %s",
                    requiredRoomType, adults, children)));
  }

  private RoomStayType createRoomStayType(RoomRateTypeDto matchingPriceResponse,
      String defaultMarketCode, String sourceCode) {

    RoomStayType roomStayType = new RoomStayType();
    roomStayType.addRoomRatesItem(
        createRoomRateType(matchingPriceResponse, defaultMarketCode, sourceCode));

    return roomStayType;
  }

  private RoomRateType createRoomRateType(RoomRateTypeDto matchingPriceResponse,
      String defaultMarketCode, String sourceCode) {
    RoomRateType roomRateType = new RoomRateType();
    roomRateType.setRoomType(matchingPriceResponse.getRoomType());
    roomRateType.setRatePlanCode(matchingPriceResponse.getRatePlanCode());
    roomRateType.setSourceCode(sourceCode);
    roomRateType.setMarketCode(defaultMarketCode);
    roomRateType.setFixedRate(true);

    var availabilityRates = Optional.ofNullable(matchingPriceResponse)
        .map(RoomRateTypeDto::getRates)
        .map(RatesTypeDto::getRate)
        .orElse(Collections.emptyList());

    RatesType rates = new RatesType();
    availabilityRates.stream()
        .map(this::createAmountType)
        .forEach(rates::addRateItem);

    roomRateType.setRates(rates);
    return roomRateType;
  }

  private AmountType createAmountType(AmountTypeDto rate) {
    TotalType base = new TotalType();

    base.amountBeforeTax(rate.getBase().getAmountBeforeTax());

    AmountType amountType = new AmountType();
    amountType.setBase(base);
    amountType.setStart(mapStringToLocalDate(rate.getStart()));
    amountType.setEnd(mapStringToLocalDate(rate.getEnd()));

    return amountType;
  }

  private HotelReservationInstructionType createHotelReservationInstructionType(
      HotelReservationType hotelReservationType, RoomRateTypeDto matchingPriceResponse,
      String defaultMarketCode) {
    HotelReservationInstructionType hotelReservationInstructionType = new HotelReservationInstructionType();
    hotelReservationInstructionType.setReservationIdList(
        hotelReservationType.getReservationIdList());
    hotelReservationInstructionType.setRoomStay(
        createRoomStayType(matchingPriceResponse, defaultMarketCode,
            hotelReservationType.getRoomStay().getRoomRates().get(0).getSourceCode()));
    hotelReservationInstructionType.setHotelId(hotelReservationType.getHotelId());
    return hotelReservationInstructionType;
  }

  private LocalDate mapStringToLocalDate(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    return LocalDate.parse(date, formatter);
  }
}
