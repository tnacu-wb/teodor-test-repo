package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.RateClassificationMapper.mapRateClassificationsToRateClassificationsMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassification;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassificationsList;


public class RateClassificationMapperTest {

  @Test
  public void mapRateClassificationsToRateClassificationsDtoTest() {
    RateClassificationsList rateClassificationsList = getRateClassifications();
    Map<String, RateClassification> rateClassificationMap
        = mapRateClassificationsToRateClassificationsMap(rateClassificationsList);

    List<RateClassification> rateClassificationsListFetch = new ArrayList();
    for (Map.Entry<String, RateClassification> entry : rateClassificationMap.entrySet()) {
      rateClassificationsListFetch.add(entry.getValue());
    }

    assertThat(rateClassificationsListFetch).isNotEmpty()
        .extracting(RateClassification::getOrder).contains("5", "2", "1");

    assertThat(rateClassificationsListFetch).isNotEmpty()
        .extracting(RateClassification::getClassification).contains("A", "S", "R");
  }

  private RateClassificationsList getRateClassifications() {

    return RateClassificationsList.builder().rateClassifications(new ArrayList<>(
        Arrays.asList(getRateClassification("A", "5", "Flex",
                "Pay on arrival. Fully refundable up to 1pm on arrival day."),
            getRateClassification("S", "2", "Standard",
                "Pay on arrival. Change arrival date. Non-refundable"),
            getRateClassification("R", "1", "non-Flex",
                "Pay on arrival. No change"))
    )).build();
  }

  private RateClassification getRateClassification(String classification, String order, String name,
      String description) {
    return RateClassification.builder()
        .classification(classification)
        .description(description)
        .name(name)
        .order(order)
        .build();
  }
}
