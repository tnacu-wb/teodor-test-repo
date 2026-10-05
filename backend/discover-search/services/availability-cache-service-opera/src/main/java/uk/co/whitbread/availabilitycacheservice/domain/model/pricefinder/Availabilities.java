package uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Availabilities implements Comparable<Availabilities> {

  private String availableDate;
  private String currency;
  private BigDecimal minimumRate;
  private BigDecimal finalPrice;
  private String rateCode;
  private String rateClassification;
  private String roomType;
  private String roomCategory;
  private int quantity;
  private Integer minimumNights;
  private boolean hasMlosRestriction;
  private boolean hasClosedRestriction;

  @Override
  public int compareTo(Availabilities other) {
    return this.getAvailableDate().compareTo(other.getAvailableDate());
  }
}
