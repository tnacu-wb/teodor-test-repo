package uk.co.whitbread.domain.logic;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsRequest;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIds;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityResultV2;
import uk.co.whitbread.domain.model.availability.out.PriceInfo;
import uk.co.whitbread.domain.model.availability.out.Room;
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.domain.ports.secondary.RulesAgentOutPort;

@Slf4j
@UtilityClass
public class OccupancySupplementDistributionUtils {

  public static void applyOccupancySupplement(
      HotelAvailabilityByIdsV2Request v2Request,
      HotelAvailabilityByIdsV2 hotelAvailabilityByIdsV2,
      UnleashWrapper<FeatureFlag> unleashWrapper,
      RulesAgentOutPort rulesAgentOutPort,
      boolean isPriceFromOpera,
      boolean shouldMultiplyForNoNights) {

    var noOfExtraAdults = v2Request.getRooms().stream()
        .filter(room -> Objects.nonNull(room.getAdults()) && room.getAdults() > 1)
        .count();
    var hotelIds = hotelAvailabilityByIdsV2.getHotelAvailability().stream()
        .map(HotelAvailabilityResultV2::getHotelId)
        .toList();

    boolean isOTA = Boolean.TRUE.equals(v2Request.getIsOTA());
    if ((noOfExtraAdults > 0 || isOTA)
            && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getOccupancySupplement())) {
      var occSupplement = rulesAgentOutPort.getMultiOccupancySupplementPricing(hotelIds);

      hotelAvailabilityByIdsV2.getHotelAvailability()
          .forEach(hotel -> hotel.getRoomStays()
              .forEach(roomStay -> roomStay.getRoomTypes()
                  .stream().filter(roomType -> shouldApplySupplementToRoomPrice(isPriceFromOpera, isOTA,
                              roomType.getAdults() == null ? null
                                      : Integer.parseInt(roomType.getAdults())))
                  .forEach(roomType -> roomType.getRoomRates()
                      .forEach(roomRate -> roomRate.getRoomRateInfo().getPriceInfo()
                          .forEach(priceInfo ->
                                  addOccupancySupplement(priceInfo, shouldMultiplyForNoNights,
                                          occSupplement, hotel.getHotelId(), roomType.getNumberOfRooms()))))));
    }
  }

  public static void applyOccupancySupplement(HotelAvailabilityByIdsRequest hotelAvailabilityByIdsRequest,
      HotelAvailabilityByIds hotelAvailabilityByIds, UnleashWrapper<FeatureFlag> unleashWrapper,
      RulesAgentOutPort rulesAgentOutPort, boolean isPriceFromOpera) {

    var isOTA = hotelAvailabilityByIdsRequest.getIsOTA() != null
        && Boolean.TRUE.equals(hotelAvailabilityByIdsRequest.getIsOTA());

    var noOfExtraAdults = hotelAvailabilityByIdsRequest.getAdultsNumber().stream()
        .filter(adults -> adults.intValue() > 1)
        .count();

    if ((isOTA || noOfExtraAdults > 0)
        && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getOccupancySupplement())) {

      var noOfNights = BigDecimal.valueOf(getNumberOfNights(hotelAvailabilityByIdsRequest.getArrivalDate(),
          hotelAvailabilityByIdsRequest.getDepartureDate()));

      var occSupplement = rulesAgentOutPort
          .getMultiOccupancySupplementPricing(hotelAvailabilityByIdsRequest.getHotelIds());

      hotelAvailabilityByIds.getHotelAvailability()
          .forEach(hotel -> hotel.getRoomRates()
              .forEach(roomRate -> roomRate.getRoomTypes()
                  .stream().filter(roomType ->
                              shouldApplySupplementToRoomPrice(isPriceFromOpera, isOTA, roomType.getAdults()))
                  .forEach(roomType -> roomType.getRooms().stream()
                      .filter(room -> Objects.nonNull(room.getRoomPriceBreakdown())
                          && Objects.nonNull(room.getRoomPriceBreakdown().getTotalNetAmount()))
                      .map(Room::getRoomPriceBreakdown)
                      .forEach(priceBreakdown -> {
                        priceBreakdown
                            .setTotalNetAmount(priceBreakdown.getTotalNetAmount()
                                .add(occSupplement.get(hotel.getHotelId()).multiply(noOfNights)));
                        priceBreakdown.getDailyPrices()
                            .stream().filter(dailyPrice -> Objects.nonNull(dailyPrice.getNetPrice()))
                            .forEach(dailyPrice -> dailyPrice
                                .setNetPrice(dailyPrice.getNetPrice().add(occSupplement.get(hotel.getHotelId()))));
                      }))));
    }
  }

  private static boolean shouldApplySupplementToRoomPrice(boolean isPriceFromOpera, boolean isOTA,
      Integer adultsNumber) {
    return (isOTA && !isPriceFromOpera)
        || (isOTA && isPriceFromOpera
        && Objects.nonNull(adultsNumber) && adultsNumber == 1)
        || (!isOTA && !isPriceFromOpera
        && Objects.nonNull(adultsNumber) && adultsNumber > 1);
  }

  public static int getNumberOfNights(String arrivalDate, String departureDate) {
    return (int) Duration.between(
        LocalDate.parse(arrivalDate).atStartOfDay(ZoneOffset.UTC),
        LocalDate.parse(departureDate).atStartOfDay(ZoneOffset.UTC)).toDays();
  }

  private static void addOccupancySupplement(PriceInfo  priceInfo, boolean shouldMultiplyOccupancySupplement,
                                             Map<String, BigDecimal> occSupplement, String hotelId,
                                             String noOfRooms) {
    var supplement = shouldMultiplyOccupancySupplement && noOfRooms != null
        ? occSupplement.get(hotelId).multiply(BigDecimal.valueOf(Integer.parseInt(noOfRooms)))
        : occSupplement.get(hotelId);
    if (priceInfo.getAmountBeforeTax() != null) {
      priceInfo.setAmountBeforeTax(priceInfo.getAmountBeforeTax().add(supplement));
    }
    if (priceInfo.getAmountAfterTax() != null) {
      priceInfo.setAmountAfterTax(priceInfo.getAmountAfterTax().add(supplement));
    }
  }
}
