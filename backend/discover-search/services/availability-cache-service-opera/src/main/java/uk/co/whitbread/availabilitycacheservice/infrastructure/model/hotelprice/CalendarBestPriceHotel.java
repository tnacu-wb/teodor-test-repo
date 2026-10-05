package uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarBestPriceHotel {

  private String hotelCode;
  private String date;
  private BigDecimal bestPrice;
  private String currency;
  private BigDecimal bestPriceWithCityTax;

  public CalendarBestPriceHotel(String hotelCode, LocalDate date, BigDecimal bestPrice,
      BigDecimal bestPriceWithCityTax, String currency) {
    this.date = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    this.hotelCode = hotelCode;
    this.bestPrice = bestPrice;
    this.currency = currency;
    this.bestPriceWithCityTax = bestPriceWithCityTax;
  }
}
