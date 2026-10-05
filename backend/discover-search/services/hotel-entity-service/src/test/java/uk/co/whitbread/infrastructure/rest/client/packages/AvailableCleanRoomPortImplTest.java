package uk.co.whitbread.infrastructure.rest.client.packages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.basket.out.Basket;
import uk.co.whitbread.domain.model.basket.out.BasketItem;
import uk.co.whitbread.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.domain.ports.secondary.BasketServiceOutPort;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.KioskPreferenceCollectionDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.KioskPreferenceDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.KioskReservationPreferencesDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.KioskRoomDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByBasketRefResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByIdDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayByIdDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomConditionValueDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomConditionDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.VacantRoomResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HousekeepingDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelRoomsDetailsDto;
import uk.co.whitbread.infrastructure.PreferenceProperties;
import uk.co.whitbread.infrastructure.rest.client.OhipClient;

@ExtendWith(MockitoExtension.class)
class AvailableCleanRoomPortImplTest {

  private static final String HOTEL_ID = "TKINPT";
  private static final String BASKET_REF = "basket-123";
  private static final String RESERVATION_ID = "res-1";
  private static final String ROOM_TYPE = "DLX";

  @InjectMocks
  private AvailableCleanRoomPortImpl availableCleanRoomPort;

  @Mock
  private PreferenceProperties preferenceProperties;

  @Mock
  private OhipClient ohipClient;

  @Mock
  private BasketServiceOutPort basketServiceOutPort;

  @ParameterizedTest(name = "basketReference={0}, basketType={1}, reservationInfoMissing={2}")
  @MethodSource("provideInputValidationScenarios")
  void availableCleanRooms_withInvalidInput_returnsZero(
      String basketReference, String basketType, boolean reservationInfoMissing) {

    var request = packagesRequestWithBasketReference(basketReference);

    if (basketType != null) {
      // No stay reservations scenario
      when(basketServiceOutPort.getBasket(basketReference))
          .thenReturn(Basket.builder()
              .bookingReference(basketReference)
              .items(List.of(BasketItem.builder().type(basketType).sourceId("res-x").build()))
              .build());
    } else if (reservationInfoMissing) {
      // Missing reservation info scenario
      when(basketServiceOutPort.getBasket(basketReference))
          .thenReturn(stayBasket(RESERVATION_ID));
      when(ohipClient.getReservationInfo(HOTEL_ID, List.of(RESERVATION_ID), false, false, false))
          .thenReturn(null);
    }
    // Blank basket reference: no mocking needed

    long result = availableCleanRoomPort.availableCleanRooms(request);

    assertEquals(0, result);
  }

  private static Stream<Arguments> provideInputValidationScenarios() {
    return Stream.of(
        // Blank basket reference
        Arguments.of(" ", null, false),
        // No stay reservations
        Arguments.of("basket-123", "OTHER", false),
        // Missing reservation info
        Arguments.of("basket-123", null, true)
    );
  }

  @Test
  void availableCleanRooms_returnsOneWhenVacantRoomExistsAndGuestHasNoPreferences() {
    var request = packagesRequestWithBasketReference(BASKET_REF);
    when(basketServiceOutPort.getBasket(BASKET_REF)).thenReturn(stayBasket(RESERVATION_ID));
    when(ohipClient.getReservationInfo(HOTEL_ID, List.of(RESERVATION_ID), false, false, false))
        .thenReturn(reservationInfoWithRoomType(RESERVATION_ID, ROOM_TYPE));
    when(ohipClient.getVacantRooms(HOTEL_ID, ROOM_TYPE))
        .thenReturn(vacantRoomWithNoCondition("101"));
    when(ohipClient.fetchReservationPreferences(HOTEL_ID, RESERVATION_ID)).thenReturn(null);

    long result = availableCleanRoomPort.availableCleanRooms(request);

    assertEquals(1, result);
  }

