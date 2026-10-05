package uk.co.whitbread.ohip.domain.logic;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.tracing.Tracer;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.exceptions.ParameterMismatchException;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchCriteriaV2;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityRoomSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilitySearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.BookingChannel;
import uk.co.whitbread.ohip.domain.model.availability.in.HotelInventoryRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.ItemInventoryRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.MultiHotelAvailabilityRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.MultiHotelAvailabilityRequestV2;
import uk.co.whitbread.ohip.domain.model.availability.in.Rate;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeRoomInfoCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RateV2;
import uk.co.whitbread.ohip.domain.model.availability.in.RestrictionsByDateRangeSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.Room;
import uk.co.whitbread.ohip.domain.model.availability.in.RoomPriceBreakdownRequest;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityByIdsResult;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityByIdsResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityDailyPrice;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRequest;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityResult;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoom;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomPriceBreakdown;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomRate;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomType;
import uk.co.whitbread.ohip.domain.model.availability.out.HotelAvailabilityResult;
import uk.co.whitbread.ohip.domain.model.availability.out.HotelAvailabilityResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.HotelInventoryRoomType;
import uk.co.whitbread.ohip.domain.model.availability.out.InventoryAvailability;
import uk.co.whitbread.ohip.domain.model.availability.out.ItemInventory;
import uk.co.whitbread.ohip.domain.model.availability.out.ItemInventoryResponse;
import uk.co.whitbread.ohip.domain.model.availability.out.MealsIncluded;
import uk.co.whitbread.ohip.domain.model.availability.out.MultiAvailabilityResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.PackageInfo;
import uk.co.whitbread.ohip.domain.model.availability.out.PriceInfo;
import uk.co.whitbread.ohip.domain.model.availability.out.RateCodePricingResult;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomLevelInventory;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomPriceBreakdownResult;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomRate;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomRateInfo;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomRateInfoV2;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomStay;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomType;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomTypeV2;
import uk.co.whitbread.ohip.domain.model.availability.out.StatisticsDateItem;
import uk.co.whitbread.ohip.domain.model.availability.out.StatisticsInventoryItem;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.domain.model.rates.out.DynamicBaseRate;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlanBasedOnRate;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlanInfoResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlans;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitution;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitutionRequestDetails;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.RatePlansOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Slf4j
@ExtendWith(MockitoExtension.class)
class HotelAvailabilityInPortImplTest {

  public static final String AVAILABLE_ROOMS = "AvailableRooms";
  public static final String PHYSICAL_ROOMS = "PhysicalRooms";
  public static final String PROMOTION_CODE = "PROMO";
  public static final BigDecimal ROOM_NET_AMOUNT = new BigDecimal("118");
  public static final BigDecimal ROOM_GROSS_AMOUNT = new BigDecimal("98");
  public static final BigDecimal ROOM_TAX_AMOUNT = new BigDecimal("20");
  public static final BigDecimal ROOM_NET_AMOUNT_SMALL = new BigDecimal("95");
  public static final BigDecimal ROOM_GROSS_AMOUNT_SMALL = new BigDecimal("80");
  public static final BigDecimal ROOM_TAX_AMOUNT_SMALL = new BigDecimal("15");
  public static final BigDecimal PROMOTION_ROOM_NET_AMOUNT = new BigDecimal("100");
  public static final BigDecimal PROMOTION_ROOM_GROSS_AMOUNT = new BigDecimal("85");
  public static final BigDecimal PROMOTION_ROOM_TAX_AMOUNT = new BigDecimal("15");
  private static final BigDecimal EFFECTIVE_RATE_AMOUNT = new BigDecimal(100);
  private static final BigDecimal EFFECTIVE_RATE_AMOUNT_SMALL = new BigDecimal(100);

  private HotelAvailabilityInPortImpl hotelAvailability;
  private ExecutorService restrictionsExecutorService;
  private final Executor availabilityExecutor = Runnable::run;

  @Mock
  private MealsIncludedConfigProperties mealsConfig;

  @Mock
  private HotelAvailabilityOutPort hotelBookingPort;

  @Mock
  private RulesAgentOutPort rulesAgentOutPort;

  @Mock
  private AvailabilityConfigProperties availabilityProperties;

  @Mock
  private RestrictionsByDateRangeSearchCriteria restrictionsByDateRangeSearchCriteriaMock;
  
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Mock
  private RatePlansOutPort ratePlansOutPort;


  @Spy
  private ConcurrentTracer concurrentTracer = new ConcurrentTracer(Tracer.NOOP);

  @BeforeEach
  void init() {
    restrictionsExecutorService = Executors.newFixedThreadPool(2); // Use a real executor for testing
    hotelAvailability = new HotelAvailabilityInPortImpl(hotelBookingPort, rulesAgentOutPort, mealsConfig,
        availabilityProperties, concurrentTracer, restrictionsExecutorService, unleashWrapper, ratePlansOutPort,
        availabilityExecutor);
  }

