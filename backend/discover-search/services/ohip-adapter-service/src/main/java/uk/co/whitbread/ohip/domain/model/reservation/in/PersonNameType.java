package uk.co.whitbread.ohip.domain.model.reservation.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonNameType {

  private String givenName;
  private String surname;
  private String nameTitle;
  private PersonNameTypeType nameType;
  private String language;
  private String email;

}
