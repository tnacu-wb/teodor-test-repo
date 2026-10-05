package uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermsAndConditionsDto {

  private String rate;
  private String text;

}
