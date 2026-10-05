package uk.co.whitbread.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static uk.co.whitbread.domain.logic.AvailabilitiesResponseUtils.PARKING_FILTERS;
import static uk.co.whitbread.domain.model.srp.in.FacilityFilter.ACO;
import static uk.co.whitbread.domain.model.srp.in.FacilityFilter.DIN;
import static uk.co.whitbread.domain.model.srp.in.FacilityFilter.HAC;
import static uk.co.whitbread.domain.model.srp.in.FacilityFilter.HAR;
import static uk.co.whitbread.domain.model.srp.in.FacilityFilter.HLG;
import static uk.co.whitbread.domain.model.srp.in.FacilityFilter.HRS;
import static uk.co.whitbread.domain.model.srp.in.FacilityFilter.HUL;
import static uk.co.whitbread.domain.model.srp.in.FacilityFilter.LFT;
import static uk.co.whitbread.domain.model.srp.in.FacilityFilter.LUG;
import static uk.co.whitbread.domain.model.srp.in.FacilityFilter.RES;
import static uk.co.whitbread.domain.model.srp.in.FacilityFilter.WET;
import static uk.co.whitbread.domain.model.srp.in.ParkingFilter.CHARGEABLE_OFF_SITE_PARKING;
import static uk.co.whitbread.domain.model.srp.in.ParkingFilter.CHARGEABLE_OFF_SITE_PARKING_ALT;
import static uk.co.whitbread.domain.model.srp.in.ParkingFilter.CHARGEABLE_ON_SITE_PARKING;
import static uk.co.whitbread.domain.model.srp.in.ParkingFilter.FREE_PARKING;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelMigrationStatusResponse;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.in.LocationFormatEnum;
import uk.co.whitbread.domain.model.srp.in.OldWorldChannelEnum;
import uk.co.whitbread.domain.model.srp.in.RadiusUnitEnum;
import uk.co.whitbread.domain.model.srp.out.Cost;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.domain.model.srp.out.HotelsWithFilterModel;
import uk.co.whitbread.domain.ports.secondary.CacheSearchOutPort;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;

@ExtendWith(MockitoExtension.class)
class AvailabilitiesResponseUtilsTest {

  private static final String OPERA_SOURCE = "OPERA";
  public static final String OPERA_HOTEL = "OPERA_HOTEL";

  @Test
  void test_sanitizeResponseForBb() {
    // Arrange
    var input = buildAvailabilitiesResponse();
    var expectedOutput = buildListResponse();

    // Act
    var output = AvailabilitiesResponseUtils.sanitizeResponseForBb(input);

    // Assert
    assertEquals(expectedOutput.size(), output.size());
    for (int i=0; i<expectedOutput.size(); i++) {
       assertTrue(EqualsBuilder.reflectionEquals(expectedOutput.get(i),output.get(i)));
    }
  }

  @Test
  void test_updateFamilyOrTwinHubHotels() {
    // Arrange
    var inputAvailabilities = buildAvailabilitiesResponseHub();
    var hubHotels = List.of("HOTEL2", "HOTEL3");
    var roomTypes = List.of("TWIN", "FAM", "DB");

    // Act
    AvailabilitiesResponseUtils.flagHubHotelsAndUpdateForFamilyOrTwin(roomTypes, inputAvailabilities, hubHotels);

    // Assert
    hubHotels.stream().forEach(hotelCode -> {
      var availability = inputAvailabilities.getHotelAvailabilityList().stream()
          .filter(hotel -> hotelCode.equals(hotel.getHotelId()))
          .findFirst().get();

      assertEquals(Boolean.FALSE, availability.getAvailable());
    });
  }

  @Test
  void test_buildEmptyResponse() {
    // Arrange
    var mockRequest = buildAvailabilityRequest();

    // Act
    var response = AvailabilitiesResponseUtils.buildEmptyResponse(mockRequest);

    // Assert
    assertEquals(10, response.getPage());
    assertEquals(10, response.getPageSize());
  }

