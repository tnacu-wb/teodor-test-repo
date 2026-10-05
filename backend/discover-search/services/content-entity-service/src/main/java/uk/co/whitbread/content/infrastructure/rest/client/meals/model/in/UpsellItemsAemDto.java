package uk.co.whitbread.content.infrastructure.rest.client.meals.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpsellItemsAemDto {

  private String code;
  private String name;
  private String description;
  private String shortDescription;
  private String additionalInfo;
  private List<String> images;
  private List<AttachmentsAemDto> attachments;
  private Boolean show;
  private Boolean freeBreakfastOption;
  private String freeBreakfastCode;
  private Boolean freeBreakfastTrigger;
  private Integer order;
  private Integer freeBreakfastMaxPerMeal;
  private String upsellType;
}
