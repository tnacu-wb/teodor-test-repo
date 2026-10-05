package uk.co.whitbread.domain.logic;

import static uk.co.whitbread.domain.logic.AvailabilitySortOption.DISTANCE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import uk.co.whitbread.domain.model.srp.in.RecommendedSearchModifiers;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.properties.AvailabilityProperties;

@UtilityClass
public class HotelAvailabilitySorter {

  public static final int UNAVAILABLE_HOTEL_SCORE = -5;
  public static final int DISTANCE_PENALTY = 4;

  private static AvailabilityProperties availabilityProperties;

  public static void setAvailabilityProperties(AvailabilityProperties availabilityProperties) {
    HotelAvailabilitySorter.availabilityProperties = availabilityProperties;
  }

  public static void sort(HotelAvailabilitiesResponse hotelAvailabilitiesResponse, List<String> sortingOptions,
      RecommendedSearchModifiers recommendedSearchModifiers) {
    List<HotelAvailabilityResponse> hotelAvailabilityResponseList =
        new ArrayList<>(hotelAvailabilitiesResponse.getHotelAvailabilityList());

    var sortingOptionElseDefault =
        sortingOptions != null && !sortingOptions.contains(null)
            ? AvailabilitySortOption.valueOf(sortingOptions.get(0)) : DISTANCE;

    hotelAvailabilityResponseList =
        applySorting(sortingOptionElseDefault, hotelAvailabilityResponseList, recommendedSearchModifiers);

    hotelAvailabilitiesResponse.setHotelAvailabilityList(hotelAvailabilityResponseList);
  }

  private static List<HotelAvailabilityResponse> applySorting(AvailabilitySortOption sortingOption,
      List<HotelAvailabilityResponse> hotelAvailabilityResponseList,
      RecommendedSearchModifiers recommendedSearchModifiers) {
    return switch (sortingOption) {
      case DISTANCE, AVAILABLE_FIRST -> {
        Map<Boolean, List<HotelAvailabilityResponse>> availableMap = hotelAvailabilityResponseList.stream()
            .collect(Collectors.partitioningBy(HotelAvailabilityResponse::getAvailable));

        List<HotelAvailabilityResponse> availableByDistance = sortAvailableByDistance(availableMap.get(Boolean.TRUE));
        List<HotelAvailabilityResponse> availableByOpenSoonAndDistance =
            sortAvailableByOpenSoonAndDistance(availableMap.get(Boolean.FALSE));

        yield Stream.concat(availableByDistance.stream(), availableByOpenSoonAndDistance.stream()).toList();
      }
      case PRICE -> sortByPrice(hotelAvailabilityResponseList);
      case RECOMMENDATION -> sortByRecommendation(hotelAvailabilityResponseList, recommendedSearchModifiers);
    };
  }

  /**
   * Calculates a relevance score for each displayed hotel based on its distance from the search location and its price.
   *
   * <p>The scoring process follows these steps:</p>
   * <ul>
   *   <li>Each hotel is assigned an initial score based on its distance and price.</li>
   *   <li>The scores are normalized within the range of distances and prices from the full results.</li>
   *   <li>The distance score is exponentially scaled to give higher priority to closer hotels, as distance
   *       has been found to be a more significant factor than price.</li>
   *   <li>Hub locations are deprioritized since they tend to be both close and inexpensive, which would
   *       otherwise dominate the top results.</li>
   *   <li>Hotels located more than 5 miles away and those that are sold out receive a steep score reduction.</li>
   * </ul>
   *
   * <p>This approach ensures a balanced ranking that prioritizes proximity while maintaining fair price consideration.
   * </p>
   *
   * @param hotelAvailabilityResponseList List of hotel availability responses to be sorted
   * @return List of hotel availability responses sorted by recommendation score
   */
  private static List<HotelAvailabilityResponse> sortByRecommendation(
      List<HotelAvailabilityResponse> hotelAvailabilityResponseList,
      RecommendedSearchModifiers recommendedSearchModifiers) {

    var recommendedSearchOption = availabilityProperties.getRecommendedSearch();
    double dropOffModifier = recommendedSearchOption.getDropOffModifier();
    double dropOffPoint = recommendedSearchOption.getDropOffPoint();
    double distanceModifier = Optional.ofNullable(recommendedSearchModifiers)
        .filter(modifiers -> modifiers.getDistanceModifier() != null)
        .map(modifiers -> (double) modifiers.getDistanceModifier())
        .orElse(recommendedSearchOption.getDistanceModifier());
    double priceModifier = Optional.ofNullable(recommendedSearchModifiers)
        .filter(modifiers -> modifiers.getPriceModifier() != null)
        .map(modifiers -> (double) modifiers.getPriceModifier())
        .orElse(recommendedSearchOption.getPriceModifier());
    double hubModifier = Optional.ofNullable(recommendedSearchModifiers)
        .filter(modifiers -> modifiers.getHubModifier() != null)
        .map(modifiers -> (double) modifiers.getHubModifier())
        .orElse(0.85d);

    hotelAvailabilityResponseList = sortHotelsByDistance(hotelAvailabilityResponseList);

    Double minimumDistance = getMinimumDistance(hotelAvailabilityResponseList);
    Double maximumDistance = getMaximumDistance(hotelAvailabilityResponseList);

    BigDecimal minimumPrice = getMinimumPrice(hotelAvailabilityResponseList);
    BigDecimal maximumPrice = getMaximumPrice(hotelAvailabilityResponseList);

    double distanceRange = calculateDistanceRange(minimumDistance, maximumDistance);
    BigDecimal priceRange = calculatePriceRange(minimumPrice, maximumPrice);

    for (HotelAvailabilityResponse hotel : hotelAvailabilityResponseList) {
      var hotelDistance = getValueElseDefault(hotel.getDistance(), 0.0);
      double distanceNormal = 1 - (hotelDistance - minimumDistance) / distanceRange;
      BigDecimal priceNormal = BigDecimal.ONE;
      if (priceRange.compareTo(BigDecimal.ZERO) != 0) {
        priceNormal = hotel.getLowestRoomRate() != null && hotel.getLowestRoomRate().getNetTotal() != null
            ? BigDecimal.ONE.subtract(hotel.getLowestRoomRate().getNetTotal()
            .subtract(minimumPrice)
            .divide(priceRange, 4, RoundingMode.HALF_UP))
            : BigDecimal.ZERO;
      }

      distanceNormal *= Math.exp(-dropOffModifier * hotelDistance);
      double score = distanceNormal * distanceModifier + priceNormal.doubleValue() * priceModifier;

      if (Boolean.TRUE.equals(hotel.getIsHub())) {
        score *= hubModifier;
      }
      if (hotelDistance > dropOffPoint) {
        score = distanceNormal - DISTANCE_PENALTY;
      }
      if (Boolean.FALSE.equals(hotel.getAvailable())) {
        score = UNAVAILABLE_HOTEL_SCORE;
      }

      hotel.setScore(score);
    }

    return hotelAvailabilityResponseList.stream()
        .sorted(Comparator.comparingDouble(HotelAvailabilityResponse::getScore).reversed())
        .toList();
  }

