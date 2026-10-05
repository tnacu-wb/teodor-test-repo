package uk.co.whitbread.booking.domain.model.email.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ResendInvoiceEmailRequest extends BaseEmailRequest {

  private String invoiceRecordNumber;
  private String language;
  private String sourceSystem;

}
