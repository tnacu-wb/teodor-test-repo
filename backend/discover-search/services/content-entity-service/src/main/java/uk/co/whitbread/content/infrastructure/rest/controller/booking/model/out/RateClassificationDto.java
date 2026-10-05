package uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateClassificationDto {

  private String rateClassification;
  private String ratePlanCode;
  private String rateDisplaySet;
  private String rateCategory;
  private String rateOrder;
  private String rateName;
  private String rateDescription;
  private String additionalDescription;
  private String rateLongDescription;
  private String rateNotes;
  private List<String> rateTags;

}
