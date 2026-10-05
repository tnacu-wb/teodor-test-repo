package uk.co.whitbread.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.domain.model.availability.in.BookingChannel.BB_BOOKING_CHANNEL;

import io.micrometer.tracing.Tracer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.co.whitbread.domain.exceptions.ErrorCode;
import uk.co.whitbread.domain.exceptions.InvalidChannelException;
import uk.co.whitbread.domain.model.availability.in.BookingChannel;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.domain.model.availability.in.RateV2;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityResultV2;
import uk.co.whitbread.domain.model.availability.out.PriceInfo;
import uk.co.whitbread.domain.model.availability.out.RestrictionSets;
import uk.co.whitbread.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.domain.model.availability.out.RoomRateInfoV2;
import uk.co.whitbread.domain.model.availability.out.RoomRateV2;
import uk.co.whitbread.domain.model.availability.out.RoomStay;
import uk.co.whitbread.domain.model.availability.out.RoomTypeV2;
import uk.co.whitbread.domain.model.distance.out.DistanceFromSearchResponse;
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelMigrationStatusResponse;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelsMigrationStatusResponse;
import uk.co.whitbread.domain.model.searchrules.out.RoomOccupancy;
import uk.co.whitbread.domain.model.searchrules.out.SearchRules;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.in.LocationFormatEnum;
import uk.co.whitbread.domain.model.srp.in.OldWorldChannelEnum;
import uk.co.whitbread.domain.model.srp.in.RadiusUnitEnum;
import uk.co.whitbread.domain.model.srp.out.Cost;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.domain.ports.secondary.CacheSearchOutPort;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.domain.ports.secondary.OnSaleFlagOutPort;
import uk.co.whitbread.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.hotel.content.generated.models.GlobalConfigDto;
import uk.co.whitbread.hotel.content.generated.models.HotelCityTaxDto;
import uk.co.whitbread.hotel.content.generated.models.HotelInformationExtendedDto;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@ExtendWith(MockitoExtension.class)
public class HotelAvailabilitiesInPortV2ImplTest {

  private static final String PI_CHANNEL_ID = "PI";
  private static final String CCUI_CHANNEL_ID = "CCUI";
  private static final String BB_CHANNEL_ID = "BB";
  private static final String DISTR_CHANNEL_ID = "DISTR";
  private static final int DEFAULT_PAGE_SIZE = 40;
  private static final int DEFAULT_LAZY_LOAD_PAGE_SIZE = 10;

  @Mock
  private AvailabilitiesResponseFromAvCache availabilitiesResponseFromAvCache;
  @Mock
  private RulesAgentValidations rulesAgentValidations;
  @Mock
  private CacheSearchOutPort cacheSearchOutPort;
  @Mock
  private ContentServiceOutPort contentServiceOutPort;
  @Mock
  private MlosCommonLogic mlosCommonLogic;
  @Mock
  private AuthenticatedUserService authenticatedUserService;
  @Mock
  private SnowdropInformation snowdropInformation;
  @Mock
  private OnSaleFlagOutPort onSaleFlagOutPortImpl;
  @Mock
  private AvailabilitiesFromOpera availabilitiesFromOpera;
  @Spy
  private ConcurrentTracer concurrentTracer = new ConcurrentTracer(Tracer.NOOP);
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  private RulesAgentOutPort rulesAgentOutPort;
  @InjectMocks
  private HotelAvailabilitiesInPortV2Impl hotelAvailabilitiesInPortV2;
  @InjectMocks
  private HotelAvailabilitiesInPortImpl hotelAvailabilitiesInPort;
  @Mock
  private HotelAvailabilityOutPort hotelAvailabilityOutboundPort;

  @Test
  public void testGetAvailabilitiesInvalidChannel_exception() {
    var request = buildHotelAvailabilitiesRequest("INVALID", null, List.of("DB"),
        1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(1), List.of(0), "2023-06-01", "2023-06-15",
        AvailabilitySortOption.AVAILABLE_FIRST.name());
    String message = "Invalid channel received: " + request.getChannel();
    var exception = assertThrows(InvalidChannelException.class, () -> hotelAvailabilitiesInPort.getAvailabilities(request));

    // Assert
    assertEquals(ErrorCode.DIGITAL_INVALID_CHANNEL_EXCEPTION.getCode(), exception.getErrorCode());
    assertEquals(ErrorCode.DIGITAL_INVALID_CHANNEL_EXCEPTION.getMessage(),
        exception.getGlobalErrTextTemplate());
    assertEquals(message, exception.getDebugMessage());

  }

  @Test
  void testGetAvailabilitiesInvalidChannel_exceptionV2() {
    var request = buildHotelAvailabilitiesRequest("INVALID", null, List.of("DB"),
        1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(1), List.of(0), "2023-06-01", "2023-06-15",
        AvailabilitySortOption.AVAILABLE_FIRST.name());
    String message = "Invalid channel received: " + request.getChannel();
    var exception = assertThrows(InvalidChannelException.class, () -> hotelAvailabilitiesInPortV2.getAvailabilities(request));

    // Assert
    assertEquals(ErrorCode.DIGITAL_INVALID_CHANNEL_2_EXCEPTION.getCode(), exception.getErrorCode());
    assertEquals(ErrorCode.DIGITAL_INVALID_CHANNEL_2_EXCEPTION.getMessage(),
        exception.getGlobalErrTextTemplate());
    assertEquals(message, exception.getDebugMessage());

  }

  @Test
  public void testGetAvailabilitiesPiV2_withPagination_success() {
    var requestFirstPage = buildHotelAvailabilitiesRequest(PI_CHANNEL_ID, null, List.of("DB"),
        1, 5, 10, List.of(1), List.of(0), "2023-06-01", "2023-06-15",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    var requestSecondPage = buildHotelAvailabilitiesRequest(PI_CHANNEL_ID, null, List.of("DB"),
        2, 5, 10, List.of(1), List.of(0), "2023-06-01", "2023-06-15",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    doNothing().when(rulesAgentValidations).validateBusinessRules(any(), eq(PI_CHANNEL_ID), eq(true));

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getShowMlosCcui())).thenReturn(false);

    var snowDropHotels = buildRandomSnowDropHotels(12);
    var snowDropHotels2 = buildRandomSnowDropHotels(12);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();


    var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId).toList();
    var operaAvailabilities1 = buildHotelAvailabilitiesResponse(
        operaHotels,
        operaHotelIds,
        buildPrices(operaHotelIds, 100));

    var operaAvailabilities2 = buildHotelAvailabilitiesResponse(
        operaHotels,
        operaHotelIds,
        buildPrices(operaHotelIds, 100));

    AvailabilitiesResponseUtils.updateAvCacheWithSnowdrop(requestFirstPage, operaAvailabilities1,
        snowDropHotels);

    AvailabilitiesResponseUtils.updateAvCacheWithSnowdrop(requestSecondPage, operaAvailabilities2,
        snowDropHotels2);

    when(availabilitiesResponseFromAvCache.getFullAvailabilitiesFromAvCache(
        requestFirstPage, true, false))
        .thenReturn(operaAvailabilities1);

    when(availabilitiesResponseFromAvCache.getFullAvailabilitiesFromAvCache(
        requestSecondPage, true, false))
        .thenReturn(operaAvailabilities2);

    var resultFirstPage = hotelAvailabilitiesInPortV2.getAvailabilities(requestFirstPage);
    var resultSecondPage = hotelAvailabilitiesInPortV2.getAvailabilities(requestSecondPage);

    assertNotNull(resultFirstPage);

    assertEquals(6, resultFirstPage.getTotal());
    assertEquals("HOTEL1", resultFirstPage.getHotelAvailabilityList().get(0).getHotelId());
    assertEquals("HOTEL3", resultFirstPage.getHotelAvailabilityList().get(1).getHotelId());
    assertEquals("HOTEL5", resultFirstPage.getHotelAvailabilityList().get(2).getHotelId());
    assertEquals("HOTEL7", resultFirstPage.getHotelAvailabilityList().get(3).getHotelId());
    assertEquals("HOTEL9", resultFirstPage.getHotelAvailabilityList().get(4).getHotelId());

    assertNotNull(resultSecondPage);

