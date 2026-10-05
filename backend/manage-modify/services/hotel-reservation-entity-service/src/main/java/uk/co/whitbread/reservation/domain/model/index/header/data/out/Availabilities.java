package uk.co.whitbread.reservation.domain.model.index.header.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Availabilities {

  private String domain;
  private String hotels;
  private String method;
  private String datelessUrl;
  private String dateless;
  private boolean enabled;
}
