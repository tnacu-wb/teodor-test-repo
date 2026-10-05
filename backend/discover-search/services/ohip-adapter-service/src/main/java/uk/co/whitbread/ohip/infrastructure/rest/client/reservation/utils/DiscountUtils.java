package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class DiscountUtils {

  private DiscountUtils() {
  }

  /**
   * Gets the weighted discount amounts based on the rates' weights. The weights are obtained by
   * dividing the rate to the total cost.
   *
   * @param ratesList      The list of rates currently residing in the reservation.
   * @param discountAmount The discountAmount that is applied to the reservations.
   * @return The list of divided amounts.
   */
  public static List<BigDecimal> getWeightedDiscountAmounts(
      List<BigDecimal> ratesList, BigDecimal discountAmount) {
    var weightedAmounts = getWeights(ratesList, discountAmount);

    var sumDiscounts = weightedAmounts.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

    if (discountAmount.compareTo(sumDiscounts) != 0) {
      var diff = discountAmount.subtract(sumDiscounts);
      for (int i = 0; i < ratesList.size(); i++) {
        var updatedDiscount = weightedAmounts.get(i).add(diff);
        if (ratesList.get(i).compareTo(updatedDiscount) >= 0) {
          weightedAmounts.set(i, updatedDiscount);
          i = ratesList.size();
        } else {
          var newDiff = ratesList.get(i).subtract(weightedAmounts.get(i));
          weightedAmounts.set(i, ratesList.get(i));
          diff = diff.subtract(newDiff);
        }
      }
    }

    return weightedAmounts;
  }

  private static List<BigDecimal> getWeights(
      List<BigDecimal> ratesList, BigDecimal discountAmount) {
    List<BigDecimal> weightedAmounts = new ArrayList<>();

    var totalCost = ratesList.stream().reduce(BigDecimal::add).orElse(BigDecimal.ONE);

    var discountPercent = discountAmount.divide(totalCost, 2, RoundingMode.DOWN);

    ratesList.forEach(rate -> weightedAmounts.add(rate.multiply(discountPercent)));

    return weightedAmounts;
  }
}
