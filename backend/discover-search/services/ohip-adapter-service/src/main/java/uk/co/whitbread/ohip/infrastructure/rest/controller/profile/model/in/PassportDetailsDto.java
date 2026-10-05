package uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassportDetailsDto {

  private String passportNumber;
  private String placeOfIssue;
  private String nextDestination;

}
