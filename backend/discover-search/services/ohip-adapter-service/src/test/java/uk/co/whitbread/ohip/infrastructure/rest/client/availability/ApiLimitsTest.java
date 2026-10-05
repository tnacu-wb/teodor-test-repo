package uk.co.whitbread.ohip.infrastructure.rest.client.availability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.OfferTotalType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.PropertySearchPropertyInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.RoomTagType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.SearchPropertyResponseType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.SearchPropertyRoomStayType;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.availability.in.ItemInventoryRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeRoomInfoCriteria;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.exceptions.HotelAvailabilityException;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.AmountTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelAvailabilityDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelAvailabilityDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.ItemInventoryRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.PriceBreakdownDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.RatesTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.RoomRateTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.RoomStayTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.SummaryDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.TotalTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.AvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.InventoryAvailabilityDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.ItemInventoryDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.ItemInventoryResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.MultiHotelAvailabilityRequestV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.ApiLimitsService;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.OhipAvailabilityClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.properties.AvailabilityOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.RoomSubstitutionDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.RoomSubstitutionRequestDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.RoomSubstitutionRuleResponseDto;

@ExtendWith(MockitoExtension.class)
class ApiLimitsTest {

  @InjectMocks
  private ApiLimitsService apiLimitsService;
  @Mock
  private AvailabilityOhipProperties availabilityOhipProperties;
  @Mock
  private OhipAvailabilityClient ohipAvailabilityClient;
  
  private Executor apiLimitsExecutor = Runnable::run;

  @BeforeEach
  void init() {
    apiLimitsService = new ApiLimitsService(availabilityOhipProperties, ohipAvailabilityClient,
        apiLimitsExecutor);
  }

  @SneakyThrows
  @Test
  void getItemInventoryResponses_ShouldReturnOk() {
    // Arrange
    ItemInventoryResponseDto expected = createItemInventoryResponse();
    when(ohipAvailabilityClient.getItemsInventory(any())).thenReturn(mockResponse());

    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();

    when(ohipAvailabilityClient.getItemsInventory(
        any(ItemInventoryRequestDto.class)))
        .thenAnswer(invocation -> {
          threadNames.add(Thread.currentThread().getName());
          return mockResponse();
        });

    var request = getRequest();
    var result = apiLimitsService.getItemInventoryResponses(request);

    //Assert
    assertNotNull(result);
    assertEquals(expected, result);
  }

  @SneakyThrows
  @Test
  void getHotelAvailabilityNoResponses_ShouldReturnOk() {
    // Arrange
    var expected = HotelAvailabilityDetailsDto.builder()
        .hotelAvailability(List.of(HotelAvailabilityDto.builder()
            .hotelId("HOTELID")
            .roomStays(
                List.of(RoomStayTypeDto.builder().roomRates(Collections.EMPTY_LIST).build()))
            .build()))
        .build();

    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(3);
    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();

    AtomicInteger i = new AtomicInteger(-1);

    when(ohipAvailabilityClient.getHotelAvailabilityRequest(
        any(AvailabilityRequestDto.class)))
        .thenAnswer(invocation -> {
          i.getAndIncrement();
          threadNames.add(Thread.currentThread().getName());
          if (1 == i.intValue()) {
            return Mono.just(HotelAvailabilityDetailsDto.builder().build());
          } else {
            return mockAvailabilityResponse();
          }
        });

    var request = AvailabilityRequestDto.builder()
        .hotelId("HOTELID")
        .roomStayStartDate("2025-05-01")
        .roomStayEndDate("2025-05-04")
        .ratePlanSet("planSet")
        .ratePlanCode("planCode")
        .roomTypes(List.of("roomType"))
        .promotionCode("promoCode")
        .build();
    var result = apiLimitsService.getHotelAvailabilityResponses(request);

    //Assert
    assertNotNull(result);
    assertEquals(expected, result);
  }

