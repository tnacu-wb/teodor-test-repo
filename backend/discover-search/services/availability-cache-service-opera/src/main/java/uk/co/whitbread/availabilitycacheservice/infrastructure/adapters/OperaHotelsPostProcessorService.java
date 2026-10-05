package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Optional.ofNullable;
import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil.shouldApplyCityTax;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil.shouldCalculateCityTax;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.HotelAvailabilitiesUtil.buildHotelFromSearchCriteria;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.HotelAvailabilitiesUtil.createRoom;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Room;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelAvailabilitiesPersistencePostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.OperaHotelsPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaHotelsSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil;

@Slf4j
@AllArgsConstructor
public class OperaHotelsPostProcessorService implements OperaHotelsPostProcessorPort {

  private static final int CLOSED_LOS_RESTRICTION = 666;
  private final LosRestrictionPort losRestrictionPort;
  private final HotelAvailabilitiesPersistencePostProcessorPort hotelAvailabilitiesPostProcessorPort;
  private final CityTaxFeatureUtil cityTaxFeatureUtil;
  private final ContentClientLookUpService contentClientLookUpService;

  @Override
  public List<Hotel> performOperaHotelsPostProcess(
      final OperaHotelsSearchCriteria operaHotelsSearchCriteria,
      final SearchCriteria searchCriteria,
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet) {

    log.debug("Postprocessing Hotel Availabilities Result Set - {}", hotelAvailabilitiesResultSet);

    final List<Hotel> hotels = processHotelResultSetToHotel(operaHotelsSearchCriteria,
        searchCriteria, hotelAvailabilitiesResultSet);

    if (hotels.isEmpty()) {
      return Collections.emptyList();
    }

    final List<Hotel> hotelsWithLimitedAvail = hotelAvailabilitiesPostProcessorPort
        .populateLimitedAvailability(hotels, hotelAvailabilitiesResultSet);

    log.debug("hotels size - {} ,before applying Los Restrictions", hotelsWithLimitedAvail.size());
    //Add opera hotelsCodes to searchCriteria before calling the LOS Restrictions
    searchCriteria.setHotelCodes(operaHotelsSearchCriteria.getHotelCodes());
    return losRestrictionPort.applyLosRestrictions(searchCriteria, hotelsWithLimitedAvail);
  }

  @Override
  public List<Hotel> processHotelResultSetToHotel(
      final OperaHotelsSearchCriteria operaHotelsSearchCriteria,
      final SearchCriteria searchCriteria,
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet) {

    log.debug("Mapping HotelResultSet to hotel for the searchCriteria: {}",
        operaHotelsSearchCriteria);
    if (CollectionUtils.isEmpty(hotelAvailabilitiesResultSet)) {
      return Collections.emptyList();
    }

    final Set<String> hotelCodesRetrievedFromDB =
        hotelAvailabilitiesResultSet.stream()
            .map(HotelAvailabilitiesResultSet::getHotelCode)
            .collect(Collectors.toUnmodifiableSet());

    final Set<String> hotelsFromOperaCriteria =
        operaHotelsSearchCriteria.getHotelCodes().stream().collect(Collectors.toSet());

    final Set<String> hotelCodesNotPresentInDB = new HashSet<>();

    for (String hotelCodesFromCriteria : hotelsFromOperaCriteria) {
      if (!hotelCodesRetrievedFromDB.contains(hotelCodesFromCriteria)) {
        hotelCodesNotPresentInDB.add(hotelCodesFromCriteria);
      }
    }
    final List<Hotel> hotelsWithRates = processOperaHotelsFromResultSet(
        hotelCodesRetrievedFromDB,
        operaHotelsSearchCriteria, searchCriteria, hotelAvailabilitiesResultSet);

    for (String hotelCode : hotelCodesNotPresentInDB) {
      log.debug("hotel - {} is not available in DB, so add an empty ratePlan", sanitize(hotelCode));
      Hotel hotel = buildHotelFromSearchCriteria(hotelCode);
      hotel.setRates(Collections.emptyList());
      hotelsWithRates.add(hotel);
    }
    return hotelsWithRates;
  }

