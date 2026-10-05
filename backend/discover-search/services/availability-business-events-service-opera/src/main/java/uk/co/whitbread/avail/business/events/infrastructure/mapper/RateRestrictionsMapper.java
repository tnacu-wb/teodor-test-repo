package uk.co.whitbread.avail.business.events.infrastructure.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.avail.business.events.infrastructure.entity.HotelEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RatePlanEntity;
import uk.co.whitbread.avail.business.events.infrastructure.model.BusinessEventType;
import uk.co.whitbread.avail.business.events.infrastructure.model.RateDataElement;
import uk.co.whitbread.avail.business.events.infrastructure.model.RateDataElementValue;
import uk.co.whitbread.avail.business.events.infrastructure.model.enums.RateCode;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventDetail;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventHeader;
import uk.co.whitbread.avail.business.events.infrastructure.repository.RoomEntityJpaRepository;

@Slf4j
@AllArgsConstructor
public class RateRestrictionsMapper {

  private RoomEntityJpaRepository roomEntityJpaRepository;
  private static final String PMS_SOURCE = "OPERA";
  private static final String UNDERSCORE = "_";
  private static final String CLOSE =  "C";
  private static final String OPEN =  "O";

  public List<HotelEntity> mapRateRestrictionsEventToHotelEntity(
      final EventHeader eventHeader) {

    final String hotelCode = eventHeader.getHotelId();
    final RateDataElementValue rateDataElementValue
        = getRateDataElementValue(eventHeader);
    log.debug("rateDataElementValue: {}", rateDataElementValue);

    String beginDate = "";
    String endDate = "";
    if (Optional.ofNullable(rateDataElementValue.getMaxLos()).isPresent()) {
      beginDate = rateDataElementValue.getMaxLosScopeFrom();
      endDate = rateDataElementValue.getMaxLosScopeTo();
    } else if (Optional.ofNullable(rateDataElementValue.getMinLos()).isPresent()) {
      beginDate = rateDataElementValue.getMinLosScopeFrom();
      endDate = rateDataElementValue.getMinLosScopeTo();
    } else {
      log.error("Unsupported Rate Restriction type. No record will be updated!");
      return Collections.emptyList();
    }
    log.debug("before conversion beginDate: {}, endDate: {}", beginDate, endDate);

    if (StringUtils.isBlank(beginDate) && StringUtils.isBlank(endDate)) {
      log.error("As both begin and end dates are null/empty so no record will be updated!");
      return Collections.emptyList();
    }

    DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
        .append(DateTimeFormatter.ofPattern("dd-MMM-yy")).toFormatter();

    final List<HotelEntity> hotelEntityList = new ArrayList<>();

    LocalDate fromDate = null;
    LocalDate toDate = null;

    try {
      fromDate = LocalDate.parse(beginDate, formatter);
      toDate = LocalDate.parse(endDate, formatter);
    } catch (DateTimeParseException e) {
      log.error("Error occurred while parsing event time stamp: {}", e.getMessage());
    }
    log.debug("after conversion fromDate: {}, toDate: {}", fromDate, toDate);

    List<LocalDate> datesInRange = Collections.emptyList();
    if (fromDate != null && toDate != null) {
      datesInRange = fromDate.datesUntil(toDate.plusDays(1))
          .collect(Collectors.toList());
    }

    for (final LocalDate date : datesInRange) {
      final String hotelId = hotelCode + "_OPERA_" + date;
      final HotelEntity hotelEntity = buildHotelEntity(hotelCode, hotelId, date);
      log.debug("hotelEntity: {}", hotelEntity);
      hotelEntity.setEventHeader(eventHeader);
      hotelEntityList.add(hotelEntity);
    }

    processRateAdditionToHotelEntity(rateDataElementValue, hotelEntityList,
        hotelCode, eventHeader);
    return hotelEntityList;
  }

