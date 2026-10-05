package uk.co.whitbread.content.domain.model.inn.business.header.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactBanner {
  private String type;
  private String text;
  private String date;
  private List<String> enabledPages;
}
