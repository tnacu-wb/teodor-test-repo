package uk.co.whitbread.infrastructure.rest.client.availability;

import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.infrastructure.rest.client.availabilitycache.AvailabilityCacheV1SearchOutPortImplTest.ARRIVAL_DATE_V2;
import static uk.co.whitbread.infrastructure.rest.client.availabilitycache.AvailabilityCacheV1SearchOutPortImplTest.DEPARTURE_DATE_V2;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import uk.co.whitbread.domain.model.availability.in.BookingChannel;
import uk.co.whitbread.domain.model.availability.in.CorporateRate;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsRequest;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityRequest;
import uk.co.whitbread.domain.model.availability.in.HotelInventoryRequest;
import uk.co.whitbread.domain.model.availability.in.MultiHotelAvaSearchCriteria;
import uk.co.whitbread.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.domain.model.availability.in.RateV2;
import uk.co.whitbread.domain.model.availability.in.RestrictionsByDateRangeRequest;
import uk.co.whitbread.domain.model.availability.in.RoomPriceBreakdownRequest;
import uk.co.whitbread.domain.model.availability.out.HotelAvailability;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIds;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.domain.model.availability.out.HotelInventoryRoomType;
import uk.co.whitbread.domain.model.availability.out.MultiAvailabilityResponse;
import uk.co.whitbread.domain.model.availability.out.RateCodePricingResult;
import uk.co.whitbread.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.domain.model.availability.out.Room;
import uk.co.whitbread.domain.model.availability.out.RoomLevelInventory;
import uk.co.whitbread.domain.model.availability.out.RoomPriceBreakdown;
import uk.co.whitbread.domain.model.availability.out.RoomPriceBreakdownResult;
import uk.co.whitbread.domain.model.availability.out.RoomRate;
import uk.co.whitbread.domain.model.availability.out.RoomTypeInfo;
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AvailabilityByIdsResponseV2Dto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelInventoryRoomTypeDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MultiAvailabilityResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ClassificationsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlanDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlansResponseDto;
import uk.co.whitbread.infrastructure.rest.client.availability.exception.HotelAvailabilityException;
import uk.co.whitbread.infrastructure.rest.client.availability.mapper.HotelAvailabilityMapper;
import uk.co.whitbread.infrastructure.rest.client.availability.mapper.HotelAvailabilityRequestMapper;
import uk.co.whitbread.infrastructure.rest.client.availability.mapper.HotelInventoryMapper;
import uk.co.whitbread.infrastructure.rest.client.availability.model.HotelAvailabilityByIdsRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.HotelAvailabilityRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.HotelAvailabilityByIdsRequestOhipV2Dto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.HotelInventoryRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.MultiHotelAvaSearchCriteriaOhip;
import uk.co.whitbread.infrastructure.rest.client.OhipClient;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityByIdsDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityResponseDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.RoomDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.RoomRateDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.RoomTypeDto;


@ExtendWith(MockitoExtension.class)
class HotelAvailabilityOHIPClientPortImplTest {

  public static final String HOTEL_ID = "TTSSTT";
  public static final String START_DATE = "2022-12-20";
  public static final String END_DATE = "2022-12-26";
  public static final String TIMESTAMP = "2022-04-21T18:25:43-05:00";

  @InjectMocks
  private HotelAvailabilityOutPortImpl hotelAvailabilityOHIPClientPort;
  @Mock
  private HotelAvailabilityRequestMapper requestAdapter;
  @Mock
  private HotelAvailabilityMapper availabilityAdapter;

  @Mock
  private HotelInventoryMapper hotelInventoryMapper;

