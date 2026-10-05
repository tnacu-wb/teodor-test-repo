package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static uk.co.whitbread.ohip.ErrorCode.DIGITAL_MATCH_PRICE_BREAKDOWN_EXCEPTION;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
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
import uk.co.whitbread.ohip.domain.model.reservation.in.RatePlanRoomTypeChangeRequest;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.DetailDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.PriceBreakdownOhipDto;


@Mapper(componentModel = "spring", uses = {
    ReservationOhipMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {Arrays.class})
public interface RatePlanChangeRequestOhipMapper {

  default ChangeReservation toChangeReservationDto(
      RatePlanRoomTypeChangeRequest ratePlanChangeRequest,
      List<HotelReservationType> reservationsResponses,
      List<PriceBreakdownOhipDto> priceBreakdownResponse,
      String defaultMarketCode, String sourceCode) {

    var changeReservation = new ChangeReservation();
    List<HotelReservationInstructionType> reservations = reservationsResponses
        .stream()
        .map(reservation -> updateReservation(ratePlanChangeRequest, reservation,
            priceBreakdownResponse,
            defaultMarketCode, sourceCode))
        .toList();

    changeReservation.setReservations(reservations);

    return changeReservation;
  }

  private HotelReservationInstructionType updateReservation(
      RatePlanRoomTypeChangeRequest ratePlanChangeRequest,
      HotelReservationType reservation,
      List<PriceBreakdownOhipDto> priceBreakdownResponse, String defaultMarketCode,
      String sourceCode) {

    var reservationInstructionType = createHotelReservationInstructionType(reservation);
    var matchingPriceResponse = findPriceBreakdownByRoomCriteria(reservation,
        priceBreakdownResponse);
    return createRoomStay(ratePlanChangeRequest.getRateCode(), reservationInstructionType,
        matchingPriceResponse,
        ratePlanChangeRequest.getHotelId(), defaultMarketCode, sourceCode);
  }

  private PriceBreakdownOhipDto findPriceBreakdownByRoomCriteria(HotelReservationType reservation,
      List<PriceBreakdownOhipDto> priceBreakdownResponse) {

    var roomType = reservation.getRoomStay().getCurrentRoomInfo().getRoomType();
    var adults = reservation.getRoomStay().getGuestCounts().getAdults();
    var children = reservation.getRoomStay().getGuestCounts().getChildren();

    return priceBreakdownResponse
        .stream()
        .filter(priceBreakdownOhipDto ->
            priceBreakdownOhipDto.getRoomType().equals(roomType)
                && priceBreakdownOhipDto.getAdults().equals(adults)
                && priceBreakdownOhipDto.getChildren().equals(children))
        .findFirst()
        .orElseThrow(() ->
            new HotelReservationException(DIGITAL_MATCH_PRICE_BREAKDOWN_EXCEPTION,
                String.format(
                    "Could not match price breakdown for roomStay, adults and children %s %s %s",
                    roomType, adults, children)));
  }

  private HotelReservationInstructionType createRoomStay(String ratePlanCode,
      HotelReservationInstructionType hotelReservationInstructionType,
      PriceBreakdownOhipDto matchingPriceResponse,
      String hotelId, String defaultMarketCode, String sourceCode) {
    RoomRateType roomRatesTmp = new RoomRateType();
    RatesType rates = new RatesType();
    matchingPriceResponse.getSummary().getDetails().forEach(roomDetails -> {
      rates.addRateItem(createAmountType(roomDetails));
      roomRatesTmp.setRoomType(matchingPriceResponse.getRoomType());
    });
    roomRatesTmp.setRatePlanCode(ratePlanCode);
    roomRatesTmp.setSourceCode(sourceCode);
    roomRatesTmp.setMarketCode(defaultMarketCode);
    roomRatesTmp.setRates(rates);

    RoomStayType roomStayType = new RoomStayType();
    roomStayType.addRoomRatesItem(roomRatesTmp);

    hotelReservationInstructionType.setRoomStay(roomStayType);
    hotelReservationInstructionType.setHotelId(hotelId);

    return hotelReservationInstructionType;
  }

  private AmountType createAmountType(DetailDto roomDetails) {
    TotalType base = new TotalType();
    base.amountBeforeTax(roomDetails.getNet());
    AmountType amountType = new AmountType();
    amountType.setBase(base);
    amountType.setStart(mapStringToLocalDate(roomDetails.getSummaryDate()));
    amountType.setEnd(mapStringToLocalDate(roomDetails.getSummaryDate()));

    return amountType;
  }

  private HotelReservationInstructionType createHotelReservationInstructionType(
      HotelReservationType reservation) {
    HotelReservationInstructionType reservationInstructionType = new HotelReservationInstructionType();
    reservationInstructionType.setReservationIdList(reservation.getReservationIdList());
    return reservationInstructionType;
  }

  private LocalDate mapStringToLocalDate(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    return LocalDate.parse(date, formatter);
  }

}
