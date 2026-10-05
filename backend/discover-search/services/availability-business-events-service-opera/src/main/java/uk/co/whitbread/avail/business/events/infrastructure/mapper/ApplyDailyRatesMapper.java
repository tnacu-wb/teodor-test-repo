package uk.co.whitbread.avail.business.events.infrastructure.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.avail.business.events.infrastructure.entity.HotelEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RatePlanEntity;
import uk.co.whitbread.avail.business.events.infrastructure.model.RateDataElement;
import uk.co.whitbread.avail.business.events.infrastructure.model.RateDataElementValue;
import uk.co.whitbread.avail.business.events.infrastructure.model.enums.RateCode;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventDetail;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventHeader;

@Slf4j
public final class ApplyDailyRatesMapper {

  private static final String UNDERSCORE = "_";
  private static final String OPERA = "OPERA";

  private ApplyDailyRatesMapper() {
  }

  public static List<HotelEntity> mapApplyDailyRatesEventToHotelEntity(
      final EventHeader eventHeader) {

    final String hotelCode = eventHeader.getHotelId();
    final String primaryKeyOfEvent = eventHeader.getPrimaryKey();
    final String rateCode =
        primaryKeyOfEvent.substring(primaryKeyOfEvent.indexOf(";") + 1);
    log.debug("rateCode from mapper: {}", rateCode);
    final String rateCategory = getRateCategory(rateCode);
    log.debug("rateCategory from mapper: {}", rateCategory);
    final RateDataElementValue rateDataElementValue
        = getRateDataElementValue(eventHeader);
    log.debug("rateDataElementValue: {}", rateDataElementValue);
    final String beginDate = rateDataElementValue.getBeginDate();
    final String endDate = rateDataElementValue.getEndDate();

    final List<HotelEntity> hotelEntityList = new ArrayList<>();
    final LocalDate fromDate = LocalDate.parse(beginDate);
    final LocalDate toDate = LocalDate.parse(endDate);
    List<LocalDate> datesInRange = fromDate.datesUntil(toDate.plusDays(1))
        .collect(Collectors.toList());
    for (LocalDate date : datesInRange) {
      final String hotelId = hotelCode + "_OPERA_" + date;
      final HotelEntity hotelEntity = buildHotelEntity(hotelCode, hotelId, date);
      hotelEntity.setEventHeader(eventHeader);
      hotelEntityList.add(hotelEntity);
    }

    addRatesToEntities(rateCategory, rateDataElementValue, hotelEntityList, rateCode);
    log.debug("hotelEntityList: {}", hotelEntityList);

    return hotelEntityList;
  }

  private static String getRateCategory(final String rateCode) {
    String rateCategory = "";
    try {
      rateCategory = (RateCode.valueOf(rateCode)).toString();
    } catch (IllegalArgumentException ex) {
      log.error("Invalid Rate code : {} received with message: {}", rateCode, ex.getMessage());
    }
    return rateCategory;
  }

  private static void addRatesToEntities(
      final String rateCategory,
      final RateDataElementValue rateDataElementValue,
      final List<HotelEntity> hotelEntityList,
      final String rateCode) {

    for (HotelEntity hotelEntity : hotelEntityList) {
      final List<RatePlanEntity> rateEntityList = new ArrayList<>();
      final String personRate1 = rateDataElementValue.getPersonRate1();
      final List<String> roomTypes = rateDataElementValue.getRoomTypes();
      RatePlanEntity ratePlanEntity = null;
      for (final String roomType : roomTypes) {
        final String rateId = new StringBuilder(rateCode)
            .append(UNDERSCORE).append(hotelEntity.getHotelCode())
            .append(UNDERSCORE).append(roomType)
            .append(UNDERSCORE).append(OPERA)
            .append(UNDERSCORE).append(hotelEntity.getDate()).toString();

        ratePlanEntity = buildRatePlanEntity(rateId, personRate1, rateCategory, rateCode);
        if (ratePlanEntity != null) {
          ratePlanEntity.setHotel(hotelEntity);
          rateEntityList.add(ratePlanEntity);
        }
      }
      hotelEntity.setRates(rateEntityList);
    }
  }

  private static RatePlanEntity buildRatePlanEntity(
      final String id,
      final String personRate1,
      final String rateClassification,
      final String rateCode
  ) {
    final BigDecimal amount = new BigDecimal(personRate1);
    return RatePlanEntity.builder()
        .id(id)
        .amount(amount)
        .rateClassification(rateClassification)
        .rateCode(rateCode)
        .build();
  }

  private static RateDataElementValue getRateDataElementValue(
      final EventHeader eventHeader) {

    String personRate1 = "";
    String beginDate = "";
    String endDate = "";
    List<String> roomTypes = new ArrayList<>();

    final Set<EventDetail> eventDetailSet =
        eventHeader.getDetail();
    for (EventDetail eventDetail : eventDetailSet) {
      final String dateElement = eventDetail.getElementName();

      switch (dateElement) {
        case RateDataElement.PERSON_RATE_1:
          personRate1 = eventDetail.getNewValue();
          log.debug("personRate1: {}", personRate1);
          break;
        case RateDataElement.BEGIN_DATE:
          beginDate = eventDetail.getNewValue();
          log.debug("beginDate: {}", beginDate);
          break;
        case RateDataElement.END_DATE:
          endDate = eventDetail.getNewValue();
          log.debug("endDate: {}", endDate);
          break;
        case RateDataElement.ROOM_TYPES:
          String roomType = eventDetail.getNewValue();
          log.debug("roomType: {}", roomType);
          roomTypes.add(roomType);
          break;
        default:
          //do nothing
      }
    }

    return buildRateDataElementValue(personRate1, beginDate, endDate, roomTypes);

  }

  private static RateDataElementValue buildRateDataElementValue(
      final String personRate1,
      final String beginDate,
      final String endDate,
      final List<String> roomTypes) {

    return RateDataElementValue.builder()
        .personRate1(personRate1)
        .beginDate(beginDate)
        .endDate(endDate)
        .roomTypes(roomTypes)
        .build();
  }

  private static HotelEntity buildHotelEntity(
      final String hotelCode,
      final String hotelId,
      final LocalDate date) {

    return HotelEntity.builder()
        .id(hotelId)
        .hotelCode(hotelCode)
        .pmsSource(OPERA)
        .date(date)
        .build();
  }

}
