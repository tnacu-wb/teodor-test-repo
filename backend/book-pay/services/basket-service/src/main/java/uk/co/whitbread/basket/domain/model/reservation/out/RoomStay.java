package uk.co.whitbread.basket.domain.model.reservation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.basket.out.RatePerNight;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RoomStay {
  private Integer adultsNumber;
  private Integer childrenNumber;
  private String roomType;
  private String ratePlanCode;
  private String arrivalDate;
  private String departureDate;
  private List<RatePerNight> ratesPerNight;
}