  @SneakyThrows
  @Test
  void getHotelAvailabilityResponses_ShouldReturnOk() {
    // Arrange
    var expected = createHotelAvailabilityResponse();

    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(3);
    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();

    when(ohipAvailabilityClient.getHotelAvailabilityRequest(
        any(AvailabilityRequestDto.class)))
        .thenAnswer(invocation -> {
          threadNames.add(Thread.currentThread().getName());
          return mockAvailabilityResponse();
        });

    var request = AvailabilityRequestDto.builder()
        .hotelId("HOTELID")
        .roomStayStartDate("2025-05-01")
        .roomStayEndDate("2025-05-04")
        .ratePlanSet("planSet")
        .ratePlanCode("planCode")
        .roomTypes(List.of("roomType"))
        .promotionCode("promoCode")
        .build();
    var result = apiLimitsService.getHotelAvailabilityResponses(request);

    //Assert
    assertNotNull(result);
    assertEquals(expected, result);
  }

  @SneakyThrows
  @Test
  void getItemInventoryResponses_ShouldThrowException() {
    // Arrange
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();
    when(ohipAvailabilityClient.getItemsInventory(
        any(ItemInventoryRequestDto.class)))
        .thenAnswer(invocation -> {
          threadNames.add(Thread.currentThread().getName());
          throw new HotelReservationException(ErrorCode.OHIP_HOTEL_ITEMS_INVENTORY_EXCEPTION,
              "error");
        });

    var request = getRequest();
    //Act
    var exception = Assertions.assertThrows(HotelReservationException.class,
        () -> apiLimitsService.getItemInventoryResponses(request));

    //Assert
    assertEquals("error", exception.getMessage());
  }

  @SneakyThrows
  @Test
  void getHotelAvailabilityResponses_ShouldThrowException() {
    // Arrange
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(3);
    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();
    when(ohipAvailabilityClient.getHotelAvailabilityRequest(
        any(AvailabilityRequestDto.class)))
        .thenAnswer(invocation -> {
          threadNames.add(Thread.currentThread().getName());
          throw new HotelAvailabilityException(ErrorCode.OHIP_RETRIEVE_AVAILABILITY_EXCEPTION,
              "error");
        });

    var request = AvailabilityRequestDto.builder()
        .hotelId("HOTELID")
        .roomStayStartDate("2025-05-01")
        .roomStayEndDate("2025-05-04")
        .ratePlanSet("planSet")
        .ratePlanCode("planCode")
        .roomTypes(List.of("roomType"))
        .promotionCode("promoCode")
        .build();

    //Act
    var exception = Assertions.assertThrows(HotelAvailabilityException.class,
        () -> apiLimitsService.getHotelAvailabilityResponses(request));

    //Assert
    assertEquals("error", exception.getMessage());
  }

  @SneakyThrows
  @Test
  void getRatesInfo_PriceBreakdownPerNightInterval_ShouldReturnOK() {
    //Arrange
    when(availabilityOhipProperties.getMaxRequestedDaysRateInfo()).thenReturn(4);

    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();
    List<String> listStart = List.of("2022-11-01", "2022-11-04");
    List<String> listEnd = List.of("2022-11-04", "2022-11-05");
    AtomicInteger i = new AtomicInteger(-1);

    when(ohipAvailabilityClient.getPriceBreakdownPerNight(
        any(AvailabilityRequestDto.class)))
        .thenAnswer(invocation -> {
          i.getAndIncrement();
          threadNames.add(Thread.currentThread().getName());
          return mockPriceBreakdownDto(listStart.get(i.get()), listEnd.get(i.get()));
        });
    var request = mockAvailabilityRequestDto("FLEXRATE", "2022-11-05");

    //Act
    var result = apiLimitsService.getRateInfoResponse(request);

    //Assert
    assertNotNull(result);
    assertEquals("EUR", result.getSummary().getCurrencyCode());
    assertEquals(236, result.getSummary().getNet().intValue());
    assertEquals(196, result.getSummary().getGross().intValue());
    assertEquals("2022-11-01", result.getSummary().getStart());
    assertEquals("2022-11-05", result.getSummary().getEnd());
  }


  @SneakyThrows
  @Test
  void getRatesInfo_PriceBreakdownPerNightInterval_ShouldThrowException() {
    // Arrange
    when(availabilityOhipProperties.getMaxRequestedDaysRateInfo()).thenReturn(4);
    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();
    when(ohipAvailabilityClient.getPriceBreakdownPerNight(
        any(AvailabilityRequestDto.class)))
        .thenAnswer(invocation -> {
          threadNames.add(Thread.currentThread().getName());
          throw new HotelAvailabilityException(ErrorCode.OHIP_PRICE_BREAKDOWN_PERNIGHT_EXCEPTION,
              "error");
        });

    var request = mockAvailabilityRequestDto("FLEXRATE", "2022-11-05");

    //Act
    var exception = Assertions.assertThrows(HotelAvailabilityException.class,
        () -> apiLimitsService.getRateInfoResponse(request));

    //Assert
    assertEquals("error", exception.getMessage());
  }

