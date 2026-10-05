package uk.co.whitbread.avail.business.events.infrastructure.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RateDataElementValue {

  private String currency;
  private String rateClassification;
  private String personRate1;
  private String beginDate;
  private String endDate;
  private String maxLos;
  private String maxLosScopeFrom;
  private String maxLosScopeTo;
  private String minLos;
  private String minLosScopeFrom;
  private String minLosScopeTo;
  private String rateCode;
  private String roomType;
  private String rateCategory;
  private List<String> roomTypes;

}
