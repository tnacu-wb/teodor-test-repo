package uk.co.whitbread.avail.business.events.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.avail.business.events.infrastructure.entity.HotelEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RatePlanEntity;
import uk.co.whitbread.avail.business.events.infrastructure.model.BusinessEventType;
import uk.co.whitbread.avail.business.events.infrastructure.model.RateDataElement;
import uk.co.whitbread.avail.business.events.infrastructure.model.enums.RateCode;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventDetail;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventHeader;
import uk.co.whitbread.avail.business.events.infrastructure.repository.RoomEntityJpaRepository;


@ExtendWith(MockitoExtension.class)
public class RateRestrictionsMapperTest {

  //default time zone
  ZoneId defaultZoneId = ZoneId.systemDefault();
  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy");

  final int year = LocalDate.now().getYear() + 1;
  final String hotelCode = "LONSUT";

  final LocalDate localDate1 = LocalDate.of(year, Month.MAY, 18);
  Date date1 = Date.from(localDate1.atStartOfDay(defaultZoneId).toInstant());
  final LocalDate localDate2 = localDate1.plusDays(1);
  final LocalDate localDate3 = localDate2.plusDays(1);
  Date date3 = Date.from(localDate3.atStartOfDay(defaultZoneId).toInstant());
  final String hotelId1 = hotelCode + "_OPERA_" + localDate1.format(
      DateTimeFormatter.ISO_LOCAL_DATE);

  final String hotelId2 = hotelCode + "_OPERA_" + localDate2.format(
      DateTimeFormatter.ISO_LOCAL_DATE);

  final String hotelId3 = hotelCode + "_OPERA_" + localDate3.format(
      DateTimeFormatter.ISO_LOCAL_DATE);

  final String rateClassification = "A";
  final String rateCode = "FLEXRATE";
  final String roomType = null;

  final String roomTypeDouble= "Double";
  final String roomTypeSingle= "Single";
  final String roomTypeFamily= "Family";

  final String maxLosThreeDays = "3";
  final String maxLosOneDay = "1";
  final String scopeFrom = simpleDateFormat.format(date1);
  final String scopeTo = simpleDateFormat.format(date3);
  final String minLosOneDay = "1";
  final String minLosThreeDays = "3";
  final String pmsSource = "OPERA";

  EventHeader eventHeader;

  List<RatePlanEntity> rates1;
  List<RatePlanEntity> rates2;
  List<RatePlanEntity> rates3;

  List<RatePlanEntity> rateWithDifferentRoomTypes;
  List<RatePlanEntity> rateWithOneRoomTypesDifferentCategories;

  List<RatePlanEntity> emptyRates;
  List<HotelEntity> hotelEntitiesExpected;
  List<HotelEntity> hotelEntitiesExpectedForSigneDay;
  List<HotelEntity> hotelEntitiesExpectedForSigneDayNoRates;
  List<HotelEntity> hotelEntitiesExpectedForSigneDayMultipleDbRates;
  List<HotelEntity> multipleHotelEntitiesMultipleRatesExpected;
  @Mock
  private RoomEntityJpaRepository roomEntityJpaRepository;
  private RateRestrictionsMapper rateRestrictionsMapper;


