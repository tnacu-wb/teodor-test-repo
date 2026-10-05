package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;


@Builder
@Data
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class HotelAvailResultSetDto {

  private String hotelCode;

  private LocalDate availableDate;

  private String pmsSource;

  private boolean availability;

  private String rateClassification;

  private String rateCode;

  private BigDecimal amount;

  private String currency;

  private int quantity;

  private Integer minNights;

  private Integer maxNights;

  private String roomType;

  public HotelAvailResultSetDto(String hotelCode, LocalDate availableDate,
      String pmsSource, boolean availability, String rateClassification,
      String rateCode, BigDecimal amount, String currency, int quantity,
      Integer minNights, Integer maxNights, String roomType) {
    this.hotelCode = hotelCode;
    this.availableDate = availableDate;
    this.pmsSource = pmsSource;
    this.availability = availability;
    this.rateClassification = rateClassification;
    this.rateCode = rateCode;
    this.amount = amount;
    this.currency = currency;
    this.quantity = quantity;
    this.minNights = minNights;
    this.maxNights = maxNights;
    this.roomType = roomType;
  }

  public HotelAvailResultSetDto() {
  }

}
