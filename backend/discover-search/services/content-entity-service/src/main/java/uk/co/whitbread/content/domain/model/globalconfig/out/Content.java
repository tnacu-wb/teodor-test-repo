package uk.co.whitbread.content.domain.model.globalconfig.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {

  private Form form;
  private Global global;
}
