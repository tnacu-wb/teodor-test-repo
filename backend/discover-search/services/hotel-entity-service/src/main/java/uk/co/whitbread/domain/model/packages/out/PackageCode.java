package uk.co.whitbread.domain.model.packages.out;

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
