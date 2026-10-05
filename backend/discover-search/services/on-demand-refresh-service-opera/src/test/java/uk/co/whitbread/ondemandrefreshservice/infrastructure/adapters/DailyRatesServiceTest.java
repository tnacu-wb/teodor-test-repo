package uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static uk.co.whitbread.ondemandrefreshservice.utils.MockDataReader.generateMockRsp;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RateCategory;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.DailyRates;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.DailyRatesInput;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.Description;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.DynamicBaseRate;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.PrimaryDetails;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.RatePlanBasedOnRates;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.RatePlanMasterInfo;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.RatePlanSchedule;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.RatePlanScheduleList;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.RoomType;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.rates.opera.HotelDailyRatesClient;

@ExtendWith(MockitoExtension.class)
class DailyRatesServiceTest {

  @Mock
  private HotelDailyRatesClient hotelDailyRatesClient;

  @InjectMocks
  private DailyRatesService dailyRatesService;

  private static final String HOTEL_ID = "TKINPT";
  private static final LocalDate START_DATE = LocalDate.parse("2022-11-01");
  private static final LocalDate END_DATE = LocalDate.parse("2022-11-30");

  @Test
  void getDailyRatesTest() {

    final DailyRates dailyRatesResponse = buildDailyRatesResponse();

    Mockito.doReturn(dailyRatesResponse).when(hotelDailyRatesClient)
        .getDailyRates(anyString(),
            anyString(),
            Mockito.anyLong(),
            anyString(),
            anyString());

    final DailyRatesInput dailyRatesInput1 = buildDailyRatesInput();
    DailyRates dailyRates = dailyRatesService.getDailyRates(dailyRatesInput1);
    assertEquals(HOTEL_ID, dailyRates.getRatePlanScheduleList().getHotelId());

  }

  private DailyRates buildDailyRatesResponse() {

    RatePlanScheduleList ratePlanScheduleList = new RatePlanScheduleList();
    ratePlanScheduleList.setHotelId(HOTEL_ID);

    return DailyRates.builder()
        .ratePlanScheduleList(ratePlanScheduleList)
        .ratePlanMasterInfo(buildRatePlanMasterInfo())
        .links(Collections.emptyList())
        .build();

  }


  private DailyRatesInput buildDailyRatesInput() {
    return DailyRatesInput.builder()
        .ratePlanCode("FLEXRATE")
        .startDate("2023-10-01")
        .endDate("2023-10-25")
        .hotelId("TKINPT")
        .build();
  }

  @Test
  void getOperaDailyRates_Success() throws IOException {
    final DailyRates flexDailyRates = generateMockRsp("/mock_data/flexrate_daily_rates.json",
        DailyRates.class);

    Mockito.doReturn(flexDailyRates).when(hotelDailyRatesClient)
        .getDailyRates(anyString(),
            anyString(),
            Mockito.anyLong(),
            anyString(),
            anyString());

    final Map<String, DailyRates> dailyRates = dailyRatesService.getOperaDailyRates(HOTEL_ID,
        START_DATE, END_DATE);
    assertEquals(1, dailyRates.size());
    assertNotNull(dailyRates.get(RateCategory.FLEXRATE.name()));
  }

  @Test
  void getOperaDailyRates_EmptyRatePlanSchedules_Success() throws IOException {

    final DailyRates advanceDailyRates = generateMockRsp("/mock_data/advance_daily_rates.json",
        DailyRates.class);
    Mockito.doReturn(advanceDailyRates).when(hotelDailyRatesClient)
        .getDailyRates(anyString(),
            anyString(),
            Mockito.anyLong(),
            anyString(),
            anyString());

    final Map<String, DailyRates> dailyRates = dailyRatesService.getOperaDailyRates(HOTEL_ID,
        START_DATE, END_DATE);
    assertEquals(0, dailyRates.size());
  }

  @Test
  void getChainedOperaDailyRates_Success() throws IOException {
    final DailyRates flexDailyRates = generateMockRsp("/mock_data/flexrate_daily_rates_has_more.json",
        DailyRates.class);
    Mockito.doReturn(flexDailyRates).when(hotelDailyRatesClient)
        .getDailyRates(eq(HOTEL_ID),
            anyString(),
            eq(1000L),
            eq(START_DATE.toString()),
            eq(END_DATE.toString()));

    final DailyRates flexDailyRatesNext =
        generateMockRsp("/mock_data/flexrate_daily_rates_has_more.json", DailyRates.class);
    flexDailyRatesNext.getRatePlanScheduleList().setHasMore(false);
    List<RatePlanSchedule> ratePlanScheduleNext = flexDailyRatesNext.getRatePlanScheduleList().getRatePlanSchedule();
    final RatePlanSchedule lastRatePlanScheduleElement = ratePlanScheduleNext.get(
        ratePlanScheduleNext.size() - 1);

    Mockito.doReturn(flexDailyRatesNext).when(hotelDailyRatesClient)
        .getDailyRates(HOTEL_ID,
            "FLEXRATE",
            1000L,
            lastRatePlanScheduleElement.getRatePlanScheduleDetail().getStart(),
            END_DATE.toString());

    final Map<String, DailyRates> dailyRates = dailyRatesService.getOperaDailyRates(HOTEL_ID,
        START_DATE, END_DATE);
    assertEquals(1, dailyRates.size());
    assertNotNull(dailyRates.get(RateCategory.FLEXRATE.name()));
  }


  private RatePlanMasterInfo buildRatePlanMasterInfo() {
    return RatePlanMasterInfo.builder()
        .primaryDetails(buildPrimaryDetails())
        .roomTypeList(buildRoomTypeList())
        .ratePlanBasedOnRates(buildRatePlanBasedOnRates())
        .hotelId(HOTEL_ID)
        .ratePlanCode("RATE_CODE")
        .bARRate(false)
        .tiered(false)
        .daily(false)
        .currencyCode("GBP")
        .complimentary(false)
        .houseUse(false)
        .advancedDailyBase(false)
        .build();

  }

  private static @NotNull List<RatePlanBasedOnRates> buildRatePlanBasedOnRates() {
    return List.of(RatePlanBasedOnRates.builder()
        .dynamicBaseRate(DynamicBaseRate.builder()
            .dependentRatePlans(List.of("dependentRatePlans"))
            .build())
        .build());
  }

  private static @NotNull List<RoomType> buildRoomTypeList() {
    return List.of(RoomType.builder()
        .code("ROOM")
        .description("generic room")
        .pseudo(false)
        .build());
  }

  private static PrimaryDetails buildPrimaryDetails() {
    return PrimaryDetails.builder()
        .description(buildDescription())
        .startSellDate("01/01/2022")
        .endSellDate("02/01/2022")
        .privilegedRate(false)
        .privilegedRateRestriction(false)
        .lockStatus("lockStatus")
        .sellSequence(1)
        .build();
  }

  private static Description buildDescription() {
    return Description.builder()
        .defaultText("default text")
        .translatedTexts(List.of("translatedTexts"))
        .build();
  }
}