  @Test
  void availableCleanRooms_returnsOneWhenMappedPreferenceMatchesVacantRoomCondition() {
    var request = packagesRequestWithBasketReference(BASKET_REF);
    when(basketServiceOutPort.getBasket(BASKET_REF)).thenReturn(stayBasket(RESERVATION_ID));
    when(ohipClient.getReservationInfo(HOTEL_ID, List.of(RESERVATION_ID), false, false, false))
        .thenReturn(reservationInfoWithRoomType(RESERVATION_ID, ROOM_TYPE));
    when(ohipClient.getVacantRooms(HOTEL_ID, ROOM_TYPE))
        .thenReturn(vacantRoomsWithCondition("101", "COND-MATCH"));
    when(ohipClient.fetchReservationPreferences(HOTEL_ID, RESERVATION_ID))
        .thenReturn(preferencesWithValues("HIGHFLR"));
    when(preferenceProperties.getCondition()).thenReturn(Map.of("HIGHFLR", "COND-MATCH"));

    long result = availableCleanRoomPort.availableCleanRooms(request);

    assertEquals(1, result);
  }

  @Test
  void availableCleanRooms_returnsZeroWhenMappedPreferenceDoesNotMatchVacantRoomCondition() {
    var request = packagesRequestWithBasketReference(BASKET_REF);
    when(basketServiceOutPort.getBasket(BASKET_REF)).thenReturn(stayBasket(RESERVATION_ID));
    when(ohipClient.getReservationInfo(HOTEL_ID, List.of(RESERVATION_ID), false, false, false))
        .thenReturn(reservationInfoWithRoomType(RESERVATION_ID, ROOM_TYPE));
    when(ohipClient.getVacantRooms(HOTEL_ID, ROOM_TYPE))
        .thenReturn(vacantRoomsWithCondition("101", "COND-ROOM"));
    when(ohipClient.fetchReservationPreferences(HOTEL_ID, RESERVATION_ID))
        .thenReturn(preferencesWithValues("HIGHFLR"));
    when(preferenceProperties.getCondition()).thenReturn(Map.of("HIGHFLR", "COND-OTHER"));

    long result = availableCleanRoomPort.availableCleanRooms(request);

    assertEquals(0, result);
  }

  @Test
  void availableCleanRooms_returnsOneWhenCotPreferenceIsMappedWithCotSuffix() {
    var request = packagesRequestWithBasketReference(BASKET_REF);
    when(basketServiceOutPort.getBasket(BASKET_REF)).thenReturn(stayBasket(RESERVATION_ID));
    when(ohipClient.getReservationInfo(HOTEL_ID, List.of(RESERVATION_ID), false, false, false))
        .thenReturn(reservationInfoWithRoomType(RESERVATION_ID, ROOM_TYPE));
    when(ohipClient.getVacantRooms(HOTEL_ID, ROOM_TYPE))
        .thenReturn(vacantRoomsWithCondition("101", "COND-COT"));
    when(ohipClient.fetchReservationPreferences(HOTEL_ID, RESERVATION_ID))
        .thenReturn(preferencesWithValues("HIGHFLR", "COTR"));
    when(preferenceProperties.getCondition()).thenReturn(
        Map.of("HIGHFLRCOTR", "COND-COT"));

    long result = availableCleanRoomPort.availableCleanRooms(request);

    assertEquals(1, result);
  }

