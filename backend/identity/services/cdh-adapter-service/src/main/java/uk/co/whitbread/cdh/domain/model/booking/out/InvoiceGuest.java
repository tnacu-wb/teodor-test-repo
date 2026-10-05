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
public class InvoiceGuest {

  @JsonProperty("Name")
  private String name;

  @JsonProperty("EmailAddress")
  private String emailAddress;

  @JsonProperty("IsLeadGuest")
  private Boolean isLeadGuest;
}

