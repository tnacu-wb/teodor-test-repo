package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.email.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailStayInvoiceRequestDto {

  private String sessionId;
  private String guestHistoryNumber;
  private String invoiceRecordNumber;
  private String emailAddress;
}