  @Test
  void test_buildEmptyResponseExtraPage() {
    // Arrange
    var mockRequest = buildAvailabilityRequestExtraPage();

    // Act
    var response = AvailabilitiesResponseUtils.buildEmptyResponse(mockRequest);

    // Assert
    assertEquals(20, response.getPage());
    assertEquals(10, response.getPageSize());
  }

  @Test
  void test_buildRadiusUnit() {
    // Arrange
    // Act
    var miles = AvailabilitiesResponseUtils.buildRadiusUnit(RadiusUnitEnum.MILES);
    var kilometers = AvailabilitiesResponseUtils.buildRadiusUnit(RadiusUnitEnum.KILOMETERS);

    // Assert
    assertEquals("km", kilometers);
    assertEquals("mi", miles);
  }

  @Test
  void updateAvailabilitiesResponseWithOperaSoldOutHotels_success() {
    // Arrange
    var availabilitiesResponse = buildHotelAvailabilitiesResponse();
    var hotels = buildMigrationStatusList();

    // Act
    AvailabilitiesResponseUtils.updateAvailabilitiesResponseWithOperaSoldOutHotels(availabilitiesResponse, hotels);
    // Assert
    assertEquals(12, availabilitiesResponse.getHotelAvailabilityList().size());

  }

  @Test
  void updateAvailabilitiesResponseWithSort() {
    // Arrange
    var availabilitiesResponse = buildHotelAvailabilitiesResponseSort();

    // Act
     AvailabilitiesResponseUtils.applySorting(availabilitiesResponse,null);
    // Assert
    assertFalse("OPERA_HOTEL3".equalsIgnoreCase(availabilitiesResponse.getHotelAvailabilityList().get(0).getHotelId()));
    assertEquals(0.5d,availabilitiesResponse.getHotelAvailabilityList().get(0).getDistance());
  }