  @BeforeEach
  public void setup() {
    final String eventName = BusinessEventType.RATE_RATE_RESTRICTIONS;
    eventHeader = buildBusinessEventHeader(hotelCode, eventName);

    hotelEntitiesExpected = new ArrayList<>();
    hotelEntitiesExpectedForSigneDay = new ArrayList<>();
    hotelEntitiesExpectedForSigneDayNoRates = new ArrayList<>();
    hotelEntitiesExpectedForSigneDayMultipleDbRates = new ArrayList<>();
    multipleHotelEntitiesMultipleRatesExpected = new ArrayList<>();

    rates1 = buildRatePlanEntities(hotelId1);
    rates2 = buildRatePlanEntities(hotelId2);
    rates3 = buildRatePlanEntities(hotelId3);

    rateWithDifferentRoomTypes = new ArrayList<>();
    RatePlanEntity ratePlanEntityDouble =
        buildRateEntity(hotelId1, rateCode, maxLosThreeDays, roomTypeDouble);
    RatePlanEntity ratePlanEntitySingle =
        buildRateEntity(hotelId1, rateCode, maxLosThreeDays, roomTypeSingle);
    RatePlanEntity ratePlanEntityFamily =
        buildRateEntity(hotelId1, rateCode, maxLosThreeDays, roomTypeFamily);
    rateWithDifferentRoomTypes.add(ratePlanEntityDouble);
    rateWithDifferentRoomTypes.add(ratePlanEntitySingle);
    rateWithDifferentRoomTypes.add(ratePlanEntityFamily);

    rateWithOneRoomTypesDifferentCategories = new ArrayList<>();



    final HotelEntity hotelEntity1 = buildHotelEntity(hotelId1, hotelCode, localDate1, rates1, eventHeader);
    final HotelEntity hotelEntity2 = buildHotelEntity(hotelId2, hotelCode, localDate2, rates2, eventHeader);
    final HotelEntity hotelEntity3 = buildHotelEntity(hotelId3, hotelCode, localDate3, rates3, eventHeader);

    hotelEntitiesExpected.add(hotelEntity1);
    hotelEntitiesExpected.add(hotelEntity2);
    hotelEntitiesExpected.add(hotelEntity3);

    final HotelEntity hotelEntityHavingRoomTypes1
        = buildHotelEntity(hotelId1, hotelCode, localDate1, rateWithDifferentRoomTypes, eventHeader);
    final HotelEntity hotelEntityHavingRoomTypes2
        = buildHotelEntity(hotelId2, hotelCode, localDate2, rateWithDifferentRoomTypes, eventHeader);
    final HotelEntity hotelEntityHavingRoomTypes3
        = buildHotelEntity(hotelId3, hotelCode, localDate3, rateWithDifferentRoomTypes, eventHeader);

    multipleHotelEntitiesMultipleRatesExpected.add(hotelEntityHavingRoomTypes1);
    multipleHotelEntitiesMultipleRatesExpected.add(hotelEntityHavingRoomTypes2);
    multipleHotelEntitiesMultipleRatesExpected.add(hotelEntityHavingRoomTypes3);

    hotelEntitiesExpectedForSigneDay.add(hotelEntity1);

    emptyRates = Collections.emptyList();
    hotelEntitiesExpectedForSigneDayNoRates
        .add(buildHotelEntity(hotelId1, hotelCode, localDate1, emptyRates, eventHeader));

    hotelEntitiesExpectedForSigneDayMultipleDbRates
        .add(buildHotelEntity(hotelId1, hotelCode, localDate1, rateWithDifferentRoomTypes, eventHeader));

    rateRestrictionsMapper = new RateRestrictionsMapper(roomEntityJpaRepository);

  }

  @Test
  public void maxLosThreeDaysTest() {

    final EventDetail rateCodeDe =
        buildEventDetails(RateDataElement.RATE_CODE, rateCode);

    final EventDetail maxLosDe =
        buildEventDetails(
            RateDataElement.MAX_LOS, maxLosThreeDays, scopeFrom, scopeTo);

    final EventDetail roomType = buildEventDetails(RateDataElement.ROOM_TYPE, "Double");

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(rateCodeDe);
    eventDetailSet.add(maxLosDe);
    eventDetailSet.add(roomType);

    eventHeader.setDetail(eventDetailSet);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(hotelEntitiesExpected, hotelEntityList);
    assertEquals(3, hotelEntityList.size());
  }

  @Test
  public void maxLosOneDaysTest() {

    final EventDetail rateCodeDe =
        buildEventDetails(RateDataElement.RATE_CODE, rateCode);

    final EventDetail maxLosDe =
        buildEventDetails(
            RateDataElement.MAX_LOS, maxLosOneDay, scopeFrom, scopeFrom);
    final EventDetail roomType = buildEventDetails(RateDataElement.ROOM_TYPE, "Double");

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(rateCodeDe);
    eventDetailSet.add(maxLosDe);
    eventDetailSet.add(roomType);

    eventHeader.setDetail(eventDetailSet);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(hotelEntitiesExpectedForSigneDay, hotelEntityList);
    assertEquals(1, hotelEntityList.size());
  }

