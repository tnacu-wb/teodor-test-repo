package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessItemsRequestDto {

  @NotNull
  @Valid
  @Schema(required = true)
  private Set<String> reservationIds;

  @NotNull
  @Valid
  @Schema(required = true)
  private String hotelId;

  private String companyId;

  private BusinessItemsDto businessItems;

  private String channel;

  private Boolean pibaCardPresent;
}
