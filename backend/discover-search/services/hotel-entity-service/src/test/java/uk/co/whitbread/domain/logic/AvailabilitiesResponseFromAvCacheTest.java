package uk.co.whitbread.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.distance.out.DistanceFromSearchResponse;
import uk.co.whitbread.domain.model.distance.out.HotelLocation;
import uk.co.whitbread.domain.model.migrationstatus.in.HotelsMigrationStatusRequest;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelMigrationStatusResponse;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelsMigrationStatusResponse;
import uk.co.whitbread.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitution;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitutionRequestDetails;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.in.LocationFormatEnum;
import uk.co.whitbread.domain.model.srp.in.OldWorldChannelEnum;
import uk.co.whitbread.domain.model.srp.in.RadiusUnitEnum;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.domain.ports.secondary.AvailabilityCacheV1SearchOutPort;
import uk.co.whitbread.domain.ports.secondary.OnSaleFlagOutPort;
import uk.co.whitbread.domain.ports.secondary.RulesAgentOutPort;

@ExtendWith(MockitoExtension.class)
class AvailabilitiesResponseFromAvCacheTest {

  @InjectMocks
  private AvailabilitiesResponseFromAvCache avCacheResponse;

  @Mock
  private AvailabilityCacheV1SearchOutPort avCacheOutPort;

  @Mock
  private RulesAgentOutPort rulesAgentOutPort;

  @Mock
  private OnSaleFlagOutPort onSaleFlagOutPortImpl;

  @Mock
  private SnowdropInformation snowdropInformation;

  @Test
  void test_getAvailabilitiesFromAvCache_noSnowdrop() {
    // Arrange
    var request = buildAvailabilityRequest();
    when(snowdropInformation.getHotelsFromSnowdrop(request))
        .thenReturn(new ArrayList<>());

    // Act
    var response = avCacheResponse.getFullAvailabilitiesFromAvCache(request, Boolean.FALSE, false);

    // Assert
    assertEquals(0, response.getHotelAvailabilityList().size());
  }

  @Test
  void test_getAvailabilitiesFromAvCache_returnsEmptyResponse() {
    // Arrange
    var request = buildAvailabilityRequest();
    List<String> roomList = List.of("DB","DBLWIN");

    when(snowdropInformation.getHotelsFromSnowdrop(request))
        .thenReturn(buildSnowdropResponse());

    when(rulesAgentOutPort.getRoomSubstitutionRule(buildRulesRequest()))
        .thenReturn(buildRoomSubstitutionRuleResponse());

    when(avCacheOutPort.getAvailabilitiesFromAvCache(request,
        new ArrayList<>(), List.of(roomList), false)).thenReturn(buildEmptyResponse());

    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
        .thenReturn(getMigrationStatusResponse());

    // Act
    var response = avCacheResponse.getFullAvailabilitiesFromAvCache(request, Boolean.TRUE, false);

    // Assert
    assertEquals(0, response.getHotelAvailabilityList().size());
    verifyNoMoreInteractions(avCacheOutPort);
    verifyNoMoreInteractions(rulesAgentOutPort);
  }

  @Test
  void test_getAvailabilitiesFromAvCacheCcui() {
    // Arrange
    var request = buildAvailabilityRequestCcui();
    List<String> roomList = List.of("DB","DBLWIN");

    when(rulesAgentOutPort.getRoomSubstitutionRule(buildRulesRequestCcui()))
        .thenReturn(buildRoomSubstitutionRuleResponse());
    when(snowdropInformation.getHotelsFromSnowdrop(request))
        .thenReturn(buildSnowdropResponse());
    when(avCacheOutPort.getAvailabilitiesFromAvCache(request,
        new ArrayList<>(), List.of(roomList), false)).thenReturn(buildAvCacheResponse());

    // Act
    var response = avCacheResponse.getFullAvailabilitiesFromAvCache(request, Boolean.FALSE, false);

    // Assert
    assertEquals(1, response.getHotelAvailabilityList().size());
    assertEquals("TTSSTT", response.getHotelAvailabilityList().get(0).getHotelId());
    verifyNoMoreInteractions(rulesAgentOutPort);
    verifyNoMoreInteractions(avCacheOutPort);
  }

