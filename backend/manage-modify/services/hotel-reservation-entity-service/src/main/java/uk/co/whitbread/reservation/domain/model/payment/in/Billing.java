package uk.co.whitbread.reservation.domain.model.payment.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Billing {

  private String title;
  private String firstName;
  private String lastName;
  private String email;
  private String telephone;
  private Address address;
}
