package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static java.time.temporal.ChronoUnit.DAYS;
import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil.shouldApplyCityTax;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil.shouldCalculateCityTax;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.HotelAvailabilitiesUtil.buildDistributedHotelFromHotelCode;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.HotelAvailabilitiesUtil.createDistributionRoom;

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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.AvailableCost;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionHotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionRatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionRoom;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.DistributionHotelsPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.DistributionHotelAvailResultWithRestrictionSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionPayload;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil;

@Slf4j
@Service
public class DistributionHotelsPostProcessorSvc implements DistributionHotelsPostProcessorPort {

  private final LosRestrictionPort<DistributionHotel> losRestrictionPort;
  private final CityTaxFeatureUtil cityTaxFeatureUtil;

  public DistributionHotelsPostProcessorSvc(
      @Qualifier("losRestrictionProcessDistributionService") LosRestrictionPort<DistributionHotel>
          losRestrictionPort,
      CityTaxFeatureUtil cityTaxFeatureUtil) {
    this.losRestrictionPort = losRestrictionPort;
    this.cityTaxFeatureUtil = cityTaxFeatureUtil;
  }

  @Override
  public List<DistributionHotel> performDistributionHotelsPostProcess(
      final DistributionPayload distributionPayload,
      final List<DistributionHotelAvailResultWithRestrictionSet> distributionHotelAvailResultSets) {
    if (log.isDebugEnabled()) {
      log.debug("Processing started for the performDistributionHotelsPostProcess with input params - "
              + "distributionPayload - {} & hotelAvailabilitiesResultSet - {}", distributionPayload,
          distributionHotelAvailResultSets);
    }
    if (distributionHotelAvailResultSets.isEmpty()) {
      log.info("no records returned from DB, so returning empty list");
      return Collections.emptyList();
    }

    final Set<String> hotelCodesFromDB =
        distributionHotelAvailResultSets.stream()
            .map(DistributionHotelAvailResultWithRestrictionSet::getHotelCode)
            .collect(Collectors.toUnmodifiableSet());

    final Set<String> hotelsFromDistributionPayload =
        new HashSet<>(distributionPayload.getHotelCodes());

    final Set<String> hotelCodesNotInDB = new HashSet<>();

    for (final String hotelCodesFromCriteria : hotelsFromDistributionPayload) {
      if (!hotelCodesFromDB.contains(hotelCodesFromCriteria)) {
        hotelCodesNotInDB.add(hotelCodesFromCriteria);
      }
    }

    final List<DistributionHotel> distributionHotels = processDistributedHotelsFromDb(
        hotelCodesFromDB, distributionPayload, distributionHotelAvailResultSets);

    final boolean arrivalDateToday = LocalDate.now()
        .isEqual(LocalDate.parse(distributionPayload.getArrival()));

    for (String hotelCode : hotelCodesNotInDB) {
      if (log.isDebugEnabled()) {
        log.debug("Hotel - {} is not available in DB, so add an empty ratePlan", sanitize(hotelCode));
      }
      DistributionHotel distributionHotel = buildDistributedHotelFromHotelCode(hotelCode);
      distributionHotel.setRates(Collections.emptyList());
      distributionHotel.setArrivalDateToday(arrivalDateToday);
      distributionHotels.add(distributionHotel);
    }

    SearchCriteria mLosCriteria = buildSearchCriteriaFromDistrPayload(distributionPayload);
    losRestrictionPort.applyLosRestrictions(mLosCriteria, distributionHotels);
    return distributionHotels;
  }

