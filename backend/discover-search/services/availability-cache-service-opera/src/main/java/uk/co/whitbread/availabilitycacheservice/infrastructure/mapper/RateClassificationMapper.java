package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassification;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassificationsList;

@Slf4j
@Component
public final class RateClassificationMapper {

  private RateClassificationMapper() {

  }

  public static Map<String, RateClassification> mapRateClassificationsToRateClassificationsMap(
      final RateClassificationsList rateClassificationsList) {

    log.trace("Map RateClassifications to RateClassificationsDto: {}", rateClassificationsList.toString());
    final Map<String, RateClassification> rateClassificationMap = new HashMap<>();

    rateClassificationsList.getRateClassifications()
        .forEach(rateClassification -> {
          final var rateClassificationVar = new RateClassification();
          rateClassificationVar.setName(rateClassification.getName());
          rateClassificationVar.setClassification(rateClassification.getClassification());
          rateClassificationVar.setOrder(rateClassification.getOrder());
          rateClassificationVar.setDescription(rateClassification.getDescription());
          rateClassificationVar.setNotes(rateClassification.getNotes());
          rateClassificationVar.setLongDescription(rateClassification.getLongDescription());

          rateClassificationMap.put(rateClassification.getClassification(), rateClassificationVar);
        });

    for (Map.Entry<String, RateClassification> entry : rateClassificationMap.entrySet()) {
      log.debug(entry.getKey() + ":" + entry.getValue().getName());
    }
    return rateClassificationMap;
  }
}
