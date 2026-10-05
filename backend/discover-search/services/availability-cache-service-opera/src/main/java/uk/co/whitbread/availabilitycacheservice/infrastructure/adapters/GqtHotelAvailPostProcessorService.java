package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil.shouldApplyCityTax;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil.shouldCalculateCityTax;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Availabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtSearchPayload;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Rate;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Room;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.GqtHotelAvailPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil;

@Slf4j
@AllArgsConstructor
public class GqtHotelAvailPostProcessorService implements GqtHotelAvailPostProcessorPort {

  private CityTaxFeatureUtil cityTaxFeatureUtil;

  private static GqtOperaHotelAvailabilities buildGqtHotelAvailabilities(String hotelCode) {
    return GqtOperaHotelAvailabilities.builder()
        .hotelCode(hotelCode)
        .build();
  }

  private static Rate buildRateFromResultSet(HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet,
      String rateClassification) {
    final Rate rate = Rate.builder()
        .available(hotelAvailabilitiesResultSet.isAvailability())
        .classification(rateClassification)
        .code(hotelAvailabilitiesResultSet.getRateCode())
        .build();
    String currencyValue = hotelAvailabilitiesResultSet.getCurrency().equals("E") ? "EUR" : "GBP";
    rate.setCurrency(currencyValue);

    return rate;
  }

  @Override
  public List<GqtOperaHotelAvailabilities> processHotelResultSetToGqtHotelAvail(
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet,
      final GqtSearchPayload gqtSearchPayload) {

    log.debug("Processing processHotelResultSetToGqtHotelAvail and hotelAvailabilitiesResultSet - {}",
        hotelAvailabilitiesResultSet);

    if (CollectionUtils.isEmpty(hotelAvailabilitiesResultSet)) {
      return Collections.emptyList();
    }

    final Set<String> hotelCodesRetrievedFromDB = hotelAvailabilitiesResultSet.stream()
        .map(HotelAvailabilitiesResultSet::getHotelCode)
        .collect(Collectors.toUnmodifiableSet());

    final Set<String> hotelCodesFromCriteria =
        gqtSearchPayload.getHotelCodes().stream().collect(Collectors.toSet());

    final Set<String> hotelCodesNotPresentInDB = new HashSet<>();

    for (String codeFromCriteria : hotelCodesFromCriteria) {
      if (!hotelCodesRetrievedFromDB.contains(codeFromCriteria)) {
        hotelCodesNotPresentInDB.add(codeFromCriteria);
      }
    }

    final List<GqtOperaHotelAvailabilities> gqtOperaHotelAvailabilitiesList = new ArrayList<>();

    /* Processing for the hotelCodes which are present in DB */
    for (String hotelCode : hotelCodesRetrievedFromDB) {

      log.info("Processing HotelResultSet for the hotel with hotelCode: {}", hotelCode);

      /* Room availability check based on house level check */
      Map<LocalDate, Integer> roomsQtyForHouseLevel = hotelAvailabilitiesResultSet.stream()
          .filter(hotelResult -> StringUtils.equalsIgnoreCase(hotelCode, hotelResult.getHotelCode()))
          .collect(Collectors.groupingBy(HotelAvailabilitiesResultSet::getAvailableDate,
              Collectors.summingInt(HotelAvailabilitiesResultSet::getQuantity)));

      log.info("House level availability for hotelCode: {}, AvailableDate & Quantity: {}", hotelCode,
          roomsQtyForHouseLevel);

      boolean houseLevelAvailability = Optional.of(roomsQtyForHouseLevel).orElse(Collections.emptyMap())
          .entrySet().stream()
          .allMatch(qty -> qty.getValue() > 0);

      if (houseLevelAvailability) {
        GqtOperaHotelAvailabilities gqtOperaHotelAvailabilities = new GqtOperaHotelAvailabilities();
        gqtOperaHotelAvailabilities.setHotelCode(hotelCode);

        final List<HotelAvailabilitiesResultSet> hotelResultSetsForHotel =
            hotelAvailabilitiesResultSet.stream()
                .filter(hotelAvailabilityResultSet ->
                    hotelAvailabilityResultSet.getHotelCode().equals(hotelCode))
                .collect(Collectors.toList());

        log.debug("before calling mapAvailabilitiesFromResultSetForHotel, hotelResultSetsForHotel -{}",
            hotelResultSetsForHotel);

        final Set<Availabilities> availabilities =
            getAvailabilitiesFromResultSetForGivenHotel(
                hotelCode, gqtSearchPayload, hotelResultSetsForHotel);

        gqtOperaHotelAvailabilities.setAvailabilities(availabilities);
        gqtOperaHotelAvailabilitiesList.add(gqtOperaHotelAvailabilities);
      }
    }

    /* Processing for the hotelCodes which are not present in DB or OnSale is set to False */
    for (String hotelCode : hotelCodesNotPresentInDB) {
      log.debug("hotel - {} is not available in DB, so add an empty Availabilities", sanitize(hotelCode));
      GqtOperaHotelAvailabilities gqtOperaHotelAvailabilities = buildGqtHotelAvailabilities(hotelCode);
      gqtOperaHotelAvailabilities.setAvailabilities(Collections.emptySet());
      gqtOperaHotelAvailabilitiesList.add(gqtOperaHotelAvailabilities);
    }

    return gqtOperaHotelAvailabilitiesList;
  }