  @Mock
  private OhipClient ohipClient;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Test
  void getAvailability__ShouldReturnOK() {

    // Arrange
    var hotelAvailabilityDto = new HotelAvailabilityDto();
    hotelAvailabilityDto.setRoomRates(List.of());

    Mockito.when(ohipClient.getHotelAvailability(
        HotelAvailabilityRequestOhipDto.builder()
            .roomTypes(List.of())
            .adultsNumber(List.of())
            .childrenNumber(List.of())
            .cotsRequired(List.of())
            .build())).thenReturn(hotelAvailabilityDto);
    when(requestAdapter.toOhipDto(any())).thenReturn(HotelAvailabilityRequestOhipDto.builder()
        .roomTypes(List.of())
        .adultsNumber(List.of())
        .childrenNumber(List.of())
        .cotsRequired(List.of())
        .build());
    when(availabilityAdapter.toModel(any())).thenReturn(mockHotelAvailability());

    // Act
    HotelAvailability response = hotelAvailabilityOHIPClientPort
        .getHotelAvailability(createHotelAvailabilityRequest());

    // Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(HOTEL_ID, response.getHotelId());
    Assertions.assertEquals(START_DATE, response.getStartDate());
    Assertions.assertEquals(END_DATE, response.getEndDate());
    Assertions.assertTrue(response.isAvailable());
    Assertions.assertEquals(Instant.parse(TIMESTAMP), response.getTimestamp());
  }


  @Test
  void getAvailability__ShouldThrowException() {

    String error = "Error while trying to get hotel availability!";
    // Arrange
    Mockito.when(ohipClient.getHotelAvailability(any()))
        .thenThrow(new HotelAvailabilityException("message", "Error while trying to get hotel availability!",
                new Exception(), 1));

    HotelAvailabilityRequest availabilityRequest = createHotelAvailabilityRequest();
    // Act
    HotelAvailabilityException exception = Assertions
        .assertThrows(HotelAvailabilityException.class, () -> {
          hotelAvailabilityOHIPClientPort.getHotelAvailability(availabilityRequest);
        });

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getHotelRoomsInventory__ShouldReturnOK() {

    // Arrange
    Mockito.when(ohipClient.getHotelRoomsInventory(any()))
        .thenReturn(new HotelInventoryRoomTypeDto());
    when(hotelInventoryMapper.toOhipDto(any())).thenReturn(new HotelInventoryRequestOhipDto());
    when(hotelInventoryMapper.toDomainModel(any())).thenReturn(mockHotelRoomsInventory());

    // Act
    HotelInventoryRoomType response = hotelAvailabilityOHIPClientPort
        .getHotelRoomsInventory(createHotelInventoryRequest());

    // Assert
    Assertions.assertNotNull(response);
    assertThat(response.getRoomTypeInventories(), hasSize(1));
    assertThat(response.getRoomTypeInventories().get(0).getAvailableCount(), is(42));
    assertThat(response.getRoomTypeInventories().get(0).getCode(), is("DOUBLE"));
  }


  @Test
  void getHotelRoomsInventory__ShouldThrowException() {

    String error = "Error while trying to get hotel rooms inventory!";
    // Arrange
    Mockito.when(ohipClient.getHotelRoomsInventory(any()))
        .thenThrow(
            new HotelAvailabilityException("message", "Error while trying to get hotel rooms inventory!",
                    new Exception(), 1));

    HotelInventoryRequest inventoryRequest = createHotelInventoryRequest();
    // Act
    HotelAvailabilityException exception = Assertions
        .assertThrows(HotelAvailabilityException.class, () -> {
          hotelAvailabilityOHIPClientPort.getHotelRoomsInventory(inventoryRequest);
        });

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getHotelAvailabilityByIds__ShouldReturnOK() {
    // Arrange
    when(requestAdapter.toAvailabilityByIdsOhipModel(any())).thenReturn(mockHotelAvailabilityByIdsRequestOhipDto());
    when(ohipClient.getHotelAvailabilityByIds(any())).thenReturn(mockHotelAvailabilityByIdsDto(""));
    when(availabilityAdapter.toAvailabilityByIdsModel(any())).thenReturn(mockHotelAvailabilityByIds(""));
    when(ohipClient.getRatePlans(any(), any())).thenReturn(Mono.just(mockRatePlansResponseDto()));

    //Act
    var result = hotelAvailabilityOHIPClientPort.getHotelAvailabilityByIds(mockHotelAvailabilityByIdsRequest(false));

    //Assert
    Assertions.assertNotNull(result);
    Assertions.assertNotNull(result.getHotelAvailability());
    assertThat(result.getHotelAvailability().size(), is(1));
  }

  @Test
  void getHotelAvailabilityByIds__ShouldReturnOk__WhenFilteringTwin() {
    // Arrange
    when(requestAdapter.toAvailabilityByIdsOhipModel(any())).thenReturn(mockHotelAvailabilityByIdsRequestOhipDtoTwinRooms());
    when(ohipClient.getHotelAvailabilityByIds(any())).thenReturn(mockHotelAvailabilityByIdsDto("TWIN"));
    when(availabilityAdapter.toAvailabilityByIdsModel(any())).thenReturn(mockHotelAvailabilityByIds("TWIN"));
    when(ohipClient.getRatePlans(any(), any())).thenReturn(Mono.just(mockRatePlansResponseDto()));
    FeatureFlag featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getFilterTwinOnPriority())).thenReturn(true);

    //Act
    var result = hotelAvailabilityOHIPClientPort.getHotelAvailabilityByIds(mockHotelAvailabilityByIdsRequest(true));

    //Assert
    Assertions.assertNotNull(result);
    Assertions.assertNotNull(result.getHotelAvailability());
    assertThat(result.getHotelAvailability().size(), is(1));
    assertThat(result.getHotelAvailability().stream().map(HotelAvailability::getRoomRates).flatMap(List::stream)
        .map(RoomRate::getRoomTypes).flatMap(List::stream).map(RoomTypeInfo::getRooms).flatMap(List::stream).toList().size(), is(1));
  }

