package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositFolioRequestDto {
  private List<DepositFolioChargeDto> charges;
  private String defaultPaymentMethod;
  private String hotelId;
  private String paymentId;
  private String reservationId;
}