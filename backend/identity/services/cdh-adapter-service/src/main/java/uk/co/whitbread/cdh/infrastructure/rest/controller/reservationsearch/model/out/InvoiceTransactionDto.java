package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceTransactionDto {

  private String dateTime;
  private String type;
  private String code;
  private String paymentMethod;
  private String amount;
  private String status;
  private String reference;
  private String text;
  private Object card;
}
