package uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LightweightReservationByIdDto {
  private String reservationId;
  private String hotelId;
  private String checkInTime;
  private String checkOutTime;
  private String email;
  private List<ReservationPackagesDetailsResponseDto> reservationPackageList;
}
