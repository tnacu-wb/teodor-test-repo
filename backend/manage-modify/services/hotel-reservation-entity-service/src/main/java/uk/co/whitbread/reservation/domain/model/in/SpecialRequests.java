package uk.co.whitbread.reservation.domain.model.in;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialRequests {

  private List<String> reservationIds;
  private String hotelId;
  private List<String> specialRequests;
  private List<String> bookingNotes;
}
