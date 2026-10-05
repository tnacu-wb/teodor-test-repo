package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingInvoiceRequestDto {

  @NotEmpty
  private String accessContext;
  @NotEmpty
  private String accessedBy;


}