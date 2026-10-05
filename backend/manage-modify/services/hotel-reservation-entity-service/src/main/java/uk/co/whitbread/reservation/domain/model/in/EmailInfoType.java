package uk.co.whitbread.reservation.domain.model.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailInfoType {
  private EmailType email;
}
