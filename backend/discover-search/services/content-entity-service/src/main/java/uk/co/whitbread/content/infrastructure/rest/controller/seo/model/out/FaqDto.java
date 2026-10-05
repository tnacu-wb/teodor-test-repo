package uk.co.whitbread.content.infrastructure.rest.controller.seo.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqDto {

  private String title;
  private List<FaqItemDto> faqItems;

}
