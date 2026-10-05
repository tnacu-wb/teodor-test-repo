package uk.co.whitbread.reservation.domain.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMemoRequest {

  private String basketReference;
  private String hotelId;
  private List<String> reservationIds;
  private String description;

}