  private RatePlansResponseDto mockRatePlansResponseDto(){
    var ratePlansResponse = new RatePlansResponseDto();
    ratePlansResponse.setRatePlans(mockRatePlansDto());
    return ratePlansResponse;
  }

  private List<RatePlanDto> mockRatePlansDto() {
    var ratePlanDto = new RatePlanDto();
    ratePlanDto.setRatePlanCode("FLEXRATE");
    ratePlanDto.setHotelId(HOTEL_ID);
    ratePlanDto.setClassifications(mockClassificationDto());
    return Collections.singletonList(ratePlanDto);
  }

  private ClassificationsDto mockClassificationDto() {
    var classificationDto = new ClassificationsDto();
    classificationDto.setDisplaySet("PBF");
    classificationDto.setMarketCode("OTH");
    classificationDto.setRateCategory("A");
    return classificationDto;
  }


  private HotelAvailabilityByIdsRequest mockHotelAvailabilityByIdsRequest(boolean isGds){
    return HotelAvailabilityByIdsRequest
        .builder()
        .arrivalDate(LocalDate.now().toString())
        .departureDate(LocalDate.now().plusDays(3).toString())
        .roomTypes(List.of("DOUBLE", "TWIN"))
        .hotelIds(List.of("LONEUS"))
        .adultsNumber(List.of(2,2))
        .channel("DISTR")
        .subchannel(isGds ? "AMADEUS" : "AGENCY")
        .build();
  }

  private HotelAvailabilityByIdsV2Request getHotelAvailabilityByIdsV2Request() {
    return HotelAvailabilityByIdsV2Request.builder()
            .hotelIds(new ArrayList<>(List.of(HOTEL_ID)))
            .rooms(new ArrayList<>(List.of(uk.co.whitbread.domain.model.availability.in.Room.builder()
                    .tag("DB")
                    .adults(2)
                    .children(0)
                    .numberOfRooms(1)
                    .build())))
            .arrivalDate(ARRIVAL_DATE_V2)
            .departureDate(DEPARTURE_DATE_V2)
            .rates(RateV2.builder()
                    .ratePlanCodes(List.of("FLEXRATE"))
                    .build())
            .bookingChannel(BookingChannel.builder()
                    .language("en")
                    .subchannel("WEB")
                    .build())
            .vatNotRequired(false)
            .build();
  }


  private HotelAvailabilityByIdsRequestOhipDto mockHotelAvailabilityByIdsRequestOhipDto() {
    return HotelAvailabilityByIdsRequestOhipDto
        .builder()
        .hotelIds(List.of("LONEUS", "MANOLD"))
        .arrivalDate("2023-06-06")
        .departureDate("2023-06-20")
        .build();
  }

