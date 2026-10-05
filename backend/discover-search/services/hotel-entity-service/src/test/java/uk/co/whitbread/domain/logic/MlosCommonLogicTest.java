package uk.co.whitbread.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.availability.in.RestrictionsByDateRangeRequest;
import uk.co.whitbread.domain.model.availability.out.RestrictionControl;
import uk.co.whitbread.domain.model.availability.out.RestrictionSets;
import uk.co.whitbread.domain.model.availability.out.RestrictionStatus;
import uk.co.whitbread.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.feature.FeatureFlag.Feature;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.domain.ports.secondary.HotelAvailabilityOutPort;

@ExtendWith(MockitoExtension.class)
class MlosCommonLogicTest {

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapperMock;
  @Mock
  private HotelAvailabilityOutPort hotelAvailabilityOutPortMock;

  @InjectMocks
  private MlosCommonLogic mlosCommonLogic;

  @Test
  void isMlosEnabled_WhenFlagIsEnabledAndChannelIsCccui_ThenReturnTrue() {
    var featureFlagMock = mock(FeatureFlag.class);
    var featureMock = mock(Feature.class);
    when(featureFlagMock.getShowMlosCcui()).thenReturn(featureMock);
    when(unleashWrapperMock.featureFlag()).thenReturn(featureFlagMock);
    when(unleashWrapperMock.isEnabled(featureMock)).thenReturn(true);

    var isEnabled = mlosCommonLogic.isMlosEnabled("CCUI");

    assertTrue(isEnabled);
  }

  @Test
  void isMlosEnabled_WhenFlagIsEnabledAndChannelIsPi_ThenReturnFalse() {
    var isEnabled = mlosCommonLogic.isMlosEnabled("PI");

    assertFalse(isEnabled);
  }

  @Test
  void hasMlosRestriction_WhenHotelIsNotAvailable_ThenReturnFalse() {
    var roomRatesMock = mock(List.class);

    var result = mlosCommonLogic.hasMlosRestriction(
          "hotelId",
          "2025-01-01",
          "2025-01-10",
          false,
          roomRatesMock);

    assertFalse(result);
    verify(roomRatesMock, never()).isEmpty();
  }

  @Test
  void hasMlosRestriction_WhenHotelIsAvailableButThereAreRoomRates_ThenReturnFalse() {
    var roomRatesMock = mock(List.class);
    when(roomRatesMock.isEmpty()).thenReturn(false);

    var result = mlosCommonLogic.hasMlosRestriction(
          "hotelId",
          "2025-01-01",
          "2025-01-10",
          true,
          roomRatesMock);

    assertFalse(result);
  }

  @Test
  void hasMlosRestriction_WhenHotelIsAvailableButOhipResponseIsNull_ThenReturnFalse() {
    var roomRatesMock = mock(List.class);
    when(roomRatesMock.isEmpty()).thenReturn(true);
    when(hotelAvailabilityOutPortMock.getRestrictionsByDateRange(any())).thenReturn(null);

    var result = mlosCommonLogic.hasMlosRestriction(
          "hotelId",
          "2025-01-01",
          "2025-01-10",
          true,
          roomRatesMock);

    assertFalse(result);
  }

  @Test
  void hasMlosRestriction_WhenHotelIsAvailableButOhipRestrictionsAreNull_ThenReturnFalse() {
    var roomRatesMock = mock(List.class);
    when(roomRatesMock.isEmpty()).thenReturn(true);
    when(hotelAvailabilityOutPortMock.getRestrictionsByDateRange(any())).thenReturn(
          RestrictionsByDateRangeResult
                .builder()
                .restrictionSets(null)
                .build());

    var result = mlosCommonLogic.hasMlosRestriction(
          "hotelId",
          "2025-01-01",
          "2025-01-10",
          true,
          roomRatesMock);

    assertFalse(result);
  }

  @Test
  void hasMlosRestriction_WhenHotelIsAvailableButOhipRestrictionsAreEmpty_ThenReturnFalse() {
    var roomRatesMock = mock(List.class);
    when(roomRatesMock.isEmpty()).thenReturn(true);
    when(hotelAvailabilityOutPortMock.getRestrictionsByDateRange(any())).thenReturn(
          RestrictionsByDateRangeResult
                .builder()
                .restrictionSets(List.of())
                .build());

    var result = mlosCommonLogic.hasMlosRestriction(
          "hotelId",
          "2025-01-01",
          "2025-01-10",
          true,
          roomRatesMock);

    assertFalse(result);
  }