  @Test
  public void minLosThreeDaysTest() {

    final EventDetail rateCodeDe =
        buildEventDetails(RateDataElement.RATE_CODE, rateCode);

    final EventDetail maxLosDe =
        buildEventDetails(
            RateDataElement.MIN_LOS, minLosThreeDays, scopeFrom, scopeTo);

    final EventDetail roomType = buildEventDetails(RateDataElement.ROOM_TYPE, "Double");

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(rateCodeDe);
    eventDetailSet.add(maxLosDe);
    eventDetailSet.add(roomType);

    eventHeader.setDetail(eventDetailSet);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(hotelEntitiesExpected, hotelEntityList);
    assertEquals(3, hotelEntityList.size());
  }

  @Test
  public void minLosOneDaysTest() {

    final EventDetail rateCodeDe =
        buildEventDetails(RateDataElement.RATE_CODE, rateCode);

    final EventDetail maxLosDe =
        buildEventDetails(
            RateDataElement.MIN_LOS, minLosOneDay, scopeFrom, scopeFrom);

    final EventDetail roomType = buildEventDetails(RateDataElement.ROOM_TYPE, roomTypeDouble);

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(rateCodeDe);
    eventDetailSet.add(maxLosDe);
    eventDetailSet.add(roomType);

    eventHeader.setDetail(eventDetailSet);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(hotelEntitiesExpectedForSigneDay, hotelEntityList);
    assertEquals(1, hotelEntityList.size());
  }

  @Test
  public void maxLosOneDayNoRoomTypeTest() {

    final EventDetail rateCodeDe =
        buildEventDetails(RateDataElement.RATE_CODE, rateCode);

    final EventDetail maxLosDe =
        buildEventDetails(
            RateDataElement.MAX_LOS, maxLosOneDay, scopeFrom, scopeFrom);

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(rateCodeDe);
    eventDetailSet.add(maxLosDe);

    eventHeader.setDetail(eventDetailSet);

    List<String> roomTypes = Collections.emptyList();

    Mockito.when(roomEntityJpaRepository.getHotelTypesByHotelCode(hotelId1))
        .thenReturn(roomTypes);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(hotelEntitiesExpectedForSigneDayNoRates, hotelEntityList);
    assertEquals(1, hotelEntityList.size());
    assertEquals(roomTypes.size(), hotelEntityList.get(0).getRates().size());
  }

  @Test
  public void maxLosOneDaysNoRoomTypeFromEventMultipleRoomTypeFromDbTest() {

    final EventDetail rateCodeDe = buildEventDetails(RateDataElement.RATE_CODE, rateCode);

    final EventDetail maxLosDe =
        buildEventDetails(RateDataElement.MAX_LOS, maxLosOneDay, scopeFrom, scopeFrom);

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(rateCodeDe);
    eventDetailSet.add(maxLosDe);

    eventHeader.setDetail(eventDetailSet);

    List<String> roomTypes = new ArrayList<>();
    roomTypes.add(roomTypeDouble);
    roomTypes.add(roomTypeSingle);
    roomTypes.add(roomTypeFamily);

    Mockito.when(roomEntityJpaRepository.getHotelTypesByHotelCode(hotelId1))
        .thenReturn(roomTypes);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(hotelEntitiesExpectedForSigneDayMultipleDbRates, hotelEntityList);
    assertEquals(1, hotelEntityList.size());
    assertEquals(roomTypes.size(), hotelEntityList.get(0).getRates().size());
  }

  @Test
  public void maxLosMultipleDaysNoRoomTypeFromEventMultipleRoomTypeFromDbTest() {

    final EventDetail rateCodeDe = buildEventDetails(RateDataElement.RATE_CODE, rateCode);

    final EventDetail maxLosDe =
        buildEventDetails(RateDataElement.MAX_LOS, maxLosThreeDays, scopeFrom, scopeTo);

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(rateCodeDe);
    eventDetailSet.add(maxLosDe);

    eventHeader.setDetail(eventDetailSet);

    List<String> roomTypes = new ArrayList<>();
    roomTypes.add(roomTypeDouble);
    roomTypes.add(roomTypeSingle);
    roomTypes.add(roomTypeFamily);

    Mockito.when(roomEntityJpaRepository.getHotelTypesByHotelCode(hotelId1))
        .thenReturn(roomTypes);
    Mockito.when(roomEntityJpaRepository.getHotelTypesByHotelCode(hotelId2))
        .thenReturn(roomTypes);
    Mockito.when(roomEntityJpaRepository.getHotelTypesByHotelCode(hotelId3))
        .thenReturn(roomTypes);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(multipleHotelEntitiesMultipleRatesExpected.size(), hotelEntityList.size());
    assertEquals(multipleHotelEntitiesMultipleRatesExpected, hotelEntityList);
    assertEquals(roomTypes.size(), hotelEntityList.get(0).getRates().size());
    assertEquals(roomTypes.size(), hotelEntityList.get(1).getRates().size());
    assertEquals(roomTypes.size(), hotelEntityList.get(2).getRates().size());
  }

