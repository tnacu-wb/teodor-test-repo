package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class OccasionsResponseDto {

  List<OccasionsDto> occasions;

  public OccasionsResponseDto(List<OccasionsDto> occasions) {
    this.occasions = occasions;
  }


}
