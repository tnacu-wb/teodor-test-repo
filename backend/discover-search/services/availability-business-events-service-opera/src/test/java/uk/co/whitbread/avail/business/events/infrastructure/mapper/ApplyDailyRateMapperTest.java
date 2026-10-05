package uk.co.whitbread.avail.business.events.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.avail.business.events.infrastructure.entity.HotelEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RatePlanEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RoomEntity;
import uk.co.whitbread.avail.business.events.infrastructure.model.BusinessEventType;
import uk.co.whitbread.avail.business.events.infrastructure.model.RateDataElement;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.*;

@RequiredArgsConstructor
@Slf4j
public class ApplyDailyRateMapperTest {

  private static final String UNDERSCORE = "_";
  private static final String OPERA = "OPERA";

  final int year = LocalDate.now().getYear() + 1;
  final String hotelCode = "LONSUT";

  final LocalDate localDate1 = LocalDate.of(year, 05, 18);
  final LocalDate localDate2 = localDate1.plusDays(1);
  final LocalDate localDate3 = localDate2.plusDays(1);
  final String hotelId1 = hotelCode + "_OPERA_" + localDate1.format(
      DateTimeFormatter.ISO_LOCAL_DATE);

  final String hotelId2 = hotelCode + "_OPERA_" + localDate2.format(
      DateTimeFormatter.ISO_LOCAL_DATE);

  final String hotelId3 = hotelCode + "_OPERA_" + localDate3.format(
      DateTimeFormatter.ISO_LOCAL_DATE);

  final String rateCategory = "A";
  final String rateCode = "FLEXRATE";
  final String amount = "39.9";
  final String pmsSource = "OPERA";
  final String roomType = "EXTDBL";
  final String roomTypeFmquad = "FMQUAD";
  final String roomTypeLowdbl = "LOWDBL";

  EventHeader eventHeader;

  List<RatePlanEntity> rates1;
  List<RatePlanEntity> rates2;
  List<RatePlanEntity> rates3;
  List<HotelEntity> hotelEntitiesExpected;
  List<HotelEntity> hotelEntitiesExpectedForSigneDay;

  @BeforeEach
  public void setup() {

    eventHeader = buildMockEventHeader();

    hotelEntitiesExpected = new ArrayList<>();
    hotelEntitiesExpectedForSigneDay = new ArrayList<>();

    rates1 = buildRatePlanEntities(hotelId1);
    rates2 = buildRatePlanEntities(hotelId2);
    rates3 = buildRatePlanEntities(hotelId3);
    HotelEntity hotelEntity1 = buildHotelEntity(hotelId1, hotelCode, localDate1, rates1, eventHeader);
    HotelEntity hotelEntity2 = buildHotelEntity(hotelId2, hotelCode, localDate2, rates2, eventHeader);
    HotelEntity hotelEntity3 = buildHotelEntity(hotelId3, hotelCode, localDate3, rates3, eventHeader);

    hotelEntitiesExpected.add(hotelEntity1);
    hotelEntitiesExpected.add(hotelEntity2);
    hotelEntitiesExpected.add(hotelEntity3);

    hotelEntitiesExpectedForSigneDay.add(hotelEntity1);
  }

  private EventHeader buildMockEventHeader(){
    final String eventName = BusinessEventType.RATE_APPLY_DAILY_RATES;
    final EventDetail beginDate =
        buildEventDetail(RateDataElement.BEGIN_DATE, localDate1.format(
            DateTimeFormatter.ISO_LOCAL_DATE));
    final EventDetail endDate =
        buildEventDetail(RateDataElement.END_DATE, localDate3.format(
            DateTimeFormatter.ISO_LOCAL_DATE));
    final EventDetail personRate1 =
        buildEventDetail(RateDataElement.PERSON_RATE_1, amount);
    final EventDetail roomTypeElement =
        buildEventDetail(RateDataElement.ROOM_TYPES, roomType);
    final Set<EventDetail> eventDetailsSet = new HashSet<>();
    eventDetailsSet.add(beginDate);
    eventDetailsSet.add(endDate);
    eventDetailsSet.add(personRate1);
    eventDetailsSet.add(roomTypeElement);
    return buildEventHeader(hotelCode, eventName, eventDetailsSet);
  }

  @Test
  public void convertSummaryTotalEventToHotelEntityTest() {

    List<HotelEntity> hotelEntityList =
        ApplyDailyRatesMapper.mapApplyDailyRatesEventToHotelEntity(eventHeader);

    assertEquals(hotelEntitiesExpected, hotelEntityList);
    assertEquals(3, hotelEntityList.size());
  }