  @Test
  void hasMlosRestriction_WhenHotelIsAvailableButRestrictionCodeIsNotMlos_ThenReturnFalse() {
    var roomRatesMock = mock(List.class);
    when(roomRatesMock.isEmpty()).thenReturn(true);
    when(hotelAvailabilityOutPortMock.getRestrictionsByDateRange(any())).thenReturn(
          RestrictionsByDateRangeResult
                .builder()
                .restrictionSets(List.of(RestrictionSets.builder()
                            .restrictionControl(RestrictionControl.builder()
                                  .house(true)
                                  .build())
                            .restrictionStatus(RestrictionStatus.builder()
                                  .unit(2)
                                  .code("other")
                                  .build())
                      .build()))
                .build());

    var result = mlosCommonLogic.hasMlosRestriction(
          "hotelId",
          "2025-01-10",
          "2025-01-15",
          true,
          roomRatesMock);

    assertFalse(result);
  }

  @Test
  void hasMlosRestriction_WhenHotelIsAvailableButUnitIsSmallerThanNoOfNights_ThenReturnFalse() {
    var roomRatesMock = mock(List.class);
    when(roomRatesMock.isEmpty()).thenReturn(true);
    when(hotelAvailabilityOutPortMock.getRestrictionsByDateRange(any())).thenReturn(
          RestrictionsByDateRangeResult
                .builder()
                .restrictionSets(List.of(RestrictionSets.builder()
                      .restrictionControl(RestrictionControl.builder()
                            .house(true)
                            .build())
                      .restrictionStatus(RestrictionStatus.builder()
                            .unit(2)
                            .code("MinimumLengthOfStay")
                            .build())
                      .build()))
                .build());

    var result = mlosCommonLogic.hasMlosRestriction(
          "hotelId",
          "2025-01-10",
          "2025-01-15",
          true,
          roomRatesMock);

    assertFalse(result);
  }

  @Test
  void hasMlosRestriction_WhenHotelMlosRestrictionApplies_ThenReturnTrue() {
    var roomRatesMock = mock(List.class);
    when(roomRatesMock.isEmpty()).thenReturn(true);
    when(hotelAvailabilityOutPortMock.getRestrictionsByDateRange(any())).thenReturn(
          RestrictionsByDateRangeResult
                .builder()
                .restrictionSets(List.of(RestrictionSets.builder()
                      .restrictionControl(RestrictionControl.builder()
                            .house(true)
                            .build())
                      .restrictionStatus(RestrictionStatus.builder()
                            .unit(2)
                            .code("MinimumLengthOfStay")
                            .build())
                      .build()))
                .build());

    var result = mlosCommonLogic.hasMlosRestriction(
          "hotelId",
          "2025-01-10",
          "2025-01-11",
          true,
          roomRatesMock);

    assertTrue(result);
    ArgumentCaptor<RestrictionsByDateRangeRequest> requestArgumentCaptor = ArgumentCaptor.forClass(RestrictionsByDateRangeRequest.class);
    verify(hotelAvailabilityOutPortMock, times(1)).getRestrictionsByDateRange(requestArgumentCaptor.capture());
    var request = requestArgumentCaptor.getValue();
    assertEquals("hotelId", request.getHotelId());
    assertEquals("2025-01-10", request.getStartDate());
    assertEquals("2025-01-11", request.getEndDate());
  }