  @SneakyThrows
  @Test
  void getRatesInfo_getRateCodePricing_ShouldReturnOK() {
    //Arrange
    when(availabilityOhipProperties.getMaxRequestedDaysRateInfo()).thenReturn(4);

    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();
    List<String> listStart = List.of("2022-11-01", "2022-11-04");
    List<String> listEnd = List.of("2022-11-04", "2022-11-05");
    AtomicInteger i = new AtomicInteger(-1);

    when(ohipAvailabilityClient.getRateCodePricing(
        any(RateCodeCriteria.class), any(RateCodeRoomInfoCriteria.class)))
        .thenAnswer(invocation -> {
          i.getAndIncrement();
          threadNames.add(Thread.currentThread().getName());
          return mockPriceBreakdownDto(listStart.get(i.get()), listEnd.get(i.get())).block();
        });

    //Act
    var result = apiLimitsService.getRateInfoResponse(createRateCodeCriteria(),
        createRateCodeRoomInfoCriteria());

    //Assert
    assertEquals("EUR", result.getSummary().getCurrencyCode());
    assertEquals(236, result.getSummary().getNet().intValue());
    assertEquals(196, result.getSummary().getGross().intValue());
    assertEquals("2022-11-01", result.getSummary().getStart());
    assertEquals("2022-11-05", result.getSummary().getEnd());
  }

  @SneakyThrows
  @Test
  void getMultiHotelAvailabilityRequestV2_ShouldReturnOK() {
    //Arrange
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(4);

    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();
    when(ohipAvailabilityClient.getMultiHotelAvailabilityRequestV2(
        any(MultiHotelAvailabilityRequestV2Dto.class)))
        .thenAnswer(invocation -> {
          threadNames.add(Thread.currentThread().getName());
          return mockSearchPropertyResponseType(BigDecimal.valueOf(100), BigDecimal.valueOf(200));
        });

    var expected = mockSearchPropertyResponseType(BigDecimal.valueOf(200),
        BigDecimal.valueOf(400)).block();

    MultiHotelAvailabilityRequestV2Dto requestDto = mockMultiHotelAvailabilityDto
        (LocalDate.of(2011, 11, 1), LocalDate.of(2011, 11, 5)).block();
    //Act
    var result = apiLimitsService.getMultiHotelAvailabilityRequestV2(requestDto);

    //Assert
    assertEquals(expected.getLimit(), result.getLimit());
    assertEquals(expected.getOffset(), result.getOffset());
    assertEquals(expected.getHasMore(), result.getHasMore());
    assertEquals(expected.getRoomStays(), result.getRoomStays());
  }

  @SneakyThrows
  @Test
  void getMultiHotelAvailabilityRequestV2NoResults_ShouldReturnOK() {
    //Arrange
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(4);
    AtomicInteger i = new AtomicInteger(-1);
    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();
    when(ohipAvailabilityClient.getMultiHotelAvailabilityRequestV2(
        any(MultiHotelAvailabilityRequestV2Dto.class)))
        .thenAnswer(invocation -> {
          i.getAndIncrement();
          threadNames.add(Thread.currentThread().getName());
          return mockSearchPropertyNoResponseType("A" + i.get(), BigDecimal.valueOf(200));
        });

    var expected = new SearchPropertyResponseType();
    List<SearchPropertyRoomStayType> list = Collections.EMPTY_LIST;
    expected.setRoomStays(list);
    expected.setLimit(1);
    expected.setOffset(0);
    expected.setHasMore(false);

    MultiHotelAvailabilityRequestV2Dto requestDto = mockMultiHotelAvailabilityDto
        (LocalDate.of(2011, 11, 1), LocalDate.of(2011, 11, 5)).block();
    //Act
    var result = apiLimitsService.getMultiHotelAvailabilityRequestV2(requestDto);

    //Assert
    assertEquals(expected.getLimit(), result.getLimit());
    assertEquals(expected.getOffset(), result.getOffset());
    assertEquals(expected.getHasMore(), result.getHasMore());
    assertEquals(expected.getRoomStays(), result.getRoomStays());
  }

