package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static java.util.Optional.ofNullable;
import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil.shouldApplyCityTax;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil.shouldCalculateCityTax;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.HotelAvailabilitiesUtil.buildHotelFromSearchCriteria;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.HotelAvailabilitiesUtil.createRoom;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Room;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelAvailabilitiesPersistencePostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelRatesPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil;


@Slf4j
@AllArgsConstructor
public class HotelAvailabilitiesPostProcessorService implements HotelAvailabilitiesPersistencePostProcessorPort {

  private static final String PMS_SOURCE_OPERA = "OPERA";
  private final LosRestrictionPort losRestrictionPort;
  private final HotelRatesPostProcessorPort hotelRatesPostProcessorPort;
  private final CityTaxFeatureUtil cityTaxFeatureUtil;
  @Value("${low.inventory.threshold}")
  private int lowInventoryThreshold = 10;
  @Value("${enable.citytax.currency}")
  private boolean isCitytaxCurrencySupported = true;

  @Override
  public List<Hotel> performPostProcessHotelAvailabilities(final SearchCriteria searchCriteria,
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet) {
    final List<Hotel> hotels = processHotelResultSetToHotel(searchCriteria, hotelAvailabilitiesResultSet);
    log.debug("hotels size - {} ,before applying Los Restrictions", hotels.size());
    if (hotels.isEmpty()) {
      return Collections.emptyList();
    }

    return losRestrictionPort.applyLosRestrictions(searchCriteria, hotels);
  }

  @Override
  public List<Hotel> processHotelResultSetToHotel(final SearchCriteria searchCriteria,
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet) {

    log.debug("Mapping HotelResultSet to hotel for the searchCriteria: {}", sanitize(searchCriteria));
    if (CollectionUtils.isEmpty(hotelAvailabilitiesResultSet)) {
      return Collections.emptyList();
    }

    final Set<String> hotelCodesRetrievedFromDB =
        hotelAvailabilitiesResultSet.stream()
            .map(HotelAvailabilitiesResultSet::getHotelCode)
            .collect(Collectors.toUnmodifiableSet());
    final Set<String> hotelsFromCriteria =
        searchCriteria.getHotelCodes().stream().collect(Collectors.toSet());

    final Set<String> hotelCodesNotPresentInDB = new HashSet<>();
    for (String hotelCodesFromCriteria : hotelsFromCriteria) {
      if (!hotelCodesRetrievedFromDB.contains(hotelCodesFromCriteria)) {
        hotelCodesNotPresentInDB.add(hotelCodesFromCriteria);
      }
    }
    final List<Hotel> hotelsWithRates = new ArrayList<>();

    for (String hotelCode : hotelCodesRetrievedFromDB) {
      log.trace("Processing HotelResultSet for the hotel with hotelCode: {}", hotelCode);
      Hotel hotel = buildHotelFromSearchCriteria(hotelCode);

      /* Pick up hotelsResultSet list for the hotelCode picked for processing */
      List<HotelAvailabilitiesResultSet> hotelResultSetsForHotel =
          hotelAvailabilitiesResultSet.stream()
              .filter(hotelAvailabilityResultSet ->
                  hotelAvailabilityResultSet.getHotelCode().equals(hotelCode))
              .collect(Collectors.toList());

      final List<RatePlan> ratePlans = mapRateFromResultSetToRatePlan(
          hotelCode, searchCriteria, hotelResultSetsForHotel);
      hotel.setRates(ratePlans);
      hotelsWithRates.add(hotel);

    }
    for (String hotelCode : hotelCodesNotPresentInDB) {
      log.debug("hotel - {} is not available in DB, so add an empty ratePlan", sanitize(hotelCode));
      Hotel hotel = buildHotelFromSearchCriteria(hotelCode);
      hotel.setRates(Collections.emptyList());
      hotelsWithRates.add(hotel);
    }
    return populateLimitedAvailabilityAndEuroCurrency(hotelsWithRates, hotelAvailabilitiesResultSet);
  }

