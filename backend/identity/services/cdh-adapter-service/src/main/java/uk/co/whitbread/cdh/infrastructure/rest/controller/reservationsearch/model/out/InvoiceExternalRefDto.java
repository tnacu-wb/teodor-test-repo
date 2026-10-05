package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceExternalRefDto {

  private String context;
  private String value;
}