  @Test
  void availableCleanRooms_withCotRequiredAndNullPreferenceEntries_returnsMatchingRoomCount() {
    var request = packagesRequestWithBasketReference(BASKET_REF);
    when(basketServiceOutPort.getBasket(BASKET_REF)).thenReturn(stayBasket(RESERVATION_ID));
    when(ohipClient.getReservationInfo(HOTEL_ID, List.of(RESERVATION_ID), false, false, false))
        .thenReturn(reservationInfoWithRoomType(RESERVATION_ID, ROOM_TYPE));
    when(ohipClient.getVacantRooms(HOTEL_ID, ROOM_TYPE))
        .thenReturn(vacantRoomsWithCondition("101", "COND-COT"));

    var prefCollection = new KioskPreferenceCollectionDto();
    var prefList = new java.util.ArrayList<KioskPreferenceDto>();
    prefList.add(null);
    prefList.add(kioskPreference("HIGHFLR"));
    prefList.add(kioskPreference("COTR"));
    prefCollection.setKioskPreference(prefList);

    var collectionList = new java.util.ArrayList<KioskPreferenceCollectionDto>();
    collectionList.add(null);
    collectionList.add(prefCollection);
    collectionList.add(new KioskPreferenceCollectionDto());

    when(ohipClient.fetchReservationPreferences(HOTEL_ID, RESERVATION_ID))
        .thenReturn(preferencesResponseWithCollections(collectionList));
    when(preferenceProperties.getCondition()).thenReturn(Map.of("HIGHFLRCOTR", "COND-COT"));

    long result = availableCleanRoomPort.availableCleanRooms(request);

    assertEquals(1, result);
  }

  @Test
  void availableCleanRooms_whenPreferenceAlreadyHasCotSuffix_doesNotAppendItTwice() {
    var request = packagesRequestWithBasketReference(BASKET_REF);
    when(basketServiceOutPort.getBasket(BASKET_REF)).thenReturn(stayBasket(RESERVATION_ID));
    when(ohipClient.getReservationInfo(HOTEL_ID, List.of(RESERVATION_ID), false, false, false))
        .thenReturn(reservationInfoWithRoomType(RESERVATION_ID, ROOM_TYPE));
    when(ohipClient.getVacantRooms(HOTEL_ID, ROOM_TYPE))
        .thenReturn(vacantRoomsWithCondition("101", "COND-COT"));

    when(ohipClient.fetchReservationPreferences(HOTEL_ID, RESERVATION_ID))
        .thenReturn(preferencesWithValues("HIGHFLRCOTR", "COTR"));
    when(preferenceProperties.getCondition()).thenReturn(Map.of("HIGHFLRCOTR", "COND-COT"));

    long result = availableCleanRoomPort.availableCleanRooms(request);

    assertEquals(1, result);
  }

  @ParameterizedTest(name = "scenario={0}, hasPreferences={1}, expectedResult={2}")
  @MethodSource("provideNullConditionScenarios")
  void availableCleanRooms_withNullHousekeepingOrConditions_returnsCorrectCount(
      String scenario, boolean hasPreferences, long expectedResult) {

    var request = packagesRequestWithBasketReference(BASKET_REF);
    when(basketServiceOutPort.getBasket(BASKET_REF)).thenReturn(stayBasket(RESERVATION_ID));
    when(ohipClient.getReservationInfo(HOTEL_ID, List.of(RESERVATION_ID), false, false, false))
        .thenReturn(reservationInfoWithRoomType(RESERVATION_ID, ROOM_TYPE));

    VacantRoomResponseDto vacantRooms = switch(scenario) {
      case "null_housekeeping" -> vacantRoomWithNullHousekeeping("101");
      case "null_condition_wrapper" -> vacantRoomWithNoCondition("101");
      case "null_condition_value" -> vacantRoomWithNullRoomConditionValue("101");
      case "null_condition_code" -> vacantRoomWithNullRoomCode("101");
      default -> vacantRoomWithNoCondition("101");
    };

    when(ohipClient.getVacantRooms(HOTEL_ID, ROOM_TYPE)).thenReturn(vacantRooms);

    if (hasPreferences) {
      when(ohipClient.fetchReservationPreferences(HOTEL_ID, RESERVATION_ID))
          .thenReturn(preferencesWithValues("HIGHFLR"));
      when(preferenceProperties.getCondition()).thenReturn(Map.of("HIGHFLR", "COND-MATCH"));
    } else {
      when(ohipClient.fetchReservationPreferences(HOTEL_ID, RESERVATION_ID)).thenReturn(null);
    }

    long result = availableCleanRoomPort.availableCleanRooms(request);

    assertEquals(expectedResult, result);
  }

