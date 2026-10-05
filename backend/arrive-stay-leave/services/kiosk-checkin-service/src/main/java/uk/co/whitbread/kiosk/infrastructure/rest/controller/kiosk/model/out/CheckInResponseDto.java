package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckInResponseDto {

  private int numberOfKeys;
  private String roomNumber;
  private VatDto vat;
  private String transaction;
  private String cardType;
  private String cardNumber;
  private String cardExpiry;
  private String cardStart;
  private BigDecimal amountPaid;
  private String entryType;
  private String cvm;
  private String aid;
  private String panSeqNum;
  private String auth;
  private String status;
  private String merchantID;
  private String terminalID;
  private String transactionReferenceNumber;
  private String transactionDate;
  private String transactionTime;
  private String wifiAccessCode;

}
