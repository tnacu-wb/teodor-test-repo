package uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.inventory.StatisticType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.MultiRoomRateAvailabilityResponseType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.MultiRoomRateAvailabilityType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.MultiRoomRateStayType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.MultiRoomRateType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.MultiRoomTypesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.RoomRateInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.RoomRatePriceInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.RoomTagType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.SearchPropertyResponseType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.SearchPropertyRoomStayType;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeRoomInfoCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RestrictionsByDateRangeSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.exceptions.HotelAvailabilityException;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.AmountTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HeldByIdDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HoldItemInfoDetailDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HoldItemInfoDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HoldItemInventoryRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HoldItemTimeSpanDto;
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
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.AvailabilityByIdsSearchCriteriaV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.AvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.ItemInventoryResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.MultiHotelAvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.MultiHotelAvailabilityRequestV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RateDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RateV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomByIdsDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomTypesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.properties.AvailabilityOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class OhipAvailabilityClientTest {

  @Mock
  AvailabilityOhipProperties availabilityOhipProperties;
  @InjectMocks
  private OhipAvailabilityClient ohipAvailabilityClient;
  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec responseSpecMock;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Test
  void getAvailability__ShouldReturnOK() {
    // Arrange
    AvailabilityRequestDto availabilityRequest = createAvailabilityRequest();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelAvailabilityDetailsDto.class)).thenReturn(
        mockHotelAvailabilityDetails());

    //Act
    Mono<HotelAvailabilityDetailsDto> hotelAvailabilityDetails =
        ohipAvailabilityClient.getHotelAvailabilityRequest(availabilityRequest);

    //Assert
    assertThat(hotelAvailabilityDetails, notNullValue());
    assertEquals("LONEUS",
        Objects.requireNonNull(hotelAvailabilityDetails.block()).getHotelAvailability().get(0)
            .getHotelId());
  }

  @Test
  void getPriceBreakDown__ShouldReturnOk() {
    //Arrange
    AvailabilityRequestDto availabilityRequest = createAvailabilityRequest();

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PriceBreakdownDto.class)).thenReturn(
        mockHPriceBreakdownDetails());

    //Act
    PriceBreakdownDto foundPriceBreakDown =
        ohipAvailabilityClient.getPriceBreakdownPerNight(availabilityRequest).block();

    //Assert
    assertThat(foundPriceBreakDown, notNullValue());
  }

  @Test
  void getItemsInventory__ShouldReturnOk() {
    //Arrange
    ItemInventoryRequestDto itemInventoryRequestDto = createItemInventoryRequestDto();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ItemInventoryResponseDto.class)).thenReturn(
        mockItemInventoryResponseDto());

    //Act
    ItemInventoryResponseDto itemInventoryResponseDto =
        ohipAvailabilityClient.getItemsInventory(itemInventoryRequestDto);

    //Assert
    assertThat(itemInventoryResponseDto, notNullValue());
  }

  @Test
  void getItemsInventory__error_4xx() {
    ///Arrange
    ItemInventoryRequestDto itemInventoryRequestDto = createItemInventoryRequestDto();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> ohipAvailabilityClient.getItemsInventory(itemInventoryRequestDto));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("Error while trying to get items inventory for hotelId=LONEUS",
        debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void itemInventoryHold__Success() {
    //Arrange
    var requestDto = createHoldItemInventoryRequestDto();
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(
        Mono.just(new ResponseEntity(HttpStatusCode.valueOf(204))));
    //Act
    ohipAvailabilityClient.itemInventoryHold(requestDto);

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getRoomTypesInfo__ShouldReturnOk() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RoomTypesResponseDto.class)).thenReturn(
        mockRoomTypesResponseDto());

    //Act
    RoomTypesResponseDto itemInventoryResponseDto =
        ohipAvailabilityClient.getRoomTypes("LONEUS");

    //Assert
    assertThat(itemInventoryResponseDto, notNullValue());
  }

  @Test
  void getHotelAvailabilityByIdsRequest__ShouldReturnOk() {
    //Arrange
    var requestDto = mockMultiHotelAvailabilityRequestDto();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelAvailabilityDetailsDto.class)).thenReturn(
        mockHotelAvailability());
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    //Act
    Mono<HotelAvailabilityDetailsDto> response =
        ohipAvailabilityClient.getHotelAvailabilityByIdsRequest(requestDto);

    //Assert
    assertThat(response.block(), notNullValue());
  }

  private Mono<RoomTypesResponseDto> mockRoomTypesResponseDto() {
    return Mono.just(RoomTypesResponseDto
        .builder()
        .build());
  }

  private ItemInventoryRequestDto createItemInventoryRequestDto() {
    return ItemInventoryRequestDto.builder()
        .hotelId("LONEUS")
        .startDate("2022-03-01")
        .endDate("2022-03-03")
        .build();
  }

  private HoldItemInventoryRequestDto createHoldItemInventoryRequestDto() {
    return HoldItemInventoryRequestDto.builder()
        .holdItemInfo(HoldItemInfoDto.builder()
            .heldById(HeldByIdDto.builder().id("123").type("type").build())
            .heldBy("holdBy")
            .hotelId("LONEUS")
            .holdItemInfoList(List.of(HoldItemInfoDetailDto.builder()
                .itemCode("CODE1")
                .count(3)
                .timeSpan(HoldItemTimeSpanDto.builder()
                    .endDate("2023-08-12")
                    .startDate("2023-08-05")
                    .build()).build())).build()).build();
  }

  @Test
  void getHotelInventory__ShouldReturnOK() {
    //Arrange
    AvailabilityRequestDto availabilityRequest = createAvailabilityRequest();
    int roomCountRequested = 1;

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelInventoryDto.class)).thenReturn(
        mockHotelInventoryDetails());

    //Act
    Mono<HotelInventoryDto> hotelInventory = ohipAvailabilityClient.getHotelInventory(
        availabilityRequest.getHotelId(), availabilityRequest.getRoomStayStartDate(),
        availabilityRequest.getRoomStayEndDate(),
        roomCountRequested);

    //Assert
    assertThat(hotelInventory, notNullValue());
    assertTrue(Objects.requireNonNull(hotelInventory.block()).getHotelInventories().get(0)
        .getHouseInventory().get(0)
        .getAvailable());
    assertTrue(
        Objects.requireNonNull(hotelInventory.block()).getHotelInventories().get(0)
            .getHouseInventory().get(0).getAvailable());
    assertThat(
        Objects.requireNonNull(hotelInventory.block()).getHotelInventories().get(0)
            .getRoomTypeInventories().get(0).getCode(),
        is("DOUBLE"));
    assertThat(
        Objects.requireNonNull(hotelInventory.block()).getHotelInventories().get(0)
            .getRoomTypeInventories().get(0)
            .getInventoryCounts().get(0).getAvailableCount(), is(42)
    );
  }

  private AvailabilityRequestDto createAvailabilityRequest() {
    return AvailabilityRequestDto.builder()
        .hotelId("LONEUS")
        .roomStayStartDate("2022-03-01")
        .roomStayEndDate("2022-03-03")
        .ratePlanCode("SEMIFLEX")
        .adults(Collections.singletonList(1))
        .roomStayQuantity(1)
        .roomTypes(Collections.singletonList("DOUBLE"))
        .children(Collections.singletonList(0))
        .cotsRequired(Collections.singletonList(Boolean.TRUE))
        .build();
  }

  private Mono<HotelAvailabilityDetailsDto> mockHotelAvailabilityDetails() {
    return Mono.just(HotelAvailabilityDetailsDto.builder()
        .hotelAvailability(createHotelAvailabilityList())
        .build());
  }

  private List<HotelAvailabilityDto> createHotelAvailabilityList() {
    List<HotelAvailabilityDto> hotelAvailabilityList = new LinkedList<>();
    HotelAvailabilityDto hotelAvailability = HotelAvailabilityDto.builder()
        .hotelId("LONEUS")
        .build();
    hotelAvailabilityList.add(hotelAvailability);
    return hotelAvailabilityList;
  }


  private Mono<PriceBreakdownDto> mockHPriceBreakdownDetails() {
    return Mono.just(new PriceBreakdownDto());
  }

  private Mono<ItemInventoryResponseDto> mockItemInventoryResponseDto() {
    return Mono.just(new ItemInventoryResponseDto());
  }

  private Mono<HotelInventoryDto> mockHotelInventoryDetails() {
    return Mono.just(
        HotelInventoryDto.builder().hotelInventories(createHotelInventoriesList()).build());
  }

  private List<HotelInventoryTypeDto> createHotelInventoriesList() {
    List<HotelInventoryTypeDto> hotelInventory = new LinkedList<>();

    List<InventoryCountsTypeDto> houseInventory = new LinkedList<>();
    InventoryCountsTypeDto inventoryCountsType = InventoryCountsTypeDto.builder()
        .available(true)
        .build();
    houseInventory.add(inventoryCountsType);

    List<InventoryLevelCountsListTypeDto> roomTypeInventories = new LinkedList<>();

    List<InventoryCountsTypeDto> inventoryCounts = new LinkedList<>();
    InventoryCountsTypeDto inventoryCount = InventoryCountsTypeDto.builder()
        .availableCount(42)
        .build();
    inventoryCounts.add(inventoryCount);

    InventoryLevelCountsListTypeDto inventoryLevelCounts = InventoryLevelCountsListTypeDto.builder()
        .code("DOUBLE")
        .inventoryCounts(inventoryCounts)
        .build();
    roomTypeInventories.add(inventoryLevelCounts);

    HotelInventoryTypeDto hotelInventoryType = HotelInventoryTypeDto.builder()
        .houseInventory(houseInventory)
        .roomTypeInventories(roomTypeInventories)
        .build();
    hotelInventory.add(hotelInventoryType);

    return hotelInventory;
  }

  @Test
  void getRateCodePricing__ShouldReturnOK() {
    //Arrange
    RateCodeCriteria rateCodeCriteria = createRateCodeRequest();
    RateCodeRoomInfoCriteria rateCodeRoomInfoCriteria = createRateCodeRoomInfoCriteria();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PriceBreakdownDto.class)).thenReturn(
        mockHPriceBreakdownDetails());

    //Act
    PriceBreakdownDto priceBreakdownDto =
        ohipAvailabilityClient.getRateCodePricing(rateCodeCriteria, rateCodeRoomInfoCriteria);
    //Assert
    assertThat(priceBreakdownDto, notNullValue());
  }

  @Test
  void getRateCodePricing__error_4xx() {
    //Arrange
    RateCodeCriteria rateCodeCriteria = createRateCodeRequest();
    RateCodeRoomInfoCriteria rateCodeRoomInfoCriteria = createRateCodeRoomInfoCriteria();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> ohipAvailabilityClient.getRateCodePricing(rateCodeCriteria,
            rateCodeRoomInfoCriteria));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("Error while trying to get rate code pricing for hotelId=MANOLD",
        debugMessage);
    verifyNoMoreInteractions(webClient);
  }


  @Test
  void getHotelAvailabilityByIdsRequestV2__ShouldReturnOK() {
    //Arrange
    var availabilityByIdsSearchReqV2 = createAvailabilityByIdsSearchCriteriaV2Dto();
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        availabilityOhipProperties.getMultiRoomRateAvaEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(MultiRoomRateAvailabilityResponseType.class)).thenReturn(
        mockMultiRoomRateAvailabilityResponseType());

    //Act
    var result = ohipAvailabilityClient.getHotelAvailabilityByIdsRequestV2(
        availabilityByIdsSearchReqV2);

    //Assert
    assertThat(result.block(), notNullValue());
  }

  @Test
  void getMultiHotelAvailabilityRequestV2__ShouldReturnOK() {
    //Arrange
    var multiHotelAvailabilityRequestV2 = createMultiHotelAvailabilityRequestV2Dto();
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        availabilityOhipProperties.getMultiRoomRateAvaEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(SearchPropertyResponseType.class)).thenReturn(
        mockSearchPropertyResponseType());

    //Act
    var result = ohipAvailabilityClient.getMultiHotelAvailabilityRequestV2(
        multiHotelAvailabilityRequestV2);

    //Assert
    assertThat(result.block(), notNullValue());
  }

  @Test
  void getHotelInventoryStatistics__ShouldReturnOK() {
    // Arrange
    AvailabilityRequestDto availabilityRequest = createAvailabilityRequest();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(new ParameterizedTypeReference<List<StatisticType>>() {
    })).thenReturn(mockInventoryStatistics());

    //Act
    List<StatisticType> operaInventoryStatistics =
        ohipAvailabilityClient.getHotelInventoryStatistics(availabilityRequest);

    //Assert
    assertThat(operaInventoryStatistics, notNullValue());
  }

  @Test
  void getRestrictionsByDateRange_WhenCallSucceeds_ThenCorrectObjectReturned() {
    RestrictionsByDateRangeSearchCriteria request = RestrictionsByDateRangeSearchCriteria
        .builder()
        .startDate("2025-01-01")
        .endDate("2025-01-31")
        .build();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    var resultMock = Mono.just(mock(RestrictionsByDateRangeResult.class));
    when(responseSpec.bodyToMono(RestrictionsByDateRangeResult.class)).thenReturn(resultMock);

    var result = ohipAvailabilityClient.getRestrictionsByDateRange(request);

    assertEquals(resultMock.block(), result);
  }

  @Test
  void getRestrictionsByDateRange_When4xx_ThenExceptionThrown() {
    RestrictionsByDateRangeSearchCriteria request = RestrictionsByDateRangeSearchCriteria
        .builder()
        .hotelId("hotelId")
        .startDate("2025-01-01")
        .endDate("2025-01-31")
        .build();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    var thrownException = assertThrowsExactly(HotelAvailabilityException.class,
        () -> ohipAvailabilityClient.getRestrictionsByDateRange(request));

    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals(String.format("Error while trying to get hotel restrictions by date "
        + "range for hotelId=%s, start=%s, end=%s.", request.getHotelId(), request.getStartDate(),
        request.getEndDate()), debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  private MultiHotelAvailabilityRequestV2Dto createMultiHotelAvailabilityRequestV2Dto() {
    return MultiHotelAvailabilityRequestV2Dto.builder()
        .accountId("alabala")
        .hotelIds(List.of("MANOLD"))
        .arrivalDate(LocalDate.of(2029, 1, 1))
        .departureDate(LocalDate.of(2029, 1, 2))
        .includePublicRates(true)
        .limit(20)
        .minRate(BigDecimal.valueOf(15))
        .rooms(List.of(
            RoomDto.builder()
                .roomTypes(List.of("Type1", "Type2"))
                .numberOfUnits(2)
                .adults(2)
                .children(1)
                .tag("FAM")
                .build()))
        .build();
  }

  private Mono<MultiRoomRateAvailabilityResponseType> mockMultiRoomRateAvailabilityResponseType() {

    var result = new MultiRoomRateAvailabilityResponseType();
    var multiRoomRateStayType = new MultiRoomRateStayType();
    var roomTypes = List.of(new MultiRoomTypesType());
    var multiRoomRateType = new MultiRoomRateType();
    var roomRateInfoType = new RoomRateInfoType();
    var priceInfo = new RoomRatePriceInfoType();
    var multiRoomRateAvailabilityType = new MultiRoomRateAvailabilityType();

    priceInfo.setStayDate(LocalDate.of(2029, 1, 1));
    priceInfo.setAmountAfterTax(BigDecimal.valueOf(123));
    priceInfo.setAmountBeforeTax(BigDecimal.valueOf(113));

    roomRateInfoType.setPriceInfo(List.of(priceInfo));
    multiRoomRateType.setRatePlanCode("FLEXRATE");
    multiRoomRateType.setCurrencyCode("GBP");
    multiRoomRateType.setRoomRateInfo(roomRateInfoType);
    roomTypes.get(0).setRoomType("DOUBLE");
    roomTypes.get(0).setTag("DB");
    roomTypes.get(0).setRoomRates(List.of(multiRoomRateType));
    multiRoomRateStayType.setRoomTypes(roomTypes);

    multiRoomRateAvailabilityType.setHotelId("LONEUS");
    multiRoomRateAvailabilityType.setRoomStays(List.of(multiRoomRateStayType));

    result.setHotelAvailability(List.of(multiRoomRateAvailabilityType));

    return Mono.just(result);
  }

  private Mono<SearchPropertyResponseType> mockSearchPropertyResponseType() {

    var searchPropertyRoomStayType = new SearchPropertyRoomStayType();
    var rtt = new RoomTagType();
    rtt.setRoomTypes(List.of("FAMABC", "FAMCDE"));
    rtt.setTag("FAM");
    searchPropertyRoomStayType.setRoomTags(List.of(rtt));
    var result = new SearchPropertyResponseType();
    result.setLimit(3);
    result.setTotalResults(2);
    result.setRoomStays(List.of(searchPropertyRoomStayType));

    return Mono.just(result);


  }

  private RateCodeRoomInfoCriteria createRateCodeRoomInfoCriteria() {
    return RateCodeRoomInfoCriteria.builder()
        .adultsNo(2)
        .childrenNo(0)
        .roomType("DOUBLE")
        .build();
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

  private MultiHotelAvailabilityRequestDto mockMultiHotelAvailabilityRequestDto() {
    return MultiHotelAvailabilityRequestDto.builder()
        .hotelIds(List.of("LONEUS"))
        .roomStayQuantity(1)
        .roomStayStartDate("2022-11-01")
        .roomStayEndDate("2022-11-03")
        .roomTypes(List.of("DB"))
        .adults(List.of(2))
        .children(List.of(0))
        .cotsRequired(List.of(Boolean.TRUE))
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .companyId("1353")
        .ratePlanCodes(List.of("FLEXRATE"))
        .build();
  }

  private Mono<HotelAvailabilityDetailsDto> mockHotelAvailability() {
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

  private AvailabilityByIdsSearchCriteriaV2Dto createAvailabilityByIdsSearchCriteriaV2Dto() {
    return AvailabilityByIdsSearchCriteriaV2Dto.builder()
        .arrivalDate(LocalDate.of(2029, 1, 1))
        .departureDate(LocalDate.of(2029, 1, 2))
        .hotelIds(List.of("LONEUS", "MANOLD"))
        .rates(createPublicRatesList())
        .rooms(createRoomsList())
        .build();
  }

  private RateDto createPublicRatesList() {
    return RateDto.builder()
        .ratePlanCodes(List.of("FLEXRATE"))
        .build();
  }

  private List<RoomByIdsDto> createRoomsList() {
    return List.of(RoomByIdsDto.builder()
        .tag("DB")
        .adults(1)
        .children(0)
        .numberOfRooms(2)
        .roomTypes(List.of("ROOM1", "ROOM2"))
        .build()
    );
  }

  private Mono<List<StatisticType>> mockInventoryStatistics() {
    return Mono.just(List.of(new StatisticType()));
  }
}