  @SneakyThrows
  @Test
  void getMultiHotelAvailabilityRequestV2_ShouldThrowException() {
    // Arrange
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(4);
    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();
    when(ohipAvailabilityClient.getMultiHotelAvailabilityRequestV2(
        any(MultiHotelAvailabilityRequestV2Dto.class)))
        .thenAnswer(invocation -> {
          threadNames.add(Thread.currentThread().getName());
          throw new HotelReservationException(ErrorCode.OHIP_MULTIHOTEL_AVAILABILITY_EXCEPTION,
              "error");
        });

    MultiHotelAvailabilityRequestV2Dto requestDto = mockMultiHotelAvailabilityDto
        (LocalDate.of(2011, 11, 1), LocalDate.of(2011, 11, 5)).block();

    //Act
    var exception = Assertions.assertThrows(HotelReservationException.class,
        () -> apiLimitsService.getMultiHotelAvailabilityRequestV2(requestDto));

    //Assert
    assertEquals("error", exception.getMessage());
  }

  private Mono<MultiHotelAvailabilityRequestV2Dto> mockMultiHotelAvailabilityDto(
      LocalDate startDate, LocalDate endDate) {
    return Mono.just(MultiHotelAvailabilityRequestV2Dto.builder()
        .arrivalDate(startDate)
        .departureDate(endDate)
        .accountId("123")
        .limit(1)
        .offset(0)
        .includePublicRates(Boolean.TRUE)
        .hotelIds(List.of("A", "B"))
        .sortBy("DISTANCE")
        .rooms(List.of(RoomDto.builder()
            .adults(1)
            .children(0)
            .roomTypes(List.of("DB"))
            .numberOfUnits(1)
            .build())
        )
        .build());
  }

  private SearchPropertyRoomStayType mockResponseType(String hotelCode, BigDecimal value) {
    SearchPropertyRoomStayType roomStay = new SearchPropertyRoomStayType();
    RoomTagType tagType = new RoomTagType();
    tagType.setRoomTypes(List.of("DB"));

    OfferTotalType totalA = new OfferTotalType();
    totalA.setAmountAfterTax(value);
    totalA.setCurrencyCode("EUR");

    PropertySearchPropertyInfo infoA = new PropertySearchPropertyInfo();
    infoA.setHotelCode(hotelCode);
    roomStay.setPropertyInfo(infoA);
    roomStay.setMinimumRate(totalA);
    roomStay.setRoomTags(List.of(tagType));
    return roomStay;
  }

  private Mono<SearchPropertyResponseType> mockSearchPropertyNoResponseType(String hotelCode,
      BigDecimal value) {
    SearchPropertyResponseType response = new SearchPropertyResponseType();
    SearchPropertyRoomStayType roomStayA = mockResponseType(hotelCode, value);
    List<SearchPropertyRoomStayType> list = List.of(roomStayA);
    response.setRoomStays(list);
    response.setLimit(1);
    response.setOffset(0);
    response.setHasMore(false);
    return Mono.just(response);
  }

  private Mono<SearchPropertyResponseType> mockSearchPropertyResponseType(BigDecimal valueA,
      BigDecimal valueB) {
    SearchPropertyResponseType response = new SearchPropertyResponseType();
    SearchPropertyRoomStayType roomStayA = mockResponseType("A", valueA);
    SearchPropertyRoomStayType roomStayB = mockResponseType("B", valueB);
    List<SearchPropertyRoomStayType> list = List.of(roomStayA, roomStayB);
    response.setRoomStays(list);
    response.setLimit(1);
    response.setOffset(0);
    response.setHasMore(false);
    return Mono.just(response);
  }

