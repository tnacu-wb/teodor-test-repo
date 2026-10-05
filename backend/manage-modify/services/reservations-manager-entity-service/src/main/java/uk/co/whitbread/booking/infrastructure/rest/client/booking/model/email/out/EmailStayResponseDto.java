package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.email.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailStayResponseDto {

  private boolean success;
}
