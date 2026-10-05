package uk.co.whitbread.company.utils;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.company.properties.UpsellItemAllowancesProperties;

@Component
@RequiredArgsConstructor
public class UpsellItemsAllowedUtils {

  private final UpsellItemAllowancesProperties upsellItemAllowances;


  public List<String> calculateUpsellItemsAllowed(List<String> extrasCodes) {
    List<String> upsellItemsAllowed = extrasCodes.stream()
        .map(this::getUpsellItemsAllowedForCode)
        .flatMap(Arrays::stream)
        .collect(toList());
    removeRepeatedElements(upsellItemsAllowed);
    return upsellItemsAllowed;
  }

  private String[] getUpsellItemsAllowedForCode(String extrasCode) {

    final String PREMIER_BREAKFAST = "1";
    final String CONTINENTAL_BREAKFAST = "2";
    final String MEAL_DEAL = "3";
    final String HUB_BREAKFAST = "4";
    final String WIFI = "5";
    final String ITEM_SEPARATOR = ",";

    if (extrasCode != null) {
      switch (extrasCode) {
        case PREMIER_BREAKFAST:
          return upsellItemAllowances.getPremierBreakfast().split(ITEM_SEPARATOR);
        case CONTINENTAL_BREAKFAST:
          return upsellItemAllowances.getContinentalBreakfast().split(ITEM_SEPARATOR);
        case MEAL_DEAL:
          return upsellItemAllowances.getMealDeal().split(ITEM_SEPARATOR);
        case WIFI:
          return upsellItemAllowances.getWiFi().split(ITEM_SEPARATOR);
        case HUB_BREAKFAST:
          return upsellItemAllowances.getHubBreakfast().split(ITEM_SEPARATOR);
        default:
          return new String[0];
      }
    }
    return new String[0];
  }

  private void removeRepeatedElements(List<String> elements) {
    Set<String> noRepetitions = new HashSet<>();
    elements.removeIf(element -> !noRepetitions.add(element));
  }

}
