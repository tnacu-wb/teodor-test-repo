package uk.co.whitbread.content.domain.model.labels.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Extras {
  private String id;
  private String name;
  private String description;
  private String imageSrc;
  private Integer order;
  private String referenceDateType;
  private Boolean requiresInventory;
}
