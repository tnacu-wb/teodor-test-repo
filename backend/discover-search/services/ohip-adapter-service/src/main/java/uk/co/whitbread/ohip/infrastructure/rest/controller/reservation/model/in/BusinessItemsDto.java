package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessItemsDto {

  private String purchaseOrderNumber;

  private String customReferenceNumber;

  @NotNull
  @Valid
  @Schema(required = true)
  private List<BusinessAllowanceDto> businessAllowances;

  @NotNull
  @Valid
  @Schema(required = true)
  private String businessNotes;
}