  private static Stream<Arguments> provideNullConditionScenarios() {
    return Stream.of(
        // Housekeeping is null
        Arguments.of("null_housekeeping", true, 0),

        // Room condition null with preferences
        Arguments.of("null_condition_wrapper", true, 0),

        // Room condition null without preferences
        Arguments.of("null_condition_wrapper", false, 1),

        // Room condition value is null
        Arguments.of("null_condition_value", true, 0),

        // Room condition code is null
        Arguments.of("null_condition_code", true, 0)
    );
  }

  @ParameterizedTest(name = "scenario={0}, hasPreferences={1}, expectedCount={2}")
  @MethodSource("provideMultipleRoomsScenarios")
  void availableCleanRooms_withMultipleRooms_returnsCorrectCount(
      String scenario, boolean hasPreferences, long expectedCount) {

    var request = packagesRequestWithBasketReference(BASKET_REF);
    when(basketServiceOutPort.getBasket(BASKET_REF)).thenReturn(stayBasket(RESERVATION_ID));
    when(ohipClient.getReservationInfo(HOTEL_ID, List.of(RESERVATION_ID), false, false, false))
        .thenReturn(reservationInfoWithRoomType(RESERVATION_ID, ROOM_TYPE));

    VacantRoomResponseDto vacantRooms = switch(scenario) {
      case "mixed_conditions" -> vacantRoomsWithMixedConditions("101", "102", "103", "104");
      case "multiple_matching" -> vacantRoomsWithMultipleMatchingConditions("101", "102", "103");
      case "multiple_null" -> vacantRoomsWithMultipleNullConditions("101", "102", "103");
      case "null_room_object" -> vacantRoomsWithNullRoom();
      default -> vacantRoomWithNoCondition("101");
    };

    when(ohipClient.getVacantRooms(HOTEL_ID, ROOM_TYPE)).thenReturn(vacantRooms);

    if (hasPreferences) {
      String preferenceCode = scenario.equals("multiple_null") ? "LOWFLR" : "HIGHFLR";
      String conditionCode = scenario.equals("multiple_null") ? "COND-OTHER" : "COND-MATCH";
      when(ohipClient.fetchReservationPreferences(HOTEL_ID, RESERVATION_ID))
          .thenReturn(preferencesWithValues(preferenceCode));
      when(preferenceProperties.getCondition()).thenReturn(Map.of(preferenceCode, conditionCode));
    } else {
      when(ohipClient.fetchReservationPreferences(HOTEL_ID, RESERVATION_ID)).thenReturn(null);
    }

    long result = availableCleanRoomPort.availableCleanRooms(request);

    assertEquals(expectedCount, result);
  }

  private static Stream<Arguments> provideMultipleRoomsScenarios() {
    return Stream.of(
        // Mixed housekeeping states with preferences
        Arguments.of("mixed_conditions", true, 1),

        // Multiple rooms with matching condition
        Arguments.of("multiple_matching", true, 3),

        // Multiple rooms with null conditions, no preferences
        Arguments.of("multiple_null", false, 3),

        // Multiple rooms with preferences but no matching condition
        Arguments.of("multiple_null", true, 0),

        // Null room object
        Arguments.of("null_room_object", true, 0)
    );
  }