  private List<DistributionHotel> processDistributedHotelsFromDb(
      final Set<String> hotelCodesFromDB,
      final DistributionPayload distributionPayload,
      final List<DistributionHotelAvailResultWithRestrictionSet> distributionHotelAvailResultSets) {

    final List<DistributionHotel> distributionHotelListWithRates = new ArrayList<>();
    final boolean arrivalDateToday = LocalDate.now()
        .isEqual(LocalDate.parse(distributionPayload.getArrival()));
    for (String hotelCode : hotelCodesFromDB) {
      if (log.isDebugEnabled()) {
        log.debug("Processing HotelResultSet for the hotel with hotelCode: {}", hotelCode);
      }
      DistributionHotel distributionHotel = buildDistributedHotelFromHotelCode(hotelCode);

      /* Pick up hotelsResultSet list for the hotelCode picked for processing */
      final List<DistributionHotelAvailResultWithRestrictionSet> distributedHotelResultSetForHotel =
          distributionHotelAvailResultSets.stream()
              .filter(hotelAvailabilityResultSet ->
                  hotelAvailabilityResultSet.getHotelCode().equals(hotelCode))
              .toList();

      final List<DistributionRatePlan> ratePlans =
          processDistributionRatePlansFromDbForHotel(hotelCode, distributionPayload,
              distributedHotelResultSetForHotel);
      distributionHotel.setRates(ratePlans);
      distributionHotel.setAvailable(!ratePlans.isEmpty());
      distributionHotel.setArrivalDateToday(arrivalDateToday);
      distributionHotelListWithRates.add(distributionHotel);
    }

    return distributionHotelListWithRates;
  }

  private List<DistributionRatePlan> processDistributionRatePlansFromDbForHotel(
      final String hotelCode,
      final DistributionPayload distributionPayload,
      final List<DistributionHotelAvailResultWithRestrictionSet> distributedHotelResultSetForHotel) {
    if (log.isDebugEnabled()) {
      log.debug(
          "processDistributedRatePlanFromDbForHotel with input params for hotel - {} payload- {} and resultSet - {}",
          hotelCode, distributionPayload, distributedHotelResultSetForHotel);
    }

    final List<DistributionRatePlan> distributionRatePlanList = new ArrayList<>();

    final Set<String> uniqueRates = distributedHotelResultSetForHotel.stream()
        .map(DistributionHotelAvailResultWithRestrictionSet::getRateClassification)
        .collect(Collectors.toUnmodifiableSet());

    /* If there are no Rates fetched from DB, then return empty RatePlans*/
    if (uniqueRates.isEmpty()) {
      return Collections.emptyList();
    }

    Map<String, List<DistributionHotelAvailResultWithRestrictionSet>> distributedHotelResultSetMapForRates =
        distributedHotelResultSetForHotel.stream()
            .filter(result -> result.getQuantity() > 0)
            .collect(Collectors.groupingBy(DistributionHotelAvailResultWithRestrictionSet::getRateClassification));

    for (final Map.Entry<String, List<DistributionHotelAvailResultWithRestrictionSet>> entry :
        distributedHotelResultSetMapForRates.entrySet()) {
      final List<DistributionHotelAvailResultWithRestrictionSet> distributedHotelResultSetWithRates = entry
          .getValue();
      final String rateClassification = entry.getKey();

      final List<DistributionRoom> distributionRooms = processDistributionRoomsFromDb(hotelCode,
          distributionPayload, distributedHotelResultSetWithRates, rateClassification);

      if (!distributionRooms.isEmpty()) {
        final var distributedRatePlan = populateDistributionRatePlanFromResultSet(
            rateClassification, distributedHotelResultSetWithRates);
        distributedRatePlan.setRooms(distributionRooms);
        distributionRatePlanList.add(distributedRatePlan);
      }
    }

    /* Room availability check based on house level check */
    Map<LocalDate, Integer> roomsQtyForHouseLevel = distributedHotelResultSetForHotel.stream()
        .filter(hotelResult
            -> Strings.CI.equals(hotelCode, hotelResult.getHotelCode()))
        .collect(Collectors.groupingBy(DistributionHotelAvailResultWithRestrictionSet::getAvailableDate,
            Collectors.summingInt(DistributionHotelAvailResultWithRestrictionSet::getQuantity)));

    log.info("House level availability for hotelCode: {}, AvailableDate & Quantity: {}", hotelCode,
        roomsQtyForHouseLevel);

    boolean houseLevelAvailability = Optional.of(roomsQtyForHouseLevel).orElse(Collections.emptyMap())
        .entrySet().stream()
        .allMatch(qty -> qty.getValue() > 0);

    if (!houseLevelAvailability) {
      return Collections.emptyList();
    }
    return distributionRatePlanList;
  }

