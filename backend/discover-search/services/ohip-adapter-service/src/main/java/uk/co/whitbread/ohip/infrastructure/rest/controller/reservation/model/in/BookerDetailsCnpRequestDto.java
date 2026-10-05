package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookerDetailsCnpRequestDto {

  @NotEmpty
  private String hotelId;
  @NotEmpty
  private List<String> reservationIds;
  private BookerDetailsCnpDto booker;
}
