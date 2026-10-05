package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPaymentMethodDto {

  private String paymentMethod;

  private String description;

  private Integer folioView;
}