  @Override
  public List<RatePlan> mapRateFromResultSetToRatePlan(final String hotelCode,
      final SearchCriteria searchCriteria,
      final List<HotelAvailabilitiesResultSet> hotelResultSetList) {

    log.debug("Mapping HotelAvailabilities ResultSet to ratePlan for hotelCode: {}, hotelResultSet: {}",
        hotelCode, hotelResultSetList.toString());

    if (hotelResultSetList.isEmpty()) {
      return Collections.emptyList();
    }

    final List<RatePlan> ratePlans = new ArrayList<>();

    Set<String> uniqueRates = hotelResultSetList.stream()
        .map(HotelAvailabilitiesResultSet::getRateClassification)
        .collect(Collectors.toUnmodifiableSet());
    /* If there are no Rates fetched from DB, then return empty RatePlans*/
    if (uniqueRates.isEmpty()) {
      return Collections.emptyList();
    }

    Map<String, List<HotelAvailabilitiesResultSet>> hotelResultSetMapForRates =
        hotelResultSetList.stream().collect(Collectors.groupingBy(HotelAvailabilitiesResultSet::getRateClassification));

    for (Map.Entry<String, List<HotelAvailabilitiesResultSet>> entry : hotelResultSetMapForRates.entrySet()) {
      final List<HotelAvailabilitiesResultSet> hotelResultSetWithRates = entry.getValue();
      final String rateClassification = entry.getKey();
      if (!hotelRatesPostProcessorPort.isAllDaysRatesReturned(searchCriteria, hotelResultSetWithRates,
          rateClassification, hotelCode)) {
        log.debug("Rates are not returned from DB for all days, for rateClassification - {} and hotel - {} "
            + ",so not adding rate to hotel", rateClassification, hotelCode);
      } else {
        final List<Room> roomsList = mapRoomFromResultSetToRoom(hotelCode, searchCriteria, hotelResultSetWithRates);
        if (!roomsList.isEmpty()) {
          final RatePlan ratePlan = processRoomsListDataToRatePlan(roomsList, hotelResultSetWithRates,
              rateClassification, searchCriteria, hotelCode);
          ratePlan.setRooms(roomsList);
          ratePlans.add(ratePlan);
        }
      }
    }
    return ratePlans;
  }

