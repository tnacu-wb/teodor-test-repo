package uk.co.whitbread.cdh.domain.model.booking.out;

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
public class ReservationInvoicesResponse {

  @JsonProperty("NumberOfResults")
  private Integer numberOfResults;

  @JsonProperty("Invoices")
  private List<InvoiceContainer> invoices;

  @JsonProperty("NotFound")
  private Object notFound;
}