  private static List<HotelAvailabilityResponse> sortHotelsByDistance(
      List<HotelAvailabilityResponse> hotelAvailabilityResponseList) {
    return hotelAvailabilityResponseList.stream()
        .sorted(Comparator.comparingDouble(h -> h.getDistance() != null
            ? h.getDistance() : Double.MAX_VALUE))
        .toList();
  }

  private static Double getMinimumDistance(List<HotelAvailabilityResponse> hotelAvailabilityResponseList) {
    return getValueElseDefault(hotelAvailabilityResponseList.isEmpty() ? Double.valueOf(0.0) :
        hotelAvailabilityResponseList.get(0).getDistance(), 0.0);
  }

  private static Double getMaximumDistance(List<HotelAvailabilityResponse> hotelAvailabilityResponseList) {
    return getValueElseDefault(hotelAvailabilityResponseList.isEmpty() ? Double.valueOf(0.0) :
        hotelAvailabilityResponseList.get(hotelAvailabilityResponseList.size() - 1).getDistance(), 0.0);
  }

  private static BigDecimal getMinimumPrice(List<HotelAvailabilityResponse> hotelAvailabilityResponseList) {
    return hotelAvailabilityResponseList.stream()
        .map(h -> h.getLowestRoomRate() != null ? h.getLowestRoomRate().getNetTotal() : null)
        .filter(Objects::nonNull)
        .min(BigDecimal::compareTo)
        .orElse(BigDecimal.ZERO);
  }

  private static BigDecimal getMaximumPrice(List<HotelAvailabilityResponse> hotelAvailabilityResponseList) {
    return hotelAvailabilityResponseList.stream()
        .map(h -> h.getLowestRoomRate() != null ? h.getLowestRoomRate().getNetTotal() : null)
        .filter(Objects::nonNull)
        .max(BigDecimal::compareTo)
        .orElse(BigDecimal.ZERO);
  }

  private static double calculateDistanceRange(Double minimumDistance, Double maximumDistance) {
    return (maximumDistance - minimumDistance) == 0 ? 1 : (maximumDistance - minimumDistance);
  }

  private static BigDecimal calculatePriceRange(BigDecimal minimumPrice, BigDecimal maximumPrice) {
    return maximumPrice.subtract(minimumPrice).compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE :
        maximumPrice.subtract(minimumPrice);
  }

  private static List<HotelAvailabilityResponse> sortAvailableByDistance(
      List<HotelAvailabilityResponse> availableList) {
    return availableList.stream()
        .filter(h -> h.getDistance() != null)
        .sorted(Comparator.nullsLast(Comparator.comparing(HotelAvailabilityResponse::getDistance)))
        .toList();
  }

  private static List<HotelAvailabilityResponse> sortAvailableByOpenSoonAndDistance(
      List<HotelAvailabilityResponse> availableList) {
    return availableList.stream()
        .filter(h -> h.getDistance() != null)
        .sorted(Comparator.nullsLast(Comparator.comparing(HotelAvailabilityResponse::getHotelOpeningSoon)).reversed()
            .thenComparing(HotelAvailabilityResponse::getDistance))
        .toList();
  }

  private static List<HotelAvailabilityResponse> sortByPrice(
      List<HotelAvailabilityResponse> hotelAvailabilityResponseList) {
    return hotelAvailabilityResponseList.stream()
        .sorted(Comparator.nullsLast(Comparator.comparing(HotelAvailabilityResponse::getAvailable))
            .reversed().thenComparing(
                hotelAvailabilityResponse ->
                    hotelAvailabilityResponse.getLowestRoomRate() == null ? new BigDecimal(0)
                        : hotelAvailabilityResponse.getLowestRoomRate().getNetTotal()))
        .toList();
  }

  private static <T> T getValueElseDefault(T value, T defaultValue) {
    return value != null ? value : defaultValue;
  }
}