package uk.co.whitbread.domain.model.packages.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachments {

  private String label;
  private String path;
  private String type;
}
