package uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositFoliosRequestDto {

  @Size(min = 1)
  @Valid
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  public List<DepositFolioRequestDto> depositFolios;

}