  @Test
  void applyParkingFilters() {
    CacheSearchOutPort cacheSearchOutPort = mock(CacheSearchOutPort.class);
    ContentServiceOutPort contentServiceOutPort = mock(ContentServiceOutPort.class);

    String copHotelId = "cop";
    String cocHotelId = "coc";
    String cppHotelId = "cpp";
    String cpfHotelId = "cpf";

    HotelAvailabilityResponse responseCop = HotelAvailabilityResponse.builder().hotelId(copHotelId).build();
    HotelAvailabilityResponse responseCoc = HotelAvailabilityResponse.builder().hotelId(cocHotelId).build();
    HotelAvailabilityResponse responseCpp = HotelAvailabilityResponse.builder().hotelId(cppHotelId).build();
    HotelAvailabilityResponse responseCpf = HotelAvailabilityResponse.builder().hotelId(cpfHotelId).build();

    HotelAvailabilitiesRequest hotelAvailabilitiesRequest = HotelAvailabilitiesRequest.builder()
        .filters(new ArrayList<>(PARKING_FILTERS))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .subChannel("PI")
        .channel("WEB")
        .page(1)
        .pageSize(10)
        .country("gb")
        .language("en")
        .radius(10)
        .radiusUnit(RadiusUnitEnum.KILOMETERS)
        .adultsNumber(List.of(1))
        .childrenNumber(List.of(0))
        .roomTypes(List.of("DB"))
        .location("location")
        .locationFormat(LocationFormatEnum.PLACEID)
        .build();
    HotelAvailabilitiesResponse hotelAvailabilitiesResponse = HotelAvailabilitiesResponse.builder()
        .hotelAvailabilityList(List.of(responseCop, responseCoc, responseCpf, responseCpp)).build();

    doReturn(true).when(cacheSearchOutPort).checkCacheFacilitiesFilterAvailability(any());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(copHotelId)).build()).when(
        cacheSearchOutPort).getHotelIdsByFilter(CHARGEABLE_OFF_SITE_PARKING.getValue());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(cocHotelId)).build()).when(
        cacheSearchOutPort).getHotelIdsByFilter(CHARGEABLE_OFF_SITE_PARKING_ALT.getValue());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(cppHotelId)).build()).when(
        cacheSearchOutPort).getHotelIdsByFilter(CHARGEABLE_ON_SITE_PARKING.getValue());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(cpfHotelId)).build()).when(
        cacheSearchOutPort).getHotelIdsByFilter(FREE_PARKING.getValue());

    AvailabilitiesResponseUtils.applyFilters(cacheSearchOutPort, contentServiceOutPort, hotelAvailabilitiesRequest,
        hotelAvailabilitiesResponse);

    assertThat(hotelAvailabilitiesResponse.getHotelAvailabilityList(), containsInAnyOrder(responseCop, responseCoc, responseCpp, responseCpf));
  }

  @Test
  void applyFilters_parkingAndOther_noIntersectionBetweenThem() {
    CacheSearchOutPort cacheSearchOutPort = mock(CacheSearchOutPort.class);
    ContentServiceOutPort contentServiceOutPort = mock(ContentServiceOutPort.class);

    String copHotelId = "cop";
    String cppHotelId = "cpp";
    String cpfHotelId = "cpf";
    String otherHotelId = "other";
    String otherFilter = "otherFilter";

    HotelAvailabilityResponse responseCop = HotelAvailabilityResponse.builder().hotelId(copHotelId).build();
    HotelAvailabilityResponse responseCpp = HotelAvailabilityResponse.builder().hotelId(cppHotelId).build();
    HotelAvailabilityResponse responseCpf = HotelAvailabilityResponse.builder().hotelId(cpfHotelId).build();
    HotelAvailabilityResponse responseOther = HotelAvailabilityResponse.builder().hotelId(otherHotelId).build();

    HotelAvailabilitiesRequest hotelAvailabilitiesRequest = HotelAvailabilitiesRequest.builder()
        .filters(List.of(CHARGEABLE_OFF_SITE_PARKING.getValue(), CHARGEABLE_ON_SITE_PARKING.getValue(), FREE_PARKING.getValue(),
            otherFilter))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .subChannel("PI")
        .channel("WEB")
        .page(1)
        .pageSize(10)
        .country("gb")
        .language("en")
        .radius(10)
        .radiusUnit(RadiusUnitEnum.KILOMETERS)
        .adultsNumber(List.of(1))
        .childrenNumber(List.of(0))
        .roomTypes(List.of("DB"))
        .location("location")
        .locationFormat(LocationFormatEnum.PLACEID)
        .build();
    HotelAvailabilitiesResponse hotelAvailabilitiesResponse = HotelAvailabilitiesResponse.builder()
        .hotelAvailabilityList(List.of(responseCop, responseCpf, responseCpp, responseOther)).build();

    doReturn(true).when(cacheSearchOutPort).checkCacheFacilitiesFilterAvailability(any());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(copHotelId)).build()).when(
        cacheSearchOutPort).getHotelIdsByFilter(CHARGEABLE_OFF_SITE_PARKING.getValue());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(cppHotelId)).build()).when(
        cacheSearchOutPort).getHotelIdsByFilter(CHARGEABLE_ON_SITE_PARKING.getValue());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(cpfHotelId)).build()).when(
        cacheSearchOutPort).getHotelIdsByFilter(FREE_PARKING.getValue());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(otherHotelId)).build()).when(
        cacheSearchOutPort).getHotelIdsByFilter(otherFilter);

    AvailabilitiesResponseUtils.applyFilters(cacheSearchOutPort, contentServiceOutPort, hotelAvailabilitiesRequest,
        hotelAvailabilitiesResponse);

    assertThat(hotelAvailabilitiesResponse.getHotelAvailabilityList(), hasSize(0));
  }

  @Test
  void applyFilters_parkingAndOther_withIntersectionBetweenThem() {
    CacheSearchOutPort cacheSearchOutPort = mock(CacheSearchOutPort.class);
    ContentServiceOutPort contentServiceOutPort = mock(ContentServiceOutPort.class);

    String copHotelId = "cop";
    String cppHotelId = "cpp";
    String cpfHotelId = "cpf";
    String otherFilter = "otherFilter";

    HotelAvailabilityResponse responseCop = HotelAvailabilityResponse.builder().hotelId(copHotelId).build();
    HotelAvailabilityResponse responseCpp = HotelAvailabilityResponse.builder().hotelId(cppHotelId).build();
    HotelAvailabilityResponse responseCpf = HotelAvailabilityResponse.builder().hotelId(cpfHotelId).build();

    HotelAvailabilitiesRequest hotelAvailabilitiesRequest = HotelAvailabilitiesRequest.builder()
        .filters(List.of(CHARGEABLE_OFF_SITE_PARKING.getValue(), CHARGEABLE_ON_SITE_PARKING.getValue(), FREE_PARKING.getValue(),
            otherFilter))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .subChannel("PI")
        .channel("WEB")
        .page(1)
        .pageSize(10)
        .country("gb")
        .language("en")
        .radius(10)
        .radiusUnit(RadiusUnitEnum.KILOMETERS)
        .adultsNumber(List.of(1))
        .childrenNumber(List.of(0))
        .roomTypes(List.of("DB"))
        .location("location")
        .locationFormat(LocationFormatEnum.PLACEID)
        .build();
    HotelAvailabilitiesResponse hotelAvailabilitiesResponse = HotelAvailabilitiesResponse.builder()
        .hotelAvailabilityList(List.of(responseCop, responseCpf, responseCpp)).build();

    doReturn(true).when(cacheSearchOutPort).checkCacheFacilitiesFilterAvailability(any());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(copHotelId)).build()).when(
        cacheSearchOutPort).getHotelIdsByFilter(CHARGEABLE_OFF_SITE_PARKING.getValue());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(cppHotelId)).build()).when(
        cacheSearchOutPort).getHotelIdsByFilter(CHARGEABLE_ON_SITE_PARKING.getValue());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(cpfHotelId)).build()).when(
        cacheSearchOutPort).getHotelIdsByFilter(FREE_PARKING.getValue());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(copHotelId, cppHotelId)).build()).when(
        cacheSearchOutPort).getHotelIdsByFilter(otherFilter);

    AvailabilitiesResponseUtils.applyFilters(cacheSearchOutPort, contentServiceOutPort, hotelAvailabilitiesRequest,
        hotelAvailabilitiesResponse);

    assertThat(hotelAvailabilitiesResponse.getHotelAvailabilityList(), containsInAnyOrder(responseCop, responseCpp));
  }

  private List<HotelAvailabilitiesResponse> buildAvailabilitiesResponse() {
    List<HotelAvailabilitiesResponse> availabilitiesResponses = new ArrayList<>();

    HotelAvailabilitiesResponse firstResponse = HotelAvailabilitiesResponse
        .builder()
        .hotelAvailabilityList(List.of(
            buildHotelAvailabilityResponse("HOTEL1",BigDecimal.valueOf(100),OPERA_SOURCE),
            buildHotelAvailabilityResponse("HOTEL2",BigDecimal.valueOf(200),OPERA_SOURCE),
            buildHotelAvailabilityResponse("HOTEL3",BigDecimal.valueOf(255),OPERA_SOURCE),
            buildHotelAvailabilityResponse("HOTEL4",BigDecimal.valueOf(123),OPERA_SOURCE),
            buildHotelAvailabilityResponse("HOTEL5",BigDecimal.valueOf(152),OPERA_SOURCE)
        ))
        .page(1)
        .pageSize(40)
        .total(5)
        .build();

    HotelAvailabilitiesResponse secondResponse = HotelAvailabilitiesResponse
        .builder()
        .hotelAvailabilityList(List.of(
            buildHotelAvailabilityResponse("HOTEL1",BigDecimal.valueOf(330),OPERA_SOURCE),
            buildHotelAvailabilityResponse("HOTEL3",BigDecimal.valueOf(134),OPERA_SOURCE),
            buildHotelAvailabilityResponse("HOTEL4",BigDecimal.valueOf(124),OPERA_SOURCE)
        ))
        .page(1)
        .pageSize(40)
        .total(3)
        .build();

    availabilitiesResponses.add(firstResponse);
    availabilitiesResponses.add(secondResponse);

    return availabilitiesResponses;
  }

  private HotelAvailabilityResponse buildHotelAvailabilityResponse(String hotelId,
      BigDecimal roomRatePrice, String source) {
    return HotelAvailabilityResponse.builder()
        .hotelId(hotelId)
        .pmsSource(source)
        .available(Boolean.TRUE)
        .lowestRoomRate(Cost.builder()
            .currencyCode("GBP")
            .netTotal(roomRatePrice)
            .build())
        .build();
  }

  private List<HotelAvailabilityResponse> buildListResponse() {
    List<HotelAvailabilityResponse> availabilityResponses = new ArrayList<>();
    availabilityResponses.add(buildHotelAvailabilityResponse("HOTEL1",BigDecimal.valueOf(100),OPERA_SOURCE));
    availabilityResponses.add(buildHotelAvailabilityResponse("HOTEL2",BigDecimal.valueOf(200),OPERA_SOURCE));
    availabilityResponses.add(buildHotelAvailabilityResponse("HOTEL3",BigDecimal.valueOf(255),OPERA_SOURCE));
    availabilityResponses.add(buildHotelAvailabilityResponse("HOTEL4",BigDecimal.valueOf(123),OPERA_SOURCE));
    availabilityResponses.add(buildHotelAvailabilityResponse("HOTEL5",BigDecimal.valueOf(152),OPERA_SOURCE));
    return availabilityResponses;
  }

  private HotelAvailabilitiesResponse buildAvailabilitiesResponseHub() {
    return HotelAvailabilitiesResponse.builder()
        .hotelAvailabilityList(List.of(
            buildHotelAvailabilityResponse("HOTEL1",BigDecimal.valueOf(100),OPERA_SOURCE),
            buildHotelAvailabilityResponse("HOTEL2",BigDecimal.valueOf(200),OPERA_SOURCE),
            buildHotelAvailabilityResponse("HOTEL3",BigDecimal.valueOf(255),OPERA_SOURCE),
            buildHotelAvailabilityResponse("HOTEL4",BigDecimal.valueOf(123),OPERA_SOURCE),
            buildHotelAvailabilityResponse("HOTEL5",BigDecimal.valueOf(152),OPERA_SOURCE)
        ))
        .page(1)
        .pageSize(40)
        .total(5)
        .build();
  }

  @Test
  void applyFilters_noComplementaryFilter_doesNotCrash() {
    CacheSearchOutPort cacheSearchOutPort = mock(CacheSearchOutPort.class);
    ContentServiceOutPort contentServiceOutPort = mock(ContentServiceOutPort.class);

    String filterWithNoComplement = "WIFI";
    String matchingHotelId = "hotel-wifi";

    HotelAvailabilityResponse matchingHotel = HotelAvailabilityResponse.builder().hotelId(matchingHotelId).build();
    HotelAvailabilityResponse otherHotel = HotelAvailabilityResponse.builder().hotelId("hotel-other").build();

    HotelAvailabilitiesRequest request = HotelAvailabilitiesRequest.builder()
        .filters(List.of(filterWithNoComplement))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .subChannel("PI")
        .channel("WEB")
        .page(1)
        .pageSize(10)
        .country("gb")
        .language("en")
        .radius(10)
        .radiusUnit(RadiusUnitEnum.KILOMETERS)
        .adultsNumber(List.of(1))
        .childrenNumber(List.of(0))
        .roomTypes(List.of("DB"))
        .location("location")
        .locationFormat(LocationFormatEnum.PLACEID)
        .build();
    HotelAvailabilitiesResponse response = HotelAvailabilitiesResponse.builder()
        .hotelAvailabilityList(new ArrayList<>(List.of(matchingHotel, otherHotel))).build();

    doReturn(true).when(cacheSearchOutPort).checkCacheFacilitiesFilterAvailability(any());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(matchingHotelId)).build())
        .when(cacheSearchOutPort).getHotelIdsByFilter(filterWithNoComplement);

    AvailabilitiesResponseUtils.applyFilters(cacheSearchOutPort, contentServiceOutPort, request, response);

    assertThat(response.getHotelAvailabilityList(), containsInAnyOrder(matchingHotel));
  }

  @Test
  void applyFilters_nullReturnFromCache_doesNotCrash() {
    CacheSearchOutPort cacheSearchOutPort = mock(CacheSearchOutPort.class);
    ContentServiceOutPort contentServiceOutPort = mock(ContentServiceOutPort.class);

    HotelAvailabilitiesRequest request = HotelAvailabilitiesRequest.builder()
        .filters(List.of(ACO.getValue()))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .subChannel("PI")
        .channel("WEB")
        .page(1)
        .pageSize(10)
        .country("gb")
        .language("en")
        .radius(10)
        .radiusUnit(RadiusUnitEnum.KILOMETERS)
        .adultsNumber(List.of(1))
        .childrenNumber(List.of(0))
        .roomTypes(List.of("DB"))
        .location("location")
        .locationFormat(LocationFormatEnum.PLACEID)
        .build();
    HotelAvailabilitiesResponse response = HotelAvailabilitiesResponse.builder()
        .hotelAvailabilityList(new ArrayList<>(List.of(
            HotelAvailabilityResponse.builder().hotelId("hotel-1").build()))).build();

    doReturn(true).when(cacheSearchOutPort).checkCacheFacilitiesFilterAvailability(any());
    doReturn(null).when(cacheSearchOutPort).getHotelIdsByFilter(any());

    AvailabilitiesResponseUtils.applyFilters(cacheSearchOutPort, contentServiceOutPort, request, response);

    assertThat(response.getHotelAvailabilityList(), hasSize(1));
  }

  private HotelAvailabilitiesRequest buildAvailabilityRequest() {
    return HotelAvailabilitiesRequest.builder()
        .arrivalDate("2026-06-06")
        .departureDate("2026-06-11")
        .locationFormat(LocationFormatEnum.PLACEID)
        .location("ABCNDKEKFG")
        .channel("PI")
        .subChannel("WEB")
        .language("en")
        .country("gb")
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .adultsNumber(List.of(1))
        .childrenNumber(List.of(0))
        .radius(50)
        .radiusUnit(RadiusUnitEnum.KILOMETERS)
        .roomTypes(List.of("DB"))
        .page(1)
        .lazyLoadPageSize(10)
        .pageSize(10)
        .build();
  }

  private HotelAvailabilitiesRequest buildAvailabilityRequestExtraPage() {
    return HotelAvailabilitiesRequest.builder()
        .arrivalDate("2026-06-06")
        .departureDate("2026-06-11")
        .locationFormat(LocationFormatEnum.PLACEID)
        .location("ABCNDKEKFG")
        .channel("PI")
        .subChannel("WEB")
        .language("en")
        .country("gb")
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .adultsNumber(List.of(1))
        .childrenNumber(List.of(0))
        .radius(50)
        .radiusUnit(RadiusUnitEnum.KILOMETERS)
        .roomTypes(List.of("DB"))
        .page(10)
        .lazyLoadPageSize(20)
        .pageSize(10)
        .build();
  }

  private List<HotelMigrationStatusResponse> buildMigrationStatusList() {
    List<HotelMigrationStatusResponse> hotelMigrationStatusList = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      hotelMigrationStatusList.add(HotelMigrationStatusResponse.builder()
          .hotelId(OPERA_HOTEL + i)
          .onSale(true)
          .pmsSource(OPERA_SOURCE)
          .build());
    }

    return hotelMigrationStatusList;
  }

  private HotelAvailabilitiesResponse buildHotelAvailabilitiesResponse() {

   return HotelAvailabilitiesResponse
        .builder()
        .hotelAvailabilityList(List.of(
            buildHotelAvailabilityResponse("HOTEL1",BigDecimal.valueOf(100),OPERA_SOURCE),
            buildHotelAvailabilityResponse(OPERA_HOTEL + "2",BigDecimal.valueOf(200),OPERA_SOURCE),
            buildHotelAvailabilityResponse(OPERA_HOTEL + "3",BigDecimal.valueOf(255),OPERA_SOURCE),
            buildHotelAvailabilityResponse(OPERA_HOTEL + "4",BigDecimal.valueOf(123),OPERA_SOURCE),
            buildHotelAvailabilityResponse("HOTEL5",BigDecimal.valueOf(152),OPERA_SOURCE)
        ))
        .page(1)
        .pageSize(40)
        .total(5)
        .build();
  }
  private HotelAvailabilitiesResponse buildHotelAvailabilitiesResponseSort() {

    return HotelAvailabilitiesResponse
            .builder()
            .hotelAvailabilityList(List.of(
                    buildHotelAvailabilityResponsesort("HOTEL1",BigDecimal.valueOf(100),OPERA_SOURCE,3d,false),
                    buildHotelAvailabilityResponsesort(OPERA_HOTEL + "2",BigDecimal.valueOf(200),OPERA_SOURCE,0.5d,false),
                    buildHotelAvailabilityResponsesort(OPERA_HOTEL + "3",BigDecimal.valueOf(255),OPERA_SOURCE,30d,true),
                    buildHotelAvailabilityResponsesort(OPERA_HOTEL + "4",BigDecimal.valueOf(123),OPERA_SOURCE,5d,false),
                    buildHotelAvailabilityResponsesort("HOTEL5",BigDecimal.valueOf(152),OPERA_SOURCE,2d,false)
            ))
            .page(1)
            .pageSize(40)
            .total(5)
            .build();
  }

  @Test
  void getComplementaryFilters_returnsComplementsForKnownFilters() {
    assertThat(AvailabilitiesResponseUtils.getComplementaryFilters(ACO.getValue()), containsInAnyOrder(HAC.getValue()));
    assertThat(AvailabilitiesResponseUtils.getComplementaryFilters(HAC.getValue()), containsInAnyOrder(ACO.getValue()));
    assertThat(AvailabilitiesResponseUtils.getComplementaryFilters(LFT.getValue()), containsInAnyOrder(HUL.getValue()));
    assertThat(AvailabilitiesResponseUtils.getComplementaryFilters(HUL.getValue()), containsInAnyOrder(LFT.getValue()));
    assertThat(AvailabilitiesResponseUtils.getComplementaryFilters(RES.getValue()), containsInAnyOrder(DIN.getValue(), HRS.getValue()));
    assertThat(AvailabilitiesResponseUtils.getComplementaryFilters(DIN.getValue()), containsInAnyOrder(RES.getValue(), HRS.getValue()));
    assertThat(AvailabilitiesResponseUtils.getComplementaryFilters(HRS.getValue()), containsInAnyOrder(RES.getValue(), DIN.getValue()));
    assertThat(AvailabilitiesResponseUtils.getComplementaryFilters(HLG.getValue()), containsInAnyOrder(LUG.getValue()));
    assertThat(AvailabilitiesResponseUtils.getComplementaryFilters(LUG.getValue()), containsInAnyOrder(HLG.getValue()));
    assertThat(AvailabilitiesResponseUtils.getComplementaryFilters(HAR.getValue()), containsInAnyOrder(WET.getValue()));
    assertThat(AvailabilitiesResponseUtils.getComplementaryFilters(WET.getValue()), containsInAnyOrder(HAR.getValue()));
  }

  @Test
  void getComplementaryFilters_returnsEmptyForUnknownFilter() {
    assertThat(AvailabilitiesResponseUtils.getComplementaryFilters("UNKNOWN"), hasSize(0));
  }

  @Test
  void applyFilters_includesHotelMatchingComplementaryFilter() {
    CacheSearchOutPort cacheSearchOutPort = mock(CacheSearchOutPort.class);
    ContentServiceOutPort contentServiceOutPort = mock(ContentServiceOutPort.class);

    String hotelMatchingACO = "hotel-aco";
    String hotelMatchingHAC = "hotel-hac";
    String hotelNoMatch = "hotel-none";

    HotelAvailabilityResponse responseACO = HotelAvailabilityResponse.builder().hotelId(hotelMatchingACO).build();
    HotelAvailabilityResponse responseHAC = HotelAvailabilityResponse.builder().hotelId(hotelMatchingHAC).build();
    HotelAvailabilityResponse responseNone = HotelAvailabilityResponse.builder().hotelId(hotelNoMatch).build();

    HotelAvailabilitiesRequest request = HotelAvailabilitiesRequest.builder()
        .filters(List.of(ACO.getValue()))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .subChannel("PI")
        .channel("WEB")
        .page(1)
        .pageSize(10)
        .country("gb")
        .language("en")
        .radius(10)
        .radiusUnit(RadiusUnitEnum.KILOMETERS)
        .adultsNumber(List.of(1))
        .childrenNumber(List.of(0))
        .roomTypes(List.of("DB"))
        .location("location")
        .locationFormat(LocationFormatEnum.PLACEID)
        .build();
    HotelAvailabilitiesResponse response = HotelAvailabilitiesResponse.builder()
        .hotelAvailabilityList(new ArrayList<>(List.of(responseACO, responseHAC, responseNone))).build();

    doReturn(true).when(cacheSearchOutPort).checkCacheFacilitiesFilterAvailability(any());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(hotelMatchingACO)).build())
        .when(cacheSearchOutPort).getHotelIdsByFilter(ACO.getValue());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(hotelMatchingHAC)).build())
        .when(cacheSearchOutPort).getHotelIdsByFilter(HAC.getValue());

    AvailabilitiesResponseUtils.applyFilters(cacheSearchOutPort, contentServiceOutPort, request, response);

    assertThat(response.getHotelAvailabilityList(), containsInAnyOrder(responseACO, responseHAC));
  }

  @Test
  void applyFilters_includesHotelMatchingAnyOfThreeComplementaryFilters() {
    CacheSearchOutPort cacheSearchOutPort = mock(CacheSearchOutPort.class);
    ContentServiceOutPort contentServiceOutPort = mock(ContentServiceOutPort.class);

    String hotelRES = "hotel-res";
    String hotelDIN = "hotel-din";
    String hotelHRS = "hotel-hrs";
    String hotelNoMatch = "hotel-none";

    HotelAvailabilityResponse responseRES = HotelAvailabilityResponse.builder().hotelId(hotelRES).build();
    HotelAvailabilityResponse responseDIN = HotelAvailabilityResponse.builder().hotelId(hotelDIN).build();
    HotelAvailabilityResponse responseHRS = HotelAvailabilityResponse.builder().hotelId(hotelHRS).build();
    HotelAvailabilityResponse responseNone = HotelAvailabilityResponse.builder().hotelId(hotelNoMatch).build();

    HotelAvailabilitiesRequest request = HotelAvailabilitiesRequest.builder()
        .filters(List.of(RES.getValue()))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .subChannel("PI")
        .channel("WEB")
        .page(1)
        .pageSize(10)
        .country("gb")
        .language("en")
        .radius(10)
        .radiusUnit(RadiusUnitEnum.KILOMETERS)
        .adultsNumber(List.of(1))
        .childrenNumber(List.of(0))
        .roomTypes(List.of("DB"))
        .location("location")
        .locationFormat(LocationFormatEnum.PLACEID)
        .build();
    HotelAvailabilitiesResponse response = HotelAvailabilitiesResponse.builder()
        .hotelAvailabilityList(new ArrayList<>(List.of(responseRES, responseDIN, responseHRS, responseNone))).build();

    doReturn(true).when(cacheSearchOutPort).checkCacheFacilitiesFilterAvailability(any());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(hotelRES)).build())
        .when(cacheSearchOutPort).getHotelIdsByFilter(RES.getValue());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(hotelDIN)).build())
        .when(cacheSearchOutPort).getHotelIdsByFilter(DIN.getValue());
    doReturn(HotelsWithFilterModel.builder().hotelIds(List.of(hotelHRS)).build())
        .when(cacheSearchOutPort).getHotelIdsByFilter(HRS.getValue());

    AvailabilitiesResponseUtils.applyFilters(cacheSearchOutPort, contentServiceOutPort, request, response);

    assertThat(response.getHotelAvailabilityList(), containsInAnyOrder(responseRES, responseDIN, responseHRS));
  }

  private HotelAvailabilityResponse buildHotelAvailabilityResponsesort(String hotelId,
                                                                   BigDecimal roomRatePrice, String source,Double distance,Boolean hotelOpenSoon) {
    return HotelAvailabilityResponse.builder()
            .hotelId(hotelId)
            .pmsSource(source)
            .available(Boolean.TRUE)
            .distance(distance)
            .hotelOpeningSoon(hotelOpenSoon)
            .lowestRoomRate(Cost.builder()
                    .currencyCode("GBP")
                    .netTotal(roomRatePrice)
                    .build())
            .build();
  }
}
