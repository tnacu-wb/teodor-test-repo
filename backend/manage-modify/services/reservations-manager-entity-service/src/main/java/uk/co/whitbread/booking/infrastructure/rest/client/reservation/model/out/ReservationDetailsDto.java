package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDetailsDto {

  private ReservationRoomDto roomStay;
  private ReservationBillingDto billing;
  private List<ReservationGuestDto> reservationGuestList;
  private List<ReservationPackagesDetailsDto> reservationPackageList;
  private ReservationCancelInfoResponseDto reservationCancelInfoResponse;
  private Boolean onHold;
  private String reservationStatus;
  private ReservationAllowanceDto dinnerAllowance;
  private ReservationPaymentCardDto paymentCard;
}
