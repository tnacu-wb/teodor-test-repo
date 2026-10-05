package uk.co.whitbread.booking.domain.model.email.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ResendConfirmationEmailRequest extends BaseEmailRequest {

  private String sessionId;
  private String sourceSystem;

}
