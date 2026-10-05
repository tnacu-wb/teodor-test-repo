package uk.co.whitbread.spending.domain.model.out.cdh;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
  private String bookingReference;
  private String customerAccountId;
  private String companyAccountId;
  private String employeeAccountId;
  private String bookingType;
  private String cardType;
  private String cardToken;
  private String cardMaskedPAN;
  private String bookerName;
  private BigDecimal bookingValue;
  private LocalDateTime bookingDate;
  private LocalDateTime arrivalDate;
  private LocalDateTime departureDate;
  private String pibaAccountId;
  private String pibaAccountNo;
  private String pibaCostCentre;
  private String pibaCardNo;
  private String pibaUser;
  private String bookingStatus;
  private String hotelCode;
  private String hotelName;
}