  private List<Hotel> processOperaHotelsFromResultSet(
      final Set<String> hotelCodesRetrievedFromDB,
      final OperaHotelsSearchCriteria operaHotelsSearchCriteria,
      final SearchCriteria searchCriteria,
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet) {

    HotelsCityTaxInfo hotelsCityTaxInfo = null;
    if (cityTaxFeatureUtil.isFeatureEnabled()) {
      hotelsCityTaxInfo =
          contentClientLookUpService.getHotelsCityTaxInfo(operaHotelsSearchCriteria.getCountry(),
              operaHotelsSearchCriteria.getLanguage(), new ArrayList<>(hotelCodesRetrievedFromDB));
      searchCriteria.setHotelsCityTaxInfo(hotelsCityTaxInfo);
    }

    final List<Hotel> hotelsWithRates = new ArrayList<>();
    for (String hotelCode : hotelCodesRetrievedFromDB) {
      log.debug("Processing HotelResultSet for the hotel with hotelCode: {}", hotelCode);
      Hotel hotel = buildHotelFromSearchCriteria(hotelCode);

      /* Pick up hotelsResultSet list for the hotelCode picked for processing */
      final List<HotelAvailabilitiesResultSet> hotelResultSetsForHotel =
          hotelAvailabilitiesResultSet.stream()
              .filter(hotelAvailabilityResultSet ->
                  hotelAvailabilityResultSet.getHotelCode().equals(hotelCode))
              .collect(Collectors.toList());
      log.debug(
          "Opera hotelCode - {}, so perform processing through HotelAvailabilitiesPostProcessorService {} ",
          hotelCode, operaHotelsSearchCriteria);
      final List<RatePlan> ratePlans = mapRatesToRatePlanForOperaHotels(hotelCode, operaHotelsSearchCriteria,
          hotelResultSetsForHotel, hotelsCityTaxInfo);
      hotel.setRates(ratePlans);
      hotelsWithRates.add(hotel);
    }
    return hotelsWithRates;
  }

  private List<RatePlan> mapRatesToRatePlanForOperaHotels(final String hotelCode,
      final OperaHotelsSearchCriteria operaHotelsSearchCriteria,
      final List<HotelAvailabilitiesResultSet> hotelResultSetList, HotelsCityTaxInfo hotelsCityTaxInfo) {

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
        hotelResultSetList.stream()
            .collect(Collectors.groupingBy(HotelAvailabilitiesResultSet::getRateClassification));

    for (Entry<String, List<HotelAvailabilitiesResultSet>> entry : hotelResultSetMapForRates
        .entrySet()) {
      final List<HotelAvailabilitiesResultSet> hotelResultSetWithRates = entry.getValue();
      final String rateClassification = entry.getKey();

      final List<Room> roomsList = processRoomsForOperaHotels(hotelCode,
          operaHotelsSearchCriteria, hotelResultSetWithRates, rateClassification, hotelsCityTaxInfo);
      if (!roomsList.isEmpty()) {
        if (isAtleastOneRoomPresentForEachRoomType(roomsList, operaHotelsSearchCriteria)
            && !isClosedRestrictionAppliedOnRates(hotelResultSetWithRates)) {
          final var ratePlan = populateRatesWithMinMaxNightsAndClassification(rateClassification,
              hotelResultSetWithRates, operaHotelsSearchCriteria.getArrival());
          ratePlan.setRooms(roomsList);
          ratePlans.add(ratePlan);
        } else {
          log.debug(
              "For Hotel - {} , with Classification - {} ,at least one room present in roomList for each RoomTypes "
                  + "on search criteria failed or "
                  + "Closed LOS Restriction Applied, so don't add the rates", hotelCode, rateClassification);
        }
      }
    }
    return ratePlans;
  }

  private RatePlan populateRatesWithMinMaxNightsAndClassification(final String rateClassification,
      final List<HotelAvailabilitiesResultSet> hotelResultSetWithRates,
      String arrival) {
    final var ratePlan = new RatePlan();
    ratePlan.setClassification(rateClassification);
    //Mapping of minimum and maximum nights based on arrival date
    final Optional<HotelAvailabilitiesResultSet> hotelRateOpt = hotelResultSetWithRates
        .stream()
        .filter(rate -> rate.getRateClassification().equalsIgnoreCase(rateClassification)
            && rate.getAvailableDate().isEqual(LocalDate.parse(arrival)))
        .findFirst();
    if (hotelRateOpt.isPresent()) {
      final HotelAvailabilitiesResultSet hotelRate = hotelRateOpt.get();
      //setting the rate code from DB for Opera hotels as content svc call for opera hotels is not required.
      ratePlan.setCode(hotelRate.getRateCode());
      ratePlan.setMinNights(hotelRate.getMinNights());
      if (hotelRate.getMaxNights() != null) {
        ratePlan.setMaxNights(hotelRate.getMaxNights());
      } else {
        ratePlan.setMaxNights(0);
      }
    }
    return ratePlan;
  }


