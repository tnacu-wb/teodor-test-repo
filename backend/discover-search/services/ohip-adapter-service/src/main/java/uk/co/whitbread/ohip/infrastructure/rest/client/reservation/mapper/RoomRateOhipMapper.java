package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AmountType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomRateType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.TotalType;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingChannel;
import uk.co.whitbread.ohip.domain.model.reservation.in.RatePrice;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomRateReservation;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RoomRateOhipMapper {

  @Mapping(source = "roomRate.roomType", target = "roomType")
  // RoomTypeCharged INFO
  // roomTypeCharged is required by Opera if no rate amounts are specified
  // we currently have no requirement to charge a room as another room type
  // therefore we always charge the actual room type
  @Mapping(source = "roomRate.roomType", target = "roomTypeCharged")
  @Mapping(source = "roomRate.ratePlanCode", target = "ratePlanCode")
  @Mapping(constant = "1", target = "numberOfUnits")
  @Mapping(source = "sourceCode", target = "sourceCode")
  @Mapping(source = "defaultMarketCode", target = "marketCode")
  @Mapping(expression = "java(toStartDateForDto(roomRate, ratePrice))", target = "start")
  @Mapping(expression = "java(toEndDateForDto(roomRate, ratePrice))", target = "end")
  @Mapping(source = "ratePrice", target = "rates", qualifiedByName = "toRatesTypeDto")
  @Mapping(expression = "java(toFixedRateForDto(ratePrice))", target = "fixedRate")
  RoomRateType toRoomRateTypeModel(RoomRateReservation roomRate, RatePrice ratePrice,
      String sourceCode, String defaultMarketCode);

  default List<RoomRateType> toRoomRateTypesModel(RoomRateReservation roomRate, String sourceCode,
      String defaultMarketCode, BookingChannel bookingChannel) {
    if (bookingChannel.isAllowedFixedRate() && CollectionUtils.isNotEmpty(roomRate.getRatePrices())) {
      return roomRate.getRatePrices().stream()
          .map(ratePrice -> toRoomRateTypeModel(roomRate, ratePrice, sourceCode, defaultMarketCode))
          .toList();
    } else {
      return List.of(toRoomRateTypeModel(roomRate, null, sourceCode, defaultMarketCode));
    }
  }

  @Named("toRatesTypeDto")
  default RatesType toRatesTypeDto(RatePrice ratePrice) {
    if (ratePrice == null) {
      return null;
    }

    var totalType = new TotalType();
    totalType.setAmountBeforeTax(ratePrice.getAmount());

    var amountType = new AmountType();
    amountType.setBase(totalType);
    amountType.setStart(ratePrice.getPriceStartDate());
    // Start date is intended here, OPERA needs one entry for each stay date
    amountType.setEnd(ratePrice.getPriceStartDate());

    var ratesType = new RatesType();
    ratesType.setRate(List.of(amountType));

    return ratesType;
  }

  default LocalDate toStartDateForDto(RoomRateReservation roomRate, RatePrice ratePrice) {
    return Optional.ofNullable(ratePrice).map(RatePrice::getPriceStartDate)
        .orElse(LocalDate.parse(roomRate.getStart()));
  }

  default LocalDate toEndDateForDto(RoomRateReservation roomRate, RatePrice ratePrice) {
    // Start date is intended here, OPERA needs one entry for each stay date
    return Optional.ofNullable(ratePrice).map(RatePrice::getPriceStartDate)
        .orElse(LocalDate.parse(roomRate.getEnd()));
  }

  default boolean toFixedRateForDto(RatePrice ratePrice) {
    return ratePrice != null;
  }
}
