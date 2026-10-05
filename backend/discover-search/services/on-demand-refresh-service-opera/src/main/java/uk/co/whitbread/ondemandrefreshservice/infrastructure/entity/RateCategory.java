package uk.co.whitbread.ondemandrefreshservice.infrastructure.entity;

import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum RateCategory {
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
  private String category;

  RateCategory(final String category){
    this.category = category;
  }

  public String rateCategory(){
    return category;
  }

  public static Set<RateCategory> getRatePlanCodes(final String rateCategoryInput) {
    final Set<RateCategory> rateCodes = new HashSet<>();
    for (final RateCategory rateCategory : RateCategory.values()) {
      if(rateCategoryInput.equalsIgnoreCase(rateCategory.rateCategory())) {
        rateCodes.add(rateCategory);
      }
    }
    log.info("RatePlan-Code :{} for input Rate-Category:{}",rateCodes, rateCategoryInput);
    return rateCodes;
  }
}