  @Test
  void hasMlosRestriction_WhenHotelHasMultipleRestrictionsAndOnlyOneApplies_ThenReturnTrue() {
    var roomRatesMock = mock(List.class);
    when(roomRatesMock.isEmpty()).thenReturn(true);
    when(hotelAvailabilityOutPortMock.getRestrictionsByDateRange(any())).thenReturn(
          RestrictionsByDateRangeResult
                .builder()
                .restrictionSets(List.of(
                      RestrictionSets.builder()
                      .restrictionControl(RestrictionControl.builder()
                            .house(true)
                            .build())
                      .restrictionStatus(RestrictionStatus.builder()
                            .unit(1)
                            .code("MinimumLengthOfStay")
                            .build())
                      .build(),
                      RestrictionSets.builder()
                      .restrictionControl(RestrictionControl.builder()
                            .house(false)
                            .build())
                      .restrictionStatus(RestrictionStatus.builder()
                            .unit(2)
                            .code("MinimumLengthOfStay")
                            .build())
                      .build()))
                .build());

    var result = mlosCommonLogic.hasMlosRestriction(
          "hotelId",
          "2025-01-10",
          "2025-01-11",
          true,
          roomRatesMock);

    assertTrue(result);
    ArgumentCaptor<RestrictionsByDateRangeRequest> requestArgumentCaptor = ArgumentCaptor.forClass(RestrictionsByDateRangeRequest.class);
    verify(hotelAvailabilityOutPortMock, times(1)).getRestrictionsByDateRange(requestArgumentCaptor.capture());
    var request = requestArgumentCaptor.getValue();
    assertEquals("hotelId", request.getHotelId());
    assertEquals("2025-01-10", request.getStartDate());
    assertEquals("2025-01-11", request.getEndDate());
  }

  @Test
  void hasMlosRestriction_WhenHotelHasBrokenRestrictionOnMultipleRates_ThenReturnTrue() {
    var roomRatesMock = mock(List.class);
    when(roomRatesMock.isEmpty()).thenReturn(true);
    when(hotelAvailabilityOutPortMock.getRestrictionsByDateRange(any())).thenReturn(
          RestrictionsByDateRangeResult
                .builder()
                .restrictionSets(List.of(
                      RestrictionSets.builder()
                            .restrictionControl(RestrictionControl.builder()
                                  .ratePlanCategory("U")
                                  .house(false)
                                  .build())
                            .restrictionStatus(RestrictionStatus.builder()
                                  .unit(1)
                                  .code("MinimumLengthOfStay")
                                  .build())
                            .build(),
                      RestrictionSets.builder()
                            .restrictionControl(RestrictionControl.builder()
                                  .ratePlanCategory("Z")
                                  .house(false)
                                  .build())
                            .restrictionStatus(RestrictionStatus.builder()
                                  .unit(2)
                                  .code("MinimumLengthOfStay")
                                  .build())
                            .build()))
                .build());

    var result = mlosCommonLogic.hasMlosRestriction(
          "hotelId",
          "2025-01-10",
          "2025-01-11",
          true,
          roomRatesMock);

    assertTrue(result);
    ArgumentCaptor<RestrictionsByDateRangeRequest> requestArgumentCaptor = ArgumentCaptor.forClass(RestrictionsByDateRangeRequest.class);
    verify(hotelAvailabilityOutPortMock, times(1)).getRestrictionsByDateRange(requestArgumentCaptor.capture());
    var request = requestArgumentCaptor.getValue();
    assertEquals("hotelId", request.getHotelId());
    assertEquals("2025-01-10", request.getStartDate());
    assertEquals("2025-01-11", request.getEndDate());
  }

  @Test
  void testGetRestrictionsMapForHotels_WhenCalled_ThenReturnMapSuccess() {
    // Arrange
    String hotelId = "LONEUS";
    List<RestrictionSets> restrictionsList = List.of(RestrictionSets.builder().build());
    var restrictions = List.of(
        RestrictionsByDateRangeResult.builder().build(),
        RestrictionsByDateRangeResult.builder().hotelId(hotelId).restrictionSets(restrictionsList).build()
    );
    when(hotelAvailabilityOutPortMock.getMultiHotelRestrictionsByDateRange(any())).thenReturn(restrictions);

    // Act
    var result = mlosCommonLogic.getRestrictionsMapForHotels(
        List.of("HEAPTI", hotelId), "2025-01-01", "2025-01-10");

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.containsKey(hotelId));
    assertSame(restrictionsList, result.get(hotelId).getRestrictionSets());
  }

  @Test
  void testGetRestrictionsMapForHotels_WhenCalledWithEmptyHotels_ThenDontCallOhip() {
    // Act
    var result = mlosCommonLogic.getRestrictionsMapForHotels(
        List.of(), "2025-01-01", "2025-01-10");

    // Assert
    verifyNoInteractions(hotelAvailabilityOutPortMock);
    assertTrue(result.isEmpty());
  }

}