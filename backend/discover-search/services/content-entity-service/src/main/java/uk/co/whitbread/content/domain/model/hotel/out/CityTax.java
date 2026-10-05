package uk.co.whitbread.content.domain.model.hotel.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CityTax {

  private Boolean isCityTaxHotel;
  private Boolean isCityTaxBusinessHotel;
  private String cityTaxWebUrl;
  private BigDecimal amount;
  private BigDecimal percentage;
  private String calculation;
  private String posting;
  private BigDecimal vat;
  private Integer maxNights;
  private String effectiveFrom;
  private String bookingDateFrom;
}