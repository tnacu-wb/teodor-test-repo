package uk.co.whitbread.cdh.domain.model.booking.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCharge {

  @JsonProperty("DateTime")
  private String dateTime;

  @JsonProperty("TransactionCode")
  private String transactionCode;

  @JsonProperty("ItemDescription")
  private String itemDescription;

  @JsonProperty("Quantity")
  private String quantity;

  @JsonProperty("PostingRemark")
  private String postingRemark;

  @JsonProperty("NetAmount")
  private String netAmount;

  @JsonProperty("VatRate")
  private String vatRate;

  @JsonProperty("VatRateDesc")
  private String vatRateDesc;

  @JsonProperty("VatAmount")
  private String vatAmount;

  @JsonProperty("CreditAmount")
  private String creditAmount;

  @JsonProperty("DebitAmount")
  private String debitAmount;
}