  //When both room type and rate category is null or empty no rates should be added
  @Test
  public void maxLosOneDaysNoRoomTypeNoCategoryFromEventTest() {

    final EventDetail maxLosDe =
        buildEventDetails(
            RateDataElement.MAX_LOS, maxLosOneDay, scopeFrom, scopeFrom);

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(maxLosDe);

    eventHeader.setDetail(eventDetailSet);

    List<String> roomTypes = Collections.emptyList();

    Mockito.when(roomEntityJpaRepository.getHotelTypesByHotelCode(hotelId1))
        .thenReturn(roomTypes);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(hotelEntitiesExpectedForSigneDayNoRates, hotelEntityList);
    assertEquals(1, hotelEntityList.size());
    assertEquals(0, hotelEntityList.get(0).getRates().size());
  }

  //When one room type and multiple rate categories, as categories are there as many rates
  //should be inserted
  @Test
  public void oneRoomTypeMultipleCategoryTest() {

    final EventDetail maxLosDe =
        buildEventDetails(
            RateDataElement.MIN_LOS, minLosOneDay, scopeFrom, scopeFrom);

    final EventDetail roomType = buildEventDetails(RateDataElement.ROOM_TYPE, roomTypeDouble);

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(maxLosDe);
    eventDetailSet.add(roomType);

    eventHeader.setDetail(eventDetailSet);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(1, hotelEntityList.size());

    final List<String> rateCodeEnumValues = Stream.of(RateCode.values())
        .map(Enum::name)
        .collect(Collectors.toList());

    assertEquals(rateCodeEnumValues.size(), hotelEntityList.get(0).getRates().size());

  }

  @Test
  public void manyRoomTypeManyCategoryTest() {

    final EventDetail maxLosDe =
        buildEventDetails(RateDataElement.MAX_LOS, maxLosThreeDays, scopeFrom, scopeTo);

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(maxLosDe);

    eventHeader.setDetail(eventDetailSet);

    List<String> roomTypes = new ArrayList<>();
    roomTypes.add(roomTypeDouble);
    roomTypes.add(roomTypeSingle);
    roomTypes.add(roomTypeFamily);

    Mockito.when(roomEntityJpaRepository.getHotelTypesByHotelCode(hotelId1))
        .thenReturn(roomTypes);
    Mockito.when(roomEntityJpaRepository.getHotelTypesByHotelCode(hotelId2))
        .thenReturn(roomTypes);
    Mockito.when(roomEntityJpaRepository.getHotelTypesByHotelCode(hotelId3))
        .thenReturn(roomTypes);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    final List<String> rateCodeEnumValues = Stream.of(RateCode.values())
        .map(Enum::name)
        .collect(Collectors.toList());

    int expectedRateListSize = roomTypes.size()*rateCodeEnumValues.size();

    assertEquals(multipleHotelEntitiesMultipleRatesExpected.size(), hotelEntityList.size());
    assertEquals(expectedRateListSize, hotelEntityList.get(0).getRates().size());
    assertEquals(expectedRateListSize, hotelEntityList.get(1).getRates().size());
    assertEquals(expectedRateListSize, hotelEntityList.get(2).getRates().size());

  }

