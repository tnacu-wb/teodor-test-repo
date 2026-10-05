package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceChargeDto {

  private String dateTime;
  private String transactionCode;
  private String itemDescription;
  private String quantity;
  private String postingRemark;
  private String netAmount;
  private String vatRate;
  private String vatRateDesc;
  private String vatAmount;
  private String creditAmount;
  private String debitAmount;
}
