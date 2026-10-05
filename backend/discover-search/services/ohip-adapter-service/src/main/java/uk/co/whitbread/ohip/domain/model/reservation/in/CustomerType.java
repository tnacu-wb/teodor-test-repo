package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerType {

  private List<PersonNameType> personName;
  private LocalDate birthDate;

}
