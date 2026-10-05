package uk.co.whitbread.avail.business.events.infrastructure.model.enums;

import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum RateCode {
  ADVANCE("S"),
  BUSIFLEX("F"),
  EMERALD1("B"),
  EMPLOYEE("B"),
  FLEXRATE("A"),
  NONFLEX("O"),
  NONFLEXD("W"),
  SEMIFLEX("C"),
  STANDARD("U"),
  FITRATE1("I");

  private final String rateCodeValue;

  RateCode(final String rateCode) {
    this.rateCodeValue = rateCode;
  }

  @Override
  public String toString() {
    return rateCodeValue;
  }

  public static Set<RateCode> getRatePlanCodes(final String rateCategoryInput) {
    final Set<RateCode> rateCodes = new HashSet<>();
    for (final RateCode rateCategory : RateCode.values()) {
      if (rateCategoryInput
          .equalsIgnoreCase(rateCategory.rateCodeValue)) {
        rateCodes.add(rateCategory);
      }
    }
    log.debug("RatePlan-Code :{} for input Rate-Category:{}", rateCodes, rateCategoryInput);
    return rateCodes;
  }
}
