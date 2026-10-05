package uk.co.whitbread.content.infrastructure.rest.client.booking.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RateOverrideDetailsDto {
  private String ratePlanCode;
  private String rateName;
  private String additionalDescription;
  private String rateOrder;
  private String rateDescription;
  private String rateLongDescription;
  private String rateNotes;
  private List<String> rateTags;
}