  private void processRateAdditionToHotelEntity(
      final RateDataElementValue rateDataElementValue,
      final List<HotelEntity> hotelEntityList,
      final String hotelCode,
      final EventHeader eventHeader) {
    final List<RateCode> possibleRateCategories = resolveRateCategories(rateDataElementValue,
        hotelCode, eventHeader);
    for (final HotelEntity hotelEntity : hotelEntityList) {
      //If Biz events does not have a room types,
      //all distinct room types from the DB will be retrieved
      final String bizEventRoomType = rateDataElementValue.getRoomType();
      final List<String> roomTypeList = resolveRoomTypes(bizEventRoomType,
          hotelEntity.getId(),
          hotelCode,
          eventHeader);
      addRateListToHotelEntity(rateDataElementValue, possibleRateCategories,
          roomTypeList, hotelEntity);
    }
  }

  private List<RateCode> resolveRateCategories(
      final RateDataElementValue rateDataElementValue,
      final String hotelCode,
      final EventHeader eventHeader) {
    final String bizEventRatePlanCode = rateDataElementValue.getRateCode();
    log.trace("RatePlanCode from event: {}", bizEventRatePlanCode);
    final String bizEventRateCategory = rateDataElementValue.getRateCategory();
    log.trace("RateCategory from event: {}", bizEventRateCategory);
    boolean isNullRatePlanCode = isNullRatePlanCode(bizEventRatePlanCode, bizEventRateCategory);
    if (isNullRatePlanCode) {
      log.debug("Received biz event of type : {} with "
              + "NULL/EMPTY Rate Plan Code for hotel : {} "
              + "for event : {}."
              + "All configured rate plan codes applicable.",
          BusinessEventType.RATE_RATE_RESTRICTIONS,
          hotelCode,
          eventHeader.getMetadata());
    }
    final String convertedRateCategory =
        convertToRateCategory(rateDataElementValue, bizEventRatePlanCode);
    log.trace("Null RateCategory in event, derived RateCategory : {} "
        + "from biz event RatePlanCode.", convertedRateCategory);
    final List<RateCode> possibleRateCategories =
        getRateCategories(convertedRateCategory, bizEventRatePlanCode, hotelCode);
    log.trace("Applicable RateCategories: {}", possibleRateCategories);
    return possibleRateCategories;
  }

  private boolean isNullRatePlanCode(final String bizEventRateCode,
      final String bizEventRateCategory) {
    return StringUtils.isBlank(bizEventRateCode) && StringUtils.isBlank(bizEventRateCategory);
  }

  private void addRateListToHotelEntity(final RateDataElementValue rateDataElementValue,
      final List<RateCode> possibleRateCategories,
      final List<String> roomTypeList,
      final HotelEntity hotelEntity) {
    final List<RatePlanEntity> rateEntityList = new ArrayList<>();
    final String maxLos = rateDataElementValue.getMaxLos();
    final String minLos = rateDataElementValue.getMinLos();
    for (final RateCode rateCategory : possibleRateCategories) {
      for (final String roomType : roomTypeList) {
        final String ratePlanCode = rateCategory.name();
        final String rateId = buildRateId(hotelEntity,
            ratePlanCode, roomType);
        final RatePlanEntity ratePlanEntity =
            buildRatePlanEntity(rateId, maxLos, minLos, rateCategory.toString(), ratePlanCode);
        ratePlanEntity.setHotel(hotelEntity);
        rateEntityList.add(ratePlanEntity);
      }
      hotelEntity.setRates(rateEntityList);
    }
  }

  private String buildRateId(
      final HotelEntity hotelEntity, final String rateCode, final String typeOfRoom) {
    return new StringBuilder(rateCode).append(UNDERSCORE).append(hotelEntity.getHotelCode())
        .append(UNDERSCORE).append(typeOfRoom).append(UNDERSCORE).append(PMS_SOURCE)
        .append(UNDERSCORE).append(hotelEntity.getDate()).toString();
  }

