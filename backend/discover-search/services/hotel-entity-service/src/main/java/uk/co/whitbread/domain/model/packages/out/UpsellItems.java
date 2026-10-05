package uk.co.whitbread.domain.model.packages.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpsellItems {

  private String additionalInfo;
  private List<Attachments> attachments;
  private String code;
  private String description;
  private String freeBreakfastCode;
  private Integer freeBreakfastMaxPerMeal;
  private Boolean freeBreakfastOption;
  private Boolean freeBreakfastTrigger;
  private List<String> images = null;
  private String name;
  private Integer order;
  private String shortDescription;
  private Boolean show;
  private String upsellType;
}
