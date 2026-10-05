package uk.co.whitbread.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.Builder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.whitbread.domain.model.srp.in.RecommendedSearchModifiers;
import uk.co.whitbread.domain.model.srp.out.Cost;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.properties.AvailabilityProperties;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.properties.RecommendedSearchOption;

class HotelAvailabilitySorterTest {


  @BeforeAll
  static void init(){
    // Given
    AvailabilityProperties availabilityProperties = new AvailabilityProperties();
    RecommendedSearchOption recommendedSearchOption = new RecommendedSearchOption();
    recommendedSearchOption.setDropOffModifier(0.3);
    recommendedSearchOption.setDropOffPoint(5.0);
    recommendedSearchOption.setDistanceModifier(1.0);
    recommendedSearchOption.setPriceModifier(1.0);
    availabilityProperties.setRecommendedSearch(recommendedSearchOption);
    HotelAvailabilitySorter.setAvailabilityProperties(availabilityProperties);
  }

  @ParameterizedTest
  @MethodSource("provideHotelAvailabilityResponses")
  void sort(RecommendedSortTestSuite recommendedSortTestSuite) {
    // Given
    HotelAvailabilitiesResponse hotelAvailabilitiesResponse = new HotelAvailabilitiesResponse();
    hotelAvailabilitiesResponse.setHotelAvailabilityList(recommendedSortTestSuite.hotelAvailabilityResponseList());
    List<String> sortingOptions = Collections.singletonList("RECOMMENDATION");

    // When
    HotelAvailabilitySorter.sort(hotelAvailabilitiesResponse, sortingOptions, null);

    // Then
    recommendedSortTestSuite.assertions().accept(hotelAvailabilitiesResponse);
  }

  @ParameterizedTest
  @MethodSource("provideHotelAvailabilityResponsesWithModifiers")
  void sortWithModifiers(RecommendedSortTestSuite recommendedSortTestSuite) {
    // Given
    HotelAvailabilitiesResponse hotelAvailabilitiesResponse = new HotelAvailabilitiesResponse();
    hotelAvailabilitiesResponse.setHotelAvailabilityList(recommendedSortTestSuite.hotelAvailabilityResponseList());
    List<String> sortingOptions = Collections.singletonList("RECOMMENDATION");
    var modifiers = new RecommendedSearchModifiers(0.1f, 1f, 0.9f);

    // When
    HotelAvailabilitySorter.sort(hotelAvailabilitiesResponse, sortingOptions, modifiers);

    // Then
    recommendedSortTestSuite.assertions().accept(hotelAvailabilitiesResponse);
  }

  @ParameterizedTest
  @MethodSource("provideHotelAvailabilityResponsesWithModifiers")
  void sortWithModifiers_NullHubModifier_UsesDefaultValue(RecommendedSortTestSuite recommendedSortTestSuite) {
    // Given
    HotelAvailabilitiesResponse hotelAvailabilitiesResponse = new HotelAvailabilitiesResponse();
    hotelAvailabilitiesResponse.setHotelAvailabilityList(recommendedSortTestSuite.hotelAvailabilityResponseList());
    List<String> sortingOptions = Collections.singletonList("RECOMMENDATION");
    var modifiers = new RecommendedSearchModifiers(0.1f, 1f, null);

    // When
    HotelAvailabilitySorter.sort(hotelAvailabilitiesResponse, sortingOptions, modifiers);

    // Then
    recommendedSortTestSuite.assertions().accept(hotelAvailabilitiesResponse);
  }

