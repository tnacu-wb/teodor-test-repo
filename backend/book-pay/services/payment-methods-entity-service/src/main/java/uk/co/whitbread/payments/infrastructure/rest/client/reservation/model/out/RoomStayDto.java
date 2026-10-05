package uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoomStayDto {

  private Integer adultsNumber;
  private Integer childrenNumber;
  private String roomType;
  private String ratePlanCode;
  private String arrivalDate;
  private String departureDate;
  private List<RatePerNightDto> ratesPerNight;
}