  private HotelAvailabilityByIdsRequestOhipDto mockHotelAvailabilityByIdsRequestOhipDtoTwinRooms() {
    return HotelAvailabilityByIdsRequestOhipDto
        .builder()
        .hotelIds(List.of("LONEUS", "MANOLD"))
        .arrivalDate("2023-06-06")
        .departureDate("2023-06-20")
        .subchannel("AMADEUS")
        .roomTypes(List.of("TWIN"))
        .build();
  }

  private HotelAvailabilityByIdsDto mockHotelAvailabilityByIdsDto(String roomType){
    var hotelAvailabilityByIdsDto = new HotelAvailabilityByIdsDto();
    if (StringUtils.equalsIgnoreCase(roomType, "TWIN")){
      hotelAvailabilityByIdsDto.setHotelAvailability(List.of(mockHotelAvailabilityDtoTwinRooms()));
    } else {
      hotelAvailabilityByIdsDto.setHotelAvailability(List.of(mockHotelAvailabilityDto()));
    }

    return hotelAvailabilityByIdsDto;
  }

  private HotelAvailabilityByIds mockHotelAvailabilityByIds(String roomType) {
    var mockHotelAvailability = StringUtils.equalsIgnoreCase(roomType, "TWIN") ? mockHotelAvailabilityTwinRooms() : mockHotelAvailability();

    return HotelAvailabilityByIds
        .builder()
        .hotelAvailability(List.of(mockHotelAvailability))
        .build();
  }

  private HotelAvailabilityResponseDto mockHotelAvailabilityDto(){
    var hotelAvailabilityDto = new HotelAvailabilityResponseDto();
    hotelAvailabilityDto.setAvailable(Boolean.TRUE);

    hotelAvailabilityDto.setHotelId(HOTEL_ID);
    hotelAvailabilityDto.setStartDate(START_DATE);
    hotelAvailabilityDto.setEndDate(END_DATE);
    hotelAvailabilityDto.setRoomRates(Collections.singletonList(new RoomRateDto()));
    return hotelAvailabilityDto;
  }

  private HotelAvailabilityResponseDto mockHotelAvailabilityDtoTwinRooms(){
    var hotelAvailabilityDto = new HotelAvailabilityResponseDto();
    hotelAvailabilityDto.setAvailable(Boolean.TRUE);
    hotelAvailabilityDto.setHotelId(HOTEL_ID);
    hotelAvailabilityDto.setStartDate(START_DATE);
    hotelAvailabilityDto.setEndDate(END_DATE);

    var room1 = new RoomDto();
    room1.setPmsRoomType("FMTRPL");
    room1.setSpecialRequests(Collections.singletonList("TWDS"));

    var room2 = new RoomDto();
    room2.setPmsRoomType("FMQUAD");
    room2.setSpecialRequests(Collections.singletonList("TWDS"));

    var roomTypeDto = new RoomTypeDto();
    roomTypeDto.setRoomType("TWIN");
    roomTypeDto.setRooms(List.of(room1, room2));

    var roomRateDto = new RoomRateDto();
    roomRateDto.setRoomTypes(Collections.singletonList(roomTypeDto));

    hotelAvailabilityDto.setRoomRates(Collections.singletonList(roomRateDto));
    return hotelAvailabilityDto;
  }

  @Test
  void testGetMultiHotelAvailability__ShouldReturnOK(){
    //Arrange
    when(availabilityAdapter.toMultiAvaDomainModel(any())).thenReturn(MultiAvailabilityResponse.builder().build());
    when(requestAdapter.toMultiAvaOhipModel(any())).thenReturn(mockMultiHotelAvaSearchCriteriaOhip());
    when(ohipClient.getMultiHotelAvailability(any())).thenReturn(mockMultiHotelAvailabilityDto());
    //Act
    var result = hotelAvailabilityOHIPClientPort.getMultiHotelAvailability(MultiHotelAvaSearchCriteria.builder()
        .build());
    //Assert
    Assertions.assertNotNull(result);
  }



