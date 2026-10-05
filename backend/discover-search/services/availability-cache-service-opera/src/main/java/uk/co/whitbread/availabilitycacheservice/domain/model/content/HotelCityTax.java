package uk.co.whitbread.availabilitycacheservice.domain.model.content;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class HotelCityTax {

  private BigDecimal amount;

  private String bookingDateFrom;

  @Getter
  public enum CalculationEnum {
    PER_ROOM,
    PER_ADULT,
    PER_CHILD,
    PER_PERSON
  }

  private HotelCityTax.CalculationEnum calculation;

  private String cityTaxWebUrl;

  private String effectiveFrom;

  private Boolean isCityTaxBusinessHotel;

  private Boolean isCityTaxHotel;

  private Integer maxNights;

  private BigDecimal percentage;

  @Getter
  public enum PostingEnum {
    PER_NIGHT
  }

  private HotelCityTax.PostingEnum posting;

  private BigDecimal vat;
}
