package uk.co.whitbread.content.infrastructure.rest.client.booking.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckinOnlineDto {

  private String title;
  private String description;

}