  private List<Room> processRoomsForOperaHotels(final String hotelCode,
      final OperaHotelsSearchCriteria operaHotelsSearchCriteria,
      final List<HotelAvailabilitiesResultSet> hotelResultSetList,
      final String rateClassification, HotelsCityTaxInfo hotelsCityTaxInfo) {

    log.debug("Mapping HotelResultSet to room for hotelCode: {}, hotelResultSet: {} and rateClassification - {} ",
        hotelCode, hotelResultSetList.toString(), rateClassification);

    if (hotelResultSetList.isEmpty()) {
      return Collections.emptyList();
    }

    final long daysBetween = DAYS.between(LocalDate.parse(operaHotelsSearchCriteria.getArrival()),
        LocalDate.parse(operaHotelsSearchCriteria.getDeparture()));

    //Map to hold the sum of totalPrice for all the days for Each roomType
    Map<String, BigDecimal> totalRoomPrice = hotelResultSetList.stream().collect(
        Collectors.groupingBy(HotelAvailabilitiesResultSet::getRoomType,
            Collectors.reducing(BigDecimal.ZERO,
                getAmountWithCityTaxIfNeeded(hotelsCityTaxInfo,
                    LocalDate.parse(operaHotelsSearchCriteria.getArrival())),
                BigDecimal::add)));

    Map<String, Long> roomsCounterFromDbMap = hotelResultSetList.stream().collect(
        Collectors.groupingBy(HotelAvailabilitiesResultSet::getRoomType,
            Collectors.counting()));

    //retrieve quantity available in DB for each room type
    Map<String, Integer> roomsQtyAvailable = hotelResultSetList.stream().collect(
        Collectors.groupingBy(HotelAvailabilitiesResultSet::getRoomType,
            Collectors.reducing(Integer.MAX_VALUE, HotelAvailabilitiesResultSet::getQuantity, Math::min)));

    Set<String> roomsFetchedFromDb = roomsCounterFromDbMap.keySet();

    final String currency = hotelResultSetList.get(0).getCurrency().equals("E") ? "EUR" : "GBP";

    //Room availability check based on house level check
    Map<LocalDate, Integer> roomsQtyForHouseLevel = hotelResultSetList.stream()
        .filter(hotelResult
            -> StringUtils.equalsIgnoreCase(hotelCode, hotelResult.getHotelCode()))
        .collect(Collectors.groupingBy(HotelAvailabilitiesResultSet::getAvailableDate,
            Collectors.summingInt(HotelAvailabilitiesResultSet::getQuantity)));

    log.info("House level availability for hotelCode: {}, AvailableDate & Quantity: {}", hotelCode,
        roomsQtyForHouseLevel);

    boolean houseLevelAvailability = Optional.of(roomsQtyForHouseLevel).orElse(Collections.emptyMap())
        .entrySet().stream()
        .allMatch(qty -> qty.getValue() > 0);

    final List<Room> roomList = new ArrayList<>();
    if (houseLevelAvailability) {
      roomList.addAll(processRooms(operaHotelsSearchCriteria, currency, roomsFetchedFromDb, roomsQtyAvailable,
          totalRoomPrice, roomsCounterFromDbMap, daysBetween));
    }
    log.info(
        "Processing completed to room for hotelCode: {}, hotelResultSet: {} and rateClassification - {} with "
            + "roomList - {}",
        hotelCode, hotelResultSetList, rateClassification, roomList);
    return roomList;
  }

  private @NotNull Function<HotelAvailabilitiesResultSet, BigDecimal> getAmountWithCityTaxIfNeeded(
      HotelsCityTaxInfo hotelsCityTaxInfo, LocalDate arrivalDate) {
    return resultSet -> {

      BigDecimal dbAmountWithCityTax = resultSet.getAmountWithCityTax();
      String hotelCode = resultSet.getHotelCode();

      if (shouldApplyCityTax(cityTaxFeatureUtil.isFeatureEnabled(), hotelCode, hotelsCityTaxInfo,
          resultSet.getAvailableDate(), arrivalDate)) {
        HotelCityTax hotelCityTax = hotelsCityTaxInfo.getHotelsCityTaxes().get(hotelCode);
        return  shouldCalculateCityTax(cityTaxFeatureUtil.isFallbackEnabled(), dbAmountWithCityTax)
            ? CityTaxUtil.getAmountWithCityTax(hotelCityTax, hotelCode, resultSet.getAmount(), 1, 1)
            : dbAmountWithCityTax;
      }

      return resultSet.getAmount() != null ? resultSet.getAmount() : BigDecimal.ZERO;
    };
  }

