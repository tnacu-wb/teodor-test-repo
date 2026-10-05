package uk.co.whitbread.availabilitycacheservice.domain.model.gqt;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rate {

  private boolean available;
  private String code;
  private String classification;
  private String currency;
  private Set<Room> rooms;

}
