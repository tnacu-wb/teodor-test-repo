package uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@Builder
@Data
@ToString(callSuper = true, includeFieldNames = true)
public class DistributionHotelAvailResultSet {

  private String hotelId;

  private String hotelCode;

  private LocalDate availableDate;

  private String pmsSource;

  private String rateId;

  private boolean availability;

  private String rateClassification;

  private String rateCode;

  private BigDecimal amount;

  private String currency;

  private String roomId;

  private int quantity;

  private String roomType;

  public DistributionHotelAvailResultSet(String hotelId, String hotelCode, LocalDate availableDate,
      String pmsSource, String rateId, boolean availability,
      String rateClassification,
      String rateCode, BigDecimal amount, String currency,
      String roomId, int quantity, String roomType) {
    this.hotelId = hotelId;
    this.hotelCode = hotelCode;
    this.availableDate = availableDate;
    this.pmsSource = pmsSource;
    this.rateId = rateId;
    this.availability = availability;
    this.rateClassification = rateClassification;
    this.rateCode = rateCode;
    this.amount = amount;
    this.currency = currency;
    this.roomId = roomId;
    this.quantity = quantity;
    this.roomType = roomType;
  }

  public DistributionHotelAvailResultSet() {
  }
}
