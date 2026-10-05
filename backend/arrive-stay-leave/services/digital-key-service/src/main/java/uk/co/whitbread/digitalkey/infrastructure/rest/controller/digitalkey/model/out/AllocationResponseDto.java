package uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out;

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
