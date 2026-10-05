package uk.co.whitbread.cdh.domain.model.booking.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationInvoicesRequest {

  @JsonProperty("InvoiceDetails")
  private List<ReservationInvoicesRequestInvoiceDetail> invoiceDetails;
}
