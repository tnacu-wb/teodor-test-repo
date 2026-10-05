package uk.co.whitbread.ohip.infrastructure.rest.client.availability;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Company;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.MultiRoomRateAvailabilityResponseType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.OfferTotalType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.PropertySearchPropertyInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.RoomTagType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.SearchPropertyResponseType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.SearchPropertyRoomStayType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.RatePlanClassificationsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.RatePlanShortInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.RatePlansSummary;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.RatePlansSummaryRatePlanShortInfoList;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchCriteriaV2;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityRoomSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.BookingChannel;
import uk.co.whitbread.ohip.domain.model.availability.in.CorporateRate;
import uk.co.whitbread.ohip.domain.model.availability.in.HotelInventoryRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.ItemInventoryRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.MultiHotelAvailabilityRequestV2;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeRoomInfoCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RateV2;
import uk.co.whitbread.ohip.domain.model.availability.in.RestrictionsByDateRangeSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.Room;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityByIdsResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityDailyPrice;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRequest;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoom;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomPriceBreakdown;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomRate;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomType;
import uk.co.whitbread.ohip.domain.model.availability.out.HotelAvailabilityResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.InventoryAvailability;
import uk.co.whitbread.ohip.domain.model.availability.out.ItemInventory;
import uk.co.whitbread.ohip.domain.model.availability.out.ItemInventoryResponse;
import uk.co.whitbread.ohip.domain.model.availability.out.MultiAvailabilityResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.PackageInfo;
import uk.co.whitbread.ohip.domain.model.availability.out.PriceInfo;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionControl;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionSets;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionStatus;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionsByDateRange;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionsByDateRangeParent;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomRate;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomRateInfoV2;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomStay;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomTypeV2;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitution;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitutionRequestDetails;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.exceptions.HotelAvailabilityException;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.AvailabilityByIdsRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.AvailabilityRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.HotelInventoryStatisticsMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.HotelRoomInventoryMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.ItemsInventoryMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.MultiAvailabilityRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.PriceBreakdownMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.AmountTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelAvailabilityDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelAvailabilityDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelInventoryDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelInventoryTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.InventoryCountsTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.InventoryLevelCountsListTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.ItemInventoryRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.PriceBreakdownDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.RatesTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.RoomRateTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.RoomStayTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.SummaryDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.TotalTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.AvailabilityByIdsSearchCriteriaV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.AvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.CorporateRateDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.HotelInventoryRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.InventoryAvailabilityDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.ItemInventoryDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.ItemInventoryResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.MultiHotelAvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.MultiHotelAvailabilityRequestV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RateDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomByIdsDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomTypeInfoDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomTypesDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomTypesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.ApiLimitsService;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.OhipAvailabilityClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.properties.AvailabilityOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.ohip.OhipProfileClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.ohip.OhipRatePlansClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.RulesAgentClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.BookingChannelInfoResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.RoomSubstitutionDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.RoomSubstitutionRequestDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.RoomSubstitutionRuleResponseDto;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HotelAvailabilityOutPortImplTest {

  @InjectMocks
  private HotelAvailabilityOutPortImpl hotelAvailabilityOutPort;

  @Mock
  private AvailabilityOhipProperties availabilityOhipProperties;
  @Mock
  private OhipAvailabilityClient ohipAvailabilityClient;

  @Mock
  private OhipProfileClient ohipProfileClient;

  @Mock
  private RulesAgentClient rulesAgentClient;

  @Mock
  private OhipRatePlansClient ohipRatePlansClient;

  @Mock
  private AvailabilityRequestMapper availabilityRequestMapper;

  @Mock
  private AvailabilityByIdsRequestMapper availabilityByIdsRequestMapper;

  @Mock
  private PriceBreakdownMapper priceBreakdownMapper;

  @Mock
  private ItemsInventoryMapper itemsInventoryMapper;

  @Mock
  private HotelRoomInventoryMapper hotelRoomInventoryMapper;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  private MultiAvailabilityRequestMapper multiAvailabilityRequestMapper;

  @Mock
  private HotelInventoryStatisticsMapper hotelInventoryStatisticsMapper;

  @Mock
  private ApiLimitsService apiLimitsService;

  @Mock
  private ConcurrentTracer concurrentTracer;

  @Captor
  ArgumentCaptor<AvailabilityByIdsSearchCriteriaV2Dto> requestsCaptor;

  @Captor
  ArgumentCaptor<AvailabilityByIdsSearchCriteriaV2> requestsBeforeMappingCaptor;

  private ExecutorService restrictionsExecutorService;

  private static final LocalDate ARRIVAL_DATE = LocalDate.of(2025, 3, 1);
  private static final LocalDate DEPARTURE_DATE = LocalDate.of(2025, 3, 2);
  private static final String HOTEL_ID_1 = "MANOLD";
  private static final String HOTEL_ID_2 = "LONEUS";
  private static final String ERROR = "An error was returned by OHIP!";
  private static final BigDecimal EFFECTIVE_RATE = BigDecimal.valueOf(100);
  private static final BigDecimal BASE_RATE =  BigDecimal.valueOf(70);
  private static final BigDecimal NET_RATE =  BigDecimal.valueOf(120);

  static Stream<Arguments> hotelIdSets() {
    return Stream.of(
        Arguments.of(List.of("LONEUS1", "LONEUS2", "LONEUS3"), 1),
        Arguments.of(List.of("LONEUS1", "LONEUS2", "LONEUS3", "LONEUS4", "LONEUS5", "LONEUS6",
            "LONEUS7", "LONEUS8", "LONEUS9", "LONEUS10", "LONEUS11", "LONEUS12", "LONEUS13", "LONEUS14", "LONEUS15",
            "LONEUS16", "LONEUS17", "LONEUS18", "LONEUS19", "LONEUS20", "LONEUS21"), 3),
        Arguments.of(List.of("LONEUS1", "LONEUS2", "LONEUS3", "LONEUS4", "LONEUS5", "LONEUS6",
            "LONEUS7", "LONEUS8", "LONEUS9", "LONEUS10", "LONEUS11", "LONEUS12","LONEUS13", "LONEUS14", "LONEUS15",
            "LONEUS16", "LONEUS17", "LONEUS18", "LONEUS19", "LONEUS20", "LONEUS21","LONEUS22","LONEUS23", "LONEUS24",
            "LONEUS25", "LONEUS26", "LONEUS27","LONEUS28","LONEUS29", "LONEUS30", "LONEUS31"), 4)
    );
  }

  @BeforeEach
  void init() {
    restrictionsExecutorService = Executors.newFixedThreadPool(
        2); // Use a real executor for testing
    hotelAvailabilityOutPort = new HotelAvailabilityOutPortImpl(availabilityRequestMapper,
        availabilityByIdsRequestMapper, ohipAvailabilityClient,
        ohipProfileClient, ohipRatePlansClient, rulesAgentClient, priceBreakdownMapper,
        itemsInventoryMapper, hotelRoomInventoryMapper, multiAvailabilityRequestMapper,
        availabilityOhipProperties, hotelInventoryStatisticsMapper, unleashWrapper, apiLimitsService,
        concurrentTracer);
  }
  @Test
  void getHotelAvailabilityWithRatePlanCode__ShouldReturnOK() {
    //Arrange
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    when(apiLimitsService.getHotelAvailabilityResponses(
        any(AvailabilityRequestDto.class))).thenReturn(
        mockHotelAvailability("2022-11-01", "2022-11-03").block());
    when(availabilityRequestMapper.toHotelAvailabilityRequestDto(
        any(AvailabilityRequest.class))).thenReturn(
        mockAvailabilityRequestDto("FLEXRATE", "2022-11-03"));
    when(ohipAvailabilityClient.getRoomTypes(any(String.class))).thenReturn(mockRoomTypesMultipleDB());
    when(ohipAvailabilityClient.getHotelAvailabilityRequest(
        any(AvailabilityRequestDto.class))).thenReturn(
        mockHotelAvailabilityDB());
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(rulesAgentClient.getRoomSubstitution(any(), any(), any(), any()))
        .thenReturn(mockRoomSubstitutionResponse(List.of("WETDBL", "DOUBLE")));
    when(ohipAvailabilityClient.getHotelInventory(
        any(String.class), any(String.class), any(String.class),
        any(Integer.class), any())).thenReturn(mockHotelInventory(200));

    //Act
    var result = hotelAvailabilityOutPort.getHotelAvailability(
        createAvailabilityRequestMultipleDB("FLEXRATE"));

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.getHotelId(), is("LONEUS"));
    assertThat(result.getStartDate(), is("2022-11-01"));
    assertThat(result.getEndDate(), is("2022-11-03"));
    assertThat(result.getRoomRates(), Matchers.<Collection<AvailabilityRoomRate>>allOf(
        notNullValue(),
        hasSize(equalTo(2)),
        contains(hasProperty("ratePlanCode", is("FLEXRATE")),
            hasProperty("ratePlanCode", is("ADVANCED")))
    ));

    var roomRates = result.getRoomRates();
    var roomTypes = roomRates.get(0).getRoomTypes();
    assertThat(roomTypes,
        Matchers.<Collection<AvailabilityRoomType>>allOf(
            notNullValue(),
            hasSize(equalTo(1)),
            contains(hasProperty("roomType", equalTo("DB")))
        ));
    assertThat(roomRates.get(0).getRoomTypes().get(0).getRooms().size(), is(1));
    assertThat(roomRates.get(0).getRoomTypes().get(0).getRooms().get(0).getNumberOfRoomsAvailable(), is(400));
    assertThat(roomRates.get(1).getRoomTypes().get(0).getRooms().size(), is(1));
    assertThat(roomRates.get(1).getRoomTypes().get(0).getRooms().get(0).getNumberOfRoomsAvailable(), is(400));
  }

  @Test
  void getHotelAvailabilityDifferentRates__ShouldReturnOK() {
    //Arrange
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    when(apiLimitsService.getHotelAvailabilityResponses(
        any(AvailabilityRequestDto.class))).thenReturn(
        mockHotelAvailabilityMultupleRoomTypes("2022-11-01", "2022-11-03").block());
    when(availabilityRequestMapper.toHotelAvailabilityRequestDto(
        any(AvailabilityRequest.class))).thenReturn(
        mockAvailabilityRequestDto("FLEXRATE", "2022-11-03"));
    when(ohipAvailabilityClient.getRoomTypes(any(String.class))).thenReturn(mockRoomTypesMultipleDB());
    when(ohipAvailabilityClient.getHotelAvailabilityRequest(
        any(AvailabilityRequestDto.class))).thenReturn(
        mockHotelAvailabilityDBDifferentRates(true));
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(rulesAgentClient.getRoomSubstitution(any(), any(), any(), any()))
        .thenReturn(mockRoomSubstitutionResponse(List.of("WETDBL", "DOUBLE", "PPLDBL")));
    when(ohipAvailabilityClient.getHotelInventory(
        any(String.class), any(String.class), any(String.class),
        any(Integer.class), any())).thenReturn(mockHotelInventoryPPLDBL(200));

    //Act
    var result = hotelAvailabilityOutPort.getHotelAvailability(
        createAvailabilityRequestMultipleDB("FLEXRATE"));

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.getHotelId(), is("LONEUS"));
    assertThat(result.getStartDate(), is("2022-11-01"));
    assertThat(result.getEndDate(), is("2022-11-03"));
    assertThat(result.getRoomRates(), Matchers.<Collection<AvailabilityRoomRate>>allOf(
        notNullValue(),
        hasSize(equalTo(2)),
        contains(hasProperty("ratePlanCode", is("FLEXRATE")),
            hasProperty("ratePlanCode", is("ADVANCED")))
    ));

    var roomRates = result.getRoomRates();
    var roomTypes = roomRates.get(0).getRoomTypes();
    assertThat(roomTypes,
        Matchers.<Collection<AvailabilityRoomType>>allOf(
            notNullValue(),
            hasSize(equalTo(1)),
            contains(hasProperty("roomType", equalTo("DB")))
        ));
    assertThat(roomRates.get(0).getRoomTypes().get(0).getRooms().size(), is(1));
    assertThat(roomRates.get(0).getRoomTypes().get(0).getRooms().get(0).getNumberOfRoomsAvailable(), is(400));
    assertThat(roomRates.get(1).getRoomTypes().get(0).getRooms().size(), is(2));
    assertThat(roomRates.get(1).getRoomTypes().get(0).getRooms().get(0).getNumberOfRoomsAvailable(), is(200));
  }

  @Test
  void getHotelAvailabilityEmptyRatePlanCode__ShouldReturnOK() {
    //Arrange
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    when(apiLimitsService.getHotelAvailabilityResponses(
        any(AvailabilityRequestDto.class))).thenReturn(
        mockHotelAvailability("2022-11-01", "2022-11-03").block());
    when(availabilityRequestMapper.toHotelAvailabilityRequestDto(
        any(AvailabilityRequest.class))).thenReturn(
        mockAvailabilityRequestDto(null, "2022-11-03"));
    when(ohipAvailabilityClient.getRoomTypes(any(String.class))).thenReturn(mockRoomTypes("ST"));
    when(ohipAvailabilityClient.getHotelAvailabilityRequest(
        any(AvailabilityRequestDto.class))).thenReturn(
        mockHotelAvailability("2022-11-01", "2022-11-03"));
    when(rulesAgentClient.getBookingChannelInfo(any())).thenReturn(
        mockBookingChannelInfoResponseDto());
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(ohipAvailabilityClient.getHotelInventory(
        any(String.class), any(String.class), any(String.class),
        any(Integer.class), any())).thenReturn(mockHotelInventory(200));
    when(concurrentTracer.wrap(any(Supplier.class))).thenAnswer(invocation -> invocation.getArgument(0));

    //Act
    var result = hotelAvailabilityOutPort.getHotelAvailability(createAvailabilityRequest(""));

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.getHotelId(), is("LONEUS"));
    assertThat(result.getStartDate(), is("2022-11-01"));
    assertThat(result.getEndDate(), is("2022-11-03"));
    assertThat(result.getRoomRates(), Matchers.<Collection<AvailabilityRoomRate>>allOf(
        notNullValue(),
        hasSize(equalTo(2)),
        contains(hasProperty("ratePlanCode", is("FLEXRATE")),
            hasProperty("ratePlanCode", is("ADVANCED")))
    ));

    assertThat(result.getRoomRates().get(0).getRoomTypes(),
        Matchers.<Collection<AvailabilityRoomType>>allOf(
            notNullValue(),
            hasSize(equalTo(1)),
            contains(hasProperty("roomType", equalTo("DB")))
        ));

  }

  @ParameterizedTest
  @CsvSource({"true,true", "true,false", "false,true", "false,false"})
  void getHotelAvailability__ShouldReturnOK(boolean isFistAvailable, boolean isSecondAvailable) {
    //Arrange
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    when(apiLimitsService.getHotelAvailabilityResponses(
        any(AvailabilityRequestDto.class))).thenReturn(
        mockHotelAvailability("2022-11-01", "2022-11-03").block());
    when(availabilityRequestMapper.toHotelAvailabilityRequestDto(
        any(AvailabilityRequest.class))).thenReturn(
        mockAvailabilityRequestDto(null, "2022-11-03"));
    when(ohipAvailabilityClient.getRoomTypes(any(String.class))).thenReturn(mockRoomTypes("ST"));
    when(ohipAvailabilityClient.getHotelAvailabilityRequest(
        any(AvailabilityRequestDto.class))).thenReturn(
        mockHotelAvailability("2022-11-01", "2022-11-03"));
    when(rulesAgentClient.getBookingChannelInfo(any())).thenReturn(
        mockBookingChannelInfoResponseDto());
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(ohipAvailabilityClient.getHotelInventory(
        any(String.class), any(String.class), any(String.class),
        any(Integer.class), any())).thenReturn(
        mockHouseInventoryAvailability(isFistAvailable, isSecondAvailable));
    when(concurrentTracer.wrap(any(Supplier.class))).thenAnswer(invocation -> invocation.getArgument(0));

    //Act
    var result = hotelAvailabilityOutPort.getHotelAvailability(createAvailabilityRequest(null));

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.getHotelId(), is("LONEUS"));
    assertThat(result.getStartDate(), is("2022-11-01"));
    assertThat(result.getEndDate(), is("2022-11-03"));
    assertThat(result.isAvailable(), is(isFistAvailable && isSecondAvailable));
    assertThat(result.getRoomRates(), Matchers.<Collection<AvailabilityRoomRate>>allOf(
        notNullValue(),
        hasSize(equalTo(2)),
        contains(hasProperty("ratePlanCode", is("FLEXRATE")),
            hasProperty("ratePlanCode", is("ADVANCED")))
    ));

    assertThat(result.getRoomRates().get(0).getRoomTypes(),
        Matchers.<Collection<AvailabilityRoomType>>allOf(
            notNullValue(),
            hasSize(equalTo(1)),
            contains(hasProperty("roomType", equalTo("DB")))
        ));

  }

  @Test
  void getHotelAvailabilitySilentFlag__ShouldReturnOK() {
    //Arrange
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    when(apiLimitsService.getHotelAvailabilityResponses(
        any(AvailabilityRequestDto.class))).thenReturn( mockHotelAvailability("2022-11-01", "2022-11-03").block());
    when(availabilityRequestMapper.toHotelAvailabilityRequestDto(
        any(AvailabilityRequest.class))).thenReturn(
        mockAvailabilityRequestDto(null, "2022-11-03"));
    when(ohipAvailabilityClient.getRoomTypes(any(String.class))).thenReturn(mockRoomTypes("PP"));
    when(ohipAvailabilityClient.getHotelAvailabilityRequest(
        any(AvailabilityRequestDto.class))).thenReturn(
        mockHotelAvailability("2022-11-01", "2022-11-03"));
    when(rulesAgentClient.getBookingChannelInfo(any())).thenReturn(
        mockBookingChannelInfoResponseDto());
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(ohipAvailabilityClient.getHotelInventory(
        any(String.class), any(String.class), any(String.class),
        any(Integer.class), any())).thenReturn(mockHotelInventory(200));
    when(concurrentTracer.wrap(any(Supplier.class))).thenAnswer(invocation -> invocation.getArgument(0));

    //Act
    var result = hotelAvailabilityOutPort.getHotelAvailability(createAvailabilityRequest(null));

    //Assert
    assertThat(result, notNullValue());
    assertEquals(Boolean.FALSE,
        result.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getSilentSubstitution());

  }

  @Test
  void createAvailabilityRoom_ShouldSetIsSubstitutionTrue_WhenPreferredPmsDiffersFromSelected()
      throws ReflectiveOperationException {
    var roomSubstitutionDto = RoomSubstitutionDto.builder()
        .type("DOUBLE")
        .silent(Boolean.TRUE)
        .specialRequest("DBLE")
        .build();
    var roomRateTypeDto = RoomRateTypeDto.builder()
        .roomType("DOUBLE")
        .ratePlanSet("PBF")
        .build();

    var method = HotelAvailabilityOutPortImpl.class.getDeclaredMethod(
        "createAvailabilityRoom",
        RoomSubstitutionDto.class,
        RoomRateTypeDto.class,
        String.class,
        Integer.class,
        String.class);
    method.setAccessible(true);

    var room = (AvailabilityRoom) method.invoke(
        hotelAvailabilityOutPort,
        roomSubstitutionDto,
        roomRateTypeDto,
        "ST",
        2,
        "WETDBL");

    assertTrue(room.getIsSubstitution());
    assertEquals("WETDBL", room.getSubstitution());
  }

  @Test
  void createAvailabilityRoom_ShouldSetIsSubstitutionFalse_WhenPreferredPmsMatchesSelected()
      throws ReflectiveOperationException {
    var roomSubstitutionDto = RoomSubstitutionDto.builder()
        .type("DOUBLE")
        .silent(Boolean.TRUE)
        .specialRequest("DBLE")
        .build();
    var roomRateTypeDto = RoomRateTypeDto.builder()
        .roomType("DOUBLE")
        .ratePlanSet("PBF")
        .build();

    var method = HotelAvailabilityOutPortImpl.class.getDeclaredMethod(
        "createAvailabilityRoom",
        RoomSubstitutionDto.class,
        RoomRateTypeDto.class,
        String.class,
        Integer.class,
        String.class);
    method.setAccessible(true);

    var room = (AvailabilityRoom) method.invoke(
        hotelAvailabilityOutPort,
        roomSubstitutionDto,
        roomRateTypeDto,
        "ST",
        2,
        "DOUBLE");

    assertFalse(room.getIsSubstitution());
    assertNull(room.getSubstitution());
  }

  @Test
  void createRoomForAvailabilityRoomType_ShouldKeepConfiguredPreferred_WhenFallbackIsOnlyAvailable()
      throws ReflectiveOperationException {
    var roomSubstitutionResponse = RoomSubstitutionRuleResponseDto.builder()
        .requestDetails(RoomSubstitutionRequestDetailsDto.builder()
            .roomType("DB")
            .adults(2)
            .children(0)
            .build())
        .substitutionList(List.of(
            RoomSubstitutionDto.builder().type("DBLWIN").silent(Boolean.FALSE).specialRequest("DBLWIN").build(),
            RoomSubstitutionDto.builder().type("PPLDBL").silent(Boolean.FALSE).specialRequest("PPLDBL").build(),
            RoomSubstitutionDto.builder().type("PPDLOW").silent(Boolean.FALSE).specialRequest("PPDLOW").build()))
        .build();

    var roomRates = List.of(
        RoomRateTypeDto.builder().roomType("PPLDBL").ratePlanSet("FLEX").build(),
        RoomRateTypeDto.builder().roomType("PPDLOW").ratePlanSet("FLEX").build());

    var roomTypeAvailability = new java.util.HashMap<>(java.util.Map.of(
        "PPLDBL", 0,
        "PPDLOW", 1));

    var method = HotelAvailabilityOutPortImpl.class.getDeclaredMethod(
        "createRoomForAvailabilityRoomType",
        RoomSubstitutionRuleResponseDto.class,
        List.class,
        java.util.Map.class,
        List.class,
        String.class);
    method.setAccessible(true);

    @SuppressWarnings("unchecked")
    var rooms = (List<AvailabilityRoom>) method.invoke(
        hotelAvailabilityOutPort,
        roomSubstitutionResponse,
        roomRates,
        roomTypeAvailability,
        List.of("PPLDBL", "PPDLOW"),
        "PP");

    assertThat(rooms, hasSize(1));
    assertEquals("PPDLOW", rooms.getFirst().getPmsRoomType());
    assertTrue(rooms.getFirst().getIsSubstitution());
    assertEquals("PPLDBL", rooms.getFirst().getSubstitution());
  }

  @Test
  void getHotelAvailabilityLimitedInventory__ShouldReturnOK() {
    //Arrange
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    when(apiLimitsService.getHotelAvailabilityResponses(
        any(AvailabilityRequestDto.class))).thenReturn(
        mockHotelAvailability("2022-11-01", "2022-11-03").block());
    AvailabilityRequestDto availabilityRequestDto = mockAvailabilityRequestDto(null, "2022-11-03");
    availabilityRequestDto.setRoomTypes(List.of("DB", "DB"));
    availabilityRequestDto.setAdults(List.of(2, 2));
    availabilityRequestDto.setChildren(List.of(0, 0));
    availabilityRequestDto.setCotsRequired(List.of(Boolean.TRUE, Boolean.FALSE));

    when(availabilityRequestMapper.toHotelAvailabilityRequestDto(
        any(AvailabilityRequest.class))).thenReturn(
        availabilityRequestDto);
    when(ohipAvailabilityClient.getRoomTypes(any(String.class))).thenReturn(mockRoomTypes("ST"));
    when(ohipAvailabilityClient.getHotelAvailabilityRequest(
        any(AvailabilityRequestDto.class))).thenReturn(
        mockHotelAvailability("2022-11-01", "2022-11-03"));
    when(rulesAgentClient.getBookingChannelInfo(any())).thenReturn(
        mockBookingChannelInfoResponseDto());
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(ohipAvailabilityClient.getHotelInventory(
        any(String.class), any(String.class), any(String.class),
        any(Integer.class), any())).thenReturn(mockHotelInventory(200));
    when(concurrentTracer.wrap(any(Supplier.class))).thenAnswer(invocation -> invocation.getArgument(0));

    //Act
    var result = hotelAvailabilityOutPort.getHotelAvailability(createAvailabilityRequest(null));

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.getHotelId(), is("LONEUS"));
    assertThat(result.getStartDate(), is("2022-11-01"));
    assertThat(result.getEndDate(), is("2022-11-03"));

    assertThat(result.getRoomRates(), Matchers.<Collection<AvailabilityRoomRate>>allOf(
        notNullValue(),
        hasSize(equalTo(2)),
        contains(hasProperty("ratePlanCode", is("FLEXRATE")),
            hasProperty("ratePlanCode", is("ADVANCED")))
    ));

    assertThat(result.getRoomRates().get(0).getRoomTypes(),
        Matchers.<Collection<AvailabilityRoomType>>allOf(
            notNullValue(),
            hasSize(equalTo(1))
        ));
  }

  @Test
  void getHotelAvailability_promotionCode__ShouldReturnOK() {
    //Arrange
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    when(apiLimitsService.getHotelAvailabilityResponses(
        any(AvailabilityRequestDto.class))).thenReturn(
        mockHotelAvailability("2022-11-01", "2022-11-03").block());
    var availabilityRequestDto = mockAvailabilityRequestDto(null, "2022-11-03");
    availabilityRequestDto.setPromotionCode("PROMO");

    when(availabilityRequestMapper.toHotelAvailabilityRequestDto(
        any(AvailabilityRequest.class))).thenReturn(availabilityRequestDto);
    when(ohipAvailabilityClient.getRoomTypes(any(String.class))).thenReturn(mockRoomTypes("ST"));
    when(ohipAvailabilityClient.getHotelAvailabilityRequest(
        any(AvailabilityRequestDto.class))).thenReturn(
        mockHotelAvailability("2022-11-01", "2022-11-03"));
    when(rulesAgentClient.getBookingChannelInfo(any())).thenReturn(
        mockBookingChannelInfoResponseDto());
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(ohipAvailabilityClient.getHotelInventory(
        any(String.class), any(String.class), any(String.class),
        any(Integer.class), any())).thenReturn(mockHotelInventory(200));
    when(concurrentTracer.wrap(any(Supplier.class))).thenAnswer(invocation -> invocation.getArgument(0));

    //Act
    var result = hotelAvailabilityOutPort.getHotelAvailability(createAvailabilityRequest(""));

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.getHotelId(), is("LONEUS"));
    assertThat(result.getStartDate(), is("2022-11-01"));
    assertThat(result.getEndDate(), is("2022-11-03"));
    assertThat(result.getRoomRates(), Matchers.<Collection<AvailabilityRoomRate>>allOf(
        notNullValue(),
        hasSize(equalTo(2)),
        contains(hasProperty("ratePlanCode", is("FLEXRATE")),
            hasProperty("ratePlanCode", is("ADVANCED")))
    ));

    assertThat(result.getRoomRates().get(0).getRoomTypes(),
        Matchers.<Collection<AvailabilityRoomType>>allOf(
            notNullValue(),
            hasSize(equalTo(1)),
            contains(hasProperty("roomType", equalTo("DB")))
        ));

  }

  @Test
  void getHotelAvailabilityByIds_forPIChannel__ShouldReturnOK() {
    //Arrange
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency())
        .thenReturn(1);
    when(availabilityByIdsRequestMapper.toRequestDto(
        any(AvailabilityByIdsSearchRequest.class))).thenReturn(
        mockMultiHotelAvailabilityRequestDto("PI"));
    when(ohipAvailabilityClient.getRoomTypes(any(String.class))).thenReturn(mockRoomTypes("ST"));
    when(ohipAvailabilityClient.getHotelInventory(
        any(String.class), any(String.class), any(String.class),
        any(Integer.class), any())).thenReturn(mockHotelInventory(200));
    when(ohipProfileClient.getCompanyByCorporateId(anyString()))
        .thenReturn(mockCompanyProfile());
    when(ohipRatePlansClient.getRatePlans(any(), any()))
        .thenReturn(Mono.just(mockRatePlanSummary()));
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequest(
        any(MultiHotelAvailabilityRequestDto.class))).thenReturn(
        mockHotelAvailability("2022-11-01", "2022-11-03"));
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    //Act
    var result = hotelAvailabilityOutPort.getHotelAvailabilityByIds(
        createAvailabilityByIdsSearchRequest());

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.getHotelAvailability().get(0).getHotelId(), is("LONEUS"));
    assertThat(result.getHotelAvailability().get(0).getStartDate(), is("2022-11-01"));
    assertThat(result.getHotelAvailability().get(0).getEndDate(), is("2022-11-03"));
    assertThat(result.getHotelAvailability().get(0).getRoomRates(),
        Matchers.<Collection<AvailabilityRoomRate>>allOf(
            notNullValue(),
            hasSize(equalTo(1)),
            contains(hasProperty("ratePlanCode", is("FLEXRATE")))
        ));

    assertThat(result.getHotelAvailability().get(0).getRoomRates().get(0).getRoomTypes(),
        Matchers.<Collection<AvailabilityRoomType>>allOf(
            notNullValue(),
            hasSize(equalTo(1)),
            contains(hasProperty("roomType", equalTo("DB")))
        ));

  }

  @SneakyThrows
  @Test
  void getHotelAvailabilityByIds_Parallel_ShouldReturnOk() {
    // Arrange
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency())
        .thenReturn(1);
    when(availabilityByIdsRequestMapper.toRequestDto(
        any(AvailabilityByIdsSearchRequest.class))).thenReturn(
        mockMultiHotelAvailabilityRequestDto("PI"));
    when(ohipAvailabilityClient.getRoomTypes(any(String.class))).thenReturn(mockRoomTypes("ST"));
    when(ohipAvailabilityClient.getHotelInventory(
        any(String.class), any(String.class), any(String.class),
        any(Integer.class), any())).thenReturn(mockHotelInventory(200));
    when(ohipProfileClient.getCompanyByCorporateId(anyString()))
        .thenReturn(mockCompanyProfile());
    when(ohipRatePlansClient.getRatePlans(any(), any()))
        .thenReturn(Mono.just(mockRatePlanSummary()));
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();
    List<String> listStart = List.of("2023-02-01", "2023-03-04");
    List<String> listEnd = List.of("2023-02-04", "2023-03-14");
    AtomicInteger i = new AtomicInteger(-1);

    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequest(
        any(MultiHotelAvailabilityRequestDto.class)))
        .thenAnswer(invocation -> {
          i.getAndIncrement();
          threadNames.add(Thread.currentThread().getName());
          return mockHotelAvailability(listStart.get(i.get()), listEnd.get(i.get()));
        });

    var request = getAvailabilityByIdsSearchRequest();
    var result = hotelAvailabilityOutPort.getHotelAvailabilityByIds(
        request);

    //Assert
    assertNotNull(result);
    assertEquals("LONEUS", result.getHotelAvailability().get(0).getHotelId());
    assertEquals("2023-02-01", result.getHotelAvailability().get(0).getStartDate());
    assertEquals("2023-03-14", result.getHotelAvailability().get(0).getEndDate());
    assertEquals("FLEXRATE",
        result.getHotelAvailability().get(0).getRoomRates().get(0).getRatePlanCode());
    assertEquals("DB",
        result.getHotelAvailability().get(0).getRoomRates().get(0).getRoomTypes().get(0)
            .getRoomType());
  }

  @Test
  void getHotelAvailabilityByIds_Parallel__shouldThrowException() throws InterruptedException {
    //Arrange
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency())
        .thenReturn(1);
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    when(availabilityByIdsRequestMapper.toRequestDto(
        any(AvailabilityByIdsSearchRequest.class))).thenReturn(
        mockMultiHotelAvailabilityRequestDto("PI"));
    when(ohipAvailabilityClient.getRoomTypes(any(String.class))).thenReturn(mockRoomTypes("ST"));
    when(ohipAvailabilityClient.getHotelInventory(
        any(String.class), any(String.class), any(String.class),
        any(Integer.class), any())).thenReturn(mockHotelInventory(200));
    when(ohipProfileClient.getCompanyByCorporateId(anyString()))
        .thenReturn(mockCompanyProfile());
    when(ohipRatePlansClient.getRatePlans(any(), any()))
        .thenReturn(Mono.just(mockRatePlanSummary()));
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);

    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequest(
        any(MultiHotelAvailabilityRequestDto.class)))
        .thenAnswer(invocation -> {
          threadNames.add(Thread.currentThread().getName());
          throw new HotelAvailabilityException(ErrorCode.OHIP_RETRIVE_AVAILABILITY_BY_IDS_EXCEPTION, ERROR);
        });

    var request = getAvailabilityByIdsSearchRequest();
    //Act
    var exception = Assertions.assertThrows(HotelAvailabilityException.class,
        () -> hotelAvailabilityOutPort.getHotelAvailabilityByIds(request));

    //Assert
    assertEquals(ERROR, exception.getMessage());
    // Verify all threads completed
    restrictionsExecutorService.shutdown();
    assertTrue(restrictionsExecutorService.awaitTermination(5, TimeUnit.SECONDS));
  }

  @Test
  void getHotelAvailabilityByIds_forDISTRChannel__ShouldReturnOK() {
    //Arrange
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency())
            .thenReturn(1);
    when(availabilityByIdsRequestMapper.toRequestDto(
            any(AvailabilityByIdsSearchRequest.class))).thenReturn(
            mockMultiHotelAvailabilityRequestDtoDBAndTwinDistr("AMADEUS"));
    when(ohipAvailabilityClient.getRoomTypes(any(String.class))).thenReturn(mockRoomTypesDBAndTwin());
    when(ohipAvailabilityClient.getHotelInventory(
            any(String.class), any(String.class), any(String.class),
            any(Integer.class), any())).thenReturn(mockHotelInventoryDBAndTwin(200));
    when(ohipProfileClient.getCompanyByCorporateId(anyString()))
            .thenReturn(mockCompanyProfile());
    when(ohipRatePlansClient.getRatePlans(any(), any()))
            .thenReturn(Mono.just(mockRatePlanSummary()));
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequest(
            any(MultiHotelAvailabilityRequestDto.class))).thenReturn(
            mockHotelAvailabilityDbAndTwin());
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);

    //Act
    var result = hotelAvailabilityOutPort.getHotelAvailabilityByIds(
            createAvailabilityByIdsSearchRequestDBAndTwinDISTR("AMADEUS"));

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.getHotelAvailability().get(0).getHotelId(), is("LONEUS"));
    assertThat(result.getHotelAvailability().get(0).getStartDate(), is("2022-11-01"));
    assertThat(result.getHotelAvailability().get(0).getEndDate(), is("2022-11-03"));
    assertThat(result.getHotelAvailability().get(0).getRoomRates(),
            Matchers.<Collection<AvailabilityRoomRate>>allOf(
                    notNullValue(),
                    hasSize(equalTo(1)),
                    contains(hasProperty("ratePlanCode", is("FLEXRATE")))
            ));

    assertThat(result.getHotelAvailability().get(0).getRoomRates().get(0).getRoomTypes(),
            Matchers.<Collection<AvailabilityRoomType>>anyOf(
                    notNullValue(),
                    hasSize(equalTo(2)),
                    contains(hasProperty("roomType", equalTo("DB")))
            ));

    assertThat(result.getHotelAvailability().get(0).getRoomRates().get(0).getRoomTypes(),
            Matchers.<Collection<AvailabilityRoomType>>anyOf(
                    notNullValue(),
                    hasSize(equalTo(2)),
                    contains(hasProperty("roomType", equalTo("TWIN")))
            ));
  }

  @Test
  void getHotelAvailabilityByIdsV2__ShouldSplitRequestsOk() {
    //Arrange
    var multipleRequests = mockMultiHotelAvailabilityRequestV2DtoList();
    var mappedResponses = createAvailabilityByIdsV2ResultList();
    when(availabilityByIdsRequestMapper.toRequestV2Dto(
            any(AvailabilityByIdsSearchCriteriaV2.class))).thenReturn(
            multipleRequests.get(0), multipleRequests.get(1), multipleRequests.get(2), multipleRequests.get(3));
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
            .thenReturn(mockHotelInventory(6));
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency())
            .thenReturn(1);
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(
            any(AvailabilityByIdsSearchCriteriaV2Dto.class))).thenReturn(
            mockHotelAvailabilityV2());
    when(availabilityByIdsRequestMapper.toResultV2Model(
            any(MultiRoomRateAvailabilityResponseType.class))).thenReturn(
            mappedResponses.get(0), mappedResponses.get(1), mappedResponses.get(2), mappedResponses.get(3)
    );

    //Act
    hotelAvailabilityOutPort
            .getHotelAvailabilityByIdsV2(createAvailabilityByIdsSearchRequestV2("PI"));

    //Assert
    Mockito.verify(availabilityByIdsRequestMapper, times(1))
            .toRequestV2Dto(requestsBeforeMappingCaptor.capture());
    List<AvailabilityByIdsSearchCriteriaV2> requests = requestsBeforeMappingCaptor.getAllValues();
    requests.sort(Comparator.comparing((AvailabilityByIdsSearchCriteriaV2 request) -> request.getRooms().size())
            .thenComparing(request -> request.getRooms().get(0).getChildren()));

    assertRequestBeforeMapping(requests.get(0));
    assertEquals(1, requests.get(0).getRooms().size());
    assertRoomBeforeMapping(requests.get(0).getRooms().get(0),"DB", 1, 0);
  }

  @Test
  void getHotelAvailabilityByIdsV2__NoHotelInventory() {
    //Arrange
    var multipleRequests = mockMultiHotelAvailabilityRequestV2DtoList();
    var mappedResponses = createAvailabilityByIdsV2ResultList();
    when(availabilityByIdsRequestMapper.toRequestV2Dto(
            any(AvailabilityByIdsSearchCriteriaV2.class))).thenReturn(
            multipleRequests.get(0), multipleRequests.get(1), multipleRequests.get(2), multipleRequests.get(3));
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
            .thenReturn(mockHotelInventory(0));
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency())
            .thenReturn(1);
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(
            any(AvailabilityByIdsSearchCriteriaV2Dto.class))).thenReturn(
            mockHotelAvailabilityV2());
    when(availabilityByIdsRequestMapper.toResultV2Model(
            any(MultiRoomRateAvailabilityResponseType.class))).thenReturn(
            mappedResponses.get(0), mappedResponses.get(1), mappedResponses.get(2), mappedResponses.get(3));

    //Act
    var result = hotelAvailabilityOutPort
            .getHotelAvailabilityByIdsV2(createAvailabilityByIdsSearchRequestV2("PI"));


    assertThat(result, notNullValue());
  }

  @Test
  void getHotelAvailabilityByIdsV2__DISTR_channel_NoHotelInventory() {
//Arrange
    var multipleRequests = mockMultiHotelAvailabilityRequestV2DtoList();
    var mappedResponses = createAvailabilityByIdsV2ResultList();
    when(availabilityByIdsRequestMapper.toRequestV2Dto(
            any(AvailabilityByIdsSearchCriteriaV2.class))).thenReturn(
            multipleRequests.get(0), multipleRequests.get(1), multipleRequests.get(2), multipleRequests.get(3));
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
            .thenReturn(mockHotelInventory(0));
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency())
            .thenReturn(1);
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(
            any(AvailabilityByIdsSearchCriteriaV2Dto.class))).thenReturn(
            mockHotelAvailabilityV2());
    when(availabilityByIdsRequestMapper.toResultV2Model(
            any(MultiRoomRateAvailabilityResponseType.class))).thenReturn(
            mappedResponses.get(0), mappedResponses.get(1), mappedResponses.get(2), mappedResponses.get(3));
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    //Act
    var result = hotelAvailabilityOutPort
            .getHotelAvailabilityByIdsV2(createAvailabilityByIdsSearchRequestV2("DISTR"));

    assertThat(result, notNullValue());
  }

  @Test
  void getHotelAvailabilityByIdsV2__ShouldHandleMultipleRequestsAndMergeResponsesOk() {
    //Arrange
    var multipleRequests = mockMultiHotelAvailabilityRequestV2DtoList();
    var mappedResponses = createAvailabilityByIdsV2ResultList();
    when(availabilityByIdsRequestMapper.toRequestV2Dto(
            any(AvailabilityByIdsSearchCriteriaV2.class))).thenReturn(
                    multipleRequests.get(0), multipleRequests.get(1), multipleRequests.get(2), multipleRequests.get(3));
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
            .thenReturn(mockHotelInventory(6));
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency())
            .thenReturn(1);
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(
            any(AvailabilityByIdsSearchCriteriaV2Dto.class))).thenReturn(
            mockHotelAvailabilityV2());
    when(availabilityByIdsRequestMapper.toResultV2Model(
            any(MultiRoomRateAvailabilityResponseType.class))).thenReturn(
                    mappedResponses.get(0), mappedResponses.get(1), mappedResponses.get(2), mappedResponses.get(3)
    );

    //Act
    var result = hotelAvailabilityOutPort
            .getHotelAvailabilityByIdsV2(createAvailabilityByIdsSearchRequestV2("PI"));

    //Assert
    Mockito.verify(availabilityByIdsRequestMapper, times(1))
            .toRequestV2Dto(any());
    Mockito.verify(ohipAvailabilityClient, times(1))
            .getHotelAvailabilityByIdsRequestV2(requestsCaptor.capture());
    var actualRequests = requestsCaptor.getAllValues();
    assertFalse(actualRequests.containsAll(multipleRequests));

    assertThat(result, notNullValue());
    assertEquals(2, result.getHotelAvailability().size());
    assertTrue(result.getHotelAvailability().stream().map(AvailabilityResultV2::getHotelId).anyMatch("LONEUS"::equals));
    assertTrue(result.getHotelAvailability().stream().map(AvailabilityResultV2::getHotelId).anyMatch("MANOLD"::equals));

    assertEquals(1, result.getHotelAvailability().get(0).getRoomStays().size());
    assertEquals("ST", result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomClass());
    assertEquals(3, result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().size());
    assertEquals("DB", result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().get(0).getTag());
    assertEquals("DB", result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().get(1).getTag());
    assertEquals("SB", result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().get(2).getTag());

    assertEquals(1, result.getHotelAvailability().get(1).getRoomStays().size());
    assertEquals("ST", result.getHotelAvailability().get(1).getRoomStays().get(0).getRoomClass());
    assertEquals(3, result.getHotelAvailability().get(1).getRoomStays().get(0).getRoomTypes().size());
    assertEquals("DB", result.getHotelAvailability().get(1).getRoomStays().get(0).getRoomTypes().get(0).getTag());
    assertEquals("DB", result.getHotelAvailability().get(1).getRoomStays().get(0).getRoomTypes().get(1).getTag());
    assertEquals("SB", result.getHotelAvailability().get(1).getRoomStays().get(0).getRoomTypes().get(2).getTag());
  }

  @Test
  void getHotelAvailabilityByIdsV2__MapResponseOk() {
    //Arrange
    var multipleRequests = mockMultiHotelAvailabilityRequestV2DtoList();
    var mappedResponses = createAvailabilityByIdsV2ResultList();
    when(availabilityByIdsRequestMapper.toRequestV2Dto(
            any(AvailabilityByIdsSearchCriteriaV2.class))).thenReturn(
            multipleRequests.get(0), multipleRequests.get(1), multipleRequests.get(2), multipleRequests.get(3));
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
            .thenReturn(mockHotelInventory(6));
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency())
            .thenReturn(1);
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(
            any(AvailabilityByIdsSearchCriteriaV2Dto.class))).thenReturn(
            mockHotelAvailabilityV2());
    when(availabilityByIdsRequestMapper.toResultV2Model(
            any(MultiRoomRateAvailabilityResponseType.class))).thenReturn(
            mappedResponses.get(0), mappedResponses.get(1), mappedResponses.get(2), mappedResponses.get(3));

    //Act
    var result = hotelAvailabilityOutPort
            .getHotelAvailabilityByIdsV2(createAvailabilityByIdsSearchRequestV2("PI"));

    //Assert
    assertThat(result, notNullValue());
    assertEquals(2, result.getHotelAvailability().size());
    assertEquals(1, result.getHotelAvailability().get(0).getRoomStays().size());
    assertEquals("ST", result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomClass());
    assertEquals(3, result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().size());
    var room1 = result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().get(0);
    assertRoom(room1, "DB", "1", "0");
    var room2 = result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().get(1);
    assertRoom(room2, "DB", "1", "0");
    var room3 = result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().get(2);
    assertRoom(room3, "SB", "1", "0");
  }

  @Test
  void getHotelAvailabilityByIdsV2__DISTR_channel_availability_from_different_roomClasses_MapResponseOk() {
    //Arrange
    var multipleRequests = mockMultiHotelAvailabilityRequestV2DtoList();
    var mappedResponses = createAvailabilityByIdsV2ResultList();
    when(availabilityByIdsRequestMapper.toRequestV2Dto(
            any(AvailabilityByIdsSearchCriteriaV2.class))).thenReturn(
            multipleRequests.get(0), multipleRequests.get(1), multipleRequests.get(2), multipleRequests.get(3));
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
            .thenReturn(mockHotelInventory(10));
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency())
            .thenReturn(1);
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(
            any(AvailabilityByIdsSearchCriteriaV2Dto.class))).thenReturn(
            mockHotelAvailabilityV2());
    when(availabilityByIdsRequestMapper.toResultV2Model(
            any(MultiRoomRateAvailabilityResponseType.class))).thenReturn(
            mappedResponses.get(0), mappedResponses.get(1), mappedResponses.get(2), mappedResponses.get(3));
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    //Act
    var result = hotelAvailabilityOutPort
            .getHotelAvailabilityByIdsV2(createAvailabilityByIdsSearchRequestV2("DISTR"));

    //Assert
    assertThat(result, notNullValue());
    assertEquals(2, result.getHotelAvailability().size());
    assertEquals(1, result.getHotelAvailability().get(0).getRoomStays().size());
    assertEquals("ST", result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomClass());
    assertEquals(3, result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().size());
    var room1 = result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().get(0);
    assertRoom(room1, "DB", "1", "0");
    var room2 = result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().get(1);
    assertRoom(room2, "DB", "1", "0");
    var room3 = result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().get(2);
    assertRoom(room3, "SB", "1", "0");
  }

  @Test
  void getHotelAvailabilityByIdsV2__RemoveAllHotelAvailabilityWhenOneRequestHasNoAvailability() {
    //Arrange
    var multipleRequests = mockMultiHotelAvailabilityRequestV2DtoList();
    var mappedResponses = createAvailabilityByIdsV2ResultList();
    mappedResponses.get(0).setHotelAvailability(null);
    when(availabilityByIdsRequestMapper.toRequestV2Dto(
            any(AvailabilityByIdsSearchCriteriaV2.class))).thenReturn(
            multipleRequests.get(0), multipleRequests.get(1), multipleRequests.get(2), multipleRequests.get(3));
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
            .thenReturn(mockHotelInventory(6));
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency())
            .thenReturn(1);
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(
            any(AvailabilityByIdsSearchCriteriaV2Dto.class))).thenReturn(
            mockHotelAvailabilityV2());
    when(availabilityByIdsRequestMapper.toResultV2Model(
            any(MultiRoomRateAvailabilityResponseType.class))).thenReturn(
            mappedResponses.get(0), mappedResponses.get(1), mappedResponses.get(2), mappedResponses.get(3)
    );

    //Act
    var result = hotelAvailabilityOutPort
            .getHotelAvailabilityByIdsV2(createAvailabilityByIdsSearchRequestV2("PI"));

    //Assert
    assertThat(result, notNullValue());
    assertNull(result.getHotelAvailability());
  }

  @Test
  void getHotelAvailabilityByIdsV2__DISTR_channel_RemoveAllHotelAvailabilityWhenOneRequestHasNoAvailability() {
    //Arrange
    var multipleRequests = mockMultiHotelAvailabilityRequestV2DtoList();
    var mappedResponses = createAvailabilityByIdsV2ResultList();
    mappedResponses.get(0).setHotelAvailability(null);
    when(availabilityByIdsRequestMapper.toRequestV2Dto(
            any(AvailabilityByIdsSearchCriteriaV2.class))).thenReturn(
            multipleRequests.get(0), multipleRequests.get(1), multipleRequests.get(2), multipleRequests.get(3));
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
            .thenReturn(mockHotelInventory(6));
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency())
            .thenReturn(1);
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(
            any(AvailabilityByIdsSearchCriteriaV2Dto.class))).thenReturn(
            mockHotelAvailabilityV2());
    when(availabilityByIdsRequestMapper.toResultV2Model(
            any(MultiRoomRateAvailabilityResponseType.class))).thenReturn(
            mappedResponses.get(0), mappedResponses.get(1), mappedResponses.get(2), mappedResponses.get(3)
    );
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    //Act
    var result = hotelAvailabilityOutPort
            .getHotelAvailabilityByIdsV2(createAvailabilityByIdsSearchRequestV2("DISTR"));

    //Assert
    assertThat(result, notNullValue());
    assertNull(result.getHotelAvailability());
  }

  @Test
  void getHotelAvailabilityByIdsV2__RemoveOneHotelWithNoAvailabilityForOneRequest() {
    //Arrange
    var multipleRequests = mockMultiHotelAvailabilityRequestV2DtoList();
    var mappedResponses = createAvailabilityByIdsV2ResultList();
    var availability = new ArrayList<>(mappedResponses.get(0).getHotelAvailability());
    availability.remove(0);
    mappedResponses.get(0).setHotelAvailability(availability);
    when(availabilityByIdsRequestMapper.toRequestV2Dto(
            any(AvailabilityByIdsSearchCriteriaV2.class))).thenReturn(
            multipleRequests.get(0), multipleRequests.get(1), multipleRequests.get(2), multipleRequests.get(3));
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
            .thenReturn(mockHotelInventory(6));
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency())
            .thenReturn(1);
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(
            any(AvailabilityByIdsSearchCriteriaV2Dto.class))).thenReturn(
            mockHotelAvailabilityV2());
    when(availabilityByIdsRequestMapper.toResultV2Model(
            any(MultiRoomRateAvailabilityResponseType.class))).thenReturn(
            mappedResponses.get(0), mappedResponses.get(1), mappedResponses.get(2), mappedResponses.get(3)
    );

    //Act
    var result = hotelAvailabilityOutPort
            .getHotelAvailabilityByIdsV2(createAvailabilityByIdsSearchRequestV2("PI"));

    //Assert
    assertThat(result, notNullValue());
    assertEquals(1, result.getHotelAvailability().size());
    assertEquals(HOTEL_ID_2, result.getHotelAvailability().get(0).getHotelId());
  }

  @Test
  void getHotelAvailabilityByIdsV2__RoomTypeNotPresentInAllResponsesAndNotIncludedInFinalResponseOk() {
    //Arrange
    var multipleRequests = mockMultiHotelAvailabilityRequestV2DtoList();
    var mappedResponses = createAvailabilityByIdsV2ResultList();
    var roomStays = new ArrayList<>(mappedResponses.get(1).getHotelAvailability().get(0).getRoomStays());
    roomStays.add(RoomStay.builder()
            .roomClass("PP")
            .roomTypes(List.of(RoomTypeV2.builder().tag("DB").roomRates(createRoomRates()).build()))
            .build());
    mappedResponses.get(1).getHotelAvailability().get(0).setRoomStays(roomStays);
    when(availabilityByIdsRequestMapper.toRequestV2Dto(
            any(AvailabilityByIdsSearchCriteriaV2.class))).thenReturn(
            multipleRequests.get(0), multipleRequests.get(1), multipleRequests.get(2), multipleRequests.get(3));
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
            .thenReturn(mockHotelInventory(6));
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency())
            .thenReturn(1);
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(
            any(AvailabilityByIdsSearchCriteriaV2Dto.class))).thenReturn(
            mockHotelAvailabilityV2());
    when(availabilityByIdsRequestMapper.toResultV2Model(
            any(MultiRoomRateAvailabilityResponseType.class))).thenReturn(
            mappedResponses.get(0), mappedResponses.get(1), mappedResponses.get(2), mappedResponses.get(3));

    //Act
    var result = hotelAvailabilityOutPort
            .getHotelAvailabilityByIdsV2(createAvailabilityByIdsSearchRequestV2("PI"));

    //Assert
    assertThat(result, notNullValue());
    assertEquals(2, result.getHotelAvailability().size());

    assertEquals(1, result.getHotelAvailability().get(0).getRoomStays().size());
    assertEquals("ST", result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomClass());
    assertEquals(3, result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().size());
    assertTrue(result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().stream()
            .noneMatch(roomType -> roomType.getTag().equals("X")));

    assertEquals(1, result.getHotelAvailability().get(1).getRoomStays().size());
    assertEquals("ST", result.getHotelAvailability().get(1).getRoomStays().get(0).getRoomClass());
    assertEquals(3, result.getHotelAvailability().get(1).getRoomStays().get(0).getRoomTypes().size());
    assertTrue(result.getHotelAvailability().get(1).getRoomStays().get(0).getRoomTypes().stream()
            .noneMatch(roomType -> roomType.getTag().equals("X")));
  }

  @Test
  void getHotelAvailabilityByIdsV2__DISTR_channel_RoomTypeNotPresentInAllResponsesAndIncludedInFinalResponseOk() {
    //Arrange
    var multipleRequests = mockMultiHotelAvailabilityRequestV2DtoList();
    var mappedResponses = createAvailabilityByIdsV2ResultList();
    var roomStays = new ArrayList<>(mappedResponses.get(1).getHotelAvailability().get(0).getRoomStays());
    roomStays.add(RoomStay.builder()
            .roomClass("PP")
            .roomTypes(List.of(RoomTypeV2.builder().tag("X").roomRates(createRoomRates()).build()))
            .build());
    mappedResponses.get(1).getHotelAvailability().get(0).setRoomStays(roomStays);
    when(availabilityByIdsRequestMapper.toRequestV2Dto(
            any(AvailabilityByIdsSearchCriteriaV2.class))).thenReturn(
            multipleRequests.get(0), multipleRequests.get(1), multipleRequests.get(2), multipleRequests.get(3));
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
            .thenReturn(mockHotelInventory(6));
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency())
            .thenReturn(1);
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(
            any(AvailabilityByIdsSearchCriteriaV2Dto.class))).thenReturn(
            mockHotelAvailabilityV2());
    when(availabilityByIdsRequestMapper.toResultV2Model(
            any(MultiRoomRateAvailabilityResponseType.class))).thenReturn(
            mappedResponses.get(0), mappedResponses.get(1), mappedResponses.get(2), mappedResponses.get(3));
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);

    var availabilityByIdsSearchCriteriaV2 = createAvailabilityByIdsSearchRequestV2("DISTR");
    availabilityByIdsSearchCriteriaV2.setRates(RateV2.builder().ratePlanCodes(List.of("FLEXRATE")).build());
    //Act
    var result = hotelAvailabilityOutPort
            .getHotelAvailabilityByIdsV2(availabilityByIdsSearchCriteriaV2);

    //Assert
    assertThat(result, notNullValue());
    assertEquals(2, result.getHotelAvailability().size());

    assertEquals(1, result.getHotelAvailability().get(0).getRoomStays().size());
    assertEquals("ST", result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomClass());
    assertEquals(3, result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().size());
    assertTrue(result.getHotelAvailability().get(0).getRoomStays().get(0).getRoomTypes().stream()
            .noneMatch(roomType -> roomType.getTag().equals("X")));

    assertEquals(1, result.getHotelAvailability().get(1).getRoomStays().size());
    assertEquals("ST", result.getHotelAvailability().get(1).getRoomStays().get(0).getRoomClass());
    assertEquals(3, result.getHotelAvailability().get(1).getRoomStays().get(0).getRoomTypes().size());
    assertTrue(result.getHotelAvailability().get(1).getRoomStays().get(0).getRoomTypes().stream()
            .noneMatch(roomType -> roomType.getTag().equals("X")));
  }

  @Test
  void getHotelRoomPriceBreakdown__ShouldReturnOK() {
    //Arrange
    when(availabilityRequestMapper.toAvailabilityRequestDto(
        any(AvailabilityRoomSearchCriteria.class))).thenReturn(
        mockAvailabilityRequestDto(null, "2022-11-03"));
    when(apiLimitsService.getRateInfoResponse(
        any(AvailabilityRequestDto.class))).thenReturn(
        mockPriceBreakdownPerNight().block());
    when(priceBreakdownMapper.toDomainModel(any(SummaryDto.class))).thenReturn(
        mockAvailabilityRoomPriceBreakdown());
    when(availabilityOhipProperties.getMaxRequestedDaysRateInfo()).thenReturn(4);
    //Act
    var result = hotelAvailabilityOutPort.getHotelRoomPriceBreakdown(
        createAvailabilityRoomSearchCriteria("2022-11-03"));

    //Assert
    assertThat(result, notNullValue());

    assertThat(result.getCurrencyCode(), is("GBP"));
    assertThat(result.getTotalNetAmount(), is(new BigDecimal("118")));
    assertThat(result.getTotalGrossAmount(), is(new BigDecimal("98")));
    assertThat(result.getTotalTaxAmount(), is(new BigDecimal("20")));
    assertThat(result.getDailyPrices().get(0).getDate(), is("2022-11-01"));
    assertThat(result.getDailyPrices().get(0).getNetPrice(), is(new BigDecimal("59")));
    assertThat(result.getDailyPrices().get(1).getDate(), is("2022-11-02"));
    assertThat(result.getDailyPrices().get(1).getNetPrice(), is(new BigDecimal("59")));

  }

  @SneakyThrows
  @Test
  void getHotelRoomPriceBreakdownResponses_ShouldReturnOk() {
    //Arrange
    when(availabilityRequestMapper.toAvailabilityRequestDto(
        any(AvailabilityRoomSearchCriteria.class))).thenReturn(
        mockAvailabilityRequestDto(null, "2022-11-06"));
    when(priceBreakdownMapper.toDomainModel(any(SummaryDto.class))).thenReturn(
        mockAvailabilityRoomPriceBreakdown());
    when(availabilityOhipProperties.getMaxRequestedDaysRateInfo()).thenReturn(4);

    when(apiLimitsService.getRateInfoResponse(
        any(AvailabilityRequestDto.class))).thenReturn(mockPriceBreakdownPerNight().block());

    var request = createAvailabilityRoomSearchCriteria("2022-11-06");
    var result = hotelAvailabilityOutPort.getHotelRoomPriceBreakdown(request);

    //Assert
    assertNotNull(result);
    assertThat(result.getCurrencyCode(), is("GBP"));
    assertThat(result.getTotalNetAmount(), is(new BigDecimal("118")));
    assertThat(result.getTotalGrossAmount(), is(new BigDecimal("98")));
    assertThat(result.getTotalTaxAmount(), is(new BigDecimal("20")));
    assertThat(result.getDailyPrices().get(0).getDate(), is("2022-11-01"));
    assertThat(result.getDailyPrices().get(0).getNetPrice(), is(new BigDecimal("59")));
    assertThat(result.getDailyPrices().get(1).getDate(), is("2022-11-02"));
    assertThat(result.getDailyPrices().get(1).getNetPrice(), is(new BigDecimal("59")));
  }

  @Test
  void getHotelRoomPriceBreakdown__shouldThrowException() {
    //Arrange
    when(availabilityRequestMapper.toAvailabilityRequestDto(
        any(AvailabilityRoomSearchCriteria.class))).thenReturn(
        mockAvailabilityRequestDto(null, "2022-11-03"));
    when(apiLimitsService.getRateInfoResponse(
        any(AvailabilityRequestDto.class))).thenThrow(
        new HotelAvailabilityException(ErrorCode.OHIP_PRICE_BREAKDOWN_PERNIGHT_EXCEPTION, ERROR));
    when(priceBreakdownMapper.toDomainModel(any(SummaryDto.class))).thenReturn(
        mockAvailabilityRoomPriceBreakdown());
    when(availabilityOhipProperties.getMaxRequestedDaysRateInfo()).thenReturn(4);
    var request = createAvailabilityRoomSearchCriteria("2022-11-03");
    //Act
    var exception = Assertions.assertThrows(HotelAvailabilityException.class,
        () -> hotelAvailabilityOutPort.getHotelRoomPriceBreakdown(request));

    //Assert
    assertEquals(ERROR, exception.getMessage());

  }

  @Test
  void getgetHotelRoomPriceBreakdownResponses__shouldThrowException() {
    //Arrange
    when(availabilityRequestMapper.toAvailabilityRequestDto(
        any(AvailabilityRoomSearchCriteria.class))).thenReturn(
        mockAvailabilityRequestDto(null, "2022-11-06"));
    when(priceBreakdownMapper.toDomainModel(any(SummaryDto.class))).thenReturn(
        mockAvailabilityRoomPriceBreakdown());
    when(availabilityOhipProperties.getMaxRequestedDaysRateInfo()).thenReturn(4);

    when(apiLimitsService.getRateInfoResponse(
        any(AvailabilityRequestDto.class)))
        .thenThrow(
           new HotelAvailabilityException(ErrorCode.OHIP_PRICE_BREAKDOWN_PERNIGHT_EXCEPTION, ERROR)
        );

    var request = createAvailabilityRoomSearchCriteria("2022-11-06");
    //Act
    var exception = Assertions.assertThrows(HotelAvailabilityException.class,
        () -> hotelAvailabilityOutPort.getHotelRoomPriceBreakdown(request));

    //Assert
    assertEquals(ERROR, exception.getMessage());
  }

  @SneakyThrows
  @Test
  void getRatesInfo__ShouldReturnOK() {
    //Arrange
    when(availabilityOhipProperties.getMaxRequestedDaysRateInfo()).thenReturn(4);
    when(availabilityRequestMapper.toAvailabilityRequestDto(
        any(AvailabilityRoomSearchCriteria.class))).thenReturn(
         AvailabilityRequestDto.builder()
             .hotelId("LONEUS")
             .ratePlanCode("FLEXRATE")
             .roomStayStartDate("2022-11-01")
             .roomStayEndDate("2022-11-05").build());
    when(apiLimitsService
        .getRateInfoResponse(any(AvailabilityRequestDto.class))).thenReturn(
        mockPriceBreakdownPerNight().block());
    when(priceBreakdownMapper.toDomainModel(any(SummaryDto.class))).thenReturn(
        mockAvailabilityRoomPriceBreakdown());

    //Act
    var result = hotelAvailabilityOutPort.getRatesInfo(
        List.of(createAvailabilityRoomSearchCriteria("2022-11-03")));

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.get(0).getCurrencyCode(), is("GBP"));
    assertThat(result.get(0).getTotalNetAmount(), is(new BigDecimal("118")));
    assertThat(result.get(0).getTotalGrossAmount(), is(new BigDecimal("98")));
    assertThat(result.get(0).getTotalTaxAmount(), is(new BigDecimal("20")));
    assertThat(result.get(0).getDailyPrices().get(0).getDate(), is("2022-11-01"));
    assertThat(result.get(0).getDailyPrices().get(0).getNetPrice(), is(new BigDecimal("59")));
    assertThat(result.get(0).getDailyPrices().get(1).getDate(), is("2022-11-02"));
    assertThat(result.get(0).getDailyPrices().get(1).getNetPrice(), is(new BigDecimal("59")));
  }

  @SneakyThrows
  @Test
  void getRatesInfo__shouldThrowException() {
    //Arrange
    when(availabilityOhipProperties.getMaxRequestedDaysRateInfo()).thenReturn(3);
    when(availabilityRequestMapper.toAvailabilityRequestDto(
        any(AvailabilityRoomSearchCriteria.class))).thenReturn(
        AvailabilityRequestDto.builder()
            .hotelId("LONEUS")
            .ratePlanCode("FLEXRATE")
            .roomStayStartDate("2022-11-01")
            .roomStayEndDate("2022-11-05").build());

    when(apiLimitsService.getRateInfoResponse(
        any(AvailabilityRequestDto.class)))
        .thenThrow(new HotelAvailabilityException(
              ErrorCode.OHIP_PRICE_BREAKDOWN_PERNIGHT_EXCEPTION, ERROR)
        );
    var request = List.of(createAvailabilityRoomSearchCriteria("2022-11-03"));

    //Act
    var exception = Assertions.assertThrows(HotelAvailabilityException.class,
        () -> hotelAvailabilityOutPort.getRatesInfo(request));

    //Assert
    assertEquals(ERROR, exception.getMessage());
  }

  @SneakyThrows
  @Test
  void getHotelItemsInventory__ShouldReturnOk(){
    // Arrange
    when(itemsInventoryMapper.toDomainModel(any(ItemInventoryResponseDto.class))).thenReturn(
        mockItemInventoryResponse());
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    when(apiLimitsService.getItemInventoryResponses(
        any(ItemInventoryRequest.class))).thenReturn(createItemInventoryResponse());
    var request = ItemInventoryRequest.builder()
        .hotelId("LONEUS")
        .startDate("2022-01-01")
        .endDate("2022-11-03")
        .build();
    //Act
    var result = hotelAvailabilityOutPort.getHotelItemsInventory(request);

    // Assert
    assertThat(result, notNullValue());
    assertEquals(1, result.getItemsInventory().size());
    assertEquals(3, result.getItemsInventory().get(0).getInventories().size());
    assertThat(result.getItemsInventory().get(0).getName(), is("Cot"));
    assertThat(result.getItemsInventory().get(0).getInventories().get(0).getAvailable(), is(5));
  }

  @Test
  void getHotelItemsInventory__shouldThrowException() throws InterruptedException {
    //Arrange
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    when(itemsInventoryMapper.toDto(any(ItemInventoryRequest.class))).thenReturn(
        new ItemInventoryRequestDto());
    when(apiLimitsService.getItemInventoryResponses(any(ItemInventoryRequest.class))).thenThrow(
        new HotelReservationException(ErrorCode.OHIP_HOTEL_ITEMS_INVENTORY_EXCEPTION, ERROR));

    var request = ItemInventoryRequest.builder()
        .hotelId("LONEUS")
        .startDate("2022-01-01")
        .endDate("2022-11-03")
        .build();
    //Act
    var exception = Assertions.assertThrows(HotelReservationException.class,
        () -> hotelAvailabilityOutPort.getHotelItemsInventory(request));

    //Assert
    assertEquals(ERROR, exception.getMessage());
    // Verify all threads completed
    restrictionsExecutorService.shutdown();
    assertTrue(restrictionsExecutorService.awaitTermination(5, TimeUnit.SECONDS));
  }

  @Test
  void getHotelRoomsInventory__ShouldReturnOK() {
    //Arrange
    when(hotelRoomInventoryMapper.toDto(any(HotelInventoryRequest.class))).thenReturn(
        new HotelInventoryRequestDto("LONEUS", "2022-11-01", "2022-11-03"));
    when(ohipAvailabilityClient.getHotelInventory(
        any(String.class),
        any(String.class),
        any(String.class), any(Integer.class), any())).thenReturn(mockHotelInventory(200));

    //Act
    var result = hotelAvailabilityOutPort.getHotelRoomsInventory(createHotelInventoryRequest());

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.getRoomTypeInventories(), notNullValue());
    assertThat(result.getRoomTypeInventories(), hasSize(2));
    result.getRoomTypeInventories().forEach(roomTypeInventory -> {
      assertThat(roomTypeInventory.getCode(), in(List.of("DOUBLE", "WETDBL")));
      assertThat(roomTypeInventory.getAvailableCount(), is(200));
    });
  }

  @Test
  void getHotelRoomsInventoryOver90Nights__ShouldReturnOK() {
    //Arrange
    when(hotelRoomInventoryMapper.toDto(any(HotelInventoryRequest.class))).thenReturn(
        new HotelInventoryRequestDto("LONEUS", "2022-11-01", "2023-02-11"));
    when(ohipAvailabilityClient.getHotelInventory(
        any(String.class),
        any(String.class),
        any(String.class), any(Integer.class), any())).thenReturn(mockHotelInventory(200));

    //Act
    var result = hotelAvailabilityOutPort.getHotelRoomsInventory(
        createHotelInventoryRequestOver90Nights());

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.getRoomTypeInventories(), notNullValue());
    assertThat(result.getRoomTypeInventories(), hasSize(2));
    result.getRoomTypeInventories().forEach(roomTypeInventory -> {
      assertThat(roomTypeInventory.getCode(), in(List.of("DOUBLE", "WETDBL")));
      assertThat(roomTypeInventory.getAvailableCount(), is(200));
    });
  }

  @Test
  void getRoomTypesFromHotelInventory_success() {

    when(ohipAvailabilityClient.getHotelInventory(
       anyString(), anyString(), anyString(), anyInt())).thenReturn(mockHotelInventoryForRoomTypes());

    //Act
    var result = hotelAvailabilityOutPort.getRoomTypesFromHotelInventory("HEAPTI", "2023-10-01",
        "2023-10-10", 10);

    //Assert
    assertThat(result, notNullValue());
    assertThat(result, hasSize(1));
    assertEquals("DOUBLE", result.get(0));
  }

  @SneakyThrows
  @Test
  void getItemInventoryResponses_ShouldThrowException() {
    String inventoryError = "Error while requesting Hotel Inventory";

    // Arrange
    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();
        when(ohipAvailabilityClient.getHotelInventory(
            anyString(), anyString(), anyString(), anyInt()))
        .thenAnswer(invocation -> {
          threadNames.add(Thread.currentThread().getName());
          throw new  HotelReservationException(
              ErrorCode.OHIP_HOTEL_INVENTORY_EXCEPTION, inventoryError);
        });

    //Act
    var exception = Assertions.assertThrows(HotelReservationException.class,
        () -> hotelAvailabilityOutPort.getRoomTypesFromHotelInventory("HEAPTI", "2025-04-01",
            "2025-07-02", 10));

    // Verify all threads completed
    restrictionsExecutorService.shutdown();
    assertTrue(restrictionsExecutorService.awaitTermination(5, TimeUnit.SECONDS));

    //Assert
    assertEquals(inventoryError, exception.getMessage());
  }

  @Test
  void getRestrictionsByDateRange_success() {

    when(ohipAvailabilityClient.getRestrictionsByDateRange(
        any(RestrictionsByDateRangeSearchCriteria.class))).thenReturn(mockRestrictionsByDateRangeResult());

    //Act
    var result = hotelAvailabilityOutPort.getRestrictionsByDateRange(RestrictionsByDateRangeSearchCriteria.builder()
        .hotelId("HEAPTI").startDate("2025-08-01").endDate("2025-09-30").build());

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.getRestrictionsByDateRange(), notNullValue());
    assertThat(result.getRestrictionsByDateRange().getRestrictionsByDateRange(), notNullValue());
    assertThat(result.getRestrictionsByDateRange().getRestrictionsByDateRange().getRestrictionSets(), hasSize(2));
  }

  @Test
  void getRestrictionsByDateRange_over90days_success() {

    when(ohipAvailabilityClient.getRestrictionsByDateRange(
        any(RestrictionsByDateRangeSearchCriteria.class))).thenReturn(mockRestrictionsByDateRangeResult());

    //Act
    var result = hotelAvailabilityOutPort.getRestrictionsByDateRange(RestrictionsByDateRangeSearchCriteria.builder()
        .hotelId("HEAPTI").startDate("2025-08-01").endDate("2025-12-02").build());

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.getRestrictionsByDateRange(), notNullValue());
    assertThat(result.getRestrictionsByDateRange().getRestrictionsByDateRange(), notNullValue());
    assertThat(result.getRestrictionsByDateRange().getRestrictionsByDateRange().getRestrictionSets(), hasSize(4));
  }

  @SneakyThrows
  @Test
  void getRestrictionsByDateRange_ShouldThrowException() {
    String restrictiontsError = "Error while requesting hotel restrictions";

    // Arrange
    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();
    when(ohipAvailabilityClient.getRestrictionsByDateRange(
        any(RestrictionsByDateRangeSearchCriteria.class)))
        .thenAnswer(invocation -> {
          threadNames.add(Thread.currentThread().getName());
          throw new  HotelAvailabilityException(
              ErrorCode.OHIP_GET_RESTRICTIONS_BY_DATE_RANGE_EXCEPTION, restrictiontsError);
        });

    //Act
    RestrictionsByDateRangeSearchCriteria criteria = RestrictionsByDateRangeSearchCriteria.builder()
        .hotelId("HEAPTI").startDate("2025-08-01").endDate("2025-12-02").build();
    var exception = Assertions.assertThrows(HotelAvailabilityException.class,
        () -> hotelAvailabilityOutPort.getRestrictionsByDateRange(criteria));

    // Verify all threads completed
    restrictionsExecutorService.shutdown();
    assertTrue(restrictionsExecutorService.awaitTermination(5, TimeUnit.SECONDS));

    //Assert
    assertEquals(restrictiontsError, exception.getMessage());
  }

  @Test
  void getRestrictionsByDateRange_restrictionsResultEmpty() {

    when(ohipAvailabilityClient.getRestrictionsByDateRange(
        any(RestrictionsByDateRangeSearchCriteria.class))).thenReturn(RestrictionsByDateRangeResult.builder().build());

    //Act
    var result = hotelAvailabilityOutPort.getRestrictionsByDateRange(RestrictionsByDateRangeSearchCriteria.builder()
        .hotelId("HEAPTI").startDate("2025-08-01").endDate("2025-12-02").build());

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.getRestrictionsByDateRange(), nullValue());
  }

  @Test
  void getRestrictionsByDateRange_restrictionsResultParentEmpty() {

    when(ohipAvailabilityClient.getRestrictionsByDateRange(
        any(RestrictionsByDateRangeSearchCriteria.class))).thenReturn(RestrictionsByDateRangeResult.builder()
        .restrictionsByDateRange(RestrictionsByDateRangeParent.builder().build()).build());

    //Act
    var result = hotelAvailabilityOutPort.getRestrictionsByDateRange(RestrictionsByDateRangeSearchCriteria.builder()
        .hotelId("HEAPTI").startDate("2025-08-01").endDate("2025-12-02").build());

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.getRestrictionsByDateRange(), notNullValue());
    assertThat(result.getRestrictionsByDateRange().getRestrictionsByDateRange(), nullValue());
  }

  @Test
  void getHotelRoomsInventory__shouldThrowException() {
    //Arrange
    when(hotelRoomInventoryMapper.toDto(any(HotelInventoryRequest.class))).thenReturn(
        new HotelInventoryRequestDto("LONEUS", "2022-11-01", "2022-11-03"));
    when(ohipAvailabilityClient.getHotelInventory("LONEUS", "2022-11-01", "2022-11-03",
        1, null)).thenThrow(
        new HotelAvailabilityException(ErrorCode.OHIP_HOTEL_INVENTORY_EXCEPTION, ERROR));

    var request = createHotelInventoryRequest();
    //Act
    var exception = Assertions.assertThrows(HotelAvailabilityException.class,
        () -> hotelAvailabilityOutPort.getHotelRoomsInventory(request));

    //Assert
    assertEquals(ERROR, exception.getMessage());
  }

  @Test
  void getRateCodePricing__ShouldReturnOK() {
    //Arrange
    when(apiLimitsService.getRateInfoResponse(any(RateCodeCriteria.class),
        any(RateCodeRoomInfoCriteria.class))).thenReturn(mockPriceBreakdownPerNight().block());

    //Act
    var result = hotelAvailabilityOutPort.getRateCodePricing(createRateCodeCriteria());

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.getRatePlanCode(), is("FLEXRATE"));
    assertThat(result.getCurrencyCode(), is("EUR"));
    assertThat(result.getTotalNetAmount(), is(BigDecimal.valueOf(118)));

  }

  @Test
  void getRateCodePricing__shouldThrowException() {
    //Arrange
    String error = "Could not calculate the total cost for the rate plan code";
    when(apiLimitsService.getRateInfoResponse(any(RateCodeCriteria.class),
        any(RateCodeRoomInfoCriteria.class))).thenThrow(
        new HotelAvailabilityException(ErrorCode.OHIP_RATECODE_INFO_EXCEPTION, error));

    var request = createRateCodeCriteria();
    //Act
    var exception = Assertions.assertThrows(HotelAvailabilityException.class,
        () -> hotelAvailabilityOutPort.getRateCodePricing(request));

    //Assert
    assertEquals(error, exception.getMessage());
  }

  @ParameterizedTest
  @CsvSource({"true,true", "true,false", "false,true", "false,false"})
  void getMultiHotelAvailabilityRequestV2_ShouldReturnOK(boolean hasMimimumRate,
      boolean hasRoomStay) {
    //Arrange
    MultiHotelAvailabilityRequestV2Dto req = MultiHotelAvailabilityRequestV2Dto.builder()
        .hotelIds(List.of(HOTEL_ID_1))
        .arrivalDate(LocalDate.of(2025, 3, 1))
        .departureDate(LocalDate.of(2025, 3, 2))
        .rooms(List.of(
            RoomDto.builder().roomTypes(List.of("DB")).build()))
        .build();
    MultiAvailabilityResultV2 resp = MultiAvailabilityResultV2.builder()
        .hotelAvailabilityResults(List.of(HotelAvailabilityResultV2.builder().build()))
        .build();
    SearchPropertyResponseType response = new SearchPropertyResponseType();
    if (hasRoomStay) {
      SearchPropertyRoomStayType roomStay = mockResponseType(hasMimimumRate);
      List<SearchPropertyRoomStayType> list = List.of(roomStay);
      response.setRoomStays(list);
      response.setLimit(1);
      response.setOffset(0);
      response.setHasMore(false);
    }
    MultiHotelAvailabilityRequestV2 request =
        MultiHotelAvailabilityRequestV2.builder().hotelIds(List.of(HOTEL_ID_1))
            .arrivalDate(LocalDate.of(2025, 3, 1))
            .departureDate(LocalDate.of(2025, 3, 2))
            .rooms(List.of(
                Room.builder().roomTypes(List.of("DB")).build()))
            .build();
    when(multiAvailabilityRequestMapper.toRequestV2Dto(
        any(MultiHotelAvailabilityRequestV2.class))).thenReturn(req);
    when(multiAvailabilityRequestMapper.toResultV2Model(
        any(SearchPropertyResponseType.class))).thenReturn(resp);

    when(apiLimitsService.getMultiHotelAvailabilityRequestV2(
        any(MultiHotelAvailabilityRequestV2Dto.class))).thenReturn(response);

    //Act
    var result = hotelAvailabilityOutPort.getMultiHotelAvailabilitiesV2(request);

    //Assert
    assertThat(result, notNullValue());
    assertEquals(resp, result);
  }

  @Test
  void getMultiHotelAvailabilityRequestV2__shouldThrowException() {
    //Arrange
    String error = "error";
    MultiHotelAvailabilityRequestV2Dto req = MultiHotelAvailabilityRequestV2Dto.builder()
        .hotelIds(List.of(HOTEL_ID_1, HOTEL_ID_2))
        .arrivalDate(LocalDate.of(2025, 3, 1))
        .departureDate(LocalDate.of(2025, 3, 2))
        .rooms(List.of(
            RoomDto.builder().roomTypes(List.of("DB")).build()))
        .build();
    when(apiLimitsService.getMultiHotelAvailabilityRequestV2(
        any(MultiHotelAvailabilityRequestV2Dto.class))).thenThrow(
        new HotelReservationException(ErrorCode.OHIP_MULTIHOTEL_AVAILABILITY_EXCEPTION, error));
    when(multiAvailabilityRequestMapper.toRequestV2Dto(
        any(MultiHotelAvailabilityRequestV2.class))).thenReturn(req);
    MultiHotelAvailabilityRequestV2 request =
        MultiHotelAvailabilityRequestV2.builder().hotelIds(List.of(HOTEL_ID_1, HOTEL_ID_2))
            .arrivalDate(LocalDate.of(2025, 3, 1))
            .departureDate(LocalDate.of(2025, 3, 2))
            .rooms(List.of(
                Room.builder().roomTypes(List.of("DB")).build()))
            .build();

    //Act
    var exception = Assertions.assertThrows(HotelReservationException.class,
        () -> hotelAvailabilityOutPort.getMultiHotelAvailabilitiesV2(request));

    //Assert
    assertEquals(error, exception.getMessage());
  }

  @ParameterizedTest
  @MethodSource("hotelIdSets")
  void fetchAvailabilityInParallel_shouldReturnResults_forBatchRequest(List<String> hotelIds, int expectedBatchCount) {
    var request = mockMultiHotelAvailabilityRequestV2DtoListForBatchRequest(hotelIds);
    var result = AvailabilityByIdsResultV2.builder().build();

    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(
        any(AvailabilityByIdsSearchCriteriaV2Dto.class))).thenReturn(
        mockHotelAvailabilityV2());
    when(availabilityByIdsRequestMapper.toResultV2Model(any()))
        .thenReturn(result);

    var resp = hotelAvailabilityOutPort.fetchAvailabilityInParallel(request);

    assertNotNull(resp);

    // Capture arguments
    ArgumentCaptor<AvailabilityByIdsSearchCriteriaV2Dto> captor =
        ArgumentCaptor.forClass(AvailabilityByIdsSearchCriteriaV2Dto.class);
    verify(ohipAvailabilityClient, times(expectedBatchCount))
        .getHotelAvailabilityByIdsRequestV2(captor.capture());

    // 1. Each batch has <= 10 hotelIds
    captor.getAllValues().forEach(dto ->
        assertTrue(dto.getHotelIds().size() <= 10, "Batch size exceeded 10"));

    // 2. All hotelIds are preserved and in order
    List<String> allBatchedIds = captor.getAllValues().stream()
        .flatMap(dto -> dto.getHotelIds().stream())
        .toList();
    assertEquals(hotelIds, allBatchedIds, "Batched hotelIds do not match input");
  }

  private RoomSubstitutionRuleResponseDto mockRoomSubstitutionResponse(List<String> roomTypes) {
    return RoomSubstitutionRuleResponseDto.builder()
        .requestDetails(RoomSubstitutionRequestDetailsDto.builder().children(1).adults(1).roomType("DB").pms("OP").build())
        .substitutionList(mockRoomSubstitutionList(roomTypes)).build();
  }

  private List<RoomSubstitutionDto> mockRoomSubstitutionList (List<String> roomTypes) {
    return roomTypes.stream().map(type -> RoomSubstitutionDto.builder().type(type).build()).toList();
  }

  private void assertRoom(RoomTypeV2 room, String tag, String adults, String children) {
    assertEquals(tag, room.getTag());
    assertEquals(adults, room.getAdults());
    assertEquals(children, room.getChildren());
    assertEquals("1", room.getNumberOfRooms());
    var roomRate = room.getRoomRates().get(0);
    assertThat(roomRate.getRatePlanCode(), is("FLEXRATE"));
    assertThat(roomRate.getRoomRateInfo().getPackages().get(0).getCode(), is("MDP"));
    assertThat(roomRate.getRoomRateInfo().getPriceInfo().get(0).getAmountAfterTax(), is(BigDecimal.ONE));
  }

  private void assertRoomBeforeMapping(Room room, String tag, Integer adults, Integer children) {
    assertEquals(tag, room.getTag());
    assertEquals(adults, room.getAdults());
    assertEquals(children, room.getChildren());
    assertEquals(1, room.getNumberOfRooms());
  }

  private void assertRequestBeforeMapping(AvailabilityByIdsSearchCriteriaV2 request) {
    assertEquals(2, request.getHotelIds().size());
    assertTrue(request.getHotelIds().containsAll(List.of(HOTEL_ID_1, HOTEL_ID_2)));
    assertEquals(ARRIVAL_DATE, request.getArrivalDate());
    assertEquals(DEPARTURE_DATE, request.getDepartureDate());
  }

  private RateCodeCriteria createRateCodeCriteria() {
    return RateCodeCriteria.builder()
        .hotelId("LONEUS")
        .arrivalDate("2022-11-01")
        .departureDate("2022-11-03")
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


  private HotelInventoryRequest createHotelInventoryRequest() {
    return HotelInventoryRequest.builder()
        .hotelId("LONEUS")
        .dateRangeStart("2022-11-01")
        .dateRangeEnd("2022-11-03")
        .build();
  }

  private HotelInventoryRequest createHotelInventoryRequestOver90Nights() {
    return HotelInventoryRequest.builder()
        .hotelId("LONEUS")
        .dateRangeStart("2022-11-01")
        .dateRangeEnd("2023-02-11")
        .build();
  }


  private ItemInventoryResponse mockItemInventoryResponse() {
    return ItemInventoryResponse.builder()
        .itemsInventory(Collections.singletonList(mockItemInventoryMock()))
        .build();
  }

  private ItemInventory mockItemInventoryMock() {

    return ItemInventory.builder()
        .name("Cot")
        .description("Cot")
        .inventories(Arrays.asList(mockInventoryAvailabilityMock("2022-03-01"),
            mockInventoryAvailabilityMock("2022-03-02"),
            mockInventoryAvailabilityMock("2022-03-03")))
        .build();

  }

  private InventoryAvailability mockInventoryAvailabilityMock(String date) {
    return InventoryAvailability
        .builder()
        .available(5)
        .total(5)
        .date(date)
        .build();
  }

  private AvailabilityRoomSearchCriteria createAvailabilityRoomSearchCriteria(String date) {
    return AvailabilityRoomSearchCriteria.builder()
        .hotelId("LONEUS")
        .ratePlanCode("FLEXRATE")
        .arrivalDate("2022-11-01")
        .departureDate(date)
        .roomType("DB")
        .numberOfRooms(1)
        .adults(2)
        .children(0)
        .build();
  }

  private AvailabilityRoomPriceBreakdown mockAvailabilityRoomPriceBreakdown() {
    AvailabilityDailyPrice pb1 = AvailabilityDailyPrice.builder()
        .netPrice(new BigDecimal("59"))
        .date("2022-11-01")
        .build();

    AvailabilityDailyPrice pb2 = AvailabilityDailyPrice.builder()
        .netPrice(new BigDecimal("59"))
        .date("2022-11-02")
        .build();

    return AvailabilityRoomPriceBreakdown.builder()
        .dailyPrices(new ArrayList<>(Arrays.asList(pb1, pb2)))
        .totalNetAmount(new BigDecimal("118"))
        .totalGrossAmount(new BigDecimal("98"))
        .totalTaxAmount(new BigDecimal("20"))
        .currencyCode("GBP")
        .build();
  }

  private Mono<PriceBreakdownDto> mockPriceBreakdownPerNight() {

    var summaryDto = SummaryDto.builder()
        .currencyCode("EUR")
        .net(BigDecimal.valueOf(118))
        .gross(BigDecimal.valueOf(98))
        .build();

    return Mono.just(PriceBreakdownDto.builder()
        .summary(summaryDto)
        .build());

  }

  private AvailabilityRequest createAvailabilityRequest(String ratePlanCode) {
    return AvailabilityRequest.builder()
        .hotelId("LONEUS")
        .roomStayStartDate("2022-11-01")
        .roomStayEndDate("2022-11-03")
        .roomTypes(List.of("DB"))
        .adults(List.of(2))
        .children(List.of(0))
        .cotsRequired(List.of(Boolean.TRUE))
        .channel("WEB")
        .subchannel("SUBWEB")
        .language("en")
        .ratePlanCode(ratePlanCode)
        .roomSubstitutions(mockRoomSubstitutions())
        .build();
  }

  private AvailabilityRequest createAvailabilityRequestMultipleDB(String ratePlanCode) {
    return AvailabilityRequest.builder()
        .hotelId("LONEUS")
        .roomStayStartDate("2022-11-01")
        .roomStayEndDate("2022-11-03")
        .roomTypes(List.of("DB"))
        .adults(List.of(2))
        .children(List.of(0))
        .cotsRequired(List.of(Boolean.TRUE))
        .channel("WEB")
        .subchannel("SUBWEB")
        .language("en")
        .ratePlanCode(ratePlanCode)
        .roomSubstitutions(mockRoomSubstitutionsMultipleDB())
        .build();
  }

  private Mono<HotelAvailabilityDetailsDto> mockHotelAvailability(String start, String end) {
    var amountTypeDto = AmountTypeDto.builder()
        .start(start)
        .end(end)
        .effectiveRate(TotalTypeDto.builder()
            .amountBeforeTax(EFFECTIVE_RATE)
            .build())
        .base(TotalTypeDto.builder()
            .amountBeforeTax(BASE_RATE)
            .build())
        .base(TotalTypeDto.builder()
            .amountBeforeTax(NET_RATE)
            .build())
        .build();
    var rates = RatesTypeDto.builder()
        .rate(List.of(amountTypeDto))
        .build();

    var flexRoomRateTypeDto = RoomRateTypeDto.builder()
        .roomType("DOUBLE")
        .ratePlanCode("FLEXRATE")
        .rates(rates)
        .start(start)
        .end(end)
        .build();

    var advancedRoomRateTypeDto = RoomRateTypeDto.builder()
        .roomType("DOUBLE")
        .ratePlanCode("ADVANCED")
        .rates(rates)
        .start(start)
        .end(end)
        .build();

    var roomStaysDto = RoomStayTypeDto.builder()
        .roomRates(new ArrayList<>(List.of(flexRoomRateTypeDto, advancedRoomRateTypeDto)))
        .build();

    var hotelAvailabilityDto = HotelAvailabilityDto.builder()
        .hotelId("LONEUS")
        .ratePlanSet("PBF")
        .roomStays(List.of(roomStaysDto))
        .build();
    return Mono.just(HotelAvailabilityDetailsDto.builder()
        .hotelAvailability(List.of(hotelAvailabilityDto))
        .build());

  }

  private Mono<HotelAvailabilityDetailsDto> mockHotelAvailabilityMultupleRoomTypes(String start,
      String end) {
    var amountTypeDto = AmountTypeDto.builder()
        .start(start)
        .end(end)
        .build();
    var rates = RatesTypeDto.builder()
        .rate(List.of(amountTypeDto))
        .build();

    var flexRoomRateTypeDto = RoomRateTypeDto.builder()
        .roomType("DOUBLE")
        .ratePlanCode("FLEXRATE")
        .rates(rates)
        .start(start)
        .end(end)
        .build();

    var advancedRoomRateTypeDto1 = RoomRateTypeDto.builder()
        .roomType("DOUBLE")
        .ratePlanCode("ADVANCED")
        .rates(rates)
        .start(start)
        .end(end)
        .build();

    var advancedRoomRateTypeDto2 = RoomRateTypeDto.builder()
        .roomType("PPLDBL")
        .ratePlanCode("ADVANCED")
        .rates(rates)
        .start(start)
        .end(end)
        .build();

    var advancedRoomRateTypeDto3 = RoomRateTypeDto.builder()
        .roomType("WETDBL")
        .ratePlanCode("ADVANCED")
        .rates(rates)
        .start(start)
        .end(end)
        .build();

    var roomStaysDto = RoomStayTypeDto.builder()
        .roomRates(new ArrayList<>(List.of(flexRoomRateTypeDto, advancedRoomRateTypeDto1,
            advancedRoomRateTypeDto2, advancedRoomRateTypeDto3)))
        .build();

    var hotelAvailabilityDto = HotelAvailabilityDto.builder()
        .hotelId("LONEUS")
        .ratePlanSet("PBF")
        .roomStays(List.of(roomStaysDto))
        .build();
    return Mono.just(HotelAvailabilityDetailsDto.builder()
        .hotelAvailability(List.of(hotelAvailabilityDto))
        .build());

  }

  private Mono<HotelAvailabilityDetailsDto> mockHotelAvailabilityDB() {
    return mockHotelAvailabilityDBDifferentRates(false);
  }

  private Mono<HotelAvailabilityDetailsDto> mockHotelAvailabilityDBDifferentRates(
      boolean setDifferentRates) {
    var amountTypeDto = AmountTypeDto.builder()
            .start("2022-11-01")
            .end("2022-11-03")
            .build();
    var rates = RatesTypeDto.builder()
            .rate(List.of(amountTypeDto))
            .build();

    var flexRoomRateTypeDto = RoomRateTypeDto.builder()
            .roomType("DOUBLE")
            .ratePlanCode("FLEXRATE")
            .rates(rates)
            .start("2022-11-01")
            .end("2022-11-03")
            .build();

    var advancedRoomRateTypeDto = RoomRateTypeDto.builder()
            .roomType("DOUBLE")
            .ratePlanCode("ADVANCED")
            .rates(rates)
            .start("2022-11-01")
            .end("2022-11-03")
            .build();

    var flexRoomRateTypeDtoDB2 = RoomRateTypeDto.builder()
            .roomType("PPLDBL")
            .ratePlanCode("FLEXRATE")
            .rates(rates)
            .start("2022-11-01")
            .end("2022-11-03")
            .build();

    var advancedRoomRateTypeDtoDB2 = RoomRateTypeDto.builder()
            .roomType("PPLDBL")
            .ratePlanCode("ADVANCED")
            .rates(rates)
            .start("2022-11-01")
            .end("2022-11-03")
            .build();

    var flexRoomRateTypeDtoDB3 = RoomRateTypeDto.builder()
        .roomType("WETDBL")
        .ratePlanCode("FLEXRATE")
        .rates(rates)
        .start("2022-11-01")
        .end("2022-11-03")
        .build();

    var advancedRoomRateTypeDtoDB3 = RoomRateTypeDto.builder()
        .roomType("WETDBL")
        .ratePlanCode("ADVANCED")
        .rates(rates)
        .start("2022-11-01")
        .end("2022-11-03")
        .build();

    var roomRates = new ArrayList<>(List.of(flexRoomRateTypeDto, advancedRoomRateTypeDto,
        advancedRoomRateTypeDtoDB2, flexRoomRateTypeDtoDB3, advancedRoomRateTypeDtoDB3));
    if (!setDifferentRates) {
      roomRates.add(flexRoomRateTypeDtoDB2);
      roomRates.add(flexRoomRateTypeDtoDB3);
    }
    var roomStaysDto = RoomStayTypeDto.builder()
            .roomRates(roomRates)
            .build();

    var hotelAvailabilityDto = HotelAvailabilityDto.builder()
            .hotelId("LONEUS")
            .ratePlanSet("PBF")
            .roomStays(List.of(roomStaysDto))
            .build();
    return Mono.just(HotelAvailabilityDetailsDto.builder()
            .hotelAvailability(List.of(hotelAvailabilityDto))
            .build());

  }

  private Mono<HotelAvailabilityDetailsDto> mockHotelAvailabilityDbAndTwin() {
    var amountTypeDto = AmountTypeDto.builder()
            .start("2022-11-01")
            .end("2022-11-03")
            .build();
    var rates = RatesTypeDto.builder()
            .rate(List.of(amountTypeDto))
            .build();

    var flexRoomRateTypeDtoDB1 = RoomRateTypeDto.builder()
            .roomType("DOUBLE")
            .ratePlanCode("FLEXRATE")
            .rates(rates)
            .start("2022-11-01")
            .end("2022-11-03")
            .build();

    var advancedRoomRateTypeDtoDB1 = RoomRateTypeDto.builder()
            .roomType("DOUBLE")
            .ratePlanCode("ADVANCED")
            .rates(rates)
            .start("2022-11-01")
            .end("2022-11-03")
            .build();

    var flexRoomRateTypeDtoDB2 = RoomRateTypeDto.builder()
            .roomType("PPLDBL")
            .ratePlanCode("FLEXRATE")
            .rates(rates)
            .start("2022-11-01")
            .end("2022-11-03")
            .build();

    var advancedRoomRateTypeDtoDB2 = RoomRateTypeDto.builder()
            .roomType("PPLDBL")
            .ratePlanCode("ADVANCED")
            .rates(rates)
            .start("2022-11-01")
            .end("2022-11-03")
            .build();

    var flexRoomRateTypeDtoTWIN = RoomRateTypeDto.builder()
            .roomType("ZPLDBL")
            .ratePlanCode("FLEXRATE")
            .rates(rates)
            .start("2022-11-01")
            .end("2022-11-03")
            .build();

    var advancedRoomRateTypeDtoTWIN = RoomRateTypeDto.builder()
            .roomType("ZPLDBL")
            .ratePlanCode("ADVANCED")
            .rates(rates)
            .start("2022-11-01")
            .end("2022-11-03")
            .build();

    var roomStaysDto = RoomStayTypeDto.builder()
            .roomRates(new ArrayList<>(List.of(flexRoomRateTypeDtoDB1, advancedRoomRateTypeDtoDB1,
                    flexRoomRateTypeDtoDB2,advancedRoomRateTypeDtoDB2, flexRoomRateTypeDtoTWIN, advancedRoomRateTypeDtoTWIN)))
            .build();

    var hotelAvailabilityDto = HotelAvailabilityDto.builder()
            .hotelId("LONEUS")
            .ratePlanSet("PBF")
            .roomStays(List.of(roomStaysDto))
            .build();
    return Mono.just(HotelAvailabilityDetailsDto.builder()
            .hotelAvailability(List.of(hotelAvailabilityDto))
            .build());

  }

  private Mono<HotelInventoryDto> mockHotelInventoryPPLDBL(Integer roomQuantity) {

    var inventoryCountsTypeDto = InventoryCountsTypeDto.builder()
        .available(Boolean.TRUE)
        .availableCount(roomQuantity)
        .build();

    var roomLevelInventoryDB1 = InventoryLevelCountsListTypeDto.builder()
        .inventoryCounts(List.of(inventoryCountsTypeDto))
        .code("DOUBLE")
        .build();

    var roomLevelInventoryDB2 = InventoryLevelCountsListTypeDto.builder()
        .inventoryCounts(List.of(inventoryCountsTypeDto))
        .code("WETDBL")
        .build();

    var roomLevelInventoryDB3 = InventoryLevelCountsListTypeDto.builder()
        .inventoryCounts(List.of(inventoryCountsTypeDto))
        .code("PPLDBL")
        .build();

    var hotelInventoryTypeDto1 = HotelInventoryTypeDto.builder()
        .houseInventory(List.of(inventoryCountsTypeDto))
        .roomTypeInventories(List.of(roomLevelInventoryDB1, roomLevelInventoryDB2,
            roomLevelInventoryDB3))
        .build();

    return Mono.just(HotelInventoryDto.builder()
        .hotelInventories(List.of(hotelInventoryTypeDto1))
        .build());
  }

  private Mono<HotelInventoryDto> mockHotelInventory(Integer roomQuantity) {

    var inventoryCountsTypeDto = InventoryCountsTypeDto.builder()
        .available(Boolean.TRUE)
        .availableCount(roomQuantity)
        .build();

    var roomLevelInventoryDB1 = InventoryLevelCountsListTypeDto.builder()
        .inventoryCounts(List.of(inventoryCountsTypeDto))
        .code("DOUBLE")
        .build();

    var roomLevelInventoryDB2 = InventoryLevelCountsListTypeDto.builder()
        .inventoryCounts(List.of(inventoryCountsTypeDto))
        .code("WETDBL")
        .build();

    var hotelInventoryTypeDto1 = HotelInventoryTypeDto.builder()
        .houseInventory(List.of(inventoryCountsTypeDto))
        .roomTypeInventories(List.of(roomLevelInventoryDB1, roomLevelInventoryDB2))
        .build();

    return Mono.just(HotelInventoryDto.builder()
        .hotelInventories(List.of(hotelInventoryTypeDto1))
        .build());
  }

  private Mono<HotelInventoryDto> mockHouseInventoryAvailability(boolean isHouseInventoryAvailable1,
      boolean isHouseInventoryAvailable2) {
    var inventoryCountsTypeDto1 = InventoryCountsTypeDto.builder()
        .available(isHouseInventoryAvailable1)
        .availableCount(200)
        .build();
    var inventoryCountsTypeDto2 = InventoryCountsTypeDto.builder()
        .available(isHouseInventoryAvailable2)
        .availableCount(200)
        .build();

    var roomLevelInventoryDB1 = InventoryLevelCountsListTypeDto.builder()
        .inventoryCounts(List.of(inventoryCountsTypeDto1))
        .code("DOUBLE")
        .build();

    var roomLevelInventoryDB2 = InventoryLevelCountsListTypeDto.builder()
        .inventoryCounts(List.of(inventoryCountsTypeDto2))
        .code("WETDBL")
        .build();

    var hotelInventoryTypeDto1 = HotelInventoryTypeDto.builder()
        .houseInventory(List.of(inventoryCountsTypeDto1))
        .roomTypeInventories(List.of(roomLevelInventoryDB1, roomLevelInventoryDB2))
        .build();

    var hotelInventoryTypeDto2 = HotelInventoryTypeDto.builder()
        .houseInventory(List.of(inventoryCountsTypeDto2))
        .roomTypeInventories(List.of(roomLevelInventoryDB1, roomLevelInventoryDB2))
        .build();

    return Mono.just(HotelInventoryDto.builder()
        .hotelInventories(List.of(hotelInventoryTypeDto1, hotelInventoryTypeDto2))
        .build());
  }

  private Mono<HotelInventoryDto> mockHotelInventoryForRoomTypes() {

    var doubleRoomInventoryCountsTypeDto = InventoryCountsTypeDto.builder()
        .available(Boolean.TRUE)
        .availableCount(2)
        .build();
    var wetdblRoomInventoryCountsTypeDto = InventoryCountsTypeDto.builder()
        .available(Boolean.TRUE)
        .availableCount(0)
        .build();

    var roomLevelInventoryDB1 = InventoryLevelCountsListTypeDto.builder()
        .inventoryCounts(List.of(doubleRoomInventoryCountsTypeDto))
        .code("DOUBLE")
        .build();

    var roomLevelInventoryDB2 = InventoryLevelCountsListTypeDto.builder()
        .inventoryCounts(List.of(wetdblRoomInventoryCountsTypeDto))
        .code("WETDBL")
        .build();

    var hotelInventoryTypeDto1 = HotelInventoryTypeDto.builder()
        .houseInventory(List.of(doubleRoomInventoryCountsTypeDto))
        .roomTypeInventories(List.of(roomLevelInventoryDB1, roomLevelInventoryDB2))
        .build();

    return Mono.just(HotelInventoryDto.builder()
        .hotelInventories(List.of(hotelInventoryTypeDto1))
        .build());
  }

  private Mono<HotelInventoryDto> mockHotelInventoryDBAndTwin(Integer roomQuantity) {

    var inventoryCountsTypeDto = InventoryCountsTypeDto.builder()
            .available(Boolean.TRUE)
            .availableCount(roomQuantity)
            .build();

    var roomLevelInventoryDB1 = InventoryLevelCountsListTypeDto.builder()
            .inventoryCounts(List.of(inventoryCountsTypeDto))
            .code("DOUBLE")
            .build();

    var roomLevelInventoryDB2 = InventoryLevelCountsListTypeDto.builder()
            .inventoryCounts(List.of(inventoryCountsTypeDto))
            .code("PPLDBL")
            .build();

    var roomLevelInventoryTWIN = InventoryLevelCountsListTypeDto.builder()
            .inventoryCounts(List.of(inventoryCountsTypeDto))
            .code("ZPLDBL")
            .build();

    var hotelInventoryTypeDto = HotelInventoryTypeDto.builder()
            .houseInventory(List.of(inventoryCountsTypeDto))
            .roomTypeInventories(List.of(roomLevelInventoryDB1, roomLevelInventoryDB2, roomLevelInventoryTWIN))
            .build();

    return Mono.just(HotelInventoryDto.builder()
            .hotelInventories(List.of(hotelInventoryTypeDto))
            .build());
  }

  private RoomTypesResponseDto mockRoomTypes(String roomClass) {
    var roomTypeInfo = RoomTypeInfoDto.builder()
        .roomType("DOUBLE")
        .roomClass(roomClass)
        .numberOfRooms(3)
        .build();

    var roomTypesDto = RoomTypesDto.builder()
        .roomTypeSummary(List.of(roomTypeInfo))
        .build();

    return RoomTypesResponseDto.builder()
        .roomTypesSummary(List.of(roomTypesDto))
        .build();
  }

  private RoomTypesResponseDto mockRoomTypesDBAndTwin() {
    var roomTypeInfoDB1 = RoomTypeInfoDto.builder()
            .roomType("DOUBLE")
            .roomClass("ST")
            .numberOfRooms(1)
            .build();

    var roomTypeInfoDB2 = RoomTypeInfoDto.builder()
            .roomType("PPLDBL")
            .roomClass("PP")
            .numberOfRooms(1)
            .build();

    var roomTypeInfoTWIN = RoomTypeInfoDto.builder()
            .roomType("ZPLDBL")
            .roomClass("ST")
            .numberOfRooms(1)
            .build();

    var roomTypesDto = RoomTypesDto.builder()
            .roomTypeSummary(List.of(roomTypeInfoDB1,roomTypeInfoDB2,roomTypeInfoTWIN))
            .build();

    return RoomTypesResponseDto.builder()
            .roomTypesSummary(List.of(roomTypesDto))
            .build();
  }

  private RoomTypesResponseDto mockRoomTypesMultipleDB() {
    var roomTypeInfoDB1 = RoomTypeInfoDto.builder()
        .roomType("DOUBLE")
        .roomClass("ST")
        .numberOfRooms(1)
        .build();

    var roomTypeInfoDB2 = RoomTypeInfoDto.builder()
        .roomType("PPLDBL")
        .roomClass("PP")
        .numberOfRooms(1)
        .build();

    var roomTypeInfoDB3 = RoomTypeInfoDto.builder()
        .roomType("WETDBL")
        .roomClass("ST")
        .numberOfRooms(1)
        .build();

    var roomTypesDto = RoomTypesDto.builder()
        .roomTypeSummary(List.of(roomTypeInfoDB1,roomTypeInfoDB2,roomTypeInfoDB3))
        .build();

    return RoomTypesResponseDto.builder()
        .roomTypesSummary(List.of(roomTypesDto))
        .build();
  }


  private AvailabilityRequestDto mockAvailabilityRequestDto(String ratePlanCode, String date) {
    return AvailabilityRequestDto.builder()
        .hotelId("LONEUS")
        .roomStayStartDate("2022-11-01")
        .roomStayEndDate(date)
        .roomTypes(List.of("DB"))
        .adults(List.of(2))
        .children(List.of(0))
        .cotsRequired(List.of(Boolean.TRUE))
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .ratePlanCode(ratePlanCode)
        .roomSubstitutions(mockRoomSubstitutionsDtoDB())
        .build();
  }

  private MultiHotelAvailabilityRequestDto mockMultiHotelAvailabilityRequestDto(String channel) {
    return MultiHotelAvailabilityRequestDto.builder()
        .hotelIds(List.of("LONEUS"))
        .roomStayStartDate("2022-11-01")
        .roomStayEndDate("2022-11-03")
        .roomTypes(List.of("DB", "TWIN"))
        .adults(List.of(2))
        .children(List.of(0))
        .cotsRequired(List.of(Boolean.TRUE))
        .channel(channel)
        .subchannel("WEB")
        .language("EN")
        .companyId("1353")
        .ratePlanCodes(List.of("FLEXRATE"))
        .roomSubstitutions(mockRoomSubstitutionsDtoDB())
        .build();
  }

  private MultiHotelAvailabilityRequestDto mockMultiHotelAvailabilityRequestDtoDBAndTwinDistr(String subchannel) {
    return MultiHotelAvailabilityRequestDto.builder()
            .hotelIds(List.of("LONEUS"))
            .roomStayStartDate("2022-11-01")
            .roomStayEndDate("2022-11-03")
            .roomTypes(List.of("DB", "TWIN"))
            .adults(List.of(2))
            .children(List.of(0))
            .cotsRequired(List.of(Boolean.FALSE))
            .channel("DISTR")
            .subchannel(subchannel)
            .language("EN")
            .companyId("1353")
            .ratePlanCodes(List.of("FLEXRATE"))
            .roomSubstitutions(mockRoomSubstitutionsDtoDbAndTWIN())
            .build();
  }

  private RoomSubstitutionRuleResponseDto mockRoomSubstitutionRule() {

    var roomSubstRequestDetailsDto = RoomSubstitutionRequestDetailsDto.builder()
        .adults(2)
        .children(0)
        .roomType("DB")
        .pms("OP").build();

    var roomSubstDto = RoomSubstitutionDto.builder()
        .type("DOUBLE")
        .silent(Boolean.FALSE)
        .specialRequest("DBLE")
        .build();

    return RoomSubstitutionRuleResponseDto.builder()
        .requestDetails(roomSubstRequestDetailsDto)
        .generatedAt(new Date())
        .substitutionList(List.of(roomSubstDto))
        .build();
  }

  private BookingChannelInfoResponseDto mockBookingChannelInfoResponseDto() {
    return BookingChannelInfoResponseDto.builder()
        .sourceId("pms")
        .ratePlanSets(List.of("PBN", "PBF"))
        .generatedAt(LocalDateTime.now())
        .build();

  }

  private List<RoomSubstitutionRuleResponse> mockRoomSubstitutions() {
    return List.of(RoomSubstitutionRuleResponse.builder()
        .requestDetails(
            RoomSubstitutionRequestDetails.builder().roomType("DB").adults(1).children(0)
                .cotRequired(false)
                .build()).substitutionList(mockRoomSubstitutionListDB(List.of("DOUBLE"))).build());
  }

  private List<RoomSubstitutionRuleResponse> mockRoomSubstitutionsMultipleDB() {
    return List.of(RoomSubstitutionRuleResponse.builder()
            .requestDetails(
                RoomSubstitutionRequestDetails.builder().roomType("DB").adults(2).children(0)
                    .cotRequired(false)
                    .build()).substitutionList(mockRoomSubstitutionListDB(List.of("DOUBLE"))).build(),
        RoomSubstitutionRuleResponse.builder()
            .requestDetails(
                RoomSubstitutionRequestDetails.builder().roomType("DB").adults(2).children(0)
                    .cotRequired(false)
                    .build()).substitutionList(mockRoomSubstitutionListDB(List.of("PPLDBL"))).build(),
        RoomSubstitutionRuleResponse.builder()
            .requestDetails(
                RoomSubstitutionRequestDetails.builder().roomType("DB").adults(2).children(0)
                    .cotRequired(false)
                    .build()).substitutionList(mockRoomSubstitutionListDB(List.of("WETDBL"))).build());
  }

  private List<RoomSubstitutionRuleResponse> mockRoomSubstitutionsDBAndTwin() {
    return List.of(RoomSubstitutionRuleResponse.builder()
            .requestDetails(
                    RoomSubstitutionRequestDetails.builder().roomType("DB").adults(2).children(0)
                            .cotRequired(false)
                            .build()).substitutionList(mockRoomSubstitutionListDB(List.of("DOUBLE"))).build(),
            RoomSubstitutionRuleResponse.builder()
                    .requestDetails(
                            RoomSubstitutionRequestDetails.builder().roomType("DB").adults(2).children(0)
                                    .cotRequired(false)
                                    .build()).substitutionList(mockRoomSubstitutionListDB(List.of("PPLDBL"))).build(),
            RoomSubstitutionRuleResponse.builder()
                    .requestDetails(
                            RoomSubstitutionRequestDetails.builder().roomType("TWIN").adults(2).children(0)
                                    .cotRequired(false)
                                    .build()).substitutionList(mockRoomSubstitutionListTWIN(List.of("ZPLDBL"))).build());
  }

  private List<RoomSubstitution> mockRoomSubstitutionListDB(List<String> roomTypes) {
    return roomTypes.stream()
            .map(type -> RoomSubstitution.builder().type(type).specialRequest("DBLE").build()).toList();

  }

  private List<RoomSubstitution> mockRoomSubstitutionListTWIN(List<String> roomTypes) {
    return roomTypes.stream()
            .map(type -> RoomSubstitution.builder().type(type).specialRequest("TWIN").build()).toList();

  }


  private List<RoomSubstitutionRuleResponseDto> mockRoomSubstitutionsDtoDB() {
    return new ArrayList<>(List.of(RoomSubstitutionRuleResponseDto.builder().requestDetails(
            RoomSubstitutionRequestDetailsDto.builder().roomType("DB").adults(2).children(0)
                .cotRequired(false)
                .build())
        .substitutionList(mockRoomSubstitutionDtoList("DB",
            List.of("DOUBLE", "PPLDBL", "WETDBL"))).build()));
  }

  private List<RoomSubstitutionRuleResponseDto> mockRoomSubstitutionsDtoDbAndTWIN() {
    return new ArrayList<>(List.of(RoomSubstitutionRuleResponseDto.builder().requestDetails(
                    RoomSubstitutionRequestDetailsDto.builder().roomType("DB").adults(1).children(0)
                            .cotRequired(false)
                            .build()).substitutionList(mockRoomSubstitutionDtoList("DB", List.of("DOUBLE", "PPLDBL"))).build(),
            RoomSubstitutionRuleResponseDto.builder().requestDetails(
                    RoomSubstitutionRequestDetailsDto.builder().roomType("TWIN").adults(1).children(0)
                            .cotRequired(false)
                            .build()).substitutionList(mockRoomSubstitutionDtoList("TWIN", List.of("ZPLDBL"))).build()));
  }

  private List<RoomSubstitutionDto> mockRoomSubstitutionDtoList(String roomType, List<String> roomTypes) {
    if ("DB".equals(roomType)) {
      return roomTypes.stream()
              .map(type -> RoomSubstitutionDto.builder().type(type).silent(Boolean.FALSE)
                      .specialRequest("DBLE").build())
              .toList();
    } else if ("TWIN".equals(roomType)) {
      return roomTypes.stream()
              .map(type -> RoomSubstitutionDto.builder().type(type).silent(Boolean.FALSE)
                      .specialRequest("TW2S").build())
              .toList();
    }
    return Collections.emptyList();
  }

  private List<AvailabilityByIdsResultV2> createAvailabilityByIdsV2ResultList() {
    return List.of(
            AvailabilityByIdsResultV2.builder()
                    .hotelAvailability(List.of(
                            AvailabilityResultV2.builder()
                                    .hotelId(HOTEL_ID_1)
                                    .roomStays(createRoomStayList_1())
                                    .build(),
                            AvailabilityResultV2.builder()
                                    .hotelId(HOTEL_ID_2)
                                    .roomStays(createRoomStayList_1())
                                    .build()))
                    .build(),
            AvailabilityByIdsResultV2.builder()
                    .hotelAvailability(List.of(
                            AvailabilityResultV2.builder()
                                    .hotelId(HOTEL_ID_1)
                                    .roomStays(createRoomStayList_2("DB"))
                                    .build(),
                            AvailabilityResultV2.builder()
                                    .hotelId(HOTEL_ID_2)
                                    .roomStays(createRoomStayList_2("DB"))
                                    .build()))
                    .build(),
            AvailabilityByIdsResultV2.builder()
                    .hotelAvailability(List.of(
                            AvailabilityResultV2.builder()
                                    .hotelId(HOTEL_ID_1)
                                    .roomStays(createRoomStayList_2("FAM"))
                                    .build(),
                            AvailabilityResultV2.builder()
                                    .hotelId(HOTEL_ID_2)
                                    .roomStays(createRoomStayList_2("FAM"))
                                    .build()))
                    .build(),
            AvailabilityByIdsResultV2.builder()
                    .hotelAvailability(List.of(
                            AvailabilityResultV2.builder()
                                    .hotelId(HOTEL_ID_1)
                                    .roomStays(createRoomStayList_2("FAM"))
                                    .build(),
                            AvailabilityResultV2.builder()
                                    .hotelId(HOTEL_ID_2)
                                    .roomStays(createRoomStayList_2("FAM"))
                                    .build()))
                    .build());
  }

  private List<RoomStay> createRoomStayList_1() {
    return List.of(
            RoomStay.builder().roomClass("ST")
                    .roomTypes(List.of(
                            RoomTypeV2.builder().tag("DB").roomRates(createRoomRates())
                                    .roomType("DOUBLE")
                                    .build(),
                            RoomTypeV2.builder().tag("DB").roomRates(createRoomRates())
                                    .roomType("DOUBLE")
                                    .build(),
                            RoomTypeV2.builder().tag("SB").roomRates(createRoomRates())
                                    .roomType("DOUBLE")
                                    .build()))
                    .build());
  }

  private List<RoomStay> createRoomStayList_2(String roomType) {
    return List.of(
            RoomStay.builder().roomClass("ST")
                    .roomTypes(List.of(
                            RoomTypeV2.builder().tag(roomType).roomRates(createRoomRates())
                                    .build()))
                    .build());
  }

  private List<RoomRate> createRoomRates() {
    return List.of(
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
                    .build());
  }

  private AvailabilityByIdsSearchCriteriaV2 createAvailabilityByIdsSearchRequestV2(String channel) {
    return AvailabilityByIdsSearchCriteriaV2.builder()
            .hotelIds(List.of(HOTEL_ID_1, HOTEL_ID_2))
            .arrivalDate(ARRIVAL_DATE)
            .departureDate(DEPARTURE_DATE)
            .bookingChannel(BookingChannel.builder().channel(channel).build())
            .rooms(List.of(
                    Room.builder().numberOfRooms(1).adults(1).children(0).tag("DB").build()))
            .rates(RateV2.builder().corporateRates(List.of(createCorporateRate())).ratePlanCodes(List.of("FLEXRATE")).build())
            .build();
  }

  private Mono<MultiRoomRateAvailabilityResponseType> mockHotelAvailabilityV2() {
    return Mono.just(new MultiRoomRateAvailabilityResponseType());
  }

  private List<AvailabilityByIdsSearchCriteriaV2Dto> mockMultiHotelAvailabilityRequestV2DtoList() {
    List<String> hotelIds = List.of(HOTEL_ID_1, HOTEL_ID_2);
    return List.of(
            AvailabilityByIdsSearchCriteriaV2Dto.builder()
                    .hotelIds(List.of(HOTEL_ID_1, HOTEL_ID_2))
                    .arrivalDate(LocalDate.of(2025, 3, 1))
                    .departureDate(LocalDate.of(2025, 3, 2))
                    .rooms(List.of(
                            RoomByIdsDto.builder().numberOfRooms(1).adults(1).children(0).tag("DB").build(),
                            RoomByIdsDto.builder().numberOfRooms(1).adults(1).children(0).tag("DB").build(),
                            RoomByIdsDto.builder().numberOfRooms(1).adults(1).children(0).tag("SB").build()))
                    .rates(RateDto.builder().corporateRates(CorporateRateDto.builder()
                        .corporateId("15017452")
                        .ratePlanSets(List.of("NEG"))
                        .build()).ratePlanCodes(List.of("FLEXRATE")).build())
                    .build(),
            AvailabilityByIdsSearchCriteriaV2Dto.builder()
                    .hotelIds(hotelIds)
                    .arrivalDate(LocalDate.of(2025, 3, 1))
                    .departureDate(LocalDate.of(2025, 3, 2))
                    .rooms(List.of(
                            RoomByIdsDto.builder().numberOfRooms(1).adults(2).children(0).tag("DB").build()))
                    .rates(RateDto.builder().ratePlanCodes(List.of("FLEXRATE")).build())
                    .build(),
            AvailabilityByIdsSearchCriteriaV2Dto.builder()
                    .hotelIds(hotelIds)
                    .arrivalDate(LocalDate.of(2025, 3, 1))
                    .departureDate(LocalDate.of(2025, 3, 2))
                    .rooms(List.of(
                            RoomByIdsDto.builder().numberOfRooms(1).adults(2).children(1).tag("FAM").build()))
                    .rates(RateDto.builder().ratePlanCodes(List.of("FLEXRATE")).build())
                    .build(),
            AvailabilityByIdsSearchCriteriaV2Dto.builder()
                    .hotelIds(hotelIds)
                    .arrivalDate(LocalDate.of(2025, 3, 1))
                    .departureDate(LocalDate.of(2025, 3, 2))
                    .rooms(List.of(
                            RoomByIdsDto.builder().numberOfRooms(1).adults(2).children(2).tag("FAM").build()))
                    .rates(RateDto.builder().ratePlanCodes(List.of("FLEXRATE")).build())
                    .build()
    );
  }

  private List<AvailabilityByIdsSearchCriteriaV2Dto> mockMultiHotelAvailabilityRequestV2DtoListForBatchRequest(List<String> hotelIds) {
    return List.of(
        AvailabilityByIdsSearchCriteriaV2Dto.builder()
            .hotelIds(hotelIds)
            .arrivalDate(LocalDate.of(2025, 3, 1))
            .departureDate(LocalDate.of(2025, 3, 2))
            .rooms(List.of(
                RoomByIdsDto.builder().numberOfRooms(1).adults(1).children(0).tag("DB").build(),
                RoomByIdsDto.builder().numberOfRooms(1).adults(1).children(0).tag("DB").build(),
                RoomByIdsDto.builder().numberOfRooms(1).adults(1).children(0).tag("SB").build()))
            .rates(RateDto.builder().corporateRates(CorporateRateDto.builder()
                .corporateId("15017452")
                .ratePlanSets(List.of("NEG"))
                .build()).ratePlanCodes(List.of("FLEXRATE")).build())
            .build()
    );
  }

  private AvailabilityByIdsSearchRequest createAvailabilityByIdsSearchRequest() {
    return AvailabilityByIdsSearchRequest.builder()
            .negotiatedRateDisplaySets(List.of("BMD"))
            .adults(List.of(1))
            .hotelIds(List.of("LONEUS"))
            .arrivalDate("2023-06-01")
            .departureDate("2023-06-03")
            .channel("DISTR")
            .globalCompanyId("123")
            .cotsRequired(List.of(false))
            .children(List.of(0))
            .language("en")
            .numberOfRooms(1)
            .subchannel("WEB")
            .ratePlanCodes(List.of("FLEXRATE"))
            .globalCompanyId("15017452")
            .roomTypes(List.of("DB"))
            .roomSubstitutions(mockRoomSubstitutions())
            .build();
  }

  private AvailabilityByIdsSearchRequest createAvailabilityByIdsSearchRequestDBAndTwinDISTR(String subchannel) {
    return AvailabilityByIdsSearchRequest.builder()
            .negotiatedRateDisplaySets(List.of("BMD"))
            .adults(List.of(2))
            .hotelIds(List.of("LONEUS"))
            .arrivalDate("2023-06-01")
            .departureDate("2023-06-03")
            .channel("DISTR")
            .globalCompanyId("123")
            .cotsRequired(List.of(false))
            .children(List.of(0))
            .language("en")
            .numberOfRooms(1)
            .subchannel(subchannel)
            .ratePlanCodes(List.of("FLEXRATE"))
            .globalCompanyId("15017452")
            .roomTypes(List.of("DB", "TWIN"))
            .roomSubstitutions(mockRoomSubstitutionsDBAndTwin())
            .build();
  }

  private RatePlansSummary mockRatePlanSummary() {
    var ratePlansSummary = new RatePlansSummary();
    var ratePlanShortInfoList = new RatePlansSummaryRatePlanShortInfoList();
    var ratePlanShortInfo = new RatePlanShortInfoType();
    var ratePlanClassification = new RatePlanClassificationsType();
    ratePlanClassification.setDisplaySet("BMD");
    ratePlanShortInfo.setHotelId("LONEUS");
    ratePlanShortInfo.setRatePlanCode("FLEXRATE");
    ratePlanShortInfo.setClassifications(ratePlanClassification);
    ratePlanShortInfoList.setRatePlanShortInfo(List.of(ratePlanShortInfo));

    ratePlansSummary.setRatePlanShortInfoList(ratePlanShortInfoList);

    return ratePlansSummary;
  }

  private Company mockCompanyProfile() {
    var company = new Company();
    var uniqueId = new UniqueIDType();
    uniqueId.setId("123");
    uniqueId.setType("Profile");
    company.setCompanyIdList(
            List.of(uniqueId)
    );

    return company;
  }
  private ItemInventoryResponseDto createItemInventoryResponse() {
    return ItemInventoryResponseDto.builder()
        .itemsInventory(Collections.singletonList(ItemInventoryDto.builder().inventories(List.of(
            InventoryAvailabilityDto.builder().build())).build()))
        .build();
  }

  private AvailabilityByIdsSearchRequest getAvailabilityByIdsSearchRequest() {
    return AvailabilityByIdsSearchRequest.builder()
        .negotiatedRateDisplaySets(List.of("BMD"))
        .adults(List.of(1))
        .hotelIds(List.of("LONEUS"))
        .arrivalDate("2023-02-01")
        .departureDate("2023-06-03")
        .channel("DISTR")
        .globalCompanyId("123")
        .cotsRequired(List.of(false))
        .children(List.of(0))
        .language("en")
        .numberOfRooms(1)
        .subchannel("WEB")
        .ratePlanCodes(List.of("FLEXRATE"))
        .roomTypes(List.of("DB"))
        .roomSubstitutions(mockRoomSubstitutions())
        .build();
  }

  private RestrictionsByDateRangeResult mockRestrictionsByDateRangeResult() {

    List<RestrictionSets> restrictionSets = new ArrayList<>(List.of(
        RestrictionSets.builder()
            .start("2025-07-01")
            .end("2025-08-03")
            .restrictionControl(RestrictionControl.builder().ratePlanCategory("A").build())
            .restrictionStatus(RestrictionStatus.builder().code("CLOSED").build()).build(),
        RestrictionSets.builder()
            .start("2025-08-04")
            .end("2025-09-05")
            .restrictionControl(RestrictionControl.builder().ratePlanCategory("B").build())
            .restrictionStatus(RestrictionStatus.builder().code("MinimumLengthOfStay").unit(2).build()).build()
    ));

    return RestrictionsByDateRangeResult.builder()
        .restrictionsByDateRange(RestrictionsByDateRangeParent.builder()
            .restrictionsByDateRange(RestrictionsByDateRange.builder()
                .restrictionSets(restrictionSets).build()).build())
        .build();
  }

  private SearchPropertyRoomStayType mockResponseType(boolean hasMinimumRate) {
    SearchPropertyRoomStayType roomStay = new SearchPropertyRoomStayType();
    RoomTagType tagType = new RoomTagType();
    tagType.setRoomTypes(List.of("DB"));

    OfferTotalType totalA = new OfferTotalType();
    totalA.setAmountAfterTax(new BigDecimal(50));
    totalA.setCurrencyCode("EUR");

    PropertySearchPropertyInfo infoA = new PropertySearchPropertyInfo();
    infoA.setHotelCode(HOTEL_ID_1);
    roomStay.setPropertyInfo(infoA);
    if (hasMinimumRate) {
      roomStay.setMinimumRate(totalA);
    }
    roomStay.setRoomTags(List.of(tagType));
    return roomStay;
  }

  private CorporateRate createCorporateRate() {
    return CorporateRate.builder()
        .corporateId("15017452")
        .ratePlanSets(List.of("NEG"))
        .build();
  }

  // =========================================================================
  // retryWithCombinedRequest
  // Both per-room split calls return the same room type (WETDBL); inventory
  // detects only 1 unit → false no-availability → retry with combined request.
  // =========================================================================

  // --- happy-path: conflict detected, retry succeeds ----------------------

  @Test
  void retryWithCombinedRequest__WhenBothSplitCallsReturnSameRoomTypeAndInventoryIsTight_ShouldTriggerRetryAndReturnAvailability() {
    // Arrange
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency()).thenReturn(1);

    // mapper returns: split-dto-room1, split-dto-room2, then combined-dto on 3rd call
    when(availabilityByIdsRequestMapper.toRequestV2Dto(any(AvailabilityByIdsSearchCriteriaV2.class)))
        .thenReturn(splitDtoRoom("DB1"), splitDtoRoom("DB2"), combinedDtoTwoRooms());

    // both split calls come back with WETDBL (same room type → inventory conflict)
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(any()))
        .thenReturn(mockHotelAvailabilityV2());
    when(availabilityByIdsRequestMapper.toResultV2Model(any()))
        .thenReturn(
            splitResponseSingleRoom("DB1", "WETDBL"),   // split call 1
            splitResponseSingleRoom("DB2", "WETDBL"),   // split call 2
            combinedResponseTwoRooms() // retry combined call
        );

    // only 1 WETDBL in stock → triggers inventory conflict
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
        .thenReturn(lowInventoryMono(1, "DOUBLE", 5));

    // Act
    var result = hotelAvailabilityOutPort.getHotelAvailabilityByIdsV2(twoRoomDistrSearchCriteria());

    // Assert — availability returned from the combined call
    assertNotNull(result);
    assertNotNull(result.getHotelAvailability());
    assertFalse(result.getHotelAvailability().isEmpty());
    assertEquals(HOTEL_ID_1, result.getHotelAvailability().getFirst().getHotelId());

    var roomStays = result.getHotelAvailability().getFirst().getRoomStays();
    assertFalse(roomStays.isEmpty());
    var roomTypes = roomStays.getFirst().getRoomTypes();
    assertEquals(2, roomTypes.size(),
        "Combined OPERA response should contain 2 distinct room types");

    // verify 3 calls: 2 split + 1 combined
    verify(ohipAvailabilityClient, times(3)).getHotelAvailabilityByIdsRequestV2(any());
  }

  @Test
  void retryWithCombinedRequest__WhenCorporateRateRequest_ShouldSetGlobalCompanyIdOnRoomRates() {
    // fetchAvailabilityRaw skips mapRequestToResponse, which normally sets globalCompanyId.
    // applyGlobalCompanyId must restore it in the combined retry path.
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency()).thenReturn(1);

    var combinedDtoWithCorpRate = AvailabilityByIdsSearchCriteriaV2Dto.builder()
        .hotelIds(List.of(HOTEL_ID_1)).arrivalDate(ARRIVAL_DATE).departureDate(DEPARTURE_DATE)
        .rooms(List.of(
            RoomByIdsDto.builder().tag("DB1").adults(1).children(0).numberOfRooms(1)
                .roomTypes(List.of("WETDBL", "DOUBLE")).build(),
            RoomByIdsDto.builder().tag("DB2").adults(1).children(0).numberOfRooms(1)
                .roomTypes(List.of("WETDBL", "DOUBLE")).build()
        ))
        .rates(RateDto.builder()
            .corporateRates(CorporateRateDto.builder().corporateId("CORP123").build())
            .build())
        .build();

    when(availabilityByIdsRequestMapper.toRequestV2Dto(any(AvailabilityByIdsSearchCriteriaV2.class)))
        .thenReturn(splitDtoRoom("DB1"), splitDtoRoom("DB2"), combinedDtoWithCorpRate);
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(any()))
        .thenReturn(mockHotelAvailabilityV2());
    // both split calls return WETDBL (conflict), combined returns WETDBL + DOUBLE
    when(availabilityByIdsRequestMapper.toResultV2Model(any()))
        .thenReturn(
            splitResponseSingleRoom("DB1", "WETDBL"),
            splitResponseSingleRoom("DB2", "WETDBL"),
            combinedResponseTwoRooms()
        );
    // 1 WETDBL → inventory conflict triggers retry
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
        .thenReturn(lowInventoryMono(1, "DOUBLE", 5));

    var criteriaWithCorpRate = twoRoomDistrSearchCriteria().toBuilder()
        .rates(RateV2.builder()
            .corporateRates(List.of(CorporateRate.builder().corporateId("CORP123").build()))
            .ratePlanCodes(Collections.emptyList())
            .build())
        .build();

    // Act
    var result = hotelAvailabilityOutPort.getHotelAvailabilityByIdsV2(criteriaWithCorpRate);

    // Assert — every room rate in the combined response must carry globalCompanyId
    assertNotNull(result.getHotelAvailability());
    var allRoomRates = result.getHotelAvailability().getFirst().getRoomStays().getFirst()
        .getRoomTypes().stream().flatMap(rt -> rt.getRoomRates().stream()).toList();
    assertFalse(allRoomRates.isEmpty(), "Combined retry must return room rates");
    allRoomRates.forEach(roomRate ->
        assertEquals("CORP123", roomRate.getGlobalCompanyId(),
            "globalCompanyId must be propagated to all room rates in the combined retry response"));
  }

  @Test
  void retryWithCombinedRequest__ShouldEnrichOccupancyViaPhase1ExactRoomTypeMatch() {
    // Arrange — room DB1 has candidate ["WETDBL"], room DB2 has candidate ["DOUBLE"]
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency()).thenReturn(1);

    var splitDto1 = AvailabilityByIdsSearchCriteriaV2Dto.builder()
        .hotelIds(List.of(HOTEL_ID_1)).arrivalDate(ARRIVAL_DATE).departureDate(DEPARTURE_DATE)
        .rooms(List.of(RoomByIdsDto.builder().tag("DB1").adults(1).children(0).numberOfRooms(1)
            .roomTypes(List.of("WETDBL")).build()))
        .build();
    var splitDto2 = AvailabilityByIdsSearchCriteriaV2Dto.builder()
        .hotelIds(List.of(HOTEL_ID_1)).arrivalDate(ARRIVAL_DATE).departureDate(DEPARTURE_DATE)
        .rooms(List.of(RoomByIdsDto.builder().tag("DB2").adults(2).children(0).numberOfRooms(1)
            .roomTypes(List.of("DOUBLE")).build()))
        .build();
    var combinedDto = AvailabilityByIdsSearchCriteriaV2Dto.builder()
        .hotelIds(List.of(HOTEL_ID_1)).arrivalDate(ARRIVAL_DATE).departureDate(DEPARTURE_DATE)
        .rooms(List.of(
            RoomByIdsDto.builder().tag("DB1").adults(1).children(0).numberOfRooms(1).roomTypes(List.of("WETDBL")).build(),
            RoomByIdsDto.builder().tag("DB2").adults(2).children(0).numberOfRooms(1).roomTypes(List.of("DOUBLE")).build()
        ))
        .build();

    when(availabilityByIdsRequestMapper.toRequestV2Dto(any(AvailabilityByIdsSearchCriteriaV2.class)))
        .thenReturn(splitDto1, splitDto2, combinedDto);

    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(any()))
        .thenReturn(mockHotelAvailabilityV2());
    when(availabilityByIdsRequestMapper.toResultV2Model(any()))
        .thenReturn(
            splitResponseSingleRoom("DB1", "WETDBL"),
            splitResponseSingleRoom("DB2", "WETDBL"), // conflict: DB2 also got WETDBL
            combinedResponseTwoRooms()
        );

    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
        .thenReturn(lowInventoryMono(1, "DOUBLE", 5));

    // Act
    var result = hotelAvailabilityOutPort.getHotelAvailabilityByIdsV2(twoRoomDistrSearchCriteriaWithDifferentAdults());

    // Assert — enrichCombinedResponseWithOccupancy must have matched WETDBL→DB1 and DOUBLE→DB2
    assertNotNull(result.getHotelAvailability());
    var roomTypes = result.getHotelAvailability().getFirst().getRoomStays().getFirst().getRoomTypes();
    assertEquals(2, roomTypes.size());

    var wetdbl = roomTypes.stream().filter(r -> "WETDBL".equals(r.getRoomType())).findFirst();
    var dbl = roomTypes.stream().filter(r -> "DOUBLE".equals(r.getRoomType())).findFirst();
    assertTrue(wetdbl.isPresent(), "WETDBL must be present after enrichment");
    assertTrue(dbl.isPresent(), "DOUBLE must be present after enrichment");

    assertEquals("DB1", wetdbl.get().getTag(), "WETDBL should be enriched with DB1 tag (phase-1 exact match)");
    assertEquals("1", wetdbl.get().getAdults());
    assertEquals("DB2", dbl.get().getTag(), "DOUBLE should be enriched with DB2 tag (phase-1 exact match)");
    assertEquals("2", dbl.get().getAdults());
  }

  @Test
  void retryWithCombinedRequest__ShouldEnrichOccupancyViaPhase2PositionalFallback_WhenRoomTypeNotInCandidateList() {
    // OPERA returned PREMIUM (not in any candidate list) — phase-2 positional fallback must assign DB2's occupancy.
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency()).thenReturn(1);

    var splitDto1 = AvailabilityByIdsSearchCriteriaV2Dto.builder()
        .hotelIds(List.of(HOTEL_ID_1)).arrivalDate(ARRIVAL_DATE).departureDate(DEPARTURE_DATE)
        .rooms(List.of(RoomByIdsDto.builder().tag("DB1").adults(1).children(0).numberOfRooms(1)
            .roomTypes(List.of("WETDBL")).build()))
        .build();
    var splitDto2 = AvailabilityByIdsSearchCriteriaV2Dto.builder()
        .hotelIds(List.of(HOTEL_ID_1)).arrivalDate(ARRIVAL_DATE).departureDate(DEPARTURE_DATE)
        .rooms(List.of(RoomByIdsDto.builder().tag("DB2").adults(2).children(1).numberOfRooms(1)
            .roomTypes(List.of("WETDBL")).build()))
        .build();
    // combined DTO — PREMIUM is not in either room's candidate list
    var combinedDto = AvailabilityByIdsSearchCriteriaV2Dto.builder()
        .hotelIds(List.of(HOTEL_ID_1)).arrivalDate(ARRIVAL_DATE).departureDate(DEPARTURE_DATE)
        .rooms(List.of(
            RoomByIdsDto.builder().tag("DB1").adults(1).children(0).numberOfRooms(1).roomTypes(List.of("WETDBL")).build(),
            RoomByIdsDto.builder().tag("DB2").adults(2).children(1).numberOfRooms(1).roomTypes(List.of("WETDBL")).build()
        ))
        .build();

    when(availabilityByIdsRequestMapper.toRequestV2Dto(any(AvailabilityByIdsSearchCriteriaV2.class)))
        .thenReturn(splitDto1, splitDto2, combinedDto);
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(any()))
        .thenReturn(mockHotelAvailabilityV2());

    // combined response has WETDBL (phase-1 matches DB1) and PREMIUM (not in any list → phase-2)
    var combinedResponseWithUnknownType = AvailabilityByIdsResultV2.builder()
        .hotelAvailability(List.of(AvailabilityResultV2.builder()
            .hotelId(HOTEL_ID_1)
            .roomStays(List.of(RoomStay.builder().roomClass("ST").roomTypes(List.of(
                RoomTypeV2.builder().roomType("WETDBL").roomRates(createRoomRates()).build(),
                RoomTypeV2.builder().roomType("PREMIUM").roomRates(createRoomRates()).build()
            )).build()))
            .build()))
        .build();

    when(availabilityByIdsRequestMapper.toResultV2Model(any()))
        .thenReturn(
            splitResponseSingleRoom("DB1", "WETDBL"),
            splitResponseSingleRoom("DB2", "WETDBL"),
            combinedResponseWithUnknownType
        );
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
        .thenReturn(lowInventoryMono(1, "PREMIUM", 3));

    // Act
    var result = hotelAvailabilityOutPort.getHotelAvailabilityByIdsV2(twoRoomDistrSearchCriteriaWithDifferentAdults());

    // Assert — phase-2 must have applied DB2 occupancy to PREMIUM
    assertNotNull(result.getHotelAvailability());
    var roomTypes = result.getHotelAvailability().getFirst().getRoomStays().getFirst().getRoomTypes();
    assertEquals(2, roomTypes.size());

    var premium = roomTypes.stream().filter(r -> "PREMIUM".equals(r.getRoomType())).findFirst();
    assertTrue(premium.isPresent(), "PREMIUM room type must be present in result");
    assertEquals("DB2", premium.get().getTag(),
        "PREMIUM should have received DB2 occupancy via phase-2 positional fallback");
    assertEquals("2", premium.get().getAdults());
    assertEquals("1", premium.get().getChildren());
  }

  // --- combined call also fails -------------------------------------------

  @Test
  void retryWithCombinedRequest__WhenCombinedCallAlsoReturnsNoAvailability_ShouldReturnEmptyResult() {
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency()).thenReturn(1);

    when(availabilityByIdsRequestMapper.toRequestV2Dto(any(AvailabilityByIdsSearchCriteriaV2.class)))
        .thenReturn(splitDtoRoom("DB1"), splitDtoRoom("DB2"), combinedDtoTwoRooms());
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(any()))
        .thenReturn(mockHotelAvailabilityV2());
    when(availabilityByIdsRequestMapper.toResultV2Model(any()))
        .thenReturn(
            splitResponseSingleRoom("DB1", "WETDBL"),
            splitResponseSingleRoom("DB2", "WETDBL"),
            // combined call: OPERA also returns no availability
            AvailabilityByIdsResultV2.builder().hotelAvailability(Collections.emptyList()).build()
        );
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
        .thenReturn(lowInventoryMono(1, "DOUBLE", 5));

    // Act
    var result = hotelAvailabilityOutPort.getHotelAvailabilityByIdsV2(twoRoomDistrSearchCriteria());

    // Assert
    assertNotNull(result);
    assertTrue(result.getHotelAvailability() == null || result.getHotelAvailability().isEmpty(),
        "Should be no-availability when OPERA combined call also returns nothing");
    verify(ohipAvailabilityClient, times(3)).getHotelAvailabilityByIdsRequestV2(any());
  }

  // --- retry must NOT be triggered ----------------------------------------

  @Test
  void retryWithCombinedRequest__WhenInventorySufficient_ShouldNotTriggerRetry() {
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency()).thenReturn(1);

    when(availabilityByIdsRequestMapper.toRequestV2Dto(any(AvailabilityByIdsSearchCriteriaV2.class)))
        .thenReturn(splitDtoRoom("DB1"), splitDtoRoom("DB2"));
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(any()))
        .thenReturn(mockHotelAvailabilityV2());
    // both split calls return different room types (no conflict)
    when(availabilityByIdsRequestMapper.toResultV2Model(any()))
        .thenReturn(
            splitResponseSingleRoom("DB1", "WETDBL"),
            splitResponseSingleRoom("DB2", "DOUBLE")
        );
    // 5 WETDBL available — count of 1 requested ≤ 5 → no conflict
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
        .thenReturn(lowInventoryMono(5, "DOUBLE", 5));

    // Act
    var result = hotelAvailabilityOutPort.getHotelAvailabilityByIdsV2(twoRoomDistrSearchCriteria());

    // Assert — only 2 calls (no combined retry)
    assertNotNull(result);
    verify(ohipAvailabilityClient, times(2)).getHotelAvailabilityByIdsRequestV2(any());
  }

  @Test
  void retryWithCombinedRequest__WhenNonDistrChannel_ShouldNeverTriggerRetry() {
    // Non-DISTR channel → rooms grouped by occupancy → 1 OPERA call, no retry.
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency()).thenReturn(1);

    when(availabilityByIdsRequestMapper.toRequestV2Dto(any(AvailabilityByIdsSearchCriteriaV2.class)))
        .thenReturn(splitDtoRoom("DB1"));
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(any()))
        .thenReturn(mockHotelAvailabilityV2());
    when(availabilityByIdsRequestMapper.toResultV2Model(any()))
        .thenReturn(splitResponseSingleRoom("DB1", "WETDBL"));
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
        .thenReturn(lowInventoryMono(1, "DOUBLE", 5));

    // Act — PI channel, not DISTR
    var criteria = twoRoomDistrSearchCriteria().toBuilder()
        .bookingChannel(BookingChannel.builder().channel("PI").build())
        .build();
    hotelAvailabilityOutPort.getHotelAvailabilityByIdsV2(criteria);

    // Assert — 1 OPERA call (grouped by occupancy), no combined retry call
    verify(ohipAvailabilityClient, times(1)).getHotelAvailabilityByIdsRequestV2(any());
  }

  // --- guard: parallel calls report no availability ----------------------

  @Test
  void retryWithCombinedRequest__WhenOneParallelCallHasNoAvailability_ShouldReturnEmptyWithoutRetry() {
    // Any split call with no availability triggers early return before inventory check or retry.
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency()).thenReturn(1);

    when(availabilityByIdsRequestMapper.toRequestV2Dto(any(AvailabilityByIdsSearchCriteriaV2.class)))
        .thenReturn(splitDtoRoom("DB1"), splitDtoRoom("DB2"));
    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(any()))
        .thenReturn(mockHotelAvailabilityV2());

    // second split call returns empty → isAnyResponseWithoutAvailability fires before inventory check
    when(availabilityByIdsRequestMapper.toResultV2Model(any()))
        .thenReturn(
            splitResponseSingleRoom("DB1", "WETDBL"),
            AvailabilityByIdsResultV2.builder().hotelAvailability(Collections.emptyList()).build()
        );

    // Act
    var result = hotelAvailabilityOutPort.getHotelAvailabilityByIdsV2(twoRoomDistrSearchCriteria());

    // Assert — empty result, no inventory call, no retry
    assertNotNull(result);
    assertTrue(result.getHotelAvailability() == null || result.getHotelAvailability().isEmpty());
    verify(ohipAvailabilityClient, times(2)).getHotelAvailabilityByIdsRequestV2(any());
    verify(ohipAvailabilityClient, times(0)).getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any());
  }

  @Test
  void retryWithCombinedRequest__WhenOneBatchHasEmptyAvailability_ShouldNotFailFastAndReturnAvailableHotels() {
    // Combined retry spans 2 batches (11 hotelIds > BATCH_SIZE=10).
    // Batch 1 returns HOTEL_ID_1 available; batch 2 returns empty hotelAvailability.
    // The old guard (isAnyResponseWithoutAvailability) would treat batch 2's empty list
    // as a hard failure and return noAvailability. The new guard only fails on null
    // hotelAvailability or a missing response; mergeResponses drops unavailable hotels naturally.
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(availabilityOhipProperties.getMaxAvailabilityConcurrency()).thenReturn(1);

    // Calls 1 & 2: split DTOs (one room each); call 3: combined DTO with 11 hotelIds → 2 batches.
    when(availabilityByIdsRequestMapper.toRequestV2Dto(any(AvailabilityByIdsSearchCriteriaV2.class)))
        .thenReturn(splitDtoRoom("DB1"), splitDtoRoom("DB2"), combinedDtoWithElevenHotels());

    when(ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(any()))
        .thenReturn(mockHotelAvailabilityV2());

    // Responses: split × 2 (conflict), batch1 = HOTEL_ID_1 available, batch2 = empty (no available hotels).
    var emptyBatchResponse = AvailabilityByIdsResultV2.builder()
        .hotelAvailability(Collections.emptyList()).build();
    when(availabilityByIdsRequestMapper.toResultV2Model(any()))
        .thenReturn(
            splitResponseSingleRoom("DB1", "WETDBL"),  // split call 1
            splitResponseSingleRoom("DB2", "WETDBL"),  // split call 2
            combinedResponseTwoRooms(),                 // combined batch 1 (hotels 1-10)
            emptyBatchResponse                          // combined batch 2 (hotel 11 — none available)
        );

    // 1 WETDBL in stock → inventory conflict triggers retry.
    when(ohipAvailabilityClient.getHotelInventory(anyString(), anyString(), anyString(), anyInt(), any()))
        .thenReturn(lowInventoryMono(1, "DOUBLE", 5));

    // Act
    var result = hotelAvailabilityOutPort.getHotelAvailabilityByIdsV2(twoRoomDistrSearchCriteria());

    // Assert — HOTEL_ID_1 from batch 1 is returned; empty batch 2 does not kill the result.
    assertNotNull(result);
    assertNotNull(result.getHotelAvailability());
    assertFalse(result.getHotelAvailability().isEmpty(),
        "Hotels from batch 1 must be returned even though batch 2 had empty availability");
    assertEquals(HOTEL_ID_1, result.getHotelAvailability().getFirst().getHotelId());

    // 2 split calls + 2 combined batch calls
    verify(ohipAvailabilityClient, times(4)).getHotelAvailabilityByIdsRequestV2(any());
  }

  // =========================================================================
  // Fixtures for retry tests
  // =========================================================================

  /** 2-room DISTR request, no corporate rates, single hotel. */
  private AvailabilityByIdsSearchCriteriaV2 twoRoomDistrSearchCriteria() {
    return AvailabilityByIdsSearchCriteriaV2.builder()
        .hotelIds(List.of(HOTEL_ID_1))
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .bookingChannel(BookingChannel.builder().channel("DISTR").build())
        .rooms(List.of(
            Room.builder().numberOfRooms(1).adults(1).children(0).tag("DB1").build(),
            Room.builder().numberOfRooms(1).adults(1).children(0).tag("DB2").build()
        ))
        .rates(RateV2.builder().corporateRates(Collections.emptyList()).ratePlanCodes(Collections.emptyList()).build())
        .build();
  }

  /** Same as {@link #twoRoomDistrSearchCriteria()} but the two rooms have different adult counts,
   * used for enrichment assertion tests. */
  private AvailabilityByIdsSearchCriteriaV2 twoRoomDistrSearchCriteriaWithDifferentAdults() {
    return AvailabilityByIdsSearchCriteriaV2.builder()
        .hotelIds(List.of(HOTEL_ID_1))
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .bookingChannel(BookingChannel.builder().channel("DISTR").build())
        .rooms(List.of(
            Room.builder().numberOfRooms(1).adults(1).children(0).tag("DB1").build(),
            Room.builder().numberOfRooms(1).adults(2).children(1).tag("DB2").build()
        ))
        .rates(RateV2.builder().corporateRates(Collections.emptyList()).ratePlanCodes(Collections.emptyList()).build())
        .build();
  }

  /** DTO representing a per-room split request for the given tag. */
  private AvailabilityByIdsSearchCriteriaV2Dto splitDtoRoom(String tag) {
    return AvailabilityByIdsSearchCriteriaV2Dto.builder()
        .hotelIds(List.of(HOTEL_ID_1))
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .rooms(List.of(RoomByIdsDto.builder()
            .tag(tag).adults(1).children(0).numberOfRooms(1)
            .roomTypes(List.of("WETDBL", "DOUBLE"))
            .build()))
        .build();
  }

  /** DTO representing the combined retry request containing both rooms. */
  private AvailabilityByIdsSearchCriteriaV2Dto combinedDtoTwoRooms() {
    return AvailabilityByIdsSearchCriteriaV2Dto.builder()
        .hotelIds(List.of(HOTEL_ID_1))
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .rooms(List.of(
            RoomByIdsDto.builder().tag("DB1").adults(1).children(0).numberOfRooms(1)
                .roomTypes(List.of("WETDBL", "DOUBLE")).build(),
            RoomByIdsDto.builder().tag("DB2").adults(1).children(0).numberOfRooms(1)
                .roomTypes(List.of("WETDBL", "DOUBLE")).build()
        ))
        .build();
  }

  /**
   * Combined retry DTO with 11 hotelIds (> BATCH_SIZE=10) — forces 2 batch Monos so that
   * the retry produces one batch with availability and one with an empty hotelAvailability list.
   */
  private AvailabilityByIdsSearchCriteriaV2Dto combinedDtoWithElevenHotels() {
    var hotelIds = new java.util.ArrayList<String>();
    hotelIds.add(HOTEL_ID_1);
    for (int i = 2; i <= 11; i++) {
      hotelIds.add(String.format("HOTEL%03d", i));
    }
    return AvailabilityByIdsSearchCriteriaV2Dto.builder()
        .hotelIds(Collections.unmodifiableList(hotelIds))
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .rooms(List.of(
            RoomByIdsDto.builder().tag("DB1").adults(1).children(0).numberOfRooms(1)
                .roomTypes(List.of("WETDBL", "DOUBLE")).build(),
            RoomByIdsDto.builder().tag("DB2").adults(1).children(0).numberOfRooms(1)
                .roomTypes(List.of("WETDBL", "DOUBLE")).build()
        ))
        .build();
  }

  /**
   * Response for a single per-room split call: Opera returned {@code roomType} with {@code tag}
   * already set (mirrors what {@code mapRequestToResponse} sets).
   */
  private AvailabilityByIdsResultV2 splitResponseSingleRoom(String tag, String roomType) {
    return AvailabilityByIdsResultV2.builder()
        .hotelAvailability(List.of(AvailabilityResultV2.builder()
            .hotelId(HotelAvailabilityOutPortImplTest.HOTEL_ID_1)
            .roomStays(List.of(RoomStay.builder()
                .roomClass("ST")
                .roomTypes(List.of(RoomTypeV2.builder()
                    .tag(tag).roomType(roomType)
                    .adults("1").children("0").numberOfRooms("1")
                    .roomRates(createRoomRates())
                    .build()))
                .build()))
            .build()))
        .build();
  }

  /**
   * Combined retry response with two different room types; no tag/adults/children set —
   * {@code enrichCombinedResponseWithOccupancy} fills those in.
   */
  private AvailabilityByIdsResultV2 combinedResponseTwoRooms() {
    return AvailabilityByIdsResultV2.builder()
        .hotelAvailability(List.of(AvailabilityResultV2.builder()
            .hotelId(HotelAvailabilityOutPortImplTest.HOTEL_ID_1)
            .roomStays(List.of(RoomStay.builder()
                .roomClass("ST")
                .roomTypes(List.of(
                    RoomTypeV2.builder().roomType("WETDBL").roomRates(createRoomRates()).build(),
                    RoomTypeV2.builder().roomType("DOUBLE").roomRates(createRoomRates()).build()
                ))
                .build()))
            .build()))
        .build();
  }

  /**
   * Inventory with two room types; WETDBL has {@code lowRoomType} has {@code lowCount}
   * units to simulate low stock.
   */
  private Mono<HotelInventoryDto> lowInventoryMono(
      int lowCount, String normalRoomType, int normalCount) {
    var lowCounts = InventoryCountsTypeDto.builder().available(true).availableCount(lowCount).build();
    var normalCounts = InventoryCountsTypeDto.builder().available(true).availableCount(normalCount).build();
    var houseInventory = InventoryCountsTypeDto.builder().available(true)
        .availableCount(lowCount + normalCount).build();

    var lowLevel = InventoryLevelCountsListTypeDto.builder()
        .code("WETDBL").inventoryCounts(List.of(lowCounts)).build();
    var normalLevel = InventoryLevelCountsListTypeDto.builder()
        .code(normalRoomType).inventoryCounts(List.of(normalCounts)).build();

    return Mono.just(HotelInventoryDto.builder()
        .hotelInventories(List.of(HotelInventoryTypeDto.builder()
            .houseInventory(List.of(houseInventory))
            .roomTypeInventories(List.of(lowLevel, normalLevel))
            .build()))
        .build());
  }
}