  /**
   * This method is to generate a list of rate categories if we are
   * getting rate categories from event
   * we add that to the list otherwise we use RateCode enum file where we have list of valid rate
   * codes and their corresponding categories.
   */
  private List<RateCode> getRateCategories(final String rateCategory,
                             final String bizEventRatePlanCode, final String hotelCode) {
    if (StringUtils.isBlank(rateCategory) && StringUtils.isBlank(bizEventRatePlanCode)) {
      log.trace("Rate category & Rate plan code not available in the event for hotel: {}",
          hotelCode);
      return getListOfRateCodesFromEnumFile();
    }
    List<RateCode> possibleRateCategories = new ArrayList<>(
        RateCode.getRatePlanCodes(rateCategory));
    log.trace("Possible rate categories : {}", possibleRateCategories);
    return possibleRateCategories;
  }

  private RatePlanEntity buildRatePlanEntity(
      final String id,
      final String maxLos,
      final String minLos,
      final String rateClassification,
      final String rateCode
  ) {

    RatePlanEntity ratePlanEntity = RatePlanEntity.builder()
        .id(id)
        .rateClassification(rateClassification)
        .rateCode(rateCode)
        .build();
    if (Optional.ofNullable(maxLos).isPresent()) {
      ratePlanEntity.setMaxNights(Integer.parseInt(maxLos));
    }
    if (Optional.ofNullable(minLos).isPresent()) {
      ratePlanEntity.setMinNights(Integer.parseInt(minLos));
    }

    return ratePlanEntity;
  }

  private RateDataElementValue getRateDataElementValue(
      final EventHeader eventHeader) {

    String maxLos = null;
    String maxLosScopeFrom = null;
    String maxLosScopeTo = null;
    String minLos = null;
    String minLosScopeFrom = null;
    String minLosScopeTo = null;
    String rateCode = null;
    String roomType = null;
    String rateCategory = null;

    final Set<EventDetail> eventDetailSet =
        eventHeader.getDetail();
    for (EventDetail eventDetail : eventDetailSet) {
      final String dateElement = eventDetail.getElementName();

      switch (dateElement) {
        case RateDataElement.MAX_LOS:
          maxLos = eventDetail.getNewValue();
          maxLosScopeFrom = eventDetail.getScopeFrom();
          maxLosScopeTo = eventDetail.getScopeTo();
          log.trace("maxLos: {}, scopeFrom: {}, scopeTo: {}",
              maxLos, maxLosScopeFrom, maxLosScopeTo);
          break;

        case RateDataElement.MIN_LOS:
          minLos = eventDetail.getNewValue();
          minLosScopeFrom = eventDetail.getScopeFrom();
          minLosScopeTo = eventDetail.getScopeTo();
          log.trace("minLos: {}, scopeFrom: {}, scopeTo: {}",
              minLos, minLosScopeFrom, minLosScopeTo);
          break;

        case RateDataElement.RATE_CODE:
          rateCode = eventDetail.getNewValue();
          log.trace("rateCode: {}", rateCode);
          break;

        case RateDataElement.ROOM_TYPE:
          roomType = eventDetail.getNewValue();
          log.trace("roomType: {}", roomType);
          break;

        case RateDataElement.RATE_CATEGORY:
          rateCategory = eventDetail.getNewValue();
          log.trace("rateCategory: {}", rateCategory);
          break;

        case RateDataElement.ARRIVAL:
          if (CLOSE.equalsIgnoreCase(eventDetail.getNewValue())) {
            maxLos = "0";
            minLos = "999";
          }
          if (OPEN.equalsIgnoreCase(eventDetail.getNewValue())) {
            maxLos = "0";
            minLos = "0";
          }
          maxLosScopeFrom = eventDetail.getScopeFrom();
          maxLosScopeTo = eventDetail.getScopeTo();
          minLosScopeFrom = maxLosScopeFrom;
          minLosScopeTo = maxLosScopeTo;
          log.trace("scopeFrom: {}, scopeTo: {}, minLos: {}, maxLos: {}", maxLosScopeFrom,
              maxLosScopeTo, minLos, maxLos);
          break;
        case RateDataElement.STAY:
          if (CLOSE.equalsIgnoreCase(eventDetail.getNewValue())) {
            maxLos = "0";
            minLos = "666";
          }
          if (OPEN.equalsIgnoreCase(eventDetail.getNewValue())) {
            maxLos = "0";
            minLos = "0";
          }
          maxLosScopeFrom = eventDetail.getScopeFrom();
          maxLosScopeTo = eventDetail.getScopeTo();
          minLosScopeFrom = maxLosScopeFrom;
          minLosScopeTo = maxLosScopeTo;
          log.trace("scopeFrom: {}, scopeTo: {}, minLos: {}, maxLos: {}, newValue: {}",
                  maxLosScopeFrom, maxLosScopeTo, minLos, maxLos, eventDetail.getNewValue());
          break;

        default:
          log.trace("Invalid Rate Element");
      }
    }

    return RateDataElementValue.builder()
        .maxLos(maxLos).minLos(minLos)
        .maxLosScopeFrom(maxLosScopeFrom).maxLosScopeTo(maxLosScopeTo)
        .minLosScopeFrom(minLosScopeFrom).minLosScopeTo(minLosScopeTo)
        .rateCode(rateCode).roomType(roomType)
        .rateCategory(rateCategory).build();
  }

