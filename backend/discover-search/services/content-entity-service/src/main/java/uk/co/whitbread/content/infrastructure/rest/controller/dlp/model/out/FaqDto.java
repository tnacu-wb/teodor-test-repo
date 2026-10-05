package uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FaqDto {

  private String title;
  private List<FaqItemDto> faqItems;
}
