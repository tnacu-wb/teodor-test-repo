package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceExternalReferencesDto {

  private List<InvoiceExternalRefDto> externalRef;
}
