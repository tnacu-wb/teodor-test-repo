package uk.co.whitbread.reservation.domain.model.out;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationRoomStay {

  private LocalDate arrivalDate;
  private LocalDate departureDate;
}
