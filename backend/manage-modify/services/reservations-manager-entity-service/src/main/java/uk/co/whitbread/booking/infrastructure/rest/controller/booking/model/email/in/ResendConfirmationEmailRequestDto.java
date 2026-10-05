package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.email.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResendConfirmationEmailRequestDto extends BaseEmailRequestDto {

  private String sessionId;
  private String sourceSystem;
}