  @Test
  void availableCleanRooms_returnZeroNotMaxValue_whenNoRoomsMatchPreferences() {
    var request = packagesRequestWithBasketReference(BASKET_REF);
    when(basketServiceOutPort.getBasket(BASKET_REF)).thenReturn(stayBasket(RESERVATION_ID));
    when(ohipClient.getReservationInfo(HOTEL_ID, List.of(RESERVATION_ID), false, false, false))
        .thenReturn(reservationInfoWithRoomType(RESERVATION_ID, ROOM_TYPE));
    when(ohipClient.getVacantRooms(HOTEL_ID, ROOM_TYPE))
        .thenReturn(vacantRoomsWithMultipleMatchingConditions("101", "102"));
    when(ohipClient.fetchReservationPreferences(HOTEL_ID, RESERVATION_ID))
        .thenReturn(preferencesWithValues("LOWFLR"));
    when(preferenceProperties.getCondition()).thenReturn(Map.of("LOWFLR", "COND-OTHER"));

    long result = availableCleanRoomPort.availableCleanRooms(request);

    // Line 78: result == Long.MAX_VALUE ? 0 : result
    // When availability is empty (no matching rooms), Math.min returns Long.MAX_VALUE
    // which gets converted to 0 by the ternary operator
    assertEquals(0, result);
  }

  @Test
  void availableCleanRooms_returnZeroNotMaxValue_whenVacantRoomsExistButDontMatchConditions() {
    var request = packagesRequestWithBasketReference(BASKET_REF);
    when(basketServiceOutPort.getBasket(BASKET_REF)).thenReturn(stayBasket(RESERVATION_ID));
    when(ohipClient.getReservationInfo(HOTEL_ID, List.of(RESERVATION_ID), false, false, false))
        .thenReturn(reservationInfoWithRoomType(RESERVATION_ID, ROOM_TYPE));
    when(ohipClient.getVacantRooms(HOTEL_ID, ROOM_TYPE))
        .thenReturn(vacantRoomWithNoCondition("101"));
    when(ohipClient.fetchReservationPreferences(HOTEL_ID, RESERVATION_ID))
        .thenReturn(preferencesWithValues("HIGHFLR"));
    when(preferenceProperties.getCondition()).thenReturn(Map.of("HIGHFLR", "COND-OTHER"));

    long result = availableCleanRoomPort.availableCleanRooms(request);

    // Line 78: Filters room by matching preference - finds none
    // Returns 0 after ternary operator converts MAX_VALUE to 0
    assertEquals(0, result);
  }

  private PackagesRequest packagesRequestWithBasketReference(String basketReference) {
    return PackagesRequest.builder()
        .hotelId(HOTEL_ID)
        .startDate("2026-08-18")
        .endDate("2026-08-19")
        .adultsNumber(2)
        .childrenNumber(0)
        .nightsNumber(1)
        .language("en")
        .country("gb")
        .basketReference(basketReference)
        .isCiol(false)
        .build();
  }

  private Basket stayBasket(String reservationId) {
    return Basket.builder()
        .bookingReference(BASKET_REF)
        .items(List.of(BasketItem.builder().type("STAY").sourceId(reservationId).build()))
        .build();
  }

  private ReservationByBasketRefResponseDto reservationInfoWithRoomType(String reservationId,
      String roomType) {
    var roomStay = new RoomStayByIdDto();
    roomStay.setRoomType(roomType);

    var reservation = new ReservationByIdDto();
    reservation.setReservationId(reservationId);
    reservation.setRoomStay(roomStay);

    var reservationInfo = new ReservationByBasketRefResponseDto();
    reservationInfo.setReservationByIdList(List.of(reservation));
    return reservationInfo;
  }

  private VacantRoomResponseDto vacantRoomWithNoCondition(String roomId) {
    var housekeeping = new HousekeepingDto();
    housekeeping.setRoomCondition(null);

    var room = new KioskRoomDto();
    room.setRoomId(roomId);
    room.setHousekeeping(housekeeping);

    var hotelRoomsDetails = new HotelRoomsDetailsDto();
    hotelRoomsDetails.setRoom(List.of(room));

    var response = new VacantRoomResponseDto();
    response.setHotelRoomsDetails(hotelRoomsDetails);
    return response;
  }

