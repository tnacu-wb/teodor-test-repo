package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
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