  private HotelEntity buildHotelEntity(
      final String hotelCode,
      final String hotelId,
      final LocalDate date) {

    return HotelEntity.builder()
        .id(hotelId)
        .hotelCode(hotelCode)
        .pmsSource(PMS_SOURCE)
        .date(date)
        .build();
  }

  /**
   * This is used to generate the value of rate category from rate code when no rate category is
   * provided in the event.
   */
  private String convertToRateCategory(final RateDataElementValue rateDataElementValue,
      final String bizEventRatePlanCode) {
    String bizEventRateCategory = rateDataElementValue.getRateCategory();
    if (StringUtils.isBlank(bizEventRateCategory)) {
      bizEventRateCategory = StringUtils.isBlank(bizEventRatePlanCode)
          ? "" : getRateCategory(bizEventRatePlanCode);
    }
    return bizEventRateCategory;
  }

  private String getRateCategory(final String rateCode) {
    String rateCategory = "";
    try {
      rateCategory = (RateCode.valueOf(rateCode)).toString();
    } catch (IllegalArgumentException ex) {
      log.error("Invalid Rate code : {} received with message: {}", rateCode, ex.getMessage());
    }
    return rateCategory;
  }

  private String getRatePlanCodeFromCategory(final String rateCategory) {
    log.trace("Retrieving rate code for rateCategory: {}", rateCategory);
    final List<String> rateCodeEnumValues =
        Stream.of(RateCode.values())
            .map(Enum::name)
            .collect(Collectors.toList());

    Optional<String> rateCode = rateCodeEnumValues.stream()
        .filter(code -> getRateCategory(code).equalsIgnoreCase(rateCategory))
        .findAny();
    return rateCode.orElse("");
  }

  private List<RateCode> getListOfRateCodesFromEnumFile() {
    final List<RateCode> possibleRateCategories;
    possibleRateCategories = Stream.of(RateCode.values())
        .collect(Collectors.toList());
    log.trace("Rate codes from enum : {}", possibleRateCategories);
    return possibleRateCategories;
  }

  private List<String> resolveRoomTypes(final String roomType, final String hotelId,
      final String hotelCode,
      final EventHeader eventHeader) {
    List<String> roomTypes = new ArrayList<>();
    if (StringUtils.isBlank(roomType)) {
      roomTypes = roomEntityJpaRepository.getHotelTypesByHotelCode(hotelId);
      log.debug("Received biz events of type : {} with "
              + "NULL/EMPTY Room Type for hotel : {} "
              + "for event : {}."
              + "Room types from DB :{}",
          BusinessEventType.RATE_RATE_RESTRICTIONS,
          hotelCode,
          eventHeader.getMetadata(),
          roomTypes);
    } else {
      roomTypes.add(roomType);
      log.debug("Room types from Biz Event: {}", roomTypes);
    }
    log.trace("roomTypes: {}", roomTypes);
    return roomTypes;
  }

}