  private static Stream<RecommendedSortTestSuite> provideHotelAvailabilityResponses() {
    return Stream.of(
        //mix of different data types
        RecommendedSortTestSuite.builder()
            .hotelAvailabilityResponseList(
                List.of(
                    HotelAvailabilityResponse.builder()
                        .distance(3.0)
                        .name("hotel-1")
                        .lowestRoomRate(Cost.builder()
                            .netTotal(BigDecimal.valueOf(10)).build())
                        .available(true).build(),
                    HotelAvailabilityResponse.builder()
                        .name("hotel-2")
                        .distance(300.0)
                        .isHub(true)
                        .lowestRoomRate(Cost.builder()
                            .netTotal(BigDecimal.valueOf(10)).build())
                        .available(true).build(),
                    HotelAvailabilityResponse.builder()
                        .name("hotel-3")
                        .distance(2.0)
                        .lowestRoomRate(Cost.builder()
                            .netTotal(BigDecimal.valueOf(99)).build())
                        .available(true).build(),
                    HotelAvailabilityResponse.builder()
                        .distance(1.0)
                        .name("hotel-4")
                        .lowestRoomRate(Cost.builder()
                            .netTotal(BigDecimal.valueOf(-12)).build())
                        .available(true).build(),
                    HotelAvailabilityResponse.builder().build(),
                    HotelAvailabilityResponse.builder()
                        .name("hotel-5")
                        .distance(1.0)
                        .lowestRoomRate(Cost.builder()
                            .netTotal(BigDecimal.valueOf(0)).build())
                        .available(false).build()
                ))
            .assertions(hotelAvailabilitiesResponse -> {
              List<HotelAvailabilityResponse> hotelAvailabilityList =
                  hotelAvailabilitiesResponse.getHotelAvailabilityList();
              assertNotNull(hotelAvailabilityList);
              assertEquals("hotel-1", hotelAvailabilityList.get(0).getName());
              assertEquals("hotel-2", hotelAvailabilityList.get(4).getName());
              assertEquals("hotel-5", hotelAvailabilityList.get(5).getName());
              assertEquals(6, hotelAvailabilityList.size());
            }).build(),
        //empty list
        RecommendedSortTestSuite.builder()
            .hotelAvailabilityResponseList(
                Collections.emptyList())
            .assertions(hotelAvailabilitiesResponse -> {
              List<HotelAvailabilityResponse> hotelAvailabilityList =
                  hotelAvailabilitiesResponse.getHotelAvailabilityList();
              assertNotNull(hotelAvailabilityList);
              assertEquals(0, hotelAvailabilityList.size());
            }).build()
    );
  }

  private static Stream<RecommendedSortTestSuite> provideHotelAvailabilityResponsesWithModifiers() {
    return Stream.of(
        //mix of different data types
        RecommendedSortTestSuite.builder()
            .hotelAvailabilityResponseList(
                List.of(
                    HotelAvailabilityResponse.builder()
                        .distance(3.0)
                        .name("hotel-1")
                        .lowestRoomRate(Cost.builder()
                            .netTotal(BigDecimal.valueOf(10)).build())
                        .available(true).build(),
                    HotelAvailabilityResponse.builder()
                        .name("hotel-2")
                        .distance(5.0)
                        .isHub(true)
                        .lowestRoomRate(Cost.builder()
                            .netTotal(BigDecimal.valueOf(10)).build())
                        .available(true).build(),
                    HotelAvailabilityResponse.builder()
                        .name("hotel-3")
                        .distance(2.0)
                        .lowestRoomRate(Cost.builder()
                            .netTotal(BigDecimal.valueOf(99)).build())
                        .available(true).build()
                ))
            .assertions(hotelAvailabilitiesResponse -> {
              List<HotelAvailabilityResponse> hotelAvailabilityList =
                  hotelAvailabilitiesResponse.getHotelAvailabilityList();
              assertNotNull(hotelAvailabilityList);
              assertEquals("hotel-1", hotelAvailabilityList.get(0).getName());
              assertEquals("hotel-2", hotelAvailabilityList.get(1).getName());
              assertEquals("hotel-3", hotelAvailabilityList.get(2).getName());
              assertEquals(3, hotelAvailabilityList.size());
              assertNotNull(hotelAvailabilityList.get(1).getScore());
            }).build(),
        //empty list
        RecommendedSortTestSuite.builder()
            .hotelAvailabilityResponseList(
                Collections.emptyList())
            .assertions(hotelAvailabilitiesResponse -> {
              List<HotelAvailabilityResponse> hotelAvailabilityList =
                  hotelAvailabilitiesResponse.getHotelAvailabilityList();
              assertNotNull(hotelAvailabilityList);
              assertEquals(0, hotelAvailabilityList.size());
            }).build()
    );
  }

  @Builder
  record RecommendedSortTestSuite(List<HotelAvailabilityResponse> hotelAvailabilityResponseList,
                                  Consumer<HotelAvailabilitiesResponse> assertions) {
  }
}