  private Set<Availabilities> getAvailabilitiesFromResultSetForGivenHotel(final String hotelCode,
      final GqtSearchPayload gqtSearchPayload,
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet) {
    log.debug("Processing mapAvailabilitiesFromResultSetForHotel for the hotel - {} "
        + "and hotelAvailabilitiesResultSet - {}", hotelCode, hotelAvailabilitiesResultSet);

    if (CollectionUtils.isEmpty(hotelAvailabilitiesResultSet)) {
      log.info("hotelAvailabilitiesResultSet is empty, so returning the empty set for the hotelCode - {}", hotelCode);
      return Collections.emptySet();
    }

    final Set<LocalDate> availableDatesForHotel = hotelAvailabilitiesResultSet.stream()
        .map(HotelAvailabilitiesResultSet::getAvailableDate).collect(Collectors.toSet());
    log.trace("availableDatesForHotel - {}", availableDatesForHotel);

    List<LocalDate> arrivalDepartureDatesSet = Stream.iterate(LocalDate.parse(
                gqtSearchPayload.getArrival()),
            d -> d.isBefore(LocalDate.parse(gqtSearchPayload.getDeparture())), d -> d.plusDays(1))
        .sorted().toList();
    log.trace("arrivalDepartureDatesSet - {}", arrivalDepartureDatesSet);

    Set<Availabilities> availabilitiesSet = new HashSet<>();
    Map<LocalDate, List<HotelAvailabilitiesResultSet>> hotelResultSetMapForRates =
        hotelAvailabilitiesResultSet.stream()
            .collect(Collectors.groupingBy(HotelAvailabilitiesResultSet::getAvailableDate));
    log.trace("hotelResultSetMapForRates - {}", hotelResultSetMapForRates);

    for (LocalDate availableDate : arrivalDepartureDatesSet) {
      log.trace("availableDate - {}", availableDate);
      List<HotelAvailabilitiesResultSet> ratesForAvailableDate =
          hotelResultSetMapForRates.get(availableDate);
      log.trace("ratesForAvailableDate - {}", ratesForAvailableDate);

      Availabilities availabilities = Availabilities.builder().availableDate(availableDate).build();

      Set<Rate> availableRates = mapGqtHotelAvailResultSetToRates(ratesForAvailableDate,
          gqtSearchPayload.getHotelsCityTaxInfo(), gqtSearchPayload.getArrival());
      availabilities.setRates(availableRates);
      availabilitiesSet.add(availabilities);
    }
    return availabilitiesSet;
  }

