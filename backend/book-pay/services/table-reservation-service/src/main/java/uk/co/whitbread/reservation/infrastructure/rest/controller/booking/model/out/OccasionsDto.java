package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OccasionsDto {
  private String id;
  private String name;
  private Boolean available;

}
