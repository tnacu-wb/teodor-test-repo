package uk.co.whitbread.reservation.domain.model.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonNameType {

  private String givenName;

  private String surname;

  private String nameTitle;

  private String nameType;
}
