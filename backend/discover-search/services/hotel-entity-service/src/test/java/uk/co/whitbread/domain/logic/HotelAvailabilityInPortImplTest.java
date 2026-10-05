package uk.co.whitbread.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.domain.model.availability.in.BookingChannel.BB_BOOKING_CHANNEL;
import static uk.co.whitbread.domain.model.availability.in.BookingChannel.CCUI_BOOKING_CHANNEL;
import static uk.co.whitbread.domain.model.availability.in.BookingChannel.DISTR_BOOKING_CHANNEL;
import static uk.co.whitbread.domain.model.availability.in.BookingChannel.PI_BOOKING_CHANNEL;
import static uk.co.whitbread.domain.model.availability.in.BookingChannel.WEB_BOOKING_SUBCHANNEL;
import static uk.co.whitbread.infrastructure.rest.controller.availability.model.in.PromoKind.UNIQUE;

import io.micrometer.tracing.Tracer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import uk.co.whitbread.domain.exceptions.ErrorCode;
import uk.co.whitbread.domain.model.availability.in.BookingChannel;
import uk.co.whitbread.domain.model.availability.in.CorporateRate;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsRequest;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityRequest;
import uk.co.whitbread.domain.model.availability.in.HotelInventoryRequest;
import uk.co.whitbread.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.domain.model.availability.in.RateV2;
import uk.co.whitbread.domain.model.availability.out.DailyPrice;
import uk.co.whitbread.domain.model.availability.out.HotelAvailability;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIds;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityResultV2;
import uk.co.whitbread.domain.model.availability.out.HotelInventoryRoomType;
import uk.co.whitbread.domain.model.availability.out.PriceInfo;
import uk.co.whitbread.domain.model.availability.out.RateCodePricingResult;
import uk.co.whitbread.domain.model.availability.out.Room;
import uk.co.whitbread.domain.model.availability.out.RoomLevelInventory;
import uk.co.whitbread.domain.model.availability.out.RoomPriceBreakdown;
import uk.co.whitbread.domain.model.availability.out.RoomPriceBreakdownResult;
import uk.co.whitbread.domain.model.availability.out.RoomRate;
import uk.co.whitbread.domain.model.availability.out.RoomRateInfoV2;
import uk.co.whitbread.domain.model.availability.out.RoomRateV2;
import uk.co.whitbread.domain.model.availability.out.RoomStay;
import uk.co.whitbread.domain.model.availability.out.RoomTypeInfo;
import uk.co.whitbread.domain.model.availability.out.RoomTypeV2;
import uk.co.whitbread.domain.model.cdh.CdhSearchCompaniesRequest;
import uk.co.whitbread.domain.model.company.out.Company;
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.promotion.in.PromoKindRequest;
import uk.co.whitbread.infrastructure.rest.client.companyentity.CompanyEntityServiceOutPortImpl;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.domain.model.migrationstatus.in.HotelsMigrationStatusRequest;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelMigrationStatusResponse;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelsMigrationStatusResponse;
import uk.co.whitbread.domain.model.packages.out.Meal;
import uk.co.whitbread.domain.model.packages.out.MealsInfoResponse;
import uk.co.whitbread.domain.model.packages.out.PackageCode;
import uk.co.whitbread.domain.model.packages.out.Packages;
import uk.co.whitbread.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.domain.model.packages.out.Restaurant;
import uk.co.whitbread.domain.model.packages.out.SoftBundle;
import uk.co.whitbread.domain.model.packages.out.UpsellItems;
import uk.co.whitbread.domain.model.promotion.out.PromoKindResponse;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomOccupancyData;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomOccupancyResponse;
import uk.co.whitbread.domain.model.rulesagent.out.RateSuppressionRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitution;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitutionRequestDetails;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.domain.ports.primary.PackagesInPort;
import uk.co.whitbread.domain.ports.primary.RulesAgentInPort;
import uk.co.whitbread.domain.ports.secondary.AvailabilityCacheV1SearchOutPort;
import uk.co.whitbread.domain.ports.secondary.BasketServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.CdhAdapterOutPort;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.domain.ports.secondary.OnSaleFlagOutPort;
import uk.co.whitbread.domain.ports.secondary.PromotionOutPort;
import uk.co.whitbread.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.hotel.content.generated.models.GlobalConfigDto;
import uk.co.whitbread.hotel.content.generated.models.HotelCityTaxDto;
import uk.co.whitbread.hotel.content.generated.models.HotelInformationExtendedDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DynamicBaseRate;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlanBasedOnRate;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlanInfoResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlans;
import uk.co.whitbread.infrastructure.config.CompanyProperties;
import uk.co.whitbread.infrastructure.config.PromotionProperties;
import uk.co.whitbread.infrastructure.config.RateSuppressionProperties;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.exception.NoAvailabilityException;
import uk.co.whitbread.infrastructure.rest.client.promotion.exceptions.InvalidPromotionException;
import uk.co.whitbread.infrastructure.rest.controller.packages.model.out.ExtrasDto;
import uk.co.whitbread.promo.generated.models.promotion.PromoCodeStatus;
import uk.co.whitbread.promo.generated.models.promotion.PromoKind;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@ExtendWith(MockitoExtension.class)
class HotelAvailabilityInPortImplTest {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  public static final java.time.LocalDate ARRIVAL_DATE_V2 = LocalDate.of(2024, 03, 10);
  private static final String CORPORATE_ID = "1234";
  private static final String NEG_RATE_PLAN = "NEG";
  private static final String PROMO_RATE = "PROMO";
  private final String HOTEL_ID = "TTSSTT";
  private final List<String> ROOM_TYPES = Collections.singletonList("DB");
  private final List<String> TWIN_ROOM_TYPES = Collections.singletonList("TWIN");
  private final List<Integer> ADULTS = Collections.singletonList(2);
  private final List<Integer> CHILDREN = Collections.singletonList(0);
  private final List<Boolean> COTS_REQUIRED = Collections.singletonList(false);
  private final String ARRIVAL_DATE = LocalDate.now().toString();
  private final String DEPARTURE_DATE = LocalDate.now().plusDays(5).toString();

  @InjectMocks
  private HotelAvailabilityInPortImpl hotelAvailabilityInboundPort;
  @Mock
  private HotelAvailabilityOutPort hotelAvailabilityOutboundPort;
  @Mock
  private AvailabilityCacheV1SearchOutPort availabilityCacheV1SearchOutPort;
  @Mock
  private RulesAgentInPort rulesAgentInPort;
  @Mock
  private MlosCommonLogic mlosCommonLogic;

  @Mock
  private PackagesInPort packagesInPort;
  
  @Mock
  private BasketServiceOutPort basketServiceOutPort;

  @Mock
  private ContentServiceOutPort contentServiceOutPort;

  @Mock
  private OnSaleFlagOutPort onSaleFlagOutPortImpl;

  @Mock
  private CompanyProperties companyProperties;

  @Mock
  private HotelAvailabilityCheckRules hotelAvailabilityCheckRules;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Spy
  private ConcurrentTracer concurrentTracer = new ConcurrentTracer(Tracer.NOOP);

  @Mock
  private RulesAgentOutPort rulesAgentOutPort;

  @Mock
  private PromotionProperties promotionProperties;

  @Mock
  private RateSuppressionProperties rateSuppressionProperties;

  @Mock
  private SoftBundlesAndRatesLogic softBundlesAndRatesLogic;

  @Mock
  private PromotionOutPort promotionOutPort;

  @Mock
  private CdhAdapterOutPort cdhAdapterOutPort;

  @Mock
  private CompanyEntityServiceOutPortImpl companyEntityServiceOutPort;

  private final uk.co.whitbread.hotel.content.generated.models.ExtrasLabelDto ancillariesContent;
  
  public HotelAvailabilityInPortImplTest() {
    // Initialize with empty extras labels to reflect real client contract
    this.ancillariesContent = new uk.co.whitbread.hotel.content.generated.models.ExtrasLabelDto();
    this.ancillariesContent.setExtrasLabels(List.of());
  }

  @Test
  void getAvailabilities__ShouldReturnOK() {

    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(ROOM_TYPES, null, CCUI_BOOKING_CHANNEL);

    when(hotelAvailabilityOutboundPort.getHotelAvailability(hotelAvailabilityRequest))
        .thenReturn(getHotelAvailability());

    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(
        RateSuppressionRuleResponse.builder()
            .rateSuppressionList(Collections.emptyList())
            .expiryDate(Date.from(
                LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
            .build());

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    when(hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest)).thenReturn(true);

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);

    // Assert
    assertThat(availability, notNullValue());
    assertTrue(availability.isAvailable());
    assertEquals(HOTEL_ID, availability.getHotelId());
    assertEquals(hotelAvailabilityRequest.getArrivalDate(), availability.getStartDate());
    assertEquals(hotelAvailabilityRequest.getDepartureDate(), availability.getEndDate());
    MatcherAssert.assertThat(availability.getRoomRates(), hasSize(greaterThan(0)));
    assertEquals(new BigDecimal(15), availability.getRoomRates().get(0).getRoomTypes().get(0)
        .getRooms().get(0).getRoomPriceBreakdown().getTotalCityTaxAmount());
  }

  @Test
  void getAvailabilitiesNoRooms__ShouldReturnOK() {

    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(ROOM_TYPES, null, CCUI_BOOKING_CHANNEL);

    when(hotelAvailabilityOutboundPort.getHotelAvailability(hotelAvailabilityRequest))
        .thenReturn(getHotelAvailabilityNoRooms());

    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(
        RateSuppressionRuleResponse.builder()
            .rateSuppressionList(Collections.emptyList())
            .expiryDate(Date.from(
                LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
            .build());

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    when(hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest)).thenReturn(true);

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);

    // Assert
    assertThat(availability, notNullValue());
    assertFalse(availability.isAvailable());
    assertEquals(HOTEL_ID, availability.getHotelId());
    assertEquals(hotelAvailabilityRequest.getArrivalDate(), availability.getStartDate());
    assertEquals(hotelAvailabilityRequest.getDepartureDate(), availability.getEndDate());
    MatcherAssert.assertThat(availability.getRoomRates(), hasSize(0));
  }