  //No Room type, but both Category and Code Selected
  @Test
  public void NoRoomTypeOneCodeAndCategory() {

    final EventDetail rateCodeDe = buildEventDetails(RateDataElement.RATE_CODE, rateCode);
    final EventDetail rateCategory =
        buildEventDetails(RateDataElement.RATE_CATEGORY, rateClassification);

    final EventDetail maxLosDe =
        buildEventDetails(
            RateDataElement.MAX_LOS, maxLosOneDay, scopeFrom, scopeFrom);

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(maxLosDe);
    eventDetailSet.add(rateCodeDe);
    eventDetailSet.add(rateCategory);

    eventHeader.setDetail(eventDetailSet);

    List<String> roomTypes = new ArrayList<>();
    roomTypes.add(roomTypeDouble);
    roomTypes.add(roomTypeSingle);
    roomTypes.add(roomTypeFamily);

    Mockito.when(roomEntityJpaRepository.getHotelTypesByHotelCode(hotelId1))
        .thenReturn(roomTypes);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(1, hotelEntityList.size());
    assertEquals(3, hotelEntityList.get(0).getRates().size());
    assertEquals(rateClassification,
        hotelEntityList.get(0).getRates().get(0).getRateClassification());
    assertEquals(rateClassification,
        hotelEntityList.get(0).getRates().get(1).getRateClassification());
    assertEquals(rateClassification,
        hotelEntityList.get(0).getRates().get(2).getRateClassification());

    assertEquals(rateCode,
        hotelEntityList.get(0).getRates().get(0).getRateCode());
    assertEquals(rateCode,
        hotelEntityList.get(0).getRates().get(1).getRateCode());
    assertEquals(rateCode,
        hotelEntityList.get(0).getRates().get(2).getRateCode());
  }

  @Test
  public void WithRateCategoryAndNoRatePlanCode() {

    final EventDetail rateCategory =
        buildEventDetails(RateDataElement.RATE_CATEGORY, rateClassification);

    final EventDetail maxLosDe =
        buildEventDetails(
            RateDataElement.MAX_LOS, maxLosOneDay, scopeFrom, scopeFrom);

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(maxLosDe);
    eventDetailSet.add(rateCategory);

    eventHeader.setDetail(eventDetailSet);

    List<String> roomTypes = new ArrayList<>();
    roomTypes.add(roomTypeDouble);
    roomTypes.add(roomTypeSingle);
    roomTypes.add(roomTypeFamily);

    Mockito.when(roomEntityJpaRepository.getHotelTypesByHotelCode(hotelId1))
        .thenReturn(roomTypes);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(1, hotelEntityList.size());
    assertEquals(3, hotelEntityList.get(0).getRates().size());
    assertEquals(rateClassification,
        hotelEntityList.get(0).getRates().get(0).getRateClassification());
    assertEquals(rateClassification,
        hotelEntityList.get(0).getRates().get(1).getRateClassification());
    assertEquals(rateClassification,
        hotelEntityList.get(0).getRates().get(2).getRateClassification());

    assertEquals(rateCode,
        hotelEntityList.get(0).getRates().get(0).getRateCode());
    assertEquals(rateCode,
        hotelEntityList.get(0).getRates().get(1).getRateCode());
    assertEquals(rateCode,
        hotelEntityList.get(0).getRates().get(2).getRateCode());
  }

  @Test
  public void WithRatePlanCodeAndNoRateCategory() {

    final EventDetail rateCodeDe = buildEventDetails(RateDataElement.RATE_CODE, rateCode);
    final EventDetail maxLosDe =
        buildEventDetails(
            RateDataElement.MAX_LOS, maxLosOneDay, scopeFrom, scopeFrom);

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(maxLosDe);
    eventDetailSet.add(rateCodeDe);

    eventHeader.setDetail(eventDetailSet);

    List<String> roomTypes = new ArrayList<>();
    roomTypes.add(roomTypeDouble);
    roomTypes.add(roomTypeSingle);
    roomTypes.add(roomTypeFamily);

    Mockito.when(roomEntityJpaRepository.getHotelTypesByHotelCode(hotelId1))
        .thenReturn(roomTypes);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(1, hotelEntityList.size());
    assertEquals(3, hotelEntityList.get(0).getRates().size());
    assertEquals(rateClassification,
        hotelEntityList.get(0).getRates().get(0).getRateClassification());
    assertEquals(rateClassification,
        hotelEntityList.get(0).getRates().get(1).getRateClassification());
    assertEquals(rateClassification,
        hotelEntityList.get(0).getRates().get(2).getRateClassification());

    assertEquals(rateCode,
        hotelEntityList.get(0).getRates().get(0).getRateCode());
    assertEquals(rateCode,
        hotelEntityList.get(0).getRates().get(1).getRateCode());
    assertEquals(rateCode,
        hotelEntityList.get(0).getRates().get(2).getRateCode());
  }