  private Set<Rate> mapGqtHotelAvailResultSetToRates(
      final List<HotelAvailabilitiesResultSet> availDateHotelAvailResultSet,
      final HotelsCityTaxInfo hotelsCityTaxInfo, String arrival) {

    log.debug("Processing mapGqtHotelAvailResultSetToRates with availDateHotelAvailResultSet - {}",
        availDateHotelAvailResultSet);

    if (availDateHotelAvailResultSet == null || availDateHotelAvailResultSet.isEmpty()) {
      log.info("Rates are not present for a given date, returning empty Set");
      return Collections.emptySet();
    }
    Set<Rate> availableRatesSet = new HashSet<>();
    Map<String, List<HotelAvailabilitiesResultSet>> hotelResultSetMapForRates =
        availDateHotelAvailResultSet.stream()
            .filter(result -> result.getQuantity() > 0)
            .collect(Collectors.groupingBy(HotelAvailabilitiesResultSet::getRateClassification));

    for (Map.Entry<String, List<HotelAvailabilitiesResultSet>> entry :
        hotelResultSetMapForRates.entrySet()) {

      final List<HotelAvailabilitiesResultSet> hotelResultSetWithRates = entry.getValue();
      final String rateClassification = entry.getKey();

      final Optional<HotelAvailabilitiesResultSet> firstRatesFromResultSetOp =
          hotelResultSetWithRates.stream().findFirst();

      HotelAvailabilitiesResultSet firstRatesFromRs = null;
      if (firstRatesFromResultSetOp.isPresent()) {
        firstRatesFromRs = firstRatesFromResultSetOp.get();
      }

      final Set<Room> roomsList = mapRoomsFromResultSetToGqtRoom(hotelResultSetWithRates, hotelsCityTaxInfo, arrival);
      if (!roomsList.isEmpty() && firstRatesFromResultSetOp.isPresent()) {
        final Rate rate = buildRateFromResultSet(firstRatesFromRs, rateClassification);
        rate.setRooms(roomsList);
        availableRatesSet.add(rate);
      }
    }
    return availableRatesSet;
  }

  private Set<Room> mapRoomsFromResultSetToGqtRoom(
      final List<HotelAvailabilitiesResultSet> hotelResultSetWithRates,
      final HotelsCityTaxInfo hotelsCityTaxInfo, String arrival) {
    Set<Room> roomsSet = new HashSet<>();
    for (HotelAvailabilitiesResultSet hotelAvailRsForRoom : hotelResultSetWithRates) {
      int minNight = hotelAvailRsForRoom.getMinNights() == null ? 0 : hotelAvailRsForRoom.getMinNights();
      int maxNight = hotelAvailRsForRoom.getMaxNights() == null ? 0 : hotelAvailRsForRoom.getMaxNights();
      boolean isClosToArrival = minNight == 999 && maxNight == 0;

      var finalAmount = applyCityTaxIfNeeded(hotelAvailRsForRoom, hotelsCityTaxInfo, LocalDate.parse(arrival));

      final var room = new Room();
      room.setRoomType(hotelAvailRsForRoom.getRoomType());
      room.setQuantity(hotelAvailRsForRoom.getQuantity());
      room.setAmount(finalAmount);
      room.setMinNights(minNight);
      room.setMaxNights(maxNight);
      room.setCta(isClosToArrival);
      roomsSet.add(room);
    }
    return roomsSet;
  }

  private BigDecimal applyCityTaxIfNeeded(HotelAvailabilitiesResultSet hotelAvailRsForRoom,
      HotelsCityTaxInfo hotelsCityTaxInfo, LocalDate arrivalDate) {
    var hotelCode = hotelAvailRsForRoom.getHotelCode();

    if (shouldApplyCityTax(cityTaxFeatureUtil.isFeatureEnabled(), hotelCode, hotelsCityTaxInfo,
        hotelAvailRsForRoom.getAvailableDate(), arrivalDate)) {
      HotelCityTax hotelCityTax = hotelsCityTaxInfo.getHotelsCityTaxes().get(hotelCode);
      return shouldCalculateCityTax(cityTaxFeatureUtil.isFallbackEnabled(), hotelAvailRsForRoom.getAmountWithCityTax())
          ? CityTaxUtil.getAmountWithCityTax(hotelCityTax, hotelCode, hotelAvailRsForRoom.getAmount(), 1, 1)
          : hotelAvailRsForRoom.getAmountWithCityTax();
    }

    return hotelAvailRsForRoom.getAmount();
  }
}
