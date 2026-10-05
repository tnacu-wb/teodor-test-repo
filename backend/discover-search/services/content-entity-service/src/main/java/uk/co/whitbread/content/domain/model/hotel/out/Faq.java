package uk.co.whitbread.content.domain.model.hotel.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Faq {

  private String title;
  private List<FaqItem> faqItems;
}
