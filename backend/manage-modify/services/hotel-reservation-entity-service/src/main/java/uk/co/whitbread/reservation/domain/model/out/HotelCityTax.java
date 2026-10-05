package uk.co.whitbread.reservation.domain.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelCityTax {
  private BigDecimal amount;

  private String bookingDateFrom;

  private String calculation;

  private String cityTaxWebUrl;

  private String effectiveFrom;

  private Boolean isCityTaxBusinessHotel;

  private Boolean isCityTaxHotel;

  private Integer maxNights;

  private BigDecimal percentage;

  private String posting;

  private BigDecimal vat;
}
