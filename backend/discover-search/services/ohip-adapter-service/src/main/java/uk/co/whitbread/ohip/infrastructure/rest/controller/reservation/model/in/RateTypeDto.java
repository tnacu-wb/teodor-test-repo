package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class RateTypeDto {

  @NotEmpty
  @Size(min = 1)
  @Valid
  private List<AmountTypeDto> rate;
}
