package uk.co.whitbread.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.exceptions.RulesAgentBadRequestException;
import uk.co.whitbread.domain.exceptions.SearchRulesBadRequestException;
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.domain.model.rulesagent.out.MaxNightsRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomOccupancyData;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomOccupancyResponse;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomsRuleResponse;
import uk.co.whitbread.domain.model.searchrules.out.RoomOccupancy;
import uk.co.whitbread.domain.model.searchrules.out.SearchRules;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.in.LocationFormatEnum;
import uk.co.whitbread.domain.model.srp.in.OldWorldChannelEnum;
import uk.co.whitbread.domain.model.srp.in.RadiusUnitEnum;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.RulesAgentOutPort;

@ExtendWith(MockitoExtension.class)
class RulesAgentValidationTest {

  @InjectMocks
  private RulesAgentValidations rulesAgentValidations;

  @Mock
  private RulesAgentOutPort rulesAgentOutPort;

  @Mock
  private ContentServiceOutPort contentServiceOutPort;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Test
  void retrieveRoomTypesVariants_whenRoomTypeIsEmpty_success() {
    //Arrange
    var request =  buildHotelAvailabilitiesRequest(List.of(), List.of(2,1), List.of(0,1), "2025-01-02");

    when(contentServiceOutPort.getSearchRules(any(), any()))
            .thenReturn(mockSearchRulesResponseMultipleRooms(List.of(List.of("DB","TWIN","FAM","DIS"), List.of("FAM","DB")),List.of(2,1), List.of(0,1)));
    //Act
    var searchRules = contentServiceOutPort.getSearchRules(any(), any());
    var roomTypesListOfList = rulesAgentValidations.retrieveRoomTypesVariants(request, searchRules);
    //Assert
    Assertions.assertEquals(3, roomTypesListOfList.size());
    Assertions.assertEquals(List.of("DB", "FAM"), roomTypesListOfList.get(0));
    Assertions.assertEquals(List.of("TWIN","DB"), roomTypesListOfList.get(1));
    Assertions.assertEquals(List.of("FAM","FAM"), roomTypesListOfList.get(2));
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void validateRoomOccupanciesRule_ShouldReturnBadRequest(Boolean aemSearchRulesFf) {
    //Arrange
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

    var request = buildHotelAvailabilitiesRequest(List.of("FAM"), List.of(2), List.of(1),
        "2022-01-03");

    if (aemSearchRulesFf) {
      when(this.contentServiceOutPort.getSearchRules(any(), any()))
          .thenReturn(mockSearchRulesResponse(List.of("SB"),3, 2));
    } else {
      when(this.rulesAgentOutPort.getMaxRoomsRule(any(String.class)))
          .thenReturn(mockMaxRoomsRuleResponse());
      when(this.rulesAgentOutPort.getMaxNightsRule(any(String.class)))
          .thenReturn(mockMaxNightsRuleResponse());
      when(this.rulesAgentOutPort.getMaxRoomOccupancyRule(any(String.class)))
          .thenReturn(mockMaxRoomOccupancyRuleResponse( List.of("SB")));
    }

    //Act
    //Assert
    if (aemSearchRulesFf) {
      var exception = assertThrows(SearchRulesBadRequestException.class,
          () -> rulesAgentValidations.validateBusinessRules(request, "PI", true));
      assertThat(exception.getMessage(),
          is(String.format(
              "Error while trying validate Room Occupancy Rule AEM for hotelAvailabilityRequest=%s",
              request)));
    } else {
      var exception = assertThrows(RulesAgentBadRequestException.class,
          () -> rulesAgentValidations.validateBusinessRules(request, "PI", true));
      assertThat(exception.getMessage(),
          is(String.format(
              "Error while trying validate Room Occupancy Rule for hotelAvailabilityRequest=%s",
              request)));
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void validateMaxRoomRule_ShouldReturnBadRequest(Boolean aemSearchRulesFf) {
    //Arrange
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

    var request = buildHotelAvailabilitiesRequest(List.of("SB", "A", "B", "C", "D"), List.of(2),
        List.of(1), "2022-01-03");
    if (aemSearchRulesFf) {
      when(this.contentServiceOutPort.getSearchRules(any(), any()))
          .thenReturn(mockSearchRulesResponse(List.of("SB"),2, 1));
    } else {
      when(this.rulesAgentOutPort.getMaxRoomsRule(any(String.class)))
          .thenReturn(mockMaxRoomsRuleResponse());
    }

    //Act
    //Assert
    Exception exception;
    if (aemSearchRulesFf) {
      exception = assertThrows(SearchRulesBadRequestException.class,
          () -> rulesAgentValidations.validateBusinessRules(request, "PI", true));
      assertThat(exception.getMessage(),
          is(String.format(
              "Error while trying validate Max Rooms Rule AEM for hotelAvailabilityRequest=%s",
              request)));
    } else {
      exception = assertThrows(RulesAgentBadRequestException.class,
          () -> rulesAgentValidations.validateBusinessRules(request, "PI", true));
      assertThat(exception.getMessage(),
          is(String.format(
              "Error while trying validate Max Rooms Rule for hotelAvailabilityRequest=%s",
              request)));
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void validateMaxNightsRule_ShouldReturnBadRequest(Boolean aemSearchRulesFf) {
    //Arrange
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

    var request = buildHotelAvailabilitiesRequest(List.of("SB"), List.of(2), List.of(1), "2022-01-20");
    if (aemSearchRulesFf) {
      when(this.contentServiceOutPort.getSearchRules(any(), any()))
          .thenReturn(mockSearchRulesResponse(List.of("SB"),2, 1));
    } else {
      when(this.rulesAgentOutPort.getMaxRoomsRule(any(String.class)))
          .thenReturn(mockMaxRoomsRuleResponse());
      when(this.rulesAgentOutPort.getMaxNightsRule(any(String.class)))
          .thenReturn(mockMaxNightsRuleResponse());
    }

    //Act
    //Assert
    Exception exception;
    if (aemSearchRulesFf) {
      exception = assertThrows(SearchRulesBadRequestException.class,
          () -> rulesAgentValidations.validateBusinessRules(request, "PI", true));
      assertThat(exception.getMessage(),
          is(String.format(
              "Error while trying validate Max Nights Rule AEM for hotelAvailabilityRequest=%s",
              request)));
    } else {
      exception = assertThrows(RulesAgentBadRequestException.class,
          () -> rulesAgentValidations.validateBusinessRules(request, "PI", true));
      assertThat(exception.getMessage(),
          is(String.format(
              "Error while trying validate Max Nights Rule for hotelAvailabilityRequest=%s",
              request)));
    }
  }

  private MaxRoomOccupancyResponse mockMaxRoomOccupancyRuleResponse(List<String> roomTypes) {
    return MaxRoomOccupancyResponse.builder()
        .channelId("PI")
        .roomOccupancies(List.of(MaxRoomOccupancyData
            .builder()
            .acceptedRoomTypes(roomTypes)
            .adultsNumber(2)
            .childrenNumber(1)
            .build()))
        .build();
  }

  private MaxNightsRuleResponse mockMaxNightsRuleResponse() {
    return MaxNightsRuleResponse.builder()
        .maxNights(9)
        .build();
  }

  private MaxRoomsRuleResponse mockMaxRoomsRuleResponse() {
    return MaxRoomsRuleResponse.builder()
        .maxRooms(4)
        .build();
  }

  private SearchRules mockSearchRulesResponse(List<String> roomTypes, int adults, int children) {
    return SearchRules.builder()
        .maxRooms(4)
        .maxNights(9)
        .maxArrivalDate(364)
        .roomOccupancies(List.of(RoomOccupancy
            .builder()
            .acceptedRoomTypes(roomTypes)
            .adultsNumber(adults)
            .childrenNumber(children)
            .build()))
        .build();
  }

  private HotelAvailabilitiesRequest buildHotelAvailabilitiesRequest(List<String> roomTypes,
      List<Integer> adults, List<Integer> children, String departureDate) {
    return HotelAvailabilitiesRequest.builder()
        .location("TEST LOCATION")
        .locationFormat(LocationFormatEnum.PLACEID)
        .radius(40)
        .radiusUnit(RadiusUnitEnum.MILES)
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .channel("PI")
        .subChannel("WEB")
        .companyId(null)
        .adultsNumber(adults)
        .childrenNumber(children)
        .roomTypes(roomTypes)
        .arrivalDate("2022-01-01")
        .departureDate(departureDate)
        .country("gb")
        .language("en")
        .page(1)
        .pageSize(1)
        .build();
  }
  private SearchRules mockSearchRulesResponseMultipleRooms(
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
}