  private VacantRoomResponseDto vacantRoomsWithCondition(String roomId, String conditionCode) {
    var roomConditionValue = new RoomConditionValueDto();
    roomConditionValue.setCode(conditionCode);

    var roomConditionWrapper = new RoomConditionDto();
    roomConditionWrapper.setRoomCondition(roomConditionValue);

    var housekeeping = new HousekeepingDto();
    housekeeping.setRoomCondition(roomConditionWrapper);

    var room = new KioskRoomDto();
    room.setRoomId(roomId);
    room.setHousekeeping(housekeeping);

    var hotelRoomsDetails = new HotelRoomsDetailsDto();
    hotelRoomsDetails.setRoom(List.of(room));

    var response = new VacantRoomResponseDto();
    response.setHotelRoomsDetails(hotelRoomsDetails);
    return response;
  }

  private KioskReservationPreferencesDto preferencesWithValues(String... preferenceValues) {
    List<KioskPreferenceDto> preferences = java.util.stream.Stream.of(preferenceValues)
        .map(value -> {
          var preference = new KioskPreferenceDto();
          preference.setPreferenceValue(value);
          return preference;
        })
        .toList();

    var collection = new KioskPreferenceCollectionDto();
    collection.setKioskPreference(preferences);

    var response = new KioskReservationPreferencesDto();
    response.setKioskPreferenceCollection(List.of(collection));
    return response;
  }

  private KioskPreferenceDto kioskPreference(String value) {
    var preference = new KioskPreferenceDto();
    preference.setPreferenceValue(value);
    return preference;
  }

  private KioskReservationPreferencesDto preferencesResponseWithCollections(
      List<KioskPreferenceCollectionDto> collections) {
    var response = new KioskReservationPreferencesDto();
    response.setKioskPreferenceCollection(collections);
    return response;
  }

  private VacantRoomResponseDto vacantRoomWithNullHousekeeping(String roomId) {
    var room = new KioskRoomDto();
    room.setRoomId(roomId);
    room.setHousekeeping(null);

    var hotelRoomsDetails = new HotelRoomsDetailsDto();
    hotelRoomsDetails.setRoom(List.of(room));

    var response = new VacantRoomResponseDto();
    response.setHotelRoomsDetails(hotelRoomsDetails);
    return response;
  }

  private VacantRoomResponseDto vacantRoomWithNullRoomConditionValue(String roomId) {
    var roomConditionWrapper = new RoomConditionDto();
    roomConditionWrapper.setRoomCondition(null);

    var housekeeping = new HousekeepingDto();
    housekeeping.setRoomCondition(roomConditionWrapper);

    var room = new KioskRoomDto();
    room.setRoomId(roomId);
    room.setHousekeeping(housekeeping);

    var hotelRoomsDetails = new HotelRoomsDetailsDto();
    hotelRoomsDetails.setRoom(List.of(room));

    var response = new VacantRoomResponseDto();
    response.setHotelRoomsDetails(hotelRoomsDetails);
    return response;
  }

  private VacantRoomResponseDto vacantRoomWithNullRoomCode(String roomId) {
    // This creates a scenario where the RoomConditionValueDto exists but its code is null
    var roomConditionValue = new RoomConditionValueDto();
    roomConditionValue.setCode(null);
    var roomConditionWrapper = new RoomConditionDto();
    roomConditionWrapper.setRoomCondition(roomConditionValue);
    var housekeeping = new HousekeepingDto();
    housekeeping.setRoomCondition(roomConditionWrapper);
    var room = new KioskRoomDto();
    room.setRoomId(roomId);
    room.setHousekeeping(housekeeping);
    var hotelRoomsDetails = new HotelRoomsDetailsDto();
    hotelRoomsDetails.setRoom(List.of(room));
    var response = new VacantRoomResponseDto();
    response.setHotelRoomsDetails(hotelRoomsDetails);
    return response;
  }

