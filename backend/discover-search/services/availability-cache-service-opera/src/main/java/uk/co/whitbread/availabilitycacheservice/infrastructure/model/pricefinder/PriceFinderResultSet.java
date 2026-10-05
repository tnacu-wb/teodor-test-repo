package uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, includeFieldNames = true)
public class PriceFinderResultSet {

  private String hotelCode;

  private BigDecimal minimumRate;

  private BigDecimal minimumRateWithCityTax;

  private String currency;

  private LocalDate availableDate;

  private String rateCode;

  private String rateClassification;

  private String roomType;

  private int quantity;

  private int minNights;

  private int maxNights;
}