  private List<Room> processRooms(OperaHotelsSearchCriteria operaHotelsSearchCriteria, String currency,
                                  Set<String> roomsFetchedFromDb, Map<String, Integer> roomsQtyAvailable,
                                  Map<String, BigDecimal> totalRoomPrice, Map<String, Long> roomsCounterFromDbMap,
                                  long daysBetween) {
    final List<Room> roomList = new ArrayList<>();
    int[] roomQtyArray = operaHotelsSearchCriteria.getRoomQty();
    for (int i = 0; i < roomQtyArray.length; i++) {
      final long roomQty = roomQtyArray[i];

      final String[] roomTypesArray = operaHotelsSearchCriteria.getRoomTypes()[i];
      for (final String roomType : roomTypesArray) {
        if (roomsFetchedFromDb.contains(roomType)) {
          final long roomsCountFromDb = roomsCounterFromDbMap.get(roomType);
          log.debug("roomType - {} ,roomQty -{}  ,roomsCounterFromDB.get(roomType) - {} and daysBetween - {}",
              sanitize(roomType), sanitize(roomQty), sanitize(roomsCountFromDb), sanitize(daysBetween));
          if (daysBetween == roomsCountFromDb) {
            log.debug("Add room to the roomsList as its a validRoom, roomType - {}", sanitize(roomType));
            final var room = createRoom(roomType, operaHotelsSearchCriteria.getChildren()[i],
                operaHotelsSearchCriteria.getAdults()[i]);
            room.setQtyRequested(roomQty);
            room.setQuantityAvailable(roomsQtyAvailable.get(roomType));

            // Set the TotalPrice Value  for the Room -- make sure this processing goes fine..
            ofNullable(totalRoomPrice)
                .flatMap(map -> ofNullable(map.get(roomType)))
                .ifPresent(roomPrice -> room.setTotalPrice(new Price(roomPrice, currency)));

            roomList.add(room);
          } else {
            log.debug("Room is not present for all days,  roomType- {} ", sanitize(roomType));
          }
        } else {
          log.debug("room data not present in DB, for roomType - {}", sanitize(roomType));
        }
      }
    }
    return roomList;
  }

  public boolean isAtleastOneRoomPresentForEachRoomType(final List<Room> roomsList,
      final OperaHotelsSearchCriteria operaHotelsSearchCriteria) {
    final List<String> roomTypesAddedList =
        roomsList.stream().map(Room::getType).toList();
    final int[] roomQtyArray = operaHotelsSearchCriteria.getRoomQty();
    for (int i = 0; i < roomQtyArray.length; i++) {
      boolean roomTypePresent = false;
      final String[] roomTypesArray = operaHotelsSearchCriteria.getRoomTypes()[i];
      for (String roomType : roomTypesArray) {
        if (roomTypesAddedList.contains(roomType)) {
          roomTypePresent = true;
          break;
        }
      }
      if (!roomTypePresent) {
        log.debug("At least one roomType present in rooms List check failed for roomTypes - {}",
            Arrays.stream(roomTypesArray)
                    .map(SanitizingUtils::sanitize)
                        .toList());
        return false;
      }
    }
    return true;
  }

  private boolean isClosedRestrictionAppliedOnRates(final List<HotelAvailabilitiesResultSet> hotelResultSetWithRates) {
    for (HotelAvailabilitiesResultSet hotelRsRate : hotelResultSetWithRates) {
      if (hotelRsRate.getMinNights() == CLOSED_LOS_RESTRICTION) {
        log.info("For Hotel -{}, Closed LOS Restriction applied on RatePlan with category - {} and code - {} "
                + " with minNights - {} for the date - {} ", hotelRsRate.getHotelCode(),
            hotelRsRate.getRateClassification(),
            hotelRsRate.getRateCode(), hotelRsRate.getMinNights(), hotelRsRate.getAvailableDate());
        return true;
      }
    }
    return false;
  }
}
