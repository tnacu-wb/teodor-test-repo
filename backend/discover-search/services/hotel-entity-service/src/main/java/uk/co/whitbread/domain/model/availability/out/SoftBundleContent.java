package uk.co.whitbread.domain.model.availability.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftBundleContent {

  private String id;
  private String name;
  private BigDecimal price;
  private String description;
  private String imageSrc;
  private List<Attachments> attachments;
  private Boolean strikeThrough;
}