  @Test
  public void WithNoRatePlanCodeAndNoRateCategory() {

    final EventDetail maxLosDe =
        buildEventDetails(
            RateDataElement.MAX_LOS, maxLosOneDay, scopeFrom, scopeFrom);

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(maxLosDe);
    eventHeader.setDetail(eventDetailSet);

    List<String> roomTypes = new ArrayList<>();
    roomTypes.add(roomTypeDouble);
    roomTypes.add(roomTypeSingle);
    roomTypes.add(roomTypeFamily);

    Mockito.when(roomEntityJpaRepository.getHotelTypesByHotelCode(hotelId1))
        .thenReturn(roomTypes);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(1, hotelEntityList.size());
    assertEquals(30, hotelEntityList.get(0).getRates().size());
  }

  @Test
  public void WithInvalidRatePlanCodeAndNoRateCategory() {

    final EventDetail rateCodeDe = buildEventDetails(RateDataElement.RATE_CODE, "COMPTARY");
    final EventDetail maxLosDe =
        buildEventDetails(
            RateDataElement.MAX_LOS, maxLosOneDay, scopeFrom, scopeFrom);

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(maxLosDe);
    eventDetailSet.add(rateCodeDe);
    eventHeader.setDetail(eventDetailSet);

    List<String> roomTypes = new ArrayList<>();
    roomTypes.add(roomTypeDouble);
    roomTypes.add(roomTypeSingle);
    roomTypes.add(roomTypeFamily);

    Mockito.when(roomEntityJpaRepository.getHotelTypesByHotelCode(hotelId1))
        .thenReturn(roomTypes);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(1, hotelEntityList.size());
    assertNull(hotelEntityList.get(0).getRates());
  }

  @Test
  public void closeToArrivalOneDaysTest() {

    final EventDetail rateCodeDe =
        buildEventDetails(RateDataElement.RATE_CODE, rateCode);

    final EventDetail closeToArrival =
        buildEventDetails(
            RateDataElement.ARRIVAL, "C", scopeFrom, scopeFrom);
    final EventDetail roomType = buildEventDetails(RateDataElement.ROOM_TYPE, "Double");

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(rateCodeDe);
    eventDetailSet.add(closeToArrival);
    eventDetailSet.add(roomType);

    eventHeader.setDetail(eventDetailSet);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(hotelEntitiesExpectedForSigneDay, hotelEntityList);
    assertEquals(1, hotelEntityList.size());
    assertEquals(
        hotelEntitiesExpectedForSigneDay.get(0).getRates().size(),
        hotelEntityList.get(0).getRates().size());
    assertEquals(999, hotelEntityList.get(0).getRates().get(0).getMinNights());
    assertEquals(0, hotelEntityList.get(0).getRates().get(0).getMaxNights());

  }

  @Test
  public void closeForBookingOneDaysTest() {

    final EventDetail rateCodeDe =
        buildEventDetails(RateDataElement.RATE_CODE, rateCode);

    final EventDetail closeForBooking =
        buildEventDetails(
            RateDataElement.STAY, "C", scopeFrom, scopeFrom);
    final EventDetail roomType = buildEventDetails(RateDataElement.ROOM_TYPE, "Double");

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(rateCodeDe);
    eventDetailSet.add(closeForBooking);
    eventDetailSet.add(roomType);

    eventHeader.setDetail(eventDetailSet);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(hotelEntitiesExpectedForSigneDay, hotelEntityList);
    assertEquals(1, hotelEntityList.size());
    assertEquals(
        hotelEntitiesExpectedForSigneDay.get(0).getRates().size(),
        hotelEntityList.get(0).getRates().size());
    assertEquals(666, hotelEntityList.get(0).getRates().get(0).getMinNights());
    assertEquals(0, hotelEntityList.get(0).getRates().get(0).getMaxNights());

  }

