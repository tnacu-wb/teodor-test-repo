package uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatePerNightDto {

  private String startDate;
  private BigDecimal pricePerNight;
  private BigDecimal grossPricePerNight;
  private BigDecimal cityTaxPerNight;
  private BigDecimal vatRate;
  private BigDecimal cityTaxAmountBeforeTax;
  private BigDecimal cityTaxVat;

}