  private MultiHotelAvaSearchCriteriaOhip mockMultiHotelAvaSearchCriteriaOhip() {
    return MultiHotelAvaSearchCriteriaOhip.builder()
        .hotelIds(List.of("LONEUS"))
        .build();
  }

  private MultiAvailabilityResponseDto mockMultiHotelAvailabilityDto() {
    return new MultiAvailabilityResponseDto();
  }

  @Test
  void getHotelMultiRoomsPriceBreakdown__ShouldReturnOK() {

    // Arrange
    Mockito.when(ohipClient.getRoomPriceBreakdown(anyString(), any()))
        .thenReturn(mockRoomPriceBreakdown());

    // Act
    RoomPriceBreakdownResult response = hotelAvailabilityOHIPClientPort
        .getHotelMultiRoomsPriceBreakdown("TEST", createRoomPriceBreakdownRequest())
        .block();

    // Assert
    Assertions.assertNotNull(response);
    assertThat(response.getPriceBreakdown().get(0).getCurrencyCode(), is("EUR"));
    assertThat(response.getPriceBreakdown().get(0).getTotalNetAmount(),
        is(BigDecimal.valueOf(500)));
  }

  private RoomPriceBreakdownRequest createRoomPriceBreakdownRequest() {
    return RoomPriceBreakdownRequest.builder()
        .build();
  }

  private Mono<RoomPriceBreakdownResult> mockRoomPriceBreakdown() {
    return Mono.just(RoomPriceBreakdownResult.builder()
        .priceBreakdown(of(RoomPriceBreakdown.builder()
            .totalNetAmount(BigDecimal.valueOf(500))
            .currencyCode("EUR")
            .build()))
        .build());
  }

  @Test
  void getRateCodePricing__ShouldReturnOK() {

    // Arrange
    Mockito.when(ohipClient.getRateCodePricing(any()))
        .thenReturn(mockRateCodePricing());

    // Act
    RateCodePricingResult response = hotelAvailabilityOHIPClientPort
        .getRateCodePricing(createRateCodeRequest());

    // Assert
    Assertions.assertNotNull(response);
    assertThat(response.getRatePlanCode(), is("FLEXRATE"));
    assertThat(response.getCurrencyCode(), is("EUR"));
    assertThat(response.getTotalNetAmount(), is(BigDecimal.valueOf(1280)));
  }


