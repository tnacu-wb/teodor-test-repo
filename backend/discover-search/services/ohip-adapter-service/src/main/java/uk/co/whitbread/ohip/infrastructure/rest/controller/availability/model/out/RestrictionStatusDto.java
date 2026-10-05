package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestrictionStatusDto {
  private String code;
  private int unit;
}
