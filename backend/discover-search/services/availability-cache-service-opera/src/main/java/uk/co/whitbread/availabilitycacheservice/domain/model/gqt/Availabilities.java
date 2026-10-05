package uk.co.whitbread.availabilitycacheservice.domain.model.gqt;

import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Availabilities {

  private LocalDate availableDate;

  private Set<Rate> rates;

}