  @SneakyThrows
  @Test
  void getHotelAvailability__ShouldReturnOk() {

    // Arrange
    AvailabilitySearchCriteria hasc = createHotelAvailabilitySearchCriteria();

    AvailabilityResult hotelAvailabilityResult = createHotelAvailabilityResult();

    when(hotelBookingPort.getHotelAvailability(any())).
        thenReturn(hotelAvailabilityResult);
    when(rulesAgentOutPort.getRoomSubstitution(any(), any(), any(), any())).thenReturn(
        mockRoomSubstitutionResponse(List.of("DBLWIN")));
    when(hotelBookingPort.getHotelRoomPriceBreakdown(any(AvailabilityRoomSearchCriteria.class)))
        .thenReturn(createPriceBreakdownList());

    when(hotelBookingPort.getHotelItemsInventory(
        createItemInventoryRequest(hotelAvailabilityResult.getHotelId(),
            hotelAvailabilityResult.getStartDate(), hotelAvailabilityResult.getEndDate())))
        .thenReturn(createItemInventoryResponse());
    //FIXME Temporarily disabled - see https://whitbreadis.atlassian.net/browse/OB-899
    //    when(hotelBookingPort.getHotelInventoryStatistics(any())).thenReturn(
    //        mockInventoryStatistics_LimitedAvailability_false());
    //    when(availabilityProperties.getMinimumAvailability()).thenReturn(10);

    // Act
    AvailabilityResult foundHotelAvailabilityResult =
        this.hotelAvailability.getHotelAvailability(hasc);

    // Assert
    assertThat(foundHotelAvailabilityResult, notNullValue());
    assertThat(foundHotelAvailabilityResult.getHotelId(), is(hasc.getHotelId()));
    assertThat(foundHotelAvailabilityResult.getStartDate(), is("2022-03-01"));
    assertThat(foundHotelAvailabilityResult.getEndDate(), is("2022-03-03"));
    assertThat(foundHotelAvailabilityResult.getRoomRates(), notNullValue());
    assertThat(foundHotelAvailabilityResult.getRoomRates(), hasSize(greaterThan(0)));

    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0), notNullValue());
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0).getRatePlanCode(),
        notNullValue());
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes(),
        hasSize(greaterThan(0)));
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0),
        notNullValue());
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0).getRatePlanCode(), is("DAILY"));
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
        .getCotAvailable(), is(Boolean.TRUE));
    assertThat(foundHotelAvailabilityResult.isLimitedAvailability(), is(Boolean.FALSE));
  }

  @SneakyThrows
  @Test
  void getHotelAvailability__ShouldReturnPriceBreakdown() {

    // Arrange
    AvailabilitySearchCriteria hasc = createHotelAvailabilitySearchCriteria();

    AvailabilityResult hotelAvailabilityResult = createHotelAvailabilityResult();

    when(hotelBookingPort.getHotelAvailability(any())).
        thenReturn(hotelAvailabilityResult);
    when(rulesAgentOutPort.getRoomSubstitution(any(), any(), any(), any())).thenReturn(
        mockRoomSubstitutionResponse(List.of("DBLWIN")));
    when(hotelBookingPort.getHotelRoomPriceBreakdown(any(AvailabilityRoomSearchCriteria.class)))
        .thenReturn(createPriceBreakdownList());

    when(hotelBookingPort.getHotelItemsInventory(
        createItemInventoryRequest(hotelAvailabilityResult.getHotelId(),
            hotelAvailabilityResult.getStartDate(), hotelAvailabilityResult.getEndDate())))
        .thenReturn(createItemInventoryResponse());

    // Act
    AvailabilityResult foundHotelAvailabilityResult =
        this.hotelAvailability.getHotelAvailability(hasc);

    // Assert
    assertThat(foundHotelAvailabilityResult, notNullValue());
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0)
        .getRooms().get(0).getRoomPriceBreakdown(), notNullValue());
    assertThat(
        foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getCurrencyCode(), is("GBP"));
    assertThat(
        foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getTotalNetAmount(), is(new BigDecimal("118")));
    assertThat(
        foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getTotalGrossAmount(), is(new BigDecimal("98")));
    assertThat(
        foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getTotalTaxAmount(), is(new BigDecimal("20")));
    assertThat(
        foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getEffectiveRateAmount(), is(EFFECTIVE_RATE_AMOUNT_SMALL));
    assertThat(
        foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getDailyPrices(), notNullValue());
    assertThat(
        foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getDailyPrices(), hasSize(2));
    assertThat(
        foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getDailyPrices().get(0), notNullValue());
    assertThat(
        foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getDailyPrices().get(0).getNetPrice(),
        is(new BigDecimal("59")));
    assertThat(
        foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getDailyPrices().get(0).getDate(),
        is("2022-03-01"));

    assertThat(
        foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getDailyPrices().get(1), notNullValue());
    assertThat(
        foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getDailyPrices().get(1).getNetPrice(),
        is(new BigDecimal("59")));
    assertThat(
        foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getDailyPrices().get(1).getDate(),
        is("2022-03-02"));
  }

  @SneakyThrows
  @Test
  void getHotelAvailability_includePromotionRates_hideStandard__ShouldReturnOk() {

    // Arrange
    AvailabilitySearchCriteria availabilitySearchCriteria = createHotelAvailabilitySearchCriteria();
    availabilitySearchCriteria.setPromotionCode(PROMOTION_CODE);
    availabilitySearchCriteria.setChannel("PI");

    AvailabilityResult hotelAvailabilityResult = createHotelAvailabilityResult();
    AvailabilityResult hotelAvailabilityPromotionResult = createHotelAvailabilityPromotionResult();

    when(hotelBookingPort.getHotelAvailability(any(AvailabilityRequest.class))).
        thenAnswer(invocation -> {
          AvailabilityRequest request = invocation.getArgument(0);
          if (PROMOTION_CODE.equals(request.getPromotionCode())) {
            return hotelAvailabilityPromotionResult;
          } else {
            return hotelAvailabilityResult;
          }
        });
    when(rulesAgentOutPort.getRoomSubstitution(any(), any(), any(), any())).thenReturn(
        mockRoomSubstitutionResponse(List.of("DBLWIN")));
    when(hotelBookingPort.getHotelRoomPriceBreakdown(any(AvailabilityRoomSearchCriteria.class)))
        .thenAnswer(invocation -> {
          AvailabilityRoomSearchCriteria request = invocation.getArgument(0);
          if (request.getRatePlanCode().equals("PROMOTION")) {
            log.info("Returning promotion price breakdown for rate plan PROMOTION and room type: {}",
                request.getRoomType());
            return createPriceBreakdownList(PROMOTION_ROOM_NET_AMOUNT, PROMOTION_ROOM_GROSS_AMOUNT,
                PROMOTION_ROOM_TAX_AMOUNT);
          } else if (request.getRatePlanCode().equals("STANDARD") && request.getRoomType().equals("DBLDBL")) {
            log.info("Returning small price breakdown for rate plan STANDARD and room type DBLDBL");
            return createPriceBreakdownList(ROOM_NET_AMOUNT_SMALL, ROOM_GROSS_AMOUNT_SMALL,
                ROOM_TAX_AMOUNT_SMALL);
          } else {
            log.info("Returning regular price breakdown for rate plan: {} and room type: {}",
                request.getRatePlanCode(), request.getRoomType());
            return createPriceBreakdownList();
          }
        });

    when(hotelBookingPort.getHotelItemsInventory(
        createItemInventoryRequest(hotelAvailabilityResult.getHotelId(),
            hotelAvailabilityResult.getStartDate(), hotelAvailabilityResult.getEndDate())))
        .thenReturn(createItemInventoryResponse());

    RatePlanInfoResponse ratePlanInfoResponse = createSampleRatePlanInfoResponse();
    when(ratePlansOutPort.getRatePlanInfo(anyString(), anyString())).thenReturn(ratePlanInfoResponse);

    // Act
    AvailabilityResult foundHotelAvailabilityResult =
        this.hotelAvailability.getHotelAvailability(availabilitySearchCriteria);

    // Assert
    assertThat(foundHotelAvailabilityResult, notNullValue());
    assertThat(foundHotelAvailabilityResult.getHotelId(), is(availabilitySearchCriteria.getHotelId()));
    assertThat(foundHotelAvailabilityResult.getStartDate(), is("2022-03-01"));
    assertThat(foundHotelAvailabilityResult.getEndDate(), is("2022-03-03"));
    assertThat(foundHotelAvailabilityResult.getRoomRates(), notNullValue());
    assertThat(foundHotelAvailabilityResult.getRoomRates(), hasSize(2));

    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0).getRatePlanCode(), is("DAILY"));
    var promotionRoomRate = foundHotelAvailabilityResult.getRoomRates().get(1);
    assertThat(promotionRoomRate.getRatePlanCode(), is("PROMOTION"));
    var dbRoomType = promotionRoomRate.getRoomTypes().get(0);
    var doubleRoom = dbRoomType.getRooms().get(0);
    var dbldblRoom = dbRoomType.getRooms().get(1);
    assertEquals(PROMOTION_ROOM_NET_AMOUNT, doubleRoom.getRoomPriceBreakdown().getTotalNetAmount());
    assertEquals(PROMOTION_ROOM_GROSS_AMOUNT, doubleRoom.getRoomPriceBreakdown().getTotalGrossAmount());
    assertEquals(PROMOTION_ROOM_TAX_AMOUNT, doubleRoom.getRoomPriceBreakdown().getTotalTaxAmount());
    assertEquals(ROOM_NET_AMOUNT, doubleRoom.getRoomPriceBreakdown().getBaseRateAmount());
    assertEquals(PROMOTION_ROOM_NET_AMOUNT, dbldblRoom.getRoomPriceBreakdown().getTotalNetAmount());
    assertEquals(PROMOTION_ROOM_GROSS_AMOUNT, dbldblRoom.getRoomPriceBreakdown().getTotalGrossAmount());
    assertEquals(PROMOTION_ROOM_TAX_AMOUNT, dbldblRoom.getRoomPriceBreakdown().getTotalTaxAmount());
  }

  @SneakyThrows
  @Test
  void getHotelAvailability_invalidPromotionCode__ShouldReturnRegularRates() {

    // Arrange
    AvailabilitySearchCriteria availabilitySearchCriteria = createHotelAvailabilitySearchCriteria();
    availabilitySearchCriteria.setPromotionCode("INVALID_PROMO");
    availabilitySearchCriteria.setChannel("PI");

    AvailabilityResult hotelAvailabilityResult = createHotelAvailabilityResult();
    AvailabilityResult hotelAvailabilityPromotionResult = createHotelAvailabilityResultEmptyRoomRateList();

    when(hotelBookingPort.getHotelAvailability(any(AvailabilityRequest.class))).
        thenAnswer(invocation -> {
          AvailabilityRequest request = invocation.getArgument(0);
          if ("INVALID_PROMO".equals(request.getPromotionCode())) {
            return hotelAvailabilityPromotionResult;
          } else {
            return hotelAvailabilityResult;
          }
        });
    when(rulesAgentOutPort.getRoomSubstitution(any(), any(), any(), any())).thenReturn(
        mockRoomSubstitutionResponse(List.of("DBLWIN")));

    when(hotelBookingPort.getHotelRoomPriceBreakdown(any(AvailabilityRoomSearchCriteria.class)))
        .thenReturn(createPriceBreakdownList());

    when(hotelBookingPort.getHotelItemsInventory(
        createItemInventoryRequest(hotelAvailabilityResult.getHotelId(),
            hotelAvailabilityResult.getStartDate(), hotelAvailabilityResult.getEndDate())))
        .thenReturn(createItemInventoryResponse());

    // Act
    AvailabilityResult foundHotelAvailabilityResult =
        this.hotelAvailability.getHotelAvailability(availabilitySearchCriteria);

    // Assert
    assertThat(foundHotelAvailabilityResult, notNullValue());
    assertThat(foundHotelAvailabilityResult.getHotelId(), is(availabilitySearchCriteria.getHotelId()));
    assertThat(foundHotelAvailabilityResult.getStartDate(), is("2022-03-01"));
    assertThat(foundHotelAvailabilityResult.getEndDate(), is("2022-03-03"));
    assertThat(foundHotelAvailabilityResult.getRoomRates(), notNullValue());
    assertThat(foundHotelAvailabilityResult.getRoomRates(), hasSize(2));

    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0).getRatePlanCode(), is("DAILY"));
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(1).getRatePlanCode(), is("STANDARD"));
  }

  @SneakyThrows
  @Test
  void getHotelAvailability_promotionCodeAvailable_notPiChannel__ShouldReturnRegularRates() {

    // Arrange
    AvailabilitySearchCriteria availabilitySearchCriteria = createHotelAvailabilitySearchCriteria();
    availabilitySearchCriteria.setPromotionCode(PROMOTION_CODE);
    availabilitySearchCriteria.setChannel("APPS");

    AvailabilityResult hotelAvailabilityResult = createHotelAvailabilityResult();

    when(hotelBookingPort.getHotelAvailability(any(AvailabilityRequest.class)))
        .thenReturn(hotelAvailabilityResult);
    when(rulesAgentOutPort.getRoomSubstitution(any(), any(), any(), any())).thenReturn(
        mockRoomSubstitutionResponse(List.of("DBLWIN")));
    when(hotelBookingPort.getHotelRoomPriceBreakdown(any(AvailabilityRoomSearchCriteria.class)))
        .thenReturn(createPriceBreakdownList());

    when(hotelBookingPort.getHotelItemsInventory(
        createItemInventoryRequest(hotelAvailabilityResult.getHotelId(),
            hotelAvailabilityResult.getStartDate(), hotelAvailabilityResult.getEndDate())))
        .thenReturn(createItemInventoryResponse());

    // Act
    AvailabilityResult foundHotelAvailabilityResult =
        this.hotelAvailability.getHotelAvailability(availabilitySearchCriteria);

    // Assert
    assertThat(foundHotelAvailabilityResult, notNullValue());
    assertThat(foundHotelAvailabilityResult.getHotelId(), is(availabilitySearchCriteria.getHotelId()));
    assertThat(foundHotelAvailabilityResult.getStartDate(), is("2022-03-01"));
    assertThat(foundHotelAvailabilityResult.getEndDate(), is("2022-03-03"));
    assertThat(foundHotelAvailabilityResult.getRoomRates(), notNullValue());
    assertThat(foundHotelAvailabilityResult.getRoomRates(), hasSize(2));

    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0).getRatePlanCode(), is("DAILY"));
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(1).getRatePlanCode(), is("STANDARD"));
  }
  
  @SneakyThrows
  @Test
  void getHotelAvailability_hideFlexRateForBB__ShouldReturnOk() {
    
    // Arrange
    AvailabilitySearchCriteria availabilitySearchCriteria = createHotelAvailabilitySearchCriteria();
    availabilitySearchCriteria.setChannel("BB");
    availabilitySearchCriteria.setPromotionCode("PROMO");

    AvailabilityResult hotelAvailabilityResult = createHotelAvailabilityResult_BB();
    AvailabilityResult hotelAvailabilityPromotionResult = createHotelAvailabilityPromotionResult();
    
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getFlexRateStrikethroughBB())).thenReturn(false);
    
    when(hotelBookingPort.getHotelAvailability(any(AvailabilityRequest.class))).
        thenAnswer(invocation -> {
          AvailabilityRequest request = invocation.getArgument(0);
          if (PROMOTION_CODE.equals(request.getPromotionCode())) {
            return hotelAvailabilityPromotionResult;
          } else {
            return hotelAvailabilityResult;
          }
        });
    when(rulesAgentOutPort.getRoomSubstitution(any(), any(), any(), any())).thenReturn(
        mockRoomSubstitutionResponse(List.of("DBLWIN")));
    when(hotelBookingPort.getHotelRoomPriceBreakdown(any(AvailabilityRoomSearchCriteria.class)))
        .thenAnswer(invocation -> {
          AvailabilityRoomSearchCriteria request = invocation.getArgument(0);
          if (request.getRatePlanCode().equals("PROMOTION")) {
            log.info("Returning promotion price breakdown for rate plan PROMOTION and room type: {}",
                request.getRoomType());
            return createPriceBreakdownList(PROMOTION_ROOM_NET_AMOUNT, PROMOTION_ROOM_GROSS_AMOUNT,
                PROMOTION_ROOM_TAX_AMOUNT);
          } else if (request.getRatePlanCode().equals("STANDARD") && request.getRoomType().equals("DBLDBL")) {
            log.info("Returning small price breakdown for rate plan STANDARD and room type DBLDBL");
            return createPriceBreakdownList(ROOM_NET_AMOUNT_SMALL, ROOM_GROSS_AMOUNT_SMALL,
                ROOM_TAX_AMOUNT_SMALL);
          } else {
            log.info("Returning regular price breakdown for rate plan: {} and room type: {}",
                request.getRatePlanCode(), request.getRoomType());
            return createPriceBreakdownList();
          }
        });
    
    when(hotelBookingPort.getHotelItemsInventory(
        createItemInventoryRequest(hotelAvailabilityResult.getHotelId(),
            hotelAvailabilityResult.getStartDate(), hotelAvailabilityResult.getEndDate())))
        .thenReturn(createItemInventoryResponse());
    
    // Act
    AvailabilityResult foundHotelAvailabilityResult =
        this.hotelAvailability.getHotelAvailability(availabilitySearchCriteria);
    
    // Assert
    assertThat(foundHotelAvailabilityResult, notNullValue());
    assertThat(foundHotelAvailabilityResult.getHotelId(), is(availabilitySearchCriteria.getHotelId()));
    assertThat(foundHotelAvailabilityResult.getStartDate(), is("2022-03-01"));
    assertThat(foundHotelAvailabilityResult.getEndDate(), is("2022-03-03"));
    assertThat(foundHotelAvailabilityResult.getRoomRates(), notNullValue());
    assertThat("Expected 3 room rates including promotional rate",
        foundHotelAvailabilityResult.getRoomRates(), hasSize(3));
    
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0).getRatePlanCode(), is("BUSIFLEX"));
    var promotionRoomRate = foundHotelAvailabilityResult.getRoomRates().get(1);
    assertThat(promotionRoomRate.getRatePlanCode(), is("FLEXRATE"));
    var dbRoomType = promotionRoomRate.getRoomTypes().get(0);
    var doubleRoom = dbRoomType.getRooms().get(0);
    var dbldblRoom = dbRoomType.getRooms().get(1);
    
    
    //asserting to null as amount for BUSIFLEX and base rate code - FLEXRATE are equal
    assertEquals(null, doubleRoom.getRoomPriceBreakdown().getBaseRateAmount());
  }

  @SneakyThrows
  @Test
  void getHotelAvailability_distribution__ShouldReturnOk() {

    // Arrange
    AvailabilitySearchCriteria hasc = createHotelAvailabilitySearchCriteria_distribution();

    AvailabilityResult hotelAvailabilityResult = createHotelAvailabilityResult_distribution();

    when(mealsConfig.getMealConfig(any())).thenReturn(createMealsIncluded());
    when(hotelBookingPort.getHotelAvailability(any())).thenReturn(hotelAvailabilityResult);
    when(rulesAgentOutPort.getRoomSubstitution(any(), any(), any(), any())).thenReturn(
        mockRoomSubstitutionResponse(List.of("DBLWIN")));

    when(hotelBookingPort.getHotelRoomPriceBreakdown(
            createRoomAvailabilityCriteria(hotelAvailabilityResult.getHotelId(),
                    hotelAvailabilityResult.getRoomRates().get(0).getRatePlanCode(),
                    hotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
                            .getPmsRoomType(), hasc.getArrivalDate(),
                    hasc.getDepartureDate(), hasc.getAdults().get(0), hasc.getChildren().get(0))))
            .thenReturn(createPriceBreakdownList());

    when(hotelBookingPort.getHotelItemsInventory(
            createItemInventoryRequest(hotelAvailabilityResult.getHotelId(),
                    hotelAvailabilityResult.getStartDate(), hotelAvailabilityResult.getEndDate())))
            .thenReturn(createItemInventoryResponse());

    // Act
    AvailabilityResult foundHotelAvailabilityResult =
            this.hotelAvailability.getHotelAvailability(hasc);

    // Assert
    assertThat(foundHotelAvailabilityResult, notNullValue());
    assertThat(foundHotelAvailabilityResult.getHotelId(), is(hasc.getHotelId()));
    assertThat(foundHotelAvailabilityResult.getStartDate(), is("2022-03-01"));
    assertThat(foundHotelAvailabilityResult.getEndDate(), is("2022-03-03"));
    assertThat(foundHotelAvailabilityResult.getRoomRates(), notNullValue());
    assertThat(foundHotelAvailabilityResult.getRoomRates(), hasSize(greaterThan(0)));

    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0), notNullValue());
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0).getRatePlanCode(),
            notNullValue());
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes(),
            hasSize(greaterThan(0)));
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0),
            notNullValue());
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0).getRatePlanCode(), is("DAILY"));
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0)
            .getRooms().get(0).getRoomPriceBreakdown(), notNullValue());
    assertThat(
            foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
                    .getRoomPriceBreakdown().getCurrencyCode(), is("GBP"));
    assertThat(
            foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
                    .getRoomPriceBreakdown().getTotalNetAmount(), is(new BigDecimal("118")));
    assertThat(
            foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
                    .getRoomPriceBreakdown().getTotalGrossAmount(), is(new BigDecimal("98")));
    assertThat(
            foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
                    .getRoomPriceBreakdown().getTotalTaxAmount(), is(new BigDecimal("20")));

    assertThat(
            foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
                    .getRoomPriceBreakdown().getDailyPrices(), notNullValue());
    assertThat(
            foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
                    .getRoomPriceBreakdown().getDailyPrices(), hasSize(2));
    assertThat(
            foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
                    .getRoomPriceBreakdown().getDailyPrices().get(0), notNullValue());
    assertThat(
            foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
                    .getRoomPriceBreakdown().getDailyPrices().get(0).getNetPrice(),
            is(new BigDecimal("59")));
    assertThat(
            foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
                    .getRoomPriceBreakdown().getDailyPrices().get(0).getDate(),
            is("2022-03-01"));

    assertThat(
            foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
                    .getRoomPriceBreakdown().getDailyPrices().get(1), notNullValue());
    assertThat(
            foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
                    .getRoomPriceBreakdown().getDailyPrices().get(1).getNetPrice(),
            is(new BigDecimal("59")));
    assertThat(
            foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
                    .getRoomPriceBreakdown().getDailyPrices().get(1).getDate(),
            is("2022-03-02"));
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getCotAvailable(), is(Boolean.TRUE));
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0)
            .getRoomTypes().get(0).getRooms().get(0).getMealsIncluded().getMealName(),
        is("Pizza menu"));
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0)
        .getRoomTypes().get(0).getRooms().get(0).getMealsIncluded().isBreakfast(), is(true));
    assertThat(foundHotelAvailabilityResult.getRoomRates().get(0)
        .getRoomTypes().get(0).getRooms().get(0).getMealsIncluded().isDinner(), is(false));
  }

  @Test
  void getMultiHotelAvailability__ShouldReturnOk() {
    //Arrange
    var availabilitySearch = generateMultiHotelAvailabilityRequest();
    when(rulesAgentOutPort.getRoomSubstitution("SB", 1, 0, "TestChannel")).thenReturn(
        mockRoomSubstitutionResponse(Arrays.asList("DBLWIN", "WINCMB")));
    when(rulesAgentOutPort.getRoomSubstitution("DB", 2, 0, "TestChannel")).thenReturn(
        mockRoomSubstitutionResponse(Arrays.asList("DBLWIN", "WINCMB")));
    when(rulesAgentOutPort.getRoomSubstitution("DB", 1, 0, "TestChannel")).thenReturn(
        mockRoomSubstitutionResponse(Arrays.asList("DBLWIN", "WINCMB")));

    when(hotelBookingPort.getMultiHotelAvailabilities(any(), any())).thenReturn(generateHotelAvailabilityResult());
    //Act
    var response = hotelAvailability.getMultiHotelAvailability(availabilitySearch);
    //Assert
    assertEquals(response.getHotelAvailabilityResults().get(0).getHotelId(), "TestHotelId");
    assertFalse(response.getHotelAvailabilityResults().get(0).getAvailable());
    assertEquals(response.getHotelAvailabilityResults().get(0).getRoomTypes().size(), 2);
  }

  @Test
  void getMultiHotelAvailabilityWithCompanyId__ShouldReturnOk() {
    //Arrange
    var availabilitySearch = generateMultiHotelAvailabilityCompanyIdRequest();
    when(rulesAgentOutPort.getRoomSubstitution("SB", 1, 0, "TestChannel")).thenReturn(
        mockRoomSubstitutionResponse(Arrays.asList("DBLWIN", "WINCMB")));
    when(rulesAgentOutPort.getRoomSubstitution("DB", 2, 0, "TestChannel")).thenReturn(
        mockRoomSubstitutionResponse(Arrays.asList("DBLWIN", "WINCMB")));
    when(rulesAgentOutPort.getRoomSubstitution("DB", 1, 0, "TestChannel")).thenReturn(
        mockRoomSubstitutionResponse(Arrays.asList("DBLWIN", "WINCMB")));
    when(hotelBookingPort.getMultiHotelAvailabilities(any(), any())).thenReturn(generateHotelAvailabilityResult());
    //Act
    var response = hotelAvailability.getMultiHotelAvailability(availabilitySearch);
    //Assert
    assertEquals(response.getHotelAvailabilityResults().get(0).getHotelId(), "TestHotelId");
    assertFalse(response.getHotelAvailabilityResults().get(0).getAvailable());
    assertEquals(response.getHotelAvailabilityResults().get(0).getRoomTypes().size(), 2);
  }

  @SneakyThrows
  @Test
  void getHotelAvailabilityByIds__ShouldReturnOk() {
    //Arrange
    var availabilitySearch = generateAvailabilityByIdsSearchCriteria();
    when(hotelBookingPort.getHotelAvailabilityByIds(any())).thenReturn(createHotelAvailabilityByIdsResult());
    when(rulesAgentOutPort.getRoomSubstitution(any(), any(), any(), any())).thenReturn(
        mockRoomSubstitutionResponse(List.of("DBLWIN")));
    when(hotelBookingPort.getHotelRoomPriceBreakdown(any()))
        .thenReturn(createPriceBreakdownList());

    //Act
    var response = hotelAvailability.getHotelAvailabilityByIds(availabilitySearch);
    //Assert
    assertEquals("LONEUS", response.getHotelAvailability().get(0).getHotelId());
    assertEquals(1, response.getHotelAvailability().get(0).getRoomRates().size());
  }

  @SneakyThrows
  @Test
  void getHotelAvailabilityByIds__withPms__ShouldReturnOk() {
    //Arrange
    var availabilitySearch = generateAvailabilityByIdsSearchCriteria();
    availabilitySearch.setPmsRoomTypes(Arrays.asList("DBLWIN"));
    when(hotelBookingPort.getHotelAvailabilityByIds(any())).thenReturn(createHotelAvailabilityByIdsResult());
    when(hotelBookingPort.getHotelRoomPriceBreakdown(any()))
        .thenReturn(createPriceBreakdownList());

    //Act
    var response = hotelAvailability.getHotelAvailabilityByIds(availabilitySearch);
    //Assert
    assertEquals("LONEUS", response.getHotelAvailability().get(0).getHotelId());
    assertEquals(1, response.getHotelAvailability().get(0).getRoomRates().size());
  }

  @Test
  void getHotelAvailabilityByIds__InvalidParams() {
    //Arrange
    var availabilitySearch = generateAvailabilityByIdsSearchCriteria();
    availabilitySearch.setAdults(emptyList());

    //Act
    assertThrows(ParameterMismatchException.class, () -> hotelAvailability.getHotelAvailabilityByIds(availabilitySearch));
  }

  @Test
  void getHotelAvailabilityByIds__InvalidParams__NoCompanyId() {
    //Arrange
    var availabilitySearch = generateAvailabilityByIdsSearchCriteria();
    availabilitySearch.setGlobalCompanyId(null);

    //Act
    assertThrows(ParameterMismatchException.class, () -> hotelAvailability.getHotelAvailabilityByIds(availabilitySearch));
  }

  @Test
  void getMultiHotelAvailabilityV2__ShouldReturnOk() {
    //Arrange
    var availabilitySearch = generateMultiHotelAvailabilityRequestV2();
    when(rulesAgentOutPort.getRoomSubstitution(any(), any(), any(), eq("TestChannel"))).thenReturn(
        mockRoomSubstitutionResponse(Arrays.asList("DOUBLE")));
    when(hotelBookingPort.getMultiHotelAvailabilitiesV2(any())).thenReturn(generateHotelAvailabilityResultV2());
    //Act
    var response = hotelAvailability.getMultiHotelAvailabilityV2(availabilitySearch);
    //Assert
    assertEquals(response.getHotelAvailabilityResults().get(0).getHotelId(), "TestHotelId");
    assertTrue(response.getHotelAvailabilityResults().get(0).getAvailable());
  }

  private MultiAvailabilityResultV2 generateHotelAvailabilityResultV2() {
    return MultiAvailabilityResultV2.builder().hotelAvailabilityResults(List.of(
        HotelAvailabilityResultV2.builder()
            .available(true)
            .hotelId("TestHotelId")
            .build()))
        .build();
  }

  private MultiHotelAvailabilityRequestV2 generateMultiHotelAvailabilityRequestV2() {
    return MultiHotelAvailabilityRequestV2.builder()
        .bookingChannel(BookingChannel.builder().channel("TestChannel").build())
        .hotelIds(List.of("TestHotelId"))
        .rooms(List.of(Room.builder().tag("DB").adults(1).children(1).build()))
        .build();
  }

  @Test
  void getHotelAvailabilityByIdsV2__ShouldReturnOk() {
    //Arrange
    var availabilitySearch = generateAvailabilityByIdsSearchCriteriaV2();
    when(hotelBookingPort.getHotelAvailabilityByIdsV2(any())).thenReturn(createHotelAvailabilityByIdsResultV2());
    when(rulesAgentOutPort.getRoomSubstitution(any(), any(), any(), eq("DISTR"))).thenReturn(
        mockRoomSubstitutionResponse(Arrays.asList("DOUBLE")));

    //Act
    var response = hotelAvailability.getHotelAvailabilityByIdsV2(availabilitySearch);
    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getHotelAvailability().get(0).getHotelId(), is("LONEUS"));
    assertThat(response.getHotelAvailability().get(0)
        .getRoomStays().get(0)
        .getRoomTypes().get(0)
        .getRoomType(), is("DOUBLE"));
    assertThat(response.getHotelAvailability().get(0)
        .getRoomStays().get(0)
        .getRoomTypes().get(0)
        .getRoomRates().get(0)
        .getRatePlanCode(), is("FLEXRATE"));
    assertThat(response.getHotelAvailability().get(0)
        .getRoomStays().get(0)
        .getRoomTypes().get(0)
        .getRoomRates().get(0)
        .getRoomRateInfo().getPackages().get(0)
        .getCode(), is("MDP"));
    assertThat(response.getHotelAvailability().get(0)
        .getRoomStays().get(0)
        .getRoomTypes().get(0)
        .getRoomRates().get(0)
        .getRoomRateInfo().getPriceInfo().get(0)
        .getAmountAfterTax(), is(BigDecimal.ONE));
  }

  @Test
  void getHotelAvailabilityByIdsV2__ShouldReturnOk_1() {
    //Arrange
    var availabilitySearch = generateAvailabilityByIdsSearchCriteriaV2();
    when(hotelBookingPort.getHotelAvailabilityByIdsV2(any())).thenReturn(createHotelAvailabilityByIdsResultV2ForTwinRoom());
    when(rulesAgentOutPort.getRoomSubstitution(any(), any(), any(), eq("DISTR"))).thenReturn(
        mockRoomSubstitutionResponse(Arrays.asList("DOUBLE")));

    //Act
    var response = hotelAvailability.getHotelAvailabilityByIdsV2(availabilitySearch);
    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getHotelAvailability().get(0).getHotelId(), is("LONEUS"));
    assertNull(response.getHotelAvailability().get(0)
        .getRoomStays().get(0)
        .getRoomTypes().get(0)
        .getAdults());
    assertNull(response.getHotelAvailability().get(0)
        .getRoomStays().get(0)
        .getRoomTypes().get(0)
        .getChildren());
    assertNull(response.getHotelAvailability().get(0)
        .getRoomStays().get(0)
        .getRoomTypes().get(0)
        .getNumberOfRooms());
  }

  private AvailabilityByIdsResultV2 createHotelAvailabilityByIdsResultV2() {
    return AvailabilityByIdsResultV2.builder()
        .hotelAvailability(List.of(
            AvailabilityResultV2.builder()
                .hotelId("LONEUS")
                .roomStays(new ArrayList<>(List.of(
                    RoomStay.builder().roomClass("A")
                        .roomTypes(new ArrayList<>(List.of(
                            RoomTypeV2.builder()
                                .tag("DB")
                                .roomType("DOUBLE")
                                .roomRates(List.of(
                                    RoomRate.builder()
                                        .ratePlanCode("FLEXRATE")
                                        .displaySet("PBF")
                                        .currencyCode("EUR")
                                        .roomRateInfo(RoomRateInfoV2.builder()
                                            .priceInfo(List.of(PriceInfo.builder()
                                                .amountAfterTax(BigDecimal.ONE)
                                                .build()))
                                            .packages(List.of(PackageInfo.builder()
                                                .code("MDP")
                                                .build()))
                                            .build())
                                        .build()))
                                .build())))
                        .build())))
                .build()))
        .build();
  }

  private AvailabilityByIdsResultV2 createHotelAvailabilityByIdsResultV2ForTwinRoom() {
    return AvailabilityByIdsResultV2.builder()
        .hotelAvailability(List.of(
            AvailabilityResultV2.builder()
                .hotelId("LONEUS")
                .roomStays(new ArrayList<>(List.of(
                    RoomStay.builder().roomClass("A")
                        .roomTypes(new ArrayList<>(List.of(
                            RoomTypeV2.builder()
                                .tag("TWIN")
                                .roomType("TWINRM")
                                .roomRates(List.of(
                                    RoomRate.builder()
                                        .ratePlanCode("FLEXRATE")
                                        .displaySet("PBF")
                                        .currencyCode("EUR")
                                        .roomRateInfo(RoomRateInfoV2.builder()
                                            .priceInfo(List.of(PriceInfo.builder()
                                                .amountAfterTax(BigDecimal.ONE)
                                                .build()))
                                            .packages(List.of(PackageInfo.builder()
                                                .code("MDP")
                                                .build()))
                                            .build())
                                        .build()))
                                .build())))
                        .build())))
                .build()))
        .build();
  }

  private AvailabilityByIdsSearchCriteriaV2 generateAvailabilityByIdsSearchCriteriaV2() {
    return AvailabilityByIdsSearchCriteriaV2.builder()
        .hotelIds(List.of("LONEUS"))
        .arrivalDate(LocalDate.of(2023,3,1))
        .departureDate(LocalDate.of(2023,3,2))
        .bookingChannel(BookingChannel.builder().channel("DISTR").build())
        .rooms(List.of(Room.builder().numberOfRooms(1).adults(1).children(1).tag("DB")
                        .roomTypes(Collections.singletonList("roomType")).build(),
            Room.builder().numberOfRooms(1).adults(1).children(0).tag("DB").build()))
        .rates(RateV2.builder().ratePlanCodes(List.of("FLEXRATE")).build())
        .build();
  }

  private ItemInventoryRequest createItemInventoryRequest(String hotelId, String startDate,
      String endDate) {
    return ItemInventoryRequest.builder()
        .hotelId(hotelId)
        .startDate(startDate)
        .endDate(endDate)
        .build();
  }

  private ItemInventoryResponse createItemInventoryResponse() {
    return ItemInventoryResponse.builder()
        .itemsInventory(Collections.singletonList(createItemInventoryMock()))
        .build();
  }

  private ItemInventory createItemInventoryMock() {

    return ItemInventory.builder()
        .name("Cot")
        .description("Cot")
        .inventories(Arrays.asList(createInventoryAvailabilityMock("2022-03-01"),
            createInventoryAvailabilityMock("2022-03-02"),
            createInventoryAvailabilityMock("2022-03-03")))
        .build();

  }

  private InventoryAvailability createInventoryAvailabilityMock(String date) {
    return InventoryAvailability
        .builder()
        .available(5)
        .total(5)
        .date(date)
        .build();
  }

  @SneakyThrows
  @Test
  void getHotelAvailability_emptyRoomRateList_ShouldReturnOk() {
    // Arrange
    AvailabilitySearchCriteria hasc = createHotelAvailabilitySearchCriteria();

    AvailabilityResult hotelAvailabilityResult = createHotelAvailabilityResultEmptyRoomRateList();

    when(hotelBookingPort.getHotelAvailability(any())).thenReturn(
        hotelAvailabilityResult);
    when(rulesAgentOutPort.getRoomSubstitution(any(), any(), any(), any())).thenReturn(
        mockRoomSubstitutionResponse(List.of("DBLWIN")));

    // Act
    AvailabilityResult foundHotelAvailabilityResult =
        this.hotelAvailability.getHotelAvailability(hasc);

    // Assert
    assertThat(foundHotelAvailabilityResult, notNullValue());
    assertThat(foundHotelAvailabilityResult.getHotelId(), is(hasc.getHotelId()));
    assertThat(foundHotelAvailabilityResult.getStartDate(), is("2022-03-01"));
    assertThat(foundHotelAvailabilityResult.getEndDate(), is("2022-03-03"));
    assertThat(foundHotelAvailabilityResult.getRoomRates(), notNullValue());
    assertThat(foundHotelAvailabilityResult.getRoomRates(), hasSize(0));
  }

  @SneakyThrows
  @Test
  void getHotelAvailability_emptyRatePlanCodeList_ShouldReturnOk() {
    // Arrange
    AvailabilitySearchCriteria hasc = createHotelAvailabilitySearchCriteria();

    AvailabilityResult hotelAvailabilityResult =
        createHotelAvailabilityResultEmptyRatePlanCodeList();

    when(hotelBookingPort.getHotelAvailability(any())).thenReturn(
        hotelAvailabilityResult);
    when(rulesAgentOutPort.getRoomSubstitution(any(), any(), any(), any())).thenReturn(
        mockRoomSubstitutionResponse(List.of("DBLWIN")));

    when(hotelBookingPort.getHotelItemsInventory(
        createItemInventoryRequest(hotelAvailabilityResult.getHotelId(),
            hotelAvailabilityResult.getStartDate(), hotelAvailabilityResult.getEndDate())))
        .thenReturn(createItemInventoryResponse());

    // Act
    AvailabilityResult foundHotelAvailabilityResult =
        this.hotelAvailability.getHotelAvailability(hasc);

    // Assert
    assertThat(foundHotelAvailabilityResult, notNullValue());
    assertThat(foundHotelAvailabilityResult.getHotelId(), is(hasc.getHotelId()));
    assertThat(foundHotelAvailabilityResult.getStartDate(), is("2022-03-01"));
    assertThat(foundHotelAvailabilityResult.getEndDate(), is("2022-03-03"));
    assertThat(foundHotelAvailabilityResult.getRoomRates(), notNullValue());
    assertThat(foundHotelAvailabilityResult.getRoomRates(), hasSize(1));
  }

  @SneakyThrows
  @Test
  @Disabled("Temporarily disabled - see https://whitbreadis.atlassian.net/browse/OB-899")
  void getHotelAvailability__ShouldReturnLimitedAvailability_TRUE() {

    // Arrange
    AvailabilitySearchCriteria hasc = createHotelAvailabilitySearchCriteria();

    AvailabilityResult hotelAvailabilityResult = createHotelAvailabilityResult();

    when(hotelBookingPort.getHotelAvailability(any())).
        thenReturn(hotelAvailabilityResult);
    when(rulesAgentOutPort.getRoomSubstitution(any(), any(), any(), any())).thenReturn(
        mockRoomSubstitutionResponse(List.of("DBLWIN")));
    when(hotelBookingPort.getHotelRoomPriceBreakdown(
        createRoomAvailabilityCriteria(hotelAvailabilityResult.getHotelId(),
            hotelAvailabilityResult.getRoomRates().get(0).getRatePlanCode(),
            hotelAvailabilityResult.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
                .getPmsRoomType(), hasc.getArrivalDate(),
            hasc.getDepartureDate(), hasc.getAdults().get(0), hasc.getChildren().get(0))))
        .thenReturn(createPriceBreakdownList());

    when(hotelBookingPort.getHotelItemsInventory(
        createItemInventoryRequest(hotelAvailabilityResult.getHotelId(),
            hotelAvailabilityResult.getStartDate(), hotelAvailabilityResult.getEndDate())))
        .thenReturn(createItemInventoryResponse());


    when(hotelBookingPort.getHotelInventoryStatistics(any())).thenReturn(
        mockInventoryStatistics_LimitedAvailability_true());

    when(availabilityProperties.getMinimumAvailability()).thenReturn(10);

    // Act
    AvailabilityResult foundHotelAvailabilityResult =
        this.hotelAvailability.getHotelAvailability(hasc);

    // Assert
    assertThat(foundHotelAvailabilityResult, notNullValue());
    assertThat(foundHotelAvailabilityResult.getHotelId(), is(hasc.getHotelId()));
    assertThat(foundHotelAvailabilityResult.isLimitedAvailability(), is(Boolean.TRUE));
  }

  @Test
  void getRestrictionsByDateRange_WhenInvoked_ThenRequestPassedToOutPort() {
    var restrictionsResultMock = mock(RestrictionsByDateRangeResult.class);
    when(this.hotelBookingPort.getRestrictionsByDateRange(restrictionsByDateRangeSearchCriteriaMock)).thenReturn(restrictionsResultMock);

    var result = this.hotelAvailability.getRestrictionsByDateRange(restrictionsByDateRangeSearchCriteriaMock);

    assertEquals(restrictionsResultMock, result);
    verify(this.hotelBookingPort, times(1)).getRestrictionsByDateRange(restrictionsByDateRangeSearchCriteriaMock);
  }

  @Test
  void getMultiHotelRestrictionsByDateRange_WhenInvoked_ThenParallelRequestPassedToOutPort() throws InterruptedException {
    // Arrange
    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();
    when(hotelBookingPort.getRestrictionsByDateRange(any()))
        .thenAnswer(invocation -> {
          threadNames.add(Thread.currentThread().getName());
          Thread.sleep(200);
          return new RestrictionsByDateRangeResult();
        });

    List<RestrictionsByDateRangeSearchCriteria> criteriaList = IntStream.range(0, 4)
        .mapToObj(i -> RestrictionsByDateRangeSearchCriteria.builder().hotelId("hotel" + i).build())
        .toList();

    // Act
    List<RestrictionsByDateRangeResult> results = this.hotelAvailability.getMultiHotelRestrictionsByDateRange(criteriaList);

    // Assert
    assertEquals(4, results.size());

    // Verify all threads completed
    restrictionsExecutorService.shutdown();
    assertTrue(restrictionsExecutorService.awaitTermination(1, TimeUnit.SECONDS));

    // Assert parallel execution
    assertEquals(2, threadNames.size(), "Expected 2 threads to be used");
  }
  private AvailabilityRoomPriceBreakdown createPriceBreakdownList() {
    return createPriceBreakdownList(ROOM_NET_AMOUNT, ROOM_GROSS_AMOUNT, ROOM_TAX_AMOUNT);
  }

  private AvailabilityRoomPriceBreakdown createPriceBreakdownList(final BigDecimal roomNetAmount,
      final BigDecimal roomGrossAmount, final BigDecimal roomTaxAmount) {

    AvailabilityDailyPrice pb1 = AvailabilityDailyPrice.builder()
        .netPrice(new BigDecimal("59"))
        .date("2022-03-01")
        .build();

    AvailabilityDailyPrice pb2 = AvailabilityDailyPrice.builder()
        .netPrice(new BigDecimal("59"))
        .date("2022-03-02")
        .build();

    return AvailabilityRoomPriceBreakdown.builder()
        .dailyPrices(new ArrayList<>(Arrays.asList(pb1, pb2)))
        .totalNetAmount(roomNetAmount)
        .totalGrossAmount(roomGrossAmount)
        .totalTaxAmount(roomTaxAmount)
        .currencyCode("GBP")
        .build();
  }

  private AvailabilityRoomSearchCriteria createRoomAvailabilityCriteria(String hotelCode,
      String ratePlanCode,
      String roomType,
      String arrivalDate,
      String departureDate,
      int adults, int children) {

    return AvailabilityRoomSearchCriteria.builder()
        .hotelId(hotelCode)
        .ratePlanCode(ratePlanCode)
        .roomType(roomType)
        .arrivalDate(arrivalDate)
        .departureDate(departureDate)
        .adults(adults)
        .children(children)
        .build();
  }

  private AvailabilityRequest createHotelAvailabilitySearchRequest(String hotelCode,
      String arrivalDate,
      String departureDate,
      int roomNumber,
      List<String> roomType,
      List<Integer> adultNumber,
      List<Integer> childNumber,
      List<Boolean> cotsRequired) {
    return AvailabilityRequest.builder()
        .hotelId(hotelCode)
        .roomStayStartDate(arrivalDate)
        .roomStayEndDate(departureDate)
        .roomStayQuantity(roomNumber)
        .roomTypes(roomType)
        .adults(adultNumber)
        .children(childNumber)
        .cotsRequired(cotsRequired)
        .build();
  }

  private AvailabilityRequest createHotelAvailabilitySearchRequest_distribution(String hotelCode,
      String arrivalDate,
      String departureDate,
      int roomNumber,
      List<String> roomType,
      List<Integer> adultNumber,
      List<Integer> childNumber,
      List<Boolean> cotsRequired,
      String channel,
      String subchannel
      ) {
    return AvailabilityRequest.builder()
        .hotelId(hotelCode)
        .roomStayStartDate(arrivalDate)
        .roomStayEndDate(departureDate)
        .roomStayQuantity(roomNumber)
        .roomTypes(roomType)
        .adults(adultNumber)
        .children(childNumber)
        .cotsRequired(cotsRequired)
        .channel(channel)
        .subchannel(subchannel)
        .build();
  }

  private AvailabilitySearchCriteria createHotelAvailabilitySearchCriteria() {
    return AvailabilitySearchCriteria.builder()
        .hotelId("LONEUS")
        .arrivalDate("2022-03-01")
        .departureDate("2022-03-03")
        .roomTypes(new ArrayList<>(Arrays.asList("DB", "FAM")))
        .adults(new ArrayList<>(Arrays.asList(1, 2)))
        .children(new ArrayList<>(Arrays.asList(0, 1)))
        .cotsRequired(new ArrayList<>(Arrays.asList(Boolean.TRUE, Boolean.FALSE)))
        .promotionCode("ST10R")
        .build();
  }

  private AvailabilitySearchCriteria createHotelAvailabilitySearchCriteria_distribution() {
    return AvailabilitySearchCriteria.builder()
            .hotelId("LONEUS")
            .arrivalDate("2022-03-01")
            .departureDate("2022-03-03")
            .roomTypes(Arrays.asList("DB", "FAM"))
            .adults(new ArrayList<>(Arrays.asList(1, 2)))
            .children(new ArrayList<>(Arrays.asList(0, 1)))
            .cotsRequired(new ArrayList<>(Arrays.asList(Boolean.TRUE, Boolean.FALSE)))
            .channel("DISTR")
            .subchannel("BOOKING")
            .build();
  }

  private AvailabilityResult createHotelAvailabilityResult() {
    AvailabilityRoomPriceBreakdown rpbDouble = AvailabilityRoomPriceBreakdown.builder()
        .currencyCode("GBP")
        .totalNetAmount(ROOM_NET_AMOUNT)
        .totalGrossAmount(ROOM_GROSS_AMOUNT)
        .effectiveRateAmount(EFFECTIVE_RATE_AMOUNT)
        .dailyPrices(List.of(getDailyPrice(EFFECTIVE_RATE_AMOUNT, "2022-03-01"),
            getDailyPrice(EFFECTIVE_RATE_AMOUNT, "2022-03-02")))
        .build();

    AvailabilityRoomPriceBreakdown rpbDbldbl = AvailabilityRoomPriceBreakdown.builder()
        .currencyCode("GBP")
        .totalNetAmount(ROOM_NET_AMOUNT_SMALL)
        .totalGrossAmount(ROOM_GROSS_AMOUNT_SMALL)
        .effectiveRateAmount(EFFECTIVE_RATE_AMOUNT_SMALL)
        .dailyPrices(List.of(getDailyPrice(EFFECTIVE_RATE_AMOUNT_SMALL, "2022-03-01"),
            getDailyPrice(EFFECTIVE_RATE_AMOUNT_SMALL, "2022-03-02")))
        .build();

    AvailabilityRoom ar1 = AvailabilityRoom.builder()
        .pmsRoomType("DOUBLE")
        .roomClass("ST")
        .roomPriceBreakdown(rpbDouble)
        .build();

    AvailabilityRoom ar2 = AvailabilityRoom.builder()
        .pmsRoomType("DBLDBL")
        .roomClass("ST")
        .roomPriceBreakdown(rpbDbldbl)
        .build();

    AvailabilityRoomType art = AvailabilityRoomType.builder()
        .roomType("DB")
        .room(ar1)
        .room(ar2)
        .adults(1)
        .children(0)
        .cotRequested(Boolean.TRUE)
        .build();

    AvailabilityRoomRate rr = AvailabilityRoomRate.builder()
        .ratePlanCode("DAILY")
        .promotionCode(PROMOTION_CODE)
        .roomType(art)
        .build();

    AvailabilityRoomRate standardRate = AvailabilityRoomRate.builder()
        .ratePlanCode("STANDARD")
        .promotionCode(PROMOTION_CODE)
        .roomType(art)
        .build();

    return AvailabilityResult.builder()
        .timestamp(Instant.now())
        .hotelId("LONEUS")
        .startDate("2022-03-01")
        .endDate("2022-03-03")
        .roomRates(List.of(rr, standardRate))
        .build();
  }

  private static AvailabilityDailyPrice getDailyPrice(BigDecimal amount, String date) {
    return AvailabilityDailyPrice.builder()
        .effectiveRate(amount.divide(new BigDecimal("2")))
        .date(date)
        .build();
  }

  private AvailabilityResult createHotelAvailabilityResult_BB() {
    AvailabilityRoomPriceBreakdown rpbDouble = AvailabilityRoomPriceBreakdown.builder()
        .currencyCode("GBP")
        .totalNetAmount(ROOM_NET_AMOUNT)
        .totalGrossAmount(ROOM_GROSS_AMOUNT)
        .build();
    
    AvailabilityRoomPriceBreakdown rpbDbldbl = AvailabilityRoomPriceBreakdown.builder()
        .currencyCode("GBP")
        .totalNetAmount(ROOM_NET_AMOUNT_SMALL)
        .totalGrossAmount(ROOM_GROSS_AMOUNT_SMALL)
        .build();
    
    AvailabilityRoom ar1 = AvailabilityRoom.builder()
        .pmsRoomType("DOUBLE")
        .roomClass("ST")
        .roomPriceBreakdown(rpbDouble)
        .build();
    
    AvailabilityRoom ar2 = AvailabilityRoom.builder()
        .pmsRoomType("DBLDBL")
        .roomClass("ST")
        .roomPriceBreakdown(rpbDbldbl)
        .build();
    
    AvailabilityRoomType art = AvailabilityRoomType.builder()
        .roomType("DB")
        .room(ar1)
        .room(ar2)
        .adults(1)
        .children(0)
        .cotRequested(Boolean.TRUE)
        .build();
    
    AvailabilityRoomRate rr = AvailabilityRoomRate.builder()
        .ratePlanCode("BUSIFLEX")
        .roomType(art)
        .build();
    
    AvailabilityRoomRate standardRate = AvailabilityRoomRate.builder()
        .ratePlanCode("FLEXRATE")
        .roomType(art)
        .build();
    
    return AvailabilityResult.builder()
        .timestamp(Instant.now())
        .hotelId("LONEUS")
        .startDate("2022-03-01")
        .endDate("2022-03-03")
        .roomRates(List.of(rr, standardRate))
        .build();
  }

  private AvailabilityResult createHotelAvailabilityPromotionResult() {
    AvailabilityRoomPriceBreakdown rpb = AvailabilityRoomPriceBreakdown.builder()
        .currencyCode("GBP")
        .totalNetAmount(PROMOTION_ROOM_NET_AMOUNT)
        .totalGrossAmount(PROMOTION_ROOM_GROSS_AMOUNT)
        .build();

    AvailabilityRoom ar1 = AvailabilityRoom.builder()
        .pmsRoomType("DOUBLE")
        .roomClass("ST")
        .roomPriceBreakdown(rpb)
        .build();

    AvailabilityRoom ar2 = AvailabilityRoom.builder()
        .pmsRoomType("DBLDBL")
        .roomClass("ST")
        .roomPriceBreakdown(rpb)
        .build();

    AvailabilityRoomType art = AvailabilityRoomType.builder()
        .roomType("DB")
        .room(ar1)
        .room(ar2)
        .adults(1)
        .children(0)
        .cotRequested(Boolean.TRUE)
        .build();

    AvailabilityRoomRate promotionRate = AvailabilityRoomRate.builder()
        .ratePlanCode("PROMOTION")
        .roomType(art)
        .promotionCode(PROMOTION_CODE)
        .build();

    return AvailabilityResult.builder()
        .timestamp(Instant.now())
        .hotelId("LONEUS")
        .startDate("2022-03-01")
        .endDate("2022-03-03")
        .roomRates(Collections.singletonList(promotionRate))
        .build();
  }
  
  private AvailabilityResult createHotelAvailabilityPromotionResult_BB() {
    AvailabilityRoomPriceBreakdown rpb = AvailabilityRoomPriceBreakdown.builder()
        .currencyCode("GBP")
        .totalNetAmount(PROMOTION_ROOM_NET_AMOUNT)
        .totalGrossAmount(PROMOTION_ROOM_GROSS_AMOUNT)
        .build();
    
    AvailabilityRoom ar1 = AvailabilityRoom.builder()
        .pmsRoomType("DOUBLE")
        .roomClass("ST")
        .roomPriceBreakdown(rpb)
        .build();
    
    AvailabilityRoom ar2 = AvailabilityRoom.builder()
        .pmsRoomType("DBLDBL")
        .roomClass("ST")
        .roomPriceBreakdown(rpb)
        .build();
    
    AvailabilityRoomType art = AvailabilityRoomType.builder()
        .roomType("DB")
        .room(ar1)
        .room(ar2)
        .adults(1)
        .children(0)
        .cotRequested(Boolean.TRUE)
        .build();
    
    AvailabilityRoomRate promotionRate = AvailabilityRoomRate.builder()
        .ratePlanCode("FLEXRATE")
        .roomType(art)
        .build();
    
    return AvailabilityResult.builder()
        .timestamp(Instant.now())
        .hotelId("LONEUS")
        .startDate("2022-03-01")
        .endDate("2022-03-03")
        .roomRates(Collections.singletonList(promotionRate))
        .build();
  }

  private MealsIncluded createMealsIncluded(){
    return MealsIncluded.builder()
        .breakfast(true)
        .dinner(false)
        .mealName("Pizza menu")
        .build();
  }

  private AvailabilityResult createHotelAvailabilityResult_distribution() {
    AvailabilityRoomPriceBreakdown rpb = AvailabilityRoomPriceBreakdown.builder()
        .currencyCode("GBP")
        .totalNetAmount(new BigDecimal("118"))
        .totalGrossAmount(new BigDecimal("98"))
        .totalTaxAmount(new BigDecimal("20"))
        .build();

    AvailabilityRoom ar = AvailabilityRoom.builder()
        .pmsRoomType("DOUBLE")
        .roomPriceBreakdown(rpb)
        .ratePlanSet("BMD")
        .build();

    AvailabilityRoomType art = AvailabilityRoomType.builder()
        .roomType("DB")
        .room(ar)
        .adults(1)
        .children(0)
        .cotRequested(Boolean.TRUE)
        .build();

    AvailabilityRoomRate rr = AvailabilityRoomRate.builder()
        .ratePlanCode("DAILY")
        .roomType(art)
        .build();

    return AvailabilityResult.builder()
        .timestamp(Instant.now())
        .hotelId("LONEUS")
        .startDate("2022-03-01")
        .endDate("2022-03-03")
        .roomRates(Collections.singletonList(rr))
        .build();
  }

  private AvailabilityResult createHotelAvailabilityResultEmptyRoomRateList() {
    return AvailabilityResult.builder()
        .timestamp(Instant.now())
        .hotelId("LONEUS")
        .startDate("2022-03-01")
        .endDate("2022-03-03")
        .roomRates(emptyList())
        .build();
  }

  private AvailabilityResult createHotelAvailabilityResultEmptyRatePlanCodeList() {

    AvailabilityRoomRate rr = AvailabilityRoomRate.builder().build();

    return AvailabilityResult.builder()
        .timestamp(Instant.now())
        .hotelId("LONEUS")
        .startDate("2022-03-01")
        .endDate("2022-03-03")
        .roomRates(Collections.singletonList(rr))
        .build();
  }

  private AvailabilityByIdsResult createHotelAvailabilityByIdsResult() {
    AvailabilityRoomPriceBreakdown rpb = AvailabilityRoomPriceBreakdown.builder()
        .currencyCode("GBP")
        .totalNetAmount(new BigDecimal("118"))
        .build();

    AvailabilityRoom ar = AvailabilityRoom.builder()
        .pmsRoomType("DOUBLE")
        .roomPriceBreakdown(rpb)
        .ratePlanSet("BMD")
        .build();

    AvailabilityRoomType art = AvailabilityRoomType.builder()
        .roomType("DB")
        .room(ar)
        .adults(1)
        .children(0)
        .cotRequested(Boolean.TRUE)
        .build();

    AvailabilityRoomRate rr = AvailabilityRoomRate.builder()
        .ratePlanCode("DAILY")
        .roomType(art)
        .build();

    return AvailabilityByIdsResult.builder()
        .hotelAvailability(List.of(
            AvailabilityResult
                .builder()
                .timestamp(Instant.now())
                .hotelId("LONEUS")
                .startDate("2022-03-01")
                .endDate("2022-03-03")
                .roomRates(Collections.singletonList(rr))
                .build()))
        .build();
  }

  @Test
  void getHotelRoomsInventory__ShouldReturnOk() {
    // Arrange
    HotelInventoryRequest hotelInventoryRequest = HotelInventoryRequest.builder()
        .hotelId("MANOLD")
        .dateRangeStart("2022-10-28")
        .dateRangeEnd("2022-10-30")
        .build();

    when(hotelBookingPort.getHotelRoomsInventory(hotelInventoryRequest)).thenReturn(
        createRoomInventories());

    // Act
    HotelInventoryRoomType foundHotelInventoryRoomType = this.hotelAvailability.getHotelRoomsInventory(
        hotelInventoryRequest);

    // Assert
    assertThat(foundHotelInventoryRoomType.getRoomTypeInventories(), notNullValue());
    assertThat(foundHotelInventoryRoomType.getRoomTypeInventories(), hasSize(2));
    assertThat(foundHotelInventoryRoomType.getRoomTypeInventories().get(0).getCode(), is("DOUBLE"));
    assertThat(foundHotelInventoryRoomType.getRoomTypeInventories().get(0).getAvailableCount(), is(42));
    assertThat(foundHotelInventoryRoomType.getRoomTypeInventories().get(1).getCode(), is("FMTRPL"));
    assertThat(foundHotelInventoryRoomType.getRoomTypeInventories().get(1).getAvailableCount(), is(10));

  }

  private HotelInventoryRoomType createRoomInventories() {
    RoomLevelInventory roomLevelInventoryDB = RoomLevelInventory.builder()
        .availableCount(42)
        .code("DOUBLE")
        .build();

    RoomLevelInventory roomLevelInventoryFAM = RoomLevelInventory.builder()
        .availableCount(10)
        .code("FMTRPL")
        .build();

    return HotelInventoryRoomType.builder()
        .roomTypeInventories(Arrays.asList(roomLevelInventoryDB, roomLevelInventoryFAM)).build();

  }

  @Test
  void getHotelMultiRoomsPriceBreakdown__ShouldReturnOk() {

    // Arrange
    Mockito.when(hotelBookingPort.getRatesInfo(any()))
        .thenReturn(mockRoomPriceBreakdownResult());

    // Act
    RoomPriceBreakdownResult response = hotelAvailability
        .getHotelMultiRoomsPriceBreakdown("TEST", createRoomPriceBreakdownRequest());

    // Assert
    Assertions.assertNotNull(response);
    assertThat(response.getPriceBreakdown().get(0).getCurrencyCode(), is("EUR"));
    assertThat(response.getPriceBreakdown().get(0).getTotalNetAmount(), is(BigDecimal.valueOf(1280)));

  }

  @Test
  void getBasePromoRateCode_validPromoCode_returnsBasePromoRateCodes() {
    // Arrange
    AvailabilitySearchCriteria hasc = createHotelAvailabilitySearchCriteria();

    AvailabilityRoomRate promoRoomRate = new AvailabilityRoomRate();
    promoRoomRate.setPromotionCode("ST10R");
    promoRoomRate.setRatePlanCode("STDDIS10");

    AvailabilityResult hotelAvailabilityPromotionResult = new AvailabilityResult();
    hotelAvailabilityPromotionResult.setRoomRates(List.of(promoRoomRate));

    RatePlanInfoResponse ratePlanInfoResponse = createSampleRatePlanInfoResponse();

    when(ratePlansOutPort.getRatePlanInfo("STDDIS10", "LONEUS")).thenReturn(ratePlanInfoResponse);

    // Act
    String basePromoRateCode = hotelAvailability.getBasePromoRateCode(hasc,
        hotelAvailabilityPromotionResult);
    // Assert
    assertEquals("STANDARD", basePromoRateCode);
  }

  @Test
  void getBasePromoRateCode_nullPromoCode_returnsNull() {
    // Arrange
    AvailabilitySearchCriteria hasc = createHotelAvailabilitySearchCriteria();
    hasc.setPromotionCode(null);

    AvailabilityRoomRate promoRoomRate = new AvailabilityRoomRate();
    promoRoomRate.setPromotionCode(null);

    AvailabilityResult hotelAvailabilityPromotionResult = new AvailabilityResult();
    hotelAvailabilityPromotionResult.setRoomRates(List.of(promoRoomRate));

    // Act
    String basePromoRateCode = hotelAvailability.getBasePromoRateCode(hasc,
        hotelAvailabilityPromotionResult);

    // Assert
    assertNull(basePromoRateCode);
  }

  private RatePlanInfoResponse createSampleRatePlanInfoResponse() {
    DynamicBaseRate dynamicBaseRate = new DynamicBaseRate();
    dynamicBaseRate.setDynamicBasedOnRatePlan("STANDARD");

    RatePlanBasedOnRate basedOnRate = new RatePlanBasedOnRate();
    basedOnRate.setDynamicBaseRate(dynamicBaseRate);

    RatePlans ratePlans = new RatePlans();
    ratePlans.setRatePlanBasedOnRates(List.of(basedOnRate));

    RatePlanInfoResponse ratePlanInfoResponse = new RatePlanInfoResponse();
    ratePlanInfoResponse.setRatePlanInfo(List.of(ratePlans));

    return ratePlanInfoResponse;
  }

  private List<AvailabilityRoomPriceBreakdown> mockRoomPriceBreakdownResult() {
    return List.of(AvailabilityRoomPriceBreakdown.builder()
            .currencyCode("EUR")
            .totalNetAmount(BigDecimal.valueOf(1280))
        .build());
  }

  private RoomPriceBreakdownRequest createRoomPriceBreakdownRequest() {
    return RoomPriceBreakdownRequest.builder()
        .roomTypes(List.of("DOUBLE"))
        .ratePlanCode("FLEXRATE")
        .adultsNo(List.of(1))
        .childrenNo(List.of(1))
        .arrivalDate("2023-06-01")
        .departureDate("2023-06-01")
        .build();
  }

  @Test
  void getRateCodePricing__ShouldReturnOk() {

    // Arrange
    Mockito.when(hotelBookingPort.getRateCodePricing(any()))
        .thenReturn(mockRateCodePricing());

    // Act
    RateCodePricingResult response = hotelAvailability
        .getRateCodePricing(createRateCodeRequest());

    // Assert
    Assertions.assertNotNull(response);
    assertThat(response.getRatePlanCode(), is("FLEXRATE"));
    assertThat(response.getCurrencyCode(), is("EUR"));
    assertThat(response.getTotalNetAmount(), is(BigDecimal.valueOf(1280)));

  }

  @SneakyThrows
  @Test
  void getHotelItemsInventory__ShouldReturnOk() {

    // Arrange
    Mockito.when(hotelBookingPort.getHotelItemsInventory(any()))
            .thenReturn(createItemInventoryResponse());

    // Act
    ItemInventoryResponse response = hotelAvailability
            .getHotelItemsInventory(createItemInventoryRequest("MANOLD", "2022-03-02", "2022-03-03"));

    // Assert
    Assertions.assertNotNull(response);
    assertThat(response.getItemsInventory(), hasSize(1));
    assertThat(response.getItemsInventory().get(0).getName(), is("Cot"));
    assertThat(response.getItemsInventory().get(0).getDescription(), is("Cot"));
    assertThat(response.getItemsInventory().get(0).getInventories(), hasSize(3));
    assertThat(response.getItemsInventory().get(0).getInventories().get(0).getAvailable(), is(5));
    assertThat(response.getItemsInventory().get(0).getInventories().get(0).getTotal(), is(5));
    assertThat(response.getItemsInventory().get(0).getInventories().get(0).getDate(), is("2022-03-01"));
    assertThat(response.getItemsInventory().get(0).getInventories().get(1).getAvailable(), is(5));
    assertThat(response.getItemsInventory().get(0).getInventories().get(1).getTotal(), is(5));
    assertThat(response.getItemsInventory().get(0).getInventories().get(1).getDate(), is("2022-03-02"));
    assertThat(response.getItemsInventory().get(0).getInventories().get(2).getAvailable(), is(5));
    assertThat(response.getItemsInventory().get(0).getInventories().get(2).getTotal(), is(5));
    assertThat(response.getItemsInventory().get(0).getInventories().get(2).getDate(), is("2022-03-03"));
  }

  private RateCodeCriteria createRateCodeRequest() {
    return RateCodeCriteria.builder()
        .hotelId("MANOLD")
        .arrivalDate("2022-10-20")
        .departureDate("2022-10-22")
        .roomInfoCriteria(
            RateCodeRoomInfoCriteria.builder()
                .adultsNo(2)
                .childrenNo(0)
                .roomType("DOUBLE")
                .build()
        )
        .ratePlanCode("FLEXRATE")
        .build();
  }

  private RateCodePricingResult mockRateCodePricing() {
    return RateCodePricingResult.builder()
        .ratePlanCode("FLEXRATE")
        .totalNetAmount(BigDecimal.valueOf(1280))
        .currencyCode("EUR")
        .build();
  }

  private MultiHotelAvailabilityRequest generateMultiHotelAvailabilityRequest() {
    return MultiHotelAvailabilityRequest.builder()
        .hotelIds(Arrays.asList("TestHotelId"))
        .arrivalDate("TestArrivalDate")
        .departureDate("TestDepartureDate")
        .numberOfRooms(Arrays.asList(1,2,1))
        .roomTypes(Arrays.asList("SB","DB","DB"))
        .adults(Arrays.asList(1,2,1))
        .children(Arrays.asList(0,0,0))
        .cotsRequired(Arrays.asList(false, false,false))
        .channel("TestChannel")
        .build();
  }

  private MultiHotelAvailabilityRequest generateMultiHotelAvailabilityCompanyIdRequest() {
    return MultiHotelAvailabilityRequest.builder()
        .hotelIds(Arrays.asList("TestHotelId"))
        .arrivalDate("TestArrivalDate")
        .departureDate("TestDepartureDate")
        .numberOfRooms(Arrays.asList(1,2,1))
        .roomTypes(Arrays.asList("SB","DB","DB"))
        .adults(Arrays.asList(1,2,1))
        .children(Arrays.asList(0,0,0))
        .cotsRequired(Arrays.asList(false, false,false))
        .channel("TestChannel")
        .companyId("TestCompanyId")
        .build();
  }

  private AvailabilityByIdsSearchCriteria generateAvailabilityByIdsSearchCriteria() {
    return AvailabilityByIdsSearchCriteria.builder()
        .arrivalDate("TestArrivalDate")
        .departureDate("TestDepartureDate")
        .adults(Arrays.asList(1))
        .children(Arrays.asList(0))
        .cotsRequired(Arrays.asList(false))
        .roomTypes(List.of("DOUBLE"))
        .channel("TestChannel")
        .globalCompanyId("TestCompanyId")
        .negotiatedRateDisplaySets(List.of("BMD)"))
        .build();
  }

  private List<HotelAvailabilityResult> generateHotelAvailabilityResult() {
    var hotelResult = HotelAvailabilityResult.builder()
        .hotelId("TestHotelId")
        .available(true)
        .arrivalDate("TestArrivalDate")
        .departureDate("TestDepartureDate")
        .roomTypes(generateRoomTypes())
        .build();
    return Arrays.asList(hotelResult);
  }

  private List<RoomType> generateRoomTypes() {
    var roomTypeSB = RoomType.builder()
        .roomType("SB")
        .numberOfRooms(1)
        .adults("1")
        .children("0")
        .cotRequested("false")
        .roomRates(generateRoomRates())
        .build();
    var roomTypeDB = RoomType.builder()
        .roomType("DB")
        .numberOfRooms(1)
        .adults("1")
        .children("0")
        .cotRequested("false")
        .roomRates(new LinkedList<>())
        .build();
    return Arrays.asList(roomTypeSB, roomTypeDB);
  }

  private List<RoomRateInfo> generateRoomRates() {
    var roomRateInfo = RoomRateInfo.builder()
        .ratePlan("TestRatePlan")
        .roomType("TestRoomType")
        .currency("TestCurrency")
        .totalPrice(BigDecimal.ZERO)
        .build();
    return Arrays.asList(roomRateInfo);
  }

  private RoomSubstitutionRuleResponse mockRoomSubstitutionResponse(List<String> roomTypes) {
    return RoomSubstitutionRuleResponse.builder().requestDetails(RoomSubstitutionRequestDetails.builder().children(1).adults(1).roomType("DB").pms("OP").build())
        .substitutionList(mockRoomSubstitutionList(roomTypes)).build();
  }

  private List<RoomSubstitution> mockRoomSubstitutionList (List<String> roomTypes){
    return roomTypes.stream().map(type -> RoomSubstitution.builder().type(type).build()).toList();
  }

  private List<StatisticsDateItem> mockInventoryStatistics_LimitedAvailability_false(){
    var statistics = new ArrayList<StatisticsDateItem>();
    statistics.add(StatisticsDateItem.builder().inventoryItemList(
        List.of(StatisticsInventoryItem.builder().value(new BigDecimal(51)).code(PHYSICAL_ROOMS).build(),
            StatisticsInventoryItem.builder().value(new BigDecimal(51)).code(AVAILABLE_ROOMS).build())).build());
    statistics.add(StatisticsDateItem.builder().inventoryItemList(
        List.of(StatisticsInventoryItem.builder().value(new BigDecimal(26)).code(PHYSICAL_ROOMS).build(),
            StatisticsInventoryItem.builder().value(new BigDecimal(17)).code(AVAILABLE_ROOMS).build())).build());
    statistics.add(StatisticsDateItem.builder().inventoryItemList(
        List.of(StatisticsInventoryItem.builder().value(new BigDecimal(0)).code(PHYSICAL_ROOMS).build(),
            StatisticsInventoryItem.builder().value(new BigDecimal(0)).code(AVAILABLE_ROOMS).build())).build());

    return statistics;
  }
  private List<StatisticsDateItem> mockInventoryStatistics_LimitedAvailability_true(){
    var statistics = new ArrayList<StatisticsDateItem>();
    statistics.add(StatisticsDateItem.builder().inventoryItemList(
        List.of(StatisticsInventoryItem.builder().value(new BigDecimal(51)).code(PHYSICAL_ROOMS).build(),
            StatisticsInventoryItem.builder().value(new BigDecimal(3)).code(AVAILABLE_ROOMS).build())).build());
    statistics.add(StatisticsDateItem.builder().inventoryItemList(
        List.of(StatisticsInventoryItem.builder().value(new BigDecimal(26)).code(PHYSICAL_ROOMS).build(),
            StatisticsInventoryItem.builder().value(new BigDecimal(17)).code(AVAILABLE_ROOMS).build())).build());
    statistics.add(StatisticsDateItem.builder().inventoryItemList(
        List.of(StatisticsInventoryItem.builder().value(new BigDecimal(0)).code(PHYSICAL_ROOMS).build(),
            StatisticsInventoryItem.builder().value(new BigDecimal(0)).code(AVAILABLE_ROOMS).build())).build());

    return statistics;
  }
}