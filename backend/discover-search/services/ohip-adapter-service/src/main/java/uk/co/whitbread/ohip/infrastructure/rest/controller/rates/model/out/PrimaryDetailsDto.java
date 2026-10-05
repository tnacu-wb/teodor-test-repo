package uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class PrimaryDetailsDto {

  private DescriptionDto description;

}
