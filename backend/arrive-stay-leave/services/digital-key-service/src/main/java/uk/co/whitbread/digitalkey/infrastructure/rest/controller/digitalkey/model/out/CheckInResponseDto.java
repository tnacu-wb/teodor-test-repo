package uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckInResponseDto {

  private String roomNumber;
  private String checkInStatus;

}
