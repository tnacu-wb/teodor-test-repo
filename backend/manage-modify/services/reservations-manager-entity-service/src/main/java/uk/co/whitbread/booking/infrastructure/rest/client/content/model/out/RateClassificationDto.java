package uk.co.whitbread.booking.infrastructure.rest.client.content.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RateClassificationDto {

  private String rateClassification;
  private String rateName;
  private String rateDescription;
  private String rateNotes;

}