  private RatePlan processRoomsListDataToRatePlan(final List<Room> roomsList,
      final List<HotelAvailabilitiesResultSet> hotelResultSetWithRates,
      final String rateClassification,
      final SearchCriteria searchCriteria, final String hotelCode) {
    final var ratePlan = new RatePlan();
    ratePlan.setClassification(rateClassification);
    //Mapping of minimum and maximum nights based on arrival date
    final Optional<HotelAvailabilitiesResultSet> hotelRateOpt = hotelResultSetWithRates
        .stream()
        .filter(rate -> rate.getRateClassification().equalsIgnoreCase(rateClassification)
            && rate.getAvailableDate().isEqual(LocalDate.parse(searchCriteria.getArrival())))
        .findFirst();
    String currencyValue = "GBP";
    if (hotelRateOpt.isPresent()) {
      final HotelAvailabilitiesResultSet hotelRate = hotelRateOpt.get();
      ratePlan.setMinNights(hotelRate.getMinNights());
      currencyValue = hotelRate.getCurrency().equals("E") ? "EUR" : "GBP";
      if (hotelRate.getMaxNights() != null) {
        ratePlan.setMaxNights(hotelRate.getMaxNights());
      } else {
        ratePlan.setMaxNights(0);
      }

    }
    BigDecimal totalPriceRates = roomsList.stream()
        .map(Room::getTotalPrice).map(Price::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    ratePlan.setTotalPrice(new Price(totalPriceRates, currencyValue));

    log.debug("totalPriceOfRates for the hotelCode: {} RatePlan: {} TotalPrice: {}",
        hotelCode, rateClassification, totalPriceRates);

    return ratePlan;
  }

  @Override
  public List<Room> mapRoomFromResultSetToRoom(final String hotelCode, final SearchCriteria searchCriteria,
      final List<HotelAvailabilitiesResultSet> hotelResultSetList) {
    log.debug("Mapping HotelResultSet to room for hotelCode: {}, hotelResultSet: {}",
        hotelCode, hotelResultSetList.toString());
    if (hotelResultSetList.isEmpty()) {
      return Collections.emptyList();
    }

    final List<Room> roomList = new ArrayList<>();

    Set<String> roomTypeFromDB = hotelResultSetList.stream()
        .map(HotelAvailabilitiesResultSet::getRoomType)
        .collect(Collectors.toUnmodifiableSet());

    if (roomTypeFromDB.size() != Arrays.stream(searchCriteria.getType()).distinct().count()) {
      log.debug("DB has not fetched all the room types needed, so hotel : {} not picked for processing.", hotelCode);
      return Collections.emptyList();
    }

    //Map to hold the sum of totalPrice for all the days for Each roomType
    Map<String, BigDecimal> totalRoomPrice = hotelResultSetList.stream().collect(
        Collectors.groupingBy(HotelAvailabilitiesResultSet::getRoomType,
            Collectors.reducing(BigDecimal.ZERO,
                HotelAvailabilitiesResultSet::getAmount,
                BigDecimal::add)));

    Map<String, BigDecimal> totalRoomPriceWithCityTax = hotelResultSetList.stream()
        .filter(resultSet -> resultSet.getAmountWithCityTax() != null
            && resultSet.getAmountWithCityTax().compareTo(BigDecimal.ZERO) > 0)
        .collect(Collectors.groupingBy(HotelAvailabilitiesResultSet::getRoomType,
            Collectors.reducing(BigDecimal.ZERO,
                HotelAvailabilitiesResultSet::getAmountWithCityTax,
                BigDecimal::add)));

    Map<String, Long> roomTypeMapFromSearchCriteria = Arrays.stream(searchCriteria.getType())
        .collect(Collectors.groupingBy(e -> e, Collectors.counting()));
    final String currency = hotelResultSetList.get(0).getCurrency().equals("E") ? "EUR" : "GBP";

    for (int i = 0; i < searchCriteria.getRooms(); i++) {

      String roomType = searchCriteria.getType()[i];
      Long roomTypeCountFromSearchCriteria = roomTypeMapFromSearchCriteria.get(roomType);

      if (roomTypeCountFromSearchCriteria > 1 && isQuantityLessThanRoomsRequested(hotelResultSetList,
          roomTypeCountFromSearchCriteria, roomType)) {
        //return empty List as the quantity Check failed
        return Collections.emptyList();
      }
      final var room = createRoom(roomType, searchCriteria.getChildren()[i], searchCriteria.getAdults()[i]);
      //Set the TotalPrice Value  for the Room
      Optional.of(totalRoomPrice)
          .flatMap(map -> ofNullable(map.get(roomType)))
          .ifPresent(roomPrice -> {
            var dbPriceWithCityTax = totalRoomPriceWithCityTax.getOrDefault(roomType, BigDecimal.ZERO);
            setRoomPrice(room, roomPrice, dbPriceWithCityTax, currency, hotelCode, searchCriteria);
          });
      roomList.add(room);
    }
    return roomList;
  }

  private void setRoomPrice(Room room, BigDecimal roomPrice, BigDecimal dbPriceWithCityTax,
      String currency, String hotelCode, SearchCriteria searchCriteria) {
    room.setTotalPrice(new Price(roomPrice, currency));
    if (shouldApplyCityTax(cityTaxFeatureUtil.isFeatureEnabled(), hotelCode, searchCriteria.getHotelsCityTaxInfo(),
        LocalDate.parse(searchCriteria.getArrival()))) {
      if (shouldCalculateCityTax(cityTaxFeatureUtil.isFallbackEnabled(), dbPriceWithCityTax)) {
        var hotelCityTaxInfo = searchCriteria.getHotelsCityTaxInfo().getHotelsCityTaxes().get(hotelCode);
        BigDecimal amountWithCityTax = CityTaxUtil.getAmountWithCityTax(hotelCityTaxInfo, hotelCode, roomPrice,
            getNumberOfNights(searchCriteria), 1);
        room.setTotalPrice(new Price(amountWithCityTax, currency));
      } else {
        room.setTotalPrice(new Price(dbPriceWithCityTax, currency));
      }
    }
  }

  private static int getNumberOfNights(SearchCriteria searchCriteria) {
    return (int) ChronoUnit.DAYS.between(LocalDate.parse(searchCriteria.getArrival()),
        LocalDate.parse(searchCriteria.getDeparture()));
  }

  @Override
  public boolean isQuantityLessThanRoomsRequested(List<HotelAvailabilitiesResultSet> hotelAvailList,
      Long roomTypeCountFromSearchCriteria,
      String roomType) {
    log.debug("Processing isQuantityLessThanRoomsRequested");
    List<HotelAvailabilitiesResultSet> hotelAvailForQuantityCheck =
        hotelAvailList.stream()
            .filter(resultSet -> resultSet.getRoomType().equals(roomType)).toList();
    for (HotelAvailabilitiesResultSet hotelAvail : hotelAvailForQuantityCheck) {
      if (hotelAvail.getQuantity() < roomTypeCountFromSearchCriteria) {
        log.trace(
            "For Hotel - {} with RoomType - {}, Quantity is less than the multi Rooms selected, Quantity - {} for "
                + "Date - {}",
            sanitize(hotelAvail.getHotelCode()), sanitize(roomType), sanitize(hotelAvail.getQuantity()),
            sanitize(hotelAvail.getAvailableDate()));
        return true;
      }
    }
    return false;
  }

  public List<Hotel> populateLimitedAvailabilityAndEuroCurrency(final List<Hotel> hotels,
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet) {
    for (Hotel hotel : hotels) {
      if (hotel.getRates().isEmpty()) {
        log.trace("For the hotel - {}, rate is Empty.So setting limitedAvailability to true & availability to False",
            hotel.getHotelCode());
        hotel.setAvailable(false);
        hotel.setLimitedAvailability(true);
      }
      List<HotelAvailabilitiesResultSet> hotelAvailRSForHotel = hotelAvailabilitiesResultSet.stream()
          .filter(hotelRs -> hotelRs.getHotelCode().equals(hotel.getHotelCode()))
          .collect(Collectors.toList());

      if (!isCitytaxCurrencySupported) {
        Optional<HotelAvailabilitiesResultSet> hotelAvailRsForCurrency = hotelAvailRSForHotel.stream()
            .findFirst();
        if (!hotelAvailRsForCurrency.isEmpty()) {
          final Boolean euroCurrencyFlag = hotelAvailRsForCurrency.get().getCurrency()
              .equals("E");
          log.debug("euroCurrencyFlag - {} and for Hotel - {}", euroCurrencyFlag,
              hotel.getHotelCode());
          hotel.setEuroCurrencyHotel(euroCurrencyFlag);
        }
      }

      for (HotelAvailabilitiesResultSet hotelAvailResultSet : hotelAvailRSForHotel) {
        if (hotelAvailResultSet.getQuantity() < lowInventoryThreshold
            && Boolean.FALSE.equals(hotel.getLimitedAvailability())) {
          log.debug("For the hotel - {} with RoomType - {}, quantity - {} is less than the limitedAvail - {} ",
              hotelAvailResultSet.getHotelCode(), hotelAvailResultSet.getRoomType(), hotelAvailResultSet.getQuantity(),
              lowInventoryThreshold);
          hotel.setLimitedAvailability(true);
          break;
        }
      }
    }
    return hotels;
  }

  public List<Hotel> populateLimitedAvailability(final List<Hotel> hotels,
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet) {
    for (Hotel hotel : hotels) {
      if (hotel.getRates().isEmpty()) {
        log.trace("For the hotel - {}, rate is Empty.So setting limitedAvailability to true & availability to False",
            hotel.getHotelCode());
        hotel.setAvailable(false);
        hotel.setLimitedAvailability(true);
      }
      List<HotelAvailabilitiesResultSet> hotelAvailRSForHotel = hotelAvailabilitiesResultSet.stream()
          .filter(hotelRs -> hotelRs.getHotelCode().equals(hotel.getHotelCode()))
          .collect(Collectors.toList());

      for (HotelAvailabilitiesResultSet hotelAvailResultSet : hotelAvailRSForHotel) {
        if (StringUtils.isEmpty(hotel.getPmsSource())) {
          hotel.setPmsSource(hotelAvailResultSet.getPmsSource());
        }
        if (!StringUtils.equals(hotel.getPmsSource(), PMS_SOURCE_OPERA)
            && hotelAvailResultSet.getQuantity() < lowInventoryThreshold
            && Boolean.FALSE.equals(hotel.getLimitedAvailability())) {
          log.debug("For the hotel - {} with RoomType - {}, quantity - {} is less than the limitedAvail - {} ",
              hotelAvailResultSet.getHotelCode(), hotelAvailResultSet.getRoomType(),
              hotelAvailResultSet.getQuantity(), lowInventoryThreshold);
          hotel.setLimitedAvailability(true);
          break;
        }
      }

      //For Op hotel set limited avbl. against room, need this flag in hotel entity service to find last few rooms
      updateRoomAvblFlagForOpera(hotel);
    }
    return hotels;
  }

  private void updateRoomAvblFlagForOpera(Hotel hotel) {

    try {
      Predicate<Room> isRoomLessThenThreshold = room -> room.getQuantityAvailable() < lowInventoryThreshold;

      Stream.of(hotel).filter(hotelExtract -> StringUtils.equals(hotelExtract.getPmsSource(),
              PMS_SOURCE_OPERA))
          .map(Hotel::getRates)
          .filter(CollectionUtils::isNotEmpty).flatMap(Collection::stream)
          .map(RatePlan::getRooms).flatMap(Collection::stream).filter(isRoomLessThenThreshold)
          .forEach(room -> {
            room.setLimitedAvailability(Boolean.TRUE);
            log.debug("For the Opera hotel - {} with RoomType - {}, quantity - {} is less than the limitedAvail - {} ",
                hotel.getHotelCode(), room.getType(), room.getQuantityAvailable(), lowInventoryThreshold);
          });
    } catch (Exception ex) {
      log.error("Exception thrown while check for room limited Availability, for hotel {} ", hotel.getHotelCode(),
          ex.getMessage());
    }
  }

}