  @SneakyThrows
  @Test
  void getRatesInfo_getRateCodePricing_ShouldThrowException() {
    // Arrange
    when(availabilityOhipProperties.getMaxRequestedDaysRateInfo()).thenReturn(4);
    CopyOnWriteArraySet<String> threadNames = new CopyOnWriteArraySet<>();
    when(ohipAvailabilityClient.getRateCodePricing(
        any(RateCodeCriteria.class), any(RateCodeRoomInfoCriteria.class)))
        .thenAnswer(invocation -> {
          threadNames.add(Thread.currentThread().getName());
          throw new HotelAvailabilityException(ErrorCode.OHIP_RATECODE_INFO_EXCEPTION,
              "error");
        });

    var rateCode = createRateCodeCriteria();
    var rateCodeRoomInfo = createRateCodeRoomInfoCriteria();

    //Act
    var exception = Assertions.assertThrows(HotelAvailabilityException.class,
        () -> apiLimitsService.getRateInfoResponse(rateCode, rateCodeRoomInfo));

    //Assert
    assertEquals("error", exception.getMessage());
  }

  private static ItemInventoryRequest getRequest() {
    return ItemInventoryRequest.builder()
        .hotelId("LONEUS")
        .startDate("2023-02-01")
        .endDate("2023-06-14")
        .build();
  }

  private ItemInventoryResponseDto mockResponse() {
    return ItemInventoryResponseDto.builder().itemsInventory(List.of(
        ItemInventoryDto.builder().inventories(List.of(InventoryAvailabilityDto.builder().build()))
            .name("name")
            .code("code")
            .build())).build();
  }

  private Mono<HotelAvailabilityDetailsDto> mockAvailabilityResponse() {
    var total = TotalTypeDto.builder()
        .amountBeforeTax(BigDecimal.valueOf(100))
        .amountAfterTax(BigDecimal.valueOf(200))
        .currencyCode("DE").build();
    return Mono.just(HotelAvailabilityDetailsDto.builder().hotelAvailability(List.of(
        HotelAvailabilityDto.builder().roomStays(List.of(
                RoomStayTypeDto.builder()
                    .roomRates(List.of(buildRoomRateTypeDto(RatesTypeDto.builder()
                        .rate(List.of(
                            AmountTypeDto.builder().total(total).build()))
                        .build(), 100, 200)))
                    .build()))
            .closed(true)
            .hasMore(true)
            .redemption(true)
            .ratePlanSet("ratePlanSet")
            .hotelId("HOTELID")
            .build())).build());
  }

  private ItemInventoryResponseDto createItemInventoryResponse() {
    return ItemInventoryResponseDto.builder()
        .itemsInventory(Collections.singletonList(ItemInventoryDto.builder()
            .name("name")
            .code("code")
            .inventories(List.of(
                InventoryAvailabilityDto.builder().build(),
                InventoryAvailabilityDto.builder().build()))
            .build()))
        .build();
  }

  private HotelAvailabilityDetailsDto createHotelAvailabilityResponse() {
    var total = TotalTypeDto.builder()
        .amountBeforeTax(BigDecimal.valueOf(100))
        .amountAfterTax(BigDecimal.valueOf(200))
        .currencyCode("DE").build();
    var availability = HotelAvailabilityDto.builder().roomStays(List.of(
            RoomStayTypeDto.builder()
                .roomRates(List.of(buildRoomRateTypeDto(RatesTypeDto.builder()
                    .rate(List.of(
                        AmountTypeDto.builder().total(total).build(),
                        AmountTypeDto.builder().total(total).build()))
                    .build(), 200, 400)))
                .build()))
        .closed(true)
        .hasMore(true)
        .redemption(true)
        .ratePlanSet("ratePlanSet")
        .hotelId("HOTELID")
        .build();
    return HotelAvailabilityDetailsDto.builder()
        .hotelAvailability(List.of(availability))
        .build();
  }

