package uk.co.whitbread.domain.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityRequest;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HotelAvailabilityCheckRulesTest {

  private final List<String> OTHER_ROOM_TYPES = Collections.singletonList("DB");
  private final List<String> TWIN_ROOM_TYPES = Collections.singletonList("TWIN");
  private final List<String> FAM_ROOM_TYPES = Collections.singletonList("FAM");

  @InjectMocks
  private HotelAvailabilityCheckRules hotelAvailabilityCheckRules;

  @Mock
  private ContentServiceOutPort contentServiceOutPort;

  @Test
  void availabilityCheckRules_HubHotelFamRoom_falseResponse() {
    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(FAM_ROOM_TYPES);

    when(contentServiceOutPort.getHotelBrand("HOTEL_ID")).thenReturn("HUB");

    // Act
    var checkRulesForHub = hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest);

    // Assert
    assertThat(checkRulesForHub, notNullValue());
    assertFalse(checkRulesForHub);
  }

  @Test
  void availabilityCheckRules_HubHotelTwinRoom_falseResponse() {
    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(TWIN_ROOM_TYPES);

    when(contentServiceOutPort.getHotelBrand("HOTEL_ID")).thenReturn("HUB");

    // Act
    var checkRulesForHub = hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest);

    // Assert
    assertThat(checkRulesForHub, notNullValue());
    assertFalse(checkRulesForHub);
  }

  @Test
  void availabilityCheckRules_HubHotelOtherRoom_successfulResponse() {
    // Arrange
    var hotelAvailabilityRequest = getHotelAvailabilityRequest(OTHER_ROOM_TYPES);

    when(contentServiceOutPort.getHotelBrand("HOTEL_ID")).thenReturn("HUB");

    // Act
    var checkRulesForHub = hotelAvailabilityCheckRules.fulfillHubRules(hotelAvailabilityRequest);

    // Assert
    assertThat(checkRulesForHub, notNullValue());
    assertTrue(checkRulesForHub);
  }

  private HotelAvailabilityRequest getHotelAvailabilityRequest(List<String> roomTypes) {

    return HotelAvailabilityRequest.builder()
            .hotelId("HOTEL_ID")
            .roomTypes(roomTypes)
            .adultsNumber(List.of(1))
            .arrivalDate("2025-12-03")
            .departureDate("2025-12-05")
            .childrenNumber(new ArrayList<>())
            .cotsRequired(new ArrayList<>())
            .build();
  }
}