  @Test
  public void sameBeginAndEndDateConvertSummaryTotalEventToHotelEntityTest() {

    final EventDetail beginDate =
        buildEventDetail(RateDataElement.BEGIN_DATE, localDate1.format(
            DateTimeFormatter.ISO_LOCAL_DATE));

    final EventDetail endDate =
        buildEventDetail(RateDataElement.END_DATE, localDate1.format(
            DateTimeFormatter.ISO_LOCAL_DATE));

    final EventDetail personRate1 =
        buildEventDetail(RateDataElement.PERSON_RATE_1, amount);

    final EventDetail roomType1 =
        buildEventDetail(RateDataElement.ROOM_TYPES, roomType);

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(beginDate);
    eventDetailSet.add(endDate);
    eventDetailSet.add(personRate1);
    eventDetailSet.add(roomType1);

    eventHeader.setDetail(eventDetailSet);

    List<HotelEntity> hotelEntityList =
        ApplyDailyRatesMapper.mapApplyDailyRatesEventToHotelEntity(eventHeader);

    assertEquals(1, hotelEntityList.size());

    assertEquals(hotelEntitiesExpectedForSigneDay.get(0).getId(),
        hotelEntityList.get(0).getId());

    assertEquals(hotelEntitiesExpectedForSigneDay.get(0).getRates(),
        hotelEntityList.get(0).getRates());

  }


  @Test
  public void sameBeginAndEndDateManyRoomTypesTest() {

    final EventDetail beginDate =
        buildEventDetail(RateDataElement.BEGIN_DATE, localDate1.format(
            DateTimeFormatter.ISO_LOCAL_DATE));

    final EventDetail endDate =
        buildEventDetail(RateDataElement.END_DATE, localDate1.format(
            DateTimeFormatter.ISO_LOCAL_DATE));

    final EventDetail personRate1 =
        buildEventDetail(RateDataElement.PERSON_RATE_1, amount);

    final EventDetail roomType1 =
        buildEventDetail(RateDataElement.ROOM_TYPES, roomType);
    final EventDetail roomType2 =
        buildEventDetail(RateDataElement.ROOM_TYPES, roomTypeFmquad);
    final EventDetail roomType3 =
        buildEventDetail(RateDataElement.ROOM_TYPES, roomTypeLowdbl);

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(beginDate);
    eventDetailSet.add(endDate);
    eventDetailSet.add(personRate1);
    eventDetailSet.add(roomType1);
    eventDetailSet.add(roomType2);
    eventDetailSet.add(roomType3);

    eventHeader.setDetail(eventDetailSet);

    List<HotelEntity> hotelEntityList =
        ApplyDailyRatesMapper.mapApplyDailyRatesEventToHotelEntity(eventHeader);

    assertEquals(1, hotelEntityList.size());

    assertEquals(hotelEntitiesExpectedForSigneDay.get(0).getId(),
        hotelEntityList.get(0).getId());

    assertEquals(3, hotelEntityList.get(0).getRates().size());

    assertNotNull(hotelEntityList.get(0).getRates().get(0));
    assertNotNull(hotelEntityList.get(0).getRates().get(1));
    assertNotNull(hotelEntityList.get(0).getRates().get(2));

  }

  private EventDetail buildEventDetail(
      final String elementName,
      final String newValue
  ) {

    return EventDetail.builder()
        .elementName(elementName)
        .newValue(newValue)
        .build();
  }

  private EventHeader buildEventHeader(
      final String hotelId,
      final String eventName,
      final Set<EventDetail> detail) {
    log.info("rateCode: {}", rateCode);
    final String primaryKey = hotelId + ";" + rateCode;
    return EventHeader.builder()
        .eventName(eventName)
        .hotelId(hotelId)
        .primaryKey(primaryKey)
            .detail(detail)
        .build();
  }

  private List<RatePlanEntity> buildRatePlanEntities(String hotelId) {

    final List<RatePlanEntity> rateEntityList = new ArrayList<>();
    final RatePlanEntity rateEntity1 = buildRateEntity(hotelId, rateCategory, amount);
    rateEntityList.add(rateEntity1);

    return rateEntityList;
  }

  private RatePlanEntity buildRateEntity(
      final String hotelId,
      final String rateClassification,
      final String amount
  ) {
    final RoomEntity roomEntity = RoomEntity.builder()
        .id(roomType + "_" + hotelId)
        .roomType(roomType)
        .build();

    final String rateId = rateCode
        + UNDERSCORE + hotelCode + UNDERSCORE + roomType
        + UNDERSCORE + OPERA  + UNDERSCORE + localDate1.format(
        DateTimeFormatter.ISO_LOCAL_DATE);

    return RatePlanEntity.builder()
        //.id(rateClassification + "_" + hotelId + "_" + roomType)
        .id(rateId)
        .rateClassification(rateClassification)
        .amount(new BigDecimal(amount))
        .room(roomEntity)
        .rateCode(rateCode)
        .build();
  }

  private HotelEntity buildHotelEntity(
      final String hotelId,
      final String hotelCode,
      final LocalDate date,
      final List<RatePlanEntity> rates,
      final EventHeader eventHeader) {

    return HotelEntity.builder()
        .id(hotelId)
        .hotelCode(hotelCode)
        .date(date)
        .pmsSource(pmsSource)
        .rates(rates)
        .eventHeader(eventHeader)
        .build();

  }

}