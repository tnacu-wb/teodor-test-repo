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
public class InvoiceTransaction {

  @JsonProperty("DateTime")
  private String dateTime;

  @JsonProperty("Type")
  private String type;

  @JsonProperty("Code")
  private String code;

  @JsonProperty("PaymentMethod")
  private String paymentMethod;

  @JsonProperty("Amount")
  private String amount;

  @JsonProperty("Status")
  private String status;

  @JsonProperty("Reference")
  private String reference;

  @JsonProperty("Text")
  private String text;

  @JsonProperty("Card")
  private Object card;
}

