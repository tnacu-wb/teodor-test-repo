package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AmendDistributionReservationDto {

  private String reservationId;
  @NotEmpty
  private String hotelId;
  private List<AmendDistributionGuestDto> reservationGuestList;
  private RoomStayDistributionDto roomStay;
  private List<AmendPackagesDistributionDto> reservationPackageList;
}