    assertEquals(6, resultSecondPage.getTotal());
    assertEquals("HOTEL11", resultSecondPage.getHotelAvailabilityList().get(0).getHotelId());
  }

  @ParameterizedTest
  @CsvSource({"true,DISTR,339", "false,DISTR,199", "true,PI,339", "false,PI,199"})
  void testGetAvailabilitiesPiAndDistrV2_addOccupancySupplement_success(boolean featureToggle,
      String channel, int expected) {

    //Arrange
    var availabilityRequest = buildHotelAvailabilitiesRequest(channel, null, List.of("DB"),
        1, 5, 10, List.of(2), List.of(0), "2023-06-01", "2023-06-15",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    doNothing().when(rulesAgentValidations).validateBusinessRules(availabilityRequest, channel, true);

    var snowDropHotels = buildRandomSnowDropHotels(12);
    var operaAvailabilities = getOperaAvailabilities(snowDropHotels);
    operaAvailabilities.getHotelAvailabilityList().get(0).setLowestRoomRate(null);

    AvailabilitiesResponseUtils.updateAvCacheWithSnowdrop(availabilityRequest, operaAvailabilities,
        snowDropHotels);

    when(availabilitiesResponseFromAvCache.getFullAvailabilitiesFromAvCache(availabilityRequest, true, false))
        .thenReturn(operaAvailabilities);

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag())
        .thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getOccupancySupplement()))
        .thenReturn(featureToggle);

    if (featureToggle) {
      var occSupplementMap = mockOccupancySupplementPrices(operaAvailabilities);
      when(rulesAgentOutPort.getMultiOccupancySupplementPricing(anyList()))
          .thenReturn(occSupplementMap);
    }

    // Act
    var availabilitiesResponse = hotelAvailabilitiesInPortV2.getAvailabilities(availabilityRequest);

    assertNotNull(availabilitiesResponse);
    assertTrue(availabilitiesResponse.getHotelAvailabilityList().stream()
        .filter(hotel -> Objects.nonNull(hotel.getLowestRoomRate()))
        .allMatch(hotel -> hotel.getLowestRoomRate().getNetTotal().compareTo(BigDecimal.valueOf(expected)) == 0));
  }

  @Test
  public void testGetAvailabilitiesDistrV2_withPagination_success() {
    var requestFirstPage = buildHotelAvailabilitiesRequest(DISTR_CHANNEL_ID, null, List.of("DB"),
        1, 5, 10, List.of(1), List.of(0), "2023-06-01", "2023-06-15",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    var requestSecondPage = buildHotelAvailabilitiesRequest(DISTR_CHANNEL_ID, null, List.of("DB"),
        2, 5, 10, List.of(1), List.of(0), "2023-06-01", "2023-06-15",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    doNothing().when(rulesAgentValidations).validateBusinessRules(any(), eq(DISTR_CHANNEL_ID), eq(true));

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getShowMlosCcui())).thenReturn(false);

    var snowDropHotels = buildRandomSnowDropHotels(12);
    var snowDropHotels2 = buildRandomSnowDropHotels(12);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();


    var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId).toList();
    var operaAvailabilities1 = buildHotelAvailabilitiesResponse(
        operaHotels,
        operaHotelIds,
        buildPrices(operaHotelIds, 100));

    var operaAvailabilities2 = buildHotelAvailabilitiesResponse(
        operaHotels,
        operaHotelIds,
        buildPrices(operaHotelIds, 100));

    AvailabilitiesResponseUtils.updateAvCacheWithSnowdrop(requestFirstPage, operaAvailabilities1,
        snowDropHotels);

    AvailabilitiesResponseUtils.updateAvCacheWithSnowdrop(requestSecondPage, operaAvailabilities2,
        snowDropHotels2);

    when(availabilitiesResponseFromAvCache.getFullAvailabilitiesFromAvCache(
        requestFirstPage, true, false))
        .thenReturn(operaAvailabilities1);

    when(availabilitiesResponseFromAvCache.getFullAvailabilitiesFromAvCache(
        requestSecondPage, true, false))
        .thenReturn(operaAvailabilities2);

    var resultFirstPage = hotelAvailabilitiesInPortV2.getAvailabilities(requestFirstPage);
    var resultSecondPage = hotelAvailabilitiesInPortV2.getAvailabilities(requestSecondPage);

    assertNotNull(resultFirstPage);

    assertEquals(6, resultFirstPage.getTotal());
    assertEquals("HOTEL1", resultFirstPage.getHotelAvailabilityList().get(0).getHotelId());
    assertEquals("HOTEL3", resultFirstPage.getHotelAvailabilityList().get(1).getHotelId());
    assertEquals("HOTEL5", resultFirstPage.getHotelAvailabilityList().get(2).getHotelId());
    assertEquals("HOTEL7", resultFirstPage.getHotelAvailabilityList().get(3).getHotelId());
    assertEquals("HOTEL9", resultFirstPage.getHotelAvailabilityList().get(4).getHotelId());

    assertNotNull(resultSecondPage);

    assertEquals(6, resultSecondPage.getTotal());
    assertEquals("HOTEL11", resultSecondPage.getHotelAvailabilityList().get(0).getHotelId());
  }

  @Test
  void testGetAvailabilitiesPiV2_emptyRoomType_success() {
    var request = buildHotelAvailabilitiesRequest(PI_CHANNEL_ID, null, List.of(),
            1, 5, 10, List.of(2), List.of(0), "2026-06-01", "2026-06-15",
            AvailabilitySortOption.AVAILABLE_FIRST.name());

    when(rulesAgentValidations.getSearchRulesByChannel(request.getChannel())).thenReturn(mockSearchRulesResponse(List.of(List.of("DB","TWIN")),List.of(2), List.of(0)));

    var snowDropHotels = buildRandomSnowDropHotels(12);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
            .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
            .toList();

    var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId).toList();
    var availabilitiesResponse1 = buildHotelAvailabilitiesResponse(
            operaHotels,
            operaHotelIds,
            buildPrices(operaHotelIds, 200));

    var availabilitiesResponse2 = buildHotelAvailabilitiesResponse(
            operaHotels,
            operaHotelIds,
            buildPrices(operaHotelIds, 2000));

    var request1 = request.toBuilder().roomTypes(List.of("DB")).build();
    var request2 = request.toBuilder().roomTypes(List.of("TWIN")).build();
    when(availabilitiesResponseFromAvCache.getFullAvailabilitiesFromAvCache(
            request1, true, false))
            .thenReturn(availabilitiesResponse1);
    when(availabilitiesResponseFromAvCache.getFullAvailabilitiesFromAvCache(
            request2, true, false))
            .thenReturn(availabilitiesResponse2);
    when(rulesAgentValidations.retrieveRoomTypesVariants(request,
            rulesAgentValidations.getSearchRulesByChannel(PI_CHANNEL_ID))).thenReturn(List.of(List.of("DB"), List.of("TWIN")));

    var lowestCostHotelAvailabilities = hotelAvailabilitiesInPortV2.getAvCacheResponseWhenRoomTypeEmpty(request, true, false);

    assertNotNull(lowestCostHotelAvailabilities);

    assertEquals(200, lowestCostHotelAvailabilities.getHotelAvailabilityList().get(0)
            .getLowestRoomRate().getNetTotal().intValue());
    assertEquals(300, lowestCostHotelAvailabilities.getHotelAvailabilityList().get(1)
            .getLowestRoomRate().getNetTotal().intValue());
  }

  @Test
  void testGetAvailabilitiesCcuiV2_notCached_noBatching_withPagination_mlosNotApplicable_success() {
    var requestFirstPage = buildHotelAvailabilitiesRequest(CCUI_CHANNEL_ID, "1401", List.of("DB"),
        1, 2, 2, List.of(1), List.of(0), "2023-06-01", "2023-06-03",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    var requestSecondPage = buildHotelAvailabilitiesRequest(CCUI_CHANNEL_ID, "1401", List.of("DB"),
        2, 2, 2, List.of(1), List.of(0), "2023-06-01", "2023-06-03",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    var requestThirdPage = buildHotelAvailabilitiesRequest(CCUI_CHANNEL_ID, "1401", List.of("DB"),
        3, 2, 2, List.of(1), List.of(0), "2023-06-01", "2023-06-03",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    doNothing().when(rulesAgentValidations).validateBusinessRules(any(), eq(CCUI_CHANNEL_ID), eq(true));

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getReleasePiCcuiCityTaxUk())).thenReturn(false);

    when(cacheSearchOutPort.getOperaAvailabilityFromCache(any()))
        .thenReturn(Collections.emptyList());
    var snowDropHotels = buildRandomSnowDropHotels(9);
    var snowDropHotels2 = buildRandomSnowDropHotels(9);
    var snowDropHotels3 = buildRandomSnowDropHotels(9);
    when(snowdropInformation.getHotelsFromSnowdrop(any()))
        .thenReturn(snowDropHotels)
        .thenReturn(snowDropHotels2)
        .thenReturn(snowDropHotels3);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(any()))
        .thenReturn(migrationStatus);

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels)))
        .thenReturn(buildHotelAvailabilitiesResponse(
            operaHotels,
            List.of("HOTEL1", "HOTEL3", "HOTEL5", "HOTEL7", "HOTEL9"),
            Map.of("HOTEL1", BigDecimal.valueOf(100),
                "HOTEL3", BigDecimal.valueOf(300),
                "HOTEL5", BigDecimal.valueOf(500),
                "HOTEL7", BigDecimal.valueOf(700),
                "HOTEL9", BigDecimal.valueOf(900))));

    doNothing().when(cacheSearchOutPort).saveOperaHotels(any(), anyList());
    when(mlosCommonLogic.isMlosEnabled(CCUI_CHANNEL_ID)).thenReturn(false);

    hotelAvailabilitiesInPortV2.ccuiSrpNegotiatedRateEnabled = true;
    var resultFirstPage = hotelAvailabilitiesInPortV2.getAvailabilities(requestFirstPage);
    var resultSecondPage = hotelAvailabilitiesInPortV2.getAvailabilities(requestSecondPage);
    var resultThirdPage = hotelAvailabilitiesInPortV2.getAvailabilities(requestThirdPage);

    assertNotNull(resultFirstPage);
    assertEquals(5, resultFirstPage.getTotal());
    assertEquals("HOTEL1", resultFirstPage.getHotelAvailabilityList().get(0).getHotelId());
    assertEquals("HOTEL3", resultFirstPage.getHotelAvailabilityList().get(1).getHotelId());

    assertNotNull(resultSecondPage);
    assertEquals(5, resultSecondPage.getTotal());
    assertEquals("HOTEL5", resultSecondPage.getHotelAvailabilityList().get(0).getHotelId());
    assertEquals("HOTEL7", resultSecondPage.getHotelAvailabilityList().get(1).getHotelId());

    assertNotNull(resultThirdPage);
    assertEquals(5, resultThirdPage.getTotal());
    assertEquals("HOTEL9", resultThirdPage.getHotelAvailabilityList().get(0).getHotelId());
  }

  @Test
  void testGetAvailabilitiesCcuiV2_mlosCalledOnlyForAvailableHotelsAndAddedOnResponse() {
    var requestFirstPage = buildHotelAvailabilitiesRequest(CCUI_CHANNEL_ID, "1401", List.of("DB"),
        1, 5, 5, List.of(1), List.of(0), "2023-06-01", "2023-06-03",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getReleasePiCcuiCityTaxUk())).thenReturn(false);

    doNothing().when(rulesAgentValidations).validateBusinessRules(any(), eq(CCUI_CHANNEL_ID), eq(true));
    when(cacheSearchOutPort.getOperaAvailabilityFromCache(any()))
        .thenReturn(Collections.emptyList());
    var snowDropHotels = buildRandomSnowDropHotels(9);
    when(snowdropInformation.getHotelsFromSnowdrop(any()))
        .thenReturn(snowDropHotels);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(any()))
        .thenReturn(migrationStatus);

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels)))
        .thenReturn(buildHotelAvailabilitiesResponse(
            operaHotels,
            List.of("HOTEL1", "HOTEL3", "HOTEL7", "HOTEL9"),
            Map.of("HOTEL1", BigDecimal.valueOf(100),
                "HOTEL3", BigDecimal.valueOf(300),
                "HOTEL7", BigDecimal.valueOf(700),
                "HOTEL9", BigDecimal.valueOf(900))));

    doNothing().when(cacheSearchOutPort).saveOperaHotels(any(), anyList());
    var hotelRestrictions = buildRestrictionsByDateResultMap("HOTEL1", "HOTEL3");
    when(mlosCommonLogic.isMlosEnabled(CCUI_CHANNEL_ID)).thenReturn(true);
    when(mlosCommonLogic.getRestrictionsMapForHotels(eq(List.of("HOTEL1", "HOTEL3", "HOTEL7", "HOTEL9")), any(), any()))
        .thenReturn(hotelRestrictions);
    when(mlosCommonLogic.isMlosRestrictionApplied(eq(hotelRestrictions.get("HOTEL1")), any(), any())).thenReturn(true);
    when(mlosCommonLogic.isMlosRestrictionApplied(eq(hotelRestrictions.get("HOTEL3")), any(), any())).thenReturn(false);

    hotelAvailabilitiesInPortV2.ccuiSrpNegotiatedRateEnabled = true;
    var resultFirstPage = hotelAvailabilitiesInPortV2.getAvailabilities(requestFirstPage);

    assertNotNull(resultFirstPage);
    assertEquals(5, resultFirstPage.getTotal());
    // assert for available hotel with mlos not applicable: mlos flag set as false
    var hotel3Index = 0;
    assertEquals("HOTEL3", resultFirstPage.getHotelAvailabilityList().get(hotel3Index).getHotelId());
    assertFalse(resultFirstPage.getHotelAvailabilityList().get(hotel3Index).getHasMlosRestriction());
    assertTrue(resultFirstPage.getHotelAvailabilityList().get(hotel3Index).getAvailable());
    assertNotNull(resultFirstPage.getHotelAvailabilityList().get(hotel3Index).getLowestRoomRate());

    // assert for available hotel with mlos applicable: is set as available=false and mlos flag set as true
    var hotel1Index = 3;
    assertEquals("HOTEL1", resultFirstPage.getHotelAvailabilityList().get(hotel1Index).getHotelId());
    assertTrue(resultFirstPage.getHotelAvailabilityList().get(hotel1Index).getHasMlosRestriction());
    assertFalse(resultFirstPage.getHotelAvailabilityList().get(hotel1Index).getAvailable());
    Assertions.assertNull(resultFirstPage.getHotelAvailabilityList().get(hotel1Index).getLowestRoomRate());

    // Hotel5 excluded from mlos calls since available=false
    verify(mlosCommonLogic, times(1))
        .getRestrictionsMapForHotels(eq(List.of("HOTEL1", "HOTEL3", "HOTEL7", "HOTEL9")), eq("2023-06-01"), eq("2023-06-03"));
  }

  @Test
  void testGetAvailabilitiesCcuiV2_mlosCalledOnlyForAvailableHotelsAndAddedOnResponse_SpecialCasesFlow() {
    var requestFirstPage = buildHotelAvailabilitiesRequest(CCUI_CHANNEL_ID, "1401", List.of("DB"),
        1, 10, 5, List.of(1), List.of(0), "2023-06-01", "2023-06-13",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    doNothing().when(rulesAgentValidations).validateBusinessRules(any(), eq(CCUI_CHANNEL_ID), eq(true));
    when(cacheSearchOutPort.getOperaAvailabilityFromCache(any()))
        .thenReturn(Collections.emptyList());
    var snowDropHotels = buildRandomSnowDropHotels(9);
    when(snowdropInformation.getHotelsFromSnowdrop(any()))
        .thenReturn(snowDropHotels);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(any()))
        .thenReturn(migrationStatus);

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels)))
        .thenReturn(buildHotelAvailabilitiesResponse(
            operaHotels,
            List.of("HOTEL1", "HOTEL3", "HOTEL7", "HOTEL9"),
            Map.of("HOTEL1", BigDecimal.valueOf(100),
                "HOTEL3", BigDecimal.valueOf(300),
                "HOTEL7", BigDecimal.valueOf(700),
                "HOTEL9", BigDecimal.valueOf(900))));

    doNothing().when(cacheSearchOutPort).saveOperaHotels(any(), anyList());
    var hotelRestrictions = buildRestrictionsByDateResultMap("HOTEL1", "HOTEL3");
    when(mlosCommonLogic.isMlosEnabled(CCUI_CHANNEL_ID)).thenReturn(true);
    when(mlosCommonLogic.getRestrictionsMapForHotels(eq(List.of("HOTEL1", "HOTEL3", "HOTEL7", "HOTEL9")), any(), any()))
        .thenReturn(hotelRestrictions);
    when(mlosCommonLogic.isMlosRestrictionApplied(eq(hotelRestrictions.get("HOTEL1")), any(), any())).thenReturn(true);
    when(mlosCommonLogic.isMlosRestrictionApplied(eq(hotelRestrictions.get("HOTEL3")), any(), any())).thenReturn(false);

    hotelAvailabilitiesInPortV2.ccuiSrpNegotiatedRateEnabled = true;
    var resultFirstPage = hotelAvailabilitiesInPortV2.getAvailabilities(requestFirstPage);

    assertNotNull(resultFirstPage);
    assertEquals(9, resultFirstPage.getTotal());
    // assert for available hotel with mlos applicable: is set as available=false and mlos flag set as true
    assertEquals("HOTEL1", resultFirstPage.getHotelAvailabilityList().get(0).getHotelId());
    assertTrue(resultFirstPage.getHotelAvailabilityList().get(0).getHasMlosRestriction());
    assertFalse(resultFirstPage.getHotelAvailabilityList().get(0).getAvailable());

    // assert for available hotel with mlos not applicable: mlos flag set as false
    assertEquals("HOTEL3", resultFirstPage.getHotelAvailabilityList().get(1).getHotelId());
    assertFalse(resultFirstPage.getHotelAvailabilityList().get(1).getHasMlosRestriction());
    assertTrue(resultFirstPage.getHotelAvailabilityList().get(1).getAvailable());

    // Hotel5 excluded from mlos calls since available=false
    verify(mlosCommonLogic, times(1))
        .getRestrictionsMapForHotels(eq(List.of("HOTEL1", "HOTEL3", "HOTEL7", "HOTEL9")), eq("2023-06-01"), eq("2023-06-13"));
  }

  @Test
  void testGetAvailabilitiesCcuiV2_notCached_noBatching_firstPage_noSnowoDropHotels_emptyResponse() {
    var request = buildHotelAvailabilitiesRequest(CCUI_CHANNEL_ID, "1401", List.of("DB"),
        1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(1), List.of(0), "2023-06-01", "2023-06-08",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    doNothing().when(rulesAgentValidations).validateBusinessRules(eq(request), eq(CCUI_CHANNEL_ID), eq(true));
    when(cacheSearchOutPort.getOperaAvailabilityFromCache(eq(request)))
        .thenReturn(Collections.emptyList());
    when(snowdropInformation.getHotelsFromSnowdrop(eq(request)))
        .thenReturn(Collections.emptyList());

    hotelAvailabilitiesInPortV2.ccuiSrpNegotiatedRateEnabled = true;

    var result = hotelAvailabilitiesInPortV2.getAvailabilities(request);

    assertNotNull(result);
    assertEquals(0, result.getHotelAvailabilityList().size());
  }

  @Test
  public void testGetAvailabilitiesCcuiV2_fromAvCache_success() {
    var request = buildHotelAvailabilitiesRequest(CCUI_CHANNEL_ID, null, List.of("DB"),
        1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(1), List.of(0), "2023-06-01", "2023-06-02",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    doNothing().when(rulesAgentValidations).validateBusinessRules(eq(request), eq(CCUI_CHANNEL_ID), eq(true));
    var snowDropHotels = buildRandomSnowDropHotels(6);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();


    var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId).toList();
    var operaAvailabilities = buildHotelAvailabilitiesResponse(
        operaHotels,
        operaHotelIds,
        buildPrices(operaHotelIds, 100));

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getShowMlosCcui())).thenReturn(false);

    AvailabilitiesResponseUtils.updateAvCacheWithSnowdrop(request, operaAvailabilities,
        snowDropHotels);

    when(availabilitiesResponseFromAvCache.getFullAvailabilitiesFromAvCache(
        request, false, false))
        .thenReturn(operaAvailabilities);

    var result = hotelAvailabilitiesInPortV2.getAvailabilities(request);

    assertNotNull(result);

    assertEquals(3, result.getTotal());
    assertEquals("HOTEL1", result.getHotelAvailabilityList().get(0).getHotelId());
    assertEquals("HOTEL3", result.getHotelAvailabilityList().get(1).getHotelId());
    assertEquals("HOTEL5", result.getHotelAvailabilityList().get(2).getHotelId());
  }

  @Test
  @Disabled
  public void testGetAvailabilitiesCcuiV2_notCached_withBatching_firstPage_success() {
    var request = buildHotelAvailabilitiesRequest(CCUI_CHANNEL_ID, null, List.of("DB"),
        1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(1), List.of(0), "2023-06-01", "2023-06-15",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    doNothing().when(rulesAgentValidations).validateBusinessRules(eq(request), eq(CCUI_CHANNEL_ID), eq(true));
    when(cacheSearchOutPort.getOperaAvailabilityFromCache(eq(request)))
        .thenReturn(Collections.emptyList());
    var snowDropHotels = buildRandomSnowDropHotels(200);
    when(snowdropInformation.getHotelsFromSnowdrop(eq(request)))
        .thenReturn(snowDropHotels);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(any()))
        .thenReturn(migrationStatus);

    var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId).toList();
    var operaAvailabilitiesFirstPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(0, 40),
        operaHotelIds.subList(0, 40),
        buildPrices(operaHotelIds.subList(0, 40), 100));

    var operaAvailabilitiesSecondPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(40, 80),
        operaHotelIds.subList(40, 80),
        buildPrices(operaHotelIds.subList(40, 80), 4100));

    var operaAvailabilitiesThirdPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(80, 100),
        operaHotelIds.subList(80, 100),
        buildPrices(operaHotelIds.subList(80, 100), 8100));

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(0, 40))))
        .thenReturn(operaAvailabilitiesFirstPage);

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(40, 80))))
        .thenReturn(operaAvailabilitiesSecondPage);

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(80, 100))))
        .thenReturn(operaAvailabilitiesThirdPage);

    doNothing().when(cacheSearchOutPort).saveOperaHotels(any(), anyList());

    var result = hotelAvailabilitiesInPortV2.getAvailabilities(request);

    assertNotNull(result);

    assertEquals(200, result.getTotal());
    assertEquals("HOTEL1", result.getHotelAvailabilityList().get(0).getHotelId());
    assertEquals("HOTEL3", result.getHotelAvailabilityList().get(1).getHotelId());
    assertEquals("HOTEL5", result.getHotelAvailabilityList().get(2).getHotelId());
    assertEquals("HOTEL7", result.getHotelAvailabilityList().get(3).getHotelId());
    assertEquals("HOTEL9", result.getHotelAvailabilityList().get(4).getHotelId());

    verify(availabilitiesFromOpera, times(1))
        .getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(0, 40)));
    verify(availabilitiesFromOpera, timeout(1000).times(1))
        .getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(40, 80)));

    verify(cacheSearchOutPort, times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesFirstPage.getHotelAvailabilityList()));

    verify(cacheSearchOutPort, timeout(1000).times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesSecondPage.getHotelAvailabilityList()));

    verify(cacheSearchOutPort, timeout(1000).times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesThirdPage.getHotelAvailabilityList()));
  }

  @Test
  void testGetAvailabilitiesCcuiV2_notCached_withBatching_sortDistance_firstPage_mlosDisabled_success() {
    var request = buildHotelAvailabilitiesRequest(CCUI_CHANNEL_ID, "1401", List.of("DB"),
        1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(1), List.of(0), "2023-06-01", "2023-06-08",
        AvailabilitySortOption.DISTANCE.name());

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getReleasePiCcuiCityTaxUk())).thenReturn(false);

    when(mlosCommonLogic.isMlosEnabled(any())).thenReturn(false);
    when(cacheSearchOutPort.getOperaAvailabilityFromCache(eq(request)))
        .thenReturn(Collections.emptyList());
    var snowDropHotels = buildRandomSnowDropHotels(200);
    when(snowdropInformation.getHotelsFromSnowdrop(eq(request)))
        .thenReturn(snowDropHotels);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(any())).thenReturn(migrationStatus);

    var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId).toList();
    var operaAvailabilitiesFirstPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(0, 40),
        operaHotelIds.subList(0, 40),
        buildPrices(operaHotelIds.subList(0, 40), 100));

    var operaAvailabilitiesSecondPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(40, 80),
        operaHotelIds.subList(40, 80),
        buildPrices(operaHotelIds.subList(40, 80), 4100));

    var operaAvailabilitiesThirdPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(80, 100),
        operaHotelIds.subList(80, 100),
        buildPrices(operaHotelIds.subList(80, 100), 8100));

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(0, 40))))
        .thenReturn(operaAvailabilitiesFirstPage);

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(40, 80))))
        .thenReturn(operaAvailabilitiesSecondPage);

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(80, 100))))
        .thenReturn(operaAvailabilitiesThirdPage);

    doNothing().when(cacheSearchOutPort).saveOperaHotels(any(), anyList());

    hotelAvailabilitiesInPortV2.ccuiSrpNegotiatedRateEnabled = true;

    var result = hotelAvailabilitiesInPortV2.getAvailabilities(request);

    assertNotNull(result);

    assertEquals(100, result.getTotal());

    var expectedOrderList = operaAvailabilitiesFirstPage.getHotelAvailabilityList()
        .stream().sorted(
            Comparator.nullsLast(Comparator.comparing(HotelAvailabilityResponse::getAvailable))
                .thenComparing(HotelAvailabilityResponse::getHotelOpeningSoon).reversed()
                .thenComparing(HotelAvailabilityResponse::getDistance))
        .map(HotelAvailabilityResponse::getHotelId)
        .limit(40)
        .toList();

    var orderResponseHotelIds = result.getHotelAvailabilityList()
        .stream().map(HotelAvailabilityResponse::getHotelId).toList();

    assertEquals(expectedOrderList, orderResponseHotelIds);

    verify(availabilitiesFromOpera, timeout(1000).times(1))
        .getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(0, 40)));
    verify(availabilitiesFromOpera, timeout(1000).times(1))
        .getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(40, 80)));

    verify(cacheSearchOutPort, timeout(1000).times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesFirstPage.getHotelAvailabilityList()));

    verify(cacheSearchOutPort, timeout(1000).times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesSecondPage.getHotelAvailabilityList()));

    verify(cacheSearchOutPort, timeout(1000).times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesThirdPage.getHotelAvailabilityList()));
    verifyNoMoreInteractions(mlosCommonLogic);
  }

  @Test
  public void testGetAvailabilitiesCcuiV2_notCached_withBatching_sortPrice_firstPage_success() {
    var request = buildHotelAvailabilitiesRequest(CCUI_CHANNEL_ID, "1401", List.of("DB"),
        1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(1), List.of(0), "2023-06-01", "2023-06-08",
        AvailabilitySortOption.PRICE.name());
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getReleasePiCcuiCityTaxUk())).thenReturn(false);
    doNothing().when(rulesAgentValidations).validateBusinessRules(eq(request), eq(CCUI_CHANNEL_ID), eq(true));
    when(cacheSearchOutPort.getOperaAvailabilityFromCache(eq(request)))
        .thenReturn(Collections.emptyList());
    var snowDropHotels = buildRandomSnowDropHotels(200);
    when(snowdropInformation.getHotelsFromSnowdrop(eq(request)))
        .thenReturn(snowDropHotels);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(any()))
        .thenReturn(migrationStatus);

    var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId).toList();
    var operaAvailabilitiesFirstPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(0, 40),
        operaHotelIds.subList(0, 40),
        buildPrices(operaHotelIds.subList(0, 40), 100));

    var operaAvailabilitiesSecondPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(40, 80),
        operaHotelIds.subList(40, 80),
        buildPrices(operaHotelIds.subList(40, 80), 4100));

    var operaAvailabilitiesThirdPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(80, 100),
        operaHotelIds.subList(80, 100),
        buildPrices(operaHotelIds.subList(80, 100), 8100));

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(0, 40))))
        .thenReturn(operaAvailabilitiesFirstPage);

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(40, 80))))
        .thenReturn(operaAvailabilitiesSecondPage);

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(80, 100))))
        .thenReturn(operaAvailabilitiesThirdPage);

    doNothing().when(cacheSearchOutPort).saveOperaHotels(any(), anyList());
    hotelAvailabilitiesInPortV2.ccuiSrpNegotiatedRateEnabled = true;
    when(mlosCommonLogic.isMlosEnabled(any())).thenReturn(false);

    var result = hotelAvailabilitiesInPortV2.getAvailabilities(request);

    assertNotNull(result);

    assertEquals(100, result.getTotal());

    var expectedOrderList = operaAvailabilitiesFirstPage.getHotelAvailabilityList()
        .stream().sorted(
            Comparator.nullsLast(Comparator.comparing(HotelAvailabilityResponse::getAvailable))
                .thenComparing(HotelAvailabilityResponse::getHotelOpeningSoon).reversed()
                .thenComparing(hotel -> hotel.getLowestRoomRate().getNetTotal()))
        .map(HotelAvailabilityResponse::getHotelId)
        .limit(40)
        .toList();

    var orderResponseHotelIds = result.getHotelAvailabilityList()
        .stream().map(HotelAvailabilityResponse::getHotelId).toList();

    assertEquals(expectedOrderList, orderResponseHotelIds);


    verify(availabilitiesFromOpera, times(1))
        .getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(0, 40)));
    verify(availabilitiesFromOpera, timeout(1000).times(1))
        .getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(40, 80)));

    verify(cacheSearchOutPort, times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesFirstPage.getHotelAvailabilityList()));

    verify(cacheSearchOutPort, timeout(1000).times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesSecondPage.getHotelAvailabilityList()));

    verify(cacheSearchOutPort, timeout(1000).times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesThirdPage.getHotelAvailabilityList()));
  }

  @Test
  public void testGetAvailabilitiesCcuiV2_cached_sortPrice_firstPage_success() {
    var request = buildHotelAvailabilitiesRequest(CCUI_CHANNEL_ID, "1401", List.of("DB"),
        1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(1), List.of(0), "2023-06-01", "2023-06-08",
        AvailabilitySortOption.PRICE.name());

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getReleasePiCcuiCityTaxUk())).thenReturn(false);

    doNothing().when(rulesAgentValidations).validateBusinessRules(eq(request), eq(CCUI_CHANNEL_ID), eq(true));

    var snowDropHotels = buildRandomSnowDropHotels(20);
    when(snowdropInformation.getHotelsFromSnowdrop(eq(request)))
        .thenReturn(snowDropHotels);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();


    var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId).toList();

    var operaAvailabilitiesFirstPage = buildHotelAvailabilitiesResponse(
        operaHotels,
        operaHotelIds,
        buildPrices(operaHotelIds, 100));

    when(cacheSearchOutPort.getOperaAvailabilityFromCache(eq(request)))
        .thenReturn(operaAvailabilitiesFirstPage.getHotelAvailabilityList().subList(0, 10));
    hotelAvailabilitiesInPortV2.ccuiSrpNegotiatedRateEnabled = true;

    var result = hotelAvailabilitiesInPortV2.getAvailabilities(request);

    assertNotNull(result);

    assertEquals(20, result.getTotal());
    assertEquals("HOTEL1", result.getHotelAvailabilityList().get(0).getHotelId());
    assertEquals("HOTEL3", result.getHotelAvailabilityList().get(1).getHotelId());
    assertEquals("HOTEL5", result.getHotelAvailabilityList().get(2).getHotelId());
    assertEquals("HOTEL7", result.getHotelAvailabilityList().get(3).getHotelId());
    assertEquals("HOTEL9", result.getHotelAvailabilityList().get(4).getHotelId());
  }

  @ParameterizedTest
  @CsvSource({"true,true,299", "false,false,199"})
  void testGetAvailabilitiesCcuiV2_addOccupancySupplement_success(boolean featureToggleOccupancy,
                                                                  boolean featureToggleMlos, int expected) {
    // Arrange
    var request = buildHotelAvailabilitiesRequest(CCUI_CHANNEL_ID, null, List.of("DB"),
        1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(2,2), List.of(0,0), "2023-06-01", "2023-06-06",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    doNothing().when(rulesAgentValidations).validateBusinessRules(request, CCUI_CHANNEL_ID,true);

    var snowDropHotels = buildRandomSnowDropHotels(6);
    var operaAvailabilities = getOperaAvailabilities(snowDropHotels);
    operaAvailabilities.getHotelAvailabilityList().get(0).setLowestRoomRate(null);

    AvailabilitiesResponseUtils.updateAvCacheWithSnowdrop(request, operaAvailabilities,
        snowDropHotels);

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getShowMlosCcui())).thenReturn(featureToggleMlos);

    when(availabilitiesResponseFromAvCache.getFullAvailabilitiesFromAvCache(
        request, false, featureToggleMlos))
        .thenReturn(operaAvailabilities);

    when(unleashWrapper.isEnabled(featureFlag.getOccupancySupplement()))
        .thenReturn(featureToggleOccupancy);
    if (featureToggleOccupancy) {
      var occSupplementMap = mockOccupancySupplementPrices(operaAvailabilities);
      when(rulesAgentOutPort.getMultiOccupancySupplementPricing(anyList()))
          .thenReturn(occSupplementMap);
    }

    // Act
    var availabilitiesResponse = hotelAvailabilitiesInPortV2.getAvailabilities(request);

    // Assert
    assertNotNull(availabilitiesResponse);
    assertTrue(availabilitiesResponse.getHotelAvailabilityList().stream()
        .filter(hotel -> Objects.nonNull(hotel.getLowestRoomRate()))
        .allMatch(hotel -> hotel.getLowestRoomRate().getNetTotal().compareTo(BigDecimal.valueOf(expected)) == 0));
  }

  @Test
  public void testGetAvailabilitiesBbV2_notCached_noBatching_withPagination_success() {
    var requestFirstPage = buildHotelAvailabilitiesRequest(BB_CHANNEL_ID, null, List.of("DB"),
        1, 2, 2, List.of(1), List.of(0), "2023-06-01", "2023-06-03",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    var requestSecondPage = buildHotelAvailabilitiesRequest(BB_CHANNEL_ID, null, List.of("DB"),
        2, 2, 2, List.of(1), List.of(0), "2023-06-01", "2023-06-03",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    var requestThirdPage = buildHotelAvailabilitiesRequest(BB_CHANNEL_ID, null, List.of("DB"),
        3, 2, 2, List.of(1), List.of(0), "2023-06-01", "2023-06-03",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getReleasePiCcuiCityTaxUk())).thenReturn(false);

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(mockAccount());
    CustomJwtAuthenticationToken jwtAuthMock = buildMockedJwtAuth();
    when(authenticatedUserService.getAuthenticatedUser())
        .thenReturn(jwtAuthMock);
    doNothing().when(rulesAgentValidations).validateBusinessRules(any(), eq(BB_CHANNEL_ID), eq(true));
    when(cacheSearchOutPort.getOperaAvailabilityFromCache(any()))
        .thenReturn(Collections.emptyList());
    var snowDropHotels = buildRandomSnowDropHotels(9);
    var snowDropHotels2 = buildRandomSnowDropHotels(9);
    var snowDropHotels3 = buildRandomSnowDropHotels(9);
    when(snowdropInformation.getHotelsFromSnowdrop(any()))
        .thenReturn(snowDropHotels)
        .thenReturn(snowDropHotels2)
        .thenReturn(snowDropHotels3);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
            .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(any()))
        .thenReturn(migrationStatus);

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels)))
        .thenReturn(buildHotelAvailabilitiesResponse(
            operaHotels,
            List.of("HOTEL1", "HOTEL3", "HOTEL5", "HOTEL7", "HOTEL9"),
            Map.of("HOTEL1", BigDecimal.valueOf(100),
                "HOTEL3", BigDecimal.valueOf(300),
                "HOTEL5", BigDecimal.valueOf(500),
                "HOTEL7", BigDecimal.valueOf(700),
                "HOTEL9", BigDecimal.valueOf(900))));

    doNothing().when(cacheSearchOutPort).saveOperaHotels(any(), anyList());
    when(mlosCommonLogic.isMlosEnabled(any())).thenReturn(false);

    var resultFirstPage = hotelAvailabilitiesInPortV2.getAvailabilities(requestFirstPage);
    var resultSecondPage = hotelAvailabilitiesInPortV2.getAvailabilities(requestSecondPage);
    var resultThirdPage = hotelAvailabilitiesInPortV2.getAvailabilities(requestThirdPage);

    assertNotNull(resultFirstPage);

    assertEquals(5, resultFirstPage.getTotal());
    assertEquals("HOTEL1", resultFirstPage.getHotelAvailabilityList().get(0).getHotelId());
    assertEquals("HOTEL3", resultFirstPage.getHotelAvailabilityList().get(1).getHotelId());

    assertNotNull(resultSecondPage);

    assertEquals(5, resultSecondPage.getTotal());
    assertEquals("HOTEL5", resultSecondPage.getHotelAvailabilityList().get(0).getHotelId());
    assertEquals("HOTEL7", resultSecondPage.getHotelAvailabilityList().get(1).getHotelId());

    assertNotNull(resultThirdPage);

    assertEquals(5, resultThirdPage.getTotal());
    assertEquals("HOTEL9", resultThirdPage.getHotelAvailabilityList().get(0).getHotelId());
  }

  @Test
  public void testGetAvailabilitiesBbV2_notCached_withBatching_firstPage_success() {
    var request = buildHotelAvailabilitiesRequest(BB_CHANNEL_ID, null, List.of("DB"),
        1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(1), List.of(0), "2023-06-01", "2023-06-03",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(mockAccount());
    CustomJwtAuthenticationToken jwtAuthMock = buildMockedJwtAuth();
    when(authenticatedUserService.getAuthenticatedUser())
        .thenReturn(jwtAuthMock);
    doNothing().when(rulesAgentValidations).validateBusinessRules(eq(request), eq(BB_CHANNEL_ID), eq(true));
    when(cacheSearchOutPort.getOperaAvailabilityFromCache(eq(request)))
        .thenReturn(Collections.emptyList());
    var snowDropHotels = buildRandomSnowDropHotels(200);
    when(snowdropInformation.getHotelsFromSnowdrop(eq(request)))
        .thenReturn(snowDropHotels);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getReleasePiCcuiCityTaxUk())).thenReturn(false);

    when(onSaleFlagOutPortImpl.getOnSaleFlag(any()))
        .thenReturn(migrationStatus);

    var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId).toList();
    var operaAvailabilitiesFirstPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(0, 40),
        operaHotelIds.subList(0, 40),
        buildPrices(operaHotelIds.subList(0, 40), 100));

    var operaAvailabilitiesSecondPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(40, 80),
        operaHotelIds.subList(40, 80),
        buildPrices(operaHotelIds.subList(40, 80), 4100));

    var operaAvailabilitiesThirdPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(80, 100),
        operaHotelIds.subList(80, 100),
        buildPrices(operaHotelIds.subList(80, 100), 8100));

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(0, 40))))
        .thenReturn(operaAvailabilitiesFirstPage);

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(40, 80))))
        .thenReturn(operaAvailabilitiesSecondPage);

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(80, 100))))
        .thenReturn(operaAvailabilitiesThirdPage);

    doNothing().when(cacheSearchOutPort).saveOperaHotels(any(), anyList());
    when(mlosCommonLogic.isMlosEnabled(any())).thenReturn(false);

    var result = hotelAvailabilitiesInPortV2.getAvailabilities(request);

    assertNotNull(result);

    assertEquals(100, result.getTotal());
    assertEquals("HOTEL1", result.getHotelAvailabilityList().get(0).getHotelId());
    assertEquals("HOTEL3", result.getHotelAvailabilityList().get(1).getHotelId());
    assertEquals("HOTEL5", result.getHotelAvailabilityList().get(2).getHotelId());
    assertEquals("HOTEL7", result.getHotelAvailabilityList().get(3).getHotelId());
    assertEquals("HOTEL9", result.getHotelAvailabilityList().get(4).getHotelId());

    verify(availabilitiesFromOpera, timeout(5000).times(1))
        .getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(0, 40)));
    verify(availabilitiesFromOpera, timeout(5000).times(1))
        .getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(40, 80)));

    verify(cacheSearchOutPort, timeout(5000).times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesSecondPage.getHotelAvailabilityList()));

    verify(cacheSearchOutPort, timeout(5000).times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesThirdPage.getHotelAvailabilityList()));
  }

  @Test
  public void testGetAvailabilitiesBbV2_notCached_withBatching_sortDistance_firstPage_success() {
    var request = buildHotelAvailabilitiesRequest(BB_CHANNEL_ID, null, List.of("DB"),
        1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(1), List.of(0), "2023-06-01", "2023-06-03",
        AvailabilitySortOption.DISTANCE.name());

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(mockAccount());
    CustomJwtAuthenticationToken jwtAuthMock = buildMockedJwtAuth();
    when(authenticatedUserService.getAuthenticatedUser())
        .thenReturn(jwtAuthMock);
    doNothing().when(rulesAgentValidations).validateBusinessRules(eq(request), eq(BB_CHANNEL_ID), eq(true));
    when(cacheSearchOutPort.getOperaAvailabilityFromCache(eq(request)))
        .thenReturn(Collections.emptyList());
    var snowDropHotels = buildRandomSnowDropHotels(200);
    when(snowdropInformation.getHotelsFromSnowdrop(eq(request)))
        .thenReturn(snowDropHotels);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getReleasePiCcuiCityTaxUk())).thenReturn(false);

    when(onSaleFlagOutPortImpl.getOnSaleFlag(any()))
        .thenReturn(migrationStatus);

    var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId).toList();
    var operaAvailabilitiesFirstPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(0, 40),
        operaHotelIds.subList(0, 40),
        buildPrices(operaHotelIds.subList(0, 40), 100));

    var operaAvailabilitiesSecondPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(40, 80),
        operaHotelIds.subList(40, 80),
        buildPrices(operaHotelIds.subList(40, 80), 4100));

    var operaAvailabilitiesThirdPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(80, 100),
        operaHotelIds.subList(80, 100),
        buildPrices(operaHotelIds.subList(80, 100), 8100));

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(0, 40))))
        .thenReturn(operaAvailabilitiesFirstPage);

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(40, 80))))
        .thenReturn(operaAvailabilitiesSecondPage);

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(80, 100))))
        .thenReturn(operaAvailabilitiesThirdPage);

    doNothing().when(cacheSearchOutPort).saveOperaHotels(any(), anyList());
    when(mlosCommonLogic.isMlosEnabled(any())).thenReturn(false);

    var result = hotelAvailabilitiesInPortV2.getAvailabilities(request);

    assertNotNull(result);

    assertEquals(100, result.getTotal());

    var expectedOrderList = operaAvailabilitiesFirstPage.getHotelAvailabilityList()
        .stream().sorted(
            Comparator.nullsLast(Comparator.comparing(HotelAvailabilityResponse::getAvailable))
                .thenComparing(HotelAvailabilityResponse::getHotelOpeningSoon).reversed()
                .thenComparing(HotelAvailabilityResponse::getDistance))
        .map(HotelAvailabilityResponse::getHotelId)
        .limit(40)
            .toList();

    var orderResponseHotelIds = result.getHotelAvailabilityList()
        .stream().map(HotelAvailabilityResponse::getHotelId).toList();

    assertEquals(expectedOrderList, orderResponseHotelIds);

    verify(availabilitiesFromOpera,  timeout(5000).times(1))
        .getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(0, 40)));
    verify(availabilitiesFromOpera, timeout(5000).times(1))
        .getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(40, 80)));

    verify(cacheSearchOutPort, timeout(5000).times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesFirstPage.getHotelAvailabilityList()));

    verify(cacheSearchOutPort, timeout(5000).times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesSecondPage.getHotelAvailabilityList()));

    verify(cacheSearchOutPort, timeout(5000).times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesThirdPage.getHotelAvailabilityList()));
  }

  @Test
  public void testGetAvailabilitiesBbV2_notCached_withBatching_sortPrice_firstPage_success() {
    var request = buildHotelAvailabilitiesRequest(BB_CHANNEL_ID, null, List.of("DB"),
        1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(1), List.of(0), "2023-06-01", "2023-06-03",
        AvailabilitySortOption.PRICE.name());

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(mockAccount());
    CustomJwtAuthenticationToken jwtAuthMock = buildMockedJwtAuth();
    when(authenticatedUserService.getAuthenticatedUser())
        .thenReturn(jwtAuthMock);
    doNothing().when(rulesAgentValidations).validateBusinessRules(eq(request), eq(BB_CHANNEL_ID), eq(true));
    when(cacheSearchOutPort.getOperaAvailabilityFromCache(eq(request)))
        .thenReturn(Collections.emptyList());
    var snowDropHotels = buildRandomSnowDropHotels(200);
    when(snowdropInformation.getHotelsFromSnowdrop(eq(request)))
        .thenReturn(snowDropHotels);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getReleasePiCcuiCityTaxUk())).thenReturn(false);

    when(onSaleFlagOutPortImpl.getOnSaleFlag(any()))
        .thenReturn(migrationStatus);

    var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId).toList();
    var operaAvailabilitiesFirstPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(0, 40),
        operaHotelIds.subList(0, 40),
        buildPrices(operaHotelIds.subList(0, 40), 100));

    var operaAvailabilitiesSecondPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(40, 80),
        operaHotelIds.subList(40, 80),
        buildPrices(operaHotelIds.subList(40, 80), 4100));

    var operaAvailabilitiesThirdPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(80, 100),
        operaHotelIds.subList(80, 100),
        buildPrices(operaHotelIds.subList(80, 100), 8100));

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(0, 40))))
        .thenReturn(operaAvailabilitiesFirstPage);

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(40, 80))))
        .thenReturn(operaAvailabilitiesSecondPage);

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(80, 100))))
        .thenReturn(operaAvailabilitiesThirdPage);

    doNothing().when(cacheSearchOutPort).saveOperaHotels(any(), anyList());
    when(mlosCommonLogic.isMlosEnabled(any())).thenReturn(false);

    var result = hotelAvailabilitiesInPortV2.getAvailabilities(request);

    assertNotNull(result);

    assertEquals(100, result.getTotal());

    var expectedOrderList = operaAvailabilitiesFirstPage.getHotelAvailabilityList()
        .stream().sorted(
            Comparator.nullsLast(Comparator.comparing(HotelAvailabilityResponse::getAvailable))
                .thenComparing(HotelAvailabilityResponse::getHotelOpeningSoon).reversed()
                .thenComparing(hotel -> hotel.getLowestRoomRate().getNetTotal()))
        .map(HotelAvailabilityResponse::getHotelId)
        .limit(40)
        .toList();

    var orderResponseHotelIds = result.getHotelAvailabilityList()
        .stream().map(HotelAvailabilityResponse::getHotelId).toList();

    assertEquals(expectedOrderList, orderResponseHotelIds);

    verify(availabilitiesFromOpera, timeout(5000).times(1))
        .getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(0, 40)));
    verify(availabilitiesFromOpera, timeout(5000).times(1))
        .getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(40, 80)));

    verify(cacheSearchOutPort, timeout(5000).times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesFirstPage.getHotelAvailabilityList()));

    verify(cacheSearchOutPort, timeout(5000).times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesSecondPage.getHotelAvailabilityList()));

    verify(cacheSearchOutPort, timeout(5000).times(1)).saveOperaHotels(eq(request),
        eq(operaAvailabilitiesThirdPage.getHotelAvailabilityList()));
  }

  @Test
  void testGetAvailabilitiesBbV2_notCached_withBatching_sortRecommendation_success() {

    try (MockedStatic<AvailabilitiesResponseUtils> availabilitiesResponseUtilsMock =
        Mockito.mockStatic(AvailabilitiesResponseUtils.class)) {

      availabilitiesResponseUtilsMock.when(() -> AvailabilitiesResponseUtils.applySorting(any(),
          any(), any())).thenAnswer(invocation -> null);
      availabilitiesResponseUtilsMock.when(() ->
              AvailabilitiesResponseUtils.flagHubHotelsAndUpdateForFamilyOrTwin(any(), any(), any()))
          .then(invocation -> {
            HotelAvailabilitiesResponse mergedResponse = invocation.getArgument(1);
            mergedResponse.getHotelAvailabilityList().get(1).setIsHub(true);
            return null;
          });

      var request = buildHotelAvailabilitiesRequest(BB_CHANNEL_ID, null, List.of("DB"),
          1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(1), List.of(0), "2023-06-01",
          "2023-06-03",
          AvailabilitySortOption.RECOMMENDATION.name());

      var mockAccount = mockAccount();
      when(authenticatedUserService.getCurrentUserAccount()).thenReturn(mockAccount);
      CustomJwtAuthenticationToken jwtAuthMock = buildMockedJwtAuth();
      when(authenticatedUserService.getAuthenticatedUser())
          .thenReturn(jwtAuthMock);
      doNothing().when(rulesAgentValidations)
          .validateBusinessRules(request, BB_CHANNEL_ID, true);
      when(cacheSearchOutPort.getOperaAvailabilityFromCache(request))
          .thenReturn(Collections.emptyList());
      var snowDropHotels = buildRandomSnowDropHotels(120);
      snowDropHotels.get(2).setBrand("HUB");
      when(snowdropInformation.getHotelsFromSnowdrop(request))
          .thenReturn(snowDropHotels);
      var migrationStatus = buildMigrationStatus(snowDropHotels);
      var operaHotels = migrationStatus.getMigrationStatusList()
          .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
          .toList();
      var featureFlag = new FeatureFlag();
      when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getReleasePiCcuiCityTaxUk())).thenReturn(false);

      when(onSaleFlagOutPortImpl.getOnSaleFlag(any()))
          .thenReturn(migrationStatus);

      var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId)
          .toList();
      var operaAvailabilitiesFirstPage = buildHotelAvailabilitiesResponse(
          operaHotels.subList(0, 40),
          operaHotelIds.subList(0, 40),
          buildPrices(operaHotelIds.subList(0, 40), 100));
      operaAvailabilitiesFirstPage.getHotelAvailabilityList().get(38).setHotelOpeningSoon(Boolean.TRUE);

      var account = mockAccount.get();

      when(availabilitiesFromOpera.getOperaHotelsAvailabilities(request, account, operaHotels.subList(0, 40)))
          .thenReturn(operaAvailabilitiesFirstPage);

      when(mlosCommonLogic.isMlosEnabled(any())).thenReturn(false);
      var result = hotelAvailabilitiesInPortV2.getAvailabilities(request);

      assertNotNull(result);

      assertEquals(60, result.getTotal());

      var expectedOrderList = getOrderedAvailabilitiesList(operaAvailabilitiesFirstPage);

      var orderResponseHotelIds = result.getHotelAvailabilityList()
          .stream().map(HotelAvailabilityResponse::getHotelId).toList();

      assertEquals(expectedOrderList, orderResponseHotelIds);

      verify(availabilitiesFromOpera, times(1))
          .getOperaHotelsAvailabilities(request, account, operaHotels.subList(0, 40));
      availabilitiesResponseUtilsMock.verify(() ->
          AvailabilitiesResponseUtils.flagHubHotelsAndUpdateForFamilyOrTwin(any(), any(), eq(List.of("HOTEL3"))),
          times(1));
      availabilitiesResponseUtilsMock
          .verify(() -> AvailabilitiesResponseUtils
                  .applySorting(argThat(mergedResponseArg ->
                      mergedResponseArg.getHotelAvailabilityList().get(1).getIsHub()), any(), any()),
              times(1));
    }
  }

  @NotNull
  private static List<String> getOrderedAvailabilitiesList(HotelAvailabilitiesResponse operaAvailabilitiesThirdPage) {
    return operaAvailabilitiesThirdPage.getHotelAvailabilityList().stream()
        .sorted(
            Comparator.nullsLast(Comparator.comparing(HotelAvailabilityResponse::getAvailable))
                .thenComparing(HotelAvailabilityResponse::getHotelOpeningSoon).reversed()
                .thenComparing(HotelAvailabilityResponse::getDistance))
        .map(HotelAvailabilityResponse::getHotelId)
        .toList();
  }

  @Test
  public void testGetAvailabilitiesBbV2_cached_sortPrice_firstPage_success() {
    var request = buildHotelAvailabilitiesRequest(BB_CHANNEL_ID, null, List.of("DB"),
        1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(1), List.of(0), "2023-06-01", "2023-06-03",
        AvailabilitySortOption.PRICE.name());

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(mockAccount());
    CustomJwtAuthenticationToken jwtAuthMock = buildMockedJwtAuth();
    when(authenticatedUserService.getAuthenticatedUser())
        .thenReturn(jwtAuthMock);
    doNothing().when(rulesAgentValidations).validateBusinessRules(eq(request), eq(BB_CHANNEL_ID), eq(true));

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getReleasePiCcuiCityTaxUk())).thenReturn(false);

    var snowDropHotels = buildRandomSnowDropHotels(20);
    when(snowdropInformation.getHotelsFromSnowdrop(eq(request)))
        .thenReturn(snowDropHotels);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();


    var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId).toList();

    var operaAvailabilitiesFirstPage = buildHotelAvailabilitiesResponse(
        operaHotels,
        operaHotelIds,
        buildPrices(operaHotelIds, 100));


    when(cacheSearchOutPort.getOperaAvailabilityFromCache(eq(request)))
        .thenReturn(operaAvailabilitiesFirstPage.getHotelAvailabilityList());

    var result = hotelAvailabilitiesInPortV2.getAvailabilities(request);

    assertNotNull(result);

    assertEquals(20, result.getTotal());
    assertEquals("HOTEL1", result.getHotelAvailabilityList().get(0).getHotelId());
    assertEquals("HOTEL3", result.getHotelAvailabilityList().get(1).getHotelId());
    assertEquals("HOTEL5", result.getHotelAvailabilityList().get(2).getHotelId());
    assertEquals("HOTEL7", result.getHotelAvailabilityList().get(3).getHotelId());
    assertEquals("HOTEL9", result.getHotelAvailabilityList().get(4).getHotelId());
  }

  @Test
  public void testGetAvailabilitiesBbV2_city_tax_success() {
    var request = buildHotelAvailabilitiesRequest(BB_CHANNEL_ID, null, List.of("DB"),
        1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(2), List.of(0), LocalDate.now().toString(), LocalDate.now().plusDays(5).toString(),
        AvailabilitySortOption.PRICE.name());

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(mockAccount());
    CustomJwtAuthenticationToken jwtAuthMock = buildMockedJwtAuth();
    when(authenticatedUserService.getAuthenticatedUser())
        .thenReturn(jwtAuthMock);
    doNothing().when(rulesAgentValidations).validateBusinessRules(eq(request), eq(BB_CHANNEL_ID), eq(true));

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getReleasePiCcuiCityTaxUk())).thenReturn(true);

    when(contentServiceOutPort.getGlobalConfig(null, null))
        .thenReturn(createMockGlobalConfigDto("HOTEL1"));
    when(contentServiceOutPort.getHotelInformation(null, null, "HOTEL1"))
        .thenReturn(createMockHotelInformationExtendedDto("2026-10-01"));

    when(hotelAvailabilityOutboundPort.getHotelAvailabilityByIdsV2(getHotelAvailabilityByIdsV2Request()))
        .thenReturn(getHotelAvailabilityByIdsV2());

    var snowDropHotels = buildRandomSnowDropHotels(20);
    when(snowdropInformation.getHotelsFromSnowdrop(eq(request)))
        .thenReturn(snowDropHotels);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();


    var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId).toList();

    var operaAvailabilitiesFirstPage = buildHotelAvailabilitiesResponse(
        operaHotels,
        operaHotelIds,
        buildPrices(operaHotelIds, 100));


    when(cacheSearchOutPort.getOperaAvailabilityFromCache(eq(request)))
        .thenReturn(operaAvailabilitiesFirstPage.getHotelAvailabilityList());

    var result = hotelAvailabilitiesInPortV2.getAvailabilities(request);

    assertNotNull(result);

    assertEquals(20, result.getTotal());
    assertEquals("HOTEL1", result.getHotelAvailabilityList().get(0).getHotelId());
    assertEquals(BigDecimal.valueOf(80.0), result.getHotelAvailabilityList().get(0).getLowestRoomRate().getNetTotal());
  }

  @Test
  void testGetAvailabilitiesCcuiV2_fromOpera_whenNightsGreaterThanNine_success() {
    var request = buildHotelAvailabilitiesRequest(CCUI_CHANNEL_ID, "1401", List.of("DB"),
        1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(1), List.of(0), "2023-06-01", "2023-06-20",
        AvailabilitySortOption.AVAILABLE_FIRST.name());

    doNothing().when(rulesAgentValidations).validateBusinessRules(eq(request), eq(CCUI_CHANNEL_ID), eq(true));
    var snowDropHotels = buildRandomSnowDropHotels(200);
    when(snowdropInformation.getHotelsFromSnowdrop(eq(request)))
        .thenReturn(snowDropHotels);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();

    when(onSaleFlagOutPortImpl.getOnSaleFlag(any()))
        .thenReturn(migrationStatus);

    var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId).toList();
    var operaAvailabilitiesFirstPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(0, 40),
        operaHotelIds.subList(0, 40),
        buildPrices(operaHotelIds.subList(0, 40), 100));

    when(availabilitiesFromOpera.getOperaHotelsAvailabilities(any(), any(), eq(operaHotels.subList(0, 40))))
        .thenReturn(operaAvailabilitiesFirstPage);

    doNothing().when(cacheSearchOutPort).saveOperaHotels(any(), anyList());
    when(mlosCommonLogic.isMlosEnabled(any())).thenReturn(false);

    var result = hotelAvailabilitiesInPortV2.getAvailabilities(request);

    assertNotNull(result);

    assertEquals(200, result.getTotal());
    assertEquals(BigDecimal.valueOf(100.0), result.getHotelAvailabilityList().get(0).getLowestRoomRate().getNetTotal());
  }

  @Test
  void testGetAvailabilitiesCcuiV2_fromOpera_whenNightsGreaterThanNineElse_success() {
    var request = buildHotelAvailabilitiesRequest(CCUI_CHANNEL_ID, "1401", List.of("DB"),
        1, DEFAULT_PAGE_SIZE, DEFAULT_LAZY_LOAD_PAGE_SIZE, List.of(1), List.of(0), "2023-06-01", "2023-06-20",
        AvailabilitySortOption.AVAILABLE_FIRST.name());
    var snowDropHotels = buildRandomSnowDropHotels(200);
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();
    var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId).toList();
    HotelAvailabilitiesResponse operaAvailabilitiesFirstPage = buildHotelAvailabilitiesResponse(
        operaHotels.subList(0, 40),
        operaHotelIds.subList(0, 40),
        buildPrices(operaHotelIds.subList(0, 40), 100));
    List<HotelAvailabilityResponse> expectedCacheResult = new ArrayList<>();

    expectedCacheResult.add(operaAvailabilitiesFirstPage.getHotelAvailabilityList().get(0));

    when(cacheSearchOutPort.getOperaAvailabilityFromCache(any())).thenReturn(expectedCacheResult);

    hotelAvailabilitiesInPortV2.ccuiSrpNegotiatedRateEnabled = true;
    when(mlosCommonLogic.isMlosEnabled(any())).thenReturn(false);

    var result = hotelAvailabilitiesInPortV2.getAvailabilities(request);

    var orderResponseHotelIds = result.getHotelAvailabilityList();

    assertNotNull(orderResponseHotelIds);

    assertNotNull(result);
  }

  private CustomJwtAuthenticationToken buildMockedJwtAuth() {
    var jwtMock = mock(Jwt.class);
    when(jwtMock.getTokenValue()).thenReturn("1234");
    return new CustomJwtAuthenticationToken(jwtMock, null);
  }

  private Map<String, BigDecimal> buildPrices(List<String> hotels, double start) {
    Map<String, BigDecimal> lowestRateMap = new HashMap<>();

    for (int i = 0; i < hotels.size(); i++) {
      lowestRateMap.put(hotels.get(i), BigDecimal.valueOf(start + (i * 100)));
    }

    return lowestRateMap;
  }

  private Map<String, BigDecimal> setPrices(List<String> hotels) {
    Map<String, BigDecimal> lowestRateMap = new HashMap<>();

    for (String hotel : hotels) {
      lowestRateMap.put(hotel, BigDecimal.valueOf(199));
    }

    return lowestRateMap;
  }

  private HotelAvailabilitiesResponse buildHotelAvailabilitiesResponse(
      List<HotelMigrationStatusResponse> hotels, List<String> availableHotels,
      Map<String, BigDecimal> pricesPerHotelLowestRate) {

    List<HotelAvailabilityResponse> availabilityResponses = new ArrayList<>();

    hotels.forEach(hotel ->
      availabilityResponses.add(HotelAvailabilityResponse.builder()
          .hotelId(hotel.getHotelId())
          .available(availableHotels.contains(hotel.getHotelId()))
          .lowestRoomRate(Cost.builder()
              .netTotal(pricesPerHotelLowestRate.get(hotel.getHotelId()))
              .currencyCode("EUR")
              .build())
          .build())
    );

    return HotelAvailabilitiesResponse.builder()
        .hotelAvailabilityList(availabilityResponses)
        .total(hotels.size())
        .build();
  }

  private HotelsMigrationStatusResponse buildMigrationStatus(
      List<DistanceFromSearchResponse> snowDropHotels) {
    List<HotelMigrationStatusResponse> hotelMigrationStatusList = new ArrayList<>();

    for (int i = 0; i < snowDropHotels.size(); i++) {
      hotelMigrationStatusList.add(HotelMigrationStatusResponse.builder()
              .hotelId(snowDropHotels.get(i).getHotelId())
              .onSale(true)
              .pmsSource(i % 2 == 0 ? "OPERA" : "")
          .build());
    }

    return HotelsMigrationStatusResponse.builder()
        .migrationStatusList(hotelMigrationStatusList)
        .build();
  }

  private List<DistanceFromSearchResponse> buildRandomSnowDropHotels(int size) {
    List<DistanceFromSearchResponse> hotels = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      hotels.add(DistanceFromSearchResponse.builder()
              .hotelId("HOTEL" + (i + 1))
              .name("HOTEL" + (i + 1))
              .distance(String.valueOf(i * 100 + 10))
          .build());
    }

    return hotels;
  }

  private Optional<Account> mockAccount() {
    return Optional.of(Account.builder()
        .build());
  }

  private HotelAvailabilitiesRequest buildHotelAvailabilitiesRequest(
      String channel, String companyId, List<String> roomTypes, int page, int pageSize, int lazyLoadPageSize,
      List<Integer> adults, List<Integer> children, String arrivalDate, String departureDate,
      String sort) {
    return HotelAvailabilitiesRequest.builder()
        .location("TEST LOCATION")
        .locationFormat(LocationFormatEnum.PLACEID)
        .radius(40)
        .radiusUnit(RadiusUnitEnum.MILES)
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .channel(channel)
        .subChannel("WEB")
        .companyId(companyId)
        .adultsNumber(adults)
        .childrenNumber(children)
        .roomTypes(roomTypes)
        .arrivalDate(arrivalDate)
        .departureDate(departureDate)
        .country("gb")
        .language("en")
        .page(page)
        .pageSize(pageSize)
        .lazyLoadPageSize(lazyLoadPageSize)
        .sort(sort)
        .rcPriceModifier(0.3f)
        .rcDistanceModifier(0.5f)
        .build();
  }

  private HotelAvailabilitiesResponse getOperaAvailabilities(
      List<DistanceFromSearchResponse> snowDropHotels) {
    var migrationStatus = buildMigrationStatus(snowDropHotels);
    var operaHotels = migrationStatus.getMigrationStatusList()
        .stream().filter(s -> "OPERA".equals(s.getPmsSource()))
        .toList();
    var operaHotelIds = operaHotels.stream().map(HotelMigrationStatusResponse::getHotelId).toList();
    return buildHotelAvailabilitiesResponse(
        operaHotels,
        operaHotelIds,
        setPrices(operaHotelIds));
  }

  private static HashMap<String, BigDecimal> mockOccupancySupplementPrices(
      HotelAvailabilitiesResponse operaAvailabilities) {
    var occSupplementMap = new HashMap<String,BigDecimal>();
    operaAvailabilities.getHotelAvailabilityList().forEach(hotel -> occSupplementMap.put(
        hotel.getHotelId(), BigDecimal.TEN));
    return occSupplementMap;
  }

  private static Map<String, RestrictionsByDateRangeResult> buildRestrictionsByDateResultMap(String... hotelId) {
    Map<String, RestrictionsByDateRangeResult> map = new HashMap<>();
    Arrays.stream(hotelId)
        .map(hotel ->
            RestrictionsByDateRangeResult.builder()
                .hotelId(hotel)
                .restrictionSets(List.of(RestrictionSets.builder().build()))
                .build())
        .forEach(restrictions -> map.put(restrictions.getHotelId(), restrictions));
    return map;
  }
  private SearchRules mockSearchRulesResponse(
          List<List<String>> roomTypes,
          List<Integer> adults,
          List<Integer> children) {

    Objects.requireNonNull(roomTypes, "roomTypes cannot be null");
    Objects.requireNonNull(adults, "adults cannot be null");
    Objects.requireNonNull(children, "children cannot be null");

    if (roomTypes.size() != adults.size() || adults.size() != children.size()) {
      throw new IllegalArgumentException("All lists should have the same size!");
    }

    List<RoomOccupancy> occupancies = new ArrayList<>(roomTypes.size());
    for (int i = 0; i < roomTypes.size(); i++) {
      occupancies.add(RoomOccupancy.builder()
              .acceptedRoomTypes(roomTypes.get(i))
              .adultsNumber(adults.get(i))
              .childrenNumber(children.get(i))
              .build());
    }
    return SearchRules.builder()
            .maxRooms(4)
            .maxNights(9)
            .maxArrivalDate(364)
            .roomOccupancies(occupancies)
            .build();
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

  private HotelAvailabilityByIdsV2Request getHotelAvailabilityByIdsV2Request() {
    LocalDate arrivalDate = LocalDate.now();
    LocalDate departureDate = arrivalDate.plusDays(5);
    return HotelAvailabilityByIdsV2Request.builder()
        .hotelIds(new ArrayList<>(List.of("HOTEL1")))
        .rooms(new ArrayList<>(List.of(uk.co.whitbread.domain.model.availability.in.Room.builder()
            .tag("DB")
            .adults(2)
            .children(0)
            .numberOfRooms(1)
            .build())))
        .arrivalDate(arrivalDate)
        .departureDate(departureDate)
        .rates(RateV2.builder()
            .ratePlanCodes(List.of("SEMIFLEX", "NONFLEX", "ADVANCE", "STANDARD"))
            .build())
        .bookingChannel(BookingChannel.builder()
            .channel(BB_BOOKING_CHANNEL)
            .build())
        .vatNotRequired(false)
        .build();
  }

  private HotelAvailabilityByIdsV2 getHotelAvailabilityByIdsV2() {
    return HotelAvailabilityByIdsV2.builder()
        .hotelAvailability(List.of(HotelAvailabilityResultV2.builder()
            .hotelId("HOTEL1")
            .roomStays(List.of(getRoomStaysV2()))
            .build()))
        .build();
  }

  private RoomStay getRoomStaysV2() {

    var roomRate1 = RoomRateV2.builder()
        .ratePlanCode("STANDARD")
        .displaySet(null)
        .currencyCode("GBP")
        .globalCompanyId("1234")
        .roomRateInfo(RoomRateInfoV2.builder()
            .priceInfo(List.of(PriceInfo.builder()
                .stayDate(LocalDate.now())
                .amountBeforeTax(BigDecimal.valueOf(132))
                .amountAfterTax(BigDecimal.valueOf(148))
                .build()))
            .build())
        .build();

    var roomRate2 = RoomRateV2.builder()
        .ratePlanCode("ADVANCE")
        .displaySet(null)
        .currencyCode("GBP")
        .globalCompanyId("1234")
        .roomRateInfo(RoomRateInfoV2.builder()
            .priceInfo(List.of(PriceInfo.builder()
                .stayDate(LocalDate.now())
                .amountBeforeTax(BigDecimal.valueOf(132))
                .amountAfterTax(BigDecimal.valueOf(148))
                .build()))
            .build())
        .build();

    var roomRate3 = RoomRateV2.builder()
        .ratePlanCode("NONFLEX")
        .displaySet(null)
        .currencyCode("GBP")
        .globalCompanyId("1234")
        .roomRateInfo(RoomRateInfoV2.builder()
            .priceInfo(List.of(PriceInfo.builder()
                .stayDate(LocalDate.now())
                .amountBeforeTax(BigDecimal.valueOf(80))
                .amountAfterTax(BigDecimal.valueOf(100))
                .build()))
            .build())
        .build();

    return uk.co.whitbread.domain.model.availability.out.RoomStay.builder()
        .roomClass("STANDARD")
        .roomTypes(new ArrayList<>(Collections.singletonList(
            RoomTypeV2.builder()
                .roomType("DOUBLE")
                .tag("DB")
                .roomRates(List.of(roomRate1, roomRate2, roomRate3)).build()))
        ).build();
  }
}
