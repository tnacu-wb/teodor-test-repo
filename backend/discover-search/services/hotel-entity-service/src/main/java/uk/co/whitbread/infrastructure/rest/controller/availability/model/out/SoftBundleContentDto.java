package uk.co.whitbread.infrastructure.rest.controller.availability.model.out;

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
public class SoftBundleContentDto {

  private String id;
  private String name;
  private BigDecimal price;
  private String description;
  private String imageSrc;
  private List<AttachmentsDto> attachments;
  private Boolean strikeThrough;
}
