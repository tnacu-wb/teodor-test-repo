package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.email.in;

import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in.ConfirmationTypeDto;

@Data
@Builder
public class EmailStayConfirmationRequestDto {

  private String destination;
  private String sessionId;
  private ConfirmationTypeDto type;
}