  @Test
  void getAvailabilitiesForEmployee__ShouldReturnOK() {

    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(ROOM_TYPES, "EMP01", CCUI_BOOKING_CHANNEL);

    when(hotelAvailabilityOutboundPort.getHotelAvailability(hotelAvailabilityRequest))
            .thenReturn(getHotelAvailability());

    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(
            RateSuppressionRuleResponse.builder()
                    .rateSuppressionList(Collections.emptyList())
                    .expiryDate(Date.from(
                            LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
                    .build());

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
            .thenReturn(getMigrationStatusResponse());

    when(companyProperties.getCompanyId()).thenReturn("123456");

    when(hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest)).thenReturn(true);

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);

    // Assert
    assertThat(availability, notNullValue());
    assertTrue(availability.isAvailable());
    MatcherAssert.assertThat(availability.getRoomRates(), hasSize(greaterThan(0)));
    assertEquals(availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0).getRoomPriceBreakdown().getTotalNetAmount(), new BigDecimal(60));
    assertEquals(availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0).getRoomPriceBreakdown().getTotalRoomNetAmount(), new BigDecimal(60));
  }

  @Test
  void getHotelAvailability__ShouldInitializeRoomNetAmountsFromInitialPrices() {
    // Arrange
    var request = getHotelAvailabilityRequest(ROOM_TYPES, null, CCUI_BOOKING_CHANNEL);

    var roomWithDailyPrices = HotelAvailability.builder()
        .hotelId(HOTEL_ID)
        .startDate(ARRIVAL_DATE)
        .endDate(DEPARTURE_DATE)
        .available(true)
        .roomRate(RoomRate.builder()
            .ratePlanCode("FLEXRATE")
            .roomType(RoomTypeInfo.builder()
                .roomType("DB")
                .adults(2)
                .room(Room.builder()
                    .roomClass("ST")
                    .roomPriceBreakdown(RoomPriceBreakdown.builder()
                        .totalNetAmount(BigDecimal.valueOf(120))
                        .effectiveRateAmount(BigDecimal.valueOf(110))
                        .dailyPrice(DailyPrice.builder()
                            .date(ARRIVAL_DATE)
                            .netPrice(BigDecimal.valueOf(60))
                            .grossPrice(BigDecimal.valueOf(72))
                            .effectiveRate(BigDecimal.valueOf(55))
                            .build())
                        .dailyPrice(DailyPrice.builder()
                            .date(DEPARTURE_DATE)
                            .netPrice(BigDecimal.valueOf(60))
                            .grossPrice(BigDecimal.valueOf(72))
                            .effectiveRate(BigDecimal.valueOf(55))
                            .build())
                        .build())
                    .build())
                .build())
            .build())
        .build();

    when(hotelAvailabilityOutboundPort.getHotelAvailability(request)).thenReturn(roomWithDailyPrices);
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(
        RateSuppressionRuleResponse.builder()
            .rateSuppressionList(Collections.emptyList())
            .expiryDate(Date.from(
                LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
            .build());
    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(request)).thenReturn(true);
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert
    var breakdown = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
        .getRoomPriceBreakdown();

    assertEquals(BigDecimal.valueOf(120), breakdown.getTotalRoomNetAmount());
    assertEquals(BigDecimal.valueOf(60), breakdown.getDailyPrices().get(0).getRoomNetPrice());
    assertEquals(BigDecimal.valueOf(60), breakdown.getDailyPrices().get(1).getRoomNetPrice());
    assertEquals(BigDecimal.valueOf(120), breakdown.getTotalNetAmount());
    assertEquals(BigDecimal.valueOf(60), breakdown.getDailyPrices().get(0).getNetPrice());
    assertEquals(BigDecimal.valueOf(60), breakdown.getDailyPrices().get(1).getNetPrice());
  }

  @Test
  void getAvailabilitiesRateSuppression__ShouldReturnOK() {

    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(ROOM_TYPES, null, CCUI_BOOKING_CHANNEL);

    when(hotelAvailabilityOutboundPort.getHotelAvailability(hotelAvailabilityRequest))
        .thenReturn(getHotelAvailability());

    when(rateSuppressionProperties.getRoomClasses()).thenReturn(
        Arrays.asList("ST","BG","SE","SV","BV","PP","PV"));

    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(mockRateSuppressionResponse(
        Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()),
        List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX")));

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    when(hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest)).thenReturn(true);

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);

    // Assert
    assertThat(availability, notNullValue());
    assertTrue(availability.isAvailable());
    assertThat(availability.getRoomRates(), hasSize(equalTo(3)));
    assertThat(availability.getRoomRates().get(0).getRatePlanCode(), is("FLEXRATE"));
    assertThat(availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
        .getRoomPriceBreakdown().getTotalNetAmount(), is(BigDecimal.valueOf(150)));
    assertThat(availability.getRoomRates().get(1).getRatePlanCode(), is("STANDARD"));
    assertThat(availability.getRoomRates().get(1).getRoomTypes().get(0).getRooms().get(0)
        .getRoomPriceBreakdown().getTotalNetAmount(), is(BigDecimal.valueOf(100)));
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void getAvailabilitiesForTwinRoomType__ShouldReturnOK(boolean shouldCityTaxBeRemoved) {

    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(TWIN_ROOM_TYPES, null, CCUI_BOOKING_CHANNEL);

    when(hotelAvailabilityOutboundPort.getHotelAvailability(hotelAvailabilityRequest))
            .thenReturn(getHotelAvailabilityV1());

    when(rateSuppressionProperties.getRoomClasses()).thenReturn(
        Arrays.asList("ST","BG","SE","SV","BV","PP","PV"));

    var substitutionResponse = RoomSubstitutionRuleResponse.builder()
            .requestDetails(RoomSubstitutionRequestDetails.builder()
                    .cotRequired(false)
                    .adults(1)
                    .children(0)
                    .build())
            .substitutionList(buildSubstitutionList())
            .build();

    when(packagesInPort.getPackages(any())).thenReturn(PackagesResponse.builder()
                    .packages(Packages.builder()
                            .meals(Collections.singletonList(Meal.builder().id("HSATWN").price(new BigDecimal(5)).build()))
                            .build())
            .build());

    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(mockRateSuppressionResponse(
            Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()),
            List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX")));

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
            .thenReturn(getMigrationStatusResponse());

    when(hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest)).thenReturn(true);

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(shouldCityTaxBeRemoved);

    if(shouldCityTaxBeRemoved) {
      when(contentServiceOutPort.getGlobalConfig(null, null))
          .thenReturn(createMockGlobalConfigDto(HOTEL_ID));
      when(contentServiceOutPort.getHotelInformation(null, null, HOTEL_ID))
          .thenReturn(createMockHotelInformationExtendedDto("2026-10-01"));
    }

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);

    // Assert
    assertThat(availability, notNullValue());
    assertTrue(availability.isAvailable());
    assertThat(availability.getRoomRates(), hasSize(equalTo(4)));
    assertThat(availability.getRoomRates().get(0).isTwinRoomTypeAvailability(), is(true));
    assertThat(availability.getRoomRates().get(0).getRatePlanCode(), is("FLEXRATE"));
    assertThat(availability.getRoomRates().get(1).getRatePlanCode(), is("SEMIFLEX"));
    assertThat(availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0).getPmsRoomType()
            , is("TWINRM"));
    assertThat(availability.getRoomRates().get(1).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getPackageAmount(), is(BigDecimal.valueOf(5)));
    assertThat(availability.getRoomRates().get(1).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getPackageCode(), is("HSATWN"));
    assertThat(
            availability.getRoomRates().get(1)
                    .getRoomTypes().get(0)
                    .getRooms().get(0)
                    .getRoomPriceBreakdown()
                    .getBaseRateAmount(),
            is(nullValue())
    );

    if(shouldCityTaxBeRemoved) {
      assertThat(availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
          .getRoomPriceBreakdown().getTotalNetAmount(), is(BigDecimal.valueOf(125)));
      assertThat(availability.getRoomRates().get(1).getRoomTypes().get(0).getRooms().get(0)
          .getRoomPriceBreakdown().getTotalNetAmount(), is(BigDecimal.valueOf(145)));
    } else {
      assertThat(availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
          .getRoomPriceBreakdown().getTotalNetAmount(), is(BigDecimal.valueOf(127)));
      assertThat(availability.getRoomRates().get(1).getRoomTypes().get(0).getRooms().get(0)
          .getRoomPriceBreakdown().getTotalNetAmount(), is(BigDecimal.valueOf(175)));
    }
  }

  @Test
  void getAvailabilitiesForTwinRoomAndFamRoomType__ShouldReturnOK() {
    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequestMultipleRooms(List.of("FAM", "TWIN"), null);

    when(hotelAvailabilityOutboundPort.getHotelAvailability(hotelAvailabilityRequest))
        .thenReturn(getHotelAvailabilityV1());

    when(rateSuppressionProperties.getRoomClasses()).thenReturn(
        Arrays.asList("ST","BG","SE","SV","BV","PP","PV"));

    when(packagesInPort.getPackages(any())).thenReturn(PackagesResponse.builder()
        .packages(Packages.builder()
            .meals(Collections.singletonList(Meal.builder().id("HSATWN").price(new BigDecimal(5)).build()))
            .build())
        .build());

    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(mockRateSuppressionResponse(
        Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()),
        List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX")));

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    when(hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest)).thenReturn(true);

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);


    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);

    // Assert
    assertThat(availability, notNullValue());
    assertTrue(availability.isAvailable());
    assertThat(availability.getRoomRates(), hasSize(equalTo(4)));
    assertThat(availability.getRoomRates().get(0).isTwinRoomTypeAvailability(), is(true));
    assertThat(availability.getRoomRates().get(0).getRatePlanCode(), is("FLEXRATE"));
    assertThat(availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
        .getRoomPriceBreakdown().getTotalNetAmount(), is(BigDecimal.valueOf(127)));
    assertThat(availability.getRoomRates().get(1).getRatePlanCode(), is("SEMIFLEX"));
    assertThat(availability.getRoomRates().get(1).getRoomTypes().get(0).getRooms().get(0)
        .getRoomPriceBreakdown().getTotalNetAmount(), is(BigDecimal.valueOf(175)));
    assertThat(availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0).getPmsRoomType()
        , is("TWINRM"));
    assertThat(availability.getRoomRates().get(1).getRoomTypes().get(0).getRooms().get(0)
        .getRoomPriceBreakdown().getPackageAmount(), is(BigDecimal.valueOf(5)));
    assertThat(availability.getRoomRates().get(1).getRoomTypes().get(0).getRooms().get(0)
        .getRoomPriceBreakdown().getPackageCode(), is("HSATWN"));
    assertThat(
            availability.getRoomRates().get(1)
                    .getRoomTypes().get(0)
                    .getRooms().get(0)
                    .getRoomPriceBreakdown()
                    .getBaseRateAmount(),
            is(nullValue())
    );
  }

  @Test
  void getAvailabilities_WhenBaseRateNotNull_ShouldUpdateBaseRate() {

    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequestMultipleRooms(List.of("TWIN"), null);

    var response = getHotelAvailabilityV1();

    response.getRoomRates().get(1)
            .getRoomTypes().get(0)
            .getRooms().get(0)
            .getRoomPriceBreakdown()
            .setBaseRateAmount(BigDecimal.valueOf(170));

    when(hotelAvailabilityOutboundPort.getHotelAvailability(hotelAvailabilityRequest))
            .thenReturn(response);

    when(packagesInPort.getPackages(any())).thenReturn(
            PackagesResponse.builder()
                    .packages(Packages.builder()
                            .meals(List.of(Meal.builder().id("HSATWN").price(BigDecimal.valueOf(5)).build()))
                            .build())
                    .build());
    when(rulesAgentInPort.getRateSuppressionRule())
            .thenReturn(mockRateSuppressionResponse(
                    Date.from(LocalDateTime.now().plusDays(1)
                            .atZone(ZoneId.systemDefault()).toInstant()),
                    List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX")
            ));

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
            .thenReturn(getMigrationStatusResponse());

    when(hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest)).thenReturn(true);
    when(rateSuppressionProperties.getRoomClasses()).thenReturn(
            Arrays.asList("ST", "BG", "SE", "SV", "BV", "PP", "PV"));

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);

    // Assert
    assertThat(
            availability.getRoomRates().get(1)
                    .getRoomTypes().get(0)
                    .getRooms().get(0)
                    .getRoomPriceBreakdown()
                    .getBaseRateAmount(),
            is(BigDecimal.valueOf(195))
    );
  }

  @Test
  void getAvailabilitiesForTwinRoomAndFamRoomType__ShouldThrowError() {
    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequestMultipleRooms(List.of("FAM", "TWIN"), null);

    when(hotelAvailabilityOutboundPort.getHotelAvailability(hotelAvailabilityRequest))
        .thenReturn(getHotelAvailabilityV2());

    when(packagesInPort.getPackages(any())).thenReturn(PackagesResponse.builder()
        .packages(Packages.builder()
            .meals(Collections.singletonList(Meal.builder().id("HSATWN").price(new BigDecimal(5)).build()))
            .build())
        .build());

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    when(hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest)).thenReturn(true);

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    // Act & Assert
    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
      hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);
    });

    assertEquals("Duplicate key conflict (attempted merging values HSATWN and HSATWN1)", exception.getMessage());
  }

  @Test
  void getAvailabilitiesRateSuppressionUnknownRate__ShouldReturnOK() {

    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(ROOM_TYPES, null, CCUI_BOOKING_CHANNEL);

    when(hotelAvailabilityOutboundPort.getHotelAvailability(hotelAvailabilityRequest))
        .thenReturn(getHotelAvailability());

    when(rateSuppressionProperties.getRoomClasses()).thenReturn(
        Arrays.asList("ST","BG","SE","SV","BV","PP","PV"));

    // "STANDARD" not present in the suppression list, so it will NOT be suppressed
    // the related roomRate will be placed at the end of the list
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(mockRateSuppressionResponse(
        Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()),
        List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "NONFLEX")));

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    when(hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest)).thenReturn(true);

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);

    // Assert
    assertThat(availability, notNullValue());
    assertTrue(availability.isAvailable());
    assertThat(availability.getRoomRates(), hasSize(equalTo(4)));
    assertThat(availability.getRoomRates().get(0).getRatePlanCode(), is("FLEXRATE"));
    assertThat(availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
        .getRoomPriceBreakdown().getTotalNetAmount(), is(BigDecimal.valueOf(150)));
    assertThat(availability.getRoomRates().get(1).getRatePlanCode(), is("NONFLEX"));
    assertThat(availability.getRoomRates().get(1).getRoomTypes().get(0).getRooms().get(0)
        .getRoomPriceBreakdown().getTotalNetAmount(), is(BigDecimal.valueOf(99.9)));
    assertThat(availability.getRoomRates().get(2).getRatePlanCode(), is("STANDARD"));
    assertThat(availability.getRoomRates().get(2).getRoomTypes().get(0).getRooms().get(0)
        .getRoomPriceBreakdown().getTotalNetAmount(), is(BigDecimal.valueOf(100)));
  }

  @Test
  void getAvailabilitiesRateSuppressionExpired__ShouldReturnOK() {

    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(ROOM_TYPES, null, CCUI_BOOKING_CHANNEL);

    when(hotelAvailabilityOutboundPort.getHotelAvailability(hotelAvailabilityRequest))
        .thenReturn(getHotelAvailability());

    when(rateSuppressionProperties.getRoomClasses()).thenReturn(
        Arrays.asList("ST","BG","SE","SV","BV","PP","PV"));

    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(mockRateSuppressionResponse(
        Date.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant()),
        List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX")))
        .thenReturn(
            mockRateSuppressionResponse(
                Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
                List.of("SEMIFLEX", "FLEXRATE", "ADVANCE", "STANDARD", "NONFLEX")));

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    when(hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest)).thenReturn(true);

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    // Act
    hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);

    // Assert
    assertThat(availability, notNullValue());
    assertTrue(availability.isAvailable());
    assertThat(availability.getRoomRates(), hasSize(equalTo(3)));
    assertThat(availability.getRoomRates().get(0).getRatePlanCode(), is("FLEXRATE"));
    assertThat(availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
        .getRoomPriceBreakdown().getTotalNetAmount(), is(BigDecimal.valueOf(150)));
    assertThat(availability.getRoomRates().get(1).getRatePlanCode(), is("STANDARD"));
    assertThat(availability.getRoomRates().get(1).getRoomTypes().get(0).getRooms().get(0)
        .getRoomPriceBreakdown().getTotalNetAmount(), is(BigDecimal.valueOf(100)));
  }

  private RateSuppressionRuleResponse mockRateSuppressionResponse(Date expiryDate,
      List<String> rateSuppressionList) {
    return RateSuppressionRuleResponse.builder()
        .rateSuppressionList(rateSuppressionList)
        .expiryDate(expiryDate)
        .build();
  }

  @Test
  void getAvailabilities__ShouldThrowException() {

    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(ROOM_TYPES, null, CCUI_BOOKING_CHANNEL);
    String error = "Could not fetch data!";

    when(hotelAvailabilityOutboundPort.getHotelAvailability(hotelAvailabilityRequest))
        .thenThrow(new RuntimeException(error));

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    when(hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest)).thenReturn(true);

    // Act
    RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
        () -> hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest));

    //Assert
    Assertions.assertEquals(exception.getMessage(), error);

  }

  @Test
  void getAvailabilities__HubHotelTwinRoom__ShouldReturnOK() {

    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(TWIN_ROOM_TYPES, null, CCUI_BOOKING_CHANNEL);

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    when(hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest)).thenReturn(false);

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);

    // Assert
    assertThat(availability, notNullValue());
    assertFalse(availability.isAvailable());
    assertTrue(availability.getRoomRates().isEmpty());

  }

  @Test
  void getAvailabilities__HubHotel__ShouldReturnOK() {

    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(ROOM_TYPES, null, CCUI_BOOKING_CHANNEL);

    when(hotelAvailabilityOutboundPort.getHotelAvailability(hotelAvailabilityRequest))
        .thenReturn(getHotelAvailability());

    when(rateSuppressionProperties.getRoomClasses()).thenReturn(
        Arrays.asList("ST","BG","SE","SV","BV","PP","PV"));

    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(mockRateSuppressionResponse(
        Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()),
        List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX")));

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    when(hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest)).thenReturn(true);

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);

    // Assert
    assertThat(availability, notNullValue());
    assertTrue(availability.isAvailable());
    assertThat(availability.getRoomRates(), hasSize(equalTo(3)));
  }

  @Test
  void getAvailabilities_WhenMlosFfIsEnabled_ThenMlosIsSetCorrectly() {
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(ROOM_TYPES, null, CCUI_BOOKING_CHANNEL);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(hotelAvailabilityRequest))
          .thenReturn(getHotelAvailability());
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(
          RateSuppressionRuleResponse.builder()
                .rateSuppressionList(Collections.emptyList())
                .expiryDate(Date.from(
                      LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
                .build());
    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
          .thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest)).thenReturn(true);
    when(mlosCommonLogic.isMlosEnabled(CCUI_BOOKING_CHANNEL)).thenReturn(true);
    when(mlosCommonLogic.hasMlosRestriction(eq("TTSSTT"),
        eq(LocalDate.now().format(DATE_FORMATTER)),
        eq(LocalDate.now().plusDays(5).format(DATE_FORMATTER)),
        eq(true), any())).thenReturn(true);
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    var availability = hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);

    verify(mlosCommonLogic, times(1)).isMlosEnabled(CCUI_BOOKING_CHANNEL);
    verify(mlosCommonLogic, times(1)).hasMlosRestriction (eq("TTSSTT"),
        eq(LocalDate.now().format(DATE_FORMATTER)),
        eq(LocalDate.now().plusDays(5).format(DATE_FORMATTER)),
        eq(true), any());
    assertTrue(availability.isMlos());
  }

  @Test
  void getAvailabilities_WhenMlosFfIsDisabled_ThenMlosIsNotSet() {
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(ROOM_TYPES, null, CCUI_BOOKING_CHANNEL);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(hotelAvailabilityRequest))
          .thenReturn(getHotelAvailability());
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(
          RateSuppressionRuleResponse.builder()
                .rateSuppressionList(Collections.emptyList())
                .expiryDate(Date.from(
                      LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
                .build());
    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
          .thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest)).thenReturn(true);
    when(mlosCommonLogic.isMlosEnabled(CCUI_BOOKING_CHANNEL)).thenReturn(false);
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    var availability = hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);

    verify(mlosCommonLogic, times(1)).isMlosEnabled(CCUI_BOOKING_CHANNEL);
    verify(mlosCommonLogic, never()).hasMlosRestriction(eq("TTSSTT"),
        eq(LocalDate.now().format(DATE_FORMATTER)),
        eq(LocalDate.now().plusDays(5).format(DATE_FORMATTER)),
        eq(true), any());
    assertFalse(availability.isMlos());
  }

  @Test
  void getCdhCompanyAccountId_WhenCompanyIdIsNonNumeric_ShouldBypassCompanyEntityServiceAndCallCdhDirectly() {
    // Arrange
    var nonNumericCompanyId = "CORP_ABC";
    var request = getHotelAvailabilityRequest(ROOM_TYPES, null, CCUI_BOOKING_CHANNEL).toBuilder()
        .companyId(nonNumericCompanyId)
        .build();

    var companyRateSuppression = new FeatureFlag.Feature();
    companyRateSuppression.setKey("company-rate-suppression");
    var featureFlag = new FeatureFlag();
    featureFlag.setCompanyRateSuppression(companyRateSuppression);

    when(hotelAvailabilityOutboundPort.getHotelAvailability(any(HotelAvailabilityRequest.class))).thenReturn(getHotelAvailability());
    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(any(HotelAvailabilityRequest.class))).thenReturn(true);
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());
    when(cdhAdapterOutPort.getCompanySuppressRates(nonNumericCompanyId)).thenReturn(Collections.emptyList());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppression)).thenReturn(true);

    // Act
    hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert
    verify(companyEntityServiceOutPort, never()).getCompanyById(any());
    verify(cdhAdapterOutPort).getCompanySuppressRates(nonNumericCompanyId);
  }

  @Test
  void getCdhCompanyAccountId_WhenCompanyIdIsNumeric_ShouldLookUpCorpIdAndBuildCdhSearchRequest() {
    // Arrange
    var numericCompanyId = "12345";
    var corpId = "67890";
    var cdhAccountId = "CDH_ACCOUNT_1";
    var request = getHotelAvailabilityRequest(ROOM_TYPES, null, CCUI_BOOKING_CHANNEL).toBuilder()
        .companyId(numericCompanyId)
        .build();

    var company = Company.builder().corpId(corpId).build();

    var companyRateSuppression = new FeatureFlag.Feature();
    companyRateSuppression.setKey("company-rate-suppression");
    var featureFlag = new FeatureFlag();
    featureFlag.setCompanyRateSuppression(companyRateSuppression);

    when(hotelAvailabilityOutboundPort.getHotelAvailability(any(HotelAvailabilityRequest.class))).thenReturn(getHotelAvailability());
    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(any(HotelAvailabilityRequest.class))).thenReturn(true);
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());
    when(companyEntityServiceOutPort.getCompanyById(numericCompanyId)).thenReturn(company);
    when(cdhAdapterOutPort.getCompanyAccountIdFromCdh(any())).thenReturn(cdhAccountId);
    when(cdhAdapterOutPort.getCompanySuppressRates(cdhAccountId)).thenReturn(Collections.emptyList());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppression)).thenReturn(true);

    // Act
    hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert
    var searchRequestCaptor = ArgumentCaptor.forClass(CdhSearchCompaniesRequest.class);
    verify(companyEntityServiceOutPort).getCompanyById(numericCompanyId);
    verify(cdhAdapterOutPort).getCompanyAccountIdFromCdh(searchRequestCaptor.capture());
    assertEquals(Integer.valueOf(corpId), searchRequestCaptor.getValue().getGlobalCompanyId());
    assertEquals(CCUI_BOOKING_CHANNEL, searchRequestCaptor.getValue().getAccessContext());
    verify(cdhAdapterOutPort).getCompanySuppressRates(cdhAccountId);
  }

  @Test
  void applyCompanyRateSuppression_WhenCdhReturnsSuppressedRates_ShouldRemoveMatchingRatesFromAvailability() {
    // Arrange
    var numericCompanyId = "12345";
    var corpId = "67890";
    var cdhAccountId = "CDH_ACCOUNT_1";
    var request = getHotelAvailabilityRequest(ROOM_TYPES, null, CCUI_BOOKING_CHANNEL).toBuilder()
        .companyId(numericCompanyId)
        .build();

    var company = Company.builder().corpId(corpId).build();

    var companyRateSuppression = new FeatureFlag.Feature();
    companyRateSuppression.setKey("company-rate-suppression");
    var featureFlag = new FeatureFlag();
    featureFlag.setCompanyRateSuppression(companyRateSuppression);

    when(hotelAvailabilityOutboundPort.getHotelAvailability(any(HotelAvailabilityRequest.class))).thenReturn(getHotelAvailability());
    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(any(HotelAvailabilityRequest.class))).thenReturn(true);
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());
    when(companyEntityServiceOutPort.getCompanyById(numericCompanyId)).thenReturn(company);
    when(cdhAdapterOutPort.getCompanyAccountIdFromCdh(any())).thenReturn(cdhAccountId);
    when(cdhAdapterOutPort.getCompanySuppressRates(cdhAccountId)).thenReturn(List.of("FLEXRATE", "SEMIFLEX"));
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppression)).thenReturn(true);

    // Act
    var result = hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert
    var rateCodes = result.getRoomRates().stream().map(RoomRate::getRatePlanCode).toList();
    assertFalse(rateCodes.contains("FLEXRATE"));
    assertFalse(rateCodes.contains("SEMIFLEX"));
    assertTrue(rateCodes.contains("STANDARD"));
  }

  @Test
  void applyCompanyRateSuppression_WhenChannelIsNotBBOrCCUI_ShouldNotInvokeCdhServices() {
    // Arrange
    var request = getHotelAvailabilityRequest(ROOM_TYPES, null, PI_BOOKING_CHANNEL).toBuilder()
        .companyId("12345")
        .build();

    var companyRateSuppression = new FeatureFlag.Feature();
    companyRateSuppression.setKey("company-rate-suppression");
    var featureFlag = new FeatureFlag();
    featureFlag.setCompanyRateSuppression(companyRateSuppression);

    when(hotelAvailabilityOutboundPort.getHotelAvailability(any(HotelAvailabilityRequest.class))).thenReturn(getHotelAvailability());
    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(any(HotelAvailabilityRequest.class))).thenReturn(true);
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppression)).thenReturn(true);

    // Act
    hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert
    verify(companyEntityServiceOutPort, never()).getCompanyById(any());
    verify(cdhAdapterOutPort, never()).getCompanyAccountIdFromCdh(any());
    verify(cdhAdapterOutPort, never()).getCompanySuppressRates(any());
  }

  @Test
  void getAvailabilitiesByIds__ShouldReturnOK() {

    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityByIdsRequest(2,false);

    when(hotelAvailabilityOutboundPort.getHotelAvailabilityByIds(hotelAvailabilityRequest))
        .thenReturn(getHotelAvailabilityByIds());

    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(
        RateSuppressionRuleResponse.builder()
            .rateSuppressionList(Collections.emptyList())
            .expiryDate(Date.from(
                LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
            .build());

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag())
        .thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getOccupancySupplement()))
        .thenReturn(true);
    when(rulesAgentOutPort.getMultiOccupancySupplementPricing(anyList()))
        .thenReturn(mockOccupancySupplementPrices(HOTEL_ID, BigDecimal.ONE));


    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailabilityByIds(hotelAvailabilityRequest);

    // Assert
    assertThat(availability, notNullValue());
    assertTrue(availability.getHotelAvailability().get(0).isAvailable());
    MatcherAssert.assertThat(availability.getHotelAvailability().get(0).getRoomRates(), hasSize(greaterThan(0)));
    assertTrue(availability.getHotelAvailability().get(0).getRoomRates().stream()
        .allMatch(roomRate -> CORPORATE_ID.equals(roomRate.getGlobalCompanyId())));
  }

  @Test
  void getAvailabilitiesByIds__ShouldReturnOK_WithEmptyResponse() {

    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityByIdsRequest(2,false);

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    when(hotelAvailabilityOutboundPort.getHotelAvailabilityByIds(hotelAvailabilityRequest))
        .thenReturn(getHotelAvailabilityByIdsWithEmptyList());

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailabilityByIds(hotelAvailabilityRequest);

    // Assert
    assertThat(availability, notNullValue());
    assertFalse(availability.getHotelAvailability().get(0).isAvailable());
    MatcherAssert.assertThat(availability.getHotelAvailability().get(0).getRoomRates(), hasSize(equalTo(0)));
  }

  @Test
  void getAvailabilitiesByIds_with_Multi_hotel__ShouldReturnOK_WithEmptyResponse_1() {

    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityByIdsRequestForMultiHotel(2,false);

    HotelsMigrationStatusRequest hotelsMigrationStatusRequest = HotelsMigrationStatusRequest.builder()
        .hotelIds(List.of("TTSSTT", "LONEUS"))
        .build();

    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(
        RateSuppressionRuleResponse.builder()
            .rateSuppressionList(Collections.emptyList())
            .expiryDate(Date.from(
                LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
            .build());

    when(onSaleFlagOutPortImpl.getOnSaleFlag(hotelsMigrationStatusRequest))
        .thenReturn(getMigrationStatusResponse());

    when(hotelAvailabilityOutboundPort.getHotelAvailabilityByIds(hotelAvailabilityRequest))
        .thenReturn(getHotelAvailabilityByIds());

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag())
        .thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getOccupancySupplement()))
        .thenReturn(true);
    when(rulesAgentOutPort.getMultiOccupancySupplementPricing(anyList()))
        .thenReturn(mockOccupancySupplementPrices(HOTEL_ID, BigDecimal.ONE));

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailabilityByIds(hotelAvailabilityRequest);

    // Assert
    assertThat(availability, notNullValue());
    assertTrue(availability.getHotelAvailability().get(0).isAvailable());
    MatcherAssert.assertThat(availability.getHotelAvailability().get(0).getRoomRates(), hasSize(greaterThan(0)));

    assertFalse(availability.getHotelAvailability().get(1).isAvailable());
    MatcherAssert.assertThat(availability.getHotelAvailability().get(1).getRoomRates(), hasSize(equalTo(0)));
  }

  @ParameterizedTest
  @CsvSource({"true, true, 2, 155, false", "true, false, 2, 150, false", "true, true, 1, 150, false",
      "true, false, 1, 150, false", "false, true, 2, 200, false", "false, false, 2, 200, false",
      "false, true, 1, 200, false", "false, false, 1, 200, false", "true, true, 1, 155, true",
      "false, true, 1, 205, true", "false, true, 2, 200, true"})
  void getAvailabilitiesByIdsAvCacheAndOpera__ShouldReturnOK(boolean vatNotRequired, boolean featureToggle,
      int noOfAdults, int expectedPrice, boolean isOTA) {

    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityByIdsRequest(noOfAdults, vatNotRequired);
    hotelAvailabilityRequest.setIsOTA(isOTA);
    hotelAvailabilityRequest.setGlobalCompanyId(null);

    var substitutionResponse = RoomSubstitutionRuleResponse.builder()
        .requestDetails(RoomSubstitutionRequestDetails.builder()
            .cotRequired(false)
            .adults(noOfAdults)
            .children(0)
            .build())
        .substitutionList(buildSubstitutionList())
        .build();

    HotelAvailabilityByIds hotelAvailabilityByIds = null;
    if (vatNotRequired) {
      hotelAvailabilityByIds = getHotelAvailabilityByIds(noOfAdults);
    } else {
      hotelAvailabilityByIds = getHotelAvailabilityByIdsOpera(noOfAdults);
    }
    when(availabilityCacheV1SearchOutPort.getHotelAvailabilityByIdsFromAvCache(
        hotelAvailabilityRequest,
        List.of(substitutionResponse)))
        .thenReturn(hotelAvailabilityByIds);

    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(
        RateSuppressionRuleResponse.builder()
            .rateSuppressionList(Collections.emptyList())
            .expiryDate(Date.from(
                LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
            .build());

    when(rulesAgentInPort.getRoomSubstitutionRule(any())).thenReturn(substitutionResponse);

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    if (!vatNotRequired) {
      when(hotelAvailabilityOutboundPort.getHotelMultiRoomsPriceBreakdown(anyString(), any()))
          .thenReturn(getPriceBreakdown());
    }

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag())
        .thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getOccupancySupplement()))
        .thenReturn(featureToggle);

    if (noOfAdults > 1 && !isOTA || isOTA) {
      //var featureFlag = new FeatureFlag();
      when(unleashWrapper.featureFlag())
          .thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getOccupancySupplement()))
          .thenReturn(featureToggle);

      if (featureToggle || isOTA) {
        when(rulesAgentOutPort.getMultiOccupancySupplementPricing(anyList()))
            .thenReturn(mockOccupancySupplementPrices(HOTEL_ID, BigDecimal.ONE));
      }
    }

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailabilityByIds(hotelAvailabilityRequest);

    // Assert
    assertThat(availability, notNullValue());
    assertTrue(availability.getHotelAvailability().get(0).isAvailable());
    MatcherAssert.assertThat(availability.getHotelAvailability().get(0).getRoomRates(), hasSize(greaterThan(0)));
    assertEquals(BigDecimal.valueOf(expectedPrice), availability.getHotelAvailability().get(0).getRoomRates().get(0)
        .getRoomTypes().get(0).getRooms().get(0).getRoomPriceBreakdown().getTotalNetAmount());
  }

  @ParameterizedTest
  @CsvSource({"true, true, 2, false, 145, 160", "true, false, 2, false, 135, 150", "true, true, 1, false, 135, 150",
      "true, false, 1, false, 135, 150", "false, true, 2, false, 135, 150", "false, false, 2, false, 135, 150",
      "false, true, 1, false, 135, 150", "false, false, 1, false, 135, 150", "true, true, 1, true, 145, 160",
      "true, true, 2, true, 145, 160", "true, false, 1, true, 135, 150", "false, true, 1, true, 145, 160",
      "false, true, 2, true, 135, 150","false, false, 1, true, 135, 150"})
  void getAvailabilitiesByIdsV2AvCache__ShouldReturnOK(boolean vatNotRequired, boolean featureToggle, int noOfAdults, boolean isOTA,
                                                       int expectedPriceBeforeTax, int expectedPriceAfterTax) {

    // Arrange
    var hotelAvailabilityRequestV2 = getHotelAvailabilityByIdsV2Request(noOfAdults, vatNotRequired, isOTA);
    hotelAvailabilityRequestV2.getRates().setRatePlanCodes(new ArrayList<>());

    var substitutionResponse = getSubstitutionResponse();

    when(availabilityCacheV1SearchOutPort.getHotelAvailabilityByIdsV2FromAvCache(hotelAvailabilityRequestV2,
        List.of(substitutionResponse)))
        .thenReturn(getHotelAvailabilityByIdsV2());

    when(rulesAgentInPort.getRoomSubstitutionRule(any())).thenReturn(substitutionResponse);

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    if (!vatNotRequired) {
      when(hotelAvailabilityOutboundPort.getHotelMultiRoomsPriceBreakdown(any(), any()))
          .thenReturn(getPriceBreakdownV2());
    }

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag())
        .thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getOccupancySupplement()))
        .thenReturn(featureToggle);

    if (noOfAdults > 1 || isOTA) {
      //var featureFlag = new FeatureFlag();
      when(unleashWrapper.featureFlag())
          .thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getOccupancySupplement()))
          .thenReturn(featureToggle);

      if (featureToggle) {
        when(rulesAgentOutPort.getMultiOccupancySupplementPricing(anyList()))
            .thenReturn(mockOccupancySupplementPrices(HOTEL_ID, BigDecimal.TEN));
      }
    }

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailabilityByIdsV2(hotelAvailabilityRequestV2);

    // Assert
    assertThat(availability, notNullValue());
    assertEquals(HOTEL_ID, availability.getHotelAvailability().get(0).getHotelId());
    MatcherAssert.assertThat(availability.getHotelAvailability().get(0).getRoomStays(), hasSize(greaterThan(0)));
    assertEquals(BigDecimal.valueOf(expectedPriceBeforeTax), availability.getHotelAvailability().get(0).getRoomStays().get(0)
        .getRoomTypes().get(0).getRoomRates().get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountBeforeTax());
    assertEquals(BigDecimal.valueOf(expectedPriceAfterTax), availability.getHotelAvailability().get(0).getRoomStays().get(0)
        .getRoomTypes().get(0).getRoomRates().get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountAfterTax());
  }

  @Test
  void getAvailabilitiesByIdsV2CorporateRates__ShouldReturnOK() {

    // Arrange
    var hotelAvailabilityRequestV2 = getHotelAvailabilityByIdsV2Request(2, false, false);
    hotelAvailabilityRequestV2.getRates().setCorporateRates(List.of(getCorporateRates()));

    when(hotelAvailabilityOutboundPort.getHotelAvailabilityByIdsV2(hotelAvailabilityRequestV2))
        .thenReturn(getHotelAvailabilityByIdsV2());

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag())
            .thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getOccupancySupplement()))
            .thenReturn(false);

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailabilityByIdsV2(hotelAvailabilityRequestV2);

    // Assert
    assertThat(availability, notNullValue());
    assertEquals(HOTEL_ID, availability.getHotelAvailability().get(0).getHotelId());
    MatcherAssert.assertThat(availability.getHotelAvailability().get(0).getRoomStays(), hasSize(greaterThan(0)));
    assertTrue(availability.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().get(0).getRoomRates().stream()
        .allMatch(roomRateV2 -> CORPORATE_ID.equals(roomRateV2.getGlobalCompanyId())));
  }

  @Test
  void getAvailabilitiesByIdsV2CorporateRates__ShouldReturnOK_OccupancySupplement() {

    // Arrange
    var hotelAvailabilityRequestV2 = getHotelAvailabilityByIdsV2Request(1, false, true);
    hotelAvailabilityRequestV2.getRates().setCorporateRates(List.of(getCorporateRates()));
    var hotelAvailabilityByIdsV2 = getHotelAvailabilityByIdsV2();
    hotelAvailabilityByIdsV2.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().get(0).setAdults("1");
    hotelAvailabilityByIdsV2.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().get(0).setNumberOfRooms("2");

    when(hotelAvailabilityOutboundPort.getHotelAvailabilityByIdsV2(hotelAvailabilityRequestV2))
            .thenReturn(hotelAvailabilityByIdsV2);

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
            .thenReturn(getMigrationStatusResponse());

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag())
            .thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getOccupancySupplement()))
            .thenReturn(true);
    when(rulesAgentOutPort.getMultiOccupancySupplementPricing(anyList()))
            .thenReturn(mockOccupancySupplementPrices(HOTEL_ID, BigDecimal.TEN));

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailabilityByIdsV2(hotelAvailabilityRequestV2);

    // Assert
    assertThat(availability, notNullValue());
    assertEquals(HOTEL_ID, availability.getHotelAvailability().get(0).getHotelId());
    MatcherAssert.assertThat(availability.getHotelAvailability().get(0).getRoomStays(), hasSize(greaterThan(0)));
    assertTrue(availability.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().get(0).getRoomRates().stream()
            .allMatch(roomRateV2 -> CORPORATE_ID.equals(roomRateV2.getGlobalCompanyId())));
    assertEquals(BigDecimal.valueOf(135 + 10*2), availability.getHotelAvailability().get(0).getRoomStays().get(0)
        .getRoomTypes().get(0).getRoomRates().get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountBeforeTax());
    assertEquals(BigDecimal.valueOf(150 + 10*2), availability.getHotelAvailability().get(0).getRoomStays().get(0)
            .getRoomTypes().get(0).getRoomRates().get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountAfterTax());
  }

  @Test
  void testAddRoomPriceBreakDown_throwsException() {
    var arrivalDate = "arrivalDate";
    var departureDate = "departureDate";
    var hotelAvailabilityByIds = getHotelAvailabilityByIds(2);
    var hotelAvailability = HotelAvailability.builder().
        roomRates(List.of(RoomRate.builder().roomTypes(
            List.of(RoomTypeInfo.builder().roomType("a").build())).build())).build();
    hotelAvailabilityByIds.setHotelAvailability(List.of(hotelAvailability));

    when(hotelAvailabilityOutboundPort.getHotelMultiRoomsPriceBreakdown(any(), any()))
        .thenReturn(getPriceBreakdown());

    //Act
    var exception = assertThrows(NoAvailabilityException.class,
        () -> {
          ReflectionTestUtils.invokeMethod(hotelAvailabilityInboundPort, "addRoomPriceBreakdown", arrivalDate,
              departureDate, hotelAvailabilityByIds);
        });
    //Assert
    String actualMessage = exception.getMessage();
    assertEquals("No availability", actualMessage);
  }

  private static CorporateRate getCorporateRates() {
    return CorporateRate.builder()
        .corporateId(CORPORATE_ID)
        .ratePlanSets(List.of(NEG_RATE_PLAN))
        .build();
  }

  private RoomSubstitutionRuleResponse getSubstitutionResponse() {
    return RoomSubstitutionRuleResponse.builder()
        .requestDetails(RoomSubstitutionRequestDetails.builder()
            .cotRequired(false)
            .roomType("DB")
            .adults(2)
            .children(0)
            .build())
        .substitutionList(buildSubstitutionListV2())
        .build();
  }

  private List<RoomSubstitution> buildSubstitutionList() {
    return List.of(RoomSubstitution.builder()
        .type("DOUBLE")
        .silent(true)
        .build(),
        RoomSubstitution.builder()
                .type("TWINRM")
                .silent(true)
                .codePackage("HSATWN")
                .build());
  }

  private List<RoomSubstitution> buildSubstitutionListFam() {
    return List.of(RoomSubstitution.builder()
        .type("TWINRM")
        .silent(false)
        .codePackage("HSATWN")
        .build());
  }

  private List<RoomSubstitution> buildSubstitutionListFamHSATWN2() {
    return List.of(RoomSubstitution.builder()
        .type("TWINRM")
        .silent(false)
        .codePackage("HSATWN2")
        .build());
  }

  private List<RoomSubstitution> buildSubstitutionListTwin() {
    return List.of(RoomSubstitution.builder()
        .type("TWINRM")
        .silent(true)
        .codePackage("HSATWN")
        .build());
  }

  private List<RoomSubstitution> buildSubstitutionListV2() {
    return List.of(RoomSubstitution.builder()
        .type("DOUBLE")
        .build());
  }

  private Mono<RoomPriceBreakdownResult> getPriceBreakdown() {
    return Mono.just(RoomPriceBreakdownResult.builder()
        .priceBreakdown(List.of(
            RoomPriceBreakdown.builder()
                .currencyCode("EUR")
                .totalNetAmount(BigDecimal.valueOf(200))
                .build()))
        .build());
  }


  private Mono<RoomPriceBreakdownResult> getPriceBreakdownV2() {
    return Mono.just(RoomPriceBreakdownResult.builder()
        .priceBreakdown(List.of(
            RoomPriceBreakdown.builder()
                .currencyCode("EUR")
                .dailyPrices(List.of(
                    DailyPrice.builder().date(ARRIVAL_DATE_V2.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .netPrice(BigDecimal.valueOf(150)).grossPrice(BigDecimal.valueOf(135)).build()))
                .totalNetAmount(BigDecimal.valueOf(200))
                .build()))
        .build());
  }

  private HotelAvailabilityByIds getHotelAvailabilityByIds() {
    return getHotelAvailabilityByIds(null);
  }

  private HotelAvailabilityByIds getHotelAvailabilityByIdsWithEmptyList() {
    return HotelAvailabilityByIds.builder()
        .hotelAvailability(new ArrayList<>())
        .build();
  }

  private HotelAvailabilityByIds getHotelAvailabilityByIds(Integer noOfAdults) {
    return HotelAvailabilityByIds.builder()
        .hotelAvailability(new ArrayList<>(List.of(getHotelAvailability(noOfAdults))))
        .build();
  }

  private HotelAvailabilityByIds getHotelAvailabilityByIdsOpera(Integer noOfAdults) {
    return HotelAvailabilityByIds.builder()
        .hotelAvailability(new ArrayList<>(List.of(getHotelAvailabilityOpera(noOfAdults))))
        .build();
  }


  private HotelAvailabilityByIdsV2 getHotelAvailabilityByIdsV2() {
    return HotelAvailabilityByIdsV2.builder()
        .hotelAvailability(List.of(HotelAvailabilityResultV2.builder()
            .hotelId(HOTEL_ID)
            .roomStays(List.of(getRoomStaysV2()))
            .build()))
        .build();
  }

  private HotelAvailabilityByIdsRequest getHotelAvailabilityByIdsRequest(int noOfAdults, boolean vatNotRequired) {
    return HotelAvailabilityByIdsRequest.builder()
        .hotelIds(new ArrayList<>(List.of(HOTEL_ID)))
        .roomTypes(ROOM_TYPES)
        .adultsNumber(List.of(noOfAdults))
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .channel(DISTR_BOOKING_CHANNEL)
        .globalCompanyId(CORPORATE_ID)
        .vatNotRequired(vatNotRequired)
        .build();
  }

  private HotelAvailabilityByIdsRequest getHotelAvailabilityByIdsRequestForMultiHotel(int noOfAdults, boolean vatNotRequired) {
    return HotelAvailabilityByIdsRequest.builder()
        .hotelIds(new ArrayList<>(List.of("TTSSTT", "LONEUS")))
        .roomTypes(ROOM_TYPES)
        .adultsNumber(List.of(noOfAdults))
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .channel(DISTR_BOOKING_CHANNEL)
        .globalCompanyId(CORPORATE_ID)
        .vatNotRequired(vatNotRequired)
        .build();
  }

  private HotelAvailabilityByIdsV2Request getHotelAvailabilityByIdsV2Request(int numberOfAdults,
      boolean vatNotRequired, boolean isOTA) {
    LocalDate arrivalDate = LocalDate.now().plusDays(2);
    LocalDate departureDate = arrivalDate.plusDays(1);
    return HotelAvailabilityByIdsV2Request.builder()
        .hotelIds(new ArrayList<>(List.of(HOTEL_ID)))
        .rooms(new ArrayList<>(List.of(uk.co.whitbread.domain.model.availability.in.Room.builder()
            .tag("DB")
            .adults(numberOfAdults)
            .children(0)
            .numberOfRooms(1)
            .build())))
        .arrivalDate(arrivalDate)
        .departureDate(departureDate)
        .rates(RateV2.builder()
            .corporateRates(List.of())
            .ratePlanCodes(List.of("FLEXRATE"))
            .build())
        .bookingChannel(BookingChannel.builder()
            .channel(DISTR_BOOKING_CHANNEL)
            .language("en")
            .subchannel("WEB")
            .build())
        .vatNotRequired(vatNotRequired).isOTA(isOTA)
        .build();
  }

  private HotelAvailabilityRequest getHotelAvailabilityRequest(List<String> roomTypes,
      String ratePlanCode, String channel) {

    return HotelAvailabilityRequest.builder()
        .channel(channel)
        .hotelId(HOTEL_ID)
        .roomTypes(roomTypes)
        .adultsNumber(ADULTS)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .ratePlanCodes(Collections.singletonList(ratePlanCode))
        .country("GB")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build();
  }

  private HotelAvailabilityRequest getHotelAvailabilityRequestMultipleRooms(List<String> roomTypes,
      String ratePlanCode) {

    return HotelAvailabilityRequest.builder()
        .channel("PI")
        .hotelId(HOTEL_ID)
        .roomTypes(roomTypes)
        .adultsNumber(List.of(1, 2))
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(List.of(1, 0))
        .cotsRequired(List.of(false, false))
        .ratePlanCodes(Collections.singletonList(ratePlanCode))
        .country("DE")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build();
  }

  private HotelAvailability getHotelAvailability() {
    return getHotelAvailability(null);
  }

  private HotelAvailability getHotelAvailability(Integer noOfAdults) {
    var roomRate1 = RoomRate.builder()
        .ratePlanCode("FLEXRATE")
        .roomType(RoomTypeInfo.builder()
            .roomType("DB")
            .adults(noOfAdults)
            .room(Room.builder()
                .roomClass("ST")
                .roomPriceBreakdown(RoomPriceBreakdown.builder()
                    .totalNetAmount(BigDecimal.valueOf(150))
                    .effectiveRateAmount(BigDecimal.valueOf(135))
                    .build())
                .pmsRoomType("")
                .build())
            .build())
        .build();

    // Should be suppressed if applying suppression rule
    var roomRate2 = RoomRate.builder()
        .ratePlanCode("SEMIFLEX")
        .roomType(RoomTypeInfo.builder()
            .roomType("DB")
            .adults(noOfAdults)
            .room(Room.builder()
                .roomClass("ST")
                .roomPriceBreakdown(RoomPriceBreakdown.builder()
                    .totalNetAmount(BigDecimal.valueOf(150))
                    .effectiveRateAmount(BigDecimal.valueOf(135))
                    .build())
                .build())
            .build())
        .build();

    // Should be suppressed if applying suppression rule
    var roomRate3 = RoomRate.builder()
        .ratePlanCode("ADVANCE")
        .roomType(RoomTypeInfo.builder()
            .roomType("DB")
            .adults(noOfAdults)
            .room(Room.builder()
                .roomClass("ST")
                .roomPriceBreakdown(RoomPriceBreakdown.builder()
                    .totalNetAmount(BigDecimal.valueOf(149.5))
                    .effectiveRateAmount(BigDecimal.valueOf(135))
                    .build())
                .build())
            .build())
        .build();

    var roomRate4 = RoomRate.builder()
        .ratePlanCode("STANDARD")
        .roomType(RoomTypeInfo.builder()
            .roomType("DB")
            .adults(noOfAdults)
            .room(Room.builder()
                .roomClass("ST")
                .roomPriceBreakdown(RoomPriceBreakdown.builder()
                    .totalNetAmount(BigDecimal.valueOf(100))
                    .effectiveRateAmount(BigDecimal.valueOf(85))
                    .build())
                .build())
            .build())
        .build();

    // Should be suppressed if applying suppression rule
    var roomRate5 = RoomRate.builder()
        .ratePlanCode("NONFLEX")
        .roomType(RoomTypeInfo.builder()
            .roomType("DB")
            .room(Room.builder()
                .roomClass("ST")
                .roomPriceBreakdown(RoomPriceBreakdown.builder()
                    .totalNetAmount(BigDecimal.valueOf(99.9))
                    .effectiveRateAmount(BigDecimal.valueOf(85))
                    .build())
                .build())
            .build())
        .build();

    var roomRate6 = RoomRate.builder()
            .ratePlanCode("EMPLOYEE")
            .roomType(RoomTypeInfo.builder()
                    .roomType("DB")
                    .room(Room.builder()
                            .roomClass("ST")
                            .roomPriceBreakdown(RoomPriceBreakdown.builder()
                                    .totalNetAmount(BigDecimal.valueOf(60))
                                    .effectiveRateAmount(BigDecimal.valueOf(50))
                                    .build())
                            .build())
                    .build())
            .build();

    return HotelAvailability.builder()
        .hotelId(HOTEL_ID)
        .startDate(ARRIVAL_DATE)
        .endDate(DEPARTURE_DATE)
        .available(true)
        .roomRate(roomRate1)
        .roomRate(roomRate2)
        .roomRate(roomRate3)
        .roomRate(roomRate4)
        .roomRate(roomRate5)
        .roomRate(roomRate6)
        .build();
  }

  private HotelAvailability getHotelAvailabilityNoRooms() {
    return HotelAvailability.builder()
        .hotelId(HOTEL_ID)
        .available(true)
        .build();
  }

  private HotelAvailability getHotelAvailabilityForRateSuppresseion(List<String> list, final Set<String> roomClasses) {
    var builder = HotelAvailability.builder()
        .hotelId(HOTEL_ID)
        .startDate(ARRIVAL_DATE)
        .endDate(DEPARTURE_DATE)
        .available(true);
    list.stream().forEach(elem -> {
      var roomRate = RoomRate.builder()
          .ratePlanCode(elem)
          .roomType(RoomTypeInfo.builder()
              .roomType("DB")
              .adults(1)
              .rooms(getRoomRates(roomClasses))
              .build())
          .build();
      builder.roomRate(roomRate);
    });
    return builder.build();
  }

  private List<Room> getRoomRates(final Set<String> roomClasses){
    final List<Room> rooms = new ArrayList<>();
    for(final String roomClass: roomClasses){
      rooms.add(Room.builder()
          .roomClass(roomClass)
          .roomPriceBreakdown(RoomPriceBreakdown.builder()
              .totalNetAmount(BigDecimal.valueOf(90))
              .build())
          .pmsRoomType("")
          .build());
    }
    return rooms;
  }


  private HotelAvailability getHotelAvailabilityOpera(Integer noOfAdults) {
    var roomRate1 = RoomRate.builder()
        .ratePlanCode("FLEXRATE")
        .roomType(RoomTypeInfo.builder()
            .roomType("DB")
            .adults(noOfAdults)
            .room(Room.builder()
                .roomClass("ST")
                .roomPriceBreakdown(RoomPriceBreakdown.builder()
                    .totalNetAmount(BigDecimal.valueOf(150))
                    .build())
                .pmsRoomType("")
                .build())
            .build())
        .build();

    var roomRate2 = RoomRate.builder()
        .ratePlanCode("STANDARD")
        .roomType(RoomTypeInfo.builder()
            .roomType("DB")
            .adults(null)
            .room(Room.builder()
                .roomClass("ST")
                .roomPriceBreakdown(RoomPriceBreakdown.builder()
                    .totalNetAmount(BigDecimal.valueOf(100))
                    .build())
                .build())
            .build())
        .build();

    return HotelAvailability.builder()
        .hotelId(HOTEL_ID)
        .startDate(ARRIVAL_DATE)
        .endDate(DEPARTURE_DATE)
        .available(true)
        .roomRate(roomRate1)
        .roomRate(roomRate2)
        .build();
  }


  private HotelAvailability getHotelAvailabilityV1() {
    return HotelAvailability.builder()
            .hotelId(HOTEL_ID)
            .startDate(ARRIVAL_DATE)
            .endDate(DEPARTURE_DATE)
            .available(true)
            .roomRates(getRoomRates())
            .substitutionList(List.of(RoomSubstitution.builder()
                    .type("TWINRM")
                    .codePackage("HSATWN")
                    .build(),
                RoomSubstitution.builder()
                    .type("FMTRPL")
                    .codePackage("HSATWN")
                    .build()))
            .build();
  }

  private HotelAvailability getHotelAvailabilityV2() {
    return HotelAvailability.builder()
        .hotelId(HOTEL_ID)
        .startDate(ARRIVAL_DATE)
        .endDate(DEPARTURE_DATE)
        .available(true)
        .roomRates(getRoomRates())
        .substitutionList(List.of(RoomSubstitution.builder()
                .type("TWINRM")
                .codePackage("HSATWN")
                .build(),
            RoomSubstitution.builder()
                .type("TWINRM")
                .codePackage("HSATWN1")
                .build()))
        .build();
  }

  private List<RoomRate> getRoomRates() {
    var roomRate1 = RoomRate.builder()
        .ratePlanCode("FLEXRATE")
        .roomType(RoomTypeInfo.builder()
            .roomType("TWIN")
            .rooms(Arrays.asList(Room.builder()
                    .roomClass("ST")
                    .pmsRoomType("TWINRM")
                    .specialRequests(Collections.singletonList("TW2S"))
                    .roomPriceBreakdown(RoomPriceBreakdown.builder()
                        .totalNetAmount(BigDecimal.valueOf(102))
                        .effectiveRateAmount(BigDecimal.valueOf(100))
                        .dailyPrices(Collections.singletonList(DailyPrice.builder()
                            .netPrice(BigDecimal.valueOf(102))
                            .effectiveRate(BigDecimal.valueOf(100))
                            .build()))
                        .build())
                    .build(),
                Room.builder()
                    .roomClass("ST")
                    .pmsRoomType("FMTRPL")
                    .specialRequests(Collections.singletonList("TWDS"))
                    .roomPriceBreakdown(RoomPriceBreakdown.builder()
                        .totalNetAmount(BigDecimal.valueOf(102))
                        .effectiveRateAmount(BigDecimal.valueOf(100))
                        .dailyPrices(Collections.singletonList(DailyPrice.builder()
                            .netPrice(BigDecimal.valueOf(102))
                            .effectiveRate(BigDecimal.valueOf(100))
                            .build()))
                        .build())
                    .build()))
            .build())
        .build();

    // Should be suppressed if applying suppression rule
    var roomRate2 = RoomRate.builder()
        .ratePlanCode("SEMIFLEX")
        .roomType(RoomTypeInfo.builder()
            .roomType("TWIN")
            .room(Room.builder()
                .roomClass("ST")
                .pmsRoomType("TWINRM")
                .specialRequests(Collections.singletonList("TW2S"))
                .roomPriceBreakdown(RoomPriceBreakdown.builder()
                    .totalNetAmount(BigDecimal.valueOf(150))
                    .effectiveRateAmount(BigDecimal.valueOf(120))
                    .build())
                .build())
            .build())
        .build();

    // Should be suppressed if applying suppression rule
    var roomRate3 = RoomRate.builder()
        .ratePlanCode("ADVANCE")
        .roomType(RoomTypeInfo.builder()
            .roomType("TWIN")
            .room(Room.builder()
                .roomClass("ST")
                .pmsRoomType("TWINRM")
                .specialRequests(Collections.singletonList("TW2S"))
                .roomPriceBreakdown(RoomPriceBreakdown.builder()
                    .totalNetAmount(BigDecimal.valueOf(149.5))
                    .effectiveRateAmount(BigDecimal.valueOf(149.5))
                    .build())
                .build())
            .build())
        .build();

    var roomRate4 = RoomRate.builder()
        .ratePlanCode("STANDARD")
        .roomType(RoomTypeInfo.builder()
            .roomType("TWIN")
            .room(Room.builder()
                .roomClass("ST")
                .pmsRoomType("TWINRM")
                .specialRequests(Collections.singletonList("TW2S"))
                .roomPriceBreakdown(RoomPriceBreakdown.builder()
                    .totalNetAmount(BigDecimal.valueOf(100))
                    .effectiveRateAmount(BigDecimal.valueOf(100))
                    .build())
                .build())
            .build())
        .build();

    // Should be suppressed if applying suppression rule
    var roomRate5 = RoomRate.builder()
        .ratePlanCode("NONFLEX")
        .roomType(RoomTypeInfo.builder()
            .roomType("TWIN")
            .room(Room.builder()
                .roomClass("ST")
                .pmsRoomType("TWINRM")
                .specialRequests(Collections.singletonList("TW2S"))
                .roomPriceBreakdown(RoomPriceBreakdown.builder()
                    .totalNetAmount(BigDecimal.valueOf(99.9))
                    .effectiveRateAmount(BigDecimal.valueOf(99.9))
                    .build())
                .build())
            .build())
        .build();

    var roomRate6 = RoomRate.builder()
        .ratePlanCode("EMPLOYEE")
        .roomType(RoomTypeInfo.builder()
            .roomType("TWIN")
            .room(Room.builder()
                .roomClass("ST")
                .pmsRoomType("TWINRM")
                .roomPriceBreakdown(RoomPriceBreakdown.builder()
                    .totalNetAmount(BigDecimal.valueOf(60))
                    .effectiveRateAmount(BigDecimal.valueOf(60))
                    .build())
                .build())
            .build())
        .build();
    return List.of(roomRate1, roomRate2, roomRate3, roomRate4, roomRate5, roomRate6);
  }

  private RoomStay getRoomStaysV2() {
    var roomRate1 = RoomRateV2.builder()
        .ratePlanCode("FLEXRATE")
        .displaySet(null)
        .currencyCode("GBP")
        .globalCompanyId(CORPORATE_ID)
        .roomRateInfo(RoomRateInfoV2.builder()
            .priceInfo(List.of(PriceInfo.builder()
                .stayDate(ARRIVAL_DATE_V2)
                .amountBeforeTax(BigDecimal.valueOf(135))
                .amountAfterTax(BigDecimal.valueOf(150))
                .build()))
            .build())
        .build();

    var roomRate2 = RoomRateV2.builder()
        .ratePlanCode("STANDARD")
        .displaySet(null)
        .currencyCode("GBP")
        .globalCompanyId(CORPORATE_ID)
        .roomRateInfo(RoomRateInfoV2.builder()
            .priceInfo(List.of(PriceInfo.builder()
                .stayDate(ARRIVAL_DATE_V2)
                .amountBeforeTax(BigDecimal.valueOf(132))
                .amountAfterTax(BigDecimal.valueOf(148))
                .build()))
            .build())
        .build();

    var roomRate3 = RoomRateV2.builder()
        .ratePlanCode("ADVANCE")
        .displaySet(null)
        .currencyCode("GBP")
        .globalCompanyId(CORPORATE_ID)
        .roomRateInfo(RoomRateInfoV2.builder()
            .priceInfo(List.of(PriceInfo.builder()
                .stayDate(ARRIVAL_DATE_V2)
                .amountBeforeTax(BigDecimal.valueOf(132))
                .amountAfterTax(BigDecimal.valueOf(148))
                .build()))
            .build())
        .build();

    var roomRate4 = RoomRateV2.builder()
        .ratePlanCode("NONFLEX")
        .displaySet(null)
        .currencyCode("GBP")
        .globalCompanyId(CORPORATE_ID)
        .roomRateInfo(RoomRateInfoV2.builder()
            .priceInfo(List.of(PriceInfo.builder()
                .stayDate(ARRIVAL_DATE_V2)
                .amountBeforeTax(BigDecimal.valueOf(132))
                .amountAfterTax(BigDecimal.valueOf(148))
                .build()))
            .build())
        .build();

    return uk.co.whitbread.domain.model.availability.out.RoomStay.builder()
        .roomClass("STANDARD")
        .roomTypes(new ArrayList<>(Collections.singletonList(
                RoomTypeV2.builder()
                        .roomType("DOUBLE")
                        .tag("DB")
                        .roomRates(List.of(roomRate1, roomRate2, roomRate3, roomRate4)).build()))
            ).build();
  }

  // =====================================================================
  // Company Rate Suppression Tests
  // =====================================================================

  @Test
  void getHotelAvailability_companyRateSuppression_flagEnabled_BBChannel_shouldRemoveSuppressedRates() {
    // Arrange
    var request = HotelAvailabilityRequest.builder()
        .channel(BB_BOOKING_CHANNEL)
        .hotelId(HOTEL_ID)
        .roomTypes(ROOM_TYPES)
        .adultsNumber(ADULTS)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .companyId("CORP123")
        .country("DE")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(request)).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(request)).thenReturn(getHotelAvailability());
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());

    var featureFlag = new FeatureFlag();
    var companyRateSuppressionFeature = new FeatureFlag.Feature();
    featureFlag.setCompanyRateSuppression(companyRateSuppressionFeature);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppressionFeature)).thenReturn(true);
    when(cdhAdapterOutPort.getCompanySuppressRates("CORP123")).thenReturn(List.of("SEMIFLEX", "NONFLEX"));

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert
    List<String> rateCodes = availability.getRoomRates().stream().map(RoomRate::getRatePlanCode).toList();
    assertFalse(rateCodes.contains("SEMIFLEX"));
    assertFalse(rateCodes.contains("NONFLEX"));
    assertTrue(rateCodes.contains("FLEXRATE"));
    assertTrue(rateCodes.contains("STANDARD"));
    verify(cdhAdapterOutPort, times(1)).getCompanySuppressRates("CORP123");
  }

  @Test
  void getHotelAvailability_companyRateSuppression_flagEnabled_CCUIChannel_shouldRemoveSuppressedRates() {
    // Arrange
    var request = HotelAvailabilityRequest.builder()
        .channel(CCUI_BOOKING_CHANNEL)
        .hotelId(HOTEL_ID)
        .roomTypes(ROOM_TYPES)
        .adultsNumber(ADULTS)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .companyId("CORP123")
        .country("GB")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(request)).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(request)).thenReturn(getHotelAvailability());
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());

    var featureFlag = new FeatureFlag();
    var companyRateSuppressionFeature = new FeatureFlag.Feature();
    featureFlag.setCompanyRateSuppression(companyRateSuppressionFeature);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppressionFeature)).thenReturn(true);
    when(cdhAdapterOutPort.getCompanySuppressRates("CORP123")).thenReturn(List.of("SEMIFLEX", "NONFLEX"));

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert
    List<String> rateCodes = availability.getRoomRates().stream().map(RoomRate::getRatePlanCode).toList();
    assertFalse(rateCodes.contains("SEMIFLEX"));
    assertFalse(rateCodes.contains("NONFLEX"));
    assertTrue(rateCodes.contains("FLEXRATE"));
    verify(cdhAdapterOutPort, times(1)).getCompanySuppressRates("CORP123");
  }

  @Test
  void getHotelAvailability_companyRateSuppression_flagDisabled_shouldNotCallCDH() {
    // Arrange
    var request = HotelAvailabilityRequest.builder()
        .channel(BB_BOOKING_CHANNEL)
        .hotelId(HOTEL_ID)
        .roomTypes(ROOM_TYPES)
        .adultsNumber(ADULTS)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .companyId("CORP123")
        .country("GB")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(request)).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(request)).thenReturn(getHotelAvailability());
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert
    assertThat(availability.getRoomRates(), hasSize(6));
    verify(cdhAdapterOutPort, never()).getCompanySuppressRates(anyString());
  }

  @Test
  void getHotelAvailability_companyRateSuppression_flagEnabled_nonBBOrCCUIChannel_shouldNotCallCDH() {
    // Arrange
    var request = HotelAvailabilityRequest.builder()
        .channel(PI_BOOKING_CHANNEL)
        .hotelId(HOTEL_ID)
        .roomTypes(ROOM_TYPES)
        .adultsNumber(ADULTS)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .companyId("CORP123")
        .country("GB")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(request)).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(request)).thenReturn(getHotelAvailability());
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());

    var featureFlag = new FeatureFlag();
    var companyRateSuppressionFeature = new FeatureFlag.Feature();
    featureFlag.setCompanyRateSuppression(companyRateSuppressionFeature);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppressionFeature)).thenReturn(true);

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert
    assertThat(availability.getRoomRates(), hasSize(6));
    verify(cdhAdapterOutPort, never()).getCompanySuppressRates(anyString());
  }

  @Test
  void getHotelAvailability_companyRateSuppression_flagEnabled_noCompanyId_shouldNotCallCDH() {
    // Arrange
    var request = HotelAvailabilityRequest.builder()
        .channel(BB_BOOKING_CHANNEL)
        .hotelId(HOTEL_ID)
        .roomTypes(ROOM_TYPES)
        .adultsNumber(ADULTS)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .country("GB")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build(); // no companyId

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(request)).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(request)).thenReturn(getHotelAvailability());
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());

    var featureFlag = new FeatureFlag();
    var companyRateSuppressionFeature = new FeatureFlag.Feature();
    featureFlag.setCompanyRateSuppression(companyRateSuppressionFeature);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppressionFeature)).thenReturn(true);

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert
    assertThat(availability.getRoomRates(), hasSize(6));
    verify(cdhAdapterOutPort, never()).getCompanySuppressRates(anyString());
  }

  @Test
  void getHotelAvailability_companyRateSuppression_flagEnabled_emptySuppressedRates_shouldKeepAllRates() {
    // Arrange
    var request = HotelAvailabilityRequest.builder()
        .channel(BB_BOOKING_CHANNEL)
        .hotelId(HOTEL_ID)
        .roomTypes(ROOM_TYPES)
        .adultsNumber(ADULTS)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .companyId("CORP123")
        .country("DE")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(request)).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(request)).thenReturn(getHotelAvailability());
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());

    var featureFlag = new FeatureFlag();
    var companyRateSuppressionFeature = new FeatureFlag.Feature();
    featureFlag.setCompanyRateSuppression(companyRateSuppressionFeature);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppressionFeature)).thenReturn(true);
    when(cdhAdapterOutPort.getCompanySuppressRates("CORP123")).thenReturn(Collections.emptyList());

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert
    assertThat(availability.getRoomRates(), hasSize(6));
  }

  @Test
  void getHotelAvailability_busiFlexCdhSuppressed_negotiatedRateStillPresent_shouldHideFlexRate() {
    // Arrange
    // Opera returns FLEXRATE, BUSIFLEX, a negotiated rate (CORPRATE) and public rates
    var request = HotelAvailabilityRequest.builder()
        .channel(BB_BOOKING_CHANNEL)
        .hotelId(HOTEL_ID)
        .roomTypes(ROOM_TYPES)
        .adultsNumber(ADULTS)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .companyId("CORP123")
        .country("GB")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build();

    var hotelAvailabilityWithNegotiatedRate = getHotelAvailabilityForRateSuppresseion(
        List.of("FLEXRATE", "BUSIFLEX", "CORPRATE", "SEMIFLEX", "STANDARD"),
        new HashSet<>(Arrays.asList("ST")));

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(request)).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(request))
        .thenReturn(hotelAvailabilityWithNegotiatedRate);
    // Rate suppression list = standard public rates (CORPRATE and BUSIFLEX are not public)
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(List.of("FLEXRATE", "SEMIFLEX", "STANDARD"))
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());
    when(rateSuppressionProperties.getRoomClasses()).thenReturn(List.of("ST"));

    var featureFlag = new FeatureFlag();
    var companyRateSuppressionFeature = new FeatureFlag.Feature();
    featureFlag.setCompanyRateSuppression(companyRateSuppressionFeature);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppressionFeature)).thenReturn(true);
    // CDH suppresses only BUSIFLEX, not the negotiated CORPRATE
    when(cdhAdapterOutPort.getCompanySuppressRates("CORP123")).thenReturn(List.of("BUSIFLEX"));

    // CORPRATE has a non-null RatePlanBasedOnRate with DynamicBaseRate pointing to FLEXRATE → it is a negotiated rate
    var dynamicBaseRate = new DynamicBaseRate();
    dynamicBaseRate.setDynamicBasedOnRatePlan("FLEXRATE");
    var ratePlanBasedOnRate = new RatePlanBasedOnRate();
    ratePlanBasedOnRate.setDynamicBaseRate(dynamicBaseRate);
    var negotiatedRatePlans = new RatePlans();
    negotiatedRatePlans.setRatePlanBasedOnRates(List.of(ratePlanBasedOnRate));
    var corpRateInfoDto = new RatePlanInfoResponseDto();
    corpRateInfoDto.setRatePlanInfo(List.of(negotiatedRatePlans));
    when(hotelAvailabilityOutboundPort.getRatePlanInfo("CORPRATE", HOTEL_ID)).thenReturn(corpRateInfoDto);

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert: FLEXRATE must be hidden because CORPRATE (negotiated rate) is still present
    List<String> rateCodes = availability.getRoomRates().stream().map(RoomRate::getRatePlanCode).toList();
    assertFalse(rateCodes.contains("FLEXRATE"),
        "FLEXRATE should be hidden when negotiated rates remain after BUSIFLEX suppression");
    assertFalse(rateCodes.contains("BUSIFLEX"),
        "BUSIFLEX should be removed by CDH suppression");
    assertTrue(rateCodes.contains("CORPRATE"),
        "Negotiated rate CORPRATE should remain visible");
  }

  @Test
  void getHotelAvailability_busiFlexCdhSuppressed_noNegotiatedRatesRemain_shouldShowFlexRate() {
    // Arrange
    // Opera returns FLEXRATE, BUSIFLEX and public rates (no negotiated rates)
    var request = HotelAvailabilityRequest.builder()
        .channel(BB_BOOKING_CHANNEL)
        .hotelId(HOTEL_ID)
        .roomTypes(ROOM_TYPES)
        .adultsNumber(ADULTS)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .companyId("CORP123")
        .country("DE")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build();

    var hotelAvailabilityWithoutNegotiatedRate = getHotelAvailabilityForRateSuppresseion(
        List.of("FLEXRATE", "BUSIFLEX", "SEMIFLEX", "STANDARD"),
        new HashSet<>(Arrays.asList("ST")));

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(request)).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(request))
        .thenReturn(hotelAvailabilityWithoutNegotiatedRate);
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(List.of("FLEXRATE", "SEMIFLEX", "STANDARD"))
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());
    when(rateSuppressionProperties.getRoomClasses()).thenReturn(List.of("ST"));

    var featureFlag = new FeatureFlag();
    var companyRateSuppressionFeature = new FeatureFlag.Feature();
    featureFlag.setCompanyRateSuppression(companyRateSuppressionFeature);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppressionFeature)).thenReturn(true);
    // CDH suppresses BUSIFLEX; no other negotiated rates exist
    when(cdhAdapterOutPort.getCompanySuppressRates("CORP123")).thenReturn(List.of("BUSIFLEX"));

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert: FLEXRATE must show because all flex-derived rates (BUSIFLEX) are CDH-suppressed
    List<String> rateCodes = availability.getRoomRates().stream().map(RoomRate::getRatePlanCode).toList();
    assertTrue(rateCodes.contains("FLEXRATE"),
        "FLEXRATE should show when all flex-derived rates are CDH-suppressed and no negotiated rates remain");
    assertFalse(rateCodes.contains("BUSIFLEX"),
        "BUSIFLEX should be removed by CDH suppression");
  }

  @Test
  void getHotelAvailability_busiFlexPresentInRates_notCdhSuppressed_shouldHideFlexRateWithoutCallingGetRatePlanInfo() {
    // Case 1: both FLEXRATE and BUSIFLEX are returned by Opera (CDH suppresses nothing).
    // Expected: FLEXRATE hidden via Case 1 path, getRatePlanInfo never called.
    var request = HotelAvailabilityRequest.builder()
        .channel(BB_BOOKING_CHANNEL)
        .hotelId(HOTEL_ID)
        .roomTypes(ROOM_TYPES)
        .adultsNumber(ADULTS)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .companyId("CORP123")
        .country("DE")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build();

    var hotelAvailability = getHotelAvailabilityForRateSuppresseion(
        List.of("FLEXRATE", "BUSIFLEX", "STANDARD"),
        new HashSet<>(Arrays.asList("ST")));

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(request)).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(request)).thenReturn(hotelAvailability);
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());

    var featureFlag = new FeatureFlag();
    var companyRateSuppressionFeature = new FeatureFlag.Feature();
    featureFlag.setCompanyRateSuppression(companyRateSuppressionFeature);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppressionFeature)).thenReturn(true);
    // CDH does not suppress anything
    when(cdhAdapterOutPort.getCompanySuppressRates("CORP123")).thenReturn(Collections.emptyList());

    var availability = hotelAvailabilityInboundPort.getHotelAvailability(request);

    List<String> rateCodes = availability.getRoomRates().stream().map(RoomRate::getRatePlanCode).toList();
    assertFalse(rateCodes.contains("FLEXRATE"), "FLEXRATE should be hidden when BUSIFLEX is present (Case 1)");
    assertTrue(rateCodes.contains("BUSIFLEX"), "BUSIFLEX should remain when not CDH-suppressed");
    // Case 1 returns early — getRatePlanInfo must never be called
    verify(hotelAvailabilityOutboundPort, never()).getRatePlanInfo(anyString(), anyString());
  }

  @Test
  void getHotelAvailability_busiFlexCdhSuppressed_duplicateRatePlanCode_shouldCallGetRatePlanInfoOnlyOnce() {
    // CORPRATE appears twice in the rates (e.g. multiple room-type entries for the same plan).
    // After deduplication, getRatePlanInfo should be called exactly once for CORPRATE.
    var request = HotelAvailabilityRequest.builder()
        .channel(BB_BOOKING_CHANNEL)
        .hotelId(HOTEL_ID)
        .roomTypes(ROOM_TYPES)
        .adultsNumber(ADULTS)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .companyId("CORP123")
        .country("DE")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build();

    // CORPRATE listed twice intentionally to validate deduplication
    var hotelAvailabilityWithDuplicateRate = getHotelAvailabilityForRateSuppresseion(
        List.of("FLEXRATE", "BUSIFLEX", "CORPRATE", "CORPRATE", "STANDARD"),
        new HashSet<>(Arrays.asList("ST")));

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(request)).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(request)).thenReturn(hotelAvailabilityWithDuplicateRate);
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());

    var featureFlag = new FeatureFlag();
    var companyRateSuppressionFeature = new FeatureFlag.Feature();
    featureFlag.setCompanyRateSuppression(companyRateSuppressionFeature);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppressionFeature)).thenReturn(true);
    when(cdhAdapterOutPort.getCompanySuppressRates("CORP123")).thenReturn(List.of("BUSIFLEX"));

    var dynamicBaseRate2 = new DynamicBaseRate();
    dynamicBaseRate2.setDynamicBasedOnRatePlan("FLEXRATE");
    var ratePlanBasedOnRate2 = new RatePlanBasedOnRate();
    ratePlanBasedOnRate2.setDynamicBaseRate(dynamicBaseRate2);
    var negotiatedRatePlans = new RatePlans();
    negotiatedRatePlans.setRatePlanBasedOnRates(List.of(ratePlanBasedOnRate2));
    var corprateInfoDto = new RatePlanInfoResponseDto();
    corprateInfoDto.setRatePlanInfo(List.of(negotiatedRatePlans));
    when(hotelAvailabilityOutboundPort.getRatePlanInfo("CORPRATE", HOTEL_ID)).thenReturn(corprateInfoDto);

    hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Despite CORPRATE appearing twice in the list, the API must be called only once
    verify(hotelAvailabilityOutboundPort, times(1)).getRatePlanInfo("CORPRATE", HOTEL_ID);
  }

  @Test
  void getHotelAvailability_busiFlexNotInCdhSuppressedRates_shouldSkipNegotiatedRateCheck() {
    // CDH suppresses a different rate (not BUSIFLEX) → Case 2 condition is false.
    // getRatePlanInfo must never be called.
    var request = HotelAvailabilityRequest.builder()
        .channel(BB_BOOKING_CHANNEL)
        .hotelId(HOTEL_ID)
        .roomTypes(ROOM_TYPES)
        .adultsNumber(ADULTS)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .companyId("CORP123")
        .country("DE")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build();

    // No BUSIFLEX in rates at all; CORPRATE present
    var hotelAvailability = getHotelAvailabilityForRateSuppresseion(
        List.of("FLEXRATE", "CORPRATE", "STANDARD"),
        new HashSet<>(Arrays.asList("ST")));

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(request)).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(request)).thenReturn(hotelAvailability);
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());

    var featureFlag = new FeatureFlag();
    var companyRateSuppressionFeature = new FeatureFlag.Feature();
    featureFlag.setCompanyRateSuppression(companyRateSuppressionFeature);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppressionFeature)).thenReturn(true);
    // CDH suppresses CORPRATE — BUSIFLEX is NOT in the suppressed list
    when(cdhAdapterOutPort.getCompanySuppressRates("CORP123")).thenReturn(List.of("CORPRATE"));

    var availability = hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Case 2 guard (companySuppressedRates.contains(BUSIFLEX)) is false → no negotiated-rate check
    verify(hotelAvailabilityOutboundPort, never()).getRatePlanInfo(anyString(), anyString());
    List<String> rateCodes = availability.getRoomRates().stream().map(RoomRate::getRatePlanCode).toList();
    assertTrue(rateCodes.contains("FLEXRATE"), "FLEXRATE should remain untouched when BUSIFLEX was not suppressed");
  }

  @Test
  void getHotelAvailability_ccuiChannel_shouldSkipFlexRateExclusionLogicEntirely() {
    // For CCUI channel, applyFlexRateExclusionWhenBusiFlex is not invoked at all.
    // getRatePlanInfo must never be called even when CDH suppresses BUSIFLEX.
    var request = HotelAvailabilityRequest.builder()
        .channel(CCUI_BOOKING_CHANNEL)
        .hotelId(HOTEL_ID)
        .roomTypes(ROOM_TYPES)
        .adultsNumber(ADULTS)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .companyId("CORP123")
        .country("DE")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build();

    var hotelAvailability = getHotelAvailabilityForRateSuppresseion(
        List.of("FLEXRATE", "BUSIFLEX", "CORPRATE", "STANDARD"),
        new HashSet<>(Arrays.asList("ST")));

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(request)).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(request)).thenReturn(hotelAvailability);
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());

    var featureFlag = new FeatureFlag();
    var companyRateSuppressionFeature = new FeatureFlag.Feature();
    featureFlag.setCompanyRateSuppression(companyRateSuppressionFeature);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppressionFeature)).thenReturn(true);
    when(cdhAdapterOutPort.getCompanySuppressRates("CORP123")).thenReturn(List.of("BUSIFLEX"));

    hotelAvailabilityInboundPort.getHotelAvailability(request);

    // CCUI bypasses applyFlexRateExclusionWhenBusiFlex entirely
    verify(hotelAvailabilityOutboundPort, never()).getRatePlanInfo(anyString(), anyString());
  }

  @Test
  void getHotelAvailability_companyRateSuppression_numericCompanyId_shouldCallGetCompanyAccountIdFromCdhAndUseReturnedId() {
    // Arrange – companyId is a plain integer: getCdhCompanyAccountId must delegate to
    // getCompanyAccountIdFromCdh and pass its result to getCompanySuppressRates.
    var request = HotelAvailabilityRequest.builder()
        .channel(BB_BOOKING_CHANNEL)
        .hotelId(HOTEL_ID)
        .roomTypes(ROOM_TYPES)
        .adultsNumber(ADULTS)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .companyId("1001")
        .country("DE")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(request)).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(request)).thenReturn(getHotelAvailability());
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());

    var featureFlag = new FeatureFlag();
    var companyRateSuppressionFeature = new FeatureFlag.Feature();
    featureFlag.setCompanyRateSuppression(companyRateSuppressionFeature);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppressionFeature)).thenReturn(true);
    when(companyEntityServiceOutPort.getCompanyById("1001"))
        .thenReturn(Company.builder().corpId("1001").build());
    when(cdhAdapterOutPort.getCompanyAccountIdFromCdh(any(CdhSearchCompaniesRequest.class)))
        .thenReturn("CDH_ACCOUNT_ID");
    when(cdhAdapterOutPort.getCompanySuppressRates("CDH_ACCOUNT_ID")).thenReturn(List.of("SEMIFLEX", "NONFLEX"));

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert – verify the CDH search was called with correct request fields
    ArgumentCaptor<CdhSearchCompaniesRequest> captor = ArgumentCaptor.forClass(CdhSearchCompaniesRequest.class);
    verify(cdhAdapterOutPort, times(1)).getCompanyAccountIdFromCdh(captor.capture());
    assertEquals(1001, captor.getValue().getGlobalCompanyId());
    assertEquals(BB_BOOKING_CHANNEL, captor.getValue().getAccessContext());

    // And suppress rates were fetched using the CDH-returned account id
    verify(cdhAdapterOutPort, times(1)).getCompanySuppressRates("CDH_ACCOUNT_ID");

    List<String> rateCodes = availability.getRoomRates().stream().map(RoomRate::getRatePlanCode).toList();
    assertFalse(rateCodes.contains("SEMIFLEX"));
    assertFalse(rateCodes.contains("NONFLEX"));
    assertTrue(rateCodes.contains("FLEXRATE"));
    assertTrue(rateCodes.contains("STANDARD"));
  }

  @Test
  void getHotelAvailability_companyRateSuppression_numericCompanyIdWithWhitespace_shouldTrimAndCallGetCompanyAccountIdFromCdh() {
    // Arrange – companyId has surrounding whitespace; getCdhCompanyAccountId must trim before parsing.
    var request = HotelAvailabilityRequest.builder()
        .channel(BB_BOOKING_CHANNEL)
        .hotelId(HOTEL_ID)
        .roomTypes(ROOM_TYPES)
        .adultsNumber(ADULTS)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .companyId(" 1001 ")
        .country("GB")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(request)).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(request)).thenReturn(getHotelAvailability());
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());

    var featureFlag = new FeatureFlag();
    var companyRateSuppressionFeature = new FeatureFlag.Feature();
    featureFlag.setCompanyRateSuppression(companyRateSuppressionFeature);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppressionFeature)).thenReturn(true);
    when(companyEntityServiceOutPort.getCompanyById(" 1001 "))
        .thenReturn(Company.builder().corpId("1001").build());
    when(cdhAdapterOutPort.getCompanyAccountIdFromCdh(any(CdhSearchCompaniesRequest.class)))
        .thenReturn("CDH_ACCOUNT_ID");
    when(cdhAdapterOutPort.getCompanySuppressRates("CDH_ACCOUNT_ID")).thenReturn(Collections.emptyList());

    // Act
    hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert – trimmed value 1001 must be passed as Integer
    ArgumentCaptor<CdhSearchCompaniesRequest> captor = ArgumentCaptor.forClass(CdhSearchCompaniesRequest.class);
    verify(companyEntityServiceOutPort).getCompanyById(" 1001 ");
    verify(cdhAdapterOutPort, times(1)).getCompanyAccountIdFromCdh(captor.capture());
    assertEquals(1001, captor.getValue().getGlobalCompanyId());
  }

  @Test
  void getHotelAvailability_companyRateSuppression_nonNumericCompanyId_shouldNotCallGetCompanyAccountIdFromCdh() {
    // Arrange – companyId is a non-numeric string: getCdhCompanyAccountId must return it as-is
    // without ever calling getCompanyAccountIdFromCdh.
    var request = HotelAvailabilityRequest.builder()
        .channel(BB_BOOKING_CHANNEL)
        .hotelId(HOTEL_ID)
        .roomTypes(ROOM_TYPES)
        .adultsNumber(ADULTS)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .childrenNumber(CHILDREN)
        .cotsRequired(COTS_REQUIRED)
        .companyId("CORP123")
        .country("GB")
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .build();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest())).thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(request)).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(request)).thenReturn(getHotelAvailability());
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(RateSuppressionRuleResponse.builder()
        .rateSuppressionList(Collections.emptyList())
        .expiryDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
        .build());

    var featureFlag = new FeatureFlag();
    var companyRateSuppressionFeature = new FeatureFlag.Feature();
    featureFlag.setCompanyRateSuppression(companyRateSuppressionFeature);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(unleashWrapper.isEnabled(companyRateSuppressionFeature)).thenReturn(true);
    when(cdhAdapterOutPort.getCompanySuppressRates("CORP123")).thenReturn(List.of("SEMIFLEX"));

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert – CDH company search must never be called for non-numeric ids
    verify(cdhAdapterOutPort, never()).getCompanyAccountIdFromCdh(any());
    // The original companyId string is used directly
    verify(cdhAdapterOutPort, times(1)).getCompanySuppressRates("CORP123");

    List<String> rateCodes = availability.getRoomRates().stream().map(RoomRate::getRatePlanCode).toList();
    assertFalse(rateCodes.contains("SEMIFLEX"));
    assertTrue(rateCodes.contains("FLEXRATE"));
  }

  @Test
  void getHotelRoomsInventory__ShouldReturnOk() {
    // Arrange
    HotelInventoryRequest hotelInventoryRequest = HotelInventoryRequest.builder()
        .hotelId("MANOLD")
        .dateRangeStart("2022-10-28")
        .dateRangeEnd("2022-10-30")
        .build();

    when(hotelAvailabilityOutboundPort.getHotelRoomsInventory(hotelInventoryRequest)).thenReturn(
        createRoomInventories());

    // Act
    HotelInventoryRoomType foundHotelInventoryRoomType =
        this.hotelAvailabilityInboundPort.getHotelRoomsInventory(
            hotelInventoryRequest);

    // Assert
    assertThat(foundHotelInventoryRoomType.getRoomTypeInventories(), notNullValue());
    assertThat(foundHotelInventoryRoomType.getRoomTypeInventories(), hasSize(2));
    assertThat(foundHotelInventoryRoomType.getRoomTypeInventories().get(0).getCode(), is("DOUBLE"));
    assertThat(foundHotelInventoryRoomType.getRoomTypeInventories().get(0).getAvailableCount(),
        is(42));
    assertThat(foundHotelInventoryRoomType.getRoomTypeInventories().get(1).getCode(), is("FMTRPL"));
    assertThat(foundHotelInventoryRoomType.getRoomTypeInventories().get(1).getAvailableCount(),
        is(10));

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
  void getRateCodePricing__ShouldReturnOk() {
    // Arrange
    Mockito.when(hotelAvailabilityOutboundPort.getRateCodePricing(any()))
        .thenReturn(mockRateCodePricing());
    Mockito.when(promotionProperties.getPromotionalRate()).thenReturn(PROMO_RATE);

    // Act
    RateCodePricingResult response = hotelAvailabilityInboundPort
        .getRateCodePricing(createRateCodeRequest());

    // Assert
    Assertions.assertNotNull(response);
    assertThat(response.getRatePlanCode(), is("FLEXRATE"));
    assertThat(response.getCurrencyCode(), is("EUR"));
    assertThat(response.getTotalNetAmount(), is(BigDecimal.valueOf(1280)));

  }

  @Test
  void getRateCodePricing__promoRate__DontAllowFlexUpgrade() {
    // Arrange
    var request = createRateCodeRequest();
    request.setReservationRatePlanCode(PROMO_RATE);
    Mockito.when(promotionProperties.getPromotionalRate()).thenReturn(PROMO_RATE);

    // Act
    RateCodePricingResult response = hotelAvailabilityInboundPort
        .getRateCodePricing(request);

    // Assert
    assertEquals(new RateCodePricingResult(), response);
  }

  @Test
  void getRateCodePricing__noPromoRate__ShouldReturnOk() {
    // Arrange
    Mockito.when(promotionProperties.getPromotionalRate()).thenReturn(PROMO_RATE);
    Mockito.when(hotelAvailabilityOutboundPort.getRateCodePricing(any()))
        .thenReturn(mockRateCodePricing());

    // Act
    RateCodePricingResult response = hotelAvailabilityInboundPort
        .getRateCodePricing(createRateCodeRequest());

    // Assert
    Assertions.assertNotNull(response);
    assertThat(response.getRatePlanCode(), is("FLEXRATE"));
    assertThat(response.getCurrencyCode(), is("EUR"));
    assertThat(response.getTotalNetAmount(), is(BigDecimal.valueOf(1280)));

  }

  @Test
  void getNullAvaliabilities_ShouldReturnOK() {
    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(ROOM_TYPES, null, CCUI_BOOKING_CHANNEL);
    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());
    //Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);
    // Assert
    assertNotNull(availability);
    assertFalse(availability.isAvailable());
    assertEquals(0, availability.getRoomRates().size());
  }

  @ParameterizedTest
  @MethodSource({"provideStringsForRate"})
  void getAvailabilitiesRateSuppression_WithBigRoomsOnly_ShouldReturnOK(List<String> input,
      List<String> suppression, String excluded, List<String> output) {

    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(ROOM_TYPES, null, "BB");
    if (suppression.size() > 0 && output.size() > 0) {
      when(rateSuppressionProperties.getRoomClasses()).thenReturn(Arrays.asList("ST", "BG"));
    }
   when(promotionProperties.getExcludedRates()).thenReturn(excluded);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(hotelAvailabilityRequest))
        .thenReturn(
            getHotelAvailabilityForRateSuppresseion(input,
                new HashSet<>(Arrays.asList("ST", "BG"))));

    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(mockRateSuppressionResponse(
        Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()),
        suppression));

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    when(hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest)).thenReturn(true);

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    // Act
    var availability = hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);

    // Assert
    assertNotNull(availability);
    assertEquals(!availability.getRoomRates().isEmpty(), availability.isAvailable());
    assertEquals(availability.getRoomRates().stream().map(RoomRate::getRatePlanCode).toList(),
        output);
    if (output.size() > 0) {
      assertEquals(availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
          .getRoomPriceBreakdown().getTotalNetAmount(), BigDecimal.valueOf(90));
      assertEquals(2, availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().size());
    }
  }

  @Test
  void getHotelAvailability__EmptyRoomTypes_ShouldReturnOK() {
    // Arrange
    var request = buildEmptyRoomTypesRequest();

    List<List<String>> roomTypesVariants = List.of(
            List.of("DB", "FAM"),
            List.of("TWIN", "FAM")
    );
    var maxRoomOccupancyResponse = createMockMaxRoomOccupancyResponse();
    var hotelAvailabilityVariant1 = mockMultiRoomHotelAvailabilityResponseV1();
    var hotelAvailabilityVariant2 = mockMultiRoomHotelAvailabilityResponseV1();
    hotelAvailabilityVariant2.getRoomRates().get(0).getRoomTypes().get(0).setRoomType("TWIN");

      // Act
      var roomTypeVariantsActual = hotelAvailabilityInboundPort.retrieveRoomTypesVariants(request, maxRoomOccupancyResponse);
      HotelAvailability result = hotelAvailabilityInboundPort.aggregateAvailabilityResponses(List.of(hotelAvailabilityVariant1,hotelAvailabilityVariant2));

      // Assert
      assertNotNull(result);
      assertEquals(HOTEL_ID, result.getHotelId());
      assertEquals(roomTypeVariantsActual, roomTypesVariants);
      assertNotNull(result.getRoomRates().get(0).getRoomTypes().stream().map(RoomTypeInfo::getRoomType).toList().get(0));
      assertThat(result.getRoomRates().get(0).getRoomTypes().stream().map(RoomTypeInfo::getRoomNumber).toList().get(0), is(1));
      assertThat(result.getRoomRates().get(0).getRoomTypes().stream().map(RoomTypeInfo::getRooms).toList().get(0).size(), is(2));
      assertNotNull(result.getRoomRates().get(0).getRoomTypes().stream().map(RoomTypeInfo::getRooms).toList().get(0).get(0).getRoomType());
  }
  @Test
  void getHotelAvailability__EmptyRoomTypes__MultipleRooms_ShouldReturnOK() {
    // Arrange
    var request = buildEmptyRoomTypesRequest();
    var maxRoomOccupancyResponse = createMockMaxRoomOccupancyResponse();

    when(rulesAgentInPort.getMaxRoomOccupancyRule(request.getChannel())).thenReturn(maxRoomOccupancyResponse);
    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
            .thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(any())).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(any())).thenReturn(mockMultiRoomHotelAvailabilityResponseV1());
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(
            RateSuppressionRuleResponse.builder()
                    .rateSuppressionList(Collections.emptyList())
                    .expiryDate(Date.from(
                            LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
                    .build());
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    // Act
    HotelAvailability result = hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert
    assertNotNull(result);
    assertEquals(HOTEL_ID, result.getHotelId());
  }

  @Test
  void getHotelAvailability__EmptyRoomTypesAndAccessibleRoomTypes_ShouldReturnOK() {
    // Arrange
    var request = buildEmptyRoomTypesRequest();
    request.setRoomTypes(List.of("","DIS"));

    List<List<String>> roomTypesVariants = List.of(
            List.of("DB", "DIS"),
            List.of("TWIN", "DIS")
    );
    var maxRoomOccupancyResponse = createMockMaxRoomOccupancyResponse();

    // Act
    var roomTypeVariantsActual = hotelAvailabilityInboundPort.retrieveRoomTypesVariants(request, maxRoomOccupancyResponse);

    // Assert
    assertEquals(roomTypeVariantsActual, roomTypesVariants);
  }

  @Test
  void getHotelAvailability_softBundles_ShouldReturnOK() {
    // Arrange
    var request = buildSoftBundlesRequest("roomClass");

    HotelAvailability availability = getHotelAvailability(1);
    PackagesResponse packagesResponse = getPackagesResponse();
    MealsInfoResponse mealsInfoResponse = getUpsellItemsAndSoftBundlesResponse();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(any())).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(any())).thenReturn(
            availability);
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(
        RateSuppressionRuleResponse.builder()
            .rateSuppressionList(Collections.emptyList())
            .expiryDate(Date.from(
                LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
            .build());
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(packagesInPort.getPackages(any())).thenReturn(packagesResponse);
    when(packagesInPort.getUpsellItemsAndSoftBundles(any(), any(), any())).thenReturn(
            mealsInfoResponse);

    // Act
    HotelAvailability result = hotelAvailabilityInboundPort.getHotelAvailability(request);

    // Assert
    assertNotNull(result);
    assertEquals(HOTEL_ID, result.getHotelId());
    verify(packagesInPort, times(1)).getPackages(any());
    verify(packagesInPort, times(1)).getUpsellItemsAndSoftBundles(any(), any(), any());
    verify(softBundlesAndRatesLogic, times(1)).updateAvailabilityResponseWithSoftBundles(
            any(), any(), any(), any(), any());
  }

  @Test
  void getHotelAvailability_softBundles_ShouldThrowError() {
    // Arrange
    var request = buildSoftBundlesRequest("rate");

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(any())).thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(any())).thenReturn(
        getHotelAvailability(1));
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(
        RateSuppressionRuleResponse.builder()
            .rateSuppressionList(Collections.emptyList())
            .expiryDate(Date.from(
                LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
            .build());
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(packagesInPort.getPackages(any())).thenThrow(new RuntimeException("Packages not found"));

    // Act
    RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
      hotelAvailabilityInboundPort.getHotelAvailability(request);
    });

    // Assert
    Assertions.assertEquals("Packages not found", exception.getMessage());
  }

  @Test
  void getHotelAvailability_applyUniquePromo_shouldReplacePromo_whenOperaPromoReturned() {
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(ROOM_TYPES, null, PI_BOOKING_CHANNEL);
    hotelAvailabilityRequest.setPromoKind(UNIQUE);
    hotelAvailabilityRequest.setPromotionCode("OPERA");
    hotelAvailabilityRequest.setCountry("GB");
    hotelAvailabilityRequest.setSubchannel(WEB_BOOKING_SUBCHANNEL);
    HotelAvailability availability = getHotelAvailability(1);
    availability.getRoomRates().get(0).setPromotionCode("CLIENT");

    PromoKindResponse response = new PromoKindResponse();
    response.setPromoKind(PromoKind.UNIQUE);
    response.setUniquePromoCodeStatus(PromoCodeStatus.ISSUED);
    response.setOperaPromoCode("OPERA");

    when(promotionOutPort.getPromoKind(promoKindRequest("OPERA"))).thenReturn(response);
    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(any(HotelAvailabilityRequest.class)))
            .thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(any())).thenReturn(
        availability);
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(
        RateSuppressionRuleResponse.builder()
            .rateSuppressionList(Collections.emptyList())
            .expiryDate(Date.from(
                LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
            .build());
    when(mlosCommonLogic.isMlosEnabled(PI_BOOKING_CHANNEL)).thenReturn(false);

    var result = hotelAvailabilityInboundPort.getHotelAvailability(hotelAvailabilityRequest);

    assertEquals("CLIENT" ,result.getRoomRates().get(0).getPromotionCode());
    assertEquals("OPERA", hotelAvailabilityRequest.getPromotionCode());
  }


  @Test
  void getHotelAvailability_uniquePromo_shouldReturnSamePromo_whenPromoServiceReturnsNull() {

    HotelAvailability availability = getHotelAvailability(1);
    availability.getRoomRates().get(0).setPromotionCode("OPERA");

    var request = getHotelAvailabilityRequest(ROOM_TYPES, null, PI_BOOKING_CHANNEL);
    request.setPromoKind(UNIQUE);
    request.setPromotionCode("CLIENT");
    request.setSubchannel(WEB_BOOKING_SUBCHANNEL);
    request.setCountry("GB");

    when(promotionOutPort.getPromoKind(promoKindRequest("CLIENT"))).thenReturn(null);

    when(onSaleFlagOutPortImpl.getOnSaleFlag(any()))
        .thenReturn(getMigrationStatusResponse());
    when(hotelAvailabilityCheckRules.fulfillHubRules(any()))
        .thenReturn(true);
    when(hotelAvailabilityOutboundPort.getHotelAvailability(any()))
        .thenReturn(availability);
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(
        RateSuppressionRuleResponse.builder()
            .rateSuppressionList(Collections.emptyList())
            .expiryDate(Date.from(
                LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
            .build());
    when(mlosCommonLogic.isMlosEnabled(PI_BOOKING_CHANNEL)).thenReturn(false);


    var result = hotelAvailabilityInboundPort.getHotelAvailability(request);

    assertEquals("CLIENT", request.getPromotionCode());
    assertEquals("OPERA", result.getRoomRates().get(0).getPromotionCode());
  }

  @Test
  void uniquePromo_shouldThrowException_whenPromoIsRedeemed() {

    var request = getHotelAvailabilityRequest(ROOM_TYPES, null, CCUI_BOOKING_CHANNEL);
    request.setPromoKind(UNIQUE);
    request.setPromotionCode("CLIENT");

    PromoKindResponse response = new PromoKindResponse();
    response.setUniquePromoCodeStatus(PromoCodeStatus.REDEEMED);

    when(promotionOutPort.getPromoKind(any())).thenReturn(response);

    InvalidPromotionException ex =
        assertThrows(InvalidPromotionException.class,
            () -> hotelAvailabilityInboundPort.getHotelAvailability(request));

    assertEquals(
        ErrorCode.DIGITAL_PROMOTION_ALREADY_USED_EXCEPTION.getCode(),
        ex.getErrorCode()
    );

    assertTrue(ex.getMessage().contains("already been used"));
  }

  @Test
  void uniquePromo_shouldThrowException_whenPromoIsExpired() {

    var request = getHotelAvailabilityRequest(ROOM_TYPES, null, CCUI_BOOKING_CHANNEL);
    request.setPromoKind(UNIQUE);
    request.setPromotionCode("CLIENT");

    PromoKindResponse response = new PromoKindResponse();
    response.setUniquePromoCodeStatus(PromoCodeStatus.EXPIRED);

    when(promotionOutPort.getPromoKind(any())).thenReturn(response);

    InvalidPromotionException ex =
        assertThrows(InvalidPromotionException.class,
            () -> hotelAvailabilityInboundPort.getHotelAvailability(request));

    assertEquals(
        ErrorCode.DIGITAL_PROMOTION_EXPIRED_EXCEPTION.getCode(),
        ex.getErrorCode()
    );

    assertTrue(ex.getMessage().contains("has expired"));
  }

  @Test
  void getHotelAvailability_uniquePromo_shouldNotApplyPromo_whenStatusIsNull() {

    HotelAvailability availability = getHotelAvailability(1);

    var request = getHotelAvailabilityRequest(ROOM_TYPES, null, PI_BOOKING_CHANNEL);
    request.setPromoKind(UNIQUE);
    request.setPromotionCode("CLIENT");
    request.setSubchannel(WEB_BOOKING_SUBCHANNEL);
    request.setCountry("GB");

    PromoKindResponse response = new PromoKindResponse();
    response.setPromoKind(PromoKind.UNIQUE);
    response.setUniquePromoCodeStatus(null);
    response.setOperaPromoCode("OPERA");

    when(promotionOutPort.getPromoKind(promoKindRequest("CLIENT")))
            .thenReturn(response);

    when(onSaleFlagOutPortImpl.getOnSaleFlag(any()))
            .thenReturn(getMigrationStatusResponse());

    when(hotelAvailabilityCheckRules.fulfillHubRules(any()))
            .thenReturn(true);

    when(hotelAvailabilityOutboundPort.getHotelAvailability(any()))
            .thenReturn(availability);

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    when(rulesAgentInPort.getRateSuppressionRule()).thenReturn(
            RateSuppressionRuleResponse.builder()
                    .rateSuppressionList(Collections.emptyList())
                    .expiryDate(Date.from(
                            LocalDateTime.now().plusDays(1)
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant()))
                    .build());

    when(mlosCommonLogic.isMlosEnabled(PI_BOOKING_CHANNEL))
            .thenReturn(false);

    hotelAvailabilityInboundPort.getHotelAvailability(request);

    ArgumentCaptor<HotelAvailabilityRequest> requestCaptor =
            ArgumentCaptor.forClass(HotelAvailabilityRequest.class);

    verify(hotelAvailabilityOutboundPort)
            .getHotelAvailability(requestCaptor.capture());

    assertEquals("CLIENT", requestCaptor.getValue().getPromotionCode());
  }

  private MealsInfoResponse getUpsellItemsAndSoftBundlesResponse() {
    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(List.of(
                    PackageCode.builder().id("BFADBF").description("Breakfast").build(),
                    PackageCode.builder().id("FI24HR").description("Ultimate Wi-Fi").build(),
                    PackageCode.builder().id("HSCKIN").description("Early check-in").build()
            ))
            .rate(List.of("FLEXRATE", "STANDARD"))
            .roomClass(List.of("ST"))
            .optional(false)
            .build();

    UpsellItems upsellItem = new UpsellItems();
    upsellItem.setShow(true);
    upsellItem.setCode("FI24HR");

    return MealsInfoResponse.builder()
            .upsellItems(List.of(upsellItem))
            .softBundles(List.of(softBundle))
            .build();

  }

  static Stream<Arguments> provideStringsForRate() {
    return Stream.of(
        Arguments.of(
            List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX"),
            List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX"),
            null,
            List.of("FLEXRATE")),

        Arguments.of(
            List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX", "BUSIFLEX"),
            List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX"),
            null,
            List.of("BUSIFLEX")),

        Arguments.of(
            List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX", "BUSIFLEX"),
            List.of("FLEXRATE", "SEMIFLEX", "STANDARD", "NONFLEX"),
            null,
            List.of("ADVANCE", "BUSIFLEX")),

        Arguments.of(
            List.of(),
            List.of("FLEXRATE", "SEMIFLEX", "STANDARD", "NONFLEX"),
            null,
            List.of()),

        Arguments.of(
            List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX", "BUSIFLEX"),
            List.of(),
            null,
            List.of("SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX", "BUSIFLEX")),

        Arguments.of(
            List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX"),
            List.of(),
            null,
            List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX")),

        Arguments.of(
            List.of("FLEXRATE", "BUSIFLEX"),
            List.of("SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX"),
            null,
            List.of("BUSIFLEX")),

        Arguments.of(
            List.of("FLEXRATE"),
            List.of("SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX"),
            null,
            List.of("FLEXRATE")),

        Arguments.of(
            List.of("FLEXRATE", "SEMIFLEX", "PARKNFLY", "STANDARD", "NONFLEX"),
            List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX"),
            null,
            List.of("FLEXRATE", "PARKNFLY")),

        Arguments.of(
            List.of("FLEXRATE", "SEMIFLEX", "PARKNFLY", "STANDARD", "NONFLEX"),
            List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX"),
            "",
            List.of("FLEXRATE", "PARKNFLY")),

        Arguments.of(
            List.of("FLEXRATE", "SEMIFLEX", "PARKNFLY", "STANDARD", "NONFLEX"),
            List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX"),
            "PARKNFLY",
            List.of("FLEXRATE")),

        Arguments.of(
            List.of("FLEXRATE", "SEMIFLEX", "PARKNFLY", "STANDARD", "NONFLEX"),
            List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX"),
            "PARKNFLY,TEST",
            List.of("FLEXRATE")),

        Arguments.of(
            List.of("FLEXRATE", "SEMIFLEX", "PARKNFLY", "STANDARD", "NONFLEX"),
            List.of("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX"),
            " PARKNFLY, TEST ",
            List.of("FLEXRATE"))
    );
  }

  private RateCodeCriteria createRateCodeRequest() {
    return RateCodeCriteria.builder()
        .hotelId("MANOLD")
        .arrivalDate("2022-10-20")
        .departureDate("2022-10-22")
        .adultsNo(List.of(2))
        .childrenNo(List.of(0))
        .ratePlanCode("FLEXRATE")
        .roomTypes(List.of("DOUBLE"))
        .build();
  }

  private RateCodePricingResult mockRateCodePricing() {
    return RateCodePricingResult.builder()
        .ratePlanCode("FLEXRATE")
        .totalNetAmount(BigDecimal.valueOf(1280))
        .currencyCode("EUR")
        .build();
  }

  private HotelsMigrationStatusRequest getMigrationStatusRequest() {
    return HotelsMigrationStatusRequest.builder()
        .hotelIds(List.of("TTSSTT"))
        .build();
  }

  private HotelsMigrationStatusResponse getMigrationStatusResponse() {
    ArrayList<HotelMigrationStatusResponse> listOfMigration = new ArrayList<>();
    HotelMigrationStatusResponse migrationStatusResponse = HotelMigrationStatusResponse.builder()
        .hotelId("TTSSTT")
        .onSale(true)
        .pmsSource("OPERA")
        .updatedOn(LocalDateTime.now())
        .build();
    listOfMigration.add(migrationStatusResponse);
    return HotelsMigrationStatusResponse.builder()
        .migrationStatusList(listOfMigration)
        .build();
  }

  private static Map<String, BigDecimal> mockOccupancySupplementPrices(String hotelId, BigDecimal supplementPrice) {
    return Collections.singletonMap(hotelId, supplementPrice);
  }

  private GlobalConfigDto createMockGlobalConfigDto(String hotelId) {
    GlobalConfigDto globalConfigDto = new GlobalConfigDto();
    globalConfigDto.setHotelsWithCityTax(List.of(hotelId));
    return globalConfigDto;
  }

  private HotelInformationExtendedDto createMockHotelInformationExtendedDto(String effectiveFrom) {
    var hotelCityTaxDto = new HotelCityTaxDto();
    hotelCityTaxDto.setBookingDateFrom("2025-10-01");
    hotelCityTaxDto.setEffectiveFrom(effectiveFrom);
    hotelCityTaxDto.isCityTaxHotel(Boolean.TRUE);

    var hotelInformationExtendedDto = new HotelInformationExtendedDto();
    hotelInformationExtendedDto.setCityTax(hotelCityTaxDto);

    return hotelInformationExtendedDto;
  }

  private HotelAvailabilityRequest buildEmptyRoomTypesRequest() {
    return HotelAvailabilityRequest.builder()
            .hotelId(HOTEL_ID)
            .channel(PI_BOOKING_CHANNEL)
            .subchannel(WEB_BOOKING_SUBCHANNEL)
            .language("EN")
            .arrivalDate(ARRIVAL_DATE)
            .departureDate(DEPARTURE_DATE)
            .roomTypes(Collections.emptyList())
            .adultsNumber(List.of(2,1))
            .childrenNumber(List.of(0,1))
            .cotsRequired(List.of(false,false))
            .country("GB")
            .build();

  }

  private HotelAvailabilityRequest buildSoftBundlesRequest(String softBundle) {
    return HotelAvailabilityRequest.builder()
        .hotelId(HOTEL_ID)
        .channel(PI_BOOKING_CHANNEL)
        .subchannel(WEB_BOOKING_SUBCHANNEL)
        .language("en")
        .country("gb")
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .roomTypes(List.of("DB"))
        .adultsNumber(List.of(1))
        .childrenNumber(List.of(0))
        .cotsRequired(List.of(false))
        .softBundle(softBundle)
        .build();
  }

    private MaxRoomOccupancyResponse createMockMaxRoomOccupancyResponse() {
    return MaxRoomOccupancyResponse.builder()
            .channelId("PI")
            .roomOccupancies(List.of(
                    MaxRoomOccupancyData.builder()
                            .adultsNumber(2)
                            .childrenNumber(2)
                            .acceptedRoomTypes(List.of("FAM"))
                            .build(),
                    MaxRoomOccupancyData.builder()
                            .adultsNumber(2)
                            .childrenNumber(1)
                            .acceptedRoomTypes(List.of("FAM"))
                            .build(),
                    MaxRoomOccupancyData.builder()
                            .adultsNumber(2)
                            .childrenNumber(0)
                            .acceptedRoomTypes(List.of("DB", "TWIN", "DIS"))
                            .build(),
                    MaxRoomOccupancyData.builder()
                            .adultsNumber(1)
                            .childrenNumber(2)
                            .acceptedRoomTypes(List.of("FAM"))
                            .build(),
                    MaxRoomOccupancyData.builder()
                            .adultsNumber(1)
                            .childrenNumber(1)
                            .acceptedRoomTypes(List.of("FAM"))
                            .build(),
                    MaxRoomOccupancyData.builder()
                            .adultsNumber(1)
                            .childrenNumber(0)
                            .acceptedRoomTypes(List.of("SB", "DB", "DIS"))
                            .build()
            ))
            .generatedAt(new Date(1764594240602L))
            .build();
  }

  private HotelAvailability mockMultiRoomHotelAvailabilityResponseV1() {
    return HotelAvailability.builder()
            .hotelId(HOTEL_ID)
            .startDate(ARRIVAL_DATE)
            .endDate(DEPARTURE_DATE)
            .available(true)
            .limitedAvailability(false)
            .mlos(false)
            .roomRates(List.of(
                    // FLEXRATE
                    RoomRate.builder()
                            .ratePlanCode("FLEXRATE")
                            .twinRoomTypeAvailability(false)
                            .roomTypes(List.of(
                                    RoomTypeInfo.builder()
                                            .roomType("DB")
                                            .adults(2)
                                            .children(0)
                                            .cotRequested(false)
                                            .rooms(List.of(
                                                    Room.builder()
                                                            .pmsRoomType("DOUBLE")
                                                            .silentSubstitution(true)
                                                            .roomClass("ST")
                                                            .cotAvailable(false)
                                                            .specialRequests(List.of("DBLE"))
                                                            .roomPriceBreakdown(RoomPriceBreakdown.builder()
                                                                    .totalNetAmount(BigDecimal.valueOf(73))
                                                                    .totalGrossAmount(BigDecimal.valueOf(68.49))
                                                                    .totalTaxAmount(BigDecimal.valueOf(4.51))
                                                                    .effectiveRateAmount(BigDecimal.valueOf(69))
                                                                    .currencyCode("EUR")
                                                                    .dailyPrices(List.of(
                                                                            DailyPrice.builder()
                                                                                    .date(ARRIVAL_DATE)
                                                                                    .netPrice(BigDecimal.valueOf(73))
                                                                                    .grossPrice(BigDecimal.valueOf(68.49))
                                                                                    .effectiveRate(BigDecimal.valueOf(69))
                                                                                    .build()
                                                                    ))
                                                                    .build())
                                                            .numberOfRoomsAvailable(192)
                                                            .build()
                                            ))
                                            .build(),
                                    RoomTypeInfo.builder()
                                            .roomType("FAM")
                                            .adults(1)
                                            .children(1)
                                            .cotRequested(false)
                                            .rooms(List.of(
                                                    Room.builder()
                                                            .pmsRoomType("FMTRPL")
                                                            .silentSubstitution(true)
                                                            .roomClass("ST")
                                                            .cotAvailable(false)
                                                            .specialRequests(List.of("TWDS"))
                                                            .roomPriceBreakdown(RoomPriceBreakdown.builder()
                                                                    .totalNetAmount(BigDecimal.valueOf(73))
                                                                    .totalGrossAmount(BigDecimal.valueOf(68.49))
                                                                    .totalTaxAmount(BigDecimal.valueOf(4.51))
                                                                    .effectiveRateAmount(BigDecimal.valueOf(69))
                                                                    .currencyCode("EUR")
                                                                    .dailyPrices(List.of(
                                                                            DailyPrice.builder()
                                                                                    .date(ARRIVAL_DATE)
                                                                                    .netPrice(BigDecimal.valueOf(73))
                                                                                    .grossPrice(BigDecimal.valueOf(68.49))
                                                                                    .effectiveRate(BigDecimal.valueOf(69))
                                                                                    .build()
                                                                    ))
                                                                    .build())
                                                            .numberOfRoomsAvailable(102)
                                                            .build()
                                            ))
                                            .build()
                            ))
                            .build(),
                    // ADVANCE
                    RoomRate.builder()
                            .ratePlanCode("ADVANCE")
                            .twinRoomTypeAvailability(false)
                            .roomTypes(List.of(
                                    RoomTypeInfo.builder()
                                            .roomType("DB")
                                            .adults(2)
                                            .children(0)
                                            .cotRequested(false)
                                            .rooms(List.of(
                                                    Room.builder()
                                                            .pmsRoomType("DOUBLE")
                                                            .silentSubstitution(true)
                                                            .roomClass("ST")
                                                            .cotAvailable(false)
                                                            .specialRequests(List.of("DBLE"))
                                                            .roomPriceBreakdown(RoomPriceBreakdown.builder()
                                                                    .totalNetAmount(BigDecimal.valueOf(60))
                                                                    .totalGrossAmount(BigDecimal.valueOf(56.34))
                                                                    .totalTaxAmount(BigDecimal.valueOf(3.66))
                                                                    .effectiveRateAmount(BigDecimal.valueOf(56))
                                                                    .currencyCode("EUR")
                                                                    .dailyPrices(List.of(
                                                                            DailyPrice.builder()
                                                                                    .date(ARRIVAL_DATE)
                                                                                    .netPrice(BigDecimal.valueOf(60))
                                                                                    .grossPrice(BigDecimal.valueOf(56.34))
                                                                                    .effectiveRate(BigDecimal.valueOf(56))
                                                                                    .build()
                                                                    ))
                                                                    .build())
                                                            .numberOfRoomsAvailable(192)
                                                            .build()
                                            ))
                                            .build(),
                                    RoomTypeInfo.builder()
                                            .roomType("FAM")
                                            .adults(1)
                                            .children(1)
                                            .cotRequested(false)
                                            .rooms(List.of(
                                                    Room.builder()
                                                            .pmsRoomType("FMTRPL")
                                                            .silentSubstitution(true)
                                                            .roomClass("ST")
                                                            .cotAvailable(false)
                                                            .specialRequests(List.of("TWDS"))
                                                            .roomPriceBreakdown(RoomPriceBreakdown.builder()
                                                                    .totalNetAmount(BigDecimal.valueOf(60))
                                                                    .totalGrossAmount(BigDecimal.valueOf(56.34))
                                                                    .totalTaxAmount(BigDecimal.valueOf(3.66))
                                                                    .effectiveRateAmount(BigDecimal.valueOf(56))
                                                                    .currencyCode("EUR")
                                                                    .dailyPrices(List.of(
                                                                            DailyPrice.builder()
                                                                                    .date(ARRIVAL_DATE)
                                                                                    .netPrice(BigDecimal.valueOf(60))
                                                                                    .grossPrice(BigDecimal.valueOf(56.34))
                                                                                    .effectiveRate(BigDecimal.valueOf(56))
                                                                                    .build()
                                                                    ))
                                                                    .build())
                                                            .numberOfRoomsAvailable(102)
                                                            .build()
                                            ))
                                            .build()
                            ))
                            .build(),
                    // STANDARD
                    RoomRate.builder()
                            .ratePlanCode("STANDARD")
                            .twinRoomTypeAvailability(false)
                            .roomTypes(List.of(
                                    RoomTypeInfo.builder()
                                            .roomType("DB")
                                            .adults(2)
                                            .children(0)
                                            .cotRequested(false)
                                            .rooms(List.of(
                                                    Room.builder()
                                                            .pmsRoomType("DOUBLE")
                                                            .silentSubstitution(true)
                                                            .roomClass("ST")
                                                            .cotAvailable(false)
                                                            .specialRequests(List.of("DBLE"))
                                                            .roomPriceBreakdown(RoomPriceBreakdown.builder()
                                                                    .totalNetAmount(BigDecimal.valueOf(53))
                                                                    .totalGrossAmount(BigDecimal.valueOf(49.79))
                                                                    .totalTaxAmount(BigDecimal.valueOf(3.21))
                                                                    .effectiveRateAmount(BigDecimal.valueOf(49))
                                                                    .currencyCode("EUR")
                                                                    .dailyPrices(List.of(
                                                                            DailyPrice.builder()
                                                                                    .date(ARRIVAL_DATE)
                                                                                    .netPrice(BigDecimal.valueOf(53))
                                                                                    .grossPrice(BigDecimal.valueOf(49.79))
                                                                                    .effectiveRate(BigDecimal.valueOf(49))
                                                                                    .build()
                                                                    ))
                                                                    .build())
                                                            .numberOfRoomsAvailable(192)
                                                            .build()
                                            ))
                                            .build(),
                                    RoomTypeInfo.builder()
                                            .roomType("FAM")
                                            .adults(1)
                                            .children(1)
                                            .cotRequested(false)
                                            .rooms(List.of(
                                                    Room.builder()
                                                            .pmsRoomType("FMTRPL")
                                                            .silentSubstitution(true)
                                                            .roomClass("ST")
                                                            .cotAvailable(false)
                                                            .specialRequests(List.of("TWDS"))
                                                            .roomPriceBreakdown(RoomPriceBreakdown.builder()
                                                                    .totalNetAmount(BigDecimal.valueOf(53))
                                                                    .totalGrossAmount(BigDecimal.valueOf(49.79))
                                                                    .totalTaxAmount(BigDecimal.valueOf(3.21))
                                                                    .effectiveRateAmount(BigDecimal.valueOf(49))
                                                                    .currencyCode("EUR")
                                                                    .dailyPrices(List.of(
                                                                            DailyPrice.builder()
                                                                                    .date(ARRIVAL_DATE)
                                                                                    .netPrice(BigDecimal.valueOf(53))
                                                                                    .grossPrice(BigDecimal.valueOf(49.79))
                                                                                    .effectiveRate(BigDecimal.valueOf(49))
                                                                                    .build()
                                                                    ))
                                                                    .build())
                                                            .numberOfRoomsAvailable(102)
                                                            .build()
                                            ))
                                            .build()
                            ))
                            .build()
            ))
            .build();
  }

  private PackagesResponse getPackagesResponse() {
    return PackagesResponse.builder()
            .restaurant(Restaurant.builder()
                    .restaurantNotFound(false)
                    .noMealsFound(false)
                    .build())
        .packages(Packages.builder()
            .meals(getMeals())
            .extrasItems(getExtrasDto())
            .build())
        .build();
  }

  private List<Meal> getMeals() {
    Meal premierInnBreakfast = Meal.builder()
        .name("Premier Inn Breakfast Food VEN")
        .id("BFADBF")
        .price(new BigDecimal(99))
        .currency("GBP")
        .build();
    return List.of(premierInnBreakfast);
  }

  private List<ExtrasDto> getExtrasDto() {

    ExtrasDto wifi = ExtrasDto.builder()
            .id("FI24HR")
            .name("Ultimate Wi-Fi")
            .description("<p>Download files faster, stream movies, make video calls and browse with ease with our Ultimate Wi-Fi package.</p>\r\n")
            .price(new BigDecimal(5))
            .currency("GBP")
            .available(null)
            .imageSrc("/content/dam/global/extras/ultimate-wifi.png")
            .build();

    ExtrasDto earlyCheckIn = ExtrasDto.builder()
            .id("HSCKIN")
            .name("Early check-in")
            .description("Check in any time from 11am (normal check-in time is 3pm).")
            .price(new BigDecimal(10))
            .currency("GBP")
            .available(100)
            .imageSrc("/content/dam/global/extras/early-check-in.png")
            .build();
    return List.of(wifi, earlyCheckIn);
  }

  private PromoKindRequest promoKindRequest(String promoCode) {
    return PromoKindRequest.builder()
            .promoCode(promoCode)
            .country("GB")
            .channel(PI_BOOKING_CHANNEL)
            .subChannel(WEB_BOOKING_SUBCHANNEL)
            .build();
  }
}
