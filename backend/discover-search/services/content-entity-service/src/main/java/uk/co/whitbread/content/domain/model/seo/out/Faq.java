package uk.co.whitbread.content.domain.model.seo.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Faq {

  private String title;
  private List<FaqItem> faqItems;
}
