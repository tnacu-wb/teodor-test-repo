package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AmountType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DiscountType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomRateType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayType;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateDiscountRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class UpdateDiscountRoomStayOhipMapper {

  private static final String DISCOUNT_REASON = "Promotion";
  private static final String DISCOUNT_CODE = "PR";
  private static final String MARKET_CODE = "OTH";
  private static final String SOURCE_CODE = "00";

  @Mapping(source = "reservationType.roomStay.registrationNumber", target = "registrationNumber")
  @Mapping(source = "reservationType.roomStay.currentRoomInfo", target = "currentRoomInfo")
  @Mapping(expression = "java(injectRoomRates(updateDiscountRequest,reservationType,discountAmounts,lastVisited))",
      target = "roomRates")
  @Mapping(source = "reservationType.roomStay.guestCounts", target = "guestCounts")
  @Mapping(source = "reservationType.roomStay.departureDate", target = "departureDate")
  @Mapping(source = "reservationType.roomStay.arrivalDate", target = "arrivalDate")
  @Mapping(source = "reservationType.roomStay.expectedTimes", target = "expectedTimes")
  @Mapping(source = "reservationType.roomStay.originalTimeSpan", target = "originalTimeSpan")
  @Mapping(source = "reservationType.roomStay.guarantee", target = "guarantee")
  @Mapping(source = "reservationType.roomStay.promotion", target = "promotion")
  @Mapping(source = "reservationType.roomStay.suiteWith", target = "suiteWith")
  @Mapping(source = "reservationType.roomStay.total", target = "total")
  @Mapping(source = "reservationType.roomStay.multiValueAttrs", target = "multiValueAttrs")
  @Mapping(source = "reservationType.roomStay.upsellInfo", target = "upsellInfo")
  @Mapping(source = "reservationType.roomStay.mobileNotifications", target = "mobileNotifications")
  @Mapping(source = "reservationType.roomStay.roomNumberLocked", target = "roomNumberLocked")
  @Mapping(source = "reservationType.roomStay.printRate", target = "printRate")
  @Mapping(source = "reservationType.roomStay.primaryShareType", target = "primaryShareType")
  @Mapping(source = "reservationType.roomStay.remoteCheckInAllowed", target = "remoteCheckInAllowed")
  @Mapping(source = "reservationType.roomStay.bookingMedium", target = "bookingMedium")
  @Mapping(source = "reservationType.roomStay.bookingMediumDescription", target = "bookingMediumDescription")
  @Mapping(source = "reservationType.roomStay.availableUpsellOfferCount", target = "availableUpsellOfferCount")
  abstract RoomStayType fromDto(
      UpdateDiscountRequest updateDiscountRequest,
      HotelReservationType reservationType,
      List<BigDecimal> discountAmounts,
      int[] lastVisited
  );

  protected List<RoomRateType> injectRoomRates(
      UpdateDiscountRequest updateDiscountRequest,
      HotelReservationType reservation,
      List<BigDecimal> discountAmounts,
      int[] lastVisited
  ) {
    List<RoomRateType> roomRates = new ArrayList<>();

    List<RoomRateType> filteredRoomRates = reservation.getRoomStay().getRoomRates()
        .stream().filter(RoomRateType::getDiscountAllowed).toList();

    var roomRatesSize = filteredRoomRates.size();

    IntStream.range(0, roomRatesSize).forEach(index -> {
      RoomRateType roomRate = filteredRoomRates.get(index);
      BigDecimal divAmount = discountAmounts.get(lastVisited[0]);
      AmountType amountTypeTmp = injectAmount(
          divAmount,
          roomRate.getRates().getRate().get(0),
          updateDiscountRequest.getCurrency());

      RoomRateType roomRateTmp = roomRate;
      RatesType rates = new RatesType();

      rates.addRateItem(amountTypeTmp);
      roomRateTmp.setRates(rates);
      roomRateTmp.setMarketCode(MARKET_CODE);
      roomRateTmp.setSourceCode(SOURCE_CODE);

      lastVisited[0]++;
      roomRates.add(roomRateTmp);
    });

    return roomRates;
  }

  private AmountType injectAmount(BigDecimal discountAmount,
      AmountType oldAmount,
      String currency) {
    var amountType = new AmountType();
    amountType.setTotal(oldAmount.getTotal());
    amountType.setDiscount(injectDiscount(discountAmount, currency));
    amountType.setStart(oldAmount.getStart());
    amountType.setEnd(oldAmount.getEnd());
    amountType.setBase(oldAmount.getBase());
    return amountType;
  }

  private DiscountType injectDiscount(BigDecimal discountAmount, String currency) {
    var discountType = new DiscountType();
    discountType.setAmount(discountAmount);
    discountType.setCurrencyCode(currency);
    discountType.discountReason(DISCOUNT_REASON);
    discountType.discountCode(DISCOUNT_CODE);
    return discountType;
  }
}
