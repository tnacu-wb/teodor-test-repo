package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.gqt;

import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilitiesDto {

  private LocalDate availableDate;

  private Set<RateDto> rates;

}
