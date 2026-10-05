package uk.co.whitbread.content.domain.model.meals.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageCode {

  private String id;
  private String name;
  private String description;
  private List<String> images;
  private List<Attachments> attachments;
}