  @Test
  void getRateCodePricing__ShouldThrowException() {

    String error = "Error while trying to get the rate code pricing!";
    // Arrange
    Mockito.when(ohipClient.getRateCodePricing(any()))
        .thenThrow(
            new HotelAvailabilityException("message", "Error while trying to get the rate code pricing!",
                    new Exception(), 1));

    RateCodeCriteria rateCodeCriteria = createRateCodeRequest();
    // Act
    HotelAvailabilityException exception = Assertions
        .assertThrows(HotelAvailabilityException.class, () -> {
          hotelAvailabilityOHIPClientPort.getRateCodePricing(rateCodeCriteria);
        });

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getHotelAvailabilityByIdsV2__ShouldReturnOK() {
    // Arrange
    when(requestAdapter.toAvailabilityByIdsOhipV2Model(any())).thenReturn(HotelAvailabilityByIdsRequestOhipV2Dto
            .builder().build());
    when(ohipClient.getHotelAvailabilityByIdsV2(any())).thenReturn(new AvailabilityByIdsResponseV2Dto());
    when(availabilityAdapter.toAvailabilityByIdsV2Model(any())).thenReturn(HotelAvailabilityByIdsV2.builder().build());

    //Act
    var result = hotelAvailabilityOHIPClientPort.getHotelAvailabilityByIdsV2(getHotelAvailabilityByIdsV2Request());

    //Assert
    Assertions.assertNotNull(result);
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void getHotelAvailabilityByIdsV3__ShouldReturnOK() {
    // Arrange
    when(requestAdapter.toAvailabilityByIdsOhipV2Model(any())).thenReturn(HotelAvailabilityByIdsRequestOhipV2Dto
        .builder().build());
    when(availabilityAdapter.toAvailabilityByIdsV2Model(any())).thenReturn(HotelAvailabilityByIdsV2.builder().build());
    HotelAvailabilityByIdsV2Request request = getHotelAvailabilityByIdsV2Request();
    request.getRates().setCorporateRates(List.of(
        CorporateRate.builder().corporateId("1234").ratePlanSets(List.of("NEG")).build(),
        CorporateRate.builder().corporateId("5678").ratePlanSets(List.of("NEG")).build()));
    //Act
    var result = hotelAvailabilityOHIPClientPort.getHotelAvailabilityByIdsV2(request);

    //Assert
    Assertions.assertNotNull(result);
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

  private HotelAvailabilityRequest createHotelAvailabilityRequest() {
    return HotelAvailabilityRequest.builder()
        .adultsNumber(List.of(1))
        .roomTypes(List.of("DB"))
        .hotelId("LONEUS")
        .departureDate("2022-02-06")
        .arrivalDate("2022-02-02")
        .childrenNumber(List.of())
        .cotsRequired(List.of())
        .build();
  }

  private HotelAvailability mockHotelAvailability() {
    var hotelAvailability = new HotelAvailability();
    hotelAvailability.setHotelId(HOTEL_ID);
    hotelAvailability.setStartDate(START_DATE);
    hotelAvailability.setEndDate(END_DATE);
    hotelAvailability.setAvailable(true);
    hotelAvailability.setTimestamp(Instant.parse(TIMESTAMP));
    hotelAvailability.setRoomRates(Collections.singletonList(new RoomRate()));
    return hotelAvailability;
  }

  private HotelAvailability mockHotelAvailabilityTwinRooms() {
    var hotelAvailability = new HotelAvailability();
    hotelAvailability.setHotelId(HOTEL_ID);
    hotelAvailability.setStartDate(START_DATE);
    hotelAvailability.setEndDate(END_DATE);
    hotelAvailability.setAvailable(true);
    hotelAvailability.setTimestamp(Instant.parse(TIMESTAMP));

    var room1 = new Room();
    room1.setPmsRoomType("FMTRPL");
    room1.setSpecialRequests(Collections.singletonList("TWDS"));

    var roomTypeInfo = new RoomTypeInfo();
    roomTypeInfo.setRoomType("TWIN");
    roomTypeInfo.setRooms(Collections.singletonList(room1));

    var roomRate = new RoomRate();
    roomRate.setRoomTypes(Collections.singletonList(roomTypeInfo));

    hotelAvailability.setRoomRates(Collections.singletonList(roomRate));
    return hotelAvailability;
  }

  private HotelInventoryRoomType mockHotelRoomsInventory() {
    var hotelRoomsInventory = new HotelInventoryRoomType();

    var roomLevelInventory = new RoomLevelInventory();
    roomLevelInventory.setAvailableCount(42);
    roomLevelInventory.setCode("DOUBLE");
    hotelRoomsInventory.setRoomTypeInventories(List.of(roomLevelInventory));

    return hotelRoomsInventory;
  }

  private HotelInventoryRequest createHotelInventoryRequest() {
    return HotelInventoryRequest.builder()
        .hotelId("LONEUS")
        .dateRangeStart("2022-10-28")
        .dateRangeEnd("2022-10-30")
        .build();
  }

  private RateCodePricingResult mockRateCodePricing() {
    return RateCodePricingResult.builder()
        .ratePlanCode("FLEXRATE")
        .totalNetAmount(BigDecimal.valueOf(1280))
        .currencyCode("EUR")
        .build();
  }

  @Test
  void getRestrictionsByDateRange_WhenCalled_ThenInvokesOhipClient(){
    var requestMock = mock(RestrictionsByDateRangeRequest.class);
    var resultMock = mock(RestrictionsByDateRangeResult.class);
    when(ohipClient.getRestrictionsByDateRange(requestMock)).thenReturn(resultMock);

    var result = hotelAvailabilityOHIPClientPort.getRestrictionsByDateRange(requestMock);

    //Assert
    Assertions.assertEquals(resultMock, result);
    verify(ohipClient, times(1)).getRestrictionsByDateRange(requestMock);
  }
}

