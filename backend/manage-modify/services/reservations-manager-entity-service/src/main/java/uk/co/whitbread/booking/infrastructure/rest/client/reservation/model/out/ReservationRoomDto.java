package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRoomDto {

  private Integer adultsNumber;
  private Integer childrenNumber;
  private Boolean cot;
  private String roomType;
  private String ratePlanCode;
  private String arrivalDate;
  private String departureDate;
  private String checkInTime;
  private String checkOutTime;
  private BigDecimal roomPrice;
  private List<ReservationRatePerNightDto> ratesPerNight;
}
