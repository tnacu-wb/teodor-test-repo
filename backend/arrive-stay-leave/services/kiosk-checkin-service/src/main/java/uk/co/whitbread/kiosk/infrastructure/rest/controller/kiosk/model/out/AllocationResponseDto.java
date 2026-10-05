package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllocationResponseDto {

  private String roomId;
  private String status;

}