  private RoomRateTypeDto buildRoomRateTypeDto(RatesTypeDto rates, Integer amountBefore,
      Integer amountAfter) {
    return RoomRateTypeDto.builder()
        .start("2025-05-01")
        .end("2025-05-01")
        .ratePlanSet("planSet")
        .ratePlanCode("planCode")
        .suppressRate(true)
        .roomType("roomType")
        .marketCode("marketCode")
        .promotionCode("promoCode")
        .rates(rates)
        .total(TotalTypeDto.builder()
            .amountAfterTax(BigDecimal.valueOf(amountAfter))
            .amountBeforeTax(BigDecimal.valueOf(amountBefore))
            .build())
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

  private List<RoomSubstitutionRuleResponseDto> mockRoomSubstitutionsDtoDB() {
    return new ArrayList<>(List.of(RoomSubstitutionRuleResponseDto.builder().requestDetails(
            RoomSubstitutionRequestDetailsDto.builder().roomType("DB").adults(1).children(0)
                .cotRequired(false)
                .build())
        .substitutionList(mockRoomSubstitutionDtoList("DB",
            List.of("DOUBLE", "PPLDBL", "WETDBL"))).build()));
  }

  private List<RoomSubstitutionDto> mockRoomSubstitutionDtoList(String roomType,
      List<String> roomTypes) {
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

  private RateCodeCriteria createRateCodeCriteria() {
    return RateCodeCriteria.builder()
        .hotelId("LONEUS")
        .arrivalDate("2022-11-01")
        .departureDate("2022-11-05")
        .ratePlanCode("FLEXRATE")
        .build();
  }

  private RateCodeRoomInfoCriteria createRateCodeRoomInfoCriteria() {
    return RateCodeRoomInfoCriteria.builder()
        .adultsNo(2)
        .childrenNo(0)
        .roomType("DOUBLE")
        .build();
  }


  private Mono<PriceBreakdownDto> mockPriceBreakdownDto(String startDate, String endDate) {

    var summaryDto = SummaryDto.builder()
        .start(startDate)
        .end(endDate)
        .currencyCode("EUR")
        .net(BigDecimal.valueOf(118))
        .gross(BigDecimal.valueOf(98))
        .build();

    return Mono.just(PriceBreakdownDto.builder()
        .summary(summaryDto)
        .build());

  }

  @SneakyThrows
  @Test
  void getItemInventoryResponses_SingleInterval_ShouldExecuteSynchronously() {
    // Arrange - Date range within maxRequestedDays limit (30 days < 90)
    ItemInventoryRequest request = ItemInventoryRequest.builder()
        .hotelId("LONEUS")
        .startDate("2023-02-01")
        .endDate("2023-03-03")
        .build();
    
    ItemInventoryResponseDto expected = mockResponse();
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    when(ohipAvailabilityClient.getItemsInventory(any(ItemInventoryRequestDto.class)))
        .thenReturn(expected);

    // Act
    var result = apiLimitsService.getItemInventoryResponses(request);

    // Assert
    assertNotNull(result);
    assertEquals(expected, result);
    assertEquals(expected.getItemsInventory().size(), result.getItemsInventory().size());
    // Verify client was called exactly once (synchronous, no parallel calls)
    org.mockito.Mockito.verify(ohipAvailabilityClient, org.mockito.Mockito.times(1))
        .getItemsInventory(any(ItemInventoryRequestDto.class));
  }

  @SneakyThrows
  @Test
  void getHotelAvailabilityResponses_SingleInterval_ShouldExecuteSynchronously() {
    // Arrange - Date range within maxRequestedDays-1 limit (7 days < 89)
    AvailabilityRequestDto request = AvailabilityRequestDto.builder()
        .hotelId("LONEUS")
        .roomStayStartDate("2023-05-01")
        .roomStayEndDate("2023-05-08")
        .roomTypes(List.of("DB"))
        .adults(List.of(2))
        .children(List.of(0))
        .cotsRequired(List.of(Boolean.FALSE))
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .ratePlanCode("FLEXRATE")
        .roomSubstitutions(mockRoomSubstitutionsDtoDB())
        .build();

    HotelAvailabilityDetailsDto expected = mockAvailabilityResponse().block();
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    when(ohipAvailabilityClient.getHotelAvailabilityRequest(any(AvailabilityRequestDto.class)))
        .thenReturn(Mono.just(expected));

    // Act
    var result = apiLimitsService.getHotelAvailabilityResponses(request);

    // Assert
    assertNotNull(result);
    assertNotNull(result.getHotelAvailability());
    // The mock returns "HOTELID" hardcoded, so we check for that
    assertEquals("HOTELID", result.getHotelAvailability().get(0).getHotelId());
    // Verify client was called exactly once (synchronous)
    org.mockito.Mockito.verify(ohipAvailabilityClient, org.mockito.Mockito.times(1))
        .getHotelAvailabilityRequest(any(AvailabilityRequestDto.class));
  }

  @SneakyThrows
  @Test
  void getRateInfoResponse_RateCodeCriteria_SingleInterval_ShouldExecuteSynchronously() {
    // Arrange - Date range within maxRequestedDaysRateInfo-1 limit (14 days < 21)
    RateCodeCriteria rateCodeCriteria = RateCodeCriteria.builder()
        .hotelId("LONEUS")
        .arrivalDate("2022-11-01")
        .departureDate("2022-11-15")
        .ratePlanCode("FLEXRATE")
        .build();
    
    RateCodeRoomInfoCriteria roomInfoCriteria = RateCodeRoomInfoCriteria.builder()
        .adultsNo(2)
        .childrenNo(0)
        .roomType("DOUBLE")
        .build();

    PriceBreakdownDto expected = mockPriceBreakdownDto("2022-11-01", "2022-11-15").block();
    when(availabilityOhipProperties.getMaxRequestedDaysRateInfo()).thenReturn(21);
    when(ohipAvailabilityClient.getRateCodePricing(
        any(RateCodeCriteria.class), any(RateCodeRoomInfoCriteria.class)))
        .thenReturn(expected);

    // Act
    var result = apiLimitsService.getRateInfoResponse(rateCodeCriteria, roomInfoCriteria);

    // Assert
    assertNotNull(result);
    assertNotNull(result.getSummary());
    assertEquals("EUR", result.getSummary().getCurrencyCode());
    assertEquals("2022-11-01", result.getSummary().getStart());
    assertEquals("2022-11-15", result.getSummary().getEnd());
    // Verify client was called exactly once (synchronous)
    org.mockito.Mockito.verify(ohipAvailabilityClient, org.mockito.Mockito.times(1))
        .getRateCodePricing(any(RateCodeCriteria.class), any(RateCodeRoomInfoCriteria.class));
  }

  @SneakyThrows
  @Test
  void getRateInfoResponse_AvailabilityRequest_SingleInterval_ShouldExecuteSynchronously() {
    // Arrange - Date range within maxRequestedDaysRateInfo-1 limit (5 days < 21)
    AvailabilityRequestDto request = AvailabilityRequestDto.builder()
        .hotelId("LONEUS")
        .roomStayStartDate("2022-11-01")
        .roomStayEndDate("2022-11-06")
        .roomTypes(List.of("DB"))
        .adults(List.of(2))
        .children(List.of(0))
        .cotsRequired(List.of(Boolean.TRUE))
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .ratePlanCode("FLEXRATE")
        .roomSubstitutions(mockRoomSubstitutionsDtoDB())
        .build();

    PriceBreakdownDto expected = mockPriceBreakdownDto("2022-11-01", "2022-11-06").block();
    when(availabilityOhipProperties.getMaxRequestedDaysRateInfo()).thenReturn(21);
    when(ohipAvailabilityClient.getPriceBreakdownPerNight(any(AvailabilityRequestDto.class)))
        .thenReturn(Mono.just(expected));

    // Act
    var result = apiLimitsService.getRateInfoResponse(request);

    // Assert
    assertNotNull(result);
    assertNotNull(result.getSummary());
    assertEquals("EUR", result.getSummary().getCurrencyCode());
    // Verify client was called exactly once (synchronous, blocking call)
    org.mockito.Mockito.verify(ohipAvailabilityClient, org.mockito.Mockito.times(1))
        .getPriceBreakdownPerNight(any(AvailabilityRequestDto.class));
  }

  @SneakyThrows
  @Test
  void getMultiHotelAvailabilityRequestV2_SingleInterval_ShouldExecuteSynchronously() {
    // Arrange - Date range within maxRequestedDays-1 limit (3 days < 89)
    MultiHotelAvailabilityRequestV2Dto request = MultiHotelAvailabilityRequestV2Dto.builder()
        .arrivalDate(LocalDate.of(2011, 11, 1))
        .departureDate(LocalDate.of(2011, 11, 4))
        .accountId("123")
        .limit(1)
        .offset(0)
        .includePublicRates(Boolean.TRUE)
        .hotelIds(List.of("HOTEL_A", "HOTEL_B"))
        .sortBy("DISTANCE")
        .rooms(List.of(RoomDto.builder()
            .adults(1)
            .children(0)
            .roomTypes(List.of("DB"))
            .numberOfUnits(1)
            .build()))
        .build();

    SearchPropertyResponseType expected = mockSearchPropertyResponseType(
        BigDecimal.valueOf(100), BigDecimal.valueOf(200)).block();
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    when(ohipAvailabilityClient.getMultiHotelAvailabilityRequestV2(
        any(MultiHotelAvailabilityRequestV2Dto.class)))
        .thenReturn(Mono.just(expected));

    // Act
    var result = apiLimitsService.getMultiHotelAvailabilityRequestV2(request);

    // Assert
    assertNotNull(result);
    assertEquals(expected.getLimit(), result.getLimit());
    assertEquals(expected.getOffset(), result.getOffset());
    assertEquals(expected.getRoomStays().size(), result.getRoomStays().size());
    // Verify client was called exactly once (synchronous, blocking call)
    org.mockito.Mockito.verify(ohipAvailabilityClient, org.mockito.Mockito.times(1))
        .getMultiHotelAvailabilityRequestV2(any(MultiHotelAvailabilityRequestV2Dto.class));
  }

  @SneakyThrows
  @Test
  void getItemInventoryResponses_SingleIntervalAtBoundary_ShouldExecuteSynchronously() {
    // Arrange - Date range exactly equal to maxRequestedDays (90 days = 90 limit)
    ItemInventoryRequest request = ItemInventoryRequest.builder()
        .hotelId("LONEUS")
        .startDate("2023-02-01")
        .endDate("2023-05-01") // Exactly 90 days inclusive (Feb 1 + 89 days = May 1)
        .build();
    
    ItemInventoryResponseDto expected = mockResponse();
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    when(ohipAvailabilityClient.getItemsInventory(any(ItemInventoryRequestDto.class)))
        .thenReturn(expected);

    // Act
    var result = apiLimitsService.getItemInventoryResponses(request);

    // Assert
    assertNotNull(result);
    assertEquals(expected, result);
    // Should still be single interval at boundary
    org.mockito.Mockito.verify(ohipAvailabilityClient, org.mockito.Mockito.times(1))
        .getItemsInventory(any(ItemInventoryRequestDto.class));
  }

  @SneakyThrows
  @Test
  void getItemInventoryResponses_SingleInterval_WithException_ShouldPropagateCorrectly() {
    // Arrange - Single interval that throws exception
    ItemInventoryRequest request = ItemInventoryRequest.builder()
        .hotelId("LONEUS")
        .startDate("2023-02-01")
        .endDate("2023-03-03")
        .build();
    
    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    when(ohipAvailabilityClient.getItemsInventory(any(ItemInventoryRequestDto.class)))
        .thenThrow(new HotelReservationException(ErrorCode.OHIP_HOTEL_ITEMS_INVENTORY_EXCEPTION,
            "Single interval error"));

    // Act & Assert
    var exception = Assertions.assertThrows(HotelReservationException.class,
        () -> apiLimitsService.getItemInventoryResponses(request));
    
    assertEquals("Single interval error", exception.getMessage());
    // Verify exception was not wrapped in CompletionException (direct propagation)
    assertNotNull(exception);
  }

  @SneakyThrows
  @Test
  void getHotelAvailabilityResponses_SingleInterval_WithException_ShouldPropagateCorrectly() {
    // Arrange - Single interval that throws exception
    AvailabilityRequestDto request = AvailabilityRequestDto.builder()
        .hotelId("LONEUS")
        .roomStayStartDate("2023-05-01")
        .roomStayEndDate("2023-05-08")
        .roomTypes(List.of("DB"))
        .adults(List.of(2))
        .children(List.of(0))
        .cotsRequired(List.of(Boolean.FALSE))
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .ratePlanCode("FLEXRATE")
        .roomSubstitutions(mockRoomSubstitutionsDtoDB())
        .build();

    when(availabilityOhipProperties.getMaxRequestedDays()).thenReturn(90);
    when(ohipAvailabilityClient.getHotelAvailabilityRequest(any(AvailabilityRequestDto.class)))
        .thenReturn(Mono.error(new HotelAvailabilityException(
            ErrorCode.OHIP_RETRIEVE_AVAILABILITY_EXCEPTION, "Availability error")));

    // Act & Assert
    var exception = Assertions.assertThrows(HotelAvailabilityException.class,
        () -> apiLimitsService.getHotelAvailabilityResponses(request));
    
    assertEquals("Availability error", exception.getMessage());
  }

}
