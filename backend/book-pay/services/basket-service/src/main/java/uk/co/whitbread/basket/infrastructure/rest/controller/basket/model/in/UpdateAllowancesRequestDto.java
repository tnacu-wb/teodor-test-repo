package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
@Jacksonized
public class UpdateAllowancesRequestDto implements
    SelfValidation<UpdateAllowancesRequestDto> {

  @NotNull
  private List<BookingAllowanceDto> bookingAllowances;

  public UpdateAllowancesRequestDto(List<BookingAllowanceDto> bookingAllowances) {
    this.bookingAllowances = bookingAllowances;
    this.validateSelf();
  }
}