  @Test
  public void openForBookingMultipleDaysTest() {

    final EventDetail rateCodeDe =
        buildEventDetails(RateDataElement.RATE_CODE, rateCode);

    final EventDetail openForBooking =
        buildEventDetails(
            RateDataElement.STAY, "O", scopeFrom, scopeTo);
    final EventDetail roomType = buildEventDetails(RateDataElement.ROOM_TYPE, "Double");

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(rateCodeDe);
    eventDetailSet.add(openForBooking);
    eventDetailSet.add(roomType);

    eventHeader.setDetail(eventDetailSet);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(hotelEntitiesExpectedForSigneDay.get(0), hotelEntityList.get(0));
    assertEquals(3, hotelEntityList.size());
    assertEquals(
        hotelEntitiesExpectedForSigneDay.get(0).getRates().size(),
        hotelEntityList.get(0).getRates().size());

    final List<RatePlanEntity> ratesFromAllHotels =
        hotelEntityList.stream().flatMap(hotelEntity -> hotelEntity.getRates().stream())
            .collect(Collectors.toList());

    for (RatePlanEntity ratePlanEntity : ratesFromAllHotels) {
      assertEquals(0, ratePlanEntity.getMinNights());
      assertEquals(0, ratePlanEntity.getMaxNights());
    }
  }

  @Test
  public void openForArrivalMultipleDaysTest() {

    final EventDetail rateCodeDe =
        buildEventDetails(RateDataElement.RATE_CODE, rateCode);

    final EventDetail closeToArrival =
        buildEventDetails(
            RateDataElement.ARRIVAL, "O", scopeFrom, scopeTo);
    final EventDetail roomType = buildEventDetails(RateDataElement.ROOM_TYPE, "Double");

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(rateCodeDe);
    eventDetailSet.add(closeToArrival);
    eventDetailSet.add(roomType);

    eventHeader.setDetail(eventDetailSet);

    final List<HotelEntity> hotelEntityList =
        rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader);

    assertEquals(hotelEntitiesExpectedForSigneDay.get(0), hotelEntityList.get(0));
    assertEquals(3, hotelEntityList.size());
    assertEquals(
        hotelEntitiesExpectedForSigneDay.get(0).getRates().size(),
        hotelEntityList.get(0).getRates().size());

    final List<RatePlanEntity> rates =
        hotelEntityList.stream().flatMap(hotelEntity -> hotelEntity.getRates().stream()).collect(
            Collectors.toList());

    for (RatePlanEntity rate: rates) {
      assertEquals(0, rate.getMinNights());
      assertEquals(0, rate.getMaxNights());
    }
  }

  private EventDetail buildEventDetails(
      final String elementName,
      final String newValue
  ) {

    return EventDetail.builder()
        .elementName(elementName)
        .newValue(newValue)
        .build();
  }

  private EventDetail buildEventDetails(
      final String elementName,
      final String newValue,
      final String scopeFrom,
      final String scopeTo
  ) {

    return EventDetail.builder()
        .elementName(elementName)
        .newValue(newValue)
        .scopeFrom(scopeFrom)
        .scopeTo(scopeTo)
        .build();
  }

  private EventHeader buildBusinessEventHeader(
      final String hotelId,
      final String eventName) {

    final Set<EventDetail> eventDetailsSet = new HashSet<>();

    return EventHeader.builder()
        .eventName(eventName)
        .hotelId(hotelId)
        .detail(eventDetailsSet)
        .build();
  }

  private List<RatePlanEntity> buildRatePlanEntities(String hotelId) {

    final List<RatePlanEntity> rateEntityList = new ArrayList<>();
    final RatePlanEntity rateEntity1 = buildRateEntity(hotelId, rateCode,
        maxLosThreeDays);
    rateEntityList.add(rateEntity1);

    return rateEntityList;
  }

  private RatePlanEntity buildRateEntity(
      final String hotelId,
      final String rateCode,
      final String maxNights
  ) {
    return RatePlanEntity.builder()
        .id(rateCode + "_" + hotelId + "_" + roomType)
        .rateClassification(rateClassification)
        .maxNights(Integer.parseInt(maxNights))
        .rateCode(rateCode)
        .build();
  }

  private RatePlanEntity buildRateEntity(
      final String hotelId,
      final String rateCode,
      final String maxNights,
      final String typeOfRoom
  ) {
    return RatePlanEntity.builder()
        .id(rateCode + "_" + hotelId + "_" + typeOfRoom)
        .rateClassification(rateClassification)
        .maxNights(Integer.parseInt(maxNights))
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
