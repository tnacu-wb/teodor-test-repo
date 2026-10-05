package uk.co.whitbread.reservation.domain.model.payment.in.ccui;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NonguaranteedItems {

  private String typeOfCaller;
}