  private List<DistributionRoom> processDistributionRoomsFromDb(final String hotelCode,
      final DistributionPayload distributionPayload,
      final List<DistributionHotelAvailResultWithRestrictionSet> distributedHotelResultSetForRate,
      final String rateClassification) {

    if (log.isDebugEnabled()) {
      log.debug("processDistributedRoomsFromDb with input params for hotel - {} rateClassification - {} "
              + ",payload- {} resultSet - {}", hotelCode, rateClassification, distributionPayload,
          distributedHotelResultSetForRate);
    }

    if (distributedHotelResultSetForRate.isEmpty()) {
      return Collections.emptyList();
    }

    final List<DistributionRoom> distributionRoomList = new ArrayList<>();

    final long daysBetween = DAYS.between(LocalDate.parse(distributionPayload.getArrival()),
        LocalDate.parse(distributionPayload.getDeparture()));

    Map<String, Long> roomsCounterFromDbMap = distributedHotelResultSetForRate.stream().collect(
        Collectors.groupingBy(DistributionHotelAvailResultWithRestrictionSet::getRoomType,
            Collectors.counting()));

    Map<String, List<DistributionHotelAvailResultWithRestrictionSet>> distHotelResultSetMapForRooms =
        distributedHotelResultSetForRate.stream()
            .collect(Collectors.groupingBy(DistributionHotelAvailResultWithRestrictionSet::getRoomType));

    Set<String> roomsFetchedFromDb = roomsCounterFromDbMap.keySet();

    final String currency = distributedHotelResultSetForRate.getFirst().getCurrency().equals("E") ? "EUR" : "GBP";

    int[] roomQtyArray = distributionPayload.getRoomQty();
    for (int i = 0; i < roomQtyArray.length; i++) {
      final int roomQty = roomQtyArray[i];

      final String[] roomTypesArray = distributionPayload.getRoomTypes()[i];
      for (final String roomType : roomTypesArray) {
        if (roomsFetchedFromDb.contains(roomType)) {
          final long roomsCountFromDb = roomsCounterFromDbMap.get(roomType);
          if (log.isDebugEnabled()) {
            log.debug(
                "roomType - {} ,roomQty -{}  ,roomsCounterFromDB.get(roomType) - {} and daysBetween - {}",
                sanitize(roomType), sanitize(roomQty), sanitize(roomsCountFromDb), sanitize(daysBetween));
          }
          if (daysBetween == roomsCountFromDb) {
            if (log.isDebugEnabled()) {
              log.debug("Add room to the roomsList as its a validRoom, roomType - {}", sanitize(roomType));
            }
            final var distributionRoom = createDistributionRoom(roomType, roomQty, false);
            final List<DistributionHotelAvailResultWithRestrictionSet> distHotelsResultSetForRoom
                = distHotelResultSetMapForRooms.get(roomType);
            final List<AvailableCost> availableCosts = processAvailableCostsForRoom(
                distHotelsResultSetForRoom, distributionPayload, roomType, currency);
            distributionRoom.setAvailableCosts(availableCosts);
            distributionRoomList.add(distributionRoom);
          } else if (log.isDebugEnabled()) {
            log.debug("Room is not present for all days,  roomType- {} ", sanitize(roomType));
          }
        } else if (log.isDebugEnabled()) {
          log.debug("room data not present in DB, for roomType - {}", sanitize(roomType));
        }
      }
    }

    return distributionRoomList;
  }