  private VacantRoomResponseDto vacantRoomsWithMixedConditions(String... roomIds) {
    List<KioskRoomDto> rooms = new java.util.ArrayList<>();

    for (int i = 0; i < roomIds.length; i++) {
      var room = new KioskRoomDto();
      room.setRoomId(roomIds[i]);

      if (i == 0) {
        // First room: has matching condition
        var roomConditionValue = new RoomConditionValueDto();
        roomConditionValue.setCode("COND-MATCH");
        var roomConditionWrapper = new RoomConditionDto();
        roomConditionWrapper.setRoomCondition(roomConditionValue);
        var housekeeping = new HousekeepingDto();
        housekeeping.setRoomCondition(roomConditionWrapper);
        room.setHousekeeping(housekeeping);
      } else if (i == 1) {
        // Second room: has null condition
        var housekeeping = new HousekeepingDto();
        housekeeping.setRoomCondition(null);
        room.setHousekeeping(housekeeping);
      } else if (i == 2) {
        // Third room: has null housekeeping
        room.setHousekeeping(null);
      } else {
        // Fourth room: has non-matching condition
        var roomConditionValue = new RoomConditionValueDto();
        roomConditionValue.setCode("COND-OTHER");
        var roomConditionWrapper = new RoomConditionDto();
        roomConditionWrapper.setRoomCondition(roomConditionValue);
        var housekeeping = new HousekeepingDto();
        housekeeping.setRoomCondition(roomConditionWrapper);
        room.setHousekeeping(housekeeping);
      }

      rooms.add(room);
    }

    var hotelRoomsDetails = new HotelRoomsDetailsDto();
    hotelRoomsDetails.setRoom(rooms);

    var response = new VacantRoomResponseDto();
    response.setHotelRoomsDetails(hotelRoomsDetails);
    return response;
  }

  private VacantRoomResponseDto vacantRoomsWithMultipleMatchingConditions(String... roomIds) {
    List<KioskRoomDto> rooms = java.util.stream.Stream.of(roomIds)
        .map(roomId -> {
          var roomConditionValue = new RoomConditionValueDto();
          roomConditionValue.setCode("COND-MATCH");

          var roomConditionWrapper = new RoomConditionDto();
          roomConditionWrapper.setRoomCondition(roomConditionValue);

          var housekeeping = new HousekeepingDto();
          housekeeping.setRoomCondition(roomConditionWrapper);

          var room = new KioskRoomDto();
          room.setRoomId(roomId);
          room.setHousekeeping(housekeeping);

          return room;
        })
        .toList();

    var hotelRoomsDetails = new HotelRoomsDetailsDto();
    hotelRoomsDetails.setRoom(rooms);

    var response = new VacantRoomResponseDto();
    response.setHotelRoomsDetails(hotelRoomsDetails);
    return response;
  }

  private VacantRoomResponseDto vacantRoomsWithMultipleNullConditions(String... roomIds) {
    List<KioskRoomDto> rooms = java.util.stream.Stream.of(roomIds)
        .map(roomId -> {
          var housekeeping = new HousekeepingDto();
          housekeeping.setRoomCondition(null);

          var room = new KioskRoomDto();
          room.setRoomId(roomId);
          room.setHousekeeping(housekeeping);

          return room;
        })
        .toList();

    var hotelRoomsDetails = new HotelRoomsDetailsDto();
    hotelRoomsDetails.setRoom(rooms);

    var response = new VacantRoomResponseDto();
    response.setHotelRoomsDetails(hotelRoomsDetails);
    return response;
  }

  private VacantRoomResponseDto vacantRoomsWithNullRoom() {
    var hotelRoomsDetails = new HotelRoomsDetailsDto();
    var nullRoom = new java.util.ArrayList<KioskRoomDto>();
    nullRoom.add(null);
    hotelRoomsDetails.setRoom(nullRoom);


    var response = new VacantRoomResponseDto();
    response.setHotelRoomsDetails(hotelRoomsDetails);
    return response;
  }
}
