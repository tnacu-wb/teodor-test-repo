package uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationByIdDto {

  private String reservationId;
  private RoomStayByIdDto roomStay;
  private List<ReservationPackagesDetailsResponseDto> reservationPackageList;
  private String reservationStatus;
  private List<Alerts> alerts;

}