  private List<AvailableCost> processAvailableCostsForRoom(
      final List<DistributionHotelAvailResultWithRestrictionSet> distHotelsResultSetForRoom,
      final DistributionPayload distributionPayload,
      final String roomType, final String currency) {

    if (log.isDebugEnabled()) {
      log.debug(
          "processAvailableCostsForRoom with params roomType- {} ,distHotelsResultSetForRoom - {} and payload - {}",
          sanitize(roomType), distHotelsResultSetForRoom, sanitize(distributionPayload));
    }

    final List<AvailableCost> availableCostList = new ArrayList<>();
    List<LocalDate> arrivalDepartureDatesSet = Stream.iterate(LocalDate.parse(
                distributionPayload.getArrival()),
            d -> d.isBefore(LocalDate.parse(distributionPayload.getDeparture())), d -> d.plusDays(1))
        .sorted().toList();
    log.trace("arrivalDepartureDatesSet - {}", arrivalDepartureDatesSet);

    for (LocalDate availableDate : arrivalDepartureDatesSet) {
      log.trace("availableDate - {}", availableDate);
      Optional<DistributionHotelAvailResultWithRestrictionSet> roomForAvailableDate =
          distHotelsResultSetForRoom.stream()
              .filter(distributionHotelAvailResultWithRestrictionSet ->
                  distributionHotelAvailResultWithRestrictionSet.getAvailableDate().isEqual(availableDate))
              .findFirst();
      if (roomForAvailableDate.isPresent()) {
        final DistributionHotelAvailResultWithRestrictionSet distributionHotelAvailForAvailDate =
            roomForAvailableDate.get();

        var finalAmount =
            applyCityTaxIfNeeded(distributionHotelAvailForAvailDate, distributionPayload.getHotelsCityTaxInfo(),
                LocalDate.parse(distributionPayload.getArrival()), availableDate);

        final var availableCost =
            AvailableCost.builder()
                .date(availableDate)
                .amount(finalAmount)
                .qtyAvailable(distributionHotelAvailForAvailDate.getQuantity())
                .currency(currency)
                .build();
        availableCostList.add(availableCost);
      }
    }
    log.trace("availableCost - {}", availableCostList);
    return availableCostList;
  }

  private BigDecimal applyCityTaxIfNeeded(
      DistributionHotelAvailResultWithRestrictionSet distributionHotelAvailForAvailDate,
      HotelsCityTaxInfo hotelsCityTaxInfo, LocalDate arrivalDate, LocalDate availableDate) {
    var hotelCode = distributionHotelAvailForAvailDate.getHotelCode();
    var originalAmount = distributionHotelAvailForAvailDate.getAmount();
    var dbAmountWithCityTax = distributionHotelAvailForAvailDate.getAmountWithCityTax();

    boolean shouldApplyCityTax = shouldApplyCityTax(cityTaxFeatureUtil.isFeatureEnabled(), hotelCode, hotelsCityTaxInfo,
        availableDate, arrivalDate);
    if (shouldApplyCityTax) {
      if (shouldCalculateCityTax(cityTaxFeatureUtil.isFallbackEnabled(), dbAmountWithCityTax)) {
        var hotelCityTax = hotelsCityTaxInfo.getHotelsCityTaxes().get(hotelCode);
        return CityTaxUtil.getAmountWithCityTax(hotelCityTax, hotelCode, originalAmount, 1, 1);
      } else {
        return dbAmountWithCityTax;
      }
    }

    return originalAmount;
  }

  private DistributionRatePlan populateDistributionRatePlanFromResultSet(
      final String rateClassification,
      final List<DistributionHotelAvailResultWithRestrictionSet> distributedHotelResultSetWithRates) {
    final var distributedRatePlan = new DistributionRatePlan();
    distributedRatePlan.setClassification(rateClassification);
    final Optional<DistributionHotelAvailResultWithRestrictionSet>
        distributionHotelRateOpt = distributedHotelResultSetWithRates
        .stream()
        .filter(rate -> rate.getRateClassification().equalsIgnoreCase(rateClassification))
        .findFirst();
    if (distributionHotelRateOpt.isPresent()) {
      final DistributionHotelAvailResultWithRestrictionSet hotelRate = distributionHotelRateOpt.get();
      distributedRatePlan.setCode(hotelRate.getRateCode());
      distributedRatePlan.setMaxNights(hotelRate.getMaxNights());
      distributedRatePlan.setMinNights(hotelRate.getMinNights());
    }
    return distributedRatePlan;
  }

  private SearchCriteria buildSearchCriteriaFromDistrPayload(DistributionPayload distributionPayload) {
    return SearchCriteria.builder()
        .hotelCodes(distributionPayload.getHotelCodes())
        .arrival(distributionPayload.getArrival())
        .departure(distributionPayload.getDeparture())
        // we always must care about the restrictions. Distribution checks the
        // restrictions in every availability call from Opera, so the restrictions
        // should be taken in consideration when we get the availability from cache too
        .flagMlos(true)
        .build();
  }
}
