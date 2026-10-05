package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookerDetailsCnpRequest {

  @NotEmpty
  private String hotelId;
  @NotEmpty
  private List<String> reservationIds;
  private BookerDetailsCnp booker;
}
