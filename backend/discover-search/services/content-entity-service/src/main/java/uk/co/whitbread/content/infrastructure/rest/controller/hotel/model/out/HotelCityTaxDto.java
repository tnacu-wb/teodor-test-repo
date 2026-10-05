package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelCityTaxDto {
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