  @Test
  void test_getAvailabilitiesWithNullArrivalAndDepartureDate() {
    // Arrange
    var request = buildAvailabilityRequestWithNullArrivalAndDepartureDate();

    when(snowdropInformation.getHotelsFromSnowdrop(request))
            .thenReturn(buildSnowdropResponse());
    when(onSaleFlagOutPortImpl.getOnSaleFlag(getMigrationStatusRequest()))
            .thenReturn(getMigrationStatusResponse());

    // Act
    var response = avCacheResponse.getFullAvailabilitiesFromAvCache(request, Boolean.FALSE, false);

    // Assert
    assertEquals(1, response.getHotelAvailabilityList().size());
    assertEquals("TTSSTT", response.getHotelAvailabilityList().get(0).getHotelId());
    verifyNoMoreInteractions(rulesAgentOutPort);
    verifyNoMoreInteractions(avCacheOutPort);
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

  private HotelAvailabilitiesRequest buildAvailabilityRequestWithNullArrivalAndDepartureDate() {
    return HotelAvailabilitiesRequest.builder()
            .arrivalDate(null)
            .departureDate(null)
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

  private HotelAvailabilitiesRequest buildAvailabilityRequestCcui() {
    return HotelAvailabilitiesRequest.builder()
        .arrivalDate("2026-06-06")
        .departureDate("2026-06-11")
        .locationFormat(LocationFormatEnum.PLACEID)
        .location("ABCNDKEKFG")
        .channel("CCUI")
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

  private HotelsMigrationStatusRequest getMigrationStatusRequest() {
    return HotelsMigrationStatusRequest.builder()
        .hotelIds(List.of("TTSSTT"))
        .build();
  }

  private HotelsMigrationStatusResponse getMigrationStatusResponse(){
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

  private List<DistanceFromSearchResponse> buildSnowdropResponse() {
    DistanceFromSearchResponse response = DistanceFromSearchResponse.builder()
        .hotelId("TTSSTT")
        .distance("7792")
        .brand("PI")
        .name("Some hotel")
        .location(HotelLocation.builder()
            .latitude(14.3)
            .longitude(123.2)
            .build())
        .build();
    return List.of(response);
  }

  private RoomSubstitutionRuleResponse buildRoomSubstitutionRuleResponse() {
    return RoomSubstitutionRuleResponse.builder()
        .requestDetails(RoomSubstitutionRequestDetails.builder()
            .adults(1)
            .children(0)
            .roomType("DB")
            .pms("OP")
            .channel("PI")
            .build())
        .generatedAt(Date.from(Instant.now()))
        .substitutionList(List.of(RoomSubstitution.builder()
                .type("DBLWIN")
                .silent(Boolean.TRUE)
                .specialRequest("SING")
            .build()))
        .build();
  }

  private RoomSubstitutionRuleRequest buildRulesRequest() {
    return RoomSubstitutionRuleRequest.builder()
        .adults(1)
        .children(0)
        .pms("OP")
        .roomType("DB")
        .channel("PI")
        .build();
  }

  private RoomSubstitutionRuleRequest buildRulesRequestCcui() {
    return RoomSubstitutionRuleRequest.builder()
        .adults(1)
        .children(0)
        .pms("OP")
        .roomType("DB")
        .channel("CCUI")
        .build();
  }

  private HotelAvailabilitiesResponse buildAvCacheResponse() {
    return HotelAvailabilitiesResponse.builder()
        .total(1)
        .page(1)
        .pageSize(10)
        .hotelAvailabilityList(List.of(HotelAvailabilityResponse.builder()
                .hotelId("TTSSTT")
                .available(Boolean.TRUE)
                .pmsSource("OPERA")
                .distance(0.5)
            .build()))
        .build();
  }

  private HotelAvailabilitiesResponse buildEmptyResponse() {
    return HotelAvailabilitiesResponse
        .builder()
        .hotelAvailabilityList(new ArrayList<>())
        .total(0)
        .page(1)
        .pageSize(10)
        .build();
  